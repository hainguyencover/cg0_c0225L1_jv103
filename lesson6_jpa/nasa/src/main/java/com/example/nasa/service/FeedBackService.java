package com.example.nasa.service;

import com.example.nasa.model.FeedBack;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FeedBackService implements IFeedBackService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(FeedBack feedBack) {
        // date auto-set by @PrePersist
        em.persist(feedBack);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedBack> findToday() {
        TypedQuery<FeedBack> q = em.createQuery(
                "SELECT f FROM FeedBack f WHERE f.date = :today ORDER BY f.id DESC", FeedBack.class);
        q.setParameter("today", LocalDate.now());
        return q.getResultList();
    }

    @Override
    public void like(Long id) {
        FeedBack fb = em.find(FeedBack.class, id);
        if (fb != null) {
            fb.increaseLike();
            em.merge(fb);
        }
    }
}
