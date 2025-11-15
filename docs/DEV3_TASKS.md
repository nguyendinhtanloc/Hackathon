# 🎯 DEV 3 - COMMUNITY MODULE - TASK LIST

## 📦 Deliverables

### Week 1: Database + Forum Basics
- [x] Create database schema (forum_posts, support_groups, buddy_pairs, events)
- [ ] Create ForumPostEntity.java
- [ ] Create SupportGroupEntity.java
- [ ] CommunityServlet.java skeleton
- [ ] GET /api/community/posts
- [ ] POST /api/community/posts

### Week 2: Forum & Groups
- [ ] ForumService.java
  - [ ] Anonymous posting
  - [ ] Tag filtering (anxiety, stress, depression)
  - [ ] Pagination support
  - [ ] Like/comment system
- [ ] POST /api/community/posts/:id/comment
- [ ] POST /api/community/posts/:id/like
- [ ] SupportGroupManager.java
  - [ ] Create groups (max 8 members)
  - [ ] Join/leave group
  - [ ] Check capacity
- [ ] POST /api/community/groups (create)
- [ ] POST /api/community/groups/:id/join

### Week 3: Buddy System & Events
- [ ] BuddyMatchingService.java
  - [ ] Find available buddy (not paired)
  - [ ] Create buddy_pairs record
  - [ ] Support daily check-ins
- [ ] POST /api/community/buddy/request
- [ ] POST /api/community/buddy/checkin
- [ ] GET /api/community/buddy/history
- [ ] EventManager.java
  - [ ] CRUD events (workshop, yoga, game nights)
  - [ ] Registration system
  - [ ] Capacity management
- [ ] GET /api/community/events
- [ ] POST /api/community/events/:id/register

### Week 4: Testing & Integration
- [ ] Test anonymous posting (hide user identity)
- [ ] Test support group capacity limits
- [ ] Test buddy matching algorithm
- [ ] Test event registration
- [ ] Integration with DEV 4 (event participation → badges)

## 🔗 Integration Points

### With DEV 4 (Gamification):
- **Provide**: Event participation, forum activity → Award points/badges
- **API**: Trigger when user registers for event or posts in forum

## 📝 Code Examples

### Anonymous Forum Posting:
```java
public class ForumService {
    
    public ForumPostEntity createPost(Long userId, String content, boolean isAnonymous) {
        ForumPostEntity post = new ForumPostEntity();
        post.setUserId(userId);
        post.setContent(content);
        post.setIsAnonymous(isAnonymous);
        
        // Save to database
        return repository.save(post);
    }
    
    public List<ForumPostDTO> getPosts() {
        List<ForumPostEntity> posts = repository.findAll();
        
        return posts.stream().map(post -> {
            ForumPostDTO dto = new ForumPostDTO();
            dto.setContent(post.getContent());
            
            // Hide user info if anonymous
            if (post.getIsAnonymous()) {
                dto.setAuthor("Anonymous");
            } else {
                dto.setAuthor(getUserName(post.getUserId()));
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}
```

### Buddy Matching:
```java
public class BuddyMatchingService {
    
    public BuddyPairEntity findBuddy(Long userId) {
        // Find user not in active pair
        List<Long> pairedUsers = getPairedUserIds();
        
        Long buddyId = userRepository.findAvailableUsers()
            .stream()
            .filter(id -> !pairedUsers.contains(id))
            .filter(id -> !id.equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No buddy available"));
        
        // Create pair
        BuddyPairEntity pair = new BuddyPairEntity();
        pair.setUser1Id(Math.min(userId, buddyId));
        pair.setUser2Id(Math.max(userId, buddyId));
        pair.setStatus("active");
        
        return repository.save(pair);
    }
}
```

## 🧪 Testing Checklist

- [ ] Test anonymous vs non-anonymous posts
- [ ] Test tag filtering
- [ ] Test support group capacity (reject when full)
- [ ] Test buddy matching (no duplicates)
- [ ] Test event registration (capacity limits)
- [ ] Test forum comment threading

## 📞 Need Help?

- Real-time chat for groups → Consider WebSocket
- Notification system → Coordinate with team
- Moderation tools → Define content policy
