package com.app.pccooker;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.app.pccooker.models.ComponentModel;

/**
 * API Integration Manager for Live PC Component Data
 * Integrates with multiple data sources and open datasets
 */
public class APIDataManager {
    
    private static final String TAG = "APIDataManager";
    private static APIDataManager instance;
    
    // API Configuration
    private static final String PCPARTPICKER_API_BASE = "https://pcpartpicker.com/api/1.0/";
    private static final String NEWEGG_API_BASE = "https://api.newegg.com/marketplace/";
    private static final String AMAZON_API_BASE = "https://webservices.amazon.com/paapi5/";
    
    // Open Dataset URLs
    private static final String GITHUB_COMPONENTS_DATASET = "https://raw.githubusercontent.com/pc-components/dataset/main/components.json";
    private static final String TECHPOWERUP_GPU_DB = "https://www.techpowerup.com/gpu-specs/";
    private static final String CPU_WORLD_API = "https://www.cpu-world.com/api/";
    
    private FirebaseFirestore db;
    private ExecutorService executorService;
    private Map<String, Long> lastFetchTimes;
    private static final long CACHE_DURATION = 6 * 60 * 60 * 1000; // 6 hours
    
    private APIDataManager() {
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newFixedThreadPool(3);
        lastFetchTimes = new HashMap<>();
    }
    
    public static synchronized APIDataManager getInstance() {
        if (instance == null) {
            instance = new APIDataManager();
        }
        return instance;
    }
    
    /**
     * Fetch latest component data from all sources
     */
    public void fetchLatestComponentData(DataFetchCallback callback) {
        Log.d(TAG, "Starting comprehensive data fetch from all sources");
        
        executorService.execute(() -> {
            try {
                List<ComponentModel> allComponents = new ArrayList<>();
                
                // Fetch from multiple sources
                allComponents.addAll(fetchFromOpenDatasets());
                allComponents.addAll(fetchFromPCPartPicker());
                allComponents.addAll(fetchFromNewegg());
                allComponents.addAll(fetchCuratedComponents());
                
                // Remove duplicates and validate
                List<ComponentModel> validatedComponents = validateAndDeduplicateComponents(allComponents);
                
                // Update Firebase with new data
                updateFirebaseWithComponents(validatedComponents, callback);
                
                Log.d(TAG, "Data fetch completed. Total components: " + validatedComponents.size());
                
            } catch (Exception e) {
                Log.e(TAG, "Error during comprehensive data fetch", e);
                if (callback != null) {
                    callback.onFetchFailed(e);
                }
            }
        });
    }
    
    /**
     * Fetch from open-source datasets
     */
    private List<ComponentModel> fetchFromOpenDatasets() {
        List<ComponentModel> components = new ArrayList<>();
        
        try {
            Log.d(TAG, "Fetching from open datasets...");
            
            // Fetch from GitHub dataset
            String jsonData = fetchDataFromUrl(GITHUB_COMPONENTS_DATASET);
            if (jsonData != null) {
                components.addAll(parseGitHubDataset(jsonData));
            }
            
            // Add more open dataset sources here
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching from open datasets", e);
        }
        
        return components;
    }
    
    /**
     * Fetch from PCPartPicker (simulated - would need actual API access)
     */
    private List<ComponentModel> fetchFromPCPartPicker() {
        List<ComponentModel> components = new ArrayList<>();
        
        try {
            Log.d(TAG, "Fetching from PCPartPicker...");
            
            // Note: PCPartPicker doesn't have a public API
            // This would require web scraping or partnership
            // For now, we'll return curated data
            
            components.addAll(generatePCPartPickerStyleData());
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching from PCPartPicker", e);
        }
        
        return components;
    }
    
    /**
     * Fetch from Newegg API (simulated)
     */
    private List<ComponentModel> fetchFromNewegg() {
        List<ComponentModel> components = new ArrayList<>();
        
        try {
            Log.d(TAG, "Fetching from Newegg...");
            
            // Note: Would require Newegg API credentials
            // Simulating with realistic data
            
            components.addAll(generateNeweggStyleData());
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching from Newegg", e);
        }
        
        return components;
    }
    
