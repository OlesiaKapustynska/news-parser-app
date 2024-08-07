package com.example.newsparserapp.parser;

import com.example.newsparserapp.model.News;
import com.example.newsparserapp.service.NewsService;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class NewsParserImplTest {
    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsParserImpl newsParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testParseNews() throws IOException {
        String html = "<html><body>" +
                "<div class='row-result-body-text'>" +
                "<div class='info'>Headline</div>" +
                "<div class='title'>Description</div>" +
                "<div class='additional-pub-date'>7 вересня, 13:51</div>" +
                "</div>" +
                "</body></html>";

        Document doc = Jsoup.parse(html);
        Connection connection = mock(Connection.class);
        when(connection.get()).thenReturn(doc);

        try (var mockedStatic = mockStatic(Jsoup.class)) {
            mockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            newsParser.parseNews();

            verify(newsService, times(1)).saveNews(any(News.class));
        }
    }
}