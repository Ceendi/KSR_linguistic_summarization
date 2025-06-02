package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;

public class LengthOfAQualifier implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        if (summary.getQualifiers().isEmpty()) return 0.0;
        double S = summary.getQualifiers().size();
        return 2 * Math.pow(0.5, S);
    }
}
