package com.example.newsparserapp.parser;

import com.example.newsparserapp.model.News;
import com.example.newsparserapp.service.NewsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class NewsParserImpl implements NewsParser {

    private static final String NEWS_SITE_URL = "https://nv.ua/ukr/allnews.html";
    private static final Locale LOCALE = new Locale("uk", "UA");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM, HH:mm")
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
            .toFormatter(LOCALE);

    private static final Logger LOGGER = Logger.getLogger(NewsParserImpl.class.getName());

    @Autowired
    NewsService newsService;

    @Scheduled(cron = "0 */20 * * * *")
    @Override
    public void parseNews() {
        try {
            Document doc = Jsoup.connect(NEWS_SITE_URL).get();

            for (Element item : doc.select(".row-result-body-text")) {
                String headline = item.select(".info").text();
                String description = item.select(".title").text();
                Timestamp publicationTime = convertDate(item.select(".additional-pub-date").text());

                News news = new News();
                news.setHeadline(headline);
                news.setDescription(description);
                news.setPublicationTime(publicationTime);

                newsService.saveNews(news);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while fetching and parsing news", e);
        }
    }

    private Timestamp convertDate(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        return Timestamp.valueOf(localDateTime);
    }
}
