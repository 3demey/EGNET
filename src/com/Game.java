package com;

public class Game extends Thread {
    private static int players;

    public static synchronized void inc() {players++;} // Increases amount of current players, by one. Used when a player joins the game.
    public static synchronized void dec() {players--;} // Decreases amount of current players, by one. Used when a player exits the game.
    public static int getPlayers() { return players;  }
}
