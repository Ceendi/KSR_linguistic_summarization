package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

import java.util.List;
import java.util.Map;

public class TheOptimalSummary implements Degree {
    @Getter
    private final String name = "To";
    Map<String, Double> weights;
    List<Degree> degrees;

    public TheOptimalSummary(List<Degree> degrees, Map<String, Double> weights) {
        this.degrees = degrees;
        this.weights = weights;
    }

    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double sum = 0.0;
        if (summary.getQualifiers() != null && summary.getQualifiers().isEmpty()) {
            weights.put("T9", 0.);
            weights.put("T10", 0.);
            weights.put("T11", 0.);
        }
        for (int i = 0; i < 11; i++) {
            sum += degrees.get(i).calculateDegree(summary) * weights.get(degrees.get(i).getName());
        }
        return sum / weights.values().stream().reduce(0.0, Double::sum);
    }
}
