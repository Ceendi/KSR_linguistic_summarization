package org.example.ksr_linguistic_summarization.logic.set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ContinuousSet implements ClassicSet {
    private double startOfUniverse;
    private double endOfUniverse;


    @Override
    public double getSize() {
        return endOfUniverse - startOfUniverse;
    }

    @Override
    public boolean isEmpty() {
        return startOfUniverse > endOfUniverse;
    }

    public List<ContinuousSet> complement(double universeStart, double universeEnd) {
        List<ContinuousSet> result = new ArrayList<>();

        if (universeStart > universeEnd) {
            return result;
        }

        if (universeStart < this.startOfUniverse) {
            result.add(new ContinuousSet(universeStart, this.startOfUniverse));
        }

        if (this.endOfUniverse < universeEnd) {
            result.add(new ContinuousSet(this.endOfUniverse, universeEnd));
        }

        return result;
    }
    public ContinuousSet intersection(ContinuousSet continuousSet) {
        return new ContinuousSet(
                Math.max(startOfUniverse, continuousSet.startOfUniverse),
                Math.min(endOfUniverse, continuousSet.endOfUniverse)
        );
    }

    public ContinuousSet union(ContinuousSet continuousSet) {
        return new ContinuousSet(
                Math.min(startOfUniverse, continuousSet.startOfUniverse),
                Math.max(endOfUniverse, continuousSet.endOfUniverse)
        );
    }
}
