package com.app.pccooker.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedAIPromptParser {
    
    public static class AdvancedRequirements {
        // Core requirements
        public String useCase = "general";
        public int budget = 0;
        public String language = "en";
        public String priority = "balanced"; // performance, budget, quality
        public List<String> specificComponents = new ArrayList<>();
        public Map<String, String> preferences = new HashMap<>();
        
        // User preferences
        public String urgency = "normal"; // urgent, normal, flexible
        public String experience = "beginner"; // beginner, intermediate, expert
        public boolean includePeripherals = false;
        public String formFactor = "atx"; // atx, matx, itx
        public String colorTheme = "any";
        public boolean rgb = false;
        public String brandPreference = "any";
        
        // Performance preferences
        public String noiseLevel = "normal"; // quiet, normal, performance
        public String powerEfficiency = "normal"; // efficient, normal, performance
        public String futureProofing = "moderate"; // minimal, moderate, maximum
        public String upgradePath = "moderate"; // minimal, moderate, maximum
        public String overclocking = "none"; // none, mild, aggressive
        public String multitasking = "moderate"; // light, moderate, heavy
        
        // Technical preferences
        public String storageType = "mixed"; // hdd, ssd, nvme, mixed
        public String cooling = "air"; // air, liquid, hybrid
        public String connectivity = "standard"; // basic, standard, premium
        public String portability = "none"; // none, moderate, high
        public String durability = "standard"; // basic, standard, premium
        
        // Service preferences
        public String warranty = "standard"; // standard, extended, premium
        public String delivery = "normal"; // urgent, normal, flexible
        public String assembly = "self"; // self, professional, guided
        public String support = "basic"; // basic, premium, expert
        
        // Special use cases
        public String streaming = "no"; // no, basic, professional
        public String aesthetics = "standard"; // minimal, standard, premium
    }

    // Context memory for conversation continuity
    private static Map<String, ConversationContext> conversationContexts = new HashMap<>();
    
    public static class ConversationContext {
        public String userId;
        public List<String> conversationHistory = new ArrayList<>();
        public AdvancedRequirements lastRequirements;
        public long lastInteraction;
        public String sessionId;
        public Map<String, Object> userPreferences = new HashMap<>();
    }

    // Enhanced keyword mappings
    private static final Map<String, String> USE_CASE_KEYWORDS = new HashMap<String, String>() {{
        // Gaming
        put("gaming", "gaming");
        put("game", "gaming");
        put("play", "gaming");
        put("gamer", "gaming");
        put("esports", "gaming");
        put("fortnite", "gaming");
        put("pubg", "gaming");
        put("valorant", "gaming");
        put("csgo", "gaming");
        put("apex", "gaming");
        put("cod", "gaming");
        put("fifa", "gaming");
        put("racing", "gaming");
        put("simulation", "gaming");
        put("vr", "gaming");
        put("virtual reality", "gaming");
        
        // Content Creation
        put("editing", "editing");
        put("edit", "editing");
        put("video", "editing");
        put("photo", "editing");
        put("photoshop", "editing");
        put("premiere", "editing");
        put("after effects", "editing");
        put("blender", "editing");
        put("3d", "editing");
        put("rendering", "editing");
        put("animation", "editing");
        put("maya", "editing");
        put("cinema 4d", "editing");
        put("davinci", "editing");
        put("final cut", "editing");
        put("lightroom", "editing");
        put("illustrator", "editing");
        put("indesign", "editing");
        put("autocad", "editing");
        put("solidworks", "editing");
        put("fusion 360", "editing");
        
        // Work & Productivity
        put("work", "work");
        put("office", "work");
        put("business", "work");
        put("productivity", "work");
        put("excel", "work");
        put("word", "work");
        put("powerpoint", "work");
        put("coding", "work");
        put("programming", "work");
        put("development", "work");
        put("software", "work");
        put("web development", "work");
        put("app development", "work");
        put("data analysis", "work");
        put("machine learning", "work");
        put("ai", "work");
        put("artificial intelligence", "work");
        put("database", "work");
        put("server", "work");
        put("virtualization", "work");
        put("docker", "work");
        put("kubernetes", "work");
        
        // Student
        put("student", "student");
        put("study", "student");
        put("college", "student");
        put("school", "student");
        put("university", "student");
        put("course", "student");
        put("learning", "student");
        put("education", "student");
        put("research", "student");
        put("thesis", "student");
        put("project", "student");
        put("assignment", "student");
        put("homework", "student");
        put("online class", "student");
        put("zoom", "student");
        put("teams", "student");
        put("google meet", "student");
        
        // Streaming
        put("streaming", "streaming");
        put("stream", "streaming");
        put("youtube", "streaming");
        put("twitch", "streaming");
        put("content creation", "streaming");
        put("vlogging", "streaming");
        put("podcast", "streaming");
        put("live streaming", "streaming");
        put("broadcasting", "streaming");
        
        // General
        put("home", "general");
        put("family", "general");
        put("general", "general");
        put("basic", "general");
        put("simple", "general");
        put("daily use", "general");
        put("browsing", "general");
        put("email", "general");
        put("social media", "general");
        put("facebook", "general");
        put("instagram", "general");
        put("whatsapp", "general");
        put("netflix", "general");
        put("amazon prime", "general");
        put("disney plus", "general");
    }};

    // Priority keywords
    private static final Map<String, String> PRIORITY_KEYWORDS = new HashMap<String, String>() {{
        put("performance", "performance");
        put("speed", "performance");
        put("fast", "performance");
        put("powerful", "performance");
        put("best", "performance");
        put("top", "performance");
        put("high end", "performance");
        put("premium", "performance");
        put("budget", "budget");
        put("cheap", "budget");
        put("affordable", "budget");
        put("low cost", "budget");
        put("economical", "budget");
        put("value", "budget");
        put("balanced", "balanced");
        put("moderate", "balanced");
        put("middle", "balanced");
        put("mid range", "balanced");
        put("reasonable", "balanced");
    }};

    // Component-specific keywords
    private static final Map<String, String> COMPONENT_KEYWORDS = new HashMap<String, String>() {{
        put("processor", "processor");
        put("cpu", "processor");
        put("intel", "processor");
        put("amd", "processor");
        put("ryzen", "processor");
        put("core i", "processor");
        put("graphics", "graphic_card");
        put("gpu", "graphic_card");
        put("nvidia", "graphic_card");
        put("rtx", "graphic_card");
        put("gtx", "graphic_card");
        put("radeon", "graphic_card");
        put("ram", "ram");
        put("memory", "ram");
        put("ddr", "ram");
        put("motherboard", "motherboard");
        put("mobo", "motherboard");
        put("board", "motherboard");
        put("storage", "storage");
        put("ssd", "storage");
        put("hdd", "storage");
        put("nvme", "storage");
        put("hard drive", "storage");
        put("power supply", "power_supply");
        put("psu", "power_supply");
        put("cabinet", "cabinet");
        put("case", "cabinet");
        put("cooling", "cooling");
        put("fan", "cooling");
        put("cooler", "cooling");
        put("monitor", "monitor");
        put("display", "monitor");
        put("screen", "monitor");
        put("keyboard", "keyboard");
        put("mouse", "mouse");
    }};

    public static AdvancedRequirements parseAdvancedPrompt(String prompt, String userId) {
        AdvancedRequirements req = new AdvancedRequirements();
        
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                return req;
            }

            // Get or create conversation context
            ConversationContext context = getConversationContext(userId);
            context.conversationHistory.add(prompt);
            context.lastInteraction = System.currentTimeMillis();

            String lower = prompt.toLowerCase().trim();
            
            // Language detection
            req.language = detectLanguage(lower);
            
            // Enhanced budget extraction with context
            req.budget = extractBudgetWithContext(lower, context);
            
            // Use case detection with confidence scoring
            req.useCase = detectUseCaseWithConfidence(lower, context);
            
            // Priority detection
            req.priority = detectPriority(lower);
            
            // Component preferences
            req.specificComponents = extractComponentPreferences(lower);
            
            // Advanced preferences
            extractAdvancedPreferences(lower, req);
            
            // Context-aware adjustments
            applyContextAdjustments(req, context);
            
            // Update context
            context.lastRequirements = req;
            
            System.out.println("Advanced parsing completed: " + req.useCase + " | Budget: " + req.budget + " | Priority: " + req.priority);
            
        } catch (Exception e) {
            System.err.println("Error in advanced prompt parsing: " + e.getMessage());
            e.printStackTrace();
            // Return sensible defaults
            req.useCase = "general";
            req.budget = 60000;
            req.priority = "balanced";
        }
        
        return req;
    }

    private static String detectLanguage(String prompt) {
        // Enhanced language detection
        if (prompt.matches(".*[\u0C00-\u0C7F]+.*")) {
            return "te"; // Telugu
        } else if (prompt.matches(".*[\u0900-\u097F]+.*")) {
            return "hi"; // Hindi
        } else if (prompt.matches(".*[\u0980-\u09FF]+.*")) {
            return "bn"; // Bengali
        } else if (prompt.matches(".*[\u0A80-\u0AFF]+.*")) {
            return "gu"; // Gujarati
        } else if (prompt.matches(".*[\u0B80-\u0BFF]+.*")) {
            return "ta"; // Tamil
        } else if (prompt.matches(".*[\u0C80-\u0CFF]+.*")) {
            return "ka"; // Kannada
        } else if (prompt.matches(".*[\u0D00-\u0D7F]+.*")) {
            return "ml"; // Malayalam
        } else if (prompt.matches(".*[\u0D80-\u0DFF]+.*")) {
            return "si"; // Sinhala
        }
        return "en"; // Default to English
    }

    private static int extractBudgetWithContext(String prompt, ConversationContext context) {
        // Multiple budget extraction patterns
        Pattern[] patterns = {
            Pattern.compile("(?:₹|rs\\.?|rupees|dabbulatho|dabbu|budget|price|cost|amount|spend|invest)[\\s]*([0-9,]+)"),
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
                    int parsedBudget = Integer.parseInt(num);
                    
                    // Handle multipliers
                    if (prompt.contains("thousand") || prompt.contains("k")) {
                        if (parsedBudget < 1000) parsedBudget *= 1000;
                    } else if (prompt.contains("lakh") || prompt.contains("l")) {
                        parsedBudget *= 100000;
                    }
                    
                    return parsedBudget;
                } catch (Exception ignored) {}
            }
        }
        
        // Context-aware budget inference
        return inferBudgetFromContext(prompt, context);
    }

    private static int inferBudgetFromContext(String prompt, ConversationContext context) {
        // Use conversation history and keywords to infer budget
        String fullContext = String.join(" ", context.conversationHistory).toLowerCase();
        
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
        
        return 60000; // Default
    }

    private static String detectUseCaseWithConfidence(String prompt, ConversationContext context) {
        Map<String, Integer> useCaseScores = new HashMap<>();
        
        // Score based on keywords
        for (Map.Entry<String, String> entry : USE_CASE_KEYWORDS.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                String useCase = entry.getValue();
                useCaseScores.put(useCase, useCaseScores.getOrDefault(useCase, 0) + 1);
            }
        }
        
        // Consider conversation history
        String fullContext = String.join(" ", context.conversationHistory).toLowerCase();
        for (Map.Entry<String, String> entry : USE_CASE_KEYWORDS.entrySet()) {
            if (fullContext.contains(entry.getKey())) {
                String useCase = entry.getValue();
                useCaseScores.put(useCase, useCaseScores.getOrDefault(useCase, 0) + 1);
            }
        }
        
        // Return highest scoring use case
        return useCaseScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("general");
    }

    private static String detectPriority(String prompt) {
        for (Map.Entry<String, String> entry : PRIORITY_KEYWORDS.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "balanced";
    }

    private static List<String> extractComponentPreferences(String prompt) {
        List<String> components = new ArrayList<>();
        for (Map.Entry<String, String> entry : COMPONENT_KEYWORDS.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                components.add(entry.getValue());
            }
        }
        return components;
    }

    private static void extractAdvancedPreferences(String prompt, AdvancedRequirements req) {
        // Form factor
        if (prompt.contains("mini") || prompt.contains("small") || prompt.contains("compact")) {
            req.formFactor = "itx";
        } else if (prompt.contains("micro") || prompt.contains("matx")) {
            req.formFactor = "matx";
        }
        
        // RGB preference
        if (prompt.contains("rgb") || prompt.contains("led") || prompt.contains("lighting")) {
            req.rgb = true;
        }
        
        // Noise level
        if (prompt.contains("quiet") || prompt.contains("silent")) {
            req.noiseLevel = "quiet";
        } else if (prompt.contains("performance") && prompt.contains("noise")) {
            req.noiseLevel = "performance";
        }
        
        // Cooling preference
        if (prompt.contains("liquid") || prompt.contains("water")) {
            req.cooling = "liquid";
        } else if (prompt.contains("air") && prompt.contains("cooling")) {
            req.cooling = "air";
        }
        
        // Storage preference
        if (prompt.contains("ssd") && !prompt.contains("hdd")) {
            req.storageType = "ssd";
        } else if (prompt.contains("nvme")) {
            req.storageType = "nvme";
        } else if (prompt.contains("hdd") && !prompt.contains("ssd")) {
            req.storageType = "hdd";
        }
        
        // Brand preferences
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
        } else if (prompt.contains("upgrade") && prompt.contains("path")) {
            req.upgradePath = "maximum";
        }
    }

    private static void applyContextAdjustments(AdvancedRequirements req, ConversationContext context) {
        // Adjust based on previous interactions
        if (context.lastRequirements != null) {
            // If user mentioned budget in previous message, use it as reference
            if (context.lastRequirements.budget > 0 && req.budget == 0) {
                req.budget = context.lastRequirements.budget;
            }
            
            // If user consistently mentions gaming, maintain gaming context
            if (context.lastRequirements.useCase.equals("gaming") && req.useCase.equals("general")) {
                req.useCase = "gaming";
            }
        }
        
        // Adjust budget based on use case if not specified
        if (req.budget == 0) {
            switch (req.useCase) {
                case "gaming":
                    req.budget = 80000;
                    break;
                case "editing":
                    req.budget = 100000;
                    break;
                case "work":
                    req.budget = 60000;
                    break;
                case "student":
                    req.budget = 50000;
                    break;
                case "streaming":
                    req.budget = 90000;
                    break;
                default:
                    req.budget = 60000;
            }
        }
    }

    public static ConversationContext getConversationContext(String userId) {
        return conversationContexts.computeIfAbsent(userId, k -> {
            ConversationContext context = new ConversationContext();
            context.userId = userId;
            context.sessionId = UUID.randomUUID().toString();
            return context;
        });
    }

    // Public method to clear conversation context
    public static void clearConversationContext(String userId) {
        conversationContexts.remove(userId);
    }

    // Public method to get conversation history
    public static List<String> getConversationHistory(String userId) {
        ConversationContext context = conversationContexts.get(userId);
        return context != null ? new ArrayList<>(context.conversationHistory) : new ArrayList<>();
    }
} 