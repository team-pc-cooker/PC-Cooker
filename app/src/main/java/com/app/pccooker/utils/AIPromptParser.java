package com.app.pccooker.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIPromptParser {
    public static class Requirements {
        public String useCase = "general"; // gaming, editing, work, etc.
        public int budget = 0; // in INR
        public String language = "en";
    }

    public static Requirements parsePrompt(String prompt) {
        Requirements req = new Requirements();
        
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                return req;
            }
            
            String lower = prompt.toLowerCase().trim();

            // Detect Telugu (more comprehensive)
            if (lower.matches(".*[\u0C00-\u0C7F]+.*")) {
                req.language = "te";
            }

            // Enhanced budget extraction (₹, rs, rupees, etc.) - more comprehensive
            Pattern[] budgetPatterns = {
                Pattern.compile("(?:₹|rs\\.?|rupees|dabbulatho|dabbu|budget|price|cost|amount|spend|invest)[\\s]*([0-9,]+)"),
                Pattern.compile("([0-9,]+)[\\s]*(?:₹|rs\\.?|rupees|thousand|k|lakh|lakhs)"),
                Pattern.compile("(?:under|below|within|max|maximum)[\\s]+(?:₹|rs\\.?|rupees)?[\\s]*([0-9,]+)"),
                Pattern.compile("([0-9,]+)[\\s]*(?:thousand|k)"),
                Pattern.compile("([0-9,]+)[\\s]*(?:lakh|lakhs|l)")
            };
            
            for (Pattern p : budgetPatterns) {
                Matcher m = p.matcher(lower);
                if (m.find()) {
                    String num = m.group(1).replace(",", "");
                    try { 
                        int parsedBudget = Integer.parseInt(num);
                        
                        // Handle thousands and lakhs
                        if (lower.contains("thousand") || lower.contains("k")) {
                            if (parsedBudget < 1000) parsedBudget *= 1000;
                        } else if (lower.contains("lakh") || lower.contains("l")) {
                            parsedBudget *= 100000;
                        }
                        
                        req.budget = parsedBudget;
                        System.out.println("Extracted budget: " + req.budget);
                        break;
                    } catch (Exception ignored) {
                        System.out.println("Failed to parse budget: " + num);
                    }
                }
            }

            // Enhanced use-case detection (English) - more comprehensive
            if (lower.contains("gaming") || lower.contains("game") || lower.contains("play") || 
                lower.contains("gamer") || lower.contains("esports") || lower.contains("fortnite") ||
                lower.contains("pubg") || lower.contains("valorant") || lower.contains("csgo")) {
                req.useCase = "gaming";
            } else if (lower.contains("editing") || lower.contains("edit") || lower.contains("video") || 
                       lower.contains("photo") || lower.contains("photoshop") || lower.contains("premiere") ||
                       lower.contains("after effects") || lower.contains("blender") || lower.contains("3d") ||
                       lower.contains("rendering") || lower.contains("animation")) {
                req.useCase = "editing";
            } else if (lower.contains("work") || lower.contains("office") || lower.contains("business") || 
                       lower.contains("productivity") || lower.contains("excel") || lower.contains("word") ||
                       lower.contains("powerpoint") || lower.contains("coding") || lower.contains("programming") ||
                       lower.contains("development") || lower.contains("software")) {
                req.useCase = "work";
            } else if (lower.contains("student") || lower.contains("study") || lower.contains("college") || 
                       lower.contains("school") || lower.contains("university") || lower.contains("course") ||
                       lower.contains("learning") || lower.contains("education")) {
                req.useCase = "student";
            } else if (lower.contains("streaming") || lower.contains("stream") || lower.contains("youtube") ||
                       lower.contains("twitch") || lower.contains("content creation")) {
                req.useCase = "gaming"; // Streaming is similar to gaming
            } else if (lower.contains("home") || lower.contains("family") || lower.contains("general") ||
                       lower.contains("basic") || lower.contains("simple")) {
                req.useCase = "general";
            } else {
                req.useCase = "general";
            }

            // Enhanced use-case detection (Telugu) - more comprehensive
            if (req.language.equals("te")) {
                if (lower.contains("pc kavali") || lower.contains("gaming ki") || lower.contains("game") || 
                    lower.contains("aadukovadam") || lower.contains("gaming kosam") || lower.contains("game aadataniki")) {
                    req.useCase = "gaming";
                }
                if (lower.contains("editing ki") || lower.contains("edit cheyyali") || lower.contains("video edit") || 
                    lower.contains("photo edit") || lower.contains("editing kosam") || lower.contains("edit cheyataniki")) {
                    req.useCase = "editing";
                }
                if (lower.contains("paniki") || lower.contains("office kosam") || lower.contains("business ki") ||
                    lower.contains("work ki") || lower.contains("pani cheyataniki")) {
                    req.useCase = "work";
                }
                if (lower.contains("vidyarthi") || lower.contains("student") || lower.contains("study") ||
                    lower.contains("chaduvukotaniki") || lower.contains("college ki")) {
                    req.useCase = "student";
                }
                if (lower.contains("intiki") || lower.contains("family ki") || lower.contains("general ga")) {
                    req.useCase = "general";
                }
            }

            // Fallback budget detection if no specific budget mentioned
            if (req.budget == 0) {
                if (lower.contains("budget") || lower.contains("cheap") || lower.contains("low cost")) {
                    req.budget = 40000; // Budget build
                } else if (lower.contains("mid") || lower.contains("medium")) {
                    req.budget = 70000; // Mid-range build
                } else if (lower.contains("high") || lower.contains("premium") || lower.contains("expensive")) {
                    req.budget = 120000; // High-end build
                } else {
                    req.budget = 60000; // Default budget
                }
            }

            System.out.println("Parsed requirements - Use case: " + req.useCase + ", Budget: " + req.budget + ", Language: " + req.language);
            
        } catch (Exception e) {
            System.err.println("Error parsing prompt: " + e.getMessage());
            // Return default requirements on error
            req.useCase = "general";
            req.budget = 60000;
            req.language = "en";
        }
        
        return req;
    }
} 