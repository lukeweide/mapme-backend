-- Create Photos Table
CREATE TABLE photos (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        file_path VARCHAR(500) NOT NULL,
                        thumbnail_path VARCHAR(500),

    -- GPS Coordinates
                        latitude DOUBLE PRECISION NOT NULL,
                        longitude DOUBLE PRECISION NOT NULL,
                        location GEOGRAPHY(POINT, 4326) NOT NULL,

    -- S2 Cell
                        s2_cell_id BIGINT NOT NULL,

    -- EXIF Metadata
                        camera_make VARCHAR(255),
                        camera_model VARCHAR(255),
                        taken_at TIMESTAMP,

    -- Timestamps
                        uploaded_at TIMESTAMP DEFAULT NOW()
);

-- Create Indexes
CREATE INDEX idx_photos_user_id ON photos(user_id);
CREATE INDEX idx_photos_s2_cell_id ON photos(s2_cell_id);
CREATE INDEX idx_photos_location ON photos USING GIST(location);
CREATE INDEX idx_photos_taken_at ON photos(taken_at);