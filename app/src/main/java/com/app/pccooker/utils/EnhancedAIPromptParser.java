package com.app.pccooker.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnhancedAIPromptParser {
    
    public static class EnhancedRequirements {
        public String useCase = "general";
        public int budget = 0;
        public String language = "en";
        public String priority = "balanced";
        public List<String> specificComponents = new ArrayList<>();
        public Map<String, String> preferences = new HashMap<>();
        public String urgency = "normal";
        public String experience = "beginner";
        public boolean includePeripherals = false;
        public String formFactor = "atx";
        public boolean rgb = false;
        public String noiseLevel = "normal";
        public String brandPreference = "any";
        public String storageType = "mixed";
        public String cooling = "air";
        public String overclocking = "none";
        public String streaming = "no";
        public String multitasking = "moderate";
        public String futureProofing = "moderate";
        public String aesthetics = "standard";
        public String connectivity = "standard";
        public String warranty = "standard";
        public String assembly = "self";
        public String support = "basic";
        public String upgradePath = "moderate";
        public String portability = "none";
        public String durability = "standard";
        public String delivery = "normal";
    }

    // Conversation context for better understanding
    private static Map<String, ConversationContext> contexts = new HashMap<>();
    
    public static class ConversationContext {
        public List<String> history = new ArrayList<>();
        public EnhancedRequirements lastReq;
        public long lastInteraction;
        public Map<String, Object> userPrefs = new HashMap<>();
    }

    // Enhanced keyword mappings
    private static final Map<String, String> USE_CASE_MAP = new HashMap<String, String>() {{
        // Gaming
        put("gaming", "gaming"); put("game", "gaming"); put("play", "gaming");
        put("gamer", "gaming"); put("esports", "gaming"); put("fortnite", "gaming");
        put("pubg", "gaming"); put("valorant", "gaming"); put("csgo", "gaming");
        put("apex", "gaming"); put("cod", "gaming"); put("fifa", "gaming");
        put("racing", "gaming"); put("simulation", "gaming"); put("vr", "gaming");
        put("virtual reality", "gaming");
        
        // Content Creation
        put("editing", "editing"); put("edit", "editing"); put("video", "editing");
        put("photo", "editing"); put("photoshop", "editing"); put("premiere", "editing");
        put("after effects", "editing"); put("blender", "editing"); put("3d", "editing");
        put("rendering", "editing"); put("animation", "editing"); put("maya", "editing");
        put("cinema 4d", "editing"); put("davinci", "editing"); put("final cut", "editing");
        put("lightroom", "editing"); put("illustrator", "editing"); put("autocad", "editing");
        
        // Work & Productivity
        put("work", "work"); put("office", "work"); put("business", "work");
        put("productivity", "work"); put("excel", "work"); put("word", "work");
        put("powerpoint", "work"); put("coding", "work"); put("programming", "work");
        put("development", "work"); put("software", "work"); put("web development", "work");
        put("app development", "work"); put("data analysis", "work"); put("machine learning", "work");
        put("ai", "work"); put("artificial intelligence", "work"); put("database", "work");
        
        // Student
        put("student", "student"); put("study", "student"); put("college", "student");
        put("school", "student"); put("university", "student"); put("course", "student");
        put("learning", "student"); put("education", "student"); put("research", "student");
        put("thesis", "student"); put("project", "student"); put("assignment", "student");
        put("homework", "student"); put("online class", "student"); put("zoom", "student");
        put("teams", "student"); put("google meet", "student");
        
        // Streaming
        put("streaming", "streaming"); put("stream", "streaming"); put("youtube", "streaming");
        put("twitch", "streaming"); put("content creation", "streaming"); put("vlogging", "streaming");
        put("podcast", "streaming"); put("live streaming", "streaming"); put("broadcasting", "streaming");
        
        // General
        put("home", "general"); put("family", "general"); put("general", "general");
        put("basic", "general"); put("simple", "general"); put("daily use", "general");
        put("browsing", "general"); put("email", "general"); put("social media", "general");
        put("facebook", "general"); put("instagram", "general"); put("whatsapp", "general");
        put("netflix", "general"); put("amazon prime", "general"); put("disney plus", "general");
    }};

    public static EnhancedRequirements parseEnhancedPrompt(String prompt, String userId) {
        EnhancedRequirements req = new EnhancedRequirements();
        
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                return req;
            }

            // Get conversation context
            ConversationContext context = getContext(userId);
            context.history.add(prompt);
            context.lastInteraction = System.currentTimeMillis();

            String lower = prompt.toLowerCase().trim();
            
            // Language detection
            req.language = detectLanguage(lower);
            
            // Enhanced budget extraction
            req.budget = extractBudget(lower, context);
            
            // Use case detection with confidence
            req.useCase = detectUseCase(lower, context);
            
            // Priority detection
            req.priority = detectPriority(lower);
            
            // Component preferences
            req.specificComponents = extractComponents(lower);
            
            // Advanced preferences
            extractPreferences(lower, req);
            
            // Context adjustments
            applyContext(req, context);
            
            // Update context
            context.lastReq = req;
            
            System.out.println("Enhanced parsing: " + req.useCase + " | Budget: " + req.budget + " | Priority: " + req.priority);
            
        } catch (Exception e) {
            System.err.println("Enhanced parsing error: " + e.getMessage());
            req.useCase = "general";
            req.budget = 60000;
            req.priority = "balanced";
        }
        
        return req;
    }

    private static String detectLanguage(String prompt) {
        if (prompt.matches(".*[\u0C00-\u0C7F]+.*")) return "te";
        if (prompt.matches(".*[\u0900-\u097F]+.*")) return "hi";
        if (prompt.matches(".*[\u0980-\u09FF]+.*")) return "bn";
        if (prompt.matches(".*[\u0A80-\u0AFF]+.*")) return "gu";
        if (prompt.matches(".*[\u0B80-\u0BFF]+.*")) return "ta";
        if (prompt.matches(".*[\u0C80-\u0CFF]+.*")) return "ka";
        if (prompt.matches(".*[\u0D00-\u0D7F]+.*")) return "ml";
        return "en";
    }

    private static int extractBudget(String prompt, ConversationContext context) {
        Pattern[] patterns = {
            Pattern.compile("(?:₹|rs\\.?|rupees|budget|price|cost|amount|spend|invest)[\\s]*([0-9,]+)"),
            Pattern.compile("([0-9,]+)[\\s]*(?:₹|rs\\.?|rupees|thousand|k|lakh|lakhs)"),
            Pattern.compile("(?:under|below|within|max|maximum)[\\s]+(?:₹|rs\\.?|rupees)?[\\s]*([0-9,]+)"),
            Pattern.compile("([0-9,]+)[\\s]*(?:thousand|k)"),
            Pattern.compile("([0-9,]+)[\\s]*(?:lakh|lakhs|l)"),
            Pattern.compile("(?:around|about|approximately)[\\s]*(?:₹|rs\\.?|rupees)?[\\s]*([0-9,]+)")
        };
        
        for (Pattern p : patterns) {
            Matcher m = p.matcher(prompt);
            if (m.find()) {
                try {
                    String num = m.group(1).replace(",", "");
                    int budget = Integer.parseInt(num);
                    
                    if (prompt.contains("thousand") || prompt.contains("k")) {
                        if (budget < 1000) budget *= 1000;
                    } else if (prompt.contains("lakh") || prompt.contains("l")) {
                        budget *= 100000;
                    }
                    
                    return budget;
                } catch (Exception ignored) {}
            }
        }
        
        return inferBudget(prompt, context);
    }

    private static int inferBudget(String prompt, ConversationContext context) {
        String fullContext = String.join(" ", context.history).toLowerCase();
        
        if (fullContext.contains("budget") || fullContext.contains("cheap") || fullContext.contains("low cost")) {
            return 40000;
        } else if (fullContext.contains("mid") || fullContext.contains("medium")) {
            return 70000;
        } else if (fullContext.contains("high") || fullContext.contains("premium") || fullContext.contains("expensive")) {
            return 120000;
        } else if (fullContext.contains("gaming") && fullContext.contains("high")) {
            return 100000;
        } else if (fullContext.contains("editing") && fullContext.contains("professional")) {
            return 150000;
        } else if (fullContext.contains("work") && fullContext.contains("basic")) {
            return 50000;
        }
        
        return 60000;
    }

    private static String detectUseCase(String prompt, ConversationContext context) {
        Map<String, Integer> scores = new HashMap<>();
        
        // Score current prompt
        for (Map.Entry<String, String> entry : USE_CASE_MAP.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                String useCase = entry.getValue();
                scores.put(useCase, scores.getOrDefault(useCase, 0) + 1);
            }
        }
        
        // Score conversation history
        String fullContext = String.join(" ", context.history).toLowerCase();
        for (Map.Entry<String, String> entry : USE_CASE_MAP.entrySet()) {
            if (fullContext.contains(entry.getKey())) {
                String useCase = entry.getValue();
                scores.put(useCase, scores.getOrDefault(useCase, 0) + 1);
            }
        }
        
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("general");
    }

    private static String detectPriority(String prompt) {
        if (prompt.contains("performance") || prompt.contains("speed") || prompt.contains("fast") || 
            prompt.contains("powerful") || prompt.contains("best") || prompt.contains("top") || 
            prompt.contains("high end") || prompt.contains("premium")) {
            return "performance";
        } else if (prompt.contains("budget") || prompt.contains("cheap") || prompt.contains("affordable") || 
                   prompt.contains("low cost") || prompt.contains("economical") || prompt.contains("value")) {
            return "budget";
        }
        return "balanced";
    }

    private static List<String> extractComponents(String prompt) {
        List<String> components = new ArrayList<>();
        String[] componentKeywords = {
            "processor", "cpu", "intel", "amd", "ryzen", "core i",
            "graphics", "gpu", "nvidia", "rtx", "gtx", "radeon",
            "ram", "memory", "ddr", "motherboard", "mobo", "board",
            "storage", "ssd", "hdd", "nvme", "hard drive",
            "power supply", "psu", "cabinet", "case", "cooling", "fan", "cooler",
            "monitor", "display", "screen", "keyboard", "mouse"
        };
        
        for (String keyword : componentKeywords) {
            if (prompt.contains(keyword)) {
                components.add(keyword);
            }
        }
        
        return components;
    }

    private static void extractPreferences(String prompt, EnhancedRequirements req) {
        // Form factor
        if (prompt.contains("mini") || prompt.contains("small") || prompt.contains("compact")) {
            req.formFactor = "itx";
        } else if (prompt.contains("micro") || prompt.contains("matx")) {
            req.formFactor = "matx";
        }
        
        // RGB
        if (prompt.contains("rgb") || prompt.contains("led") || prompt.contains("lighting")) {
            req.rgb = true;
        }
        
        // Noise level
        if (prompt.contains("quiet") || prompt.contains("silent")) {
            req.noiseLevel = "quiet";
        } else if (prompt.contains("performance") && prompt.contains("noise")) {
            req.noiseLevel = "performance";
        }
        
        // Cooling
        if (prompt.contains("liquid") || prompt.contains("water")) {
            req.cooling = "liquid";
        }
        
        // Storage
        if (prompt.contains("ssd") && !prompt.contains("hdd")) {
            req.storageType = "ssd";
        } else if (prompt.contains("nvme")) {
            req.storageType = "nvme";
        } else if (prompt.contains("hdd") && !prompt.contains("ssd")) {
            req.storageType = "hdd";
        }
        
        // Brand preference
        if (prompt.contains("intel") && !prompt.contains("amd")) {
            req.brandPreference = "intel";
        } else if (prompt.contains("amd") && !prompt.contains("intel")) {
            req.brandPreference = "amd";
        } else if (prompt.contains("nvidia") && !prompt.contains("amd")) {
            req.brandPreference = "nvidia";
        }
        
        // Experience level
        if (prompt.contains("expert") || prompt.contains("advanced") || prompt.contains("professional")) {
            req.experience = "expert";
        } else if (prompt.contains("intermediate") || prompt.contains("moderate")) {
            req.experience = "intermediate";
        } else if (prompt.contains("beginner") || prompt.contains("first time")) {
            req.experience = "beginner";
        }
        
        // Urgency
        if (prompt.contains("urgent") || prompt.contains("asap") || prompt.contains("quick")) {
            req.urgency = "urgent";
        } else if (prompt.contains("flexible") || prompt.contains("no rush")) {
            req.urgency = "flexible";
        }
        
        // Future proofing
        if (prompt.contains("future") && prompt.contains("proof")) {
            req.futureProofing = "maximum";
        }
        
        // Peripherals
        if (prompt.contains("monitor") || prompt.contains("keyboard") || prompt.contains("mouse") || 
            prompt.contains("peripherals") || prompt.contains("accessories")) {
            req.includePeripherals = true;
        }
        
        // Overclocking
        if (prompt.contains("overclock") || prompt.contains("oc")) {
            req.overclocking = "mild";
        }
        
        // Streaming
        if (prompt.contains("stream") || prompt.contains("broadcast")) {
            req.streaming = "basic";
        }
        
        // Multitasking
        if (prompt.contains("multitask") || prompt.contains("multiple")) {
            req.multitasking = "heavy";
        }
    }

    private static void applyContext(EnhancedRequirements req, ConversationContext context) {
        if (context.lastReq != null) {
            if (context.lastReq.budget > 0 && req.budget == 0) {
                req.budget = context.lastReq.budget;
            }
            if (context.lastReq.useCase.equals("gaming") && req.useCase.equals("general")) {
                req.useCase = "gaming";
            }
        }
        
        if (req.budget == 0) {
            switch (req.useCase) {
                case "gaming": req.budget = 80000; break;
                case "editing": req.budget = 100000; break;
                case "work": req.budget = 60000; break;
                case "student": req.budget = 50000; break;
                case "streaming": req.budget = 90000; break;
                default: req.budget = 60000;
            }
        }
    }

    private static ConversationContext getContext(String userId) {
        return contexts.computeIfAbsent(userId, k -> {
            ConversationContext context = new ConversationContext();
            context.lastInteraction = System.currentTimeMillis();
            return context;
        });
    }

    public static void clearContext(String userId) {
        contexts.remove(userId);
    }

    public static List<String> getHistory(String userId) {
        ConversationContext context = contexts.get(userId);
        return context != null ? new ArrayList<>(context.history) : new ArrayList<>();
    }
} 