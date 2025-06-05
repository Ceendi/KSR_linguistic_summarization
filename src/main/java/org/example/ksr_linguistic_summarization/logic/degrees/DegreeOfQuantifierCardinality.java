package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.QuantifierType;

public class DegreeOfQuantifierCardinality implements Degree {
    @Getter
    private final String name = "T7";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double r = summary.getQuantifier().getLinguisticValue().getFuzzySet().getCardinality();
        double m;
        if (summary.getQuantifier().getQuantifierType().equals(QuantifierType.RELATIVE)) {
            m = 1.0;
        }else {
            m = summary.getRecords().size();
        }
        return 1 - r/m;
    }
}
