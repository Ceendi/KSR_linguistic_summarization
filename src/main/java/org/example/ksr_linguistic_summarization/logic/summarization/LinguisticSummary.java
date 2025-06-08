package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.Getter;
import lombok.Setter;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class LinguisticSummary {
    private Quantifier quantifier;
    private List<Qualifier> qualifiers;
    private List<Summarizer> summarizers;
    private List<BodyPerformance> records;
    private SummaryType summaryType;
    private List<Degree> degrees;

    public LinguisticSummary(Quantifier quantifier, List<Qualifier> qualifiers, List<Summarizer>
            summarizers, List<BodyPerformance> records, SummaryType summaryType, List<Degree> degrees) {
        this.quantifier = quantifier;
        this.qualifiers = qualifiers;
        this.summarizers = summarizers;
        this.records = records;
        this.summaryType = summaryType;
        this.degrees = degrees;
    }

    public Map<String, Double> getLinguisticSummaryValues() {
        var result = new HashMap<String, Double>();
        for (Degree degree : degrees) {
            var degreeValue = degree.calculateDegree(this);
            result.put(degree.getName(), Math.round(degreeValue * 1000.0) / 1000.0);
        }
        return result;
    }

    public String getLinguisticSummary() {
        StringBuilder summary = new StringBuilder(quantifier.getName() + " osób");
        if (qualifiers != null && !qualifiers.isEmpty()) {
            summary.append(", która jest/posiada ");
            summary.append(qualifiers.stream()
                    .map(q -> q.getLinguisticValue().getName())
                    .collect(Collectors.joining(" i ")));
        }
        summary.append(" jest/posiada ");
        summary.append(summarizers.stream()
                .map(s -> s.getLinguisticValue().getName())
                .collect(Collectors.joining(" i "))).append(" | ");

        Map<String, Double> values = getLinguisticSummaryValues();
        List<String> order = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "To");
        for (String key : order) {
            if (values.containsKey(key)) {
                summary.append(key).append(": ").append(values.get(key)).append(" | ");
            }
        }

        return summary.toString();
    }
}