    /**
     * Fetch curated component data
     */
    private List<ComponentModel> fetchCuratedComponents() {
        List<ComponentModel> components = new ArrayList<>();
        
        // Latest Intel CPUs
        components.addAll(generateIntelCPUs());
        
        // Latest AMD CPUs
        components.addAll(generateAMDCPUs());
        
        // Latest NVIDIA GPUs
        components.addAll(generateNVIDIAGPUs());
        
        // Latest AMD GPUs
        components.addAll(generateAMDGPUs());
        
        // Latest Motherboards
        components.addAll(generateLatestMotherboards());
        
        // Latest Memory
        components.addAll(generateLatestMemory());
        
        // Latest Storage
        components.addAll(generateLatestStorage());
        
        return components;
    }
    
    private List<ComponentModel> generateIntelCPUs() {
        List<ComponentModel> cpus = new ArrayList<>();
        
        // 13th Gen Intel CPUs
        cpus.add(createComponent("Intel Core i9-13900K", "Intel", "PROCESSOR", 45000, 4.6,
            Map.of("Socket", "LGA1700", "Cores", "24", "Threads", "32", "Clock Speed", "3.0GHz", "Generation", "13th Gen"),
            "High-performance 24-core processor for gaming and workstation tasks"));
            
        cpus.add(createComponent("Intel Core i7-13700K", "Intel", "PROCESSOR", 35000, 4.5,
            Map.of("Socket", "LGA1700", "Cores", "16", "Threads", "24", "Clock Speed", "3.4GHz", "Generation", "13th Gen"),
            "Powerful 16-core processor for gaming and productivity"));
            
        cpus.add(createComponent("Intel Core i5-13600K", "Intel", "PROCESSOR", 25000, 4.4,
            Map.of("Socket", "LGA1700", "Cores", "14", "Threads", "20", "Clock Speed", "3.5GHz", "Generation", "13th Gen"),
            "Excellent gaming processor with 14 cores"));
            
        cpus.add(createComponent("Intel Core i5-13400F", "Intel", "PROCESSOR", 18000, 4.3,
            Map.of("Socket", "LGA1700", "Cores", "10", "Threads", "16", "Clock Speed", "2.5GHz", "Generation", "13th Gen"),
            "Great value gaming processor without integrated graphics"));
        
        return cpus;
    }
    
    private List<ComponentModel> generateAMDCPUs() {
        List<ComponentModel> cpus = new ArrayList<>();
        
        // AMD Ryzen 7000 Series
        cpus.add(createComponent("AMD Ryzen 9 7950X", "AMD", "PROCESSOR", 48000, 4.7,
            Map.of("Socket", "AM5", "Cores", "16", "Threads", "32", "Clock Speed", "4.5GHz", "Generation", "7th Gen"),
            "Flagship 16-core processor with exceptional performance"));
            
        cpus.add(createComponent("AMD Ryzen 9 7900X", "AMD", "PROCESSOR", 38000, 4.6,
            Map.of("Socket", "AM5", "Cores", "12", "Threads", "24", "Clock Speed", "4.7GHz", "Generation", "7th Gen"),
            "High-performance 12-core processor for demanding workloads"));
            
        cpus.add(createComponent("AMD Ryzen 7 7700X", "AMD", "PROCESSOR", 28000, 4.5,
            Map.of("Socket", "AM5", "Cores", "8", "Threads", "16", "Clock Speed", "4.5GHz", "Generation", "7th Gen"),
            "Excellent 8-core processor for gaming and productivity"));
            
        cpus.add(createComponent("AMD Ryzen 5 7600X", "AMD", "PROCESSOR", 20000, 4.3,
            Map.of("Socket", "AM5", "Cores", "6", "Threads", "12", "Clock Speed", "4.7GHz", "Generation", "7th Gen"),
            "Great gaming processor with 6 cores"));
        
        return cpus;
    }
    
