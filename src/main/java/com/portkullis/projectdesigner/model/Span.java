package com.portkullis.projectdesigner.model;

import java.util.Objects;

/**
 * Represents a span of time for a project during which activities are taking place.
 *
 * @author darius
 */
public class Span<A> {

    private final int start;
    private final int end;
    private final A endActivity;

    /**
     * Constructs the span.
     *
     * @param start       the start time.
     * @param end         the end time.
     * @param endActivity the activity that concludes the span.
     */
    public Span(int start, int end, A endActivity) {
        this.start = start;
        this.end = end;
        this.endActivity = endActivity;
    }

    @Override
    public String toString() {
        return "Span{" +
                "start=" + start +
                ", end=" + end +
                ", endActivity=" + endActivity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Span<?> span = (Span<?>) o;
        return start == span.start &&
                end == span.end &&
                endActivity.equals(span.endActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, endActivity);
    }

    /**
     * Returns the start time of the span.
     *
     * @return the start time of the span.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end time of the span.
     *
     * @return the end time of the span.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the last activity in the span.
     *
     * @return the last activity in the span.
     */
    public A getEndActivity() {
        return endActivity;
    }

}
