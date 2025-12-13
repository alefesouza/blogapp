package net.aloogle.dropandoideias.database.model;

public class Favorites {

	int id;
	String postid;
	String json;
	String favoritecontent;
	String created_at;

	// constructors
	public Favorites() {}

	public Favorites(String postid, String json, String favoritecontent) {
		this.postid = postid;
		this.json = json;
		this.favoritecontent = favoritecontent;
	}

	public Favorites(int id, String postid, String json, String favoritecontent) {
		this.id = id;
		this.postid = postid;
		this.json = json;
		this.favoritecontent = favoritecontent;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void setPostId(String postid) {
		this.postid = postid;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setContent(String content) {
		this.favoritecontent = content;
	}

	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}

	// getters
	public long getId() {
		return this.id;
	}

	public String getPostId() {
		return this.postid;
	}

	public String getJson() {
		return this.json;
	}

	public String getContent() {
		return this.favoritecontent;
	}
}