    private List<ComponentModel> generateNVIDIAGPUs() {
        List<ComponentModel> gpus = new ArrayList<>();
        
        // RTX 40 Series
        gpus.add(createComponent("NVIDIA GeForce RTX 4090", "NVIDIA", "GRAPHIC CARD", 120000, 4.8,
            Map.of("Memory", "24GB", "Memory Type", "GDDR6X", "Core Clock", "2230MHz", "Power", "450W"),
            "Ultimate gaming and content creation graphics card"));
            
        gpus.add(createComponent("NVIDIA GeForce RTX 4080", "NVIDIA", "GRAPHIC CARD", 90000, 4.6,
            Map.of("Memory", "16GB", "Memory Type", "GDDR6X", "Core Clock", "2210MHz", "Power", "320W"),
            "High-end graphics card for 4K gaming"));
            
        gpus.add(createComponent("NVIDIA GeForce RTX 4070 Ti", "NVIDIA", "GRAPHIC CARD", 65000, 4.5,
            Map.of("Memory", "12GB", "Memory Type", "GDDR6X", "Core Clock", "2310MHz", "Power", "285W"),
            "Excellent 1440p and 4K gaming performance"));
            
        gpus.add(createComponent("NVIDIA GeForce RTX 4070", "NVIDIA", "GRAPHIC CARD", 50000, 4.4,
            Map.of("Memory", "12GB", "Memory Type", "GDDR6X", "Core Clock", "1920MHz", "Power", "200W"),
            "Great 1440p gaming graphics card"));
            
        gpus.add(createComponent("NVIDIA GeForce RTX 4060 Ti", "NVIDIA", "GRAPHIC CARD", 35000, 4.2,
            Map.of("Memory", "8GB", "Memory Type", "GDDR6", "Core Clock", "2310MHz", "Power", "165W"),
            "Solid 1080p and 1440p gaming performance"));
        
        return gpus;
    }
    
    private List<ComponentModel> generateAMDGPUs() {
        List<ComponentModel> gpus = new ArrayList<>();
        
        // RX 7000 Series
        gpus.add(createComponent("AMD Radeon RX 7900 XTX", "AMD", "GRAPHIC CARD", 75000, 4.5,
            Map.of("Memory", "24GB", "Memory Type", "GDDR6", "Core Clock", "2230MHz", "Power", "355W"),
            "High-end graphics card competing with RTX 4080"));
            
        gpus.add(createComponent("AMD Radeon RX 7900 XT", "AMD", "GRAPHIC CARD", 65000, 4.4,
            Map.of("Memory", "20GB", "Memory Type", "GDDR6", "Core Clock", "2000MHz", "Power", "300W"),
            "Excellent 1440p and 4K gaming performance"));
            
        gpus.add(createComponent("AMD Radeon RX 7700 XT", "AMD", "GRAPHIC CARD", 40000, 4.3,
            Map.of("Memory", "12GB", "Memory Type", "GDDR6", "Core Clock", "2171MHz", "Power", "245W"),
            "Great 1440p gaming graphics card"));
            
        gpus.add(createComponent("AMD Radeon RX 7600", "AMD", "GRAPHIC CARD", 25000, 4.1,
            Map.of("Memory", "8GB", "Memory Type", "GDDR6", "Core Clock", "1720MHz", "Power", "165W"),
            "Solid 1080p gaming performance"));
        
        return gpus;
    }
    
    private List<ComponentModel> generateLatestMotherboards() {
        List<ComponentModel> motherboards = new ArrayList<>();
        
        // Intel Z790 Motherboards
        motherboards.add(createComponent("ASUS ROG STRIX Z790-E GAMING", "ASUS", "MOTHERBOARD", 35000, 4.6,
            Map.of("Socket", "LGA1700", "RAM", "DDR5", "Max RAM", "128GB", "Form Factor", "ATX"),
            "Premium Z790 motherboard with advanced features"));
            
        motherboards.add(createComponent("MSI MAG Z790 TOMAHAWK", "MSI", "MOTHERBOARD", 25000, 4.4,
            Map.of("Socket", "LGA1700", "RAM", "DDR5", "Max RAM", "128GB", "Form Factor", "ATX"),
            "Great value Z790 motherboard for gaming"));
        
        // AMD X670E Motherboards
        motherboards.add(createComponent("ASUS ROG STRIX X670E-E GAMING", "ASUS", "MOTHERBOARD", 40000, 4.7,
            Map.of("Socket", "AM5", "RAM", "DDR5", "Max RAM", "128GB", "Form Factor", "ATX"),
            "Premium X670E motherboard for Ryzen 7000 series"));
            
        motherboards.add(createComponent("MSI MAG X670E TOMAHAWK", "MSI", "MOTHERBOARD", 28000, 4.5,
            Map.of("Socket", "AM5", "RAM", "DDR5", "Max RAM", "128GB", "Form Factor", "ATX"),
            "Excellent X670E motherboard with great features"));
        
        return motherboards;
    }
    
