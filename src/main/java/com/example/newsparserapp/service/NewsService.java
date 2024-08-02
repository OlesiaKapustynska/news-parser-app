package com.example.newsparserapp.service;

import com.example.newsparserapp.model.News;
import java.sql.Timestamp;
import java.util.List;

public interface NewsService {
    List<News> getAllNews();
    List<News> getNewsByTimePeriod(Timestamp start, Timestamp end);
    public News saveNews(News news);
    public void deleteNews(Long id);
}
