package co.mojito.crud.model;

public class Group {
	int id;
	String groupName;

	// constructors
	public Group() {

	}

	public Group(String groupName) {
		this.groupName = groupName;
	}

	public Group(int id, String groupName) {
		this.id = id;
		this.groupName = groupName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	@Override
	public String toString() {
		return groupName;
	}

}
