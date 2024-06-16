import enums.State;
import manager.FileBackedTasksManager;
import tasks.*;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("data.csv");
        File file = new File(String.valueOf(path));
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(file);

        Task firstTask = new Task(0, "Task-1", "Task-1 Description", State.NEW,
                LocalDateTime.parse("01-01-21 00:00", DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")), 40
        );
        manager.createTask(firstTask);
        Task secondTask = new Task(0, "Task-2", "Task-2 Description", State.NEW);
        manager.createTask(secondTask);
        final String date = "01-01-23 00:00";
        LocalDateTime localDateTime = LocalDateTime.parse("2021-05-30T19:56:37.047655100");

        Epic firstEpic = new Epic(0,"Epic-1", "Epic-1 Description",
                localDateTime, 40
        );
        manager.createEpic(firstEpic);
        SubTask firstSubtask = new SubTask(0, "SubTask-1 of Epic-1", "SubTask-1 Description", firstEpic.getId());
        manager.createSubTask(firstSubtask);

        firstSubtask.setState(State.IN_PROGRESS);
        manager.updateSubTask(firstSubtask);
        manager.getTaskById(firstTask.getId());
        manager.getTaskById(secondTask.getId());
        System.out.println("Задачи");
        System.out.println(manager.getAllTasks());
        System.out.println("Эпики");
        System.out.println(manager.getAllEpics());
        System.out.println("Подзадачи");
        System.out.println(manager.getAllSubTasks());
        System.out.println("История");
        System.out.println(manager.getHistory());
        System.out.println("Список задач в зависимости от приоритета");
        System.out.println(manager.getPrioritizedTasks());
    }
}
