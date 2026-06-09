package com.example.model;

import java.util.List;

public class ImageIterator implements java.util.Iterator<ImageInfo> {
    private List<ImageInfo> images;
    private int currentIndex;

    public ImageIterator(List<ImageInfo> images) {
        this.images = images;
        this.currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return images != null && !images.isEmpty();
    }

    @Override
    public ImageInfo next() {
        if (!hasNext()) {
            return null;
        }
        currentIndex = (currentIndex + 1) % images.size();
        return images.get(currentIndex);
    }

    public ImageInfo previous() {
        if (!hasNext()) {
            return null;
        }
        currentIndex = (currentIndex - 1 + images.size()) % images.size();
        return images.get(currentIndex);
    }

    public ImageInfo first() {
        if (!hasNext()) {
            return null;
        }
        currentIndex = 0;
        return images.get(currentIndex);
    }

    public ImageInfo last() {
        if (!hasNext()) {
            return null;
        }
        currentIndex = images.size() - 1;
        return images.get(currentIndex);
    }

    public ImageInfo getCurrent() {
        if (!hasNext()) {
            return null;
        }
        return images.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex + 1;
    }

    public int getTotalCount() {
        return images != null ? images.size() : 0;
    }

    public void setImages(List<ImageInfo> images) {
        this.images = images;
        if (currentIndex >= images.size()) {
            currentIndex = images.isEmpty() ? 0 : images.size() - 1;
        }
    }
}
