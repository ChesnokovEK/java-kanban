package manager;

import enums.State;
import enums.TaskType;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String HEADER_CSV_FILE = "id,type,title,status,description,startTime,duration,epicId"
            + System.lineSeparator();
    private final File file;

    public FileBackedTasksManager(File fileName) {
        super();
        file = fileName;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }

                AbstractTask task = manager.fromString(line);
                TaskType taskType = manager.getType(task);

                switch (taskType) {
                    case TASK -> manager.addTask((Task) task);
                    case EPIC -> manager.addEpic((Epic) task);
                    case SUBTASK -> manager.addSubTask((SubTask) task);
                    default -> System.out.println("Неверный тип задачи");
                }
            }
            return manager;
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл для чтения не найден.");
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
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write(HEADER_CSV_FILE);

                for (Task task : getAllTasks()) {
                    writer.write(toString(task) + System.lineSeparator());
                }

                for (Epic epic : getAllEpics()) {
                    writer.write(toString(epic) + System.lineSeparator());
                }

                for (SubTask subtask : getAllSubTasks()) {
                    writer.write(toString(subtask) + System.lineSeparator());
                }

                writer.write(System.lineSeparator());
            } catch (IOException e) {
                throw new ManagerSaveException("Не удалось сохранить в файл", e);
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
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
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), getType(task).toString(), task.getTitle(),
                task.getState().toString(), task.getDescription(), task.getStartTime(),
                task.getDuration().toMinutes(), getParentEpicId(task)
        );
    }

    // Создание из строки
    private AbstractTask fromString(String value) {
        String[] params = value.split(",");
        switch (params[1]) {
            case "EPIC" -> {
                return new Epic(Integer.parseInt(params[0]), params[2], params[4],
                        LocalDateTime.parse(params[5]), Integer.parseInt(params[6])
                );
            }
            case "SUBTASK" -> {
                return new SubTask(Integer.parseInt(params[0]),
                        params[2], params[4], Integer.parseInt(params[7]),
                        LocalDateTime.parse(params[5]), Integer.parseInt(params[6]),
                        State.valueOf(params[3].toUpperCase())
                );
            }
            default -> {
                return new Task(Integer.parseInt(params[0]), params[2], params[4],
                        State.valueOf(params[3].toUpperCase()),
                        LocalDateTime.parse(params[5]), Integer.parseInt(params[6])
                );
            }
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