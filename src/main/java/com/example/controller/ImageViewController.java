package com.example.controller;

import com.example.model.ImageCollection;
import com.example.model.ImageInfo;
import com.example.model.ImageIterator;
import com.example.utils.ExifReader;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageViewController implements Initializable {

    @FXML private ImageView imageView;
    @FXML private Label counterLabel;
    @FXML private Label infoLabel;
    @FXML private TextArea exifArea;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button firstButton;
    @FXML private Button lastButton;
    @FXML private Button loadButton;
    @FXML private Button autoPlayButton;
    @FXML private ComboBox<String> transitionComboBox;
    @FXML private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private VBox rootPane;

    private ImageCollection imageCollection;
    private ImageIterator iterator;
    private Timeline autoPlayTimeline;
    private boolean isAutoPlaying = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageCollection = new ImageCollection();
        iterator = imageCollection.getIterator();

        setupUI();
        setupEventHandlers();
        setupAutoPlay();

        // Загрузка тестовых изображений
        loadTestImages();
    }

    private void setupUI() {
        transitionComboBox.getItems().addAll("Исчезание", "Сдвиг", "Масштабирование");
        transitionComboBox.setValue("Исчезание");

        speedSlider.setMin(0.5);
        speedSlider.setMax(5.0);
        speedSlider.setValue(2.0);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speedLabel.setText(String.format("%.1f сек", newVal.doubleValue()));
            if (isAutoPlaying) {
                restartAutoPlay();
            }
        });

        imageView.setPreserveRatio(true);
        // Привязываем размеры после того, как сцена загружена
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                imageView.fitWidthProperty().bind(rootPane.widthProperty().subtract(40));
                imageView.fitHeightProperty().bind(rootPane.heightProperty().subtract(300));
            }
        });
    }

    private void setupEventHandlers() {
        prevButton.setOnAction(e -> navigatePrevious());
        nextButton.setOnAction(e -> navigateNext());
        firstButton.setOnAction(e -> navigateFirst());
        lastButton.setOnAction(e -> navigateLast());
        loadButton.setOnAction(e -> loadDirectory());
        autoPlayButton.setOnAction(e -> toggleAutoPlay());
        filterComboBox.setOnAction(e -> applyFilter());
    }

    private void setupAutoPlay() {
        autoPlayTimeline = new Timeline();
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void loadTestImages() {
        // Создаем папку для тестовых изображений на рабочем столе
        String userHome = System.getProperty("user.home");
        File testDir = new File(userHome, "Desktop/test_images");

        if (!testDir.exists()) {
            testDir.mkdirs();
            showInfo("Создана папка: " + testDir.getAbsolutePath() +
                    "\nПожалуйста, добавьте изображения в эту папку");
        }

        imageCollection.loadImagesFromDirectory(testDir);
        updateFilters();

        if (!imageCollection.isEmpty()) {
            displayImage(iterator.first());
        } else {
            showNoImagesMessage();
        }
    }

    private void loadDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку с изображениями");
        File selectedDirectory = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if (selectedDirectory != null) {
            imageCollection.loadImagesFromDirectory(selectedDirectory);
            updateFilters();

            if (!imageCollection.isEmpty()) {
                displayImage(iterator.first());
            } else {
                showNoImagesMessage();
            }
        }
    }

    private void updateFilters() {
        filterComboBox.getItems().clear();
        filterComboBox.getItems().addAll(imageCollection.getAvailableFilters());
        filterComboBox.setValue("Все");
    }

    private void applyFilter() {
        String filter = filterComboBox.getValue();
        if (filter != null) {
            imageCollection.applyFilter(filter);
            if (!imageCollection.isEmpty()) {
                displayImage(iterator.first());
            } else {
                showNoImagesMessage();
            }
        }
    }

    private void navigateNext() {
        if (!imageCollection.isEmpty()) {
            ImageInfo nextImage = iterator.next();
            displayImageWithTransition(nextImage);
        }
    }

    private void navigatePrevious() {
        if (!imageCollection.isEmpty()) {
            ImageInfo prevImage = iterator.previous();
            displayImageWithTransition(prevImage);
        }
    }

    private void navigateFirst() {
        if (!imageCollection.isEmpty()) {
            ImageInfo firstImage = iterator.first();
            displayImageWithTransition(firstImage);
        }
    }

    private void navigateLast() {
        if (!imageCollection.isEmpty()) {
            ImageInfo lastImage = iterator.last();
            displayImageWithTransition(lastImage);
        }
    }

    private void displayImageWithTransition(ImageInfo imageInfo) {
        String transitionType = transitionComboBox.getValue();
        applyTransition(transitionType, () -> displayImage(imageInfo));
    }

    private void displayImage(ImageInfo imageInfo) {
        if (imageInfo == null) return;

        try {
            Image image = new Image(imageInfo.getFile().toURI().toString(),
                    imageView.getFitWidth(),
                    imageView.getFitHeight(),
                    true, true);
            imageView.setImage(image);

            counterLabel.setText(String.format("%d из %d",
                    iterator.getCurrentIndex(),
                    iterator.getTotalCount()));

            String info = String.format("Файл: %s\nРазмер: %s\nПуть: %s",
                    imageInfo.getName(),
                    imageInfo.getSizeFormatted(),
                    imageInfo.getPath());
            infoLabel.setText(info);

            String exif = ExifReader.readExifData(imageInfo.getFile());
            exifArea.setText(exif);

        } catch (Exception e) {
            showError("Ошибка загрузки: " + e.getMessage());
        }
    }

    // ИСПРАВЛЕННЫЙ МЕТОД applyTransition
    private void applyTransition(String type, Runnable action) {
        if (type == null) {
            action.run();
            return;
        }

        // Получаем актуальные размеры ImageView
        double width = imageView.getBoundsInLocal().getWidth();
        if (width <= 0) {
            width = 400; // Значение по умолчанию
        }

        javafx.animation.Transition transition = null;

        switch (type) {
            case "Исчезание":
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), imageView);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), imageView);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                fadeOut.setOnFinished(e -> {
                    action.run();
                    fadeIn.play();
                });
                transition = fadeOut;
                break;

            case "Сдвиг":
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), imageView);
                slideOut.setFromX(0);
                slideOut.setToX(-width);
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), imageView);
                slideIn.setFromX(width);
                slideIn.setToX(0);

                slideOut.setOnFinished(e -> {
                    action.run();
                    slideIn.play();
                });
                transition = slideOut;
                break;

            case "Масштабирование":
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(300), imageView);
                scaleOut.setFromX(1.0);
                scaleOut.setFromY(1.0);
                scaleOut.setToX(0.8);
                scaleOut.setToY(0.8);
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), imageView);
                scaleIn.setFromX(0.8);
                scaleIn.setFromY(0.8);
                scaleIn.setToX(1.0);
                scaleIn.setToY(1.0);

                scaleOut.setOnFinished(e -> {
                    action.run();
                    scaleIn.play();
                });
                transition = scaleOut;
                break;
        }

        if (transition != null) {
            transition.play();
        } else {
            action.run();
        }
    }

    private void toggleAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        isAutoPlaying = true;
        autoPlayButton.setText("⏸ Стоп");
        autoPlayTimeline.getKeyFrames().clear();
        autoPlayTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(speedSlider.getValue()), e -> navigateNext())
        );
        autoPlayTimeline.play();
    }

    private void stopAutoPlay() {
        isAutoPlaying = false;
        autoPlayButton.setText("▶ Авто");
        autoPlayTimeline.stop();
    }

    private void restartAutoPlay() {
        if (isAutoPlaying) {
            stopAutoPlay();
            startAutoPlay();
        }
    }

    private void showNoImagesMessage() {
        imageView.setImage(null);
        counterLabel.setText("0 из 0");
        infoLabel.setText("Нет изображений\nЗагрузите папку с изображениями");
        exifArea.setText("");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
