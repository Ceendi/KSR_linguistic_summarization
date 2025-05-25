package org.example.ksr_linguistic_summarization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfTruth;
import org.example.ksr_linguistic_summarization.logic.functions.Gaussian;
import org.example.ksr_linguistic_summarization.logic.functions.Trapezoidal;
import org.example.ksr_linguistic_summarization.logic.set.DiscreteSet;
import org.example.ksr_linguistic_summarization.logic.set.FuzzySet;
import org.example.ksr_linguistic_summarization.logic.summarization.*;
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

        Quantifier quantifier = new Quantifier(
                "około 1/3",
                new LinguisticValue("około 1/3",
                        new FuzzySet(
                                new DiscreteSet<>(data.stream().map(BodyPerformance::getAge).toList()),
                                new Gaussian(0.33, 0.03)
                        )
                ),
                QuantifierType.RELATIVE
        );

        Qualifier qualifier1 = new Qualifier(
                "age",
                new LinguisticValue("młoda",
                        new FuzzySet(
                                new DiscreteSet<>(data.stream().map(BodyPerformance::getAge).toList()),
                                new Trapezoidal(21, 21, 28, 33)
                        )
                )
        );

        Summarizer summarizer1 = new Summarizer(
                "gripforce",
                new LinguisticValue("słaby chwyt",
                        new FuzzySet(
                                new DiscreteSet<>(data.stream().map(BodyPerformance::getAge).toList()),
                                new Gaussian(30, 5)
                        )
                )
        );

        List<Degree> degrees = List.of(new DegreeOfTruth());

        LinguisticSummary summary = new LinguisticSummary(quantifier, List.of(qualifier1), List.of(summarizer1), data, SummaryType.ONESUBJECT, degrees);
        System.out.println(summary.getLinguisticSummary());
    }
}