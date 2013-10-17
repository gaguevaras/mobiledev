package co.mojito.crud.model;

/**
 * @author gustavoaguevara
 * 
 *         This is a model class for the entity Student in the application.
 * 
 */
public class Student {

	int id;
	String name;
	String phoneNumber;
	String companyName;
	String email;
	int status;
	String createdAt;

	// constructors
	public Student() {
	}

	public Student(String name, int status) {
		this.name = name;
		this.status = status;
	}

	public Student(int id, String name, int status) {
		this.id = id;
		this.name = name;
		this.status = status;
	}

	public Student(int id,
			String name,
			String phoneNumber,
			String companyName,
			String email,
			int status) {
		super();
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.companyName = companyName;
		this.email = email;
		this.status = status;
	}

	public Student(String name, String phoneNumber, String companyName, String email, int status) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.companyName = companyName;
		this.email = email;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreated_at() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
