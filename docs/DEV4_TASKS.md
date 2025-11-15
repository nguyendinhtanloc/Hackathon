# 🎯 DEV 4 - ACADEMIC & GAMIFICATION - TASK LIST

## 📦 Deliverables

### Week 1: Database + Task Management
- [x] Create database schema (user_tasks, challenges, badges, rewards)
- [ ] Create UserTaskEntity.java
- [ ] Create ChallengeEntity.java
- [ ] AcademicServlet.java skeleton
- [ ] GamificationServlet.java skeleton
- [ ] POST /api/academic/tasks
- [ ] GET /api/academic/tasks/:userId

### Week 2: Academic Features
- [ ] TaskManager.java
  - [ ] CRUD operations for tasks
  - [ ] Deadline management
  - [ ] Priority system (high/medium/low)
  - [ ] Break large tasks into subtasks
- [ ] WorkloadCalculator.java
  - [ ] Calculate total hours needed
  - [ ] Check if workload is reasonable
  - [ ] Suggest deadline extensions
- [ ] GET /api/academic/workload/:userId
- [ ] PomodoroTimer.java
  - [ ] Start/stop Pomodoro sessions
  - [ ] Track breaks
  - [ ] Link to tasks
- [ ] POST /api/academic/pomodoro/start
- [ ] POST /api/academic/pomodoro/complete
- [ ] StudyRoomService.java
  - [ ] Create/join virtual study rooms
  - [ ] Max 10 participants
  - [ ] Track active rooms
- [ ] GET /api/academic/study-rooms
- [ ] POST /api/academic/study-rooms/join

### Week 3: Gamification Engine
- [ ] PointsCalculator.java
  - [ ] Award points for activities
  - [ ] Daily check-in: +10 points
  - [ ] Complete task: +20 points
  - [ ] Finish Pomodoro: +5 points
  - [ ] Complete content (DEV 2): +15 points
  - [ ] Event participation (DEV 3): +25 points
- [ ] LevelSystem.java
  - [ ] Calculate level from total points
  - [ ] Level 1: 0-100 points
  - [ ] Level 2: 101-250 points
  - [ ] Level 3: 251-500 points
- [ ] BadgeAwardService.java
  - [ ] Define badge criteria
  - [ ] Check and award badges automatically
  - [ ] Example: "7-Day Streak" badge
- [ ] ChallengeManager.java
  - [ ] CRUD challenges
  - [ ] Track user progress
  - [ ] Award badges on completion
- [ ] GET /api/game/challenges
- [ ] POST /api/game/challenges/:id/join
- [ ] GET /api/game/leaderboard
- [ ] RewardManager.java
  - [ ] List available rewards
  - [ ] Redeem rewards (deduct points)
  - [ ] Generate redemption codes
- [ ] POST /api/game/rewards/:id/redeem

### Week 4: Testing & Integration
- [ ] Test workload calculation
- [ ] Test Pomodoro timer accuracy
- [ ] Test points calculation from all sources
- [ ] Test badge award triggers
- [ ] Test leaderboard (anonymous)
- [ ] Integration with DEV 1 (check-in → points)
- [ ] Integration with DEV 2 (content → points)
- [ ] Integration with DEV 3 (events → badges)

## 🔗 Integration Points

### With DEV 1 (Wellness):
- **Receive**: Daily check-in events → Award +10 points
- **API**: Listen for check-in completion

### With DEV 2 (AI):
- **Receive**: Content completion → Award +15 points
- **API**: Listen for meditation/podcast completion

### With DEV 3 (Community):
- **Receive**: Event participation, forum posts → Award points/badges
- **API**: Listen for event registration

## 📝 Code Examples

### Points Calculator:
```java
public class PointsCalculator {
    
    private static final int CHECKIN_POINTS = 10;
    private static final int TASK_COMPLETE_POINTS = 20;
    private static final int POMODORO_POINTS = 5;
    private static final int CONTENT_COMPLETE_POINTS = 15;
    private static final int EVENT_POINTS = 25;
    
    public void awardPoints(Long userId, String activityType) {
        int points = switch (activityType) {
            case "checkin" -> CHECKIN_POINTS;
            case "task_complete" -> TASK_COMPLETE_POINTS;
            case "pomodoro" -> POMODORO_POINTS;
            case "content" -> CONTENT_COMPLETE_POINTS;
            case "event" -> EVENT_POINTS;
            default -> 0;
        };
        
        // Update user_points table
        UserPointsEntity userPoints = repository.findByUserId(userId);
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        
        // Check level up
        checkLevelUp(userPoints);
        
        repository.save(userPoints);
    }
    
    private void checkLevelUp(UserPointsEntity userPoints) {
        int newLevel = calculateLevel(userPoints.getTotalPoints());
        if (newLevel > userPoints.getLevel()) {
            userPoints.setLevel(newLevel);
            // Award level-up badge
        }
    }
    
    private int calculateLevel(int totalPoints) {
        if (totalPoints < 100) return 1;
        if (totalPoints < 250) return 2;
        if (totalPoints < 500) return 3;
        return 4;
    }
}
```

### Badge Award Service:
```java
public class BadgeAwardService {
    
    public void checkAndAwardBadges(Long userId) {
        // Example: 7-Day Streak Badge
        int consecutiveDays = getConsecutiveCheckins(userId);
        if (consecutiveDays >= 7) {
            awardBadge(userId, "7_DAY_STREAK");
        }
        
        // Example: First Task Badge
        if (getCompletedTasksCount(userId) == 1) {
            awardBadge(userId, "FIRST_TASK");
        }
        
        // Example: Meditation Master (10 sessions)
        if (getCompletedContentCount(userId, "meditation") >= 10) {
            awardBadge(userId, "MEDITATION_MASTER");
        }
    }
    
    private void awardBadge(Long userId, String badgeCode) {
        BadgeEntity badge = badgeRepository.findByCode(badgeCode);
        
        // Check if already earned
        boolean alreadyEarned = userBadgeRepository
            .existsByUserIdAndBadgeId(userId, badge.getId());
        
        if (!alreadyEarned) {
            UserBadgeEntity userBadge = new UserBadgeEntity();
            userBadge.setUserId(userId);
            userBadge.setBadgeId(badge.getId());
            userBadgeRepository.save(userBadge);
        }
    }
}
```

## 🎮 Sample Badges

```sql
INSERT INTO badges (name, description, criteria, rarity) VALUES
('7-Day Streak', 'Check-in 7 ngày liên tiếp', '{"check_ins": 7, "type": "consecutive"}', 'rare'),
('First Task', 'Hoàn thành task đầu tiên', '{"tasks": 1}', 'common'),
('Pomodoro Pro', 'Hoàn thành 25 Pomodoro', '{"pomodoro": 25}', 'epic'),
('Meditation Master', '10 buổi thiền', '{"meditation": 10}', 'rare'),
('Social Butterfly', 'Tham gia 5 events', '{"events": 5}', 'epic');
```

## 🧪 Testing Checklist

- [ ] Test task creation & deadline management
- [ ] Test workload calculation
- [ ] Test Pomodoro session tracking
- [ ] Test points calculation from all sources
- [ ] Test level-up logic
- [ ] Test badge award triggers
- [ ] Test reward redemption (deduct points)
- [ ] Test leaderboard sorting

## 📞 Need Help?

- Real-time Pomodoro timer → Consider WebSocket or frontend timer
- Notification for deadlines → Coordinate with team
- Reward partnerships → Contact local businesses
