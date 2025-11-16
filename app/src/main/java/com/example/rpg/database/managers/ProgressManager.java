package com.example.rpg.database.managers;

import com.example.rpg.database.daos.TaskDao;
import com.example.rpg.model.Task;

import java.util.Calendar;
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

        int doneToday = countDoneToday(userId, now);

        int doneWeek = countDoneThisWeek(userId, now);

        int doneMonth = countDoneThisMonth(userId, now);


        if (isVeryEasy(task)) {
            if (doneToday <= 5)
            {task.difficultyXP = 1;}
        }

         if (isEasy(task)) {
             if (doneToday <= 5)
             {task.difficultyXP = 3;}
         }

         if (isNormal(task)) {
             if (doneToday <= 5)
             {task.difficultyXP = 1;}
         }

         if (isImportant(task)) {
             if (doneToday <= 5)
             {task.difficultyXP = 3;}
         }

        if (isHard(task)) {
            if (doneToday <= 2)
            {task.difficultyXP = 7;}
        }

         if (isExtremelyImportant(task)) {
             if (doneToday <= 2)
             {task.difficultyXP = 10;}
         }

        if (isExtremelyHard(task)) {
            if (doneWeek <= 1)
            {task.difficultyXP = 20;}
        }

        if (isSpecial(task)) {
            if (doneMonth <= 1)
            {task.difficultyXP = 100;}
        }
        task.totalXP = task.importanceXP + task.difficultyXP;
        return task.totalXP;
    }

    private boolean isVeryEasy(Task t) { return t.difficultyXP == 1; }
    private boolean isEasy(Task t) { return t.difficultyXP == 3; }
    private boolean isHard(Task t) { return t.difficultyXP == 7; }
    private boolean isExtremelyHard(Task t) { return t.difficultyXP == 20; }

    private boolean isNormal(Task t) { return t.importanceXP == 1; }
    private boolean isImportant(Task t) { return t.importanceXP == 3; }
    private boolean isExtremelyImportant(Task t) { return t.importanceXP == 10; }
    private boolean isSpecial(Task t) { return t.importanceXP == 100; }

    private int countDoneToday(long userId, Date today) {
        List<Task> tasks = taskDao.getAll();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(today);

        int count = 0;

        for (Task t : tasks) {
            if (!"done".equals(t.status) || t.userId != userId) continue;
            if (t.completionTime == null) continue;

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(t.completionTime);

            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {

                count++;
            }
        }
        return count;
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


