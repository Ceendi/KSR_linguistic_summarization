package org.example.ksr_linguistic_summarization.logic.summarization;


public class Quantifier extends LinguisticVariable {
    private final QuantifierType quantifierType;
    public Quantifier(String name, LinguisticValue linguisticValue, QuantifierType quantifierType) {
        super(name, linguisticValue);
        this.quantifierType = quantifierType;
    }

    public QuantifierType getQuantifierType() {
        return quantifierType;
    }
}




