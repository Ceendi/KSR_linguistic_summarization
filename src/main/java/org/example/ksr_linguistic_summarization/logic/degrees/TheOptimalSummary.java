package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

import java.util.List;

public class TheOptimalSummary implements Degree {

    private LinguisticSummary summary;
    List<Degree> degrees = List.of(new DegreeOfTruth(), new DegreeOfImprecision(),
            new DegreeOfCovering(), new DegreeOfAppropriateness(), new LengthOfASummary(),
            new DegreeOfQuantifierImprecision(), new DegreeOfQuantifierCardinality(),
            new DegreeOfSummarizerCardinality(), new DegreeOfQualifierCardinality(),
            new DegreeOfQualifierImprecision(), new LengthOfAQualifier());
    List<Double> weights = List.of(0.7, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03);

    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double sum = 0.0;
        if (summary.getQualifiers() != null && summary.getQualifiers().isEmpty()) {
            weights = List.of(0.7, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0.03, 0., 0., 0.);
        }
        for (int i = 0; i < 11; i++) {
            sum += degrees.get(i).calculateDegree(summary) * weights.get(i);
        }
        return sum / weights.stream().reduce(0.0, Double::sum);
    }
}
