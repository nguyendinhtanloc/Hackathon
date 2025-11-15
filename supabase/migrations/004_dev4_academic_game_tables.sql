-- ====================================
-- DEV 4 - ACADEMIC & GAMIFICATION MODULE
-- Tables: user_tasks, pomodoro_sessions, study_rooms, challenges, badges, rewards
-- ====================================

-- User tasks (academic work)
CREATE TABLE user_tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    deadline DATE,
    priority VARCHAR(20) DEFAULT 'medium', -- high, medium, low
    status VARCHAR(20) DEFAULT 'pending', -- pending, in_progress, completed
    estimated_hours INTEGER,
    actual_hours INTEGER,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Pomodoro sessions
CREATE TABLE pomodoro_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    task_id BIGINT REFERENCES user_tasks(id) ON DELETE SET NULL,
    duration_minutes INTEGER DEFAULT 25,
    breaks_taken INTEGER DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

-- Study rooms (virtual body doubling)
CREATE TABLE study_rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    topic VARCHAR(255),
    max_participants INTEGER DEFAULT 10,
    current_participants TEXT[], -- Array of user IDs
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Challenges
CREATE TABLE challenges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration_days INTEGER NOT NULL,
    points_reward INTEGER DEFAULT 100,
    category VARCHAR(50), -- gratitude, meditation, exercise, social
    badge_id BIGINT, -- Foreign key added later
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- User challenges (participation)
CREATE TABLE user_challenges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id BIGINT NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    progress INTEGER DEFAULT 0, -- Days completed
    completed BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    UNIQUE(user_id, challenge_id)
);

-- Badges
CREATE TABLE badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url TEXT,
    criteria TEXT, -- JSON: {"check_ins": 7, "type": "consecutive"}
    rarity VARCHAR(20) DEFAULT 'common', -- common, rare, epic, legendary
    created_at TIMESTAMP DEFAULT NOW()
);

-- User badges
CREATE TABLE user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_id BIGINT NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
    earned_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, badge_id)
);

-- User points
CREATE TABLE user_points (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_points INTEGER DEFAULT 0,
    level INTEGER DEFAULT 1,
    points_to_next_level INTEGER DEFAULT 100,
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id)
);

-- Rewards
CREATE TABLE rewards (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    points_cost INTEGER NOT NULL,
    partner VARCHAR(255), -- Cafe, gym, etc.
    reward_type VARCHAR(50), -- counseling, discount, voucher
    quantity_available INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Reward redemptions
CREATE TABLE reward_redemptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reward_id BIGINT NOT NULL REFERENCES rewards(id) ON DELETE CASCADE,
    code VARCHAR(100), -- Redemption code
    redeemed_at TIMESTAMP DEFAULT NOW(),
    used BOOLEAN DEFAULT FALSE
);

-- Add foreign key for challenge badges
ALTER TABLE challenges ADD CONSTRAINT fk_challenge_badge 
    FOREIGN KEY (badge_id) REFERENCES badges(id);

-- Indexes
CREATE INDEX idx_user_tasks_user ON user_tasks(user_id, deadline);
CREATE INDEX idx_pomodoro_sessions_user ON pomodoro_sessions(user_id, started_at DESC);
CREATE INDEX idx_user_challenges ON user_challenges(user_id, completed);
CREATE INDEX idx_user_points ON user_points(total_points DESC);

-- Enable RLS
ALTER TABLE user_tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE pomodoro_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_points ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Users can manage their own tasks" 
    ON user_tasks FOR ALL 
    USING (auth.uid()::bigint = user_id);

CREATE POLICY "Users can view their own Pomodoro sessions" 
    ON pomodoro_sessions FOR SELECT 
    USING (auth.uid()::bigint = user_id);

CREATE POLICY "Users can view their own points" 
    ON user_points FOR SELECT 
    USING (auth.uid()::bigint = user_id);

-- Everyone can view challenges and badges
CREATE POLICY "Everyone can view challenges" 
    ON challenges FOR SELECT USING (is_active = TRUE);

CREATE POLICY "Everyone can view badges" 
    ON badges FOR SELECT USING (true);
