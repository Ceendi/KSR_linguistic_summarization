package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Quantifier;

public class DegreeOfQuantifierImprecision implements Degree {
    @Getter
    private final String name = "T6";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        Quantifier quantifier = summary.getQuantifier();
        return 1 - quantifier.getLinguisticValue().getFuzzySet().getDegreeOfFuzziness();
    }
}
