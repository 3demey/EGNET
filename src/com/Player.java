package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player {

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream fromDealerInputStream;
        //DataInputStream consoleInput;
        Scanner consoleInput = new Scanner(System.in);
        DataOutputStream toDealerOutputStream;
        String initLine = "";       //line with # instead of \n
        String line = "";
        int answer;
        try{
            socket = new Socket("localhost",2000);
            System.out.println(new Date() + ". Connected to server - " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            fromDealerInputStream = new DataInputStream(socket.getInputStream());
            toDealerOutputStream = new DataOutputStream(socket.getOutputStream());
            //consoleInput = new DataInputStream(System.in);
            // Gets the message from server and prints it for him
            initLine = fromDealerInputStream.readLine();
            line = initLine.replace('#', '\n');
            while(!(line.equals("Game Over"))){
                System.out.println(line);
                answer = consoleInput.nextInt();
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
