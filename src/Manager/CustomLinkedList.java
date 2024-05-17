package Manager;

import Tasks.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CustomLinkedList {
    private final Map<Integer, Node> tasksHistory = new HashMap<>();
    private Node head;
    private Node tail;

    void linkLast(AbstractTask task) {
        Node element = new Node();
        element.setTask(task);

        if (tasksHistory.containsKey(task.getId())) {
            removeNode(tasksHistory.get(task.getId()));
        }

        if (head == null) {
            tail = element;
            head = element;
            element.setNext(null);
            element.setPrevious(null);
        } else {
            element.setPrevious(tail);
            element.setNext(null);
            tail.setNext(element);
            tail = element;
        }

        tasksHistory.put(task.getId(), element);
    }

    List<AbstractTask> getTasks() {
        List<AbstractTask> result = new ArrayList<>();
        Node element = head;
        while (element != null) {
            result.add((element.getTask()));
            element = element.getNext();
        }
        return result;
    }

    void removeNode(Node node) {
        if (node != null) {
            tasksHistory.remove(node.getTask().getId());
            Node previous = node.getPrevious();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrevious();
            }
            if (previous != null) {
                previous.setNext(next);
            }
            if (next != null) {
                next.setPrevious(previous);
            }
        }
    }

    Node getNode(int id) {
        return tasksHistory.get(id);
    }

    private static class Node {
        private AbstractTask task;
        private Node previous;
        private Node next;

        public Node getNext() {
            return next;
        }

        public Node getPrevious() {
            return previous;
        }

        public AbstractTask getTask() {
            return task;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void setPrevious(Node prev) {
            this.previous = prev;
        }

        public void setTask(AbstractTask task) {
            this.task = task;
        }
    }
}
