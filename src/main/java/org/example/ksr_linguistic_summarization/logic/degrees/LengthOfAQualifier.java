package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public class LengthOfAQualifier implements Degree {
    @Getter
    private final String name = "T11";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        if (summary.getQualifiers().isEmpty()) return 0.0;
        double S = summary.getQualifiers().size();
        return 2 * Math.pow(0.5, S);
    }
}
