package me.antcode;
import me.antcode.datacollection.CSVDataGather;
import me.antcode.datacollection.DebugPlayerStat;
import me.antcode.managers.PlayLabellingManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    // TIME DISCREPANCY IS BECAUSE FROM SECOND LAST TO LAST PLAY IS NOT REGISTERED MEANING YOU LOSE
    // TIME.
    PlayLabellingManager playLabellingManager = new PlayLabellingManager();
    String matchupPath = "src/main/java/me/antcode/MATCHUPS (2).csv";
    String playByPlayPath = "src/main/java/me/antcode/finalProcessed.csv";
    List<Matchup> allMatchups;
    CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
    TestPlayerStats testPlayerStats = new TestPlayerStats();
    testPlayerStats.loadCSV();
    allMatchups = csvDataGather.getMatchups();
    allMatchups.sort((m1, m2) -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");
      try {
        Date date1 = dateFormat.parse(m1.getDate());
        Date date2 = dateFormat.parse(m2.getDate());
        return date1.compareTo(date2);
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    });
    csvDataGather.filterAndDesignatePlays();
    for (Matchup matchup : allMatchups){
      csvDataGather.getPlays(0, matchup);
    }
    for (Matchup matchup : allMatchups){
      if (matchup.getLabeledPlayList().isEmpty()) continue;
      testPlayerStats.checkPlayerStatsForMatchup(matchup);
    }
//          testPlayerStats.checkPlayerStatsForMatchup(csvDataGather.curateSpecificPlay(400827952));
//          playLabellingManager.printPlayerInvolvedLabeledPlaysForMatchup(400827952,  2530530, allMatchups);


    for (DebugPlayerStat debugPlayerStat : testPlayerStats.getDebugStats()){
      if (debugPlayerStat.isWasOver5() && !debugPlayerStat.getType().equals("MINUTES") &&
              !debugPlayerStat.getType().equals("POINTS")) {
        System.out.println(debugPlayerStat);
      }
    }

//    for (StatBreakdown statBreakdown : testPlayerStats.getAllStats()){
//        System.out.println(statBreakdown);
//    }
    csvDataGather.developPlayTypesCSV(allMatchups);
    csvDataGather.deployTestCSV(allMatchups);
    csvDataGather.deployMatchupAndPlayByPlayCSV(allMatchups);
  }
}


