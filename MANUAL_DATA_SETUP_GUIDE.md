# Manual Data Setup Guide for PC Cooker

## 🚀 Quick Start

### 1. **Access Admin Panel**
- Open the app
- Go to Profile tab
- Click on "Help & Support" (this opens Admin Panel for now)

### 2. **Add Sample Components**
Use the sample data provided in `SAMPLE_COMPONENT_DATA.md` to add components to Firebase.

## 📊 Recommended Component Count

### **Phase 1: Essential Components (50-75 items)**
```
PROCESSOR: 10-12 models
GRAPHIC CARD: 8-10 models  
RAM: 6-8 models
MOTHERBOARD: 8-10 models
STORAGE: 8-10 models
POWER SUPPLY: 6-8 models
CABINET: 5-6 models
```

### **Phase 2: Extended Catalog (100-150 items)**
- Add more variants of each category
- Include different price ranges
- Add popular brands and models

## 💰 Price Strategy

### **Markup Guidelines**
- **Processors**: 5-8% markup
- **Graphics Cards**: 3-5% markup  
- **RAM/Storage**: 8-12% markup
- **Motherboards**: 10-15% markup
- **Power Supplies**: 12-18% markup
- **Cases**: 15-20% markup

### **Price Research Sources**
1. **Amazon.in** - Check current prices
2. **Flipkart.com** - Compare prices
3. **PrimeABGB** - Indian retailer
4. **Vedant Computers** - Indian retailer
5. **MD Computers** - Indian retailer

## 🔧 Admin Panel Features

### **Add New Component**
- Component name
- Price (₹)
- Description
- Category selection
- Automatic default values for other fields

### **Manage Existing Components**
- Edit prices individually
- Toggle stock status
- Delete components
- View all components in a list

### **Bulk Actions**
- Update all prices (future feature)
- Export component data
- Import component data

## 📱 How to Use Admin Panel

### **Adding Components**
1. Open Admin Panel
2. Fill in component details:
   - **Name**: "Intel Core i5-12400F"
   - **Price**: "15500"
   - **Description**: "6 cores, 12 threads, 2.5GHz base, 4.4GHz boost"
   - **Category**: Select from dropdown
3. Click "Add Component"

### **Editing Prices**
1. Find component in the list
2. Click "Edit Price"
3. Enter new price
4. Click "Update"

### **Managing Stock**
1. Find component in the list
2. Click "Toggle Stock"
3. Stock status will change instantly

## 🎯 Sample Component Data

### **Popular Processors**
```
Intel Core i3-12100F - ₹8,500
Intel Core i5-12400F - ₹15,500
Intel Core i7-12700F - ₹25,000
AMD Ryzen 5 5600X - ₹18,500
AMD Ryzen 7 5800X - ₹28,000
```

### **Popular Graphics Cards**
```
NVIDIA RTX 4060 - ₹28,000
NVIDIA RTX 4060 Ti - ₹35,000
NVIDIA RTX 4070 - ₹50,000
AMD RX 6600 - ₹22,000
AMD RX 6700 XT - ₹38,000
```

### **Popular RAM**
```
Corsair Vengeance LPX 8GB - ₹2,500
Corsair Vengeance LPX 16GB - ₹4,500
G.Skill Ripjaws V 16GB - ₹4,200
G.Skill Ripjaws V 32GB - ₹8,500
```

## 📈 Price Update Schedule

### **Weekly Updates**
- High-demand components
- Popular gaming components
- Components with frequent price changes

### **Monthly Updates**
- Standard components
- Mid-range components
- Components with stable prices

### **Quarterly Updates**
- Low-demand components
- Specialized components
- Components with minimal price changes

## 🔍 Quality Control

### **Before Adding Components**
- ✅ Verify current market prices
- ✅ Check component compatibility
- ✅ Ensure accurate descriptions
- ✅ Add proper specifications
- ✅ Include stock status

### **Regular Maintenance**
- ✅ Weekly price checks
- ✅ Monthly inventory review
- ✅ Quarterly catalog cleanup
- ✅ Annual price strategy review

## 🛠️ Technical Setup

### **Firebase Structure**
```
pc_components/
  PROCESSOR/
    items/
      cpu_1/
        name: "Intel Core i5-12400F"
        price: 15500
        category: "PROCESSOR"
        ...
  GRAPHIC CARD/
    items/
      gpu_1/
        name: "NVIDIA RTX 4060 Ti"
        price: 35000
        category: "GRAPHIC CARD"
        ...
```

### **Component Model Fields**
```java
String id, name, category, brand, model, description, imageUrl, amazonUrl
double price, rating
int ratingCount, quantity
boolean inStock
Map<String, Object> specifications
```

## 🎯 Success Metrics

### **Track These Metrics**
- Total components added
- Price update frequency
- Stock accuracy
- User search success rate
- Auto-select accuracy
- Sales conversion rate

### **Optimization Goals**
- 100+ quality components
- Weekly price updates
- 95% stock accuracy
- High user satisfaction
- Competitive pricing

## 🚀 Next Steps

1. **Start with 50 essential components**
2. **Add components weekly**
3. **Update prices monthly**
4. **Monitor user feedback**
5. **Scale based on demand**

## 📞 Support

If you need help with:
- Adding components
- Setting up Firebase
- Managing prices
- Technical issues

Contact your development team or refer to the Firebase documentation.

---

**Remember**: Quality over quantity. Start with popular, well-reviewed components and expand based on user demand and market trends. 