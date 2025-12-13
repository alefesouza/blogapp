package br.com.vidadesuporte.other;

public class Categorias {

    private String id;
    private String title;
    private String icon;
	
    public Categorias() {
    }

    public Categorias(String id, String title, String icon) {
        this.id = id;
		this.title = title;
		this.icon = icon;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
