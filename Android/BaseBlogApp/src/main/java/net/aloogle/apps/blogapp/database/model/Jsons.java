package net.aloogle.apps.blogapp.database.model;

public class Jsons {

	int id;
	String what;
	String json;
	String created_at;

	// constructors
	public Jsons() {}

	public Jsons(String what, String json) {
		this.what = what;
		this.json = json;
	}

	public Jsons(int id, String what, String json) {
		this.id = id;
		this.what = what;
		this.json = json;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}

	// getters
	public long getId() {
		return this.id;
	}

	public String getWhat() {
		return this.what;
	}

	public String getJson() {
		return this.json;
	}
}
