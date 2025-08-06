# PC Cooker - Complete Play Console Upload Guide

## 🚀 **STEP-BY-STEP UPLOAD PROCESS**

### **PHASE 1: PREPARATION (You do this)**

#### **Step 1: Create App Signing Key**
```bash
# Run this in PC-Cooker directory
./create_keystore.bat

# When prompted, enter:
# - Password: Choose a strong password (save it!)
# - Name: Team PC Cooker
# - Organization: PC Cooker
# - City: Your city
# - State: Your state  
# - Country: IN
```

#### **Step 2: Update Build Configuration**
In `app/build.gradle`, replace the password placeholders:
```gradle
signingConfigs {
    release {
        storeFile file('../pc-cooker-release.keystore')
        storePassword 'YOUR_ACTUAL_KEYSTORE_PASSWORD'  // Replace this
        keyAlias 'pc-cooker'
        keyPassword 'YOUR_ACTUAL_KEY_PASSWORD'         // Replace this
    }
}
```

#### **Step 3: Build Release APK**
```bash
# Clean and build release version
./gradlew clean bundleRelease

# This creates: app/build/outputs/bundle/release/app-release.aab
```

#### **Step 4: Take Screenshots**
- Install app on device/emulator
- Follow `SCREENSHOT_GUIDE.md`
- Take 5-8 high-quality screenshots
- Save as PNG files (1080x1920 resolution)

#### **Step 5: Create Feature Graphic**
- Follow `FEATURE_GRAPHIC_DESIGN.md`
- Create 1024x500px graphic
- Use "Build Your Dream PC with AI" text
- Modern tech design

---

### **PHASE 2: PLAY CONSOLE SETUP (You do this)**

