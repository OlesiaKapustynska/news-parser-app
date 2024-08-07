package com.example.newsparserapp;
import com.example.newsparserapp.model.News;
import com.example.newsparserapp.parser.NewsParser;
import com.example.newsparserapp.service.NewsService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewsApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(NewsApp.class.getName());
    private final TableView<News> newsTable = new TableView<>();
    private ConfigurableApplicationContext context;
    private ObservableList<News> newsList;
    private int currentIndex = 0;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(NewsParserAppApplication.class).run();
        NewsService newsService = context.getBean(NewsService.class);
        NewsParser newsParser = context.getBean(NewsParser.class);
        try {
            newsParser.parseNews();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot fetch and parse news", e);
        }
        List<News> list = newsService.getAllNews();
        newsList = FXCollections.observableArrayList(list);
    }

    @Override
    public void start(Stage primaryStage) {
        ComboBox<String> timeOfDayComboBox = new ComboBox<>();
        timeOfDayComboBox.setItems(FXCollections.observableArrayList("morning", "day", "evening", "all"));
        timeOfDayComboBox.setValue("all");
        timeOfDayComboBox.valueProperty().addListener(this::onFilterChanged);

        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> onFilterChanged(null, null, null));

        TableColumn<News, String> headlineColumn = new TableColumn<>("Headline");
        headlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeadline()));

        TableColumn<News, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<News, String> timeOfDayColumn = new TableColumn<>("Time of Day");
        timeOfDayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(determineTimeOfDay(cellData.getValue().getPublicationTime())));

        TableColumn<News, String> publicationTimeColumn = new TableColumn<>("Publication Time");
        publicationTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(formatTimestamp(cellData.getValue().getPublicationTime())));

        newsTable.getColumns().addAll(headlineColumn, descriptionColumn, timeOfDayColumn, publicationTimeColumn);
        newsTable.setItems(newsList);

        VBox vbox = new VBox(10, timeOfDayComboBox, datePicker, newsTable);
        vbox.setPadding(new Insets(10));

        Label headlineLabel = new Label();
        Label descriptionLabel = new Label();
        Label dateLabel = new Label();
        Button nextButton = new Button("Next");
        Button prevButton = new Button("Previous");

        if (!newsList.isEmpty()) {
            displayNews(headlineLabel, descriptionLabel, dateLabel, currentIndex);
        }

        nextButton.setOnAction(e -> {
            if (currentIndex < newsList.size() - 1) {
                currentIndex++;
                displayNews(headlineLabel, descriptionLabel, dateLabel, currentIndex);
            }
        });

        prevButton.setOnAction(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayNews(headlineLabel, descriptionLabel, dateLabel, currentIndex);
            }
        });

        vbox.getChildren().addAll(headlineLabel, descriptionLabel, dateLabel, prevButton, nextButton);

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("News App");
        primaryStage.show();
    }

    private void onFilterChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        filterNews();
    }

    private void filterNews() {
        String timeOfDay = ((ComboBox<String>)((VBox) newsTable.getParent()).getChildren().get(0)).getValue();
        LocalDate selectedDate = ((DatePicker)((VBox) newsTable.getParent()).getChildren().get(1)).getValue();

        ObservableList<News> filteredNews = FXCollections.observableArrayList();
        for (News news : newsList) {
            boolean matchesTimeOfDay = "all".equals(timeOfDay) || timeOfDay.equals(determineTimeOfDay(news.getPublicationTime()));
            boolean matchesDate = selectedDate == null || news.getPublicationTime().toLocalDateTime().toLocalDate().isEqual(selectedDate);

            if (matchesTimeOfDay && matchesDate) {
                filteredNews.add(news);
            }
        }
        newsTable.setItems(filteredNews);
    }

    private void displayNews(Label headlineLabel, Label descriptionLabel, Label dateLabel, int index) {
        News news = newsList.get(index);
        headlineLabel.setText(news.getHeadline());
        descriptionLabel.setText(news.getDescription());
        dateLabel.setText(news.getPublicationTime().toLocalDateTime().toString());
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

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
