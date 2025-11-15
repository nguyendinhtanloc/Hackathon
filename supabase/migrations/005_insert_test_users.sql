-- Insert test users for development
-- Run this in Supabase SQL Editor

INSERT INTO users (id, email, name, password_hash, avatar_url, created_at, updated_at)
VALUES 
    (1, 'test1@student.com', 'Nguyễn Văn A', 'hashed_password_123', NULL, NOW(), NOW()),
    (2, 'test2@student.com', 'Trần Thị B', 'hashed_password_456', NULL, NOW(), NOW()),
    (3, 'test3@student.com', 'Lê Văn C', 'hashed_password_789', NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Reset sequence to continue from 4
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Verify
SELECT id, email, name, created_at FROM users;
