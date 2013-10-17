/**
 * 
 */
package co.mojito.crud.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import co.mojito.crud.model.Group;
import co.mojito.crud.model.Student;

/**
 * @author gustavoaguevara
 * 
 *         This class is a DatabaseHelper class. It contains the methods to
 *         perform all database operations such as opening and closing
 *         connections and a full CRUD.
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String TAG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "studentsManager";

	// Table Names
	private static final String TABLE_STUDENT = "student";
	// Can't use reserverd keyword group in SQL
	private static final String TABLE_GROUP = "class_group";
	private static final String TABLE_STUDENT_GROUP = "student_group";

	// Common column names
	public static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";

	// STUDENTS Table - column names
	private static final String KEY_NAME = "name";
	private static final String KEY_PHONE_NUMBER = "phone_number";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_COMPANY_NAME = "company_name";
	private static final String KEY_STATUS = "status";

	// GROUPS Table - column names
	private static final String KEY_GROUP_NAME = "group_name";

	// STUDENT_GROUP Table - column names
	private static final String KEY_STUDENT_ID = "student_id";
	private static final String KEY_GROUP_ID = "group_id";

	// Table Create Statements
	// student
	private static final String CREATE_TABLE_STUDENT = "CREATE TABLE " + TABLE_STUDENT + "("
			+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PHONE_NUMBER + " TEXT,"
			+ KEY_EMAIL + " TEXT," + KEY_COMPANY_NAME + " TEXT," + KEY_STATUS + " INTEGER,"
			+ KEY_CREATED_AT + " DATETIME" + ")";

	// group
	private static final String CREATE_TABLE_GROUP = "CREATE TABLE " + TABLE_GROUP + "(" + KEY_ID
			+ " INTEGER PRIMARY KEY," + KEY_GROUP_NAME + " TEXT," + KEY_CREATED_AT + " DATETIME"
			+ ")";

	// student_group
	private static final String CREATE_TABLE_STUDENT_GROUP = "CREATE TABLE " + TABLE_STUDENT_GROUP
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_STUDENT_ID + " INTEGER," + KEY_GROUP_ID
			+ " INTEGER," + KEY_CREATED_AT + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// Create the tables
		db.execSQL(CREATE_TABLE_STUDENT);
		db.execSQL(CREATE_TABLE_GROUP);
		db.execSQL(CREATE_TABLE_STUDENT_GROUP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables.
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_GROUP);

		// Recreate.
		onCreate(db);
	}

/*
 * Create a student
 */
public long createStudent(Student student, long[] groupIds) {
	SQLiteDatabase db = this.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(KEY_NAME, student.getName());
	values.put(KEY_EMAIL, student.getEmail());
	values.put(KEY_COMPANY_NAME, student.getCompanyName());
	values.put(KEY_PHONE_NUMBER, student.getPhoneNumber());
	values.put(KEY_STATUS, student.getStatus());
	values.put(KEY_CREATED_AT, getDateTime());

	// insert row
	long student_id = db.insert(TABLE_STUDENT, null, values);

	// Assign Group to Student
	// For now a student can be in many groups. This might need to change
	// later depending on requirements.
	for (long group_id : groupIds) {
		createStudentGroup(student_id, group_id);
	}

	return student_id;
}

/*
 * get single student
 */
public Student getStudent(long student_id) {
	SQLiteDatabase db = this.getReadableDatabase();

	String selectQuery = "SELECT  * FROM " + TABLE_STUDENT + " WHERE " + KEY_ID + " = "
			+ student_id;

	Log.e(TAG, selectQuery);

	Cursor c = db.rawQuery(selectQuery, null);

	if (c != null)
		c.moveToFirst();

	Student student = new Student();
	student.setId(c.getInt(c.getColumnIndex(KEY_ID)));
	student.setName((c.getString(c.getColumnIndex(KEY_NAME))));
	student.setCompanyName((c.getString(c.getColumnIndex(KEY_COMPANY_NAME))));
	student.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
	student.setPhoneNumber((c.getString(c.getColumnIndex(KEY_PHONE_NUMBER))));
	student.setStatus((c.getInt(c.getColumnIndex(KEY_STATUS))));
	student.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

	return student;
}

/*
 * Get a list of all students
 */
public List<Student> getAllStudents() {
	List<Student> students = new ArrayList<Student>();
	String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;

	Log.e(TAG, selectQuery);

	SQLiteDatabase db = this.getReadableDatabase();
	Cursor c = db.rawQuery(selectQuery, null);

	// looping through all rows and adding to list
	if (c.moveToFirst()) {
		do {
			Student student = new Student();
			student.setId(c.getInt(c.getColumnIndex(KEY_ID)));
			student.setName((c.getString(c.getColumnIndex(KEY_NAME))));
			student.setCompanyName((c.getString(c.getColumnIndex(KEY_COMPANY_NAME))));
			student.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
			student.setPhoneNumber((c.getString(c.getColumnIndex(KEY_PHONE_NUMBER))));
			student.setStatus((c.getInt(c.getColumnIndex(KEY_STATUS))));
			student.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

			// adding to student list
			students.add(student);
		} while (c.moveToNext());
	}

	return students;
}

/*
 * Update a student
 */
