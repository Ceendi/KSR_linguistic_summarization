package org.example.ksr_linguistic_summarization.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DegreeWeight {
    private final StringProperty name;
    private final DoubleProperty weight;

    public DegreeWeight(String name, double weight) {
        this.name = new SimpleStringProperty(name);
        this.weight = new SimpleDoubleProperty(weight);
    }
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public double getWeight() { return weight.get(); }
    public void setWeight(double value) { weight.set(value); }
    public DoubleProperty weightProperty() { return weight; }
}
