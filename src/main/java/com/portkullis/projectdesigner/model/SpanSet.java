package com.portkullis.projectdesigner.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;

public class SpanSet<A> {

    private final List<Span<A>> spans;

    public SpanSet() {
        spans = emptyList();
    }

    public SpanSet(List<Span<A>> spans) {
        List<Span<A>> newSpans = new ArrayList<>(spans);
        newSpans.sort(comparing(Span::getStart));
        this.spans = unmodifiableList(newSpans);
    }

    @Override
    public String toString() {
        return "SpanSet{" +
                "spans=" + spans +
                '}';
    }

    public List<Span<A>> getSpans() {
        return spans;
    }

    public SpanSet getUnion(Span<A> span) {
        boolean spanAdded = false;

        List<Span<A>> newSpans = new ArrayList<>();
        for (Span<A> s : spans) {
            if (span.getEnd() < s.getStart()) {
                newSpans.add(span);
                spanAdded = true;
                newSpans.add(s);
            } else if (span.getStart() > s.getEnd()) {
                newSpans.add(s);
            } else {
                int start = Math.min(span.getStart(), s.getStart());
                int end = Math.max(span.getEnd(), s.getEnd());
                A activity = span.getEnd() > s.getEnd() ? span.getEndActivity() : s.getEndActivity();
                newSpans.add(new Span<>(start, end, activity));
                spanAdded = true;
            }
        }

        if (!spanAdded) {
            newSpans.add(span);
        }

        return new SpanSet(newSpans);
    }

    public SpanSet<A> getUnion(SpanSet<A> spanSet) {
        return Stream.concat(spans.stream(), spanSet.spans.stream())
                .reduce(new SpanSet<>(), SpanSet::getUnion, SpanSet::getUnion);
    }

}
