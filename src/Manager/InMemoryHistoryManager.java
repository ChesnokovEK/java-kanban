package Manager;

import Tasks.AbstractTask;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //из условий ТЗ, история хранится по последним 10 задачам
    final int HISTORY_LIMIT = 10;
    private final LinkedList<AbstractTask> tasksHistory = new LinkedList<>();

    @Override
    public void add(AbstractTask task) {
        if (task == null) {
            System.out.println("Такой задачи не существует");
            return;
        }

        if (tasksHistory.size() == HISTORY_LIMIT) {
            tasksHistory.removeFirst();
        }

        tasksHistory.add(task.clone());
    }

    @Override
    public List<AbstractTask> getHistory() {
        return tasksHistory;
    }
}
