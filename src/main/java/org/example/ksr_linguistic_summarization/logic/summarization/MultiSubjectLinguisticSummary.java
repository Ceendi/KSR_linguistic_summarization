package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiSubjectLinguisticSummary extends LinguisticSummary {

    List<Character> subjects;
    @Getter
    MultiSubjectType form;


    public MultiSubjectLinguisticSummary(Quantifier quantifier, List<Qualifier> qualifiers, List<Summarizer> summarizers,
                                         List<BodyPerformance> records, SummaryType summaryType, List<Degree> degrees,
                                         List<Character> subjects, MultiSubjectType form) {
        super(quantifier, qualifiers, summarizers, records, summaryType, degrees);
        if (subjects.size() != 2) throw new IllegalArgumentException("Invalid subjects size");
        this.subjects = subjects;
        this.form = form;
    }

    @Override
    public List<Map<String, Double>> getLinguisticSummaryValues() {
        return switch (form) {
            case FIRST_FORM -> List.of(Map.of("T1", getFirstFormValue()));
            case SECOND_FORM -> List.of(Map.of("T1", getSecondFormValue()));
            case THIRD_FORM -> List.of(Map.of("T1", getThirdFormValue()));
            case FOURTH_FORM -> List.of(Map.of("T1", getFourthFormValue()));
        };
    }

    private double andCardinalityForGender(char gender, boolean withQualifiers) {
        return getRecords().stream()
                .filter(bp -> bp.getGender() == gender)
                .mapToDouble(bp -> {
                    double minSummarizer = getSummarizers().stream()
                            .mapToDouble(s -> s.getLinguisticValue().getFuzzySet().getMembershipDegree(bp.getAttribute(s.getName())))
                            .min()
                            .orElse(1.0);

                    if (withQualifiers && !getQualifiers().isEmpty()) {
                        double minQualifier = getQualifiers().stream()
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

    private Double getFirstFormValue() {
        char gender1 = subjects.get(0);
        char gender2 = subjects.get(1);

        int M1 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, false);
        double nfoCount2 = andCardinalityForGender(gender2, false);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }


    private Double getSecondFormValue() {
        char gender1 = subjects.get(0);
        char gender2 = subjects.get(1);

        int M1 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, false);
        double nfoCount2 = andCardinalityForGender(gender2, true);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }

    private Double getThirdFormValue() {
        char gender1 = subjects.get(0);
        char gender2 = subjects.get(1);

        int M1 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender1).count();
        int M2 = (int) getRecords().stream().filter(bp -> bp.getGender() == gender2).count();

        double nfoCount1 = andCardinalityForGender(gender1, true);
        double nfoCount2 = andCardinalityForGender(gender2, false);

        var value = ((1.0 / M1) * nfoCount1) / (((1.0 / M1) * nfoCount1) + ((1.0 / M2) * nfoCount2));

        return getQuantifier().getLinguisticValue().getFuzzySet().getMembershipDegree(value);
    }

    private Double getFourthFormValue() {
        double sum = 0.0;
        for (BodyPerformance record : getRecords()) {
//            for (Character gender : subjects) {
                double min = Double.MAX_VALUE;
                for (Summarizer summarizer : getSummarizers()) {
                    double attrValue = record.getAttribute(summarizer.getName());
                    double membership = summarizer.getLinguisticValue().getFuzzySet().getMembershipDegree(attrValue);
                    if (min > membership) min = membership;
                }

                if (record.getGender() == subjects.getFirst()) {
                    sum += 1.0 - min;
//                    getSummarizers().getFirst().getLinguisticValue().getFuzzySet().getMembershipDegree(record);
                } else {
                    sum += 1.0;
                }
//            }
        }
        sum /= getRecords().size();


        return 1.0 - sum;
    }

    @Override
    public String getLinguisticSummary() {
        return switch (form) {
            case FIRST_FORM -> String.format("%s %s w porównaniu do %s jest/ma %s [%s]",
                    getQuantifier().getName(),
                    subjects.get(0),
                    subjects.get(1),
                    getSummarizersText(),
                    getValuesText()
            );
            case SECOND_FORM -> String.format("%s %s w porównaniu do %s, które są/mają %s jest/ma %s [%s]",
                    getQuantifier().getName(),
                    subjects.get(0),
                    subjects.get(1),
                    getQualifiersText(),
                    getSummarizersText(),
                    getValuesText()
            );
            case THIRD_FORM -> String.format("%s %s, które są/mają %s, w porównaniu do %s jest/ma %s [%s]",
                    getQuantifier().getName(),
                    subjects.get(0),
                    getQualifiersText(),
                    subjects.get(1),
                    getSummarizersText(),
                    getValuesText()
            );
            case FOURTH_FORM -> String.format("Więcej %s niż %s jest/ma %s [%s]",
                    subjects.get(0),
                    subjects.get(1),
                    getSummarizersText(),
                    getValuesText()
            );
        };
    }

    private String getSummarizersText() {
        return getSummarizers().stream()
                .map(s -> s.getLinguisticValue().getName())
                .collect(Collectors.joining(" i "));
    }

    private String getQualifiersText() {
        return getQualifiers().stream()
                .map(s -> s.getLinguisticValue().getName())
                .collect(Collectors.joining(" i "));
    }

    private String getValuesText() {
        return getLinguisticSummaryValues().stream()
                .map(map -> map.entrySet().stream()
                        .map(e -> String.format("%s: %.3f", e.getKey(), e.getValue()))
                        .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining(" | "));
    }
}
