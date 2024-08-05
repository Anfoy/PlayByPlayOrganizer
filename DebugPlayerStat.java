package me.antcode.datacollection;

import me.antcode.Matchup;
import me.antcode.Player;

public class DebugPlayerStat {


    private final Player player;

    private final Matchup matchup;

    private final String type;

    private final int recordedAmount;

    private final int scoreSheetAmount;

    private final int gameID;

    private boolean wasOver5, was4, was3, was2, was1;

    public DebugPlayerStat(Player player, String type, int recordedAmount, int scoreSheetAmount, int gameID, Matchup matchup) {
        this.player = player;
        this.type = type;
        this.recordedAmount = recordedAmount;
        this.scoreSheetAmount = scoreSheetAmount;
        this.gameID = gameID;
        this.matchup = matchup;
        identifySplit(recordedAmount, scoreSheetAmount);
    }

    public boolean isWasOver5() {
        return wasOver5;
    }

    public boolean isWas4() {
        return was4;
    }

    public boolean isWas3() {
        return was3;
    }

    public boolean isWas2() {
        return was2;
    }

    public boolean isWas1() {
        return was1;
    }

    public void identifySplit(int recordedAmount, int actualAmount){
        if (recordedAmount - actualAmount == 1 || recordedAmount - actualAmount == -1){
            was1 = true;
        }else if (recordedAmount - actualAmount == 2 || recordedAmount - actualAmount == -2){
            was2 = true;
        }else if (recordedAmount - actualAmount == 3 || recordedAmount - actualAmount == -3){
            was3 = true;
        }else if (recordedAmount - actualAmount == 4 || recordedAmount - actualAmount == -4){
            was4 = true;
        }else if (recordedAmount - actualAmount >= 5 || recordedAmount - actualAmount <= -5){
            wasOver5 = true;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public String getType() {
        return type;
    }

    public int getRecordedAmount() {
        return recordedAmount;
    }

    public int getScoreSheetAmount() {
        return scoreSheetAmount;
    }

    public int getGameID() {
        return gameID;
    }

    public String toString(){
        return "---------------------" + "\n" + "GAME ID: " + gameID + "\n"
                + "PLAYER NAME AND ID: {" + player.getName() + "} " + player.getId() + " \n"
                + "TYPE: " + type + "\n"
                + "RECORDED AMOUNT: " + recordedAmount + "\n"
                + "SCORE SHEET AMOUNT: " + scoreSheetAmount + "\n"
                + "GAME DATE: " + matchup.getDate() + "\n"
                + "HOME TEAM: " + matchup.getHomeTeam() + "\n"
                + "AWAY TEAM: " + matchup.getAwayTeam() + "\n"
                + "SEARCH: " + matchup.getHomeTeam() + " " + matchup.getAwayTeam() + " " + matchup.getDate() + "\n"
                + "---------------------";
    }

}
