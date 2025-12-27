-- Create Users Table
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       settings JSONB DEFAULT '{}'::jsonb
);

-- Create Index on Email
CREATE INDEX idx_users_email ON users(email);