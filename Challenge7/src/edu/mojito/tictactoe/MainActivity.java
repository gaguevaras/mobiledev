package edu.mojito.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int DIALOG_QUIT = 1;
	private static final int DIALOG_ABOUT = 2;
	private static final int DIALOG_RESET = 3;

	// Represents the internal state of the game
	private TicTacToeGame mGame;

	private boolean mGameOver = false;
	private boolean playerFirst = true;

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

	private BoardView mBoardView;

	public MediaPlayer mHumanMediaPlayer;
	public MediaPlayer mComputerMediaPlayer;

	public boolean playerMove = true;

	private SharedPreferences mPrefs;
	private boolean mSoundOn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Board
		mBoardView = (BoardView) findViewById(R.id.board);
		// Assign a game state to the Board
		mGame = new TicTacToeGame();
		mBoardView.setmGame(mGame);

		// Add touch listener to the board
		mBoardView.setOnTouchListener(mTouchListener);

		mInfoTextView = (TextView) findViewById(R.id.information);

		// Scores section
		mPlayer1Name = (TextView) findViewById(R.id.player1_name);
		mPlayer1Score = (TextView) findViewById(R.id.player1_score);
		mPlayer2Name = (TextView) findViewById(R.id.player2_name);
		mPlayer2Score = (TextView) findViewById(R.id.player2_score);
		mTiesLabel = (TextView) findViewById(R.id.ties_label);
		mTiesScore = (TextView) findViewById(R.id.ties_score);

		// Set label values:
		mPlayer1Name.setText("You: ");
		mPlayer2Name.setText("Me: ");
		mTiesLabel.setText("Ties: ");

		// Restore the scores from the persistent preference data source
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Restore values
		mSoundOn = mPrefs.getBoolean(Settings.SOUND_KEY, true);
		p1Score = mPrefs.getInt("p1Score", 0);
		p2Score = mPrefs.getInt("p2Score", 0);
		tiesScore = mPrefs.getInt("tiesScore", 0);
		mBoardView.setBoardColor(mPrefs.getInt(Settings.BOARD_COLOR_KEY, Color.GRAY));

		String difficultyLevel = mPrefs.getString(Settings.DIFFICULTY_KEY,
				getString(R.string.difficulty_hard));

		if (difficultyLevel.equals(getResources().getString(
				R.string.difficulty_easy)))
			mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
		else if (difficultyLevel.equals(getResources().getString(
				R.string.difficulty_hard)))
			mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Hard);
		else
			mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

		updateScores();
		startNewGame();

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		mGame.setBoardState(savedInstanceState.getCharArray("board"));
		mGameOver = savedInstanceState.getBoolean("mGameOver");
		mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
		p1Score = savedInstanceState.getInt("p1Score");
		p2Score = savedInstanceState.getInt("p2Score");
		tiesScore = savedInstanceState.getInt("tiesScore");
		playerFirst = savedInstanceState.getBoolean("playerFirst");
		playerMove = savedInstanceState.getBoolean("playerMove");

		if (!playerMove) {
			mInfoTextView.setText(R.string.computer_turn);
			Handler handler = new Handler();

			handler.postDelayed(runnableHandler, 500);
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {
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

		case DIALOG_RESET:

			p1Score = p2Score = tiesScore = 0;
			updateScores();
		}

		return dialog;

	}

	private void updateScores() {

		mPlayer1Score.setText(Integer.toString(p1Score));
		mPlayer2Score.setText(Integer.toString(p2Score));
		mTiesScore.setText(Integer.toString(tiesScore));

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

		case R.id.settings:
			startActivityForResult(new Intent(this, Settings.class), 0);
			return true;

		case R.id.quit:
			showDialog(DIALOG_QUIT);
			return true;

		case R.id.about:
			showDialog(DIALOG_ABOUT);
			return true;
		case R.id.reset_scores:
			showDialog(DIALOG_RESET);
			return true;

		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULT_CANCELED) {
			mSoundOn = mPrefs.getBoolean("sound", true);
			String difficultyLevel = mPrefs.getString("difficulty_level",
					getResources().getString(R.string.difficulty_hard));
			if (difficultyLevel.equals(getResources().getString(
					R.string.difficulty_easy)))
				mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
			else if (difficultyLevel.equals(getResources().getString(
					R.string.difficulty_hard)))
				mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Hard);
			else {
				mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
			}
		}
	 	mBoardView.setBoardColor(mPrefs.getInt(Settings.BOARD_COLOR_KEY, Color.GRAY));
	 	mBoardView.invalidate();
	}

	private void startNewGame() {
		mGameOver = false;
		mGame.clearBoard();

		// redraw board
		mBoardView.invalidate();
		updateScores();

		if (playerFirst) {

			mInfoTextView.setText("You go first.");
			playerFirst = false;

		} else {

			mInfoTextView.setText("I'll go first.");
			int move = mGame.getComputerMove();
			setMove(TicTacToeGame.COMPUTER_PLAYER, move);
			playerFirst = true;
			playerMove = true;

		}

	}

	private void setMove(char player, int location) {
		if (player == TicTacToeGame.HUMAN_PLAYER) {

			if (mSoundOn) {

				try {
					mHumanMediaPlayer.start();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} else if (player == TicTacToeGame.COMPUTER_PLAYER) {

			if (mSoundOn) {

				try {
					mComputerMediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		mGame.setMove(player, location);
		mBoardView.invalidate();

	}

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			// Determine which cell was touched
			int col = (int) event.getX() / mBoardView.getCellWidth();
			int row = (int) event.getY() / mBoardView.getCellHeight();
			int pos = row * 3 + col;

			if (playerMove) {

				if (!mGameOver
						&& mGame.getBoardOccupant(pos) == TicTacToeGame.OPEN_SPOT) {
					setMove(TicTacToeGame.HUMAN_PLAYER, pos);
					playerMove = false;
					mInfoTextView.setText(R.string.computer_turn);
					Handler handler = new Handler();

					handler.postDelayed(runnableHandler, 1000);

				} else {
					mInfoTextView.setText("Game is over.");
				}
			}
			updateScores();

			return false;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.snap);
		mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.drop);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mHumanMediaPlayer.release();
		mComputerMediaPlayer.release();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putCharArray("board", mGame.getBoardState());
		outState.putBoolean("mGameOver", mGameOver);
		outState.putInt("p1Score", p1Score);
		outState.putInt("p2Score", p2Score);
		outState.putInt("tiesScore", tiesScore);
		outState.putCharSequence("info", mInfoTextView.getText());
		outState.putBoolean("playerFirst", playerFirst);
		outState.putBoolean("playerMove", playerMove);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Save the current scores
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putInt("p1Score", p1Score);
		ed.putInt("p2Score", p2Score);
		ed.putInt("tiesScore", tiesScore);
		String difficultyLevel = difficultyLevel = getResources().getString(
				R.string.difficulty_easy);
		switch (mGame.getmDifficultyLevel()) {
		case Easy:
			difficultyLevel = getResources()
					.getString(R.string.difficulty_easy);
			break;
		case Hard:
			difficultyLevel = getResources()
					.getString(R.string.difficulty_hard);
			break;
		case Expert:
			difficultyLevel = getResources().getString(
					R.string.difficulty_expert);
			break;
		}
		ed.putString(Settings.DIFFICULTY_KEY, difficultyLevel);
		ed.commit();
	}

	Runnable runnableHandler = new Runnable() {

		@Override
		public void run() {
			int winner = mGame.checkForWinner();
			mInfoTextView.setText(R.string.computer_turn);

			if (winner == 0) {

				int move = mGame.getComputerMove();
				setMove(TicTacToeGame.COMPUTER_PLAYER, move);
				playerMove = true;

				winner = mGame.checkForWinner();

			}

			if (winner == TicTacToeGame.NO_WINNER) {

				mInfoTextView.setText(R.string.human_turn);

			} else if (winner == TicTacToeGame.TIE) {

				mInfoTextView.setText(R.string.result_tie);
				tiesScore++;
				updateScores();

			} else if (winner == TicTacToeGame.HUMAN_WIN) {

				String defaultMessage = getResources().getString(
						R.string.result_human_wins);
				mInfoTextView.setText(mPrefs.getString("victory_message",
						defaultMessage));
				mGameOver = true;
				p1Score++;
				updateScores();

			} else {

				mInfoTextView.setText(R.string.result_computer_wins);
				mGameOver = true;
				p2Score++;
				updateScores();

			}
		}
	};

}
