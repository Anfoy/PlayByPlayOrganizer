package me.antcode;

import me.antcode.datacollection.CSVDataGather;
import me.antcode.plays.LabeledPlay;
import me.antcode.plays.Play;
import me.antcode.plays.PlayTypes;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE TIME.
        String matchupPath = "src/main/java/me/antcode/Matchup1Month.csv";
    String playByPlayPath = "src/main/java/me/antcode/normalizedPBP.csv";
        List<Matchup> allMatchups;
        CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
        allMatchups = csvDataGather.extractAllMatchups();
        for (Matchup matchup : allMatchups){
            csvDataGather.labelAllPlays(matchup);
            csvDataGather.getPlays(0, matchup);
        }

        for (Matchup matchup : allMatchups){
            if (matchup.getGameID() == 401656362){
                for (Play play : matchup.getPlayByPlays()){
          System.out.println(play.getPlayType());
                    if (play.getPlayType() == PlayTypes.UNIDENTIFIEDPLAYTYPE){
            System.out.println(play.getQuarter() + " | " + convertSecondsToMinuteFormat(play.getTimeLeftInQuarter()));
            for (LabeledPlay play1 : play.getMakeUpOfPlay()){
              System.out.println(play1.action() + " | " + play1.gamePlayNumber());
            }
                    }
                }
            }

        }

        try (FileWriter writer = new FileWriter("playTypes.csv");
             CSVPrinter printer =
                     new CSVPrinter(
                             writer,
                             CSVFormat.DEFAULT
                                     .builder()
                                     .setHeader("game_id", "play_type", "duration_of_play", "time_after_play", "qtr")
                                     .build())) {

            for (Matchup matchup : allMatchups) {
                for (Play play : matchup.getPlayByPlays()) {
                    printer.printRecord(
                            play.getGameID(),
                            play.getPlayType(),
                            play.getPlayDuration(),
                            convertSecondsToMinuteFormat(play.getTimeLeftInQuarter()),
                            play.getQuarter());
                }
            }

            printer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertSecondsToMinuteFormat(double totalSeconds) {
        int minutes = (int) totalSeconds / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) ((totalSeconds - (int) totalSeconds) * 1000);

        // Convert milliseconds to string and remove trailing zeros

        return String.format("%02d:%02d.%01d", minutes, seconds, milliseconds);
    }
}