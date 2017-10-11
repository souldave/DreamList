package com.example.davegilbier.listdream;

/**
 * Created by Dave Gilbier on 09/10/2017.
 */

public class Dream {
    private int id;
    private String dream;
    private String description;
    private byte[] image;

    public Dream(String dream, String description, byte[] image, int id) {
        this.dream = dream;
        this.description = description;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDream() {
        return dream;
    }

    public void setDream(String dream) {
        this.dream = dream;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
