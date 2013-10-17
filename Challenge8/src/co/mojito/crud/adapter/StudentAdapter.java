package co.mojito.crud.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.mojito.crud.R;
import co.mojito.crud.model.Student;

public class StudentAdapter extends ArrayAdapter<Student> {

	private final Context context;
	private final List<Student> students;

	public StudentAdapter(Context context, List<Student> students) {
		super(context, R.layout.rowlayout, students);
		this.context = context;
		this.students = students;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.row_label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.row_icon);
		textView.setText(students.get(position).getId() + " - " + students.get(position).getName());
		imageView.setImageResource(R.drawable.student);

		return rowView;
	}
	

}