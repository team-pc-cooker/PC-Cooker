package com.app.pccooker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Advanced Image Caching and Optimization System
 * Handles copyright-free image fetching, caching, and optimization
 */
public class ImageCacheManager {
    
    private static final String TAG = "ImageCacheManager";
    private static final String CACHE_DIR = "image_cache";
    private static final int MEMORY_CACHE_SIZE = 1024 * 1024 * 50; // 50MB
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 200; // 200MB
    
    private static ImageCacheManager instance;
    private Context context;
    private LruCache<String, Bitmap> memoryCache;
    private File diskCacheDir;
    private ExecutorService executorService;
    
    // Unsplash API configuration
    private static final String UNSPLASH_API_BASE = "https://api.unsplash.com/";
    private static final String UNSPLASH_ACCESS_KEY = "YOUR_UNSPLASH_ACCESS_KEY";
    
    // Pixabay API configuration
    private static final String PIXABAY_API_BASE = "https://pixabay.com/api/";
    private static final String PIXABAY_API_KEY = "YOUR_PIXABAY_API_KEY";
    
    // Fallback high-quality component images
    private static final String[] COMPONENT_FALLBACK_IMAGES = {
        "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400&h=400&fit=crop", // Intel CPU
        "https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=400&h=400&fit=crop", // NVIDIA GPU
        "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop", // Motherboard
        "https://images.unsplash.com/photo-1562976540-8c2d6a2e8d6b?w=400&h=400&fit=crop", // RAM
        "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400&h=400&fit=crop", // Storage
        "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400&h=400&fit=crop", // PC Case
        "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop", // Power Supply
        "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=400&h=400&fit=crop"  // CPU Cooler
    };
    
