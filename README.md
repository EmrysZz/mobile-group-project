# FR (FloodRescue) - Mobile Group Project

A full-stack flood rescue and disaster management system consisting of an Android mobile application and Laravel REST API backend.

## 📁 Project Structure

```
mobile-group-project/
├── mobile-app/          # Android mobile application
└── backend/             # Laravel REST API server
```

## 📱 Mobile App

**Technology Stack:**
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM with ViewModels
- **Networking:** Ktor Client
- **Build Tool:** Gradle

**Features:**
- User authentication (register/login)
- Real-time news feed
- Interactive flood maps
- Shelter location finder
- Incident reporting

### Running the Mobile App

```bash
cd mobile-app
./gradlew build
# Open in Android Studio and run
```

**API Configuration:**
The app connects to `http://10.0.2.2:8000/api/` when running in Android emulator (localhost:8000 on your machine).

## 🌐 Backend API

**Technology Stack:**
- **Framework:** Laravel 11
- **Authentication:** Laravel Sanctum
- **Database:** MySQL
- **Language:** PHP 8.x

**Features:**
- RESTful API endpoints
- User authentication with API tokens
- News management
- Shelter location data
- Incident report handling

### Running the Backend

```bash
cd backend

# Install dependencies
composer install

# Configure environment
cp .env.example .env
php artisan key:generate

# Run migrations
php artisan migrate

# Start development server
php artisan serve --host=0.0.0.0 --port=8000
```

**Important:** Run the backend on `0.0.0.0:8000` to make it accessible from the Android emulator.

## 🚀 Quick Start (Full Stack)

**Terminal 1** - Backend Server:
```bash
cd backend
php artisan serve --host=0.0.0.0 --port=8000
```

**Terminal 2** - Mobile App:
```bash
cd mobile-app
# Open in Android Studio
# Run on emulator or device
```

The mobile app will automatically connect to the backend API.

## 📚 API Documentation

Base URL: `http://localhost:8000/api/`

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login user |
| POST | `/logout` | Logout (requires auth) |
| GET | `/profile` | Get user profile (requires auth) |
| PUT | `/profile` | Update profile (requires auth) |

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/news` | Get news feed |
| GET | `/shelters` | Get shelter locations |
| GET | `/reports` | Get incident reports |
| POST | `/reports` | Submit new report |

## 🛠️ Development Workflow

### Mobile App Development
1. Work in `/mobile-app` directory
2. Use Android Studio
3. Test with Android emulator or physical device

### Backend Development
1. Work in `/backend` directory
2. Use your preferred PHP IDE (VS Code, PhpStorm)
3. Run `php artisan serve` for development

### Full Stack Testing
1. Start backend server first
2. Then run mobile app in emulator
3. Test API integration end-to-end

## 🔧 Technical Details

### Database Schema
- **users** - User accounts with authentication
- **news** - News articles and updates
- **shelters** - Evacuation shelter locations
- **reports** - Flood incident reports
- **personal_access_tokens** - API authentication tokens

### Mobile App Architecture
```
app/src/main/java/com/example/fr/
├── model/           # Data models (User, News, etc.)
├── viewmodel/       # Business logic and state
├── ui/screens/      # Composable UI screens
└── util/            # Utilities (TokenManager, etc.)
```

### Backend Structure
```
backend/
├── app/Http/Controllers/Api/  # API controllers
├── app/Models/                # Eloquent models
├── database/migrations/       # Database schema
├── routes/api.php             # API route definitions
└── storage/                   # Logs and uploads
```

## 👥 Team Development

### Branch Strategy (Historical)
- `mob-app` - Mobile application code
- `web-server` - Laravel backend code
- `main` - **Monorepo** (current structure)

### Contributing
1. Clone this repository
2. Create feature branch from `main`
3. Make your changes in `/mobile-app` or `/backend`
4. Test locally before committing
5. Push and create pull request

## 📝 Important Notes

> **Authentication Fix Applied:** The backend User model includes `HasApiTokens` trait from Laravel Sanctum, enabling proper API token generation.

> **Environment File:** Remember to configure `/backend/.env` with your database credentials before running migrations.

> **Emulator Network:** Android emulator uses `10.0.2.2` to reach the host machine's `localhost`. The mobile app is pre-configured for this.

## 🎓 Project Information

**Course:** Mobile Group Project  
**Repository:** https://github.com/EmrysZz/mobile-group-project  

---

**Built with ❤️ for disaster preparedness and community safety**
