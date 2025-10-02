package com.example.blog.repository;

import com.example.blog.model.Blog;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Transactional
@Repository
public class BlogRepository implements IBlogRepository {

    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    public List<Blog> findAll() {
//        TypedQuery<Blog> query = entityManager.createQuery("select b from Blog b", Blog.class);
//        return query.getResultList();
//    }

    @Override
    public List<Blog> findAll(int page, int size, String sortField, boolean asc) {
        String direction = asc ? "ASC" : "DESC";
        String jpql = "SELECT b FROM Blog b ORDER BY b." + sortField + " " + direction;
        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        query.setFirstResult(page * size); // offset
        query.setMaxResults(size);        // limit
        return query.getResultList();
    }

    @Override
    public Blog findById(Long id) {
        TypedQuery<Blog> query = entityManager.createQuery("select b from Blog b where b.id=:id", Blog.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void save(Blog blog) {
        if (blog.getId() != null) {
            entityManager.merge(blog);
        } else {
            entityManager.persist(blog);
        }
    }

    @Override
    public void remove(Long id) {
        Blog blog = findById(id);
        if (blog != null) {
            entityManager.remove(blog);
        }
    }

    @Override
    public long count() {
        Query query = entityManager.createQuery("SELECT COUNT(b) FROM Blog b");
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Blog> searchByTitle(String keyword, int page, int size) {
        String jpql = "SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(:keyword)";
        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
