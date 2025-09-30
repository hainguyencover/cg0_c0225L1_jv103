package com.example.nasa.service;

import com.example.nasa.model.FeedBack;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HibernateFeedBackService implements IFeedBackService {

    private final SessionFactory sessionFactory;

    public HibernateFeedBackService() {
        this.sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                .buildSessionFactory();
    }

    @Override
    public void save(FeedBack feedBack) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(feedBack);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<FeedBack> findToday() {
        Session session = sessionFactory.openSession();
        List<FeedBack> list = session.createQuery("FROM FeedBack f WHERE f.date = :today", FeedBack.class)
                .setParameter("today", LocalDate.now())
                .list();
        session.close();
        return list;
    }

    @Override
    public void like(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        FeedBack fb = session.get(FeedBack.class, id);
        if (fb != null) {
            fb.setLikes(fb.getLikes() + 1);
            session.update(fb);
        }
        session.getTransaction().commit();
        session.close();
    }
}
