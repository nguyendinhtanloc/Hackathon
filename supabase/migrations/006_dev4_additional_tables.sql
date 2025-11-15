-- ====================================
-- DEV 4 - Additional Academic & Gamification Tables
-- Adds: points_histories, leaderboard_entries, user_wellness_scores,
--       stress_alerts, study_room_members
-- ====================================

-- Points history (detailed ledger of points changes)
CREATE TABLE points_histories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_type VARCHAR(50) NOT NULL, -- pomodoro, challenge, wellness_bonus, redeem
    source_id BIGINT, -- optional reference to source record
    points_delta INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Leaderboard entries (periodic snapshots for fast reads)
CREATE TABLE leaderboard_entries (
    id BIGSERIAL PRIMARY KEY,
    period VARCHAR(20) NOT NULL, -- daily, weekly, monthly, all_time
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INTEGER NOT NULL DEFAULT 0,
    rank INTEGER,
    computed_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(period, user_id)
);

-- User wellness scores (sync from DEV1)
CREATE TABLE user_wellness_scores (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    wellness_score INTEGER,
    mood_entries JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, date)
);

-- Stress alerts detected by combining workload & wellness
CREATE TABLE stress_alerts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    detected_at TIMESTAMP DEFAULT NOW(),
    workload_summary JSONB,
    severity VARCHAR(20) DEFAULT 'medium', -- low, medium, high
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP
);

-- Study room members (many-to-many)
CREATE TABLE study_room_members (
    id BIGSERIAL PRIMARY KEY,
    study_room_id BIGINT NOT NULL REFERENCES study_rooms(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'member', -- member, moderator, owner
    joined_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(study_room_id, user_id)
);

-- Indexes for fast queries
CREATE INDEX idx_points_histories_user ON points_histories(user_id, created_at DESC);
CREATE INDEX idx_leaderboard_period_score ON leaderboard_entries(period, score DESC);
CREATE INDEX idx_user_wellness_scores_user_date ON user_wellness_scores(user_id, date DESC);
CREATE INDEX idx_stress_alerts_user ON stress_alerts(user_id, detected_at DESC);

-- RLS not enabled here; Supabase policies can be added later as needed.

-- End of migration 006
