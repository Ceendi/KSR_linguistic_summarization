package org.example.ksr_linguistic_summarization.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import org.example.ksr_linguistic_summarization.logic.set.ContinuousSet;
import org.example.ksr_linguistic_summarization.logic.set.FuzzySet;
import org.example.ksr_linguistic_summarization.logic.summarization.*;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;
import org.example.ksr_linguistic_summarization.logic.utils.DataInitializer;
import org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable.FuzzySetDTO;
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
import java.util.Optional;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;
import java.util.Arrays;

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
    @FXML
    private ComboBox<String> sortDegreeComboBox;
    @FXML
    private TextField saveCountField;
    private List<BodyPerformance> data;
    private List<Quantifier> quantifiers;
    private List<LinguisticVariable> summarizerVariables;
    private final javafx.collections.ObservableList<DegreeWeight> degreeWeights = javafx.collections.FXCollections.observableArrayList();

    private static final Map<String, String> DEGREE_NAME_TO_KEY = Map.ofEntries(
        Map.entry("DegreeOfTruth", "T1"),
        Map.entry("DegreeOfImprecision", "T2"),
        Map.entry("DegreeOfCovering", "T3"),
        Map.entry("DegreeOfAppropriateness", "T4"),
        Map.entry("LengthOfASummary", "T5"),
        Map.entry("DegreeOfQuantifierImprecision", "T6"),
        Map.entry("DegreeOfQuantifierCardinality", "T7"),
        Map.entry("DegreeOfSummarizerCardinality", "T8"),
        Map.entry("DegreeOfQualifierImprecision", "T9"),
        Map.entry("DegreeOfQualifierCardinality", "T10"),
        Map.entry("LengthOfAQualifier", "T11"),
        Map.entry("OptimalSummary", "To")
    );

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

            sortDegreeComboBox.setItems(javafx.collections.FXCollections.observableArrayList(DEGREE_NAME_TO_KEY.keySet()));
            sortDegreeComboBox.getSelectionModel().select("OptimalSummary");
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

            String selectedDegreeName = sortDegreeComboBox.getValue();
            String sortDegree = DEGREE_NAME_TO_KEY.getOrDefault(selectedDegreeName, "To");
            StringBuilder sb = new StringBuilder();

            sb.append("--- Jednopodmiotowe - forma 1 ---\n");
            summaries.stream()
                .filter(s -> !(s instanceof MultiSubjectLinguisticSummary) && (s.getQualifiers() == null || s.getQualifiers().isEmpty()))
                .sorted((a, b) -> {
                    Double va = a.getLinguisticSummaryValues().get(sortDegree);
                    Double vb = b.getLinguisticSummaryValues().get(sortDegree);
                    if (va == null) va = 0.0;
                    if (vb == null) vb = 0.0;
                    return -Double.compare(va, vb);
                })
                .forEach(s -> sb.append(s.getLinguisticSummary()).append("\n"));

            sb.append("--- Jednopodmiotowe - forma 2  ---\n");
            summaries.stream()
                .filter(s -> !(s instanceof MultiSubjectLinguisticSummary) && s.getQualifiers() != null && !s.getQualifiers().isEmpty())
                .sorted((a, b) -> {
                    Double va = a.getLinguisticSummaryValues().get(sortDegree);
                    Double vb = b.getLinguisticSummaryValues().get(sortDegree);
                    if (va == null) va = 0.0;
                    if (vb == null) vb = 0.0;
                    return -Double.compare(va, vb);
                })
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

    @FXML
    protected void onAddCustomSummarizerClick() {
        Dialog<LinguisticVariable> dialog = new Dialog<>();
        dialog.setTitle("Dodaj własny sumaryzator");
        dialog.setHeaderText("Wprowadź dane dla nowego sumaryzatora");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> attributeCombo = new ComboBox<>();
        attributeCombo.getItems().addAll(
            "age", "heightCm", "weightKg", "bodyFatPercentage", "diastolic", "systolic",
            "gripForce", "sitAndBendForwardCm", "sitUpsCounts", "broadJumpCm"
        );
        attributeCombo.setPromptText("Wybierz atrybut");

        TextField nameField = new TextField();
        nameField.setPromptText("Nazwa sumaryzatora");

        ComboBox<String> functionTypeCombo = new ComboBox<>();
        functionTypeCombo.getItems().addAll("triangular", "trapezoidal", "gaussian");
        functionTypeCombo.setPromptText("Typ funkcji przynależności");

        TextField paramAField = new TextField();
        paramAField.setPromptText("Parametr a");
        TextField paramBField = new TextField();
        paramBField.setPromptText("Parametr b");
        TextField paramCField = new TextField();
        paramCField.setPromptText("Parametr c");
        TextField paramDField = new TextField();
        paramDField.setPromptText("Parametr d");

        grid.add(new Label("Atrybut:"), 0, 0);
        grid.add(attributeCombo, 1, 0);
        grid.add(new Label("Nazwa:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Typ funkcji:"), 0, 2);
        grid.add(functionTypeCombo, 1, 2);
        grid.add(new Label("Parametry:"), 0, 3);
        grid.add(paramAField, 1, 3);
        grid.add(paramBField, 1, 4);
        grid.add(paramCField, 1, 5);
        grid.add(paramDField, 1, 6);

        paramAField.setVisible(false);
        paramBField.setVisible(false);
        paramCField.setVisible(false);
        paramDField.setVisible(false);

        functionTypeCombo.setOnAction(e -> {
            String type = functionTypeCombo.getValue();
            paramAField.setVisible(false);
            paramBField.setVisible(false);
            paramCField.setVisible(false);
            paramDField.setVisible(false);

            if (type != null) {
                paramAField.setVisible(true);
                paramBField.setVisible(true);
                if ("gaussian".equals(type)) {
                    paramAField.setPromptText("Średnia (m)");
                    paramBField.setPromptText("Odchylenie standardowe (sigma)");
                } else if ("triangular".equals(type)) {
                    paramAField.setPromptText("Lewy punkt (a)");
                    paramBField.setPromptText("Środkowy punkt (b)");
                    paramCField.setPromptText("Prawy punkt (c)");
                    paramCField.setVisible(true);
                } else if ("trapezoidal".equals(type)) {
                    paramAField.setPromptText("Lewy punkt (a)");
                    paramBField.setPromptText("Lewy-środkowy punkt (b)");
                    paramCField.setPromptText("Prawy-środkowy punkt (c)");
                    paramDField.setPromptText("Prawy punkt (d)");
                    paramCField.setVisible(true);
                    paramDField.setVisible(true);
                }
            }
        });

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String attribute = attributeCombo.getValue();
                    if (attribute == null) {
                        throw new IllegalArgumentException("Wybierz atrybut");
                    }
                    String name = nameField.getText();
                    if (name == null || name.trim().isEmpty()) {
                        throw new IllegalArgumentException("Wprowadź nazwę sumaryzatora");
                    }
                    String type = functionTypeCombo.getValue();
                    if (type == null) {
                        throw new IllegalArgumentException("Wybierz typ funkcji przynależności");
                    }

                    double a = Double.parseDouble(paramAField.getText());
                    double b = Double.parseDouble(paramBField.getText());

                    FuzzySetDTO fuzzySetDTO = new FuzzySetDTO();
                    fuzzySetDTO.type = type;
                    
                    if ("trapezoidal".equals(type)) {
                        double c = Double.parseDouble(paramCField.getText());
                        double d = Double.parseDouble(paramDField.getText());
                        fuzzySetDTO.params = new double[]{a, b, c, d};
                    } else if ("gaussian".equals(type)) {
                        fuzzySetDTO.params = new double[]{a, b}; // a = m, b = sigma
                    } else {
                        double c = Double.parseDouble(paramCField.getText());
                        fuzzySetDTO.params = new double[]{a, b, c};
                    }

                    FuzzySet fuzzySet = LinguisticVariableFactory.createFuzzySet(fuzzySetDTO, new ArrayList<>());
                    return new LinguisticVariable(attribute, new LinguisticValue(name, fuzzySet));
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Nieprawidłowe dane");
                    alert.setContentText("Wszystkie parametry muszą być liczbami.");
                    alert.showAndWait();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd podczas tworzenia sumaryzatora");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
            return null;
        });

        Optional<LinguisticVariable> result = dialog.showAndWait();
        result.ifPresent(newSummarizer -> {
            summarizerVariables.add(newSummarizer);
            updateSummarizerTreeView();
        });
    }

    private void updateSummarizerTreeView() {
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
    }

    private List<Double> getSampleValues(String attributeName, List<BodyPerformance> data) {
        return data.stream()
                .map(bp -> bp.getAttribute(attributeName))
                .collect(Collectors.toList());
    }

    @FXML
    protected void onAddCustomQuantifierClick() {
        Dialog<Quantifier> dialog = new Dialog<>();
        dialog.setTitle("Dodaj własny kwantyfikator");
        dialog.setHeaderText("Wprowadź dane dla nowego kwantyfikatora");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nazwa kwantyfikatora");

        ComboBox<String> functionTypeCombo = new ComboBox<>();
        functionTypeCombo.getItems().addAll("triangular", "trapezoidal", "gaussian");
        functionTypeCombo.setPromptText("Typ funkcji przynależności");

        ComboBox<String> quantifierTypeCombo = new ComboBox<>();
        quantifierTypeCombo.getItems().addAll("ABSOLUTE", "RELATIVE");
        quantifierTypeCombo.setPromptText("Typ kwantyfikatora");

        TextField paramAField = new TextField();
        paramAField.setPromptText("Parametr a");
        TextField paramBField = new TextField();
        paramBField.setPromptText("Parametr b");
        TextField paramCField = new TextField();
        paramCField.setPromptText("Parametr c");
        TextField paramDField = new TextField();
        paramDField.setPromptText("Parametr d");

        grid.add(new Label("Nazwa:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Typ kwantyfikatora:"), 0, 1);
        grid.add(quantifierTypeCombo, 1, 1);
        grid.add(new Label("Typ funkcji:"), 0, 2);
        grid.add(functionTypeCombo, 1, 2);
        grid.add(new Label("Parametry:"), 0, 3);
        grid.add(paramAField, 1, 3);
        grid.add(paramBField, 1, 4);
        grid.add(paramCField, 1, 5);
        grid.add(paramDField, 1, 6);

        paramAField.setVisible(false);
        paramBField.setVisible(false);
        paramCField.setVisible(false);
        paramDField.setVisible(false);

        functionTypeCombo.setOnAction(e -> {
            String type = functionTypeCombo.getValue();
            paramAField.setVisible(false);
            paramBField.setVisible(false);
            paramCField.setVisible(false);
            paramDField.setVisible(false);

            if (type != null) {
                paramAField.setVisible(true);
                paramBField.setVisible(true);
                if ("gaussian".equals(type)) {
                    paramAField.setPromptText("Średnia (m)");
                    paramBField.setPromptText("Odchylenie standardowe (sigma)");
                } else if ("triangular".equals(type)) {
                    paramAField.setPromptText("Lewy punkt (a)");
                    paramBField.setPromptText("Środkowy punkt (b)");
                    paramCField.setPromptText("Prawy punkt (c)");
                    paramCField.setVisible(true);
                } else if ("trapezoidal".equals(type)) {
                    paramAField.setPromptText("Lewy punkt (a)");
                    paramBField.setPromptText("Lewy-środkowy punkt (b)");
                    paramCField.setPromptText("Prawy-środkowy punkt (c)");
                    paramDField.setPromptText("Prawy punkt (d)");
                    paramCField.setVisible(true);
                    paramDField.setVisible(true);
                }
            }
        });

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    if (name == null || name.trim().isEmpty()) {
                        throw new IllegalArgumentException("Wprowadź nazwę kwantyfikatora");
                    }
                    String type = functionTypeCombo.getValue();
                    if (type == null) {
                        throw new IllegalArgumentException("Wybierz typ funkcji przynależności");
                    }
                    String quantifierType = quantifierTypeCombo.getValue();
                    if (quantifierType == null) {
                        throw new IllegalArgumentException("Wybierz typ kwantyfikatora");
                    }

                    double a = Double.parseDouble(paramAField.getText());
                    double b = Double.parseDouble(paramBField.getText());


                    if ("ABSOLUTE".equals(quantifierType)) {
                        if (a < 0 || a > 13369 || b < 0 || b > 13369) {
                            throw new IllegalArgumentException("Dla kwantyfikatora absolutnego wartości muszą być między 0 a 13369");
                        }
                    } else {
                        if (a < 0 || a > 1 || b < 0 || b > 1) {
                            throw new IllegalArgumentException("Dla kwantyfikatora względnego wartości muszą być między 0 a 1");
                        }
                    }

                    FuzzySetDTO fuzzySetDTO = new FuzzySetDTO();
                    fuzzySetDTO.type = type;
                    
                    if ("trapezoidal".equals(type)) {
                        double c = Double.parseDouble(paramCField.getText());
                        double d = Double.parseDouble(paramDField.getText());
                        if ("ABSOLUTE".equals(quantifierType)) {
                            if (c < 0 || c > 13369 || d < 0 || d > 13369) {
                                throw new IllegalArgumentException("Dla kwantyfikatora absolutnego wartości muszą być między 0 a 13369");
                            }
                        } else {
                            if (c < 0 || c > 1 || d < 0 || d > 1) {
                                throw new IllegalArgumentException("Dla kwantyfikatora względnego wartości muszą być między 0 a 1");
                            }
                        }
                        fuzzySetDTO.params = new double[]{a, b, c, d};
                    } else if ("gaussian".equals(type)) {
                        fuzzySetDTO.params = new double[]{a, b};
                    } else {
                        double c = Double.parseDouble(paramCField.getText());
                        if ("ABSOLUTE".equals(quantifierType)) {
                            if (c < 0 || c > 13369) {
                                throw new IllegalArgumentException("Dla kwantyfikatora absolutnego wartości muszą być między 0 a 13369");
                            }
                        } else {
                            if (c < 0 || c > 1) {
                                throw new IllegalArgumentException("Dla kwantyfikatora względnego wartości muszą być między 0 a 1");
                            }
                        }
                        fuzzySetDTO.params = new double[]{a, b, c};
                    }

                    FuzzySet fuzzySet = LinguisticVariableFactory.createFuzzySet(fuzzySetDTO, new ArrayList<>());
                    if ("ABSOLUTE".equals(quantifierType)) {
                        fuzzySet.setUniverseOfDiscourse(new ContinuousSet(0, 13369));
                    } else {
                        fuzzySet.setUniverseOfDiscourse(new ContinuousSet(0, 1));
                    }

                    return new Quantifier(name, new LinguisticValue(name, fuzzySet), QuantifierType.valueOf(quantifierType));
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Nieprawidłowe dane");
                    alert.setContentText("Wszystkie parametry muszą być liczbami.");
                    alert.showAndWait();
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd podczas tworzenia kwantyfikatora");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
            return null;
        });

        Optional<Quantifier> result = dialog.showAndWait();
        result.ifPresent(newQuantifier -> {
            quantifiers.add(newQuantifier);
            updateQuantifierTreeView();
        });
    }

    private void updateQuantifierTreeView() {
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
    }

    @FXML
    protected void onSaveSummariesClick() {
        try {
            int count = Integer.parseInt(saveCountField.getText());
            if (count <= 0) {
                throw new IllegalArgumentException("Liczba podsumowań musi być większa od 0");
            }

            String text = resultTextArea.getText();
            if (text == null || text.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Brak podsumowań");
                alert.setContentText("Najpierw wygeneruj podsumowania przed zapisaniem do pliku.");
                alert.showAndWait();
                return;
            }

            List<String> allLines = Arrays.asList(text.split("\n"));
            List<String> summaries = new ArrayList<>();
            int summaryCount = 0;
            int currentIndex = 0;

            while (summaryCount < count && currentIndex < allLines.size()) {
                String line = allLines.get(currentIndex).trim();
                if (!line.isEmpty()) {
                    if (line.contains("forma 1") || line.contains("forma 2") || line.contains("FIRST_FORM") ||
                            line.contains("SECOND_FORM") || line.contains("THIRD_FORM") || line.contains("FOURTH_FORM")) {
                        summaries.add(line);
                    } else {
                        summaries.add(line);
                        summaryCount++;
                    }
                }
                currentIndex++;
            }

            if (summaries.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Brak podsumowań");
                alert.setContentText("Nie znaleziono żadnych podsumowań do zapisania.");
                alert.showAndWait();
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz podsumowania");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt")
            );
            File file = fileChooser.showSaveDialog(saveCountField.getScene().getWindow());
            
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Podsumowania lingwistyczne (top " + count + "):\n\n");
                    int summaryNumber = 1;
                    for (String line : summaries) {
                        if (line.contains("forma 1") || line.contains("forma 2") || line.contains("FIRST_FORM") ||
                                line.contains("SECOND_FORM") || line.contains("THIRD_FORM") || line.contains("FOURTH_FORM")) {
                            writer.write(line + "\n");
                        } else {
                            writer.write(summaryNumber + ". " + line + "\n");
                            summaryNumber++;
                        }
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sukces");
                alert.setHeaderText("Zapisano podsumowania");
                alert.setContentText("Pomyślnie zapisano " + summaryCount + " podsumowań do pliku.");
                alert.showAndWait();
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nieprawidłowa liczba");
            alert.setContentText("Wprowadź poprawną liczbę podsumowań do zapisania.");
            alert.showAndWait();
        } catch (IllegalArgumentException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nieprawidłowa wartość");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd zapisu");
            alert.setContentText("Wystąpił błąd podczas zapisywania do pliku: " + ex.getMessage());
            alert.showAndWait();
        }
    }
}