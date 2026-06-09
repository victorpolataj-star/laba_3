module com.example.laba_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires metadata.extractor;

    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.model to javafx.base;

    exports com.example;
    exports com.example.controller;
    exports com.example.model;
    exports com.example.utils;
}