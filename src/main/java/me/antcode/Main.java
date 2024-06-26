package me.antcode;

import me.antcode.datacollection.CSVDataGather;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        String matchupPath = "src/main/java/me/antcode/Matchup1Month.csv";
        String playByPlayPath = "src/main/java/me/antcode/PlayByPlay1Month.csv";
        List<Matchup> allMatchups;
        CSVDataGather csvDataGather = new CSVDataGather(matchupPath, playByPlayPath);
        allMatchups = csvDataGather.extractAllMatchups();
    }
}