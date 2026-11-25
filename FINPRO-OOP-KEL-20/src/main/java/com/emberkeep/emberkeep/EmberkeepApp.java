package com.emberkeep.emberkeep;

import com.emberkeep.emberkeep.core.GameEngine;
import javafx.application.Application;
import javafx.stage.Stage;

public class EmberkeepApp extends Application {
    @Override
    public void start(Stage stage) {
        new GameEngine(stage).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}