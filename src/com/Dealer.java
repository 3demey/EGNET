package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {

    static int clients;

    public static String Winnings(int psum, int dsum, boolean fin) { //General func that returns appropriate message for accumulated winnings.
        // This func can be used in different scenarios. End of a Game (natural or quit), or status check.
        // variable: fin - adds additional message for when we reached the end of the game (endg).
        String msg ="", endg = "";
        if (psum > dsum) {
          msg = "Player won " + (psum - dsum) + "$";
          if (fin)
            endg = "\nPlayer is the winner! :)";}
        else if (dsum > psum) {
          msg = "Player lost " + (dsum - psum) + "$";
          if (fin)
             endg = "\nDealer is the winner! :(";}
        else if (fin)
        endg = "The game has ended with a draw.";
        return msg + endg;
    }

    public static String resultMsg(int bet, int round, Card dcard, Card pcard)  { // Returns appropriate string for sending player round result.
        String header = "The result of round  " + round; // Header - appropriate for every round result.
        String result = "", dc = "Dealer's card: " + dcard.get_display() +"\n", pc = "Player's card: " + pcard.get_display() +"\n", draw ="";
        if (dcard.get_rank() > pcard.get_rank())
            result = ":\n Dealer won " + bet + "$\n"; // Appropriate result message when player wins round.
        else if (dcard.get_rank() < pcard.get_rank())
            result = ":\n Player won " + bet + "\n"; // Appropriate result message when dealer wins round.
        else {
            result = " is a tie!\n"; // Appropriate result message when round ends with a tie.
            draw = "The bet: " + bet + "$\nDo you wish to surrender or go to war?\n"; // Only in a tie scenario - different output.
            reminder //NEED TO ADD - Send surrender/war selection.
        }
        return header + result + dc + pc + draw;
    }
    public static String finalResultMsg(int psum, int dsum) { // Returns appropriate string for sending player when the game ends.
        String finalmsg = Winnings(psum,dsum,true); // A string that represents total winnings - see winnings. Game over - fin == true.
        String append = "\nWould you like to play again?"; // Additional message when game ends.
        return finalmsg + append;
    }
    public void terminateConnection() {//For terminating connectio when game ends or when a player quits.
    }
    public static String currentStatus(int round, int psum, int dsum) {
        String status = "Current round: " + round +".\n";
        String winnings = Winnings(psum,dsum,false); // fin == false, game hasn't ended.
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
                    int round = 1, bet;
                    boolean playing = true;
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
                           line = resultMsg(bet, round, dcard, pcard);
                           int choice;
                           outputStream.println(line); // Sends result message.
                        while (playing) {// Actual game - initial interpretation, prone to changes.
                            //Player picks his next move: 1 - next round, 2 - status check, 3 - quit game.
                            choice = fromPlayerInputStream.readInt();
                            switch(choice) {
                                1: {//Remember - if deck size too small -> playing = false.
                                }
                                break;
                                2:{}
                                break;
                                3:{//playing = false
                                }
                                break;
                                deafult: outputStream.println("Illegible input, please try again.");
                                        break;
                            }
                        }
                    } catch (IOException e) {}
                    clients--;
                }).run();
            }
        }
    }
}
