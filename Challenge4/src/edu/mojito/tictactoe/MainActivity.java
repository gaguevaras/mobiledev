package edu.mojito.tictactoe;

import edu.mojito.tictactoe.TicTacToeGame.DifficultyLevel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int DIALOG_DIFFICULTY_ID = 0;

	private static final int DIALOG_QUIT = 1;

	private static final int DIALOG_ABOUT = 2;

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

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
		case DIALOG_DIFFICULTY_ID:

			builder.setTitle(R.string.difficulty_choose);

			final CharSequence[] levels = {
					getResources().getString(R.string.difficulty_easy),
					getResources().getString(R.string.difficulty_hard),
					(CharSequence) getResources().getString(
							R.string.difficulty_expert) };

			int selected = 0;
			switch (mGame.getmDifficultyLevel()) {
			case Easy:
				selected = 0;
				break;

			case Hard:
				selected = 1;
				break;

			case Expert:
				selected = 2;
				break;
			}

			builder.setSingleChoiceItems(levels, selected,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();

							switch (which) {
							case 0:
								mGame.setmDifficultyLevel(DifficultyLevel.Easy);
								break;
							case 1:
								mGame.setmDifficultyLevel(DifficultyLevel.Hard);
								break;
							case 2:
								mGame.setmDifficultyLevel(DifficultyLevel.Expert);
								break;

							default:
								break;
							}

							Toast.makeText(getApplicationContext(),
									levels[which], Toast.LENGTH_SHORT).show();

						}
					});
			dialog = builder.create();

			break;

		case DIALOG_QUIT:
			builder.setMessage(R.string.quit_question)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.this.finish();

								}
							}).setNegativeButton(R.string.no, null);
			dialog = builder.create();

			break;

		case DIALOG_ABOUT:

			AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
			Context context = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.about_dialog, null);
			aboutBuilder.setView(layout);
			aboutBuilder.setPositiveButton("OK", null);
			dialog = aboutBuilder.create();
		}

		return dialog;

	}

	private void updateScores() {
		mPlayer1Score.setText("" + p1Score);
		mPlayer2Score.setText("" + p2Score);
		mTiesScore.setText("" + tiesScore);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			startNewGame();
			return true;

		case R.id.ai_difficulty:
			showDialog(DIALOG_DIFFICULTY_ID);
			return true;

		case R.id.quit:
			showDialog(DIALOG_QUIT);
			return true;
			
		case R.id.about:
			showDialog(DIALOG_ABOUT);
			return true;

		default:
			break;
		}
		return false;
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
