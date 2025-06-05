package org.example.ksr_linguistic_summarization.logic.degrees;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;

public class DegreeOfTruth implements Degree {
    @Getter
    private final String name = "T1";
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        List<BodyPerformance> subjects = summary.getRecords();
        if (subjects.isEmpty()) return 0.0;

        List<Qualifier> qualifiers = summary.getQualifiers();
        List<Summarizer> summarizers = summary.getSummarizers();

        double sum = 0.0;

        if (qualifiers == null || qualifiers.isEmpty()) {
            for (BodyPerformance bp : subjects) {
                double min = Double.MAX_VALUE;
                for (Summarizer summarizer : summarizers) {
                    double attrValue = bp.getAttribute(summarizer.getName());
                    double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (min > membership) min = membership;
                }
                sum += min;
//                return sum;
            }
//            System.out.println(sum);
            if (summary.getQuantifier().isAbsolute()) {
                return summary.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(sum);
            } else {
                double t = sum / subjects.size();
                return summary.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(t);
            }
        } else {
            if (summary.getQuantifier().isAbsolute()) throw new UnsupportedOperationException("Absolute quantifier is not supported for DegreeOfTruth with Qualifiers");
            double w_sum = 0.0;
            for (BodyPerformance bp : subjects) {
                double s_min = Double.MAX_VALUE;
                double w_min = Double.MAX_VALUE;
                for (Summarizer summarizer : summarizers) {
                    double attrValue = bp.getAttribute(summarizer.getName());
                    double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (s_min > membership) s_min = membership;
                }
                for (Qualifier qualifier : qualifiers) {
                    double attrValue = bp.getAttribute(qualifier.getName());
                    double membership = qualifier.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (w_min > membership) w_min = membership;
                }
                w_sum += w_min;
                sum += Math.min(s_min, w_min);
            }
            if (w_sum == 0.0) return 0.0;
            return summary.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(sum/w_sum);
        }
    }
}