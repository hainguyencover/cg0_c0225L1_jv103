package com.example.musicapp.service;

import com.example.musicapp.model.Song;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class HibernateSongService implements ISongService {

    private static EntityManager entityManager;
    private static SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Value("${upload.path}")   // đọc từ application.properties
    private String uploadPath;

    @Override
    public List<Song> findAll() {
        String hql = "SELECT s FROM Song s";
        TypedQuery<Song> query = entityManager.createQuery(hql, Song.class);
        return query.getResultList();
    }

    @Override
    public Song findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Song.class, id);
        }
    }

    @Override
    public void save(Song song) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(song);
            tx.commit();
        }
    }

    @Override
    public void update(Song song) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(song);
            tx.commit();
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Song song = session.get(Song.class, id);
            if (song != null) {
                session.delete(song);
            }
            tx.commit();
        }
    }

    @Override
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        // tạo tên file duy nhất
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        if (!extension.equals(".mp3")) {
            throw new RuntimeException("Chỉ cho phép upload file mp3");
        }
        String newFileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;

        // lưu file vào thư mục uploadPath
        try {
            Path path = Paths.get(uploadPath, newFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Song> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(); // nếu không có từ khóa thì trả về tất cả
        }

        String hql = "SELECT s FROM Song s " +
                "WHERE LOWER(s.title) LIKE :kw " +
                "   OR LOWER(s.artist) LIKE :kw";

        TypedQuery<Song> query = entityManager.createQuery(hql, Song.class);
        query.setParameter("kw", "%" + keyword.toLowerCase() + "%");

        return query.getResultList();
    }

    @Override
    public List<Song> findPage(int page, int size, String keyword) {
        String hql = "SELECT s FROM Song s";
        if (keyword != null && !keyword.isEmpty()) {
            hql += " WHERE s.title LIKE :kw OR s.artist LIKE :kw";
        }
        TypedQuery<Song> query = entityManager.createQuery(hql, Song.class);
        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long count(String keyword) {
        String hql = "SELECT COUNT(s) FROM Song s";
        if (keyword != null && !keyword.isEmpty()) {
            hql += " WHERE s.title LIKE :kw OR s.artist LIKE :kw";
        }
        Query query = entityManager.createQuery(hql);
        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("kw", "%" + keyword + "%");
        }
        return (long) query.getSingleResult();
    }
}
