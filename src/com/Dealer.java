package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {

    static int clients;

    public static String Winnings(int psum, int dsum, boolean fin) {
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

    public static String resultMsg(int bet, int round, Card dcard, Card pcard)  { // Returns appropriate string to round result.
        String header = "The result of round  " + round;
        String result = "", dc = "Dealer's card: " + dcard.get_display() +"\n", pc = "Player's card: " + pcard.get_display() +"\n", draw ="";
        if (dcard.get_rank() > pcard.get_rank())
            result = ":\n Dealer won " + bet + "$\n";
        else if (dcard.get_rank() < pcard.get_rank())
            result = ":\n Player won " + bet + "\n";
        else {
            result = " is a tie!\n";
            draw = "The bet: " + bet + "$\nDo you wish to surrender or go to war?\n";
            //Send surrender/war selection.
        }
        return header + result + dc + pc + draw;
    }
    public static String finalResultMsg(int psum, int dsum) {
        String finalmsg = Winnings(psum,dsum,true);
        String append = "\nWould you like to play again?";
        return finalmsg + append;

    }
    public void terminateConnection() {

    }
    public static String currentStatus(int round, int psum, int dsum) {
        String status = "Current round: " + round +".\n";
        String winnings = Winnings(psum,dsum,false);
        return status + winnings;
    }
    public static String playerQuit(int round, int psum, int dsum) {
        String endGame = "The game has ended on round" + round + "!";
        String quit = "\nThe player quit\n";
        String win = Winnings(psum,dsum,false);
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
                        while (playing) {// Actual game
                            outputStream.print(pcard); //Sends player first card.
                            // Player sends bet
                           bet = fromPlayerInputStream.readInt();
                           dcard = deck.draw();
                           line = resultMsg(bet, round, dcard, pcard);
                           outputStream.println(line); // Sends result message.
                        }
                    } catch (IOException e) {
                    }
                    clients--;
                }).run();
            }
        }
    }
}
