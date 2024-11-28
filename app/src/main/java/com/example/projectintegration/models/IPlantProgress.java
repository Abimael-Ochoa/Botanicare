package com.example.projectintegration.models;

public class IPlantProgress {
    private int imageResId;
    private String text;
    public IPlantProgress(int imageResId, String text) {
        this.imageResId = imageResId;
        this.text = text;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getText() {
        return text;
    }
}
