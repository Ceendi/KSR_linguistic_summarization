package org.example.ksr_linguistic_summarization.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class MainController {
    @FXML
    private TreeView<String> quantifierTreeView;
    @FXML
    private TreeView<String> summarizerTreeView;
    @FXML
    private TextArea resultTextArea;
    @FXML
    private TableView<DegreeWeight> degreeTableView;
    @FXML
    private TableColumn<DegreeWeight, String> degreeNameColumn;
    @FXML
    private TableColumn<DegreeWeight, Number> degreeWeightColumn;
    private List<BodyPerformance> data;
    private List<Quantifier> quantifiers;
    private List<LinguisticVariable> summarizerVariables;
    private final javafx.collections.ObservableList<DegreeWeight> degreeWeights = javafx.collections.FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        try {
            data = DataInitializer.loadBodyPerformanceData();
            List<LinguisticVariableDTO> dtos = LinguisticVariableLoader.load();
            List<LinguisticVariable> allVariables = LinguisticVariableFactory.fromDTO(dtos, data);

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

            // W initialize()
            degreeNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            degreeWeightColumn.setCellValueFactory(cellData -> cellData.getValue().weightProperty());
            degreeWeightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));
            degreeWeightColumn.setOnEditCommit(event -> {
                event.getRowValue().setWeight(event.getNewValue().doubleValue());
            });
            degreeTableView.setEditable(true);
            degreeWeights.addAll(
                new DegreeWeight("T1", 0.3),
                new DegreeWeight("T2", 0.07),
                new DegreeWeight("T3", 0.07),
                new DegreeWeight("T4", 0.07),
                new DegreeWeight("T5", 0.07),
                new DegreeWeight("T6", 0.07),
                new DegreeWeight("T7", 0.07),
                new DegreeWeight("T8", 0.07),
                new DegreeWeight("T9", 0.07),
                new DegreeWeight("T10", 0.07),
                new DegreeWeight("T11", 0.07)
            );
            degreeTableView.setItems(degreeWeights);
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

    @FXML
    protected void onGenerateSummaryClick() {
        try {
            List<Quantifier> selectedQuantifiers = getSelectedQuantifiers();
            List<Summarizer> selectedSummarizers = getSelectedSummarizers();

            Map<String, Double> weights = degreeWeights.stream()
                    .collect(java.util.stream.Collectors.toMap(DegreeWeight::getName, DegreeWeight::getWeight));

            if (selectedQuantifiers.isEmpty() || selectedSummarizers.isEmpty()) {
                resultTextArea.setText("Wybierz przynajmniej jeden kwantyfikator i sumaryzator.");
                return;
            }

            List<LinguisticSummary> summaries = LinguisticSummaryGenerator.generateSummaries(selectedQuantifiers, selectedSummarizers, data, weights);
            if (summaries.isEmpty()) {
                resultTextArea.setText("Brak podsumowań dla wybranych opcji.");
                return;
            }

            StringBuilder sb = new StringBuilder();

            sb.append("--- Jednopodmiotowe - forma 1 ---\n");
            summaries.stream()
                .filter(s -> !(s instanceof MultiSubjectLinguisticSummary) && (s.getQualifiers() == null || s.getQualifiers().isEmpty()))
                .forEach(s -> sb.append(s.getLinguisticSummary()).append("\n"));

            sb.append("--- Jednopodmiotowe - forma 2  ---\n");
            summaries.stream()
                    .filter(s -> !(s instanceof MultiSubjectLinguisticSummary) && s.getQualifiers() != null && !s.getQualifiers().isEmpty())
                    .forEach(s -> sb.append(s.getLinguisticSummary()).append("\n"));

            for (MultiSubjectType form : MultiSubjectType.values()) {
                sb.append("--- Multi subject summary - ").append(form).append(" ---\n");
                var multiSummaries = summaries.stream()
                        .filter(s -> s instanceof MultiSubjectLinguisticSummary)
                        .map(s -> (MultiSubjectLinguisticSummary) s)
                        .filter(multi -> multi.getForm() == form).sorted((a, b) -> {
                            double va = a.getLinguisticSummaryValues().get("T1");
                            double vb = b.getLinguisticSummaryValues().get("T1");
                            return -Double.compare(va, vb);
                        }).toList();


                multiSummaries.forEach(multi -> sb.append(multi.getLinguisticSummary()).append("\n"));
            }

            resultTextArea.setText(sb.toString());
        } catch (Exception e) {
            resultTextArea.setText("Błąd generowania podsumowania: " + e.getMessage());
        }
    }
}