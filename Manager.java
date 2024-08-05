package me.antcode.managers;

import me.antcode.Player;
import me.antcode.plays.Play;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager {


    /**
     * Adds the play duration to the minutes played by each player on the court.
     * @param homeCourt The home team players on the court.
     * @param awayCourt The away team players on the court.
     * @param play The play object containing the play duration.
     */
    public void addMinutesToPlayers(List<Player> homeCourt, List<Player> awayCourt, Play play){
        homeCourt.forEach(player -> player.addMinutes(play.getPlayDuration()));
        awayCourt.forEach(player -> player.addMinutes(play.getPlayDuration()));
    }

    public   int[] extractNumbers(String input) {
        // Define the regex pattern to match numbers
        Pattern pattern = Pattern.compile("(\\d+) of (\\d+)");
        Matcher matcher = pattern.matcher(input);

        // Check if the pattern matches
        if (matcher.find()) {
            // Extract the numbers from the matched groups
            int firstNumber = Integer.parseInt(matcher.group(1));
            int secondNumber = Integer.parseInt(matcher.group(2));
            return new int[] { firstNumber, secondNumber };
        }

        // Return null if no match is found
        return null;
    }

    public String checkNull(Object value) {
        return value == null ? "NA" : value.toString();
    }


    public String convertSecondsToMinuteFormat(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) ((totalSeconds - (int) totalSeconds) * 1000);

        // Convert milliseconds to string and remove trailing zeros

        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds);
    }

    /**
     * Parses a string value to an integer value.
     * @param value The string to parse.
     * @return The integer value, or 0 if parsing fails.
     */
    public int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
    }

    /**
     * Retrieves the athlete ID from the record.
     * @param record The CSV record containing play-by-play data.
     * @param id The athlete number.
     * @return The athlete ID.
     */
    public int getAthleteID(CSVRecord record, int id){
        return rowHasValue(record, "athlete_id_" + id) ? getInt(record, "athlete_id_" + id) : 0;
    }


    /**
     * Retrieves an integer value from the record.
     * @param record The CSV record containing play-by-play data.
     * @param value The column name.
     * @return The integer value.
     */
    public int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Checks if a CSV record has a valid value for the given key.
     * @param record The CSV record to check.
     * @param key The column name to check.
     * @return True if the value is valid, false otherwise.
     */
    public boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }

    /**
     * Converts a time string to seconds.
     * @param time The time string in the format "MM:SS" or "SS".
     * @return The time in seconds.
     */
    public double convertToSeconds(String time) {
        String[] parts = time.split(":");
        double minutes = 0;
        double seconds;

        if (parts.length == 2) {
            minutes = Double.parseDouble(parts[0]);
            seconds = Double.parseDouble(parts[1]);
        } else if (parts.length == 1) {
            seconds = Double.parseDouble(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        return minutes * 60 + seconds;
    }



}
