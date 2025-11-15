package com.wellness.academic.servlet;

import com.google.gson.JsonObject;
import com.wellness.academic.entity.UserTaskEntity;
import com.wellness.academic.entity.PomodoroSessionEntity;
import com.wellness.academic.service.UserTaskService;
import com.wellness.academic.service.PomodoroService;
import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DEV 4 - Academic Servlet
 * Handles: /api/academic/*
 * 
 * Endpoints:
 * - POST /api/academic/tasks - Create task
 * - GET /api/academic/workload/{userId} - Calculate workload
 * - POST /api/academic/pomodoro/start - Start Pomodoro
 * - POST /api/academic/pomodoro/complete - Complete Pomodoro
 * - GET /api/academic/tasks/{userId} - Get user tasks
 * - GET /api/academic/points/{userId} - Get user points
 */
@MultipartConfig
public class AcademicServlet extends HttpServlet {
    
    private EntityManager entityManager;
    private UserTaskService taskService;
    private PomodoroService pomodoroService;
    // In-memory fallback for tasks when EntityManager is not available
    private static final ConcurrentMap<Long, List<UserTaskEntity>> inMemoryTasks = new ConcurrentHashMap<>();
    // In-memory exam schedules per user
    private static final ConcurrentMap<Long, List<ExamEntry>> inMemoryExamSchedules = new ConcurrentHashMap<>();
    // In-memory busy slots per user
    private static final ConcurrentMap<Long, List<BusySlot>> inMemoryBusySlots = new ConcurrentHashMap<>();

    // Simple POJOs for exam entries and busy slots
    public static class ExamEntry {
        public String subject;
        public LocalDate date;
        public String time; // optional HH:mm
    }

    // GET /api/academic/exam-schedule/{userId}
    private void handleGetExamSchedule(String userId, HttpServletResponse response) throws IOException {
        try {
            Long uid = Long.parseLong(userId);
            List<ExamEntry> exams = inMemoryExamSchedules.getOrDefault(uid, new ArrayList<>());
            ApiResponse<List<ExamEntry>> apiResponse = ApiResponse.success(exams, "Exam schedule");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to get exam schedule: " + e.getMessage());
        }
    }

    // GET /api/academic/suggested-schedule/{userId}
    private void handleGetSuggestedSchedule(String userId, HttpServletResponse response) throws IOException {
        try {
            Long uid = Long.parseLong(userId);
            List<ExamEntry> exams = inMemoryExamSchedules.getOrDefault(uid, new ArrayList<>());
            List<BusySlot> busy = inMemoryBusySlots.getOrDefault(uid, new ArrayList<>());

            List<Map<String, Object>> suggestions = new ArrayList<>();

            // Simple scheduling: for each exam, schedule 6 hours before exam across days (2h per day)
            for (ExamEntry ex : exams) {
                int totalHours = 6;
                int blockHours = 2;
                LocalDate day = LocalDate.now();
                LocalDate endDay = ex.date.minusDays(1);
                while (!day.isAfter(endDay) && totalHours > 0) {
                    // skip if this day conflicts with busy slots (coarse check using date ranges)
                    boolean isBusy = false;
                    for (BusySlot b : busy) {
                        if (b.startDate != null && b.endDate != null) {
                            if (!day.isBefore(b.startDate) && !day.isAfter(b.endDate)) { isBusy = true; break; }
                        }
                    }
                    if (!isBusy) {
                        Map<String, Object> slot = new HashMap<>();
                        slot.put("subject", ex.subject);
                        slot.put("date", day.toString());
                        // choose default start times 18:00, 20:00 etc
                        int allocated = Math.min(blockHours, totalHours);
                        String start = "18:00";
                        String end = String.format("%02d:00", 18 + allocated);
                        slot.put("startTime", start);
                        slot.put("endTime", end);
                        slot.put("hours", allocated);
                        suggestions.add(slot);
                        totalHours -= allocated;
                    }
                    day = day.plusDays(1);
                }
            }

            ApiResponse<List<Map<String, Object>>> apiResponse = ApiResponse.success(suggestions, "Suggested schedule");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to generate suggested schedule: " + e.getMessage());
        }
    }

