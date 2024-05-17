package manager;

import Tasks.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList list = new CustomLinkedList();

    @Override
    public void add(AbstractTask task) {
        list.linkLast(task);
    }

    @Override
    public void remove(int id) {
        list.removeNode(list.getNode(id));
    }

    @Override
    public List<AbstractTask> getHistory() {
        return list.getTasks();
    }
}

