package com.freelancing.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic CSV import/export utility for SkillBridge data (projects, transactions, reports, audit logs).
 */
public class CsvUtil {

    private CsvUtil() {}

    /**
     * Export rows to a CSV file.
     */
    public static void exportCsv(String[] headers, List<String[]> rows, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            if (headers != null && headers.length > 0) {
                writer.println(String.join(",", escapeRow(headers)));
            }
            if (rows != null) {
                for (String[] row : rows) {
                    writer.println(String.join(",", escapeRow(row)));
                }
            }
        }
    }

    /**
     * Import rows from a CSV file.
     */
    public static List<String[]> importCsv(File file, boolean skipHeader) throws IOException {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first && skipHeader) {
                    first = false;
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) continue;
                records.add(parseCsvLine(line));
            }
        }
        return records;
    }

    private static String[] escapeRow(String[] row) {
        String[] escaped = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            escaped[i] = escapeCsv(row[i]);
        }
        return escaped;
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }
}
