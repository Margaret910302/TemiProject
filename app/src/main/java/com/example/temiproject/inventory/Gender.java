package com.example.temiproject.inventory;
import java.io.Serializable;

public class Gender implements Serializable{

    private String name;
    private int image;

    public Gender() {     }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getImage() { return image; }

    public void setImage(int image) { this.image = image; }
}
