package org.example.ksr_linguistic_summarization.logic.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataInitializer {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/ksr2";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static List<BodyPerformance> loadBodyPerformanceData() throws SQLException {
        List<BodyPerformance> bodyPerformanceList = new ArrayList<>();
        
        String query = "SELECT age, gender, height_cm, weight_kg, \"body fat_%\", " +
                       "diastolic, systolic, \"gripForce\", \"sit and bend forward_cm\", " +
                       "\"sit-ups counts\", \"broad jump_cm\" FROM public.body_performance";
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            extractData(bodyPerformanceList, resultSet);
        }
        
        return bodyPerformanceList;
    }

    private static void extractData(List<BodyPerformance> bodyPerformanceList, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Long age = resultSet.getLong("age");
            char gender = resultSet.getString("gender").charAt(0);
            double heightCm = resultSet.getDouble("height_cm");
            double weightKg = resultSet.getDouble("weight_kg");
            double bodyFatPercentage = resultSet.getDouble("body fat_%");
            Long diastolic = resultSet.getLong("diastolic");
            Long systolic = resultSet.getLong("systolic");
            double gripForce = resultSet.getDouble("gripForce");
            double sitAndBendForwardCm = resultSet.getDouble("sit and bend forward_cm");
            Long sitUpsCounts = resultSet.getLong("sit-ups counts");
            Long broadJumpCm = resultSet.getLong("broad jump_cm");

            BodyPerformance bodyPerformance = new BodyPerformance(
                age, gender, heightCm, weightKg, bodyFatPercentage,
                diastolic, systolic, gripForce, sitAndBendForwardCm,
                sitUpsCounts, broadJumpCm
            );

            bodyPerformanceList.add(bodyPerformance);
        }
    }

    /**
     * Pobiera wybrane wiersze z tabeli body_performance w oparciu o określone kryteria.
     * 
     * @param limit maksymalna liczba wierszy do pobrania
     * @param gender płeć (opcjonalnie - może być null)
     * @return Lista obiektów BodyPerformance
     * @throws SQLException w przypadku błędu podczas komunikacji z bazą danych
     */
    public static List<BodyPerformance> loadBodyPerformanceData(int limit, Character gender) throws SQLException {
        List<BodyPerformance> bodyPerformanceList = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT age, gender, height_cm, weight_kg, \"body fat_%\", ")
                    .append("diastolic, systolic, \"gripForce\", \"sit and bend forward_cm\", ")
                    .append("\"sit-ups counts\", \"broad jump_cm\" FROM public.body_performance");
        
        if (gender != null) {
            queryBuilder.append(" WHERE gender = ?");
        }
        
        queryBuilder.append(" LIMIT ?");
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            if (gender != null) {
                statement.setString(paramIndex++, String.valueOf(gender));
            }
            statement.setInt(paramIndex, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                extractData(bodyPerformanceList, resultSet);
            }
        }
        
        return bodyPerformanceList;
    }
}