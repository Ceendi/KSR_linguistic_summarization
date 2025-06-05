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

    public List<Map<String, Double>> getLinguisticSummaryValues() {
        var result = new ArrayList<Map<String, Double>>();
        for (Degree degree : degrees) {
            Map<String, Double> map = new HashMap<>();
            var degreeValue = degree.calculateDegree(this);
            map.put(degree.getName(), Math.round(degreeValue * 1000.0) / 1000.0);
            result.add(map);
        }
        return result;
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

        var values = getLinguisticSummaryValues();
        for (var value : values) {
            for (Map.Entry<String, Double> entry : value.entrySet()) {
                summary += entry.getKey() + ": " + entry.getValue() + " | ";
            }
        }
        return summary;
    }
}
