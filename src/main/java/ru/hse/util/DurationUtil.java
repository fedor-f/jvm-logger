package ru.hse.util;

public class DurationUtil {
    public static long parseDuration(String duration) {
        long totalSeconds = 0;
        StringBuilder number = new StringBuilder();

        for (char ch : duration.toCharArray()) {
            if (Character.isDigit(ch)) {
                number.append(ch);
            } else {
                long value = Long.parseLong(number.toString());
                number.setLength(0);

                switch (ch) {
                    case 'd':
                        totalSeconds += value * 24 * 3600;
                        break;
                    case 'h':
                        totalSeconds += value * 3600;
                        break;
                    case 'm':
                        totalSeconds += value * 60;
                        break;
                    case 's':
                        totalSeconds += value;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown time unit: " + ch);
                }
            }
        }

        return totalSeconds;
    }
}
