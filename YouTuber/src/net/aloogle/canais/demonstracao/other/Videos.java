package net.aloogle.canais.demonstracao.other;

public class Videos {

    private String id;
    private String title;
    private String likes;
    private String visualizacoes;
	
    public Videos() {
    }

    public Videos(String id, String title, String likes, String visualizacoes) {
        this.id = id;
		this.title = title;
		this.likes = likes;
        this.visualizacoes = visualizacoes;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLikes() {
        return this.likes;
    }

    public String getViews() {
        return this.visualizacoes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setViews(String views) {
        this.visualizacoes = views;
    }
}
