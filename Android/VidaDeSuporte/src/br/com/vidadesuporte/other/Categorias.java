package br.com.vidadesuporte.other;

public class Categorias {

    private String id;
    private String title;
	
    public Categorias() {
    }

    public Categorias(String id, String title) {
        this.id = id;
		this.title = title;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
