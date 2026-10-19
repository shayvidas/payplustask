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

## Features

### Billing List
- Shows all billing entries
- Color-coded source icons (Terminal/POS/Manual)
- Date formatted as dd-MM-yyyy HH:mm
- Upload button for MasterCard

### Billing Details  
- All transaction details
- Status-colored prices (green=Passed, red=Rejected)
- Delete with confirmation

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
