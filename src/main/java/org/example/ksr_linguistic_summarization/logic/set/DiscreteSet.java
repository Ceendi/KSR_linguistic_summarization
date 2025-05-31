package org.example.ksr_linguistic_summarization.logic.set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
public class DiscreteSet implements ClassicSet {
    List<Double> values;

    @Override
    public double getSize() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public DiscreteSet complement(List<Double> universe) {
        List<Double> result = universe.stream()
                .filter(e -> !values.contains(e))
                .distinct()
                .toList();
        return new DiscreteSet(result);
    }

    public DiscreteSet intersection(DiscreteSet discreteSet) {
        List<Double> result = values.stream()
                .filter(discreteSet.getValues()::contains)
                .distinct()
                .toList();
        return new DiscreteSet(result);
    }

    public DiscreteSet union(DiscreteSet discreteSet) {
        List<Double> result = Stream.concat(values.stream(), discreteSet.getValues().stream())
                .distinct()
                .toList();
        return new DiscreteSet(result);
    }
}
