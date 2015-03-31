// $Id: Tile.java,v 1.1 2014/12/01 00:33:19 cheon Exp $

package com.caiolopes.slidepuzzle.model;

/** A tile of the puzzle. Each tile has a (unique) number. */
public class Tile {

    /** The number of this tile. */
    private final int number;
    
    /** Create a new tile that has the given number */
    public Tile(int number) {
        this.number = number;
    }

    /** Return the number of this tile. */
    public int number() {
        return number;
    }
}