    private List<ComponentModel> generateLatestMemory() {
        List<ComponentModel> memory = new ArrayList<>();
        
        // DDR5 Memory
        memory.add(createComponent("Corsair Vengeance DDR5-5600 32GB", "Corsair", "MEMORY", 12000, 4.5,
            Map.of("Type", "DDR5", "Capacity", "32GB", "Speed", "5600MHz", "Kit", "2x16GB"),
            "High-performance DDR5 memory kit"));
            
        memory.add(createComponent("G.Skill Trident Z5 DDR5-6000 16GB", "G.Skill", "MEMORY", 8000, 4.4,
            Map.of("Type", "DDR5", "Capacity", "16GB", "Speed", "6000MHz", "Kit", "2x8GB"),
            "Premium DDR5 memory with RGB lighting"));
            
        memory.add(createComponent("Kingston Fury Beast DDR5-5200 32GB", "Kingston", "MEMORY", 10000, 4.3,
            Map.of("Type", "DDR5", "Capacity", "32GB", "Speed", "5200MHz", "Kit", "2x16GB"),
            "Reliable DDR5 memory for gaming and productivity"));
        
        return memory;
    }
    
    private List<ComponentModel> generateLatestStorage() {
        List<ComponentModel> storage = new ArrayList<>();
        
        // NVMe SSDs
        storage.add(createComponent("Samsung 980 PRO NVMe SSD 2TB", "Samsung", "STORAGE", 15000, 4.7,
            Map.of("Type", "NVMe SSD", "Capacity", "2TB", "Interface", "PCIe 4.0", "Speed", "7000MB/s"),
            "Flagship NVMe SSD with exceptional performance"));
            
        storage.add(createComponent("WD Black SN850X NVMe SSD 1TB", "Western Digital", "STORAGE", 8000, 4.6,
            Map.of("Type", "NVMe SSD", "Capacity", "1TB", "Interface", "PCIe 4.0", "Speed", "7300MB/s"),
            "High-performance gaming SSD"));
            
        storage.add(createComponent("Crucial P5 Plus NVMe SSD 1TB", "Crucial", "STORAGE", 6000, 4.4,
            Map.of("Type", "NVMe SSD", "Capacity", "1TB", "Interface", "PCIe 4.0", "Speed", "6600MB/s"),
            "Great value PCIe 4.0 SSD"));
        
        return storage;
    }
    
    private ComponentModel createComponent(String name, String brand, String category, double price, double rating,
                                         Map<String, String> specs, String description) {
        ComponentModel component = new ComponentModel();
        component.setName(name);
        component.setBrand(brand);
        component.setCategory(category);
        component.setPrice(price);
        component.setRating(rating);
        component.setRatingCount(100 + (int)(Math.random() * 900)); // Random rating count
        component.setDescription(description);
        component.setSpecifications(specs);
        component.setInStock(Math.random() > 0.1); // 90% in stock
        component.setTrendingScore(70 + Math.random() * 30); // Random trending score 70-100
        component.setTrending(component.getTrendingScore() > 85);
        
        // Set build types based on price and category
        List<String> buildTypes = new ArrayList<>();
        if (price < 10000) buildTypes.add("Budget Build");
        if (price >= 10000 && price < 40000) buildTypes.add("Mid-Range Build");
        if (price >= 40000) buildTypes.add("High-End Build");
        
        if (category.equals("GRAPHIC CARD") && price > 30000) buildTypes.add("Gaming Build");
        if (category.equals("PROCESSOR") && price > 25000) buildTypes.add("Workstation Build");
        
        component.setBuildTypes(buildTypes);
        
        return component;
    }
    
    private String fetchDataFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                reader.close();
                connection.disconnect();
                
