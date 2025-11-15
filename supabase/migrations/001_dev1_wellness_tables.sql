-- ====================================
-- DEV 1 - WELLNESS MODULE
-- Tables: users, mood_checkins, wellness_scores, mood_alerts
-- ====================================

-- Users table (shared by all modules)
-- Using BIGSERIAL instead of UUID for compatibility with Java Long
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    avatar_url TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Mood check-ins with facial analysis and heart rate
CREATE TABLE mood_checkins (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    checkin_date DATE NOT NULL DEFAULT CURRENT_DATE,
    emotion VARCHAR(50), -- happy, sad, anxious, stressed, neutral
    emoji VARCHAR(10), -- 😊😢😰😡😐
    note TEXT,
    wellness_score INTEGER CHECK (wellness_score BETWEEN 0 AND 100),
    heart_rate INTEGER,
    face_analysis_data TEXT, -- JSON from ML model
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, checkin_date) -- One check-in per day
);

-- Wellness scores over time
CREATE TABLE wellness_scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    score INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    factors TEXT, -- JSON: {"mood": 70, "sleep": 80, "activity": 60}
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, date)
);

-- Mood alerts (when mood trends downward)
CREATE TABLE mood_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    alert_type VARCHAR(50) NOT NULL, -- trending_down, crisis, low_score
    severity VARCHAR(20), -- low, medium, high
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_mood_checkins_user_date ON mood_checkins(user_id, checkin_date DESC);
CREATE INDEX idx_wellness_scores_user ON wellness_scores(user_id, date DESC);
CREATE INDEX idx_mood_alerts_user ON mood_alerts(user_id, is_read);

-- Enable Row Level Security (Optional - disable if testing without auth)
-- Uncomment these lines when you implement Supabase Auth
/*
ALTER TABLE mood_checkins ENABLE ROW LEVEL SECURITY;
ALTER TABLE wellness_scores ENABLE ROW LEVEL SECURITY;
ALTER TABLE mood_alerts ENABLE ROW LEVEL SECURITY;

-- RLS Policies (users can only see their own data)
CREATE POLICY "Users can view their own check-ins" 
    ON mood_checkins FOR SELECT 
    USING (auth.uid()::text::bigint = user_id);

CREATE POLICY "Users can insert their own check-ins" 
    ON mood_checkins FOR INSERT 
    WITH CHECK (auth.uid()::text::bigint = user_id);

CREATE POLICY "Users can view their own scores" 
    ON wellness_scores FOR SELECT 
    USING (auth.uid()::text::bigint = user_id);

CREATE POLICY "Users can view their own alerts" 
    ON mood_alerts FOR SELECT 
    USING (auth.uid()::text::bigint = user_id);
*/

-- For testing: Allow all operations without authentication
-- REMOVE THIS IN PRODUCTION!
GRANT ALL ON mood_checkins TO anon, authenticated;
GRANT ALL ON wellness_scores TO anon, authenticated;
GRANT ALL ON mood_alerts TO anon, authenticated;
GRANT ALL ON users TO anon, authenticated;

-- Grant sequence permissions
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO anon, authenticated;
