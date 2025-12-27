-- Create Visited Cells Table
CREATE TABLE visited_cells (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               s2_cell_id BIGINT NOT NULL,

    -- Stats
                               photo_count INT DEFAULT 0,
                               first_visited_at TIMESTAMP NOT NULL,
                               last_visited_at TIMESTAMP NOT NULL,

    -- Geometry
                               cell_geom GEOGRAPHY(POLYGON, 4326) NOT NULL,

    -- Unique: One cell per user
                               UNIQUE(user_id, s2_cell_id)
);

-- Create Indexes
CREATE INDEX idx_visited_cells_user_id ON visited_cells(user_id);
CREATE INDEX idx_visited_cells_s2_cell_id ON visited_cells(s2_cell_id);
CREATE INDEX idx_visited_cells_geom ON visited_cells USING GIST(cell_geom);