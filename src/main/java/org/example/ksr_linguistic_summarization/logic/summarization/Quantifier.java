package org.example.ksr_linguistic_summarization.logic.summarization;


import lombok.Getter;

@Getter
public class Quantifier extends LinguisticVariable {
    private final QuantifierType quantifierType;
    public Quantifier(String name, LinguisticValue linguisticValue, QuantifierType quantifierType) {
        super(name, linguisticValue);
        this.quantifierType = quantifierType;
    }

    public boolean isAbsolute() {
        return quantifierType == QuantifierType.ABSOLUTE;
    }
}




