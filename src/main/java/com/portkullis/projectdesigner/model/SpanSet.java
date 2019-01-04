package com.portkullis.projectdesigner.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;

/**
 * A set of spans. Spans within the set never overlap and are never immediately adjacent.
 *
 * @param <A> the activity type.
 */
public class SpanSet<A> {

    private final List<Span<A>> spans;

    public SpanSet() {
        spans = emptyList();
    }

    SpanSet(List<Span<A>> spans) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpanSet<?> spanSet = (SpanSet<?>) o;
        return spans.equals(spanSet.spans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spans);
    }

    public List<Span<A>> getSpans() {
        return spans;
    }

    public static <A> Collector<Span<A>, List<Span<A>>, SpanSet<A>> asUnion() {
        return Collector.of(
                ArrayList::new,
                SpanSet::addSpan,
                SpanSet::combineSpans,
                SpanSet::new,
                Collector.Characteristics.UNORDERED);
    }

    static <A> void addSpan(List<Span<A>> spans, Span<A> span) {
        spans.add(span);
        spans.sort(comparing(Span::getStart));

        List<Span<A>> newSpans = new ArrayList<>();
        Span<A> currentSpan = null;
        for (Span<A> s : spans) {
            if (currentSpan == null) {
                currentSpan = s;
            } else {
                if (s.getStart() > currentSpan.getEnd()) {
                    newSpans.add(currentSpan);
                    currentSpan = s;
                } else if (s.getEnd() > currentSpan.getEnd()) {
                    currentSpan = new Span<>(currentSpan.getStart(), s.getEnd(), s.getEndActivity());
                }
            }
        }

        if (currentSpan != null) {
            newSpans.add(currentSpan);
        }

        spans.clear();
        spans.addAll(newSpans);
    }

    static <A> List<Span<A>> combineSpans(List<Span<A>> spansA, List<Span<A>> spansB) {
        List<Span<A>> newList = new ArrayList<>(spansA);
        spansB.forEach(s -> addSpan(newList, s));
        return newList;
    }

}
