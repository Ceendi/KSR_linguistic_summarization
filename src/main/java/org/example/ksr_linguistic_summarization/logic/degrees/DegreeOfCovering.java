package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.set.FuzzySet;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Qualifier;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;

public class DegreeOfCovering implements Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        if (summary.getQualifiers() != null && !summary.getQualifiers().isEmpty()) {
//            FuzzySet summarizersIntersection = summary.getSummarizers().stream()
//                    .map(s -> s.getLinguisticValue().getFuzzySet())
//                    .reduce(FuzzySet::intersection)
//                    .orElseThrow(() -> new IllegalStateException("No summarizers"));
//
//            FuzzySet qualifiersIntersection = summary.getQualifiers().stream()
//                    .map(q -> q.getLinguisticValue().getFuzzySet())
//                    .reduce(FuzzySet::intersection)
//                    .orElseThrow(() -> new IllegalStateException("No qualifiers"));
//
//            System.out.println(qualifiersIntersection.getSupport().getSize());
//            System.out.println(summarizersIntersection.getSupport().getSize());
//            System.out.println(summarizersIntersection.intersection(qualifiersIntersection).getSupport().getSize());
//
//            return summarizersIntersection.intersection(qualifiersIntersection).getSupport().getSize()
//                    / qualifiersIntersection.getSupport().getSize();
            double sum1 = 0;
            double sum_w = 0;
            for (BodyPerformance subject : summary.getSubjects()) {
                double s_min = Double.MAX_VALUE;
                for (Summarizer summarizer : summary.getSummarizers()) {
                    double attrValue = subject.getAttribute(summarizer.getName());
                    double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (s_min > membership) s_min = membership;
                }
                double w_min = Double.MAX_VALUE;
                for (Qualifier qualifier : summary.getQualifiers()) {
                    double attrValue = subject.getAttribute(qualifier.getName());
                    double membership = qualifier.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (w_min > membership) w_min = membership;
                }
                if (w_min > 0.0) sum_w += 1.0;
                if (Math.min(s_min, w_min) > 0.0) sum1 += 1.0;

//                if (s_min > 0.0) sum1 += 1;
            }
//            System.out.println(sum1);
//            System.out.println(sum_w);
            return sum1 / sum_w;
        } else {
            double sum2 = 0;
            for (BodyPerformance subject : summary.getSubjects()) {
                double min = Double.MAX_VALUE;
                for (Summarizer summarizer : summary.getSummarizers()) {
                    double attrValue = subject.getAttribute(summarizer.getName());
                    double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (min > membership) min = membership;
                }
                if (min > 0.0) sum2 += 1;
            }
            return sum2 / summary.getSubjects().size();
        }


    }
}
