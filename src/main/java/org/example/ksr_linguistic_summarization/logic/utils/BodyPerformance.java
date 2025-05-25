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
    private Long age;
    private char gender;
    private double heightCm;
    private double weightKg;
    private double bodyFatPercentage;
    private Long diastolic;
    private Long systolic;
    private double gripForce;
    private double sitAndBendForwardCm;
    private Long sitUpsCounts;
    private Long broadJumpCm;
}