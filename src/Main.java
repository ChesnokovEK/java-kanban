import manager.FileBackedTasksManager;
import tasks.*;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("data.csv");
        File file = new File(String.valueOf(path));
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        Task firstTask = new Task("Task-1", "Task-1 Description");
        manager.createTask(firstTask);
        Task secondTask = new Task("Task-2", "Task-2 Description");
        manager.createTask(secondTask);

        Epic firstEpic = new Epic("Epic-1", "Epic-1 Description");
        manager.createEpic(firstEpic);

        SubTask firstSubtask = new SubTask("SubTask-1 of Epic-1", "SubTask-1 Description", firstEpic.getId());
        manager.createSubTask(firstSubtask);

        manager.getTaskById(firstTask.getId());
        manager.getTaskById(secondTask.getId());
        System.out.println();

        System.out.println("Read from file");
        manager.loadFromFile(file);
        System.out.println("Tasks");
        System.out.println(manager.getAllTasks());
        System.out.println("Epics");
        System.out.println(manager.getAllEpics());
        System.out.println("SubTasks");
        System.out.println(manager.getAllSubTasks());
        System.out.println("History");
        System.out.println(manager.getHistory());
    }
}
