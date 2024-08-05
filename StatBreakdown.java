package me.antcode.datacollection;

import me.antcode.Player;

import java.util.ArrayList;

public class StatBreakdown {

    private final String name;

    private final ArrayList<Player> affectedPlayers;

    private int byOne;

    private int byTwo;

    private int byThree;

    private int byFour;

    private int fivePlus;

    public StatBreakdown(String name) {
        this.name = name;
        this.byOne = 0;
        this.byTwo = 0;
        this.byThree = 0;
        this.byFour = 0;
        this.fivePlus = 0;
        affectedPlayers = new ArrayList<>();
    }

    public void identifySplit(int recordedAmount, int actualAmount, Player player){
        if (recordedAmount - actualAmount == 1 || recordedAmount - actualAmount == -1){
            byOne++;
            return;
        }else if (recordedAmount - actualAmount == 2 || recordedAmount - actualAmount == -2){
            byTwo++;
            return;
        }else if (recordedAmount - actualAmount == 3 || recordedAmount - actualAmount == -3){
            byThree++;
            return;
        }else if (recordedAmount - actualAmount == 4 || recordedAmount - actualAmount == -4){
            byFour++;
            return;
        }else if (recordedAmount - actualAmount >= 5 || recordedAmount - actualAmount <= -5){
//      System.out.println("----------------");
//      System.out.println(recordedAmount);
//      System.out.println(actualAmount);
//      System.out.println("_______________________");
            fivePlus++;
        }
        attemptToAddPlayer(player);
    }

    public String getName() {
        return name;
    }

    public int getByOne() {
        return byOne;
    }

    public int getByTwo() {
        return byTwo;
    }

    public int getByThree() {
        return byThree;
    }

    public int getByFour() {
        return byFour;
    }

    public int getFivePlus() {
        return fivePlus;
    }

    private void attemptToAddPlayer(Player player){
        for (Player player1 : affectedPlayers) {
            if (player1.getId() == player.getId()) return;
        }
        affectedPlayers.add(player);
    }

    public int getNumOfUniquePlayers(){
        return affectedPlayers.size();
    }

    public String toString(){
        return "-------------------" + "\n"
                + "Name: " + name + "\n"
                + "By One: " + byOne + "\n"
                + "By Two: " + byTwo + "\n"
                + "By Three: " + byThree + "\n"
                + "By Four: " + byFour + "\n"
                + "Five Plus: " + fivePlus + "\n"
                + "--------------------";
    }
}
