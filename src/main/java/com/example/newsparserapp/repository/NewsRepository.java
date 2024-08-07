package com.example.newsparserapp.repository;

import com.example.newsparserapp.model.News;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByPublicationTimeBetween(Timestamp start, Timestamp end);
    News findByHeadlineAndPublicationTime(String headline, Timestamp publicationTime);
}
