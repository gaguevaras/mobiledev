package edu.mojito.tictactoe;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import edu.mojito.tictactoe.ColorPickerDialog.OnColorChangedListener;

public class Settings extends PreferenceActivity implements
		OnColorChangedListener {
	public static final String SOUND_KEY = "sound";
	public static final String DIFFICULTY_KEY = "difficulty";
	protected static final String VICTORY_MESSAGE_KEY = "victory_message";
	public static final String BOARD_COLOR_KEY = "board_color";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		final ListPreference difficultyLevelPref = (ListPreference) findPreference(DIFFICULTY_KEY);
		String difficulty = prefs.getString(DIFFICULTY_KEY, getResources()
				.getString(R.string.difficulty_expert));
		difficultyLevelPref.setSummary((CharSequence) difficulty);
		difficultyLevelPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						difficultyLevelPref.setSummary((CharSequence) newValue);

						// Since we are handling the pref, we must save it
						SharedPreferences.Editor ed = prefs.edit();
						ed.putString(DIFFICULTY_KEY, newValue.toString());
						ed.commit();

						return true;
					}
				});

		final EditTextPreference victoryMessagePref = (EditTextPreference) findPreference("victory_message");
		String victoryMessage = prefs.getString("victory_message",
				getResources().getString(R.string.result_human_wins));
		victoryMessagePref.setSummary((CharSequence) victoryMessage);
		victoryMessagePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						difficultyLevelPref.setSummary((CharSequence) newValue);

						// Since we are handling the pref, we must save it
						SharedPreferences.Editor ed = prefs.edit();
						ed.putString(VICTORY_MESSAGE_KEY, newValue.toString());
						ed.commit();

						return true;
					}
				});
		final Preference boardColorPref = (Preference) findPreference(BOARD_COLOR_KEY);
		
		boardColorPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						int color = prefs.getInt(BOARD_COLOR_KEY, Color.GRAY);
						new ColorPickerDialog(Settings.this, Settings.this,
								color).show();
						return true;
					}
				});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null; // = new ColorPickerDialog();
		return dialog;
	}

	@Override
	public void colorChanged(int color) {
		PreferenceManager.getDefaultSharedPreferences(this).edit()
				.putInt(BOARD_COLOR_KEY, color).commit();

	}
}
