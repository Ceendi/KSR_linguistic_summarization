package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

public class DegreeOfAppropriateness implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {

        DegreeOfCovering degreeOfCovering = new DegreeOfCovering();
        double T_3 = degreeOfCovering.calculateDegree(summary);

        var result = 1.0;
        for (Summarizer summarizer : summary.getSummarizers()) {
            double sum = 0;
            for (BodyPerformance subject : summary.getSubjects()) {
                double min = Double.MAX_VALUE;
                double attrValue = subject.getAttribute(summarizer.getName());
                double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                if (min > membership) min = membership;
                if (min > 0.0) sum += 1;
            }
            result *= (sum / summary.getSubjects().size());
        }
        return Math.abs(result - T_3);
    }
}
