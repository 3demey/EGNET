package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player {

    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream fromDealerInputStream;
        DataInputStream consoleInput;
        PrintStream toDealerOutputStream;
        String initLine = "";       //line with # instead of \n
        String line = "";
        int answer;
        try{
            socket = new Socket("localhost",2000);
            System.out.println(new Date() + "Connected to server - " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            fromDealerInputStream = new DataInputStream(socket.getInputStream());
            toDealerOutputStream = new PrintStream(socket.getOutputStream());
            consoleInput = new DataInputStream(System.in);
            // Gets the message from server and prints it for him
            initLine = fromDealerInputStream.readLine();
            line = initLine.replace('#', '\n');
            while(!(line.equals("PIPIKAKI"))){
                System.out.println(line);
                answer = consoleInput.readInt();
                toDealerOutputStream.println(answer);
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
