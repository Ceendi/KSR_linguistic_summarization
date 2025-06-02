package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Quantifier;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

public class DegreeOfQuantifierImprecision implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        Quantifier quantifier = summary.getQuantifier();
        return 1 - quantifier.getLinguisticValue().getFuzzySet().getDegreeOfFuzziness();
    }
}
