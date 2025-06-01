package org.example.ksr_linguistic_summarization.logic.summarization;


import org.example.ksr_linguistic_summarization.logic.degrees.Degree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinguisticSummaryGenerator {
    public static List<LinguisticSummary> generateSummaries(List<Quantifier> quantifiers, List<Summarizer> summarizers, List<Degree> degrees) {
        List<LinguisticSummary> summaries = null;

        List<List<Summarizer>> summarizerCombinations = generateCombination(summarizers);


//        System.out.println(quantifierCombinations);


        // Q P are/have S



        // Q P are/have S and S and...
        // Q P being/having W are/have S
        // wielopodmiotowe :DDDD

        return null;
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
