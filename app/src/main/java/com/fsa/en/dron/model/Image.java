package com.fsa.en.dron.model;

/**
 * Created by Marcelo on 23/09/2016.
 */
import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image implements Serializable{
    private String name;
    private String small, medium, large;
    private String url;

    public Image() {
    }

    public Image(String name, String small, String medium, String large, String url) {
        this.name = name;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
