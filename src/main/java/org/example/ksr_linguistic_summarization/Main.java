package org.example.ksr_linguistic_summarization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfTruth;
import org.example.ksr_linguistic_summarization.logic.summarization.*;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;
import org.example.ksr_linguistic_summarization.logic.utils.DataInitializer;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableDTO;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableLoader;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableFactory;

import java.io.IOException;

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

    public static void main(String[] args) throws Exception {
        List<BodyPerformance> data = DataInitializer.loadBodyPerformanceData();

        List<LinguisticVariableDTO> dtos = LinguisticVariableLoader.load();
        List<LinguisticVariable> variables = LinguisticVariableFactory.fromDTO(dtos, data);


        Quantifier quantifierVar = (Quantifier) variables.stream()
            .filter(v -> v.getName().equals("prawie wszyscy"))
            .findFirst()
            .orElseThrow();
        Quantifier quantifier = new Quantifier(quantifierVar.getName(), quantifierVar.getLinguisticValue(), quantifierVar.getQuantifierType());


        LinguisticVariable qualifierVar = variables.stream()
            .filter(v -> v.getLinguisticValue().getName().equals("młoda"))
            .findFirst()
            .orElseThrow();
        Qualifier qualifier1 = new Qualifier(qualifierVar.getName(), qualifierVar.getLinguisticValue());


        LinguisticVariable summarizerVar = variables.stream()
            .filter(v -> v.getLinguisticValue().getName().equals("młoda"))
            .findFirst()
            .orElseThrow();
        Summarizer summarizer1 = new Summarizer(summarizerVar.getName(), summarizerVar.getLinguisticValue());


        List<Degree> degrees = List.of(new DegreeOfTruth());

        LinguisticSummary summary = new LinguisticSummary(quantifier, List.of(qualifier1), List.of(summarizer1), data, SummaryType.ONESUBJECT, degrees);

        System.out.println(summary.getLinguisticSummary());
    }
}