package org.example.ksr_linguistic_summarization.logic.set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
public class DiscreteSet<T> implements ClassicSet {
    List<T> values;

    @Override
    public double getSize() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public DiscreteSet<T> complement(List<T> universe) {
        List<T> result = universe.stream()
                .filter(e -> !values.contains(e))
                .distinct()
                .toList();
        return new DiscreteSet<>(result);
    }

    public DiscreteSet<T> intersection(DiscreteSet<T> discreteSet) {
        List<T> result = values.stream()
                .filter(discreteSet.getValues()::contains)
                .distinct()
                .toList();
        return new DiscreteSet<>(result);
    }

    public DiscreteSet<T> union(DiscreteSet<T> discreteSet) {
        List<T> result = Stream.concat(values.stream(), discreteSet.getValues().stream())
                .distinct()
                .toList();
        return new DiscreteSet<>(result);
    }
}
