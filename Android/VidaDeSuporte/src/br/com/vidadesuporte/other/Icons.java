package br.com.vidadesuporte.other;

public class Icons {

    private String title;
    private int icon;
    private String icon2;
	private boolean isSection;
    private String count = "13";

    private boolean isCounterVisible = true;

    public Icons() {
    }

    public Icons(String title, int icon, boolean isSection, String icon2) {
        this.title = title;
        this.icon = icon;
		this.isSection = isSection;
        this.icon2 = icon2;
    }

    public Icons(String title, int icon, boolean isSection, String icon2, boolean isCounterVisible, String count) {
        this.title = title;
        this.icon = icon;
		this.isSection = isSection;
        this.icon2 = icon2;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getIcon2() {
        return this.icon2;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }

    public void setCounterVisibility(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }

    public boolean isSection() {
        return this.isSection;
    }
}
