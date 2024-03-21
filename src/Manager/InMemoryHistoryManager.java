package Manager;

import Tasks.AbstractTask;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<AbstractTask> tasksHistory = new LinkedList<>();

    @Override
    public void add(AbstractTask task) {
        if (task == null) {
            System.out.println("Такой задачи не существует");
            return;
        }

        //из условий ТЗ, история хранится по последним 10 задачам
        int HISTORY_LIMIT = 10;
        if (tasksHistory.size() >= HISTORY_LIMIT) {
            tasksHistory.remove(0);
        }

        tasksHistory.add(task);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return tasksHistory;
    }
}
