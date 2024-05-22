package manager;

import enums.State;
import enums.TaskType;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static File file;
    private static final String HEADER_CSV_FILE = "id,type,title,status,description,epic\n";

    public FileBackedTasksManager(File fileName) {
        super();
        file = fileName;
    }

    public void loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }

                AbstractTask task = manager.fromString(line);

                if (task instanceof Epic epic) {
                    addEpic(epic);
                } else if (task instanceof SubTask subtask) {
                    addSubTask(subtask);
                } else {
                    addTask((Task) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER_CSV_FILE);

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subtask : getAllSubTasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    private String getParentEpicId(AbstractTask task) {
        if (task instanceof SubTask) {
            return Integer.toString(((SubTask) task).getRelatedEpicId());
        }
        return "";
    }

    private TaskType getType(AbstractTask task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof SubTask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    // Сохранение в строку
    private String toString(AbstractTask task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                task.getState().toString(), task.getDescription(), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    // Создание из строки
    private AbstractTask fromString(String value) {
        String[] params = value.split(",");
        if (params[1].equals("EPIC")) {
            Epic epic = new Epic(Integer.parseInt(params[0]), params[2], params[4]);
            epic.setState(State.valueOf(params[3].toUpperCase()));
            return epic;
        } else if (params[1].equals("SUBTASK")) {
            SubTask subtask = new SubTask(Integer.parseInt(params[0]), params[2], params[4], Integer.parseInt(params[5]));
            subtask.setState(State.valueOf(params[3].toUpperCase()));
            return subtask;
        } else {
            Task task = new Task(params[2], params[4]);
            task.setId(Integer.parseInt(params[0]));
            task.setState(State.valueOf(params[3].toUpperCase()));
            return task;
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subtask) {
        super.createSubTask(subtask);
        save();
    }

    public void addTask(Task task) {
        super.createTask(task);
    }

    public void addEpic(Epic epic) {
        super.createEpic(epic);
    }

    public void addSubTask(SubTask subtask) {
        super.createSubTask(subtask);
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subtask = super.getSubTaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
}