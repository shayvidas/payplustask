# 🚀 Setup Guide - PayPlus Billing

## Prerequisites

Before you begin, ensure you have the following installed:

### Required Software
- ✅ **Android Studio**: Hedgehog (2023.1.1) or newer
- ✅ **JDK**: Version 17 or higher
- ✅ **Node.js**: Version 14+ (for backend server)
- ✅ **npm** or **yarn**: Package manager

### Verify Installation
```bash
# Check Java version
java -version
# Should show version 17+

# Check Node.js version
node --version
# Should show v14+

# Check npm version
npm --version
```

## 📦 Step 1: Backend Server Setup

### 1.1 Navigate to Server Directory
```bash
cd "/Users/shay.vidas/Documents/Android/ai task 2/payplusv2-android-test-server-e2a81a3fef1a"
```

### 1.2 Install Dependencies
```bash
npm install
```

### 1.3 Start the Server
```bash
npm run dev
```

**Expected Output:**
```
Express app running on port 8030!
```

### 1.4 Verify Server is Running
Open browser and navigate to:
- `http://localhost:8030`

Or test with curl:
```bash
curl -X POST http://localhost:8030/payment/billing/entry/headers \
  -H "Content-Type: application/json" \
  -d '{}'
```

**✅ Server is ready when you see JSON response with billing data.**

---

## 📱 Step 2: Android Project Setup

### 2.1 Open Project in Android Studio

1. Launch **Android Studio**
2. Click **"Open"**
3. Navigate to:
   ```
   /Users/shay.vidas/Documents/Android/ai task 2/PayPlusBilling
   ```
4. Click **"OK"**

### 2.2 Wait for Gradle Sync

Android Studio will automatically:
- Download required dependencies
- Configure the project
- Index files

**This may take 5-10 minutes on first run.**

### 2.3 Gradle Sync Issues?

If Gradle sync fails:

#### Option A: Use Android Studio's Gradle
```bash
# In Android Studio Terminal
./gradlew clean
./gradlew build
```

#### Option B: Invalidate Caches
1. Go to **File → Invalidate Caches**
2. Select **"Invalidate and Restart"**
3. Wait for restart and re-sync

#### Option C: Check Gradle Version
Ensure `gradle/wrapper/gradle-wrapper.properties` has:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

### 2.4 Configure Base URL (IMPORTANT!)

The app needs to connect to the server. The base URL differs based on your device:

#### For Android Emulator (Recommended for Testing)
✅ **Already configured correctly!**

File: `app/src/main/java/com/payplus/billing/di/NetworkModule.kt`
```kotlin
private const val BASE_URL = "http://10.0.2.2:8030/"
```

`10.0.2.2` is a special IP that maps to `localhost` on your host machine.

#### For Physical Android Device
❌ **Needs modification!**

1. Find your computer's local IP address:
   ```bash
   # macOS
   ifconfig | grep "inet " | grep -v 127.0.0.1
   
   # Example output: inet 192.168.1.100
   ```

2. Update `NetworkModule.kt`:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100:8030/"
   ```

3. **Ensure device and computer are on the same Wi-Fi network!**

---

## ▶️ Step 3: Run the App

### 3.1 Create/Start an Emulator

#### Create New Emulator:
1. Click **Device Manager** (phone icon in toolbar)
2. Click **"Create Device"**
3. Select **Pixel 5** or similar
4. Select system image: **API 34 (Android 14)**
5. Click **"Finish"**

#### Start Emulator:
- Click ▶️ play button next to device name

### 3.2 Run the App

1. Select your device/emulator from dropdown
2. Click the **green play button (Run)** or press `Ctrl+R` (Mac: `Cmd+R`)
3. Wait for build and installation

### 3.3 Expected Behavior

1. **App launches** with "PayPlus Billing" title
2. **Loading indicator** appears briefly
3. **List of billing entries** displays:
   - Colored source icons
   - Prices
   - Dates
   - Entry numbers
   - Currency icons
   - Upload buttons (for MasterCard only)

4. **Click any entry** → Details screen opens
5. **Click Delete** on details screen → Confirmation dialog appears
6. **Confirm delete** → Returns to list

---

## 🐛 Troubleshooting

### Issue 1: "Unable to connect to server"

**Symptoms:**
- Error message: "Failed to load billing entries"
- Network timeout

**Solutions:**

1. **Verify server is running:**
   ```bash
   curl http://localhost:8030/payment/billing/entry/headers -X POST -H "Content-Type: application/json" -d '{}'
   ```

2. **Check BASE_URL in NetworkModule.kt:**
   - Emulator: `http://10.0.2.2:8030/`
   - Physical device: `http://YOUR_COMPUTER_IP:8030/`

