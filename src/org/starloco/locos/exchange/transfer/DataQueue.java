package org.starloco.locos.exchange.transfer;

import java.util.*;

/**
 * Created by Locos on 15/09/2015.
 */
public class DataQueue {

    private static long count = 0;
    public final static Map<Long, Queue<?>> queues = new HashMap<>();

    public static synchronized long count() {
        return count++;
    }

    public static class Queue<T> {

        private final byte type;
        private T value;

        public Queue(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        public void setValue(T value) {
            synchronized(this) {
                this.value = value;
                this.notify();
            }
        }

        public T getValue() {
            return value;
        }
    }
}
