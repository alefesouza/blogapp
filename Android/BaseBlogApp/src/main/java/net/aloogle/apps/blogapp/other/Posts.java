package net.aloogle.apps.blogapp.other;

public class Posts {

	private String id;
	private String title;
	private String image;
	private String description;
	private String url;
	private String comments;
	private String category;
	private String date;

	public Posts() {}

	public Posts(String id, String title, String image, String description, String url, String comments, String category, String date) {
		this.id = id;
		this.title = title;
		this.image = image;
		this.description = description;
		this.url = url;
		this.comments = comments;
		this.category = category;
		this.date = date;
	}

	public String getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getImage() {
		return this.image;
	}

	public String getDescription() {
		return this.description;
	}

	public String getUrl() {
		return this.url;
	}

	public String getComments() {
		return this.comments;
	}

	public String getCategory() {
		return this.category;
	}

	public String getDate() {
		return this.date;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
