# Mapme Backend

Backend API for Mapme - A photo-based world exploration app that gamifies travel by unlocking regions through GPS-tagged photos.

## Tech Stack

- **Framework:** Kotlin + Ktor 3.0
- **Database:** PostgreSQL 16 + PostGIS
- **Cache:** Redis 7
- **Storage:** MinIO (S3-compatible)
- **ORM:** Exposed
- **Migrations:** Flyway

## Prerequisites

- Docker & Docker Compose
- JDK 21+ (for local development)

## Quick Start

### 1. Clone & Setup
```bash
git clone 
cd mapme-backend
cp .env.example .env
```

### 2. Start Services
```bash
cd docker
docker-compose up -d
```

**Services:**
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432
- Redis: localhost:6379
- MinIO Console: http://localhost:9001 (mapme / dev_password)

### 3. Check Logs
```bash
docker-compose logs -f backend
```

### 4. Stop Services
```bash
docker-compose down
```

## Project Structure
```
mapme-backend/
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── init-db/
│       └── 01-init-postgis.sql
├── src/main/
│   ├── kotlin/com/mapme/
│   │   ├── Application.kt
│   │   ├── config/
│   │   ├── database/
│   │   ├── domain/
│   │   ├── services/
│   │   ├── routes/
│   │   ├── plugins/
│   │   └── utils/
│   └── resources/
│       ├── application.yaml
│       ├── logback.xml
│       └── db/migration/
│           ├── V1__create_users.sql
│           ├── V2__create_photos.sql
│           ├── V3__create_visited_cells.sql
│           └── V4__create_pois.sql
├── .env.example
├── .gitignore
└── README.md
```

## API Endpoints (Planned)

### Photos
- `POST /api/v1/photos` - Upload photo with GPS metadata
- `GET /api/v1/photos/{id}` - Get photo details
- `GET /api/v1/photos/{id}/thumbnail?size=256` - Get thumbnail
- `GET /api/v1/photos/markers?bounds={...}` - Get photo markers in viewport

### Grid
- `GET /api/v1/grid/tiles?bounds={...}&level={13}` - Get S2 grid cells
- `GET /api/v1/grid/cells/{cellId}` - Get cell details

### POIs
- `GET /api/v1/pois?bounds={...}&zoom={z}` - Get POIs in viewport
- `GET /api/v1/pois/{id}` - Get POI details
- `GET /api/v1/cities/{id}/pois` - Get POIs in city
- `GET /api/v1/cities/{id}/progress` - Get user progress in city

### User
- `GET /api/v1/users/me/stats` - Get user statistics
- `GET /api/v1/users/me/visited-cells` - Get all visited cells

## Database Schema

### Tables
- `users` - User accounts
- `photos` - Photo metadata with GPS coordinates
- `visited_cells` - S2 cells visited by users
- `cities` - City data (OSM)
- `pois` - Points of Interest (OSM)
- `user_pois` - User POI discovery progress

### Spatial Features (PostGIS)
- S2 Geometry cells (Level 13 = ~1km², Level 15 = ~200m²)
- Viewport-based spatial queries
- Geographic indices for performance

## Environment Variables

See `.env.example` for all required variables.

**Development:**
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/mapme
S3_ENDPOINT=http://localhost:9000
```

**Production:**
Update with secure passwords and production URLs.

## Development

### Run Locally (without Docker)
```bash
# Start only database services
cd docker
docker-compose up postgres redis minio -d

# Run backend from IntelliJ or:
./gradlew run
```

### Run Tests
```bash
./gradlew test
```

### Build JAR
```bash
./gradlew buildFatJar
# Output: build/libs/mapme-backend-all.jar
```

## Migrations

Migrations run automatically on startup via Flyway.

**Add new migration:**
1. Create `V5__description.sql` in `src/main/resources/db/migration/`
2. Restart backend

**Rollback:** Not supported by Flyway. Create a new migration to undo changes.

## Logging

- **Console:** All logs (DEBUG level for `com.mapme`)
- **File:** `logs/mapme-backend.log` (rotated daily, 30-day retention)

**Change log level:**
Edit `src/main/resources/logback.xml`

## Deployment

### Docker (Recommended)
```bash
docker build -f docker/Dockerfile -t mapme-backend .
docker run -p 8080:8080 --env-file .env mapme-backend
```

### Platform Support
- Railway.app
- Fly.io
- DigitalOcean App Platform
- AWS ECS/Fargate