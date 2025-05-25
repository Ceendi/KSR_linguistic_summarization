package org.example.ksr_linguistic_summarization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;
import org.example.ksr_linguistic_summarization.logic.utils.DataInitializer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        // launch();
        List<BodyPerformance> data = DataInitializer.loadBodyPerformanceData();
        System.out.println(data.size());
    }
}