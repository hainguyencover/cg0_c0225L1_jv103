package com.example.musicapp.service;

import com.example.musicapp.model.Song;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

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
}
