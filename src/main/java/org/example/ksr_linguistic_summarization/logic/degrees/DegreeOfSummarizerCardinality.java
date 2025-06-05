package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;

public class DegreeOfSummarizerCardinality implements Degree {
    @Getter
    private final String name = "T8";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double result = 1.0;
        for (Summarizer summarizer : summary.getSummarizers()) {
            double s_card = summarizer.getLinguisticValue().getFuzzySet().getCardinality();
            double s_m = summary.getRecords().size();
            double S = s_card / s_m;
            result *= S;
        }
//        System.out.println(result);
        return 1 - Math.pow(result, 1.0 / summary.getSummarizers().size());
    }
}
