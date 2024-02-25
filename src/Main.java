import Manager.TaskManager;
import Tasks.*;
import Enum.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

//        Из ТЗ - создать две задачи
        Task task1 = new Task("Задача 1", "Создать задачу 1");
        Task task2 = new Task("Задача 2", "Создать задачу 2");
        manager.addTask(task1);
        manager.addTask(task2);

//        Из ТЗ - создать эпик с двумя подзадачами
        Epic epic1 = new Epic( "Эпик 1", "Создать эпик 1");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Создать подзадачу 1 для эпика 1", epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Создать подзадачу 2 для эпика 1", epic1.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

//        Из ТЗ - создать эпик с одной подзадачей
        Epic epic2 = new Epic("Эпик 2", "Создать эпик 2");
        manager.addEpic(epic2);
        SubTask subTask3 = new SubTask("Подзадача 1", "Создать подзадачу 1 для эпика 2", epic2.getId());
        manager.addSubTask(subTask3);

//        Из ТЗ - распечатать списки эпиков, задач и подзадач через System.out.println(..)
        System.out.println(
            "Все эпики: " + manager.getAllEpics() + System.lineSeparator()
            + "Все задачи: " + manager.getAllTasks() + System.lineSeparator()
            + "Все подзадачи: " + manager.getAllSubTasks()
        );

//        Из ТЗ - Изменить статусы созданных объектов
        task1.setState(State.IN_PROGRESS);
        task2.setState(State.DONE);
        subTask1.setState(State.IN_PROGRESS);
        subTask2.setState(State.DONE);
        subTask3.setState(State.IN_PROGRESS);
        manager.updateTask(task1);
        manager.updateTask(task2);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

//        Из ТЗ - распечатать измененные объекты
        System.out.println(
            "Задачи после изменения статусов:" + System.lineSeparator()
            + "Все эпики: " + manager.getAllEpics() + System.lineSeparator()
            + "Все задачи: " + manager.getAllTasks() + System.lineSeparator()
            + "Все подзадачи: " + manager.getAllSubTasks()
        );

//        Из ТЗ - удалить одну из задач и один из эпиков
        manager.removeTask(task2.getId());
        manager.removeEpic(epic2.getId());

        System.out.println(
            "Задачи после удаления:" + System.lineSeparator()
            + "Все эпики: " + manager.getAllEpics() + System.lineSeparator()
            + "Все задачи: " + manager.getAllTasks() + System.lineSeparator()
            + "Все подзадачи: " + manager.getAllSubTasks()
        );
    }
}
