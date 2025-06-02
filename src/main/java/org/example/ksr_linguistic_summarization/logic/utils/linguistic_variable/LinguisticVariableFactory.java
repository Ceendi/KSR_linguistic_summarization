package org.example.ksr_linguistic_summarization.logic.utils.linguistic_variable;

import org.example.ksr_linguistic_summarization.logic.functions.Gaussian;
import org.example.ksr_linguistic_summarization.logic.functions.MembershipFunction;
import org.example.ksr_linguistic_summarization.logic.functions.Trapezoidal;
import org.example.ksr_linguistic_summarization.logic.functions.Triangular;
import org.example.ksr_linguistic_summarization.logic.set.*;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticValue;
import org.example.ksr_linguistic_summarization.logic.summarization.LinguisticVariable;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;
import org.example.ksr_linguistic_summarization.logic.summarization.Quantifier;
import org.example.ksr_linguistic_summarization.logic.summarization.QuantifierType;

import java.util.*;
import java.util.stream.Collectors;

public class LinguisticVariableFactory {
    public static List<LinguisticVariable> fromDTO(List<LinguisticVariableDTO> dtos, List<BodyPerformance> data) {
        List<LinguisticVariable> result = new ArrayList<>();
        for (LinguisticVariableDTO dto : dtos) {
            if (dto.labels != null && !dto.labels.isEmpty()) {
                for (LabelDTO label : dto.labels) {
                    FuzzySet fuzzySet = createFuzzySet(label.fuzzySet, getSampleValues(dto.name, data));
                    result.add(new LinguisticVariable(dto.name, new LinguisticValue(label.name, fuzzySet)));
                }
            } else if (dto.fuzzySet != null) {
                FuzzySet fuzzySet = createFuzzySet(dto.fuzzySet, new ArrayList<>());
                if (Objects.equals(dto.quantifierType, "ABSOLUTE")) {
                    fuzzySet.setUniverseOfDiscourse(new ContinuousSet(0, data.size()));
                } else {
                    fuzzySet.setUniverseOfDiscourse(new ContinuousSet(0, 1));
                }

                if (dto.quantifierType != null) {
                    QuantifierType type = QuantifierType.valueOf(dto.quantifierType.toUpperCase());
                    result.add(new Quantifier(dto.name, new LinguisticValue(dto.name, fuzzySet), type));
                } else {
                    result.add(new LinguisticVariable(dto.name, new LinguisticValue(dto.name, fuzzySet)));
                }
            }
        }
        return result;
    }

    public static FuzzySet createFuzzySet(FuzzySetDTO fuzzySetDTO, List<Double> valuesFromDb) {
        DiscreteSet universe = new DiscreteSet(valuesFromDb);

        MembershipFunction mf;
        switch (fuzzySetDTO.type.toLowerCase()) {
            case "trapezoidal" ->
                    mf = new Trapezoidal(
                            fuzzySetDTO.params[0], fuzzySetDTO.params[1], fuzzySetDTO.params[2], fuzzySetDTO.params[3]);
            case "gaussian" ->
                    mf = new Gaussian(
                            fuzzySetDTO.params[0], fuzzySetDTO.params[1]);
            case "triangular" ->
                    mf = new Triangular(
                            fuzzySetDTO.params[0], fuzzySetDTO.params[1], fuzzySetDTO.params[2]);
            default -> throw new IllegalArgumentException("Nieznany typ funkcji przynależności: " + fuzzySetDTO.type);
        }

        return new FuzzySet(universe, mf);
    }


    private static List<Double> getSampleValues(String attributeName, List<BodyPerformance> data) {
        return data.stream()
                .map(bp -> bp.getAttribute(attributeName))
                .collect(Collectors.toList());
    }

}