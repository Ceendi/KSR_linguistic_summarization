package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.MultiSubjectLinguisticSummary;
import org.example.ksr_linguistic_summarization.logic.summarization.Summarizer;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiSubjectDegrees implements  Degree {
    @Override
    public double calculateDegree(LinguisticSummary summary) {
        if (summary instanceof MultiSubjectLinguisticSummary multi) {
            return switch (multi.getForm()) {
                case FIRST_FORM -> getFirstFormValue(multi);
                case SECOND_FORM -> getSecondFormValue(multi);
                case THIRD_FORM -> getThirdFormValue(multi);
                case FOURTH_FORM -> getFourthFormValue(multi);
            };
        }
        throw new IllegalArgumentException("Invalid LinguisticSummary");
    }

    @Override
    public String getName() {
        return "T1";
    }

    private double andCardinalityForGender(char gender, boolean withQualifiers, MultiSubjectLinguisticSummary multi) {
        return multi.getRecords().stream()
                .filter(bp -> bp.getGender() == gender)
                .mapToDouble(bp -> {
                    double minSummarizer = multi.getSummarizers().stream()
                            .mapToDouble(s -> s.getLinguisticValue().getFuzzySet().getMembershipDegree(bp.getAttribute(s.getName())))
                            .min()
                            .orElse(1.0);

                    if (withQualifiers && !multi.getQualifiers().isEmpty()) {
                        double minQualifier = multi.getQualifiers().stream()
                                .mapToDouble(q -> q.getLinguisticValue().getFuzzySet().getMembershipDegree(bp.getAttribute(q.getName())))
                                .min()
                                .orElse(1.0);
                        return Math.min(minSummarizer, minQualifier);
                    } else {
                        return minSummarizer;
                    }
                })
                .sum();
    }

    private Double getFirstFormValue(MultiSubjectLinguisticSummary multi) {
        char gender1 = multi.getSubjects().getFirst();
        char gender2 = multi.getSubjects().get(1);

        int M1 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, false, multi);
        double nfoCount2 = andCardinalityForGender(gender2, false, multi);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return multi.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }


    private Double getSecondFormValue(MultiSubjectLinguisticSummary multi) {
        char gender1 = multi.getSubjects().getFirst();
        char gender2 = multi.getSubjects().get(1);

        int M1 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, false, multi);
        double nfoCount2 = andCardinalityForGender(gender2, true, multi);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return multi.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }

    private Double getThirdFormValue(MultiSubjectLinguisticSummary multi) {
        char gender1 = multi.getSubjects().getFirst();
        char gender2 = multi.getSubjects().get(1);

        int M1 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) multi.getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, true, multi);
        double nfoCount2 = andCardinalityForGender(gender2, false, multi);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return multi.getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }

    private Double getFourthFormValue(MultiSubjectLinguisticSummary multi) {
        double sum = 0.0;
        Map<Character, List<BodyPerformance>> subjectsByGender = multi.getRecords().stream()
                .collect(Collectors.groupingBy(BodyPerformance::getGender));

        for (BodyPerformance record : multi.getRecords()) {
            boolean calculateNext = true;

            Character otherGender = multi.getSubjects().getFirst() == 'M' ? 'F' : 'M';

            double minFirst = Double.MAX_VALUE;
            for (Summarizer summarizer : multi.getSummarizers()) {
                double attrValue = record.getAttribute(summarizer.getName());
                double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                if (minFirst > membership) minFirst = membership;
                boolean attr = subjectsByGender.get(otherGender).stream()
                        .anyMatch(o -> o.getAttribute(summarizer.getName()) == attrValue);
                if (!attr) {
                    calculateNext = false;
                }
            }

            double minSecond = 0.0;

            if (calculateNext) {
                minSecond = minFirst;
            }

            if (record.getGender() == multi.getSubjects().getFirst()) {
                sum += 1 - minFirst + minSecond * minFirst;
            } else {
                sum += 1 - minSecond + minFirst * minSecond;
            }

        }

        sum /= multi.getRecords().size();

        return 1.0 - sum;
    }
}
