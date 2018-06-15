package fr.wcs.teamwinsoft.winnews;

public class VideoModel {
    private String title, link, video, tags, name, firstname;
    private double latitude, longitude;

    public VideoModel() {
    }

    public VideoModel(String title, String video) {
        this.title = title;
        this.video = video;
    }

    public VideoModel(String title, String link, String video) {
        this.title = title;
        this.link = link;
        this.video = video;
    }

    public VideoModel(String title, String link, String video, String tags) {
        this.title = title;
        this.link = link;
        this.video = video;
        this.tags = tags;
    }

    public VideoModel(String title, String link, String video, String tags, String name, String firstname) {
        this.title = title;
        this.link = link;
        this.video = video;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.firstname = firstname;
    }

    public VideoModel(String title, String link, String video, String tags, double latitude, double longitude, String name, String firstname) {
        this.title = title;
        this.link = link;
        this.video = video;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.firstname = firstname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
