package com.example.rpg.database.managers;

import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProgressManager {
    private final TaskDao taskDao;
    public ProgressManager(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
    public double getSuccessRate(long userId, long stageId) {
        List<Task> tasks = taskDao.getAllTasksForPlayerAndStage(userId, stageId);
        if (tasks == null || tasks.isEmpty()) return 0;
        int total = tasks.size();
        int done = 0;
        for (Task t : tasks) {
            if ("done".equalsIgnoreCase(t.status)) done++;
        }
        return (double) done / total * 100.0;
    }

     public int calculateAwardedXp(Task task, long userId) {

        Date now = new Date();
        List<Integer> doneToday = countDoneToday(userId, now);
        int doneWeek = countDoneThisWeek(userId, now);
        int doneMonth = countDoneThisMonth(userId, now);
        int difficulty = task.difficultyXP;
        int importance = task.importanceXP;

        if (task.difficultyXP == 1) {
            difficulty = (doneToday.get(0) <= 5 ? 1 : 0);
        }
        else if (task.difficultyXP == 3) {
            difficulty = (doneToday.get(1) <= 5 ? 3 : 0);
        }
        else if (task.difficultyXP == 7) {
            difficulty = (doneToday.get(2) <= 2 ? 7 : 0);
        }
        else if (task.difficultyXP == 20) {
            difficulty = (doneWeek <= 1 ? 20 : 0);
        }

         if (task.importanceXP == 1) {
             importance = (doneToday.get(3) <= 5 ? 1 : 0);
         }
         else if (task.importanceXP == 3) {
             importance = (doneToday.get(4) <= 5 ? 3 : 0);
         }
         else if (task.importanceXP == 10) {
             importance = (doneToday.get(5) <= 2 ? 10 : 0);
         }
         else if (task.importanceXP == 100) {
             importance = (doneMonth <= 1 ? 100 : 0);
         }

        task.totalXP = importance + difficulty;
        return task.totalXP;
    }
    private List<Integer> countDoneToday(long userId, Date today) {
        List<Task> tasks = taskDao.getAll();
        List<Integer> daily = new ArrayList<>();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);
        int veryEasyCount = 0, easyCount = 0, hardCount = 0, normalCount = 0, importantCount = 0, veryImportantCount = 0;

        for (Task t : tasks) {
            if (!"done".equals(t.status) || t.userId != userId) continue;
            if (t.completionTime == null) continue;
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(t.completionTime);
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
            {
                switch (t.difficultyXP) {
                    case 1:     veryEasyCount++;      break;
                    case 3:     easyCount++;          break;
                    case 7:     hardCount++;          break;
                }
                switch (t.importanceXP) {
                    case 1:     normalCount++;        break;
                    case 3:     importantCount++;     break;
                    case 10:    veryImportantCount++; break;
                }
            }
        }

        daily.add(veryEasyCount);
        daily.add(easyCount);
        daily.add(hardCount);
        daily.add(normalCount);
        daily.add(importantCount);
        daily.add(veryImportantCount);

        return daily;
    }

    private int countDoneThisWeek(long userId, Date today) {
        List<Task> tasks = taskDao.getAll();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(today);
        int targetWeek = nowCal.get(Calendar.WEEK_OF_YEAR);
        int targetYear = nowCal.get(Calendar.YEAR);
        int count = 0;

        for (Task t : tasks) {
            if (!"done".equals(t.status) || t.userId != userId) continue;
            if (t.completionTime == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(t.completionTime);
            if (c.get(Calendar.WEEK_OF_YEAR) == targetWeek &&
                    c.get(Calendar.YEAR) == targetYear) {
                count++;
            }
        }
        return count;
    }

    private int countDoneThisMonth(long userId, Date today) {
        List<Task> tasks = taskDao.getAll();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(today);
        int targetMonth = nowCal.get(Calendar.MONTH);
        int targetYear = nowCal.get(Calendar.YEAR);
        int count = 0;

        for (Task t : tasks) {
            if (!"done".equals(t.status) || t.userId != userId) continue;
            if (t.completionTime == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(t.completionTime);
            if (c.get(Calendar.MONTH) == targetMonth &&
                    c.get(Calendar.YEAR) == targetYear) {
                count++;
            }
        }
        return count;
    }
}


