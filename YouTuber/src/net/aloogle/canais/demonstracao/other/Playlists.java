package net.aloogle.canais.demonstracao.other;

public class Playlists {

    private String id;
    private String title;
	
    public Playlists() {
    }

    public Playlists(String id, String title) {
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
