import manager.Managers;
import manager.TaskManager;
import tasks.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager();

        taskManager.createTask(new Task("Описание-1", "Task-1")); // id 0
        taskManager.createTask(new Task("Описание-2", "Task-2")); // id 1
        taskManager.createEpic(new Epic("Описание-1", "Epic-1")); // id 2
        taskManager.createEpic(new Epic("Описание-1", "Epic-2")); // id 3
        taskManager.createSubTask(new SubTask("Описание-1", "Subtask-1", 3)); // id 4
        taskManager.createSubTask(new SubTask("Описание-2", "Subtask-2", 3)); // id 5
        taskManager.createSubTask(new SubTask("Описание-3", "Subtask-3", 3)); // id 6

        System.out.println("Обращаемся к таскам");
        taskManager.getTaskById(0);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getTaskById(0);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);

        System.out.println("Запрашиваем историю обращений");
        List<AbstractTask> history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("Удаляем таски с id 0, 3");
        taskManager.removeTask(0);
        taskManager.removeEpic(3);

        System.out.println("Проверяем что история не отображает удаленные таски");
        List<AbstractTask> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);
    }
}
