# PayPlus Billing App

Android app for managing billing entries. Built with Kotlin, Jetpack Compose, and MVVM architecture.

## Architecture

**MVVM pattern** with three layers:
- **Presentation**: Compose UI + ViewModels + State
- **Domain**: Repository interfaces
- **Data**: API service + Repository implementation

## Tech Stack

- **Kotlin** + **Jetpack Compose**
- **Hilt** - Dependency injection
- **Retrofit** - Networking  
- **Coroutines + StateFlow** - Async & state management
- **Material 3** - UI design

## Screenshots

<table>
  <tr>
    <td><img src="screenshots/list_screen.png" width="250"/></td>
    <td><img src="screenshots/details_rejected.png" width="250"/></td>
    <td><img src="screenshots/details_passed.png" width="250"/></td>
  </tr>
  <tr>
    <td align="center"><b>Billing List</b><br/>Color-coded icons, currency indicators</td>
    <td align="center"><b>Details - Rejected</b><br/>Red price for rejected status</td>
    <td align="center"><b>Details - Passed</b><br/>Green price for passed status</td>
  </tr>
</table>

### API Integration
<img src="screenshots/api_logs.png" width="800"/>

*Live API calls showing Retrofit + OkHttp integration with proper logging*

### Key Features Shown:
- ✅ **List View** - Color-coded source icons (green=Terminal, gray=Manual/POS)
- ✅ **Status Colors** - Red text for Rejected, Green for Passed
- ✅ **All Fields** - Complete billing information displayed
- ✅ **Upload Button** - Visible for MasterCard entries
- ✅ **Material Design 3** - Clean, modern UI
- ✅ **API Integration** - Live data from server (200 OK responses in 17-49ms)

## Features

### Billing List
- Shows all billing entries with LazyColumn (efficient for 486 items)
- Color-coded source icons (Terminal/POS/Manual)
- Date formatted as dd-MM-yyyy HH:mm
- Upload button for MasterCard transactions
- Currency icons (USD $, ILS ₪)

### Billing Details  
- All 15+ transaction details
- Status-colored prices (green=Passed, red=Rejected)
- Color-coded source indicators
- Delete with confirmation dialog
- Scrollable content

## Setup

### Server
```bash
cd payplusv2-android-test-server-e2a81a3fef1a
npm install
npm run dev  # Runs on port 8030
```

### Android App
1. Open `PayPlusBilling/` in Android Studio
2. Wait for Gradle sync
3. Run on emulator (uses `http://10.0.2.2:8030`)

For physical device: Update BASE_URL in `NetworkModule.kt` to your computer's IP.

## Project Structure
```
PayPlusBilling/
├── data/           # Models, API, Repository
├── domain/         # Interfaces
├── presentation/   # UI, ViewModels, State
├── di/             # Hilt modules
└── util/           # Utilities
```

## API Endpoints
- `POST /payment/billing/entry/headers` - Get list
- `POST /payment/billing/entry/details` - Get details  
- `POST /payment/billing/entry/delete` - Delete entry

## Documentation
- `ARCHITECTURE.md` - Detailed architecture
- `SETUP_GUIDE.md` - Full setup instructions
- `BUILD_FIX.md` - Build troubleshooting

## License
Interview project for PayPlus