public int updateStudent(Student student) {
	SQLiteDatabase db = this.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(KEY_NAME, student.getName());
	values.put(KEY_EMAIL, student.getEmail());
	values.put(KEY_COMPANY_NAME, student.getCompanyName());
	values.put(KEY_PHONE_NUMBER, student.getPhoneNumber());
	values.put(KEY_STATUS, student.getStatus());

	// updating row
	return db.update(TABLE_STUDENT, values, KEY_ID + " = ?",
			new String[] { String.valueOf(student.getId()) });
}

/*
 * Delete a student
 */
public void deleteStudent(long student_id) {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(TABLE_STUDENT, KEY_ID + " = ?", new String[] { String.valueOf(student_id) });
}

/**
 * getting student count
 */
public int getStudentCount() {
	String countQuery = "SELECT  * FROM " + TABLE_STUDENT;
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery(countQuery, null);

	int count = cursor.getCount();
	cursor.close();

	// return count
	return count;
}

	/*
	 * getting all students in a group
	 */
	public List<Student> getAllStudentsByGroup(String groupName) {
		List<Student> students = new ArrayList<Student>();

		String selectQuery = "SELECT  * FROM " + TABLE_STUDENT + " ts, " + TABLE_GROUP + " tg, "
				+ TABLE_STUDENT_GROUP + " tsg WHERE tg." + KEY_GROUP_NAME + " = '" + groupName
				+ "'" + " AND tg." + KEY_ID + " = " + "tsg." + KEY_GROUP_ID + " AND ts." + KEY_ID
				+ " = " + "tsg." + KEY_STUDENT_ID;

		Log.e(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Student student = new Student();
				student.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				student.setName((c.getString(c.getColumnIndex(KEY_NAME))));
				student.setCompanyName((c.getString(c.getColumnIndex(KEY_COMPANY_NAME))));
				student.setEmail((c.getString(c.getColumnIndex(KEY_EMAIL))));
				student.setPhoneNumber((c.getString(c.getColumnIndex(KEY_PHONE_NUMBER))));
				student.setStatus((c.getInt(c.getColumnIndex(KEY_STATUS))));
				student.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

				// adding to student list
				students.add(student);
			} while (c.moveToNext());
		}

		return students;
	}

	/*
	 * Creating group
	 */
	public long createGroup(Group group) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, group.getGroupName());
		values.put(KEY_CREATED_AT, getDateTime());

		// insert row

		long groupId = -1;
		if (getGroupCountByName(group.getGroupName()) == 0) {
			groupId = db.insert(TABLE_GROUP, null, values);
		}

		return groupId;
	}

	/**
	 * get single group
	 */
	public Group getGroup(long groupId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_GROUP + " WHERE " + KEY_ID + " = " + groupId;

		Log.e(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Group group = new Group();
		group.setId(c.getInt((c.getColumnIndex(KEY_ID))));
		group.setGroupName(c.getString(c.getColumnIndex(KEY_GROUP_NAME)));

		return group;
	}

	/**
	 * get single group by name
	 */
	public int getGroupCountByName(String groupName) {

		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_GROUP + " WHERE " + KEY_GROUP_NAME + " = '"
				+ groupName + "'";

		Log.e(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		return c.getCount();

	}

	/**
	 * getting all groups
	 * */
	public List<Group> getAllGroups() {
		List<Group> groups = new ArrayList<Group>();
		String selectQuery = "SELECT  * FROM " + TABLE_GROUP;

		Log.e(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Group group = new Group();
				group.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				group.setGroupName(c.getString(c.getColumnIndex(KEY_GROUP_NAME)));

				// adding to tags list
				groups.add(group);
			} while (c.moveToNext());
		}
		return groups;
	}

	/*
	 * Update a group
	 */
	public int updateGroup(Group group) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_NAME, group.getGroupName());

		// updating row
		return db.update(TABLE_GROUP, values, KEY_ID + " = ?",
				new String[] { String.valueOf(group.getId()) });
	}

	/*
	 * Delete a group and it's students
	 */
	public void deleteGroup(Group group, boolean deleteAllStudentsInGroup) {
		SQLiteDatabase db = this.getWritableDatabase();

		// before deleting group
		// check if students under this group should also be deleted
		if (deleteAllStudentsInGroup) {
			// get all students under this group
			List<Student> allGroupStudents = getAllStudentsByGroup(group.getGroupName());

			// delete all students
			for (Student student : allGroupStudents) {
				// delete student
				deleteStudent(student.getId());
			}
		}

		// now delete the group
		db.delete(TABLE_GROUP, KEY_ID + " = ?", new String[] { String.valueOf(group.getId()) });
	}

	/**
	 * @param studentId
	 * @param groupId
	 * @return The id of the relation created.
	 * 
	 *         Create a relation between a student and a group.
	 */
	public long createStudentGroup(long studentId, long groupId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_STUDENT_ID, studentId);
		values.put(KEY_GROUP_ID, groupId);
		values.put(KEY_CREATED_AT, getDateTime());

		long id = db.insert(TABLE_STUDENT_GROUP, null, values);

		return id;
	}

	/*
	 * Update a student's group
	 */
	public int updateStudentGroup(long id, long groupId) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GROUP_ID, groupId);

		// updating row
		return db.update(TABLE_STUDENT, values, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	/**
	 * Delete a student group
	 */
	public void deleteStudentGroup(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STUDENT, KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}

	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
