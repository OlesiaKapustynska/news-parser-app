package com.example.newsparserapp;

import com.example.newsparserapp.model.News;
import com.example.newsparserapp.service.NewsService;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class NewsApp extends Application {
    private ConfigurableApplicationContext context;
    private List<News> newsList;
    private int currentIndex = 0;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(NewsParserAppApplication.class).run();
        NewsService newsService = context.getBean(NewsService.class);
        newsList = newsService.getAllNews();
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();
        Label headlineLabel = new Label();
        Label descriptionLabel = new Label();
        Button nextButton = new Button("Next");
        Button prevButton = new Button("Previous");

        if (!newsList.isEmpty()) {
            displayNews(headlineLabel, descriptionLabel, currentIndex);
        }

        nextButton.setOnAction(e -> {
            if (currentIndex < newsList.size() - 1) {
                currentIndex++;
                displayNews(headlineLabel, descriptionLabel, currentIndex);
            }
        });

        prevButton.setOnAction(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayNews(headlineLabel, descriptionLabel, currentIndex);
            }
        });

        vbox.getChildren().addAll(headlineLabel, descriptionLabel, prevButton, nextButton);
        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayNews(Label headlineLabel, Label descriptionLabel, int index) {
        News news = newsList.get(index);
        headlineLabel.setText(news.getHeadline());
        descriptionLabel.setText(news.getDescription());
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}