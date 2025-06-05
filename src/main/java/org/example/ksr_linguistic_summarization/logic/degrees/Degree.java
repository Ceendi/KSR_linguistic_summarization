package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public interface Degree {
    String name = "";
    double calculateDegree(LinguisticSummary summary);
    String getName();
}
