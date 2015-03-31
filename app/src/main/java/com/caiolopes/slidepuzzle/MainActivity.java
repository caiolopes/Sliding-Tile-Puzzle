package com.caiolopes.slidepuzzle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.caiolopes.slidepuzzle.model.Board;
import com.caiolopes.slidepuzzle.model.Place;
import com.caiolopes.slidepuzzle.R;

/**
 * The Class MainActivity.
 * 
 * @see BoardView
 * @see Board
 * @author Caio Lopes
 * @version 1.0 $
 */
public class MainActivity extends ActionBarActivity {

	/** The main view. */
	private ViewGroup mainView;

	/** The game board. */
	private Board board;

	/** The board view that generates the tiles and lines using 2-D graphics. */
	private BoardView boardView;

	/** Text view to show the user the number of movements. */
	private TextView moves;

	/** The board size. Default value is an 4x4 game. */
	private int boardSize = 4;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainView = (ViewGroup) findViewById(R.id.mainLayout);
		moves = (TextView) findViewById(R.id.moves);
		moves.setTextColor(Color.WHITE);
		moves.setTextSize(20);
		this.newGame();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Generates a new game.
	 */
	private void newGame() {
		this.board = new Board(this.boardSize);
		this.board.addBoardChangeListener(boardChangeListener);
		this.board.rearrange();
		this.mainView.removeView(boardView);
		this.boardView = new BoardView(this, board);
		this.mainView.addView(boardView);
		this.moves.setText("Number of movements: 0");
	}

	/**
	 * Changes the size of the board
	 *
	 * @param newSize
	 */
	public void changeSize(int newSize) {
		if (newSize != this.boardSize) {
			this.boardSize = newSize;
			this.newGame();
			boardView.invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			FragmentManager fm = getSupportFragmentManager();
			SettingsDialogFragment settings = new SettingsDialogFragment(
					this.boardSize);
			settings.show(fm, "fragment_settings");
			break;
		case R.id.action_new_game:
			new AlertDialog.Builder(this)
					.setTitle("New Game")
					.setMessage("Are you sure you want to begin a new game?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									board.rearrange();
									moves.setText("Number of movements: 0");
									boardView.invalidate();
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
			break;
		case R.id.action_help:
			new AlertDialog.Builder(this)
					.setTitle("Instructions")
					.setMessage(
							"The goal of the puzzle is to place the tiles in order by making sliding moves that use the empty space. The only valid moves are to move a tile which is immediately adjacent to the blank into the location of the blank.")
					.setPositiveButton("Understood. Let's play!",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/** The board change listener. */
	private Board.BoardChangeListener boardChangeListener = new Board.BoardChangeListener() {
		public void tileSlid(Place from, Place to, int numOfMoves) {
			moves.setText("Number of movements: "
					+ Integer.toString(numOfMoves));
		}

		public void solved(int numOfMoves) {
			moves.setText("Solved in " + Integer.toString(numOfMoves)
					+ " moves!");
			Toast.makeText(getApplicationContext(), "You won!",
					Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * The Class SettingsDialogFragment. Shows the settings alert dialog in
	 * order to change the size of the board.
	 */
	public class SettingsDialogFragment extends DialogFragment {

		/** The size. */
		private int size;

		/**
		 * Instantiates a new settings dialog fragment.
		 *
		 * @param size
		 *            the size
		 */
		public SettingsDialogFragment(int size) {
			this.size = size;
		}

		/**
		 * Sets the size.
		 *
		 * @param size
		 *            the new size
		 */
		void setSize(int size) {
			this.size = size;
		}

		/**
		 * Gets the size.
		 *
		 * @return the size
		 */
		int getSize() {
			return this.size;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle
		 * )
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Set the dialog title
			builder.setTitle("Define the size of the puzzle")
					.setSingleChoiceItems(R.array.size_options, this.size - 2,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									setSize(which + 2);

								}

							})
					.setPositiveButton("Change",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									((MainActivity) getActivity())
											.changeSize(getSize());
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			return builder.create();
		}
	}
}
