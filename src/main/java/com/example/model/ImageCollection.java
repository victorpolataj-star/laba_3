package com.example.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImageCollection {
    private List<ImageInfo> allImages;
    private List<ImageInfo> filteredImages;
    private ImageIterator iterator;
    private String currentFilter;
    private Set<String> supportedExtensions;

    public ImageCollection() {
        this.allImages = new ArrayList<>();
        this.filteredImages = new ArrayList<>();
        this.iterator = new ImageIterator(filteredImages);
        this.currentFilter = "Все";
        this.supportedExtensions = new HashSet<>(Arrays.asList(
                ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
        ));
    }

    public void loadImagesFromDirectory(File directory) {
        allImages.clear();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file)) {
                        allImages.add(new ImageInfo(file));
                    }
                }
            }
        }
        applyFilter(currentFilter);
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : supportedExtensions) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public void applyFilter(String filterType) {
        this.currentFilter = filterType;
        filteredImages.clear();

        if ("Все".equals(filterType)) {
            filteredImages.addAll(allImages);
        } else {
            String extension = filterType.toLowerCase();
            for (ImageInfo imageInfo : allImages) {
                if (imageInfo.getName().toLowerCase().endsWith(extension)) {
                    filteredImages.add(imageInfo);
                }
            }
        }

        iterator.setImages(filteredImages);
    }

    public ImageIterator getIterator() {
        return iterator;
    }

    public List<String> getAvailableFilters() {
        Set<String> filters = new HashSet<>();
        filters.add("Все");
        for (ImageInfo imageInfo : allImages) {
            String name = imageInfo.getName().toLowerCase();
            for (String ext : supportedExtensions) {
                if (name.endsWith(ext)) {
                    filters.add(ext);
                    break;
                }
            }
        }
        List<String> filterList = new ArrayList<>(filters);
        java.util.Collections.sort(filterList);
        return filterList;
    }

    public boolean isEmpty() {
        return filteredImages.isEmpty();
    }

    public int getFilteredCount() {
        return filteredImages.size();
    }
}
