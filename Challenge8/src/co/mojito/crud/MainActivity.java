package co.mojito.crud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import co.mojito.crud.adapter.GroupAdapter;
import co.mojito.crud.adapter.StudentAdapter;
import co.mojito.crud.helper.DatabaseHelper;
import co.mojito.crud.model.Group;
import co.mojito.crud.model.Student;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	protected static final String MODE = "mode";
	protected static final int MODE_CREATE = 321;
	protected static final int MODE_EDIT = 322;

	protected static final int CREATE_STUDENT_CODE = 0;
	protected static final int EDIT_STUDENT_CODE = 1;
	protected static final int DELETE_STUDENT_CODE = 2;
	protected static final int CREATE_GROUP_CODE = 3;
	protected static final int EDIT_GROUP_CODE = 4;
	protected static final int DELETE_GROUP_CODE = 5;

	protected static final int STUDENT_EDITED = 11;
	protected static final int STUDENT_DELETED = 22;
	protected static final int GROUP_EDITED = 33;
	protected static final int GROUP_DELETED = 44;

	protected static final int CREATE_GROUP_FAILED = 55;
	private ListView listView;
	private ArrayAdapter<?> adapter;
	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_activity_layout);

		db = new DatabaseHelper(getBaseContext());

		Toast.makeText(getBaseContext(), "Database prepared.", Toast.LENGTH_LONG).show();

		// Group group = new Group("ControlZeta");
		// long group_id = db.createGroup(group);

		Log.d("Group Count", "Group Count: " + db.getAllGroups().size());

		// Student student = new Student("Gustavo", "3015866282", "ControlZeta",
		// "gustavo.andres.guevara@gmail.com", 0);
		// db.createStudent(student, new long[] { group_id });

		Log.e("Student Count", "Student count: " + db.getStudentCount());

		listView = (ListView) findViewById(android.R.id.list);
		refreshGroupList();

		db.closeDB();

		handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.d(TAG, "Searching using query: " + query);

			adapter = new StudentAdapter(getBaseContext(), db.searchStudentsByName(query));
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new StudentClickListener());
			db.closeDB();

		}

	}

	private void refreshGroupList() {

		adapter = new GroupAdapter(getBaseContext(), db.getAllGroups());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new GroupClickListener());
		db.closeDB();

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_settings:
				// TODO: Settings for the app.
				break;

			case R.id.show_all_students:
				refreshStudentList();
				break;

			case R.id.show_all_groups:
				refreshGroupList();
				break;

			case R.id.search:
				onSearchRequested();
				return true;
		}
		return false;
	}

	private void refreshStudentList() {
		adapter = new StudentAdapter(getBaseContext(), db.getAllStudents());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new StudentClickListener());
		db.closeDB();
	}

	protected void editStudent(Student student) {
		Intent intent = new Intent(getApplicationContext(), StudentFormActivity.class);
		intent.putExtra(MODE, MODE_EDIT);
		intent.putExtra(DatabaseHelper.KEY_ID, student.getId());
		Log.d(TAG, "Editing student with Id: " + student.getId());
		startActivityForResult(intent, EDIT_STUDENT_CODE);

	}

	public void createStudent(View view) {
		Intent intent = new Intent(getApplicationContext(), StudentFormActivity.class);
		intent.putExtra(MODE, MODE_CREATE);
		startActivityForResult(intent, CREATE_STUDENT_CODE);
	}

	public void createGroup(View view) {
		Intent intent = new Intent(getApplicationContext(), GroupFormActivity.class);
		intent.putExtra(MODE, MODE_CREATE);
		startActivityForResult(intent, CREATE_GROUP_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CREATE_STUDENT_CODE) {
			if (resultCode == RESULT_OK) {

				Toast.makeText(this, R.string.student_created, Toast.LENGTH_SHORT).show();
				refreshStudentList();

			} else if (resultCode == STUDENT_DELETED) {

				Toast.makeText(this, R.string.student_deleted_, Toast.LENGTH_SHORT).show();
				refreshStudentList();

			}

			refreshStudentList();

		} else if (requestCode == EDIT_STUDENT_CODE) {
			if (resultCode == RESULT_OK) {

				Toast.makeText(this, R.string.student_edited_, Toast.LENGTH_SHORT).show();
				refreshStudentList();

			} else if (resultCode == STUDENT_DELETED) {

				Toast.makeText(this, R.string.student_deleted_, Toast.LENGTH_SHORT).show();
				refreshStudentList();

			}

			refreshStudentList();

		} else if (requestCode == CREATE_GROUP_CODE) {
			if (resultCode == RESULT_OK) {

				Toast.makeText(this, R.string.group_created_, Toast.LENGTH_SHORT).show();
				refreshGroupList();

			} else if (resultCode == GROUP_DELETED) {

				Toast.makeText(this, R.string.group_deleted_, Toast.LENGTH_SHORT).show();
				refreshGroupList();
			}

			refreshGroupList();

		} else if (requestCode == EDIT_GROUP_CODE) {
			if (resultCode == RESULT_OK) {

				Toast.makeText(this, R.string.group_edited_, Toast.LENGTH_SHORT).show();
				refreshGroupList();
			} else if (resultCode == GROUP_DELETED) {

				Toast.makeText(this, R.string.group_deleted_, Toast.LENGTH_SHORT).show();
				refreshGroupList();
			}

			refreshGroupList();
		}
	}

	private class StudentClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Toast.makeText(getApplicationContext(),
					"Item selected: " + ((Student) listView.getItemAtPosition(position)).getName(),
					Toast.LENGTH_SHORT).show();

			editStudent((Student) listView.getItemAtPosition(position));

		}
	}

	private class GroupClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Toast.makeText(
					getApplicationContext(),
					"Item selected: "
							+ ((Group) listView.getItemAtPosition(position)).getGroupName(),
					Toast.LENGTH_SHORT).show();

			editGroup((Group) listView.getItemAtPosition(position));

		}
	}

	public void editGroup(Group group) {
		Intent intent = new Intent(getApplicationContext(), GroupFormActivity.class);
		intent.putExtra(MODE, MODE_EDIT);
		intent.putExtra(DatabaseHelper.KEY_ID, group.getId());
		Log.d(TAG, "Editing group with Id: " + group.getId());
		startActivityForResult(intent, EDIT_GROUP_CODE);
	}
}
