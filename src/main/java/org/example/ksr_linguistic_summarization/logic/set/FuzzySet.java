package org.example.ksr_linguistic_summarization.logic.set;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import lombok.*;
import org.example.ksr_linguistic_summarization.logic.functions.MembershipFunction;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FuzzySet {
    private final ClassicSet universeOfDiscourse;
    private final MembershipFunction membershipFunction;

    public double getCardinality() throws NotImplementedException {
        throw new NotImplementedException("Not yet implemented");
    }

    public double getHeight() throws NotImplementedException {
        throw new NotImplementedException("Not yet implemented");
    }

    public double getAlphaCut(double alpha) throws NotImplementedException {
        throw new NotImplementedException("Not yet implemented");
    }

    public ClassicSet getSupport() throws NotImplementedException {
        throw new NotImplementedException("Not yet implemented");
    }

    boolean isNormal() throws NotImplementedException {
        return getHeight() == 1;
    }
    
    public double getDegreeOfFuzziness() throws NotImplementedException {
        return getSupport().getSize() / getUniverseOfDiscourse().getSize();
    }

    public double getMembershipDegree(double x) {
        return membershipFunction.getMembership(x);
    }

    public FuzzySet power(double r) {
        return null;
    }

    public FuzzySet normalize() {
        return null;
    }

    boolean isConvex() {
        return true;
    }

    public FuzzySet union(FuzzySet fuzzySet) {
        return fuzzySet;
    }

    public FuzzySet complement() {
        return null;
    }

    public FuzzySet intersection(FuzzySet fuzzySet) {
        return fuzzySet;
    }
}
