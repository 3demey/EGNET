package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {

    static int clients;

    public void printWinnings(int psum, int dsum, boolean fin) {
        //if playersum > dealersum
        System.out.println("Player won " + (psum - dsum) + "$");
        //		if (fin)
        System.out.println("Player is the winner! :)");
        //else if dealersum>playersum
        System.out.println("Player lost " + (dsum - psum) + "$");
        // 		if fin
        System.out.println("Dealer is the winner! :(");
        //else if fin
        System.out.println("The game has ended with a draw.");
    }

    public void resultMsg(int bet, int round, Card dcard, Card pcard)  {
        System.out.print("The result of round" + round);
        //if dealer > player
        System.out.println(":\n Dealer won " + bet + "$");
        //else if dealer < player
        System.out.println(":\n Player won " + bet + "$");
        //else
        System.out.print("is a tie!\n");
        System.out.print("Dealer's card: " );
        dcard.print();
        System.out.print("Player's card:  ");
        pcard.print();
        //if dealer == player
        System.out.println("The bet: " + bet + "$");
        System.out.println("Do you wish to surrender or go to war?");
        //Send surrender/war selection.
    }
    public void finalResultMsg(int psum, int dsum) {
        printWinnings(psum,dsum,true);
        System.out.println("Would you like to play again?");

    }
    public void terminateConnection() {

    }
    public void currentStatus(int round, int psum, int dsum) {
        System.out.println("Current round: " + round);
        printWinnings(psum,dsum,false);
    }
    public void playerQuit(int round, int psum, int dsum) {
        System.out.println("The game has ended on round" + round + "!");
        System.out.println("The player quit");
        printWinnings(psum,dsum,false);
    }

    public static void main(String[] args) throws IOException {
        final ServerSocket server = new ServerSocket(20);
        while (true) {
            if (clients < 3) {
                final Socket socket = new server.accept();
                clients++;
                Thread game = () -> { // Lambda expression
                    String clientAddress = "";
                    Deck drawPile = new Deck(); // A pile for dealer to draw from.
                    int round;
                    Card pcard = drawPile.draw(), dcard;
                    try {
                        clientAddress = socket.getInetAddress() + ":" + socket.getPort();
                        System.out.println(new Date() + "Connected to client- " + clientAddress);
                        DataInputStream fromPlayerInputStream = new DataInputStream(socket.getInputStream());
                        PrintStream outputStream = new PrintStream(socket.getOutputStream());
                        System.out.println("Welcome to our WAR GAME!!");
                        String line = "";
                        while () {// Actual game

                        }
                    } catch (IOException e) {}
                    clients--;
                };
                game.start();
            }
        }
    }
}
