package co.mojito.crud;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import co.mojito.crud.helper.DatabaseHelper;
import co.mojito.crud.model.Group;
import co.mojito.crud.model.Student;

public class StudentFormActivity extends Activity {

	private static final String TAG = "StudentFormActivity";

	private int mode;

	//
	// Identificador del registro que se edita cuando la opción es MODIFICAR
	//
	private Student student;

	//
	// Elementos de la vista
	//
	private EditText name;
	private EditText email;
	private EditText phoneNumber;
	private EditText companyName;
	private Spinner group;

	private Button deleteBtn;

	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_form);

		Intent intent = getIntent();
		Bundle extra = intent.getExtras();

		if (extra == null)
			return;

		// Get elements from the view
		name = (EditText) findViewById(R.id.student_name);
		email = (EditText) findViewById(R.id.student_email);
		phoneNumber = (EditText) findViewById(R.id.student_phone_number);
		companyName = (EditText) findViewById(R.id.student_company_name);
		group = (Spinner) findViewById(R.id.spinner_groups);

		if (intent.getExtras().getInt(MainActivity.MODE) == MainActivity.MODE_EDIT) {
			deleteBtn = (Button) findViewById(R.id.delete_button);
			deleteBtn.setEnabled(true);
		}

		name.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					student.setName(v.toString());

					return true;
				}

				return false;
			}
		});
		db = new DatabaseHelper(getApplicationContext());

		//
		// Obtenemos el identificador del registro si viene indicado
		//
		if (extra.containsKey(DatabaseHelper.KEY_ID)) {

			student = db.getStudent(extra.getInt(DatabaseHelper.KEY_ID));
			updateFields(student);
		}

		List<Group> list = db.getAllGroups();
		Log.d(TAG, "Group count: " + list.size());

		ArrayAdapter<Group> dataAdapter = new ArrayAdapter<Group>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		group.setAdapter(dataAdapter);

		//
		// Establecemos el mode del formulario
		//
		setMode(extra.getInt(MainActivity.MODE));

		db.closeDB();

	}

	private void setMode(int m) {
		this.mode = m;

		if (mode == MainActivity.MODE_CREATE) {
			this.setTitle(R.string.new_student);
			this.setEditable(true);
		}
	}

	private void updateFields(Student student) {

		name.setText(student.getName());
		email.setText(student.getEmail());
		phoneNumber.setText(student.getPhoneNumber());
		companyName.setText(student.getCompanyName());

	}

	public void saveStudent(View view) {
		if (student == null) {

			student = new Student();
			student.setName(name.getText().toString());
			student.setCompanyName(companyName.getText().toString());
			student.setEmail(email.getText().toString());
			student.setPhoneNumber(phoneNumber.getText().toString());

			// This shit can be fixed by implementing GroupAdapter properly
			// instead of using ArrayAdapter<Group>
			long studentId = db.createStudent(student,
					new long[] { ((Group) group.getSelectedItem()).getId() });

			Log.d(TAG, "Student created with id: " + studentId);
			setResult(RESULT_OK);
			this.finish();

		} else {
			student.setName(name.getText().toString());
			student.setCompanyName(companyName.getText().toString());
			student.setEmail(email.getText().toString());
			student.setPhoneNumber(phoneNumber.getText().toString());

			// update the selected student
			long studentId = db.updateStudent(student);

			Log.d(TAG, "Student edited with id: " + studentId);
			setResult(RESULT_OK);
			this.finish();
		}
		db.closeDB();

		this.finish();

	}

	public void cancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void deleteStudent(View view) {

		if (student != null) {

			// update the selected student
			db.deleteStudent(student.getId());

			Log.d(TAG, "Student deleted with id: " + student.getId());
			setResult(MainActivity.STUDENT_DELETED);
			db.closeDB();
			this.finish();
		}

		this.finish();

	}

	private void setEditable(boolean editable) {
		name.setEnabled(editable);
		email.setEnabled(editable);
		phoneNumber.setEnabled(editable);
		companyName.setEnabled(editable);
		email.setEnabled(editable);

	}

}
