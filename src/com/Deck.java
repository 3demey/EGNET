package com;

import java.util.*;

public class Deck {
    private ArrayList<Card> _cards = new ArrayList<Card>();
    private int _size;

    public int getSize() {
        return _size;
    }

    public void setSize(int size) {
        this._size = size;
    }

    Deck(){
        this._size=52;
        char[] s = {'S', 'H', 'D', 'C'};
        for(int i = 2; i <= 14; i++)
            for (int j = 0; j < 4; j++) {
                _cards.add(new Card(i,s[j]));
            }
        Collections.shuffle(_cards);
    }

    public Card draw() {
        setSize(getSize()-1);
        return _cards.remove(0);
    }
}
