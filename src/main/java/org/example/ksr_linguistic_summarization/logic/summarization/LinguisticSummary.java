package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.Getter;
import lombok.Setter;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LinguisticSummary {
    private Quantifier quantifier;
    private List<Qualifier> qualifiers;
    private List<Summarizer> summarizers;
    private List<BodyPerformance> subjects;
    private SummaryType summaryType;
    private List<Degree> degrees;
    private double summaryValue;

    public LinguisticSummary(Quantifier quantifier, List<Qualifier> qualifiers, List<Summarizer>
            summarizers, List<BodyPerformance> subjects, SummaryType summaryType, List<Degree> degrees) {
        this.quantifier = quantifier;
        this.qualifiers = qualifiers;
        this.summarizers = summarizers;
        this.subjects = subjects;
        this.summaryType = summaryType;
        this.degrees = degrees;

        this.summaryValue = getSummaryValue();
    }

    public double getSummaryValue() {
        double sum = 0.0;

        for (Degree degree : degrees) {
            sum += degree.calculateDegree(this);
        }
        return sum / degrees.size();
    }

    public String getLinguisticSummary() {
        String summary = quantifier.getName() + " osób";
        if (qualifiers != null && !qualifiers.isEmpty()) {
            summary += ", która jest/posiada ";
            summary += qualifiers.stream()
                    .map(q -> q.getLinguisticValue().getName())
                    .collect(Collectors.joining(" i "));
        }
        summary += " jest/posiada ";
        summary += summarizers.stream()
                .map(s -> s.getLinguisticValue().getName())
                .collect(Collectors.joining(" i ")) + " | ";
//                + " [" + Math.round(summaryValue * 1000.0) / 1000.0 + "]";
        int i = 1;
        for(Degree degree : degrees) {
            summary += "T" + i + ": " +
            Math.round(degree.calculateDegree(this) * 1000.0) / 1000.0 + " ";
            i++;
        }
        return summary;
    }
}
