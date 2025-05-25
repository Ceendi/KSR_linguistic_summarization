package org.example.ksr_linguistic_summarization.logic.summarization;

import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;

public class LinguisticSummary {
    private Quantifier quantifier;
    private List<Qualifier> qualifiers;
    private List<Summarizer> summarizers;
    private List<BodyPerformance> subjects;
    private SummaryType summaryType;

    public LinguisticSummary(Quantifier quantifier, List<Qualifier> qualifiers, List<Summarizer>
            summarizers, List<BodyPerformance> subjects, SummaryType summaryType) {
        this.quantifier = quantifier;
        this.qualifiers = qualifiers;
        this.summarizers = summarizers;
        this.subjects = subjects;
        this.summaryType = summaryType;
    }

    public double getSummaryValue() {
        return 0;
    }


}
