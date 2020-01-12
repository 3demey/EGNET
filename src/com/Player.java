package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player {
    //NEED TO SWITCH ALL prints TO Strings, so we can send to dealer.
    //NEED A MAIN MENU FOR OUR PLAYER TO CHOOSE HIS NEXT MOVE. need a main class in general.
    //Implementation for unwritten methods.
    public boolean request() {}
    public void betMsg(int bet,DataInputStream consoleInput, PrintStream toDealerOutputStream) throws IOException
    {		// Queries desired betting amount from player and sends it to the dealer.
        System.out.println("================================");
        System.out.print("Please Enter the amount you are willing to bet on:  ");
        bet = consoleInput.readInt();
        System.out.println("================================");
        toDealerOutputStream.print(bet);
    }
    public void playAgainMsg() {}
    public void viewStatus(PrintStream toDealerOutputStream)
    { // Allows player to request from the dealer to view his current game status.

    }
    public void quit() {}

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream fromDealerInputStream;
        DataInputStream consoleInput;
        PrintStream toDealerOutputStream;
        String line = "";
        try{
            socket = new Socket("localhost",7000);
            System.out.println(new Date() + "Connected to server- " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            fromDealerInputStream = new DataInputStream(socket.getInputStream());
            toDealerOutputStream = new PrintStream(socket.getOutputStream());
            consoleInput = new DataInputStream(System.in);
        } catch(Exception e) {System.err.println(e);}
        finally {
            try{
                socket.close();
                System.out.println("GOODBYE, hope you had fun!");
            } catch(IOException e) {}
        }
    }
}
