package me.antcode.managers;

import me.antcode.Matchup;
import me.antcode.Player;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MatchupManager extends Manager{



    /**
     * Extracts all data for matchups and creates a corresponding object.
     * @return a List of all matchups from the file.
     */
    public List<Matchup> extractAllMatchups(String matchupsCSVPath) {
        List<Matchup> totalMatchups = new ArrayList<>();
        try (Reader reader = new FileReader(matchupsCSVPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            for (CSVRecord csvRecord : csvParser) {
                totalMatchups.add(createMatchupFromRecord(csvRecord));
            }
            for (Matchup matchup : totalMatchups) {
                matchup.setTotalPlayers(removeDuplicates(matchup.getTotalPlayers()));
            }
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            e.printStackTrace();
        }
        return totalMatchups;
    }

    private  List<Player> removeDuplicates(List<Player> players) {
        Set<Integer> seenIds = new HashSet<>();
        List<Player> uniquePlayers = new ArrayList<>();

        for (Player player : players) {
            if (!seenIds.contains(player.getId())) {
                seenIds.add(player.getId());
                uniquePlayers.add(player);
            }
        }

        return uniquePlayers;
    }
    /**
     * Creates a matchup from a specific row of the CSV.
     * @param csvRecord which row to look at
     * @return A matchup compiled of all data gathered from the row.
     */
    private Matchup createMatchupFromRecord(CSVRecord csvRecord) {
        Matchup matchup = new  Matchup(
                csvRecord.get("game_date"),
                csvRecord.get("home_display_name"),
                csvRecord.get("away_display_name"),
                csvRecord.get("type_abbreviation"),
                extractPlayersFromMatchupData(csvRecord, "home_starter_", 5),
                extractPlayersFromMatchupData(csvRecord, "home_bench_", 13),
                extractPlayersFromMatchupData(csvRecord, "away_starter_", 5),
                extractPlayersFromMatchupData(csvRecord, "away_bench_", 13),
                new ArrayList<>()
        );
        matchup.setGameID(parseInt(csvRecord.get("game_id")));
        return matchup;
    }

    /**
     * Creates the list of players based on the row provided.
     * BENCH HAS UP TO 10 PLAYERS
     * STARTERS HAVE UP TO 5 PLAYERS
     * @param record Which row, or CSVRecord, to look at
     * @param prefix What column should it be looking for in that row. EX: "home_starter_"
     * @param count how many player columns should it look at
     * @return List of players grabbed from row.
     */
    private List<Player> extractPlayersFromMatchupData(CSVRecord record, String prefix, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> createPlayerFromRecord(record, prefix, i))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Creates a player object from the given CSV record and prefix.
     * @param record The CSV record containing player data.
     * @param prefix The prefix for the player columns.
     * @param index The index of the player in the columns.
     * @return A Player object or null if the player data is not valid.
     */
    private Player createPlayerFromRecord(CSVRecord record, String prefix, int index) {
        String idKey = prefix + index + "_id";
        String nameKey = prefix + index;
        if (rowHasValue(record, idKey) && rowHasValue(record, nameKey)) {
            Player player = new Player(parseInt(record.get(idKey)), record.get(nameKey));
            if (player.getId() == 3906522){ //Henry Ellenson
                player.setExtraID(4417428);
            }else if (player.getId() == 3191){ //Corey Brewer
                player.setExtraID(4415554);
            }else if (player.getId() == 4236300){ //Mitchell Creek
                player.setExtraID(4412474);
            }else if (player.getId() == 3979){ //Toney Douglas
                player.setExtraID(3949086);
            }
            return player;
        }
        return null;
    }

    private String convertDateFormat(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
        return outputFormat.format(date);
    }
}
