package com;

import java.util.*;

public class Deck {
    private ArrayList<Card> cards = new ArrayList<Card>();

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
}
