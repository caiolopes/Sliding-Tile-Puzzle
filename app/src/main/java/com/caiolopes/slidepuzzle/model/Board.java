// $Id: Board.java,v 1.1 2014/12/01 00:33:19 cheon Exp $

package com.caiolopes.slidepuzzle.model;

import java.util.*;

/**
 * A puzzle frame consisting of <code>size</code> * <code>size</code>
 * places where puzzle tiles can be placed. 
 *
 * @see Place
 * @see Tile
 */
public class Board {

    /** Dimension of this board. This board will have 
     *  <code>size</code> * <code>size</code> places. */
    private final int size;

    /** Number of tile moves made so far. */
    private int numOfMoves;

    /** Places of this board. */
    private final List<Place> places;
    
    /** Listeners listening to board changes such as sliding of tiles. */
    private final List<BoardChangeListener> listeners;
    
    /** To arrange tiles randomly. */
    private final static Random random = new Random();

    /** Create a new board of the given dimension. Initially, the tiles
     * are ordered with the blank tile as the last tile. */
    public Board(int size) {
    	listeners = new ArrayList<BoardChangeListener>();
        this.size = size;
        places = new ArrayList<Place>(size * size);
        for (int x = 1; x <= size; x++) {
            for (int y = 1; y <= size; y++) {
                places.add(x == size && y == size ?
                           new Place(x, y, this)
                           : new Place(x, y, (y - 1)* size + x, this));
            }
        }
        numOfMoves = 0;
    }

    /** Rearrange the tiles to create a new, solvable puzzle. */
    public void rearrange() {
        numOfMoves = 0;
        for (int i = 0; i < size*size; i++) {
            swapTiles();
        }
        do { 
            swapTiles();
        } while (!solvable() || solved());
    }

    /** Swap two tiles randomly. */
    private void swapTiles() {
        Place p1 = at(random.nextInt(size) + 1, random.nextInt(size) + 1);
        Place p2 = at(random.nextInt(size) + 1, random.nextInt(size) + 1);
        if (p1 != p2) {
            Tile t = p1.getTile();
            p1.setTile(p2.getTile());
            p2.setTile(t);
        }
    }

    /** Is the puzzle (current arrangement of tiles) solvable? */
    private boolean solvable() {
        // alg. from: http://www.cs.bham.ac.uk/~mdr/teaching/modules04/
        //                 java2/TilesSolvability.html
    	//
        // count the number of inversions, where an inversion is when
        // a tile precedes another tile with a lower number on it.
        int inversion = 0;
        for (Place p: places) {
            Tile pt = p.getTile();
            for (Place q: places) {
                Tile qt = q.getTile();
                if (p != q && pt != null && qt != null &&
                    indexOf(p) < indexOf(q) &&
                    pt.number() > qt.number()) {
                    inversion++;
                }                    
            }
        }
        final boolean isEvenSize = size % 2 == 0;
        final boolean isEvenInversion = inversion % 2 == 0;
        boolean isBlankOnOddRow = blank().getY() % 2 == 1;
        // from the bottom
        isBlankOnOddRow = isEvenSize ? !isBlankOnOddRow : isBlankOnOddRow;
        return (!isEvenSize && isEvenInversion) ||
            (isEvenSize && isBlankOnOddRow == isEvenInversion);
    }

    /** Return the 1-based index of the given place when all the places
     * are arranged in row-major order. */
    private int indexOf(Place p) {
        return (p.getY() - 1) * size + p.getX();
        
    }

   /** Is this puzzle solved? */
    public boolean solved() {
        boolean result = true;
        for (Place p: places) {
            result = result &&
                ((p.getX() == size && p.getY() == size) ||
                 (p.getTile() != null && 
                  p.getTile().number() == indexOf(p)));
        }
        return result;
    }

    /** Slide the given tile, which is assumed to be slidable, and
     * notify the change to registered board change listeners, if any.
     *  
     * @see Board#slidable(Place) */
    public void slide(Tile tile) {
        for (Place p: places) {
            if (p.getTile() == tile) {
            	final Place to = blank();
                to.setTile(tile);
                p.setTile(null);
                numOfMoves++;
                notifyTileSliding(p, to, numOfMoves);
                if (solved()) {
                	notifyPuzzleSolved(numOfMoves);
                }
                return;
            }
        }
    }
   
    /** Is the tile in the given place slidable? */
    public boolean slidable(Place place) {
    	int x = place.getX();
    	int y = place.getY();
    	return isBlank(x - 1, y) || isBlank(x + 1, y)
            || isBlank(x, y - 1) || isBlank(x, y + 1);
    }

    /** Is the place at the given indices empty? */
    private boolean isBlank(int x, int y) {
        return (0 < x && x <= size) && (0 < y && y <= size)
            && at(x,y).getTile() == null;
    }

    /** Return the blank place. */
    public Place blank() {
        for (Place p: places) {
            if (p.getTile() == null) {
                return p;
            }
        }
        //assert false : "should never reach here!";
        return null; 
    }

    /** Return all the places of this board. */
    public Iterable<Place> places() {
        return places;
    }

    /** Return the place at the given indices. */
    public Place at(int x, int y) {
        for (Place p: places) {
            if (p.getX() == x && p.getY() == y) {
                return p;
            }
        }
        //assert false : "precondition violation!";
        return null; 
    }

    /** Return the dimension of this board. */
    public int size() {
        return size;
    }

    /** Return the number of tile moves made so far. */
    public int numOfMoves() {
        return numOfMoves;
    }

    /** Register the given listener to listen to board changes. */
    public void addBoardChangeListener(BoardChangeListener listener) {
    	if (!listeners.contains(listener)) {
    		listeners.add(listener);
    	}
    }
    
    /** Unregister the given listener from listening to board changes. */
    public void removeBoardChangeListener(BoardChangeListener listener) {
		listeners.remove(listener);
    }
    
    /** Notify a tile sliding to registered board change listeners. */
    private void notifyTileSliding(Place from, Place to, int numOfMove) {
    	for (BoardChangeListener listener: listeners) {
    		listener.tileSlid(from, to, numOfMoves);
    	}
    }
    
    /** Notify solving of the puzzle to registered board change listeners. */
    private void notifyPuzzleSolved(int numOfMoves) {
    	for (BoardChangeListener listener: listeners) {
    		listener.solved(numOfMoves);
    	}
    }
    
    /** To listen to board changes such as tile sliding. */
    public interface BoardChangeListener {
    	
    	/** Called when the tile located at the <code>from</code>
    	 * place was slid to the empty <code>to</code> place. Both places
    	 * will be provided in new states; i.e., <code>from</code> will
    	 * be empty and <code>to</code> will be the tile moved. */
    	void tileSlid(Place from, Place to, int numOfMoves);
    	
    	/** Called when the puzzle is solved. The number of tile moves
    	 * is provided as the argument. */
    	void solved(int numOfMoves);
    }
    
}
