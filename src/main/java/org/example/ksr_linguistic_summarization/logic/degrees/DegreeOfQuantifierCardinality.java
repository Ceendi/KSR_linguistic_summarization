package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.QuantifierType;

public class DegreeOfQuantifierCardinality implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        double r = summary.getQuantifier().getLinguisticValue().getFuzzySet().getCardinality();
        double m;
        if (summary.getQuantifier().getQuantifierType().equals(QuantifierType.RELATIVE)) {
            m = 1.0;
        }else {
            m = summary.getSubjects().size();
        }
        return 1 - r/m;
    }
}
