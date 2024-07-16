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
    // TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE
    // TIME.
    String matchupPath = "src/main/java/me/antcode/Matchup1Month.csv";
    String playByPlayPath = "src/main/java/me/antcode/normalizedPBP.csv";
    List<Matchup> allMatchups;
    CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
    TestPlayerStats testPlayerStats = new TestPlayerStats();
    testPlayerStats.loadCSV();
    allMatchups = csvDataGather.extractAllMatchups();
    for (Matchup matchup : allMatchups) {
      csvDataGather.labelAllPlays(matchup);
      csvDataGather.getPlays(0, matchup);
//      testPlayerStats.checkPlayerStatsForMatchup(matchup);
    }
        csvDataGather.developPlayTypesCSV(allMatchups);
    csvDataGather.deployMatchupAndPlayByPlayCSV(allMatchups);
  }
}


