package com.caiolopes.slidepuzzle;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import com.caiolopes.slidepuzzle.model.Board;
import com.caiolopes.slidepuzzle.model.Place;
import com.caiolopes.slidepuzzle.R;

/**
 * The Class BoardView. It uses 2-D graphics to display the puzzle board.
 * 
 * @author Caio Lopes
 * @version 1.0 $
 */
public class BoardView extends View {

	/** The board. */
	private Board board;

	/** The width. */
	private float width;

	/** The height. */
	private float height;

	/**
	 * Instantiates a new board view.
	 *
	 * @param context
	 *            the context
	 * @param board
	 *            the board
	 */
	public BoardView(Context context, Board board) {
		super(context);
		this.board = board;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.width = w / this.board.size();
		this.height = h / this.board.size();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Locate place.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the place
	 */
	private Place locatePlace(float x, float y) {
		int ix = (int) (x / width);
		int iy = (int) (y / height);

		return board.at(ix + 1, iy + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		Place p = locatePlace(event.getX(), event.getY());
		if (p != null && p.slidable() && !board.solved()) {
			p.slide();
			invalidate();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.board_color));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.tile_color));
		dark.setStrokeWidth(15);

		// Draw the major grid lines
		for (int i = 0; i < this.board.size(); i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, dark);
			canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
		}

		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.tile_color));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);

		float x = width / 2;
		FontMetrics fm = foreground.getFontMetrics();
		float y = (height / 2) - (fm.ascent + fm.descent) / 2;

		Iterator<Place> it = board.places().iterator();
		for (int i = 0; i < board.size(); i++) {
			for (int j = 0; j < board.size(); j++) {
				if (it.hasNext()) {
					Place p = it.next();
					if (p.hasTile()) {
						String number = Integer.toString(p.getTile().number());
						canvas.drawText(number, i * width + x, j * height + y,
								foreground);
					} else {
						canvas.drawRect(i * width, j * height, i * width
								+ width, j * height + height, dark);
					}
				}
			}
		}
	}
}
