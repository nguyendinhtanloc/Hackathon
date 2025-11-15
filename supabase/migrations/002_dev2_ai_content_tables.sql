-- ====================================
-- DEV 2 - AI & CONTENT MODULE
-- Tables: ai_conversations, crisis_detections, wellness_content, user_recommendations
-- ====================================

-- AI conversations
CREATE TABLE ai_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    response TEXT,
    sentiment VARCHAR(50), -- positive, negative, neutral, crisis
    is_crisis BOOLEAN DEFAULT FALSE,
    keywords TEXT, -- JSON array of detected keywords
    created_at TIMESTAMP DEFAULT NOW()
);

-- Crisis detections
CREATE TABLE crisis_detections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    keywords TEXT, -- Detected crisis keywords
    hotline_triggered BOOLEAN DEFAULT FALSE,
    severity VARCHAR(20), -- low, medium, high, critical
    created_at TIMESTAMP DEFAULT NOW()
);

-- Wellness content library
CREATE TABLE wellness_content (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- meditation, breathing, podcast, video
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url TEXT NOT NULL,
    thumbnail_url TEXT,
    duration INTEGER, -- in seconds
    tags TEXT, -- JSON array: ["anxiety", "sleep", "stress"]
    language VARCHAR(10) DEFAULT 'vi',
    rating DECIMAL(3,2) DEFAULT 0.0,
    views_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- User content recommendations
CREATE TABLE user_recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES wellness_content(id) ON DELETE CASCADE,
    reason TEXT, -- Why recommended
    score DECIMAL(3,2), -- Recommendation confidence
    is_viewed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- User content progress
CREATE TABLE user_content_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES wellness_content(id) ON DELETE CASCADE,
    progress INTEGER DEFAULT 0, -- Percentage completed
    completed BOOLEAN DEFAULT FALSE,
    last_position INTEGER, -- For videos/podcasts
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, content_id)
);

-- Indexes
CREATE INDEX idx_ai_conversations_user ON ai_conversations(user_id, created_at DESC);
CREATE INDEX idx_crisis_detections_user ON crisis_detections(user_id, created_at DESC);
CREATE INDEX idx_wellness_content_type ON wellness_content(type);
CREATE INDEX idx_user_recommendations ON user_recommendations(user_id, is_viewed);

-- Enable RLS
ALTER TABLE ai_conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE crisis_detections ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_content_progress ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Users can view their own conversations" 
    ON ai_conversations FOR SELECT 
    USING (auth.uid()::bigint = user_id);

CREATE POLICY "Users can insert their own messages" 
    ON ai_conversations FOR INSERT 
    WITH CHECK (auth.uid()::bigint = user_id);

CREATE POLICY "Users can view their own recommendations" 
    ON user_recommendations FOR SELECT 
    USING (auth.uid()::bigint = user_id);

-- Public content (everyone can read)
CREATE POLICY "Everyone can view wellness content" 
    ON wellness_content FOR SELECT 
    USING (true);
