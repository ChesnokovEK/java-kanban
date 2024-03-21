import Manager.InMemoryTaskManager;
import Manager.Managers;
import Manager.TaskManager;
import Tasks.*;
import Enum.*;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        //Наполнение менеджера задачами
        taskManager.createTask(new Task("Описание-1", "Task-1")); // id 0
        taskManager.createTask(new Task("Описание-2", "Task-2")); // id 1
        taskManager.createEpic(new Epic("Описание-1", "Epic-1")); // id 2
        taskManager.createEpic(new Epic("Описание-1", "Epic-2")); // id 3
        taskManager.createSubTask(new SubTask("Описание-1", "Subtask-1", 3)); // id 4
        taskManager.createSubTask(new SubTask("Описание-2", "Subtask-2", 3)); // id 5
        taskManager.createSubTask(new SubTask("Описание-3", "Subtask-3", 3)); // id 6

        //Запрос разных задач по id
        taskManager.getTaskById(0);
        taskManager.getEpicById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);

        //Получение и вывод истории
        Collection<AbstractTask> history = taskManager.getHistory();
        System.out.println(history);
    }
}
