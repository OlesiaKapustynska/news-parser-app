package com.example.newsparserapp;

import com.example.newsparserapp.model.News;
import com.example.newsparserapp.service.NewsService;
import java.sql.Timestamp;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class NewsApp extends Application {
    private final TableView<News> newsTable = new TableView<>();
    private ConfigurableApplicationContext context;
    private ObservableList<News> newsList;
    private int currentIndex = 0;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(NewsParserAppApplication.class).run();
        NewsService newsService = context.getBean(NewsService.class);
        List<News> list = newsService.getAllNews();
        newsList = FXCollections.observableArrayList(list);
    }

    @Override
    public void start(Stage primaryStage) {
        ComboBox<String> timeOfDayComboBox = new ComboBox<>();
        timeOfDayComboBox.setItems(FXCollections.observableArrayList("morning", "day", "evening", "all"));
        timeOfDayComboBox.setValue("all");
        timeOfDayComboBox.valueProperty().addListener(this::onTimeOfDayChanged);

        // Налаштування таблиці новин
        TableColumn<News, String> headlineColumn = new TableColumn<>("Headline");
        headlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeadline()));

        TableColumn<News, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<News, String> timeOfDayColumn = new TableColumn<>("Time of Day");
        timeOfDayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(determineTimeOfDay(cellData.getValue().getPublicationTime())));

        newsTable.getColumns().addAll(headlineColumn, descriptionColumn, timeOfDayColumn);
        newsTable.setItems(newsList);

        VBox vbox = new VBox(10, timeOfDayComboBox, newsTable);
        vbox.setPadding(new Insets(10));

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

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("News App");
        primaryStage.show();
    }

    private void onTimeOfDayChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterNewsByTimeOfDay(newValue);
    }

    private void filterNewsByTimeOfDay(String timeOfDay) {
        if ("all".equals(timeOfDay)) {
            newsTable.setItems(newsList);
        } else {
            ObservableList<News> filteredNews = FXCollections.observableArrayList();
            for (News news : newsList) {
                if (timeOfDay.equals(determineTimeOfDay(news.getPublicationTime()))) {
                    filteredNews.add(news);
                }
            }
            newsTable.setItems(filteredNews);
        }
    }

    private void displayNews(Label headlineLabel, Label descriptionLabel, int index) {
        News news = newsList.get(index);
        headlineLabel.setText(news.getHeadline());
        descriptionLabel.setText(news.getDescription());
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

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
