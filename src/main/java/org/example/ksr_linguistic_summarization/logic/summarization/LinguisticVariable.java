package org.example.ksr_linguistic_summarization.logic.summarization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.ksr_linguistic_summarization.logic.set.FuzzySet;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LinguisticVariable {
    private String name;
    private List<LinguisticValue> linguisticValues;
}
