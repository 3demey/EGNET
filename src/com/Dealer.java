package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Dealer {

    public static String Winnings(int earn, boolean fin) { //General func that returns appropriate message for accumulated winnings.
        // This func can be used in different scenarios. End of a Game (natural or quit), or status check.
        // variable: fin - adds additional message for when we reached the end of the game (endg).
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

    public static String resultMsg(int bet, int round, Card pcard, Card dcard, int earn)  {
        // Returns appropriate string for sending player round result.
        // Function also updates sum earnings of player and dealer.
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
            draw = "The bet: " + bet + "$\nDo you wish to surrender or go to war?\nEnter 0 to surrender or 1 to go to war."; // Only in a tie scenario - different output.
        }
        return header + result + dc + pc + draw;
    }
    public static String tieProced(int bet,int round, int earn, int selection, Deck deck)  { //Tie procedure, returns appropriate message to send to player
        String header ="Round " + round + " tie breaker:\n";
        if (selection == 0) {
            earn-=(bet/2);
            return header + "Player surrendered!\nThe bet: "+ bet +"$\nDealer won: " + (bet/2) + "\n Player won: " + (bet/2); }
        if (selection == 1) {
            for (int i = 0; i < 3; i++) //Three cards to discard.
                if (deck.getSize() > 2) //If we reach the end of the deck, we won't throw the cards, we will use them.
                    deck.draw(); //Discarding & not using cards.
            String discard = "Three cards discarded";
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
        return "Illegible entry, please try again.";
    }
    public static String finalResultMsg(int earn) { // Returns appropriate string for sending player when the game ends.
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
        String quit = "\nThe player quit\n";
        String win = Winnings(earn,true); // Game ends - therefore fin = true.
        return endGame + quit + win;
    }

    //public static void firstRound(int round, int bet, int earn, Card)

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
                        System.out.println(new Date() + "Connected to client- " + clientAddress);
                        DataInputStream fromPlayerInputStream = new DataInputStream(socket.getInputStream());
                        PrintStream outputStream = new PrintStream(socket.getOutputStream());
                        String line = "";
                        boolean playing = true; // Variable the determines whether game is still going, or should be stopped.
                        Deck deck = new Deck(); // A pile for dealer to draw from.
                        int round = 1, bet, earn = 0, total = 0; // round indicator, acceptor for player's bet, earn - game earnings, total - total earnings from all games.
                        int tieSelect; // tieSelect - proceed/surrender.
                        Card pcard = deck.draw(), dcard; // Card for player
                        outputStream.println("Welcome to our WAR GAME!!\nYour card: " + pcard + "\nPlease enter the amount you are willing to bet on: ");
                        bet = fromPlayerInputStream.readInt();
                        dcard = deck.draw();
                        line = resultMsg(bet, round, pcard, dcard, earn);
                        outputStream.println(line);
                        if (dcard.get_rank() == pcard.get_rank()) {
                            tieSelect = fromPlayerInputStream.readInt(); //Go to war or Surrender
                            line = tieProced(bet, round, earn, tieSelect, deck);
                            outputStream.println(line);
                        }
                        //Need to add input.
                        int choice;
                        while (playing) {// Actual game - initial interpretation, prone to changes.
                            // Player picks his next move: 1 - next round, 2 - status check, 3 - quit game.
                            outputStream.println("Select your next move:\n1) Continue to next round.\n2) Check game status.\n3) Quit game.");
                            choice = fromPlayerInputStream.readInt();
                            switch (choice) {
                                case 1: {
                                    round++;
                                    pcard = deck.draw();
                                    outputStream.println("Your card:" + pcard.get_display() + ", please enter your bet.");
                                    bet = fromPlayerInputStream.readInt();
                                    dcard = deck.draw();
                                    line = resultMsg(bet, round, pcard, dcard, earn);
                                    outputStream.println(line);
                                    if (dcard.get_rank() == pcard.get_rank()) {
                                        if (deck.getSize() < 2) {
                                            tieSelect = 0; //Not enough cards for tie round - automatic surrender.
                                            outputStream.println("Deck empty - automatic surrender.");
                                        } // if - deck size
                                        tieSelect = fromPlayerInputStream.readInt(); //Go to war or Surrender
                                        line = tieProced(bet, round, earn, tieSelect, deck);
                                        outputStream.println(line);
                                    } // if - tie
                                } // case 1
                                break;
                                case 2: { //Player checks his current game status
                                    line = currentStatus(round, earn);
                                    outputStream.println(line); //Sends message to player
                                } //case 2
                                break;
                                case 3: {
                                    line = playerQuit(round, earn);
                                    outputStream.println(line);
                                    playing = false;
                                } // case 3
                                break;
                                default:
                                    outputStream.println("Illegible input, please try again.");
                            } // switch
                            if (deck.getSize() < 2) {//Deck empty.
                                line = finalResultMsg(earn);
                                outputStream.println(line);
                                total += earn;
                                if (total > 0)
                                    outputStream.println("In total, player won " + total + "$");
                                else
                                    outputStream.println("In total, player lost " + (-total) + "$");
                                outputStream.println("To finish the game - enter 1, to continue playing - enter any other key.");
                                choice = fromPlayerInputStream.readInt();
                                if (choice == 1)
                                    playing = false;
                                else { //Restarts game
                                    deck = new Deck();
                                    pcard = deck.draw();
                                    round = 1; //Restarting round counter.
                                    earn = 0; //Restarting game earnings.
                                    line = ""; //Restarting line variable.
                                    outputStream.println("new WAR GAME starting!!");
                                    outputStream.print("Your card: " + pcard); // Sends player first card.
                                    // Player sends bet
                                    bet = fromPlayerInputStream.readInt();
                                    dcard = deck.draw();
                                    line = resultMsg(bet, round, pcard, dcard, earn);
                                    outputStream.println(line);
                                    if (dcard.get_rank() == pcard.get_rank()) {
                                        tieSelect = fromPlayerInputStream.readInt(); // Go to war or Surrender
                                        line = tieProced(bet, round, earn, tieSelect, deck);
                                        outputStream.println(line);
                                    }
                                }
                            } //if - empty deck
                        }
                        outputStream.println("Thanks for playing, GOODBYE");
                        outputStream.println("PIPIKAKI");
                        //TODO - terminate connection.
                    } catch (IOException e) {
                    }
                }).run();
            }//if too many players
            else {//Message to player that he can't connect.
                //TODO terminate connection
                if(!socket.isClosed())
                    socket.close();
            }
            players--;
            }
        }
    }
}
