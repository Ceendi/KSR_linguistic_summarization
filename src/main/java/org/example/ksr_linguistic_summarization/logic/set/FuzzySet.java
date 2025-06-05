package org.example.ksr_linguistic_summarization.logic.set;

import lombok.*;
import org.example.ksr_linguistic_summarization.logic.functions.MembershipFunction;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FuzzySet {
    private ClassicSet universeOfDiscourse;
    private final MembershipFunction membershipFunction;

    public double getCardinality() {
        if (universeOfDiscourse instanceof DiscreteSet discreteSet) {
            return discreteSet.getValues()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .map(membershipFunction::getMembership)
                    .sum();
        } else if (universeOfDiscourse instanceof ContinuousSet continuousSet) {
            return getArea();
        } else {
            throw new UnsupportedOperationException("Unknown type of universeOfDiscourse");
        }
    }

    public double getCardinality(List<Double> subset) {
        return subset.stream()
                .mapToDouble(membershipFunction::getMembership)
                .sum();
    }

    private double getArea() {
        double area = 0.0;
        if (universeOfDiscourse instanceof ContinuousSet continuousSet) {
            ContinuousSet support = (ContinuousSet) this.getSupport();
            var start =support.getStartOfUniverse();
            var end = support.getEndOfUniverse();
            var length = end - start;
            var step = length / 1000;
            for (double i = start; i <= end; i += step) {
                area += membershipFunction.getMembership(i) * step;
            }
        }
        return area;
    }

    public double getHeight() {
        if (universeOfDiscourse instanceof DiscreteSet discreteSet) {
            return discreteSet.getValues()
                    .stream()
                    .map(membershipFunction::getMembership)
                    .max(Double::compare)
                    .orElse(Double.NEGATIVE_INFINITY);
        } else {
            throw new UnsupportedOperationException("Height calculation is only supported for DiscreteSet");
        }
    }

    public ClassicSet getAlphaCut(double alpha) {
        if (universeOfDiscourse instanceof DiscreteSet discreteSet) {
            List<Double> values = discreteSet.getValues()
                    .stream()
                    .filter(x -> membershipFunction.getMembership(x) >= alpha)
                    .toList();
            return new DiscreteSet(values);
        } else {
            throw new UnsupportedOperationException("Get Alpha cat is only supported for DiscreteSet");
        }
    }

    public ClassicSet getSupport() {
        if (universeOfDiscourse instanceof DiscreteSet discreteSet) {
            List<Double> support = new java.util.ArrayList<>(List.of());
            List<Double> values = discreteSet.getValues();
            for (Double value : values) {
                if (membershipFunction.getMembership(value) > 0) {
                    support.add(value);
                }
            }
            return new DiscreteSet(support);
        } else if (universeOfDiscourse instanceof ContinuousSet continuousSet) {
            double start = continuousSet.getStartOfUniverse();
            double end = continuousSet.getEndOfUniverse();
            double step = 0.01;

            Double supportStart = null;
            Double supportEnd = null;

            for (double x = start; x <= end; x += step) {
                if (membershipFunction.getMembership(x) > 0) {
                    if (supportStart == null) {
                        supportStart = x;
                    }
                    supportEnd = x;
                }
            }

            if (supportStart != null) {
                return new ContinuousSet(supportStart, supportEnd);
            } else {
                return new ContinuousSet(0, 0);
            }
        }
        else {
            throw new UnsupportedOperationException("Unknown type universeOfDiscourse");
        }
    }

    boolean isNormal() {
        return getHeight() == 1;
    }
    
    public double getDegreeOfFuzziness() {
        return getSupport().getSize() / getUniverseOfDiscourse().getSize();
    }

    public double getMembershipDegree(double x) {
        return membershipFunction.getMembership(x);
    }

    public FuzzySet power(double r) {
        MembershipFunction poweredMembershipFunction = x -> {
            double mu = FuzzySet.this.membershipFunction.getMembership(x);
            return Math.pow(mu, r);
        };

        return new FuzzySet(this.universeOfDiscourse, poweredMembershipFunction);
    }

    public FuzzySet normalize() {
        double height = getHeight();
        if (height == 0) {
            throw new IllegalStateException("Cannot normalize fuzzy set with height 0");
        }

        MembershipFunction normalizedMembershipFunction = x -> {
            double mu = FuzzySet.this.membershipFunction.getMembership(x);
            return mu / height;
        };

        return new FuzzySet(this.universeOfDiscourse, normalizedMembershipFunction);
    }

    public boolean isConvex() {
        if (!(universeOfDiscourse instanceof DiscreteSet discreteSet)) {
            throw new UnsupportedOperationException("Convexity check only supported for DiscreteSet");
        }

        List<Double> values = discreteSet.getValues();
        int n = values.size();

        double[] lambdas = {0.0, 0.2, 0.4, 0.6, 0.8, 1.0}; // mozna wiecej wartosci, ale wiecej obliczen potrzeba

        for (int i = 0; i < n; i++) {
            double x1 = values.get(i);
            double mu1 = membershipFunction.getMembership(x1);

            for (int j = i + 1; j < n; j++) {
                double x2 = values.get(j);
                double mu2 = membershipFunction.getMembership(x2);

                for (double lambda : lambdas) {
                    double xMid = lambda * x1 + (1 - lambda) * x2;
                    double muMid = membershipFunction.getMembership(xMid);

                    double minMu = Math.min(mu1, mu2);
                    if (muMid < minMu - 1e-9) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public FuzzySet union(FuzzySet fuzzySet) {
        MembershipFunction unionMembershipFunction = x -> {
            double mu1 = FuzzySet.this.membershipFunction.getMembership(x);
            double mu2 = fuzzySet.membershipFunction.getMembership(x);
            return Math.max(mu1, mu2);
        };

        return new FuzzySet(this.universeOfDiscourse, unionMembershipFunction);
    }

    public FuzzySet complement() {
        MembershipFunction complementMembershipFunction = x -> {
            double mu = FuzzySet.this.membershipFunction.getMembership(x);
            return 1.0 - mu;
        };

        return new FuzzySet(this.universeOfDiscourse, complementMembershipFunction);
    }

    public FuzzySet intersection(FuzzySet fuzzySet) {
        MembershipFunction unionMembershipFunction = x -> {
            double mu1 = this.membershipFunction.getMembership(x);
            double mu2 = fuzzySet.membershipFunction.getMembership(x);
            return Math.min(mu1, mu2);
        };

        return new FuzzySet(this.universeOfDiscourse, unionMembershipFunction);
    }
}
