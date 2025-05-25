package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.ksr_linguistic_summarization.logic.set.FuzzySet;

@Getter
@Setter
@AllArgsConstructor
public class LinguisticValue {
    private String name;
    private FuzzySet fuzzySet;
}
