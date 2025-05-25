package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;

public class DegreeOfTruth implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        List<BodyPerformance> subjects = summary.getSubjects();
        if (subjects.isEmpty()) return 0.0;

        List<Qualifier> qualifiers = summary.getQualifiers();
        List<Summarizer> summarizers = summary.getSummarizers();

        double sum = 0.0;

        for (BodyPerformance bp : subjects) {
            double summarizersDegree = 1.0;
            for (Summarizer summarizer : summarizers) {
                double attrValue = bp.getAttribute(summarizer.getName());
                double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                summarizersDegree *= membership; // t-norma: iloczyn
            }

            double qualifiersDegree = 1.0;
            if (qualifiers != null && !qualifiers.isEmpty()) {
                for (Qualifier qualifier : qualifiers) {
                    double attrValue = bp.getAttribute(qualifier.getName());
                    double membership = qualifier.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    qualifiersDegree *= membership; // t-norma: iloczyn
                }
            }

            double implication = Math.min(1.0, 1.0 - qualifiersDegree + summarizersDegree);

            sum += implication;
        }

        double t = sum / subjects.size();

        return summary.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(t);
    }
}