package edu.mojito.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoardView extends View {

	// Width of the board grid lines
	public static final int GRID_WIDTH = 6;

	private Bitmap mHumanBitmap;
	private Bitmap mComputerBitmap;
	private Paint mPaint;

	private TicTacToeGame mGame;

	private int cellWidth;

	private int cellHeight;

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public BoardView(Context context) {
		super(context);
		initialize();
	}

	public void initialize() {
		mComputerBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.computer_bitmap);
		mHumanBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.human_bitmap);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int boardwidth = getWidth();
		int boardheight = getHeight();

		mPaint.setColor(Color.rgb(0, 255, 0));
		mPaint.setStrokeWidth(GRID_WIDTH);

		setCellWidth(boardwidth / 3);
		setCellHeight(boardheight / 3);

		// vertical lines
		canvas.drawLine(getCellWidth(), 0, getCellWidth(), boardheight, mPaint);
		canvas.drawLine(2 * getCellWidth(), 0, 2 * getCellWidth(), boardheight,
				mPaint);

		// horizontal lines
		canvas.drawLine(0, getCellWidth(), boardwidth, getCellWidth(), mPaint);
		canvas.drawLine(0, 2 * getCellWidth(), boardwidth, 2 * getCellWidth(),
				mPaint);

		// Draw all the X and O images
		for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
			int col = i % 3;
			int row = i / 3;
			// Define the boundaries of a destination rectangle for the image

			int left = getCellWidth() * col + GRID_WIDTH / 2 * col;
			int right = left + getCellWidth() - GRID_WIDTH / 2;

			int top = (getCellHeight() * row) + (GRID_WIDTH / 2 * row);

			int bottom = (getCellHeight() * row) + (GRID_WIDTH / 2 * row)
					+ getCellHeight();

			if (getmGame() != null
					&& getmGame().getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
				canvas.drawBitmap(mHumanBitmap, null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			} else if (getmGame() != null
					&& getmGame().getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
				canvas.drawBitmap(mComputerBitmap, null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			}
		}

	}

	public TicTacToeGame getmGame() {
		return mGame;
	}

	public void setmGame(TicTacToeGame mGame) {
		this.mGame = mGame;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}
}
