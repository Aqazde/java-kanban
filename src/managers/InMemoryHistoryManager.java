package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> nodeMap = new HashMap<>();


    private void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        nodeMap.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;

        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node == head && node == tail) {
            head = tail = null;
        } else if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        nodeMap.remove(node.task.getId());
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
