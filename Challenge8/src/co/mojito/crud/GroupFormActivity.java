package co.mojito.crud;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.mojito.crud.helper.DatabaseHelper;
import co.mojito.crud.model.Group;

public class GroupFormActivity extends Activity {

	private static final String TAG = "GroupFormActivity";

	private int mode;

	//
	// Identificador del registro que se edita cuando la opción es MODIFICAR
	//
	private Group group;

	//
	// Elementos de la vista
	//
	private EditText name;

	private Button deleteBtn;

	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_form);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();

		if (extra == null)
			return;

		// Get elements from the view
		name = (EditText) findViewById(R.id.group_name);

		if (intent.getExtras().getInt(MainActivity.MODE) == MainActivity.MODE_EDIT) {
			deleteBtn = (Button) findViewById(R.id.delete_button);
			deleteBtn.setEnabled(true);
		}

		db = new DatabaseHelper(getApplicationContext());

		//
		// Obtenemos el identificador del registro si viene indicado
		//
		if (extra.containsKey(DatabaseHelper.KEY_ID)) {
			group = db.getGroup(extra.getInt(DatabaseHelper.KEY_ID));
			updateFields(group);
		}

		List<Group> list = db.getAllGroups();
		Log.d(TAG, "Group count: " + list.size());

		//
		// Establecemos el mode del formulario
		//
		setMode(extra.getInt(MainActivity.MODE));

		db.closeDB();

	}

	private void setMode(int m) {
		this.mode = m;

		if (mode == MainActivity.MODE_CREATE) {
			this.setTitle(R.string.create_new_group);
			this.setEditable(true);
		}
	}

	private void updateFields(Group group) {

		name.setText(group.getGroupName());

	}

	public void saveGroup(View view) {
		if (group == null) {

			group = new Group();
			group.setGroupName(name.getText().toString());

			// This shit can be fixed by implementing GroupAdapter properly
			// instead of using ArrayAdapter<Group>
			long groupId = db.createGroup(group);

			if (groupId != -1) {
				Log.d(TAG, "Group created with id: " + groupId);
				setResult(RESULT_OK);
				this.finish();

			} else {
				Log.d(TAG, "Could not create Group.");
				Toast.makeText(this, R.string.group_already_exists_, Toast.LENGTH_SHORT).show();
			}

		} else {
			group.setGroupName(name.getText().toString());

			// update the selected group
			long groupId = db.updateGroup(group);

			Log.d(TAG, "Group edited with id: " + groupId);
			setResult(MainActivity.GROUP_EDITED);
			this.finish();
		}
		db.closeDB();

		this.finish();

	}

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void deleteGroup(View view) {

		if (group != null) {

			// update the selected group
			db.deleteGroup(group, true);
			Log.d(TAG, "Group deleted with id: " + group.getId());
			setResult(MainActivity.STUDENT_DELETED);
			db.closeDB();
			this.finish();
		}
		db.closeDB();
		this.finish();

	}

	private void setEditable(boolean editable) {
		name.setEnabled(editable);

	}

}
