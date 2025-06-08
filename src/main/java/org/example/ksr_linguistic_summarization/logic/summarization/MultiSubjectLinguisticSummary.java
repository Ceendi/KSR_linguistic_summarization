package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.Getter;
import org.example.ksr_linguistic_summarization.logic.degrees.Degree;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.List;
import java.util.stream.Collectors;

public class MultiSubjectLinguisticSummary extends LinguisticSummary {

    @Getter
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
        return getLinguisticSummaryValues()
                .entrySet()
                .stream()
                .map(e -> String.format("%s: %.3f", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
    }
}
