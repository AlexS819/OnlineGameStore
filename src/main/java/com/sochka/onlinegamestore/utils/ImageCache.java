package com.sochka.onlinegamestore.utils;

import javafx.scene.image.Image;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Robust cross-platform image caching manager storing digital assets locally in temp files.
 */
public class ImageCache {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String CACHE_SUBDIR = "onlinegamestore_cache";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    static {
        File dir = new File(TEMP_DIR, CACHE_SUBDIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String getCacheKey(String urlString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(urlString.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String ext = "jpg";
            if (urlString.contains(".")) {
                String afterLastDot = urlString.substring(urlString.lastIndexOf('.') + 1);
                if (afterLastDot.contains("?")) {
                    afterLastDot = afterLastDot.substring(0, afterLastDot.indexOf('?'));
                }
                if (afterLastDot.length() >= 2 && afterLastDot.length() <= 4 && afterLastDot.matches("[a-zA-Z]+")) {
                    ext = afterLastDot;
                }
            }
            return hexString.toString() + "." + ext;
        } catch (Exception e) {
            return String.valueOf(urlString.hashCode()) + ".jpg";
        }
    }

    public static File getCachedFile(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return null;
        }
        String fileName = getCacheKey(urlString);
        return new File(new File(TEMP_DIR, CACHE_SUBDIR), fileName);
    }

    public static String getOrCreateCachedImageFile(String urlString) {
        File cachedFile = getCachedFile(urlString);
        if (cachedFile == null) {
            return null;
        }
        if (cachedFile.exists() && cachedFile.length() > 0) {
            return cachedFile.toURI().toString();
        }

        try {
            URL url = new URL(urlString);
            try (InputStream in = url.openStream();
                 OutputStream out = new FileOutputStream(cachedFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return cachedFile.toURI().toString();
        } catch (Exception e) {
            if (cachedFile.exists()) {
                cachedFile.delete();
            }
            return urlString;
        }
    }

    public static Image getCachedImage(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return null;
        }
        File cachedFile = getCachedFile(urlString);
        if (cachedFile != null && cachedFile.exists() && cachedFile.length() > 0) {
            return new Image(cachedFile.toURI().toString(), true);
        }
        String finalUrl = getOrCreateCachedImageFile(urlString);
        return new Image(finalUrl, true);
    }

    public static void preCacheImages(List<String> urls) {
        if (urls == null) return;
        for (String url : urls) {
            executor.submit(() -> {
                getOrCreateCachedImageFile(url);
            });
        }
    }
}
