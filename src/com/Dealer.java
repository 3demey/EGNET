package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {

    static int clients;

    public static String Winnings(int earn, boolean fin) { //General func that returns appropriate message for accumulated winnings.
        // This func can be used in different scenarios. End of a Game (natural or quit), or status check.
        // variable: fin - adds additional message for when we reached the end of the game (endg).
        String msg ="", endg = "";
        if (earn > 0) {
          msg = "Player won " + earn + "$";
          if (fin)
            endg = "\nPlayer is the winner! :)";}
        else if (earn < 0) {
          msg = "Player lost " + (-earn) + "$";
          if (fin)
             endg = "\nDealer is the winner! :(";}
        else if (fin)
        endg = "The game has ended with a draw.";
        return msg + endg;
    }

    public static String resultMsg(int bet, int round, Card pcard, Card dcard, int earn, boolean tie)  {
        // Returns appropriate string for sending player round result.
        // Function also updates sum earnings of player and dealer.
        String header = "The result of round  " + round; // Header - appropriate for every round result.
        String result = "", dc = "Dealer's card: " + dcard.get_display() +"\n", pc = "Player's card: " + pcard.get_display() +"\n", draw ="";
        if (dcard.get_rank() > pcard.get_rank()) {
            result = ":\n Dealer won " + bet + "$\n"; // Appropriate result message when player wins round.
            earn -= bet;}
        else if (dcard.get_rank() < pcard.get_rank()) {
            result = ":\n Player won " + bet + "\n"; // Appropriate result message when dealer wins round.
            earn += bet;}
        else {
            tie = true; //For later use - to start another round.
            result = " is a tie!\n"; // Appropriate result message when round ends with a tie.
            draw = "The bet: " + bet + "$\nDo you wish to surrender or go to war?\n"; // Only in a tie scenario - different output.
        }
        return header + result + dc + pc + draw;
    }
    public static String tieProced(int bet,int round, int earn, boolean selection, Deck deck) { //Tie procedure
        String header ="Round " + round + " tie breaker:\n";
        if (!selection){
            earn-=(bet/2);
            return header + "Player surrendered!\nThe bet: "+ bet +"$\nDealer won: " + (bet/2) + "\n Player won: " + (bet/2);}
        for(int i = 0 ; i < 3 ; i++)
            deck.draw();
        String discard = "Three cards discarded";
        String tieMsg = "Original bet: " + bet + "$\nNew bet: " + (2*bet) +"$\n", result ="";
        Card pcard = deck.draw(), dcard = deck.draw();
        String dc = "Dealer's card: " + dcard.get_display() +"\n", pc = "Player's card: " + pcard.get_display() +"\n";
        if (dcard.get_rank() > pcard.get_rank()) {
            result = "Dealer won " + (bet*2) + "$\n"; // Appropriate result message when player wins round.
            earn -= (bet*2);}
        else if (dcard.get_rank() < pcard.get_rank()) {
            result = "Player won: " + bet + "$\n"; // Appropriate result message when dealer wins round.
            earn += bet;}
        else {
            result = "Player won: " + (bet*2) + "$\n"; // Appropriate result message when round ends with a draw.
            earn += (bet*2);
        }
        return header + tieMsg + dc + pc + result;
    }
    public static String finalResultMsg(int earn) { // Returns appropriate string for sending player when the game ends.
        String finalmsg = Winnings(earn,true); // A string that represents total winnings - see winnings. Game over - fin == true.
        String append = "\nWould you like to play again?"; // Additional message when game ends.
        return finalmsg + append;
    }
    public void terminateConnection() {//For terminating connectio when game ends or when a player quits.
    }
    public static String currentStatus(int round, int earn) {
        String status = "Current round: " + round +".\n";
        String winnings = Winnings(earn,false); // fin == false, game hasn't ended.
        return status + winnings;
    }
    public static String playerQuit(int round, int psum, int dsum) {
        String endGame = "The game has ended on round" + round + "!";
        String quit = "\nThe player quit\n";
        String win = Winnings(psum,dsum,true); // Game ends - therefore fin = true.
        return endGame + quit + win;
    }

    public static void main(String[] args) throws IOException {
        final ServerSocket server = new ServerSocket(20);
        while (true) {
            if (clients < 3) { // THIS IS THE WRONG WAY TO ACCEPT ONLY 2 CLIENTS, FOUND RIGHT WAY ON GOOGLE. A LITTLE CONFUSING.
                final Socket socket = server.accept();
                clients++;
                new Thread(() -> {//Lambda function
                    String clientAddress = "";
                    Deck deck = new Deck(); // A pile for dealer to draw from.
                    int round = 1, bet, earn = 0;
                    boolean playing = true, tie = false, tieSelect;
                    Card pcard = deck.draw(), dcard;
                    try {
                        clientAddress = socket.getInetAddress() + ":" + socket.getPort();
                        System.out.println(new Date() + "Connected to client- " + clientAddress);
                        DataInputStream fromPlayerInputStream = new DataInputStream(socket.getInputStream());
                        PrintStream outputStream = new PrintStream(socket.getOutputStream());
                        outputStream.println("Welcome to our WAR GAME!!");
                        String line = "";

                            outputStream.print(pcard); //Sends player first card.
                            // Player sends bet
                           bet = fromPlayerInputStream.readInt();
                           dcard = deck.draw();
                           line = resultMsg(bet, round, pcard, dcard, earn, tie);
                           outputStream.println(line);
                           tieSelect = fromPlayerInputStream.readBoolean();
                           if (tie){
                               tieProced(bet,round,earn,tieSelect,deck);
                           }
                           int choice;
                           outputStream.println(line); // Sends result message.
                        while (playing) {// Actual game - initial interpretation, prone to changes.
                            //Player picks his next move: 1 - next round, 2 - status check, 3 - quit game.
                            choice = fromPlayerInputStream.readInt();
                            switch(choice) {
                                case 1: {//Remember - if deck size too small -> playing = false.
                                    round++;
                                    bet = fromPlayerInputStream.readInt();
                                    dcard = deck.draw();
                                    pcard = deck.draw();
                                    line = resultMsg(bet,round,pcard,dcard,psum,dsum);
                                    outputStream.println(line);
                                }
                                break;
                                case 2:{ //Player checks his current game status
                                    line = currentStatus(round,psum,dsum);
                                    outputStream.println(line); //Sends message to player
                                }
                                break;
                                case 3:{
                                   line = playerQuit(round, psum, dsum);
                                   outputStream.println(line);
                                   playing = false;
                                }
                                break;
                                default: outputStream.println("Illegible input, please try again.");
                            }
                        }
                        outputStream.println("Thanks for playing, GOODBYE");
                        outputStream.println("PIPIKAKI");
                       // this.terminateConnection();
                    } catch (IOException e) {}
                    clients--;
                }).run();
            }
        }
    }
}
