# Razorpay Test Configuration Guide

## 🔧 Current Test Setup

### Test Key Used
```
rzp_test_51H5j8KJqR5vX2
```

### Test Card Details (for testing)
```
Card Number: 4111 1111 1111 1111
Expiry: Any future date (e.g., 12/25)
CVV: Any 3 digits (e.g., 123)
Name: Any name
```

### Test UPI Details
```
UPI ID: success@razorpay
```

## 🧪 Testing Steps

1. **Add items to cart** (minimum ₹1)
2. **Select delivery address**
3. **Click Pay button**
4. **Use test credentials above**

## 🔍 Common Issues & Solutions

### BAD_REQUEST_ERROR
- **Cause**: Invalid payment configuration
- **Solution**: Simplified configuration implemented

### payment_authentication
- **Cause**: Test environment issues
- **Solution**: Use provided test credentials

### Invalid Amount
- **Cause**: Amount less than ₹1
- **Solution**: Ensure cart total is at least ₹1

## 📱 Test Payment Flow

1. **Cart → Address Selection → Payment**
2. **Razorpay opens with test key**
3. **Use test card/UPI credentials**
4. **Payment should succeed**

## 🚨 If Still Getting Errors

1. **Check internet connection**
2. **Ensure app has internet permission**
3. **Try different test credentials**
4. **Check if Razorpay SDK is properly initialized**

## 📞 Support

If issues persist, check:
- Razorpay dashboard for test key validity
- Network connectivity
- App permissions
- Firebase connectivity for order saving 