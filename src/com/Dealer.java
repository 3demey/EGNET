package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {
    // Tool functions for later use
    public static String Winnings(int earn, boolean fin) {
        //General function that returns a message for accumulated winnings.
        // This function can be used in different scenarios. End of a Game (natural or quit), or status check.
        // variable: fin - enables additional messages if we reached the end of the game (endg).
        String msg ="", endg = "";
        if (earn > 0) { //Total earnings are positive - player won more than he lost.
          msg = "Player won " + earn + "$";
          if (fin) //If we reached the end of a game - additional message to be added.
            endg = "\nPlayer is the winner! :)";}
        else if (earn < 0) { //Total earnings are negative - player lost more than he won.
          msg = "Player lost " + (-earn) + "$";
          if (fin)
             endg = "\nDealer is the winner! :(";}
        else if (fin) {
            msg = "Player won 0$";
            endg = "The game has ended with a draw.";
        }
        return msg + endg;
    }
    // Main functions used in game
    public static String resultMsg(int bet, int round, Card pcard, Card dcard, int earn)  {
        // Returns string with round result data, to be sent to the player.
        // Function also updates sum earnings.
        String header = "The result of round  " + round; // Header - appropriate for every round result.
        String result = "",  draw =""; //Strings for certain messages - draw only used when round ends with a tie.
        String dc = "Dealer's card: " + dcard.get_display() +"\n", pc = "Player's card: " + pcard.get_display() +"\n";
        if (pcard.get_rank() > dcard.get_rank()) {
            result = ":\n Player won " + bet + "$\n"; // Appropriate result message when player wins round.
            earn += bet; //Total earnings increased by winning bet.
             }
        else if (pcard.get_rank() < dcard.get_rank()) {
            result = ":\n Dealer won " + bet + "$\n"; // Appropriate result message when dealer wins round.
            earn -= bet; //Total winnings decreased by loss of bet.
        }
        else { //This means card ranks are level - a tie!
            result = " is a tie!\n"; // Appropriate result message when round ends with a tie.
            draw = "The bet: " + bet + "$\nDo you wish to surrender or go to war?\nEnter 0 to surrender or any other number to go to war."; // Only in a tie scenario - different output.
        }
        return header + result + dc + pc + draw;
    }
    public static String tieProced(int bet,int round, int earn, int selection, Deck deck)  {
        //Tie procedure, returns message to send to player, when there is a tie.
        String header ="Round " + round + " tie breaker:\n";
        if (selection == 0) {
            earn-=(bet/2);
            return header + "Player surrendered!\nThe bet: "+ bet +"$\nDealer won: " + (bet/2) + "\n Player won: " + (bet/2); }
        else {
            header += "Going to war!\n";
            int cardDiscarded = 0;
            for (int i = 0; i < 3; i++) //Three cards to discard.
                if (deck.getSize() > 2) { //If we reach the end of the deck, we won't throw the cards, we will use them.
                    deck.draw(); //Discarding & not using cards.
                    cardDiscarded++;
                }
            String discard = cardDiscarded + " cards were discarded";
            String tieMsg = "Original bet: " + bet + "$\nNew bet: " + (2 * bet) + "$\n", result = "";
            Card pcard = deck.draw(), dcard = deck.draw();
            String dc = "Dealer's card: " + dcard.get_display() + "\n", pc = "Player's card: " + pcard.get_display() + "\n";
            if (dcard.get_rank() > pcard.get_rank()) {
                result = "Dealer won " + (bet * 2) + "$\n"; // Appropriate result message when player wins round.
                earn -= (bet * 2);
            } else if (dcard.get_rank() < pcard.get_rank()) {
                result = "Player won: " + bet + "$\n"; // Appropriate result message when dealer wins round.
                earn += bet;
            } else {
                result = "Player won: " + (bet * 2) + "$\n"; // Appropriate result message when round ends with a draw.
                earn += (bet * 2);
            }
            return header + tieMsg + dc + pc + result;
        }
    }
    public static String finalResultMsg(int earn) {
        // Returns appropriate string for sending player when the game ends.
        String finalmsg = Winnings(earn,true); // A string that represents total winnings - see winnings. Game over - fin == true.
        String append = "\nWould you like to play again?"; // Additional message when game ends.
        return finalmsg + append;
    }
    public static String currentStatus(int round, int earn) { //Message sent when checking status.
        String status = "Current round: " + round +".\n";
        String winnings = Winnings(earn,false); // fin == false, game hasn't ended yet.
        return status + winnings;
    }
    public static String playerQuit(int round, int earn) {
        String endGame = "The game has ended on round" + round + "!";
        String quit = "\nThe player quit.\n";
        String win = Winnings(earn,false);
        String thanks = "\nThanks for playing.";
        return endGame + quit + win + thanks;
    }



    public static void main(String[] args) throws IOException {
        final ServerSocket server = new ServerSocket(2000);
        int players = 0;
        while (true) {
            Socket socket = server.accept();
            players++;
            if (players <= 2) {
                new Thread(() -> {//Lambda function
                    String clientAddress = "";
                    try {
                        clientAddress = socket.getInetAddress() + ":" + socket.getPort();
                        System.out.println(new Date() + "Connected to client - " + clientAddress + ". Client will start game shortly...");
                        DataInputStream fromPlayerInputStream = new DataInputStream(socket.getInputStream());
                        PrintStream toPlayerOutputStream = new PrintStream(socket.getOutputStream());
                        String line = "";
                        Deck deck = new Deck(); // A pile for dealer to draw from.
                        int round = 1, bet, earn = 0, total = 0; // round indicator, acceptor for player's bet, earn - game earnings, total - total earnings from all games.
                        int tieSelect; // tieSelect - proceed/surrender.
                        Card pcard = deck.draw(), dcard; // Card for player
                        line = "Welcome to our WAR GAME!!\nYour card: " + pcard + "\nPlease enter the amount you are willing to bet on: ";
                        toPlayerOutputStream.println(line.replace('\n','#'));
                        bet = fromPlayerInputStream.readInt();
                        dcard = deck.draw();
                        line = resultMsg(bet, round, pcard, dcard, earn);
                        if (dcard.get_rank() != pcard.get_rank())
                            line += "\n\nEnter any number to continue.\n\n";
                        toPlayerOutputStream.println(line.replace('\n','#'));
                        if (dcard.get_rank() == pcard.get_rank()) {
                            tieSelect = fromPlayerInputStream.readInt(); //Go to war or Surrender
                            line = tieProced(bet, round, earn, tieSelect, deck);
                            line += "\n\nEnter any number to continue.\n\n";
                            toPlayerOutputStream.println(line.replace('\n','#'));
                        } // if - tie
                        fromPlayerInputStream.readInt();
                        boolean playing = true; // Variable the determines whether game is still going, or should be stopped.
                        while (playing) {// Actual game - initial interpretation, prone to changes.
                            int choice, quit;
                            // Player picks his next move: 1 - next round, 2 - status check, 3 - quit game.
                            line = "=================================================\n";
                            line += "Select your next move:\n1) Continue to next round.\n2) Check game status.\n3) Quit game.\n";
                            line += "=================================================";
                            toPlayerOutputStream.println(line.replace('\n','#'));
                            choice = fromPlayerInputStream.readInt();
                            switch (choice) {
                                case 1: {
                                    round++;
                                    pcard = deck.draw();
                                    line = "Your card:" + pcard.get_display() + "\nPlease enter your bet.";
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    bet = fromPlayerInputStream.readInt();
                                    dcard = deck.draw();
                                    line = resultMsg(bet, round, pcard, dcard, earn);
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    if (dcard.get_rank() == pcard.get_rank()) { // TIE - receiving decision from player.
                                        tieSelect = fromPlayerInputStream.readInt(); //Go to war or Surrender
                                        if (deck.getSize() < 2) {
                                            line = "Sorry, our deck of cards is empty - surrendering automatically.\nEnter any number to continue.";
                                            toPlayerOutputStream.println(line.replace('\n','#'));
                                            fromPlayerInputStream.readInt();
                                            tieSelect = 0; //Not enough cards for tie round - automatic surrender.
                                        } // if - deck empty
                                        line = tieProced(bet, round, earn, tieSelect, deck);
                                        line += "\n\nEnter any number to continue.\n\n";
                                        toPlayerOutputStream.println(line.replace('\n','#'));
                                        fromPlayerInputStream.readInt();
                                    } // if - tie
                                } // case 1
                                break;
                                case 2: { //Player checks his current game status
                                    line = currentStatus(round, earn);
                                    line += "\n\nEnter any number to continue.\n\n";
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    fromPlayerInputStream.readInt();
                                } //case 2
                                break;
                                case 3: {
                                    line = playerQuit(round, earn);
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    playing = false;
                                } // case 3
                                break;
                                default: {
                                    line = "Illegible input, please try again.";
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                }
                            } // switch
                            if (deck.getSize() < 2) {//Deck empty.
                                line = finalResultMsg(earn);
                                total += earn;
                                if (total > 0)
                                    line += "\nIn total, player won " + total + "$";
                                else
                                    line += "\nIn total, player lost " + (-total) + "$";
                                line += "\nTo finish the game - enter 0, to continue playing - enter any other key.";
                                toPlayerOutputStream.println(line.replace('\n','#'));
                                quit = fromPlayerInputStream.readInt();
                                if (quit == 0)
                                    playing = false;
                                else { //Restarts game
                                    deck = new Deck();
                                    pcard = deck.draw();
                                    round = 1; //Restarting round counter.
                                    earn = 0; //Restarting game earnings.
                                    line = "new WAR GAME starting!!";
                                    line += "\nYour card: " + pcard + "\nPlease enter your bet."; // Sends player first card.
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    bet = fromPlayerInputStream.readInt();
                                    dcard = deck.draw();
                                    line = resultMsg(bet, round, pcard, dcard, earn);
                                    if (dcard.get_rank() != pcard.get_rank())
                                        line += "\n\nEnter any number to continue.\n\n";
                                    toPlayerOutputStream.println(line.replace('\n','#'));
                                    if (dcard.get_rank() == pcard.get_rank()) {
                                        tieSelect = fromPlayerInputStream.readInt(); // Go to war or Surrender
                                        line = tieProced(bet, round, earn, tieSelect, deck);
                                        line += "\n\nEnter any number to continue.\n\n";
                                        toPlayerOutputStream.println(line.replace('\n','#'));
                                    } // first round tie.
                                    fromPlayerInputStream.readInt();
                                } // else - start new game
                            } //if - empty deck
                        } // while - playing
                        System.out.println("Disconnecting from client - " + clientAddress);
                        toPlayerOutputStream.println("PIPIKAKI");
                        if(!socket.isClosed())
                            socket.close();
                    } catch (IOException e) { }
                }).run();
            }//if too many players
            else {
                String clientAddress = socket.getInetAddress() + ":" + socket.getPort();
                System.out.println(new Date() + "Connected to client - " + clientAddress + ". Client is kicked out of game.");
                PrintStream toPlayerOutputStream = new PrintStream(socket.getOutputStream());
                toPlayerOutputStream.println("PIPIKAKI");
                if(!socket.isClosed())
                    socket.close();
            }
            players--;
            }
        }
    }
