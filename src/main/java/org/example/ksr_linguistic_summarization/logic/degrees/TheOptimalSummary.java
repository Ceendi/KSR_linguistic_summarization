package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public class TheOptimalSummary implements Degree {

    private LinguisticSummary summary;
    private final DegreeOfTruth degreeOfTruth = new DegreeOfTruth();
    private final DegreeOfImprecision degreeOfImprecision = new DegreeOfImprecision();
    private final DegreeOfCovering degreeOfCovering = new DegreeOfCovering();
    private final DegreeOfAppropriateness degreeOfAppropriateness = new DegreeOfAppropriateness();
    private final LengthOfASummary lengthOfASummary = new LengthOfASummary();

    @Override
    public double calculateDegree(LinguisticSummary summary) {
        return 0;
    }
}