    // POST /api/academic/exam-schedule/upload (multipart form-data, part name: file, form field: userId)
    private void handleUploadExamSchedule(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userIdStr = request.getParameter("userId");
            Long userId = userIdStr != null ? Long.parseLong(userIdStr) : 1L;

            Part filePart = null;
            try { filePart = request.getPart("file"); } catch (Exception ex) { /* ignore */ }

            List<ExamEntry> exams = new ArrayList<>();

            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = filePart.getSubmittedFileName();
                if (submittedFileName != null && (submittedFileName.endsWith(".xls") || submittedFileName.endsWith(".xlsx"))) {
                    try (Workbook wb = new XSSFWorkbook(filePart.getInputStream())) {
                        Sheet sheet = wb.getSheetAt(0);
                        for (Row row : sheet) {
                            if (row.getRowNum() == 0) continue;
                            Cell c0 = row.getCell(0);
                            Cell c1 = row.getCell(1);
                            Cell c2 = row.getCell(2);
                            if (c0 == null || c1 == null) continue;
                            ExamEntry e = new ExamEntry();
                            e.subject = c0.toString();
                            String dateStr = c1.toString();
                            try { e.date = LocalDate.parse(dateStr); }
                            catch (Exception pe) {
                                try { double num = c1.getNumericCellValue(); java.util.Date d = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(num); e.date = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(); }
                                catch (Exception ignore) { continue; }
                            }
                            e.time = c2 != null ? c2.toString() : null;
                            exams.add(e);
                        }
                    }
                } else {
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(filePart.getInputStream()))) {
                        String line; boolean first = true;
                        while ((line = br.readLine()) != null) {
                            if (first) { first = false; continue; }
                            String[] cols = line.split(","); if (cols.length < 2) continue;
                            ExamEntry e = new ExamEntry(); e.subject = cols[0].trim(); e.date = LocalDate.parse(cols[1].trim()); e.time = cols.length > 2 ? cols[2].trim() : null; exams.add(e);
                        }
                    }
                }
            } else {
                String body = getRequestBody(request);
                if (body != null && body.trim().length() > 0) {
                    com.google.gson.JsonArray arr = JsonUtil.fromJson(body, com.google.gson.JsonArray.class);
                    for (com.google.gson.JsonElement el : arr) {
                        com.google.gson.JsonObject jo = el.getAsJsonObject();
                        ExamEntry e = new ExamEntry(); e.subject = jo.has("subject") ? jo.get("subject").getAsString() : ""; e.date = jo.has("date") ? LocalDate.parse(jo.get("date").getAsString()) : null; e.time = jo.has("time") ? jo.get("time").getAsString() : null; exams.add(e);
                    }
                }
            }

            inMemoryExamSchedules.put(userId, exams);
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(Map.of("count", exams.size()), "Exam schedule uploaded");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to upload exam schedule: " + e.getMessage());
        }
    }

    // POST /api/academic/busy-slots  Body: JSON array of {startDate, endDate, startTime, endTime}
    private void handleAddBusySlots(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = getRequestBody(request);
            com.google.gson.JsonArray arr = JsonUtil.fromJson(body, com.google.gson.JsonArray.class);
            String userIdStr = request.getParameter("userId"); Long userId = userIdStr != null ? Long.parseLong(userIdStr) : 1L;
            List<BusySlot> slots = new ArrayList<>();
            for (com.google.gson.JsonElement el : arr) {
                com.google.gson.JsonObject jo = el.getAsJsonObject(); BusySlot b = new BusySlot(); if (jo.has("startDate")) b.startDate = LocalDate.parse(jo.get("startDate").getAsString()); if (jo.has("endDate")) b.endDate = LocalDate.parse(jo.get("endDate").getAsString()); b.startTime = jo.has("startTime") ? jo.get("startTime").getAsString() : null; b.endTime = jo.has("endTime") ? jo.get("endTime").getAsString() : null; slots.add(b);
            }
            inMemoryBusySlots.put(userId, slots);
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(Map.of("count", slots.size()), "Busy slots saved");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to save busy slots: " + e.getMessage());
        }
    }

    public static class BusySlot {
        public LocalDate startDate;
        public LocalDate endDate;
        public String startTime; // HH:mm
        public String endTime;   // HH:mm
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize Entity Manager
        try {
            entityManager = Persistence.createEntityManagerFactory("default").createEntityManager();
            taskService = new UserTaskService(entityManager);
            pomodoroService = new PomodoroService(entityManager);
        } catch (Exception e) {
            System.err.println("Warning: EntityManager initialization failed. API will return mock data.");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/workload/")) {
            String userId = pathInfo.substring("/workload/".length());
            handleCalculateWorkload(userId, response);
        } else if (pathInfo != null && pathInfo.startsWith("/tasks/")) {
            String userId = pathInfo.substring("/tasks/".length());
            handleGetTasks(userId, response);
        } else if (pathInfo != null && pathInfo.startsWith("/exam-schedule/")) {
            String userId = pathInfo.substring("/exam-schedule/".length());
            handleGetExamSchedule(userId, response);
        } else if (pathInfo != null && pathInfo.startsWith("/suggested-schedule/")) {
            String userId = pathInfo.substring("/suggested-schedule/".length());
            handleGetSuggestedSchedule(userId, response);
        } else if (pathInfo != null && pathInfo.startsWith("/points/")) {
            String userId = pathInfo.substring("/points/".length());
            handleGetPoints(userId, response);
        } else {
            sendError(response, "Unknown GET endpoint");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/tasks".equals(pathInfo)) {
                handleCreateTask(request, response);
            } else if ("/pomodoro/start".equals(pathInfo)) {
                handleStartPomodoro(request, response);
            } else if ("/pomodoro/complete".equals(pathInfo)) {
                handleCompletePomodoro(request, response);
            } else if ("/exam-schedule/upload".equals(pathInfo)) {
                handleUploadExamSchedule(request, response);
            } else if ("/busy-slots".equals(pathInfo)) {
                handleAddBusySlots(request, response);
            } else {
                sendError(response, "Unknown POST endpoint");
            }
        } catch (Exception e) {
            sendError(response, "Server error: " + e.getMessage());
        }
    }
    
    /**
     * Create task
     * POST /api/academic/tasks
     * Body: {userId, title, description, deadline, priority, estimatedHours}
     */
    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String body = getRequestBody(request);
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
            
            Long userId = json.get("userId").getAsLong();
            String title = json.get("title").getAsString();
            String description = json.has("description") ? json.get("description").getAsString() : "";
            LocalDate deadline = json.has("deadline") ? LocalDate.parse(json.get("deadline").getAsString()) : null;
            String priority = json.has("priority") ? json.get("priority").getAsString() : "medium";
            Integer estimatedHours = json.has("estimatedHours") ? json.get("estimatedHours").getAsInt() : 1;
            
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("id", System.currentTimeMillis()); // Mock ID
            taskData.put("title", title);
            taskData.put("status", "pending");
            taskData.put("priority", priority);
            taskData.put("estimatedHours", estimatedHours);
            
            // If EntityManager available, save to DB
            if (taskService != null) {
                UserTaskEntity task = taskService.createTask(userId, title, description, deadline, priority, estimatedHours);
                taskData.put("id", task.getId());
            } else {
                // Save to in-memory fallback so UI can see created tasks
                UserTaskEntity task = new UserTaskEntity();
                task.setId(System.currentTimeMillis());
                task.setUserId(userId);
                task.setTitle(title);
                task.setDescription(description);
                task.setDeadline(deadline);
                task.setPriority(priority);
                task.setStatus("pending");
                task.setEstimatedHours(estimatedHours);
                task.setCreatedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());
                inMemoryTasks.computeIfAbsent(userId, k -> new ArrayList<>()).add(task);
                taskData.put("id", task.getId());
            }
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(taskData, "Task created successfully");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to create task: " + e.getMessage());
        }
    }
    
    /**
     * Calculate workload
     * GET /api/academic/workload/{userId}
     */
    private void handleCalculateWorkload(String userId, HttpServletResponse response) 
            throws IOException {
        try {
            Long uid = Long.parseLong(userId);
            
            Map<String, Object> workloadData = new HashMap<>();
            workloadData.put("userId", uid);
            workloadData.put("totalHoursNeeded", 8); // Mock data
            workloadData.put("pendingTasksCount", 3);
            workloadData.put("isOverloaded", false);
            workloadData.put("stressLevel", "MEDIUM");
            
            // If EntityManager available, calculate real data
            if (taskService != null) {
                Integer workloadHours = taskService.calculateWorkload(uid);
                List<UserTaskEntity> pendingTasks = taskService.getUserTasksByStatus(uid, "pending");
                workloadData.put("totalHoursNeeded", workloadHours);
                workloadData.put("pendingTasksCount", pendingTasks.size());
                workloadData.put("isOverloaded", workloadHours > 40);
                workloadData.put("stressLevel", workloadHours > 40 ? "HIGH" : workloadHours > 20 ? "MEDIUM" : "LOW");
            }
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(workloadData);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to calculate workload: " + e.getMessage());
        }
    }
    
    /**
     * Get user tasks
     * GET /api/academic/tasks/{userId}
     */
    private void handleGetTasks(String userId, HttpServletResponse response) 
            throws IOException {
        try {
            Long uid = Long.parseLong(userId);
            List<UserTaskEntity> tasks = new ArrayList<>();
            
            // If EntityManager available, fetch real data, otherwise return in-memory fallback
            if (taskService != null) {
                tasks = taskService.getUserTasks(uid);
            } else {
                tasks = new ArrayList<>(inMemoryTasks.getOrDefault(uid, new ArrayList<>()));
            }
            
            ApiResponse<List<UserTaskEntity>> apiResponse = ApiResponse.success(tasks, "Tasks retrieved");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to get tasks: " + e.getMessage());
        }
    }
    
    /**
     * Start Pomodoro session
     * POST /api/academic/pomodoro/start
     * Body: {userId, taskId, durationMinutes}
     */
    private void handleStartPomodoro(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String body = getRequestBody(request);
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
            
            Long userId = json.get("userId").getAsLong();
            Long taskId = json.has("taskId") ? json.get("taskId").getAsLong() : null;
            Integer durationMinutes = json.has("durationMinutes") ? json.get("durationMinutes").getAsInt() : 25;
            
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionId", System.currentTimeMillis());
            sessionData.put("durationMinutes", durationMinutes);
            sessionData.put("startedAt", System.currentTimeMillis());
            
            if (pomodoroService != null) {
                PomodoroSessionEntity session = pomodoroService.startPomodoro(userId, taskId, durationMinutes);
                sessionData.put("sessionId", session.getId());
                sessionData.put("startedAt", session.getStartedAt());
            }
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(sessionData, "Pomodoro started");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to start Pomodoro: " + e.getMessage());
        }
    }
    
    /**
     * Complete Pomodoro session
     * POST /api/academic/pomodoro/complete
     * Body: {pomodoroId, breaksTaken}
     */
    private void handleCompletePomodoro(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String body = getRequestBody(request);
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
            
            Long pomodoroId = json.get("pomodoroId").getAsLong();
            Integer breaksTaken = json.has("breaksTaken") ? json.get("breaksTaken").getAsInt() : 0;
            
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionId", pomodoroId);
            sessionData.put("pointsAwarded", 10);
            sessionData.put("completedAt", System.currentTimeMillis());
            
            if (pomodoroService != null) {
                PomodoroSessionEntity session = pomodoroService.completePomodoro(pomodoroId, breaksTaken);
                sessionData.put("sessionId", session.getId());
                sessionData.put("pointsAwarded", session.getPointsAwarded());
                sessionData.put("completedAt", session.getCompletedAt());
            }
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(sessionData, "Pomodoro completed, points awarded!");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to complete Pomodoro: " + e.getMessage());
        }
    }
    
    /**
     * Get user points
     * GET /api/academic/points/{userId}
     */
    private void handleGetPoints(String userId, HttpServletResponse response) 
            throws IOException {
        try {
            Long uid = Long.parseLong(userId);
            Integer totalPoints = 0;
            Integer pomodorosToday = 0;
            
            if (pomodoroService != null) {
                totalPoints = pomodoroService.getUserTotalPoints(uid);
                pomodorosToday = pomodoroService.getTotalPomodorosToday(uid);
            } else {
                totalPoints = 250; // Mock data
                pomodorosToday = 3;
            }
            
            Map<String, Object> pointsData = new HashMap<>();
            pointsData.put("userId", uid);
            pointsData.put("totalPoints", totalPoints);
            pointsData.put("pomodorosToday", pomodorosToday);
            pointsData.put("level", (totalPoints / 100) + 1);
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(pointsData);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
        } catch (Exception e) {
            sendError(response, "Failed to get points: " + e.getMessage());
        }
    }
    
    private String getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
