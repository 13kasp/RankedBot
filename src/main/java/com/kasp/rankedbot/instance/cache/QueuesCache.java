package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Queue;

import java.util.HashMap;
import java.util.Map;

public class QueuesCache {

    private static HashMap<String, Queue> queues = new HashMap<>();

    public static Queue getQueue(String ID) {
        return queues.get(ID);
    }

    public static void addQueue(Queue queue) {
        queues.put(queue.getID(), queue);

        System.out.println("Queue " + queue.getID() + " has been loaded into memory");
    }

    public static void removeQueue(Queue queue) {
        queues.remove(queue.getID());
    }

    public static boolean containsQueue(String ID) {
        return queues.containsKey(ID);
    }

    public static Queue initializeQueue(String ID, Queue queue) {
        if (!containsQueue(ID))
            addQueue(queue);

        return getQueue(ID);
    }

    public static Map<String, Queue> getQueues() {
        return queues;
    }
}
