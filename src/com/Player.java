package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player {

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream fromDealerInputStream;
        Scanner consoleInput = new Scanner(System.in);
        DataOutputStream toDealerOutputStream;
        String initLine = "";       //line with # instead of \n
        String line = "";
        int answer = 0;
        try{
            socket = new Socket("localhost",2000);
            System.out.println(new Date() + ". Connected to server - " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            fromDealerInputStream = new DataInputStream(socket.getInputStream());
            toDealerOutputStream = new DataOutputStream(socket.getOutputStream());
            // Gets the message from server and prints it for him
            initLine = fromDealerInputStream.readLine();
            line = initLine.replace('#', '\n');
            while(!(line.equals("Game Over"))){
                System.out.println(line);
                answer = consoleInput.nextInt();
                while ((answer < 0) || (answer > 9999999)) { //Bet amount boundaries.
                    System.out.println("Illegible entry, please try again.");
                    answer = consoleInput.nextInt();
                }
                toDealerOutputStream.writeInt(answer);
                initLine = fromDealerInputStream.readLine();
                line = initLine.replace('#','\n');
            }
        } catch(Exception e) {System.err.println(e);}
        finally {
            try{
                socket.close();
                System.out.println("Client disconnected");
            } catch(IOException e) {}
        }
    } // main
} // class - Dealer
