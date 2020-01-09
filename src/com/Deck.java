package com;

import java.util.*;

public class Deck {
    private ArrayList<Card> cards;

    Deck(){
        char[] s = {'S', 'H', 'D', 'C'};
        for(int i = 2; i <= 14; i++)
            for (int j = 0; j < 4; j++) {
                cards.add(new Card(i,s[j]));
            }
        Collections.shuffle(cards);
    }

    public Card draw() {
        return cards.remove(0);
    }

    public static void main(String[] args) {
        Deck mydeck = new Deck();
        mydeck.draw().print();
        mydeck.draw().print();
        mydeck.draw().print();
        mydeck.draw().print();
    }
}