#### **Step 1: Access Play Console**
1. Go to [Google Play Console](https://play.google.com/console)
2. Sign in with your Google account
3. Pay $25 one-time developer fee (if not done)

#### **Step 2: Create New App**
1. Click "Create app"
2. **App name**: "PC Cooker - Build Your Dream PC"
3. **Default language**: English (United States)
4. **App or game**: App
5. **Free or paid**: Free
6. **Declarations**: Check all required boxes
7. Click "Create app"

#### **Step 3: Upload App Bundle**
1. Go to "Release" → "Production"
2. Click "Create new release"
3. Upload your `app-release.aab` file
4. **Release name**: "1.0.0 - Initial Release"
5. **Release notes**:
```
🎉 Welcome to PC Cooker v1.0.0!

✨ Features:
• Smart Auto-Build with AI assistance
• Real-time compatibility checking  
• 1000+ PC components database
• Budget optimization tools
• Build saving and management
• AI-powered recommendations

🎮 Perfect for gaming PCs, workstations, and budget builds!
```

---

### **PHASE 3: STORE LISTING (Copy-paste ready)**

#### **App Information**
- **App name**: PC Cooker - Build Your Dream PC
- **Short description**: Build custom PCs with AI assistance, compatibility checks & price comparisons
- **Full description**: 
```
🚀 Build Your Dream PC with PC Cooker!

PC Cooker is the ultimate PC building companion that makes creating your perfect computer simple, smart, and affordable. Whether you're a gaming enthusiast, content creator, or building your first PC, our AI-powered platform guides you every step of the way.

✨ KEY FEATURES:

🎯 Smart Auto-Build
• Select preferences (AMD/Intel, DDR4/DDR5, HDD/SSD, NVIDIA/AMD)
• AI automatically selects compatible components within budget
• Get performance scores and optimization suggestions

🔧 Intelligent Compatibility Checker
• Real-time compatibility validation
• Socket, RAM, and PSU compatibility warnings
• Smart suggestions for balanced builds

💰 Budget-Friendly Options
• Components for every budget (₹15,000 to ₹5,00,000+)
• Price comparison across specifications
• DDR3 budget builds for cost-conscious users

🎮 Build Types
• Gaming PCs - Optimized for high FPS gaming
• Workstation PCs - Perfect for content creation
• Budget PCs - Maximum value for money
• Office PCs - Reliable for productivity

🤖 AI Assistant
• Get personalized PC building advice
• Ask questions in natural language
• Receive expert recommendations

📊 Advanced Features
• Save and manage multiple builds
• Component trending and popularity scores
• Build performance analytics
• Export build specifications

Perfect for:
👨‍💻 First-time PC builders
🎮 Gaming enthusiasts  
🎬 Content creators
💼 IT professionals
🎓 Students and developers

Download PC Cooker today and build your dream PC with confidence!
```

#### **Graphics**
1. **App icon**: Upload your app icon (512x512px)
2. **Feature graphic**: Upload your created feature graphic (1024x500px)
3. **Screenshots**: Upload your 5-8 screenshots

#### **Categorization**
- **App category**: Tools
- **Tags**: PC builder, computer builder, gaming PC, custom PC, components
- **Content rating**: Everyone

---

### **PHASE 4: PRIVACY & POLICY**

#### **Privacy Policy**
- **URL**: `https://github.com/team-pc-cooker/PC-Cooker/blob/master/PRIVACY_POLICY.md`
- Or host on your own website

#### **App Permissions**
Explain each permission:
- **Internet**: Required to fetch component data and prices
- **Network State**: Check connectivity for offline functionality
- **Phone State**: Used only for payment security (optional)
- **Record Audio**: For AI voice commands (optional, user permission)

#### **Data Safety**
- **Data collected**: Email, name, build preferences
- **Data shared**: None (no third-party sharing)
- **Data security**: Encrypted in transit, secure authentication
- **Data deletion**: Users can delete account and all data

---

### **PHASE 5: TESTING**

#### **Internal Testing (Required)**
1. Go to "Testing" → "Internal testing"
2. Upload same AAB file
3. Add your email as tester
4. Test app thoroughly
5. Fix any issues, upload new version

#### **Closed Testing (Recommended)**
1. Create closed testing track
2. Add 20+ testers (friends, family, beta users)
3. Gather feedback for 1-2 weeks
4. Make improvements based on feedback

---

### **PHASE 6: REVIEW & LAUNCH**

#### **Submit for Review**
1. Complete all required sections
2. Click "Send for review"
3. Review typically takes 3-7 days
4. Google will email you with results

#### **Common Rejection Reasons**
- **Incomplete store listing**: Missing screenshots or descriptions
- **Policy violations**: Inappropriate content or permissions
- **Technical issues**: App crashes or doesn't function properly
- **Metadata issues**: Misleading descriptions or keywords

#### **After Approval**
- App goes live automatically
- Monitor crash reports in Play Console
- Respond to user reviews promptly
- Plan regular updates

---

## 📋 **FINAL CHECKLIST**

Before submitting:
- [ ] App builds and runs without crashes
- [ ] All features work as described
- [ ] Screenshots show actual app functionality
- [ ] Privacy policy is accessible and complete
- [ ] App description is accurate and compelling
- [ ] Feature graphic looks professional
- [ ] All required fields in Play Console are filled
- [ ] App has been tested on multiple devices
- [ ] Release notes are clear and informative

---

## 🆘 **IF YOU NEED HELP**

### **What I Can Help With**:
- Reviewing your store listing text
- Checking screenshot quality
- Troubleshooting build issues
- Updating app code/configuration
- Creating marketing materials

### **What You Need To Do**:
- Create Google Play Console account ($25 fee)
- Take screenshots from your running app
- Upload files to Play Console
- Handle Google's review process

---

## 📞 **SUPPORT CONTACTS**

- **Play Console Help**: Available in Google Play Console
- **Developer Policy**: https://play.google.com/about/developer-content-policy/
- **Technical Issues**: support.google.com/googleplay/android-developer

---

**🎉 You're ready to launch PC Cooker on Google Play Store!**

Follow this guide step by step, and your app will be live within 1-2 weeks. The hardest part (building the app) is already done - now it's just following the process!