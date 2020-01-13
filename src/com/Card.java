package com;


public class Card {
    private int _rank; // The rank of the card will be its value. As J=11,Q=12,K=13,A=14 - as the ace is the trump card.
    private char _suit; // The suit does not affect our card in a war.
    private String _display;

    public int get_rank() { return _rank; }

    public String get_display() {
        return _display;
    }

    public void print() {
        System.out.println((_display));
    }

    Card(int r, char s){
        this._rank = r;
        this._suit = s;
        if (this._rank < 11)
            this._display = Integer.toString(this._rank) + this._suit;
        else if (this._rank == 11)
            this._display = "J" + this._suit;
        else if (this._rank == 12)
            this._display = "Q" + this._suit;
        else if (this._rank == 13)
            this._display = "K" + this._suit;
        else if (this._rank == 14)
            this._display = "A" + this._suit;
    }

    public static void main(String[] args) {
        String ex = "MEsiba\nSabich";
        System.out.println(ex);
        String ne = ex.replace('\n', '#');
        System.out.println(ne);
        String poop = ne.replace('#','\n');
        System.out.println(poop);
    }
}
