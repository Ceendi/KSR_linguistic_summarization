package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public class LengthOfASummary implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double S = summary.getSummarizers().size();
        return 2 * Math.pow(0.5, S);
    }
}
