package com.example.newsparserapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String headline;
    private String description;
    private Timestamp publicationTime;
    private String timeOfDay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Timestamp publicationTime) {
        this.publicationTime = publicationTime;
        this.timeOfDay = determineTimeOfDay(publicationTime);
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    private String determineTimeOfDay(Timestamp publicationTime) {
        if (publicationTime == null) {
            return "unknown";
        }
        int hour = publicationTime.toLocalDateTime().getHour();
        if (hour >= 0 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 18) {
            return "day";
        } else {
            return "evening";
        }
    }
}
