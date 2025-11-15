-- ====================================
-- DEV 3 - COMMUNITY MODULE
-- Tables: forum_posts, forum_comments, support_groups, buddy_pairs, events
-- ====================================

-- Forum posts (anonymous support)
CREATE TABLE forum_posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    tags TEXT, -- JSON array: ["anxiety", "stress"]
    is_anonymous BOOLEAN DEFAULT TRUE,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    is_flagged BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Forum comments
CREATE TABLE forum_comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES forum_posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    is_anonymous BOOLEAN DEFAULT TRUE,
    likes_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Support groups
CREATE TABLE support_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    topic VARCHAR(255), -- anxiety, depression, stress management
    description TEXT,
    max_members INTEGER DEFAULT 8,
    current_members INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Group members
CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES support_groups(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'member', -- admin, moderator, member
    joined_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(group_id, user_id)
);

-- Buddy pairs
CREATE TABLE buddy_pairs (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'active', -- active, inactive, completed
    matched_at TIMESTAMP DEFAULT NOW(),
    CHECK (user1_id < user2_id), -- Prevent duplicate pairs
    UNIQUE(user1_id, user2_id)
);

-- Buddy check-ins
CREATE TABLE buddy_checkins (
    id BIGSERIAL PRIMARY KEY,
    pair_id BIGINT NOT NULL REFERENCES buddy_pairs(id) ON DELETE CASCADE,
    checker_id BIGINT NOT NULL REFERENCES users(id),
    checkin_date DATE DEFAULT CURRENT_DATE,
    message TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Events (workshops, yoga, game nights)
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_type VARCHAR(50), -- workshop, yoga, game_night, webinar
    event_datetime TIMESTAMP NOT NULL,
    duration_minutes INTEGER,
    meeting_link TEXT,
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    organizer_id BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Event participants
CREATE TABLE event_participants (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    registered_at TIMESTAMP DEFAULT NOW(),
    attended BOOLEAN DEFAULT FALSE,
    UNIQUE(event_id, user_id)
);

-- Indexes
CREATE INDEX idx_forum_posts_created ON forum_posts(created_at DESC);
CREATE INDEX idx_forum_comments_post ON forum_comments(post_id, created_at);
CREATE INDEX idx_support_groups_active ON support_groups(is_active);
CREATE INDEX idx_buddy_pairs_status ON buddy_pairs(status);
CREATE INDEX idx_events_datetime ON events(event_datetime);

-- Enable RLS
ALTER TABLE forum_posts ENABLE ROW LEVEL SECURITY;
ALTER TABLE forum_comments ENABLE ROW LEVEL SECURITY;
ALTER TABLE support_groups ENABLE ROW LEVEL SECURITY;
ALTER TABLE buddy_pairs ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Everyone can view forum posts" 
    ON forum_posts FOR SELECT USING (true);

CREATE POLICY "Users can create posts" 
    ON forum_posts FOR INSERT 
    WITH CHECK (auth.uid()::bigint = user_id);

CREATE POLICY "Users can view events" 
    ON events FOR SELECT USING (true);

CREATE POLICY "Users can view their buddy pairs" 
    ON buddy_pairs FOR SELECT 
    USING (auth.uid()::bigint = user1_id OR auth.uid()::bigint = user2_id);