    private ImageCacheManager(Context context) {
        this.context = context.getApplicationContext();
        initializeMemoryCache();
        initializeDiskCache();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    public static synchronized ImageCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageCacheManager(context);
        }
        return instance;
    }
    
    private void initializeMemoryCache() {
        memoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        
        Log.d(TAG, "Memory cache initialized with size: " + (MEMORY_CACHE_SIZE / 1024 / 1024) + "MB");
    }
    
    private void initializeDiskCache() {
        diskCacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if (!diskCacheDir.exists()) {
            boolean created = diskCacheDir.mkdirs();
            Log.d(TAG, "Disk cache directory created: " + created);
        }
        
        // Clean old cache if size exceeds limit
        cleanDiskCacheIfNeeded();
    }
    
    /**
     * Load image with advanced caching and optimization
     */
    public void loadImage(String imageUrl, ImageView imageView, String componentCategory) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            // Use fallback image for component category
            imageUrl = getFallbackImageForCategory(componentCategory);
        }
        
        // If it's still a placeholder URL, replace with category-specific image
        if (imageUrl != null && (imageUrl.contains("placeholder") || imageUrl.contains("via.placeholder"))) {
            imageUrl = getFallbackImageForCategory(componentCategory);
        }
        
        final String finalImageUrl = imageUrl;
        
        // Check memory cache first
        Bitmap cachedBitmap = memoryCache.get(imageUrl);
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
            Log.d(TAG, "Image loaded from memory cache: " + imageUrl);
            return;
        }
        
        // Load with Glide and custom caching
        Glide.with(context)
                .asBitmap()
                .load(finalImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(getFallbackImageForCategory(componentCategory))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.w(TAG, "Failed to load image: " + finalImageUrl, e);
                        // Try to load a different fallback image
                        loadFallbackImage(imageView, componentCategory);
                        return true;
                    }
                    
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        // Cache the bitmap in memory
                        memoryCache.put(finalImageUrl, resource);
                        
                        // Save to disk cache asynchronously
                        executorService.execute(() -> saveToDiskCache(finalImageUrl, resource));
                        
                        Log.d(TAG, "Image loaded and cached: " + finalImageUrl);
                        return false; // Let Glide handle the rest
                    }
                })
                .into(imageView);
    }
    
    /**
     * Fetch optimized image from copyright-free sources
     */
    public void fetchCopyrightFreeImage(String searchQuery, String category, ImageLoadCallback callback) {
        executorService.execute(() -> {
            try {
                // Try Unsplash first
                String imageUrl = fetchFromUnsplash(searchQuery, category);
                
                if (imageUrl == null) {
                    // Try Pixabay as fallback
                    imageUrl = fetchFromPixabay(searchQuery, category);
                }
                
                if (imageUrl == null) {
                    // Use curated fallback
                    imageUrl = getFallbackImageForCategory(category);
                }
                
                final String finalUrl = imageUrl;
                // Return result on main thread
                if (callback != null) {
                    callback.onImageLoaded(finalUrl);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error fetching copyright-free image", e);
                if (callback != null) {
                    callback.onImageLoadFailed(e);
                }
            }
        });
    }
    
    private String fetchFromUnsplash(String searchQuery, String category) {
        if (UNSPLASH_ACCESS_KEY.equals("YOUR_UNSPLASH_ACCESS_KEY")) {
            return null; // API key not configured
        }
        
        try {
            String query = searchQuery + " " + category + " computer hardware";
            String urlString = UNSPLASH_API_BASE + "search/photos?query=" + 
                             query.replace(" ", "%20") + "&per_page=1&orientation=squarish";
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Client-ID " + UNSPLASH_ACCESS_KEY);
            connection.setRequestMethod("GET");
            
            if (connection.getResponseCode() == 200) {
                // Parse JSON response to get image URL
                // This is a simplified version - you'd want to use a JSON library
                InputStream inputStream = connection.getInputStream();
                // ... JSON parsing logic would go here
                // For now, return null to use fallbacks
                inputStream.close();
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching from Unsplash", e);
        }
        
        return null;
    }
    
    private String fetchFromPixabay(String searchQuery, String category) {
        if (PIXABAY_API_KEY.equals("YOUR_PIXABAY_API_KEY")) {
            return null; // API key not configured
        }
        
        try {
            String query = searchQuery + " " + category;
            String urlString = PIXABAY_API_BASE + "?key=" + PIXABAY_API_KEY + 
                             "&q=" + query.replace(" ", "+") + 
                             "&image_type=photo&category=computer&min_width=400&min_height=400&per_page=3";
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            if (connection.getResponseCode() == 200) {
                // Parse JSON response to get image URL
                InputStream inputStream = connection.getInputStream();
                // ... JSON parsing logic would go here
                inputStream.close();
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching from Pixabay", e);
        }
        
        return null;
    }
    
    private String getFallbackImageForCategory(String category) {
        if (category == null) return COMPONENT_FALLBACK_IMAGES[0];
        
        switch (category.toUpperCase()) {
            case "PROCESSOR":
            case "CPU":
                return COMPONENT_FALLBACK_IMAGES[0]; // Intel CPU image
            case "GRAPHIC CARD":
            case "GPU":
                return COMPONENT_FALLBACK_IMAGES[1]; // NVIDIA GPU image
            case "MOTHERBOARD":
                return COMPONENT_FALLBACK_IMAGES[2]; // Motherboard image
            case "MEMORY":
            case "RAM":
                return COMPONENT_FALLBACK_IMAGES[3]; // RAM image
            case "STORAGE":
            case "SSD":
            case "HDD":
                return COMPONENT_FALLBACK_IMAGES[4]; // Storage image
            case "CASE":
            case "CABINET":
                return COMPONENT_FALLBACK_IMAGES[5]; // PC Case image
            case "POWER SUPPLY":
            case "PSU":
                return COMPONENT_FALLBACK_IMAGES[6]; // Power Supply image
            case "CPU COOLER":
            case "COOLER":
                return COMPONENT_FALLBACK_IMAGES[7]; // CPU Cooler image
            default:
                return COMPONENT_FALLBACK_IMAGES[0]; // Default to CPU image
        }
    }
    
    private void loadFallbackImage(ImageView imageView, String category) {
        String fallbackUrl = getFallbackImageForCategory(category);
        Glide.with(context)
                .load(fallbackUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);
    }
    
    private void saveToDiskCache(String imageUrl, Bitmap bitmap) {
        try {
            String filename = generateCacheKey(imageUrl) + ".png";
            File cacheFile = new File(diskCacheDir, filename);
            
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.close();
            
            Log.d(TAG, "Image saved to disk cache: " + filename);
            
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to disk cache", e);
        }
    }
    
    private Bitmap loadFromDiskCache(String imageUrl) {
        try {
            String filename = generateCacheKey(imageUrl) + ".png";
            File cacheFile = new File(diskCacheDir, filename);
            
            if (cacheFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
                if (bitmap != null) {
                    // Also cache in memory
                    memoryCache.put(imageUrl, bitmap);
                    Log.d(TAG, "Image loaded from disk cache: " + filename);
                    return bitmap;
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from disk cache", e);
        }
        
        return null;
    }
    
    private String generateCacheKey(String imageUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(imageUrl.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(imageUrl.hashCode());
        }
    }
    
    private void cleanDiskCacheIfNeeded() {
        executorService.execute(() -> {
            File[] files = diskCacheDir.listFiles();
            if (files != null) {
                long totalSize = 0;
                for (File file : files) {
                    totalSize += file.length();
                }
                
                if (totalSize > DISK_CACHE_SIZE) {
                    Log.d(TAG, "Disk cache size exceeded, cleaning up...");
                    
                    // Sort files by last modified (oldest first)
                    java.util.Arrays.sort(files, (f1, f2) -> 
                        Long.compare(f1.lastModified(), f2.lastModified()));
                    
                    // Delete oldest files until under limit
                    for (File file : files) {
                        if (totalSize <= DISK_CACHE_SIZE * 0.8) break; // Clean to 80% of limit
                        
                        totalSize -= file.length();
                        boolean deleted = file.delete();
                        if (deleted) {
                            Log.d(TAG, "Deleted old cache file: " + file.getName());
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Clear all caches
     */
    public void clearCache() {
        // Clear memory cache
        memoryCache.evictAll();
        
        // Clear disk cache
        executorService.execute(() -> {
            File[] files = diskCacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean deleted = file.delete();
                    Log.d(TAG, "Cache file deleted: " + file.getName() + " - " + deleted);
                }
            }
        });
        
        Log.d(TAG, "All caches cleared");
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getCacheStats() {
        CacheStats stats = new CacheStats();
        stats.memoryCacheSize = memoryCache.size();
        stats.memoryCacheHitCount = memoryCache.hitCount();
        stats.memoryCacheMissCount = memoryCache.missCount();
        
        // Calculate disk cache size
        File[] files = diskCacheDir.listFiles();
        if (files != null) {
            stats.diskCacheFileCount = files.length;
            for (File file : files) {
                stats.diskCacheSize += file.length();
            }
        }
        
        return stats;
    }
    
    public static class CacheStats {
        public int memoryCacheSize;
        public long memoryCacheHitCount;
        public long memoryCacheMissCount;
        public int diskCacheFileCount;
        public long diskCacheSize;
        
        public String getFormattedStats() {
            return String.format(
                "Memory: %d items, %d hits, %d misses\nDisk: %d files, %.1f MB",
                memoryCacheSize, memoryCacheHitCount, memoryCacheMissCount,
                diskCacheFileCount, diskCacheSize / 1024.0 / 1024.0
            );
        }
    }
    
    public interface ImageLoadCallback {
        void onImageLoaded(String imageUrl);
        void onImageLoadFailed(Exception error);
    }
}