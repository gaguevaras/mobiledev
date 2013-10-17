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
import co.mojito.crud.model.Group;

public class GroupAdapter extends ArrayAdapter<Group> {

	private final Context context;
	private final List<Group> groups;

	public GroupAdapter(Context context, List<Group> groups) {
		super(context, R.layout.rowlayout, groups);
		this.context = context;
		this.groups = groups;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.row_label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.row_icon);
		
		textView.setText(groups.get(position).getId() + " - " + groups.get(position).getGroupName());
		imageView.setImageResource(R.drawable.group_icon);

		return rowView;
	}

}
