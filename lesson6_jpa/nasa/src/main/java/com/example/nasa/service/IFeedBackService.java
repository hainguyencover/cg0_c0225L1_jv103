package com.example.nasa.service;

import com.example.nasa.model.FeedBack;

import java.util.List;

public interface IFeedBackService {
    void save(FeedBack feedBack);

    List<FeedBack> findToday();

    void like(Long id);
}
