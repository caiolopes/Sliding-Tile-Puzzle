# Sliding-Tile-Puzzle
Android application that allows one to play a "15-puzzle" game.

The famous fifteen puzzle is a sliding puzzle that consists of a frame of numbered square
tiles in random order with one tile missing (from http://en.wikipedia.org/). 

The goal of the puzzle is to place the tiles in order by making sliding moves that use the
empty space (see below). The only valid moves are to move a tile which
is immediately adjacent to the blank into the location of the blank.

It provides an arbitrary, solvable (but not solved) starting arrangement of tiles.

It allows a user to play the game from the provided starting arrangement of 
tiles by sliding tiles one at a time into the configuration shown above.

It keeps track of the number of moves made by the player.

It allows a user to play a new game (or shuffle the tiles). However, if a game is already in progress, 
it uses an alert dialog to get a confirmation from the user.

It uses 2-D graphics to display the puzzle board; no use of button is allowed for displaying puzzle tiles.

It uses the Model-View-Control (MVC) metaphor.

It is reconfigurable so that you can change the size of the puzzle easily.
