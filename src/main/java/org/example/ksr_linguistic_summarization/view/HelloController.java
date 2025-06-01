package org.example.ksr_linguistic_summarization.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfTruth;
import org.example.ksr_linguistic_summarization.logic.summarization.*;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;
import org.example.ksr_linguistic_summarization.logic.utils.DataInitializer;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableDTO;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableFactory;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.LinguisticVariableLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummaryGenerator;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfImprecision;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfAppropriateness;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfCovering;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfQuantifierImprecision;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfSummarizerCardinality;
import org.example.ksr_linguistic_summarization.logic.degrees.LengthOfASummary;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfQualifierCardinality;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfQualifierImprecision;
import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfQuantifierCardinality;
import org.example.ksr_linguistic_summarization.logic.degrees.TheOptimalSummary;

import java.util.List;
import java.util.stream.Collectors;

public class HelloController {
    @FXML
    private TreeView<String> quantifierTreeView;
    @FXML
    private TreeView<String> summarizerTreeView;
    @FXML
    private TextArea resultTextArea;
    @FXML
    private TreeView<String> degreeTreeView;
    @FXML
    private ComboBox<String> sortDegreeComboBox;

    private List<LinguisticVariable> allVariables;
    private List<BodyPerformance> data;
    private List<Quantifier> quantifiers;
    private List<LinguisticVariable> summarizerVariables;

    @FXML
    public void initialize() {
        try {
            data = DataInitializer.loadBodyPerformanceData();
            List<LinguisticVariableDTO> dtos = LinguisticVariableLoader.load();
            allVariables = LinguisticVariableFactory.fromDTO(dtos, data);

            // kwantyfikatory
            quantifiers = allVariables.stream()
                    .filter(v -> v instanceof Quantifier)
                    .map(v -> (Quantifier) v)
                    .collect(Collectors.toList());

            var abs = quantifiers.stream().filter(Quantifier::isAbsolute).toList();
            var rel = quantifiers.stream().filter(q -> !q.isAbsolute()).toList();

            CheckBoxTreeItem<String> quantRoot = new CheckBoxTreeItem<>("Wszystkie");
            quantRoot.setExpanded(true);
            CheckBoxTreeItem<String> absCat = new CheckBoxTreeItem<>("Absolutne");
            for (var q : abs) {
                absCat.getChildren().add(new CheckBoxTreeItem<>(q.getName()));
            }
            CheckBoxTreeItem<String> relCat = new CheckBoxTreeItem<>("Relatywne");
            for (var q : rel) {
                relCat.getChildren().add(new CheckBoxTreeItem<>(q.getName()));
            }
            quantRoot.getChildren().addAll(absCat, relCat);
            quantifierTreeView.setRoot(quantRoot);
            quantifierTreeView.setShowRoot(false);
            quantifierTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());

            // sumaryzatory
            summarizerVariables = allVariables.stream()
                    .filter(v -> !(v instanceof Quantifier))
                    .collect(Collectors.toList());

            var grouped = summarizerVariables.stream().collect(Collectors.groupingBy(LinguisticVariable::getName));

            CheckBoxTreeItem<String> summarizerRoot = new CheckBoxTreeItem<>("Wszystkie");
            summarizerRoot.setExpanded(true);
            for (var entry : grouped.entrySet()) {
                CheckBoxTreeItem<String> category = new CheckBoxTreeItem<>(entry.getKey());
                for (var v : entry.getValue()) {
                    CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(v.getLinguisticValue().getName());
                    category.getChildren().add(item);
                }
                summarizerRoot.getChildren().add(category);
            }
            summarizerTreeView.setRoot(summarizerRoot);
            summarizerTreeView.setShowRoot(false);
            summarizerTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());

