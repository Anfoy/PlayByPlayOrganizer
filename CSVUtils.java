package me.antcode.datacollection;

import org.apache.commons.csv.CSVRecord;

public class CSVUtils {


    public static  int getGameID(CSVRecord record){
        return getInt(record, "game_id");
    }

    public static String getDate(CSVRecord record){
        return record.get("date");
    }

    public static String getAwayPlayerOne(CSVRecord record){
        return record.get("a1");
    }

    public static String getAwayPlayerTwo(CSVRecord record){
        return record.get("a2");
    }

    public static String getAwayPlayerThree(CSVRecord record){
        return record.get("a3");
    }

    public static String getAwayPlayerFour(CSVRecord record){
        return record.get("a4");
    }

    public static String getAwayPlayerFive(CSVRecord record){
        return record.get("a5");
    }

    public static String getHomePlayerOne(CSVRecord record){
        return record.get("h1");
    }

    public static String getHomePlayerTwo(CSVRecord record){
        return record.get("h2");
    }

    public static String getHomePlayerThree(CSVRecord record){
        return record.get("h3");
    }

    public static String getHomePlayerFour(CSVRecord record){
        return record.get("h4");
    }

    public static String getHomePlayerFive(CSVRecord record){
        return record.get("h5");
    }

    public static int getQuarter(CSVRecord record){
        return getInt(record, "period");
    }

    public static int getAwayScore(CSVRecord record){
        return getInt(record, "away_score");
    }

    public static int getHomeScore(CSVRecord record){
        return getInt(record, "home_score");
    }

    public static double getRemainingTimeInSeconds(CSVRecord record){
        return convertToSeconds(record.get("remaining_time"));
    }

    public static double getElapsedTimeInSeconds(CSVRecord record){
        return convertToSeconds(record.get("elapsed"));
    }

    public static double getPlayLength(CSVRecord record){
        return convertToSeconds(record.get("play_length"));
    }

    public static int getGamePlayNumber(CSVRecord record){
        return getInt(record, "play_id");
    }

    public static String getPossessionTeam(CSVRecord record){
        return record.get("team");
    }

    public static String getEventColumn(CSVRecord record){
        return record.get("event_type");
    }

    public static String getAssistPlayerColumn(CSVRecord record){
        return record.get("assist");
    }

    public static String getAwayPlayerColumn(CSVRecord record){
        return record.get("away");
    }

    public static String getHomePlayerColumn(CSVRecord record){
        return record.get("home");
    }

    public static String getBlockPlayerColumn(CSVRecord record){
        return record.get("block");
    }

    public static String getEnteredPlayerColumn(CSVRecord record){
        return record.get("entered");
    }
    public static String getLeftPlayerColumn(CSVRecord record){
        return record.get("left");
    }

    public static int getFTNumberColumn(CSVRecord record){
        return getInt(record, "num");
    }

    public static String getOpponentPlayerColumn(CSVRecord record){
        return record.get("opponent");
    }

    public static int getFTOutOfColumn(CSVRecord record){
        return getInt(record, "outof");
    }

    public static String getMultiUsePlayerColumn(CSVRecord record){
        return record.get("player");
    }

    public static String getPossessionPlayerColumn(CSVRecord record){
        return record.get("possession");
    }
    public static String getReasonColumn(CSVRecord record){
        return record.get("reason");
    }

    public static String getResultColumn(CSVRecord record){
        return record.get("result");
    }

    public static String getStealPlayerColumn(CSVRecord record){
        return record.get("steal");
    }

    public static String getTypeColumn(CSVRecord record){
        return record.get("type");
    }

    public static int getDistanceColumn(CSVRecord record){
        return getInt(record, "shot_distance");
    }

    public static String getDescription(CSVRecord record){
        return record.get("description");
    }

    public static boolean getIsThreePointer(CSVRecord record){
        return record.get("3pt").equals("true");
    }

    public static boolean isDefensive(CSVRecord record){
       return record.get("defensive").equals("true");
    }
    public static boolean isOffensive(CSVRecord record){
      return record.get("offensive").equals("true");
    }

    public static boolean isFlagrant(CSVRecord record){
        return record.get("flagrant").equals("true");
    }


    /**
     * Parses a string value to an integer value.
     * @param value The string to parse.
     * @return The integer value, or 0 if parsing fails.
     */
    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
    }
    /**
     * Retrieves an integer value from the record.
     * @param record The CSV record containing play-by-play data.
     * @param value The column name.
     * @return The integer value.
     */
    private static int getInt(CSVRecord record, String value){
        return parseInt(record.get(value));
    }

    /**
     * Converts a time string to seconds.
     * @param time The time string in the format "HH:MM:SS", "MM:SS" or "SS".
     * @return The time in seconds.
     */
    private static double convertToSeconds(String time) {
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
}
