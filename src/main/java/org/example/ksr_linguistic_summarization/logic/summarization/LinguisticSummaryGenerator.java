package org.example.ksr_linguistic_summarization.logic.summarization;


import org.example.ksr_linguistic_summarization.logic.degrees.Degree;

import org.example.ksr_linguistic_summarization.logic.degrees.DegreeOfTruth;
import org.example.ksr_linguistic_summarization.logic.utils.BodyPerformance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LinguisticSummaryGenerator {
    public static List<LinguisticSummary> generateSummaries(List<Quantifier> quantifiers, List<Summarizer> summarizers, List<BodyPerformance> records, List<Degree> degrees) {
        List<LinguisticSummary> summaries = new ArrayList<>();
        DegreeOfTruth degreeOfTruth = new DegreeOfTruth();

        List<List<Summarizer>> firstDegreeSummarizers = generateCombination(summarizers);
        List<List<List<Summarizer>>> secondDegreePairs = generateDisjointPairs(summarizers);

        List<MultiSubjectLinguisticSummary> multiSubjectSummaries = new ArrayList<>();
        List<List<Character>> genderLists = List.of(List.of('F', 'M'), List.of('M', 'F'));
        List<MultiSubjectType> forms = List.of(MultiSubjectType.SECOND_FORM, MultiSubjectType.THIRD_FORM);


        //PIERWSZY STOPIEŃ
        for (Quantifier quantifier : quantifiers) {
            //PIERWSZY STOPIEŃ
            for (List<Summarizer> summarizerCombination : firstDegreeSummarizers) {
                LinguisticSummary summary = new LinguisticSummary(
                        quantifier,
                        Collections.emptyList(),
                        summarizerCombination,
                        records,
                        SummaryType.ONESUBJECT,
                        degrees
                );
                summaries.add(summary);
                //WIELOPODMIOTOWE PIERWSZA I CZWARTA FORMA
                for (List<Character> genderList : genderLists) {
                    MultiSubjectLinguisticSummary multiSummary = new MultiSubjectLinguisticSummary(
                            quantifier,
                            Collections.emptyList(),
                            summarizerCombination,
                            records,
                            SummaryType.MULTIPLESUBJECT,
                            List.of(degreeOfTruth),
                            genderList,
                            MultiSubjectType.FIRST_FORM
                    );
                    multiSubjectSummaries.add(multiSummary);
                    multiSummary = new MultiSubjectLinguisticSummary(
                            null,
                            Collections.emptyList(),
                            summarizerCombination,
                            records,
                            SummaryType.MULTIPLESUBJECT,
                            List.of(degreeOfTruth),
                            genderList,
                            MultiSubjectType.FOURTH_FORM
                    );
                    multiSubjectSummaries.add(multiSummary);
                }

            }
            //DRUGI STOPIEŃ
            if (quantifier.getQuantifierType() == QuantifierType.RELATIVE) {
                for (List<List<Summarizer>> pair : secondDegreePairs) {
                    List<Qualifier> qualCombinationD2 = pair.get(0).stream()
                            .map(summ -> new Qualifier(summ.getName(), summ.getLinguisticValue()))
                            .collect(Collectors.toList());
                    List<Summarizer> summCombinationD2 = pair.get(1);
                    LinguisticSummary summary = new LinguisticSummary(
                            quantifier,
                            qualCombinationD2,
                            summCombinationD2,
                            records,
                            SummaryType.ONESUBJECT,
                            degrees
                    );
                    summaries.add(summary);
                    //WIELOPODMIOTOWE DRUGA I TRZECIA FORMA
                    for (List<Character> genderList : genderLists) {
                        for (MultiSubjectType form : forms) {
                            MultiSubjectLinguisticSummary multiSummary = new MultiSubjectLinguisticSummary(
                                    quantifier,
                                    qualCombinationD2,
                                    summCombinationD2,
                                    records,
                                    SummaryType.ONESUBJECT,
                                    List.of(degreeOfTruth),
                                    genderList,
                                    form
                            );
                            multiSubjectSummaries.add(multiSummary);
                        }
                    }

                }
            }
        }



        // wielopodmiotowe :DDDD
//        for (Quantifier quantifier : quantifiers) {
//            for (List<String> genderList : genderLists) {
//                //pierwsza forma
//                for (List<Summarizer> summarizerCombination : firstDegreeSummarizers) {
//                    MultiSubjectLinguisticSummary summary = new MultiSubjectLinguisticSummary(
//                            quantifier,
//                            Collections.emptyList(),
//                            summarizerCombination,
//                            records,
//                            SummaryType.MULTIPLESUBJECT,
//                            degrees,
//                            genderList,
//                            MultiSubjectType.FIRST_FORM
//                    );
//                    multiSubjectSummaries.add(summary);
//                }
//                //druga forma
//
//
//            }
//
//
//            //first form
//
//            //second form
//
//            //third form
//
//        }

        //fourth form


        summaries.addAll(multiSubjectSummaries);
        return summaries;
    }

    public static <T> List<List<T>> generateCombination(List<T> list) {
        int n = list.size();
        List<List<T>> combination = new ArrayList<>();

        int maxMask = (1 << n);
        for (int mask = 1; mask < maxMask; mask++) {
            List<T> subset = new ArrayList<>();
            for (int bit = 0; bit < n; bit++) {
                if ((mask & (1 << bit)) != 0) {
                    subset.add(list.get(bit));
                }
            }
            combination.add(subset);
        }
        return combination;
    }

    public static <T> List<List<List<T>>> generateDisjointPairs(List<T> list) {
        List<List<T>> allSubsets = generateCombination(list);
        List<List<List<T>>> result = new ArrayList<>();
        for (List<T> s1 : allSubsets) {
            for (List<T> s2 : allSubsets) {
                if (areDisjoint(s1, s2)) {
                    List<List<T>> pair = new ArrayList<>(2);
                    pair.add(s1);
                    pair.add(s2);
                    result.add(pair);
                }
            }
        }
        return result;
    }

    private static <T> boolean areDisjoint(List<T> a, List<T> b) {
        for (T elem : a) {
            if (b.contains(elem)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        List<String> lista = Arrays.asList("A", "B", "C", "D");
        List<List<String>> wszystkie = generateCombination(lista);
        List<List<List<String>>> pairs = generateDisjointPairs(lista);

        System.out.println("jedne");
        for (List<String> komb : wszystkie) {
            System.out.println(komb);
        }
        System.out.println("dwie");
        for (List<List<String>> komb : pairs) {
            System.out.println(komb);
        }
    }
}
