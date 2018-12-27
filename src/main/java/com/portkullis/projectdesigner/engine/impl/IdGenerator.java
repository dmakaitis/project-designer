package com.portkullis.projectdesigner.engine.impl;

import java.util.Stack;

/**
 * Utility class that generates identifier values.
 *
 * @author darius
 */
public class IdGenerator {

    private long lastId = 0;

    /**
     * Returns the next available ID.
     *
     * @return the next available ID.
     */
    public long getNextId() {
        return ++lastId;
    }

    public String getNextLabel() {
        long nextId = getNextId();
        Stack<Character> charStack = new Stack<>();
        while (nextId > 0) {
            char c = (char) ('A' + ((nextId - 1) % 26));
            charStack.push(c);
            nextId = (nextId - 1) / 26;
        }
        StringBuilder builder = new StringBuilder();
        while (!charStack.isEmpty()) {
            builder.append(charStack.pop());
        }
        return builder.toString();
    }

}
