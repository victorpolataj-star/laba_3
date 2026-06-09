package com.example.model;

import javafx.scene.image.Image;
import java.io.File;

public class ImageInfo {
    private File file;
    private Image thumbnail;
    private Image fullImage;
    private String exifData;

    public ImageInfo(File file) {
        this.file = file;
        this.exifData = "";
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Image getFullImage() {
        return fullImage;
    }

    public void setFullImage(Image fullImage) {
        this.fullImage = fullImage;
    }

    public String getExifData() {
        return exifData;
    }

    public void setExifData(String exifData) {
        this.exifData = exifData;
    }

    public String getName() {
        return file.getName();
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public long getSize() {
        return file.length();
    }

    public String getSizeFormatted() {
        long bytes = file.length();
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024));
    }
}
