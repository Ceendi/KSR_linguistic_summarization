package org.example.ksr_linguistic_summarization.logic.degrees;

import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticSummary;

public class TheOptimalSummary implements Degree {

    private LinguisticSummary summary;
    private DegreeOfTruth degreeOfTruth;
    private DegreeOfImprecision degreeOfImprecision;
    private DegreeOfCovering degreeOfCovering;
    private DegreeOfAppropriateness degreeOfAppropriateness;
    private LengthOfASummary lengthOfASummary;

    public TheOptimalSummary(DegreeOfTruth degreeOfTruth, DegreeOfImprecision degreeOfImprecision,
                             DegreeOfCovering degreeOfCovering, DegreeOfAppropriateness degreeOfAppropriateness,
                             LengthOfASummary lengthOfASummary) {
    }

    @Override
    public double calculateDegree(LinguisticSummary summary) {
        return 0;
    }
}
