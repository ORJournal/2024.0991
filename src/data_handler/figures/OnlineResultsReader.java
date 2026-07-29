package data_handler.figures;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Strict reader for the 14 populated online-result columns in README.md. */
public final class OnlineResultsReader {
    public static final int COLUMN_COUNT = 14;

    public List<OnlineResult> read(Path input) throws IOException {
        List<OnlineResult> results = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(input,
                StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) results.add(parse(line, input, lineNumber));
            }
        }
        return results;
    }

    private OnlineResult parse(String line, Path input, int lineNumber) {
        String[] raw = line.split(",", -1);
        int length = raw.length;
        while (length > 0 && raw[length - 1].trim().isEmpty()) length--;
        String[] values = Arrays.copyOf(raw, length);
        if (values.length != COLUMN_COUNT) {
            throw invalid(input, lineNumber, "expected " + COLUMN_COUNT
                    + " columns but found " + values.length, null);
        }
        for (int i = 0; i < values.length; i++) values[i] = values[i].trim();
        try {
            return new OnlineResult(values[0], parseInt(values[1], "total periods"),
                    parseDouble(values[2], "mu"),
                    parseDouble(values[3], "lambda"),
                    parseInt(values[4], "bandwidth"),
                    parseInt(values[5], "minimum consecutive ones"),
                    parseInt(values[6], "horizon"),
                    parseInt(values[7], "seed"),
                    parseInt(values[8], "method"),
                    parseDouble(values[9], "DD nodes"),
                    parseDouble(values[10], "DD arcs"),
                    parseDouble(values[11], "DD construction time"),
                    parseDouble(values[12], "total shortest-path time"),
                    parseDouble(values[13], "solver time"));
        } catch (IllegalArgumentException exception) {
            throw invalid(input, lineNumber, exception.getMessage(), exception);
        }
    }

    private static int parseInt(String text, String name) {
        double value = parseDouble(text, name);
        if (!Double.isFinite(value) || value != Math.rint(value)) {
            throw new IllegalArgumentException(name + " is not an integer: " + text);
        }
        return (int) value;
    }

    private static double parseDouble(String text, String name) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " is not numeric: " + text,
                    exception);
        }
    }

    private static IllegalArgumentException invalid(Path input, int lineNumber,
            String message, Throwable cause) {
        return new IllegalArgumentException(input + ":" + lineNumber + ": "
                + message, cause);
    }
}