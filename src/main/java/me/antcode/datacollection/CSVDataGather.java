package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public record CSVDataGather(String matchupsCSVPath, String playByPlayCSVPath) {

    public List<Matchup> extractAllMatchups() {
        List<Matchup> totalMatchups = new ArrayList<>();
        try (Reader reader = new FileReader(matchupsCSVPath);
             //Opens the parser and starts at second row since first row is the headers/column names
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            //Creates all the matchups
            for (CSVRecord csvRecord : csvParser) {
                Matchup matchup = createMatchupFromRecord(csvRecord);
                totalMatchups.add(matchup);
            }
        } catch (IOException e) {
      System.out.println("failed to read file.");
            e.printStackTrace();
        }
        return totalMatchups;
    }

    /**
     * Creates a matchup From a specific Row of the CSV
     * @param csvRecord which row to look at
     * @return A matchup compiled of all data gathered from the row.
     */
    private Matchup createMatchupFromRecord(CSVRecord csvRecord) {
        //Grab generic information for a matchup
        int gameId = parseInt(csvRecord.get("game_id"));
        String gameType = csvRecord.get("type_abbreviation");
        String gameDate = csvRecord.get("game_date");
        String homeTeam = csvRecord.get("home_display_name");
        String awayTeam = csvRecord.get("away_display_name");

        //Get the list of the home and away starters & bench
        List<Player> homeStarters = extractPlayers(csvRecord, "home_starter_", 5);
        List<Player> awayStarters = extractPlayers(csvRecord, "away_starter_", 5);
        List<Player> homeBench = extractPlayers(csvRecord, "home_bench_", 10);
        List<Player> awayBench = extractPlayers(csvRecord, "away_bench_", 10);

        return new Matchup(gameDate, homeTeam, awayTeam, gameType, gameId, homeStarters, homeBench, awayStarters, awayBench, new ArrayList<>());
    }

    /**
     * Creates the list of players based on the row provided
     * BENCH HAS UP TO 10 PLAYERS
     * STARTERS HAVE UP TO 5 PLAYERS
     * @param record Which row, or CSVRecord, to look at
     * @param prefix What column should it be looking for in that row. EX: "home_starter_"
     * @param count how many player columns should it look at
     * @return List of players grabbed from row.
     */
    private List<Player> extractPlayers(CSVRecord record, String prefix, int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String idKey = prefix + i + "_id";
            String nameKey = prefix + i;
            if (rowHasValue(record, idKey) && rowHasValue(record, nameKey)) {
                int playerId = parseInt(record.get(idKey));
                String playerName = record.get(nameKey);
                players.add(new Player(playerId, playerName));
            }
        }
        return players;
    }

    /**
     * Changes a String value to an integer value.
     * @param value String to get integer from.
     * @return value of String as integer; Otherwise returns 0;
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // or some default value or throw an exception
        }
    }

    /**
     * Checks to see if the spot in the CSV has a piece of information that can be grabbed
     * @param record Row/ CSVRecord to look at
     * @param key column to look at in that specified row/CSVRecord.
     * @return true if there is a value; false otherwise.
     */
    private boolean rowHasValue(CSVRecord record, String key) {
        return record.isMapped(key) && !record.get(key).equals("NA") && !record.get(key).isEmpty();
    }
}
