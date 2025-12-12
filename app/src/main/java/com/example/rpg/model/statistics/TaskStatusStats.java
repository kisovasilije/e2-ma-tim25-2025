package com.example.rpg.model.statistics;

public class TaskStatusStats {
    public int tasksCreated;

    public int tasksDone;

    public int tasksUnfinished;

    public int tasksCanceled;

    public TaskStatusStats() {
        tasksCreated = 0;
        tasksDone = 0;
        tasksUnfinished = 0;
        tasksCanceled = 0;
    }

    public TaskStatusStats(int tasksCreated, int tasksDone, int tasksUnfinished, int tasksCanceled) {
        this.tasksCreated = tasksCreated;
        this.tasksDone = tasksDone;
        this.tasksUnfinished = tasksUnfinished;
        this.tasksCanceled = tasksCanceled;
    }
}
