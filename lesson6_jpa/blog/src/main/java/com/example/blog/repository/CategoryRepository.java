package com.example.blog.repository;

import com.example.blog.model.Category;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class CategoryRepository implements ICategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    public List<Category> findAll() {
//        TypedQuery<Category> query = entityManager.createQuery("select c from Category c", Category.class);
//        return query.getResultList();
//    }

    @Override
    public Category findById(Long id) {
        return entityManager.find(Category.class, id);
    }

    @Override
    public void save(Category category) {
        if (category.getId() != null) {
            entityManager.merge(category);
        } else {
            entityManager.persist(category);
        }
    }

    @Override
    public void remove(Long id) {
        Category category = findById(id);
        if (category != null) {
            entityManager.remove(category);
        }
    }

    @Override
    public List<Category> findAll(int page, int size, String sortField, boolean asc) {
        String direction = asc ? "ASC" : "DESC";
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c ORDER BY c." + sortField + " " + direction,
                Category.class
        );
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public List<Category> searchByTitle(String keyword, int page, int size) {
        TypedQuery<Category> query = entityManager.createQuery(
                "SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(:keyword)",
                Category.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                .getSingleResult();
    }

}