            // miary podsumowania
            CheckBoxTreeItem<String> degreeRoot = new CheckBoxTreeItem<>("Wszystkie");
            degreeRoot.setExpanded(true);
            String[] degreeNames = {
                "DegreeOfTruth",
                "DegreeOfImprecision",
                "DegreeOfAppropriateness",
                "DegreeOfCovering",
                "DegreeOfQuantifierImprecision",
                "DegreeOfSummarizerCardinality",
                "LengthOfASummary",
                "DegreeOfQualifierCardinality",
                "DegreeOfQualifierImprecision",
                "DegreeOfQuantifierCardinality",
                "TheOptimalSummary"
            };
            for (String name : degreeNames) {
                degreeRoot.getChildren().add(new CheckBoxTreeItem<>(name));
            }
            degreeTreeView.setRoot(degreeRoot);
            degreeTreeView.setShowRoot(false);
            degreeTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());
            sortDegreeComboBox.setItems(FXCollections.observableArrayList(degreeNames));
            sortDegreeComboBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            resultTextArea.setText("Błąd inicjalizacji: " + e.getMessage());
        }
    }

    private List<Quantifier> getSelectedQuantifiers() {
        List<Quantifier> selected = new java.util.ArrayList<>();
        TreeItem<String> root = quantifierTreeView.getRoot();
        collectCheckedQuantifiers(root, selected);
        return selected;
    }
    private void collectCheckedQuantifiers(TreeItem<String> node, List<Quantifier> selected) {
        if (node instanceof CheckBoxTreeItem<?> cb && cb.selectedProperty().get() && node.isLeaf()) {
            TreeItem<String> parent = node.getParent();
            if (parent != null) {
                String label = node.getValue();
                quantifiers.stream()
                        .filter(q -> q.getName().equals(label))
                        .findFirst()
                        .ifPresent(selected::add);
            }
        }
        for (TreeItem<String> child : node.getChildren()) {
            collectCheckedQuantifiers(child, selected);
        }
    }

    private List<Summarizer> getSelectedSummarizers() {
        List<Summarizer> selected = new java.util.ArrayList<>();
        TreeItem<String> root = summarizerTreeView.getRoot();
        collectCheckedSummarizers(root, selected);
        return selected;
    }
    private void collectCheckedSummarizers(TreeItem<String> node, List<Summarizer> selected) {
        if (node instanceof CheckBoxTreeItem<?> cb && cb.selectedProperty().get() && node.isLeaf()) {
            TreeItem<String> parent = node.getParent();
            if (parent != null) {
                String category = parent.getValue();
                String label = node.getValue();
                summarizerVariables.stream()
                        .filter(v -> v.getName().equals(category) && v.getLinguisticValue().getName().equals(label))
                        .findFirst()
                        .ifPresent(v -> selected.add(new Summarizer(v.getName(), v.getLinguisticValue())));
            }
        }
        for (TreeItem<String> child : node.getChildren()) {
            collectCheckedSummarizers(child, selected);
        }
    }

    private List<Degree> getSelectedDegrees() {
        List<Degree> degrees = new java.util.ArrayList<>();
        TreeItem<String> root = degreeTreeView.getRoot();
        collectCheckedDegrees(root, degrees);
        return degrees;
    }
    private void collectCheckedDegrees(TreeItem<String> node, List<Degree> degrees) {
        if (node instanceof CheckBoxTreeItem<?> cb && cb.selectedProperty().get() && node.isLeaf()) {
            String name = node.getValue();
            switch (name) {
                case "DegreeOfTruth" -> degrees.add(new DegreeOfTruth());
                case "DegreeOfImprecision" -> degrees.add(new DegreeOfImprecision());
                case "DegreeOfAppropriateness" -> degrees.add(new DegreeOfAppropriateness());
                case "DegreeOfCovering" -> degrees.add(new DegreeOfCovering());
                case "DegreeOfQuantifierImprecision" -> degrees.add(new DegreeOfQuantifierImprecision());
                case "DegreeOfSummarizerCardinality" -> degrees.add(new DegreeOfSummarizerCardinality());
                case "LengthOfASummary" -> degrees.add(new LengthOfASummary());
                case "DegreeOfQualifierCardinality" -> degrees.add(new DegreeOfQualifierCardinality());
                case "DegreeOfQualifierImprecision" -> degrees.add(new DegreeOfQualifierImprecision());
                case "DegreeOfQuantifierCardinality" -> degrees.add(new DegreeOfQuantifierCardinality());
                case "TheOptimalSummary" -> degrees.add(new TheOptimalSummary());
            }
        }
        for (TreeItem<String> child : node.getChildren()) {
            collectCheckedDegrees(child, degrees);
        }
    }

    @FXML
    protected void onGenerateSummaryClick() {
        try {
            List<Quantifier> selectedQuantifiers = getSelectedQuantifiers();
            List<Summarizer> selectedSummarizers = getSelectedSummarizers();
            List<Degree> selectedDegrees = getSelectedDegrees();
            String sortDegree = sortDegreeComboBox.getValue();

            if (selectedQuantifiers.isEmpty() || selectedSummarizers.isEmpty() || selectedDegrees.isEmpty()) {
                resultTextArea.setText("Wybierz przynajmniej jeden kwantyfikator, sumaryzator i miarę podsumowania.");
                return;
            }

            List<LinguisticSummary> summaries = LinguisticSummaryGenerator.generateSummaries(selectedQuantifiers, selectedSummarizers, data, selectedDegrees);
            if (summaries == null || summaries.isEmpty()) {
                resultTextArea.setText("Brak podsumowań dla wybranych opcji.");
                return;
            }
            if (sortDegree != null && !sortDegree.isEmpty()) {
                summaries.sort((a, b) -> {
                    double va = getDegreeValueByName(a, sortDegree);
                    double vb = getDegreeValueByName(b, sortDegree);
                    return -Double.compare(va, vb);
                });
            }
            StringBuilder sb = new StringBuilder();
            for (LinguisticSummary summary : summaries) {
                sb.append(summary.getLinguisticSummary()).append("\n");
            }
            resultTextArea.setText(sb.toString());
        } catch (Exception e) {
            resultTextArea.setText("Błąd generowania podsumowania: " + e.getMessage());
        }
    }

    private double getDegreeValueByName(LinguisticSummary summary, String degreeName) {
        for (Degree d : summary.getDegrees()) {
            if (d.getClass().getSimpleName().equals(degreeName)) {
                return d.calculateDegree(summary);
            }
        }
        return Double.NaN;
    }
}