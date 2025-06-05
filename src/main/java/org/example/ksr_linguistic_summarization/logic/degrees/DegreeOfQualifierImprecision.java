package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;

public class DegreeOfQualifierImprecision implements Degree {
    @Getter
    private final String name = "T9";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double result = 1.0;
        if (summary.getQualifiers().isEmpty()) return 0.0;
        for (Qualifier qualifier : summary.getQualifiers()) {
            result *= qualifier.getLinguisticValue().getFuzzySet().getDegreeOfFuzziness();
        }
        return 1 - Math.pow(result, 1.0 / summary.getQualifiers().size());
    }
}