3. **Check network permissions in AndroidManifest.xml:**
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

4. **Check network security config:**
   ```xml
   android:networkSecurityConfig="@xml/network_security_config"
   android:usesCleartextTraffic="true"
   ```

### Issue 2: Gradle Build Failed

**Solutions:**

1. **Clean and rebuild:**
   ```bash
   ./gradlew clean build
   ```

2. **Check JDK version:**
   - Should be JDK 17
   - File → Project Structure → SDK Location

3. **Update Gradle plugin:**
   - Check `build.gradle.kts` versions match:
     - Android Gradle Plugin: 8.2.0
     - Kotlin: 1.9.20

### Issue 3: Hilt Dependency Injection Error

**Symptoms:**
- "Unable to create ViewModel"
- "@HiltAndroidApp missing"

**Solutions:**

1. **Verify @HiltAndroidApp in BillingApplication:**
   ```kotlin
   @HiltAndroidApp
   class BillingApplication : Application()
   ```

2. **Verify AndroidManifest.xml references it:**
   ```xml
   <application
       android:name=".BillingApplication"
   ```

3. **Rebuild project:**
   ```bash
   Build → Rebuild Project
   ```

### Issue 4: Compose Preview Not Working

**Solutions:**

1. **Enable Compose preview:**
   - Tools → Options → Editor → Enable Compose Preview

2. **Add @Preview annotation:**
   ```kotlin
   @Preview
   @Composable
   fun PreviewBillingListItem() {
       // Preview code
   }
   ```

### Issue 5: Icons Not Displaying

**Symptoms:**
- Missing icons in list/details

**Solutions:**

1. **Verify drawable files copied:**
   ```bash
   ls app/src/main/res/drawable/
   # Should show: ic_attendant.xml, ic_card.xml, ic_dollar.xml, etc.
   ```

2. **Re-copy from InterviewAssets if missing:**
   ```bash
   cp InterviewAssets/*.xml PayPlusBilling/app/src/main/res/drawable/
   ```

---

## 📊 Testing the App

### Manual Test Checklist

#### Billing List Screen:
- [ ] List loads successfully
- [ ] Source icons display with correct colors
- [ ] Prices display without currency symbols
- [ ] Dates formatted as `dd-MM-yyyy HH:mm`
- [ ] Entry numbers display
- [ ] Currency icons display (USD/ILS)
- [ ] Upload button appears ONLY for MasterCard
- [ ] Arrow icon displays
- [ ] Clicking item navigates to details

#### Billing Details Screen:
- [ ] All 15 fields display correctly
- [ ] Source indicator has correct color
- [ ] Price color is green for "Passed" status
- [ ] Price color is red for "Rejected" status
- [ ] Currency symbols display for paid/remaining
- [ ] Back button works
- [ ] Delete button shows confirmation dialog
- [ ] Delete confirmation works
- [ ] Returns to list after successful delete

#### Error Handling:
- [ ] Stop server → Error message displays
- [ ] Retry button works
- [ ] Loading indicators appear during operations

---

## 🔍 Viewing Logs

### Android Studio Logcat

1. Open **Logcat** tab (bottom of screen)
2. Select your device
3. Filter by package: `com.payplus.billing`
4. Set log level to **Debug** or **Verbose**

### Network Logs

Network calls are logged by OkHttp interceptor:
- Request URLs
- Request/Response bodies
- Response codes
- Errors

Look for tags: `OkHttp` or `Retrofit`

---

## 📖 Next Steps

### Learn More:
- 📐 **[ARCHITECTURE.md](ARCHITECTURE.md)** - Understand the MVVM architecture
- 📝 **[CODE_STANDARDS.md](CODE_STANDARDS.md)** - Code style and best practices
- 📚 **[README.md](README.md)** - Project overview and API documentation

### Extend the App:
1. Add search functionality
2. Implement filtering by date/source
3. Add offline caching with Room
4. Implement pull-to-refresh
5. Add unit tests

---

## 🎉 Success!

If you see the billing list with all elements displaying correctly, **congratulations!** 

You have successfully set up and run the PayPlus Billing application.

For any issues not covered here, check:
- Logcat for error messages
- Server console for backend errors
- Stack Overflow for specific errors

---

**Happy coding! 🚀**
