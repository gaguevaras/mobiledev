package edu.mojito.tictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Represents the internal state of the game
	private TicTacToeGame mGame;

	private boolean mGameOver = false;
	private boolean playerFirst = true;

	// Buttons constituting the board
	private Button mBoardButtons[];

	// Information text displayed
	private TextView mInfoTextView;

	// Scoreboard
	private TextView mPlayer1Name;
	private TextView mPlayer2Name;

	private TextView mPlayer1Score;
	private int p1Score = 0;
	private TextView mPlayer2Score;
	private int p2Score = 0;

	private TextView mTiesLabel;
	private TextView mTiesScore;
	private int tiesScore = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
		mBoardButtons[0] = (Button) findViewById(R.id.one);
		mBoardButtons[1] = (Button) findViewById(R.id.two);
		mBoardButtons[2] = (Button) findViewById(R.id.three);
		mBoardButtons[3] = (Button) findViewById(R.id.four);
		mBoardButtons[4] = (Button) findViewById(R.id.five);
		mBoardButtons[5] = (Button) findViewById(R.id.six);
		mBoardButtons[6] = (Button) findViewById(R.id.seven);
		mBoardButtons[7] = (Button) findViewById(R.id.eight);
		mBoardButtons[8] = (Button) findViewById(R.id.nine);

		mInfoTextView = (TextView) findViewById(R.id.information);

		mPlayer1Name = (TextView) findViewById(R.id.player1_name);
		mPlayer1Score = (TextView) findViewById(R.id.player1_score);
		mPlayer1Name.setText("You: ");

		mPlayer2Name = (TextView) findViewById(R.id.player2_name);
		mPlayer2Score = (TextView) findViewById(R.id.player2_score);
		mPlayer2Name.setText("Me: ");

		mTiesLabel = (TextView) findViewById(R.id.ties_label);
		mTiesScore = (TextView) findViewById(R.id.ties_score);
		mTiesLabel.setText("Ties: ");

		updateScores();

		mGame = new TicTacToeGame();

		startNewGame();

	}

	private void updateScores() {
		mPlayer1Score.setText("" + p1Score);
		mPlayer2Score.setText("" + p2Score);
		mTiesScore.setText("" + tiesScore);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		menu.add("New Game");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startNewGame();
		return true;
	}

	private void startNewGame() {
		mGameOver = false;
		mGame.clearBoard();

		// reset all buttons
		for (int i = 0; i < mBoardButtons.length; i++) {
			mBoardButtons[i].setText("");
			mBoardButtons[i].setEnabled(true);
			mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
		}
		if (playerFirst) {
			mInfoTextView.setText("You go first.");
			playerFirst = false;
		} else {
			mInfoTextView.setText("I'll go first.");
			int move = mGame.getComputerMove();
			setMove(TicTacToeGame.COMPUTER_PLAYER, move);
			playerFirst = true;
		}

	}

	private void setMove(char player, int location) {
		mGame.setMove(player, location);
		mBoardButtons[location].setEnabled(false);
		mBoardButtons[location].setText(String.valueOf(player));
		if (player == TicTacToeGame.HUMAN_PLAYER) {
			mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
		} else {
			mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
		}

	}

	private class ButtonClickListener implements OnClickListener {
		int location;

		public ButtonClickListener(int location) {
			this.location = location;
		}

		@Override
		public void onClick(View v) {
			if (mBoardButtons[location].isEnabled() && !mGameOver) {
				setMove(TicTacToeGame.HUMAN_PLAYER, location);

				int winner = mGame.checkForWinner();

				if (winner == 0) {
					mInfoTextView.setText("It's my turn.");
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);
					winner = mGame.checkForWinner();

				}

				if (winner == 0) {
					mInfoTextView.setText("It's your turn.");
				} else if (winner == 1) {
					mInfoTextView.setText("It's a tie!");
					tiesScore++;
				} else if (winner == 2) {
					mInfoTextView.setText("You won!");
					mGameOver = true;
					p1Score++;
				} else {
					mInfoTextView.setText("I won!");
					mGameOver = true;
					p2Score++;
				}

			} else {
				mInfoTextView.setText("Game is over.");
			}
			updateScores();
		}
	}
}
