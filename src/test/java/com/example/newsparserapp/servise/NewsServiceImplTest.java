package com.example.newsparserapp.servise;

import com.example.newsparserapp.model.News;
import com.example.newsparserapp.repository.NewsRepository;
import com.example.newsparserapp.service.NewsServiceImpl;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllNews() {
        News news1 = new News();
        news1.setId(1L);
        news1.setHeadline("Headline 1");
        news1.setDescription("Description 1");
        news1.setPublicationTime(Timestamp.valueOf("2024-08-07 09:00:00"));

        News news2 = new News();
        news2.setId(2L);
        news2.setHeadline("Headline 2");
        news2.setDescription("Description 2");
        news2.setPublicationTime(Timestamp.valueOf("2024-08-07 10:00:00"));

        when(newsRepository.findAll()).thenReturn(Arrays.asList(news1, news2));

        List<News> result = newsService.getAllNews();

        assertEquals(2, result.size());
        verify(newsRepository, times(1)).findAll();
    }

    @Test
    void testGetNewsByTimePeriod() {
        Timestamp start = Timestamp.valueOf("2024-08-07 00:00:00");
        Timestamp end = Timestamp.valueOf("2024-08-07 23:59:59");

        News news1 = new News();
        news1.setId(1L);
        news1.setHeadline("Headline 1");
        news1.setDescription("Description 1");
        news1.setPublicationTime(Timestamp.valueOf("2024-08-07 09:00:00"));

        News news2 = new News();
        news2.setId(2L);
        news2.setHeadline("Headline 2");
        news2.setDescription("Description 2");
        news2.setPublicationTime(Timestamp.valueOf("2024-08-07 10:00:00"));

        when(newsRepository.findByPublicationTimeBetween(start, end)).thenReturn(Arrays.asList(news1, news2));

        List<News> result = newsService.getNewsByTimePeriod(start, end);

        assertEquals(2, result.size());
        verify(newsRepository, times(1)).findByPublicationTimeBetween(start, end);
    }

    @Test
    void testSaveNews_New() {
        News news = new News();
        news.setId(1L);
        news.setHeadline("New Headline");
        news.setDescription("New Description");
        news.setPublicationTime(Timestamp.valueOf("2024-08-07 09:00:00"));

        when(newsRepository.findByHeadlineAndPublicationTime(news.getHeadline(), news.getPublicationTime())).thenReturn(null);
        when(newsRepository.save(news)).thenReturn(news);

        News result = newsService.saveNews(news);

        assertEquals(news, result);
        verify(newsRepository, times(1)).findByHeadlineAndPublicationTime(news.getHeadline(), news.getPublicationTime());
        verify(newsRepository, times(1)).save(news);
    }

    @Test
    void testSaveNews_Existing() {
        News existingNews = new News();
        existingNews.setId(1L);
        existingNews.setHeadline("Existing Headline");
        existingNews.setDescription("Existing Description");
        existingNews.setPublicationTime(Timestamp.valueOf("2024-08-07 09:00:00"));

        News newNews = new News();
        newNews.setId(1L);
        newNews.setHeadline("Existing Headline");
        newNews.setDescription("New Description");
        newNews.setPublicationTime(Timestamp.valueOf("2024-08-07 09:00:00"));

        when(newsRepository.findByHeadlineAndPublicationTime(newNews.getHeadline(), newNews.getPublicationTime())).thenReturn(existingNews);
        when(newsRepository.save(existingNews)).thenReturn(existingNews);

        News result = newsService.saveNews(newNews);

        assertEquals(existingNews, result);
        verify(newsRepository, times(1)).findByHeadlineAndPublicationTime(newNews.getHeadline(), newNews.getPublicationTime());
        verify(newsRepository, times(1)).save(existingNews);
    }

    @Test
    void testDeleteNews() {
        Long id = 1L;

        newsService.deleteNews(id);

        verify(newsRepository, times(1)).deleteById(id);
    }
}
