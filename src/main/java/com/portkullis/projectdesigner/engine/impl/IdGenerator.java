package com.portkullis.projectdesigner.engine.impl;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Utility class that generates identifier values.
 *
 * @author darius
 */
class IdGenerator {

    private static final String LABEL_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int CHAR_COUNT = LABEL_CHARS.length();

    private long lastId = 0;

    /**
     * Returns the next available ID.
     *
     * @return the next available ID.
     */
    long getNextId() {
        return ++lastId;
    }

    String getNextLabel() {
        long nextId = getNextId();
        Deque<Character> charStack = new ArrayDeque<>();
        while (nextId > 0) {
            char c = LABEL_CHARS.charAt((int) ((nextId - 1) % CHAR_COUNT));
            charStack.push(c);
            nextId = (nextId - 1) / CHAR_COUNT;
        }
        StringBuilder builder = new StringBuilder();
        while (!charStack.isEmpty()) {
            builder.append(charStack.pop());
        }
        return builder.toString();
    }

}