                return response.toString();
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "Error fetching data from URL: " + urlString, e);
        }
        
        return null;
    }
    
    private List<ComponentModel> parseGitHubDataset(String jsonData) {
        List<ComponentModel> components = new ArrayList<>();
        
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray componentsArray = jsonObject.getJSONArray("components");
            
            for (int i = 0; i < componentsArray.length(); i++) {
                JSONObject componentJson = componentsArray.getJSONObject(i);
                
                ComponentModel component = new ComponentModel();
                component.setName(componentJson.optString("name", "Unknown"));
                component.setBrand(componentJson.optString("brand", "Unknown"));
                component.setCategory(componentJson.optString("category", "OTHER"));
                component.setPrice(componentJson.optDouble("price", 0.0));
                component.setRating(componentJson.optDouble("rating", 4.0));
                component.setDescription(componentJson.optString("description", ""));
                
                // Parse specifications
                JSONObject specsJson = componentJson.optJSONObject("specifications");
                if (specsJson != null) {
                    Map<String, String> specs = new HashMap<>();
                    Iterator<String> keys = specsJson.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        specs.put(key, specsJson.optString(key));
                    }
                    component.setSpecifications(specs);
                }
                
                components.add(component);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing GitHub dataset", e);
        }
        
        return components;
    }
    
    private List<ComponentModel> generatePCPartPickerStyleData() {
        // Simulate PCPartPicker-style data with realistic pricing and specifications
        return fetchCuratedComponents();
    }
    
    private List<ComponentModel> generateNeweggStyleData() {
        // Simulate Newegg-style data with current market pricing
        return fetchCuratedComponents();
    }
    
    private List<ComponentModel> validateAndDeduplicateComponents(List<ComponentModel> components) {
        Map<String, ComponentModel> uniqueComponents = new HashMap<>();
        
        for (ComponentModel component : components) {
            // Validate component
            if (component.getName() == null || component.getName().trim().isEmpty()) {
                continue;
            }
            
            if (component.getPrice() <= 0) {
                continue;
            }
            
            // Use name as key for deduplication
            String key = component.getName().trim().toLowerCase();
            
            // Keep the component with higher rating or more recent data
            ComponentModel existing = uniqueComponents.get(key);
            if (existing == null || component.getRating() > existing.getRating()) {
                uniqueComponents.put(key, component);
            }
        }
        
        return new ArrayList<>(uniqueComponents.values());
    }
    
    private void updateFirebaseWithComponents(List<ComponentModel> components, DataFetchCallback callback) {
        Log.d(TAG, "Updating Firebase with " + components.size() + " components");
        
        int totalComponents = components.size();
        int[] processedCount = {0};
        int[] successCount = {0};
        
        for (ComponentModel component : components) {
            // Add to both Firebase structures
            db.collection("components")
                    .add(component)
                    .addOnSuccessListener(documentReference -> {
                        successCount[0]++;
                        processedCount[0]++;
                        
                        if (processedCount[0] == totalComponents) {
                            Log.d(TAG, "Firebase update completed. Success: " + successCount[0] + "/" + totalComponents);
                            if (callback != null) {
                                callback.onFetchComplete(successCount[0], totalComponents - successCount[0]);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        processedCount[0]++;
                        Log.e(TAG, "Failed to add component: " + component.getName(), e);
                        
                        if (processedCount[0] == totalComponents) {
                            if (callback != null) {
                                callback.onFetchComplete(successCount[0], totalComponents - successCount[0]);
                            }
                        }
                    });
            
            // Also add to new structure
            db.collection("pc_components")
                    .document(component.getCategory())
                    .collection("items")
                    .add(component);
        }
    }
    
    /**
     * Check if data should be refreshed
     */
    public boolean shouldRefreshData(String dataType) {
        Long lastFetch = lastFetchTimes.get(dataType);
        if (lastFetch == null) {
            return true;
        }
        
        return System.currentTimeMillis() - lastFetch > CACHE_DURATION;
    }
    
    /**
     * Update last fetch time
     */
    public void updateLastFetchTime(String dataType) {
        lastFetchTimes.put(dataType, System.currentTimeMillis());
    }
    
    public interface DataFetchCallback {
        void onFetchComplete(int successCount, int failureCount);
        void onFetchFailed(Exception error);
    }
}