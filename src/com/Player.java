package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player {
    //NEED TO SWITCH ALL prints TO Strings, so we can send to dealer.
    //NEED A MAIN MENU FOR OUR PLAYER TO CHOOSE HIS NEXT MOVE. need a main class in general.
    //Implementation for unwritten methods.


//    public int betMsg(int bet,DataInputStream consoleInput, PrintStream toDealerOutputStream)
//    {		// Queries desired betting amount from player and sends it to the dealer.
//        //should change it to return string? no, send it to the dealer
//        System.out.println("================================");
//        System.out.print("Please Enter the amount you are willing to bet on:  ");
//        bet = consoleInput.readInt();
//        System.out.println("================================");
//        toDealerOutputStream.print(bet);
//    }

//    public int answerToInt(String answer)
//    {
//        //gets the choice if he wants to continue or not, and returns int, in main we will return to server.
//        System.out.println("Enter yes for surrender and no to continue");
//        int srndr ;
//        if(answer =="yes")
//            srndr = 1;
//        else {
//            srndr = 0;
//        }
//        return srndr;
//    }

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream fromDealerInputStream;
        DataInputStream consoleInput;
        PrintStream toDealerOutputStream;
        String line = "";
        int answer;
        try{
            socket = new Socket("localhost",7000);
            System.out.println(new Date() + "Connected to server- " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            fromDealerInputStream = new DataInputStream(socket.getInputStream());
            toDealerOutputStream = new PrintStream(socket.getOutputStream());
            consoleInput = new DataInputStream(System.in);
            //gets the messege from server adn prints it for him
            line = fromDealerInputStream.readLine();
            while(!(line.equals("PIPIKAKI"))){
                System.out.println(line);
                answer = consoleInput.readInt();
                toDealerOutputStream.print(answer);
                line = fromDealerInputStream.readLine();
            }
        } catch(Exception e) {System.err.println(e);}
        finally {
            try{
                System.out.println(line);
                socket.close();
            } catch(IOException e) {}
        }
    }
}
