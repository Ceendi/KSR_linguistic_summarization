package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;

public class DegreeOfQualifierCardinality implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double result = 1.0;
        if (summary.getQualifiers().isEmpty()) return 0.0;
        for (Qualifier qualifier : summary.getQualifiers()) {
            double Q = qualifier.getLinguisticValue().getFuzzySet().getCardinality() / summary.getSubjects().size();
            result *= Q;
        }
        return 1 - Math.pow(result, 1.0 /summary.getQualifiers().size());
    }
}
