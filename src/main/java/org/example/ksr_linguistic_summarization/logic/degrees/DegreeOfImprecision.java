package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public class DegreeOfImprecision implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        var result = 1.0;
        for (var summarizer : summary.getSummarizers()) {
            result *= summarizer.getLinguisticValue().getFuzzySet().getDegreeOfFuzziness();
        }
        return 1 - Math.pow(result, (double) 1 / summary.getSummarizers().size());
    }
}
