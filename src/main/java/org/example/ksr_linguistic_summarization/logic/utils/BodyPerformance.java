package org.example.ksr_linguistic_summarization.logic.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BodyPerformance {
    private double age;
    private char gender;
    private double heightCm;
    private double weightKg;
    private double bodyFatPercentage;
    private double diastolic;
    private double systolic;
    private double gripForce;
    private double sitAndBendForwardCm;
    private double sitUpsCounts;
    private double broadJumpCm;

    public double getAttribute(String name) {
        return switch (name.toLowerCase()) {
            case "age" -> age;
            case "heightcm" -> heightCm;
            case "weightkg" -> weightKg;
            case "bodyfatpercentage" -> bodyFatPercentage;
            case "diastolic" -> diastolic;
            case "systolic" -> systolic;
            case "gripforce" -> gripForce;
            case "sitandbendforwardcm" -> sitAndBendForwardCm;
            case "situpscounts" -> sitUpsCounts;
            case "broadjumpcm" -> broadJumpCm;
            default -> throw new IllegalArgumentException("Unknown or unsupported numeric attribute: " + name);
        };
    }
}