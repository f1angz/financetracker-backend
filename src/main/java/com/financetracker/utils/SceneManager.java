package com.financetracker.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Менеджер для управления сценами приложения
 */
public class SceneManager {

    private static Stage primaryStage;
    private static Scene mainScene;

    public static void initialize(Stage stage) throws IOException {
        primaryStage = stage;

        // создаём одну сцену
        Parent root = loadRoot("login");
        mainScene = new Scene(root);

        String cssPath = SceneManager.class
                .getResource("/css/styles.css")
                .toExternalForm();

        mainScene.getStylesheets().add(cssPath);

        primaryStage.setScene(mainScene);
    }

    public static void switchScene(String sceneName) {
        try {
            Parent newRoot = loadRoot(sceneName);
            mainScene.setRoot(newRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Parent loadRoot(String sceneName) throws IOException {
        String fxmlPath = "/fxml/" + sceneName + ".fxml";
        FXMLLoader loader =
                new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        return loader.load();
    }
}

