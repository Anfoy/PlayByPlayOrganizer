package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.antcode.datacollection.CSVDataGather.nbaTeams;

public class Manager {




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
     * @param time The time string in the format "HH:MM:SS", "MM:SS" or "SS".
     * @return The time in seconds.
     */
    public double convertToSeconds(String time) {
        String[] parts = time.split(":");
        double hours = 0;
        double minutes = 0;
        double seconds;

        if (parts.length == 3) {
            hours = Double.parseDouble(parts[0]);
            minutes = Double.parseDouble(parts[1]);
            seconds = Double.parseDouble(parts[2]);
        } else if (parts.length == 2) {
            minutes = Double.parseDouble(parts[0]);
            seconds = Double.parseDouble(parts[1]);
        } else if (parts.length == 1) {
            seconds = Double.parseDouble(parts[0]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        return hours * 3600 + minutes * 60 + seconds;
    }

    public Matchup findCorrelatingMatchupWithID(String date,String name, List<Matchup> matchups, int id){
        for (Matchup matchup : matchups){
            if (matchup.getGameID() == 0){
                if (matchup.getDate().equals(date)) {
                    for (Player player : matchup.getTotalPlayers()){
                        if (player.getName().equals(name)){
                            matchup.setGameID(id);
                            return matchup;
                        }
                    }
                }
            }else{
                if (matchup.getGameID() == id){
                    return matchup;
                }
            }
        }
        return null;
    }

    public boolean isMatchup(Matchup matchup, String date, String name, int id) {
        if (matchup.getGameID() == 0){
            if (matchup.getDate().equals(date)) {
                for (Player player : matchup.getTotalPlayers()){
                    if (player.getName().equals(name)){
                        matchup.setGameID(id);
                        return true;
                    }
                }
            }
        }else{
            return matchup.getGameID() == id;
        }
        return false;
    }

    public  String convertDateFormat(String dateStr) {
        // Define the date format for MM/DD/YYYY
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        // Define the desired date format for YYYY-MM-DD
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        try {
            // Parse the input date string into a Date object
            date = inputFormat.parse(dateStr);
            // Convert it to the new format
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date format";
        }
    }



}
