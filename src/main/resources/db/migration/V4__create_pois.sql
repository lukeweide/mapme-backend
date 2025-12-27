-- Create Cities Table
CREATE TABLE cities (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(255) NOT NULL,
                        country VARCHAR(255) NOT NULL,

    -- GPS Coordinates
                        latitude DOUBLE PRECISION NOT NULL,
                        longitude DOUBLE PRECISION NOT NULL,
                        location GEOGRAPHY(POINT, 4326) NOT NULL,

    -- Boundary (optional)
                        boundary GEOGRAPHY(POLYGON, 4326)
);

-- Create POIs Table
CREATE TABLE pois (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      osm_id BIGINT,
                      name VARCHAR(255) NOT NULL,
                      type VARCHAR(100) NOT NULL,
                      city_id UUID REFERENCES cities(id) ON DELETE SET NULL,

    -- GPS Coordinates
                      latitude DOUBLE PRECISION NOT NULL,
                      longitude DOUBLE PRECISION NOT NULL,
                      location GEOGRAPHY(POINT, 4326) NOT NULL,

    -- Additional Data
                      metadata JSONB DEFAULT '{}'::jsonb
);

-- Create User POI Progress Table
CREATE TABLE user_pois (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           poi_id UUID NOT NULL REFERENCES pois(id) ON DELETE CASCADE,
                           photo_id UUID REFERENCES photos(id) ON DELETE SET NULL,
                           discovered_at TIMESTAMP DEFAULT NOW(),

    -- Unique: One POI discovery per user
                           UNIQUE(user_id, poi_id)
);

-- Create Indexes
CREATE INDEX idx_cities_location ON cities USING GIST(location);
CREATE INDEX idx_pois_location ON pois USING GIST(location);
CREATE INDEX idx_pois_city_id ON pois(city_id);
CREATE INDEX idx_pois_type ON pois(type);
CREATE INDEX idx_user_pois_user_id ON user_pois(user_id);
CREATE INDEX idx_user_pois_poi_id ON user_pois(poi_id);