package com.financetracker;

import com.financetracker.utils.SceneManager;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Главный класс приложения Finance Tracker
 */
public class FinanceTrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.setProperty("prism.lcdtext", "true");
        System.setProperty("prism.text", "t2k");
        loanFonts();

        try {
            SceneManager.initialize(primaryStage);
            SceneManager.switchScene("login");
            
            primaryStage.setTitle("Finance Tracker");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(650);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void loanFonts() {
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Arimo-Bold.ttf")).toExternalForm(), 14);
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Arimo-SemiBold.ttf")).toExternalForm(), 14);
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Arimo-Medium.ttf")).toExternalForm(), 14);
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/fonts/Arimo-Regular.ttf")).toExternalForm(), 14);
    }
}
