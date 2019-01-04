package com.portkullis.projectdesigner.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;

import static java.lang.Integer.max;
import static java.lang.Math.min;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;

/**
 * A set of spans. Spans within the set never overlap and are never immediately adjacent.
 *
 * @param <A> the activity type.
 */
public class SpanSet<A> {

    private final List<Span<A>> spans;

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

    /**
     * Returns the spans contained in the span set.
     *
     * @return the spans contained in this span set.
     */
    public List<Span<A>> getSpans() {
        return spans;
    }

    /**
     * Returns a collector that will convert a stream a Span objects into a single SpanSet that is the union of all the
     * Spans.
     *
     * @param <A> the activity type.
     * @return the span union collector.
     */
    public static <A> Collector<Span<A>, List<Span<A>>, SpanSet<A>> asUnion() {
        return Collector.of(
                ArrayList::new,
                SpanSet::addSpan,
                SpanSet::combineSpans,
                SpanSet::new,
                Collector.Characteristics.UNORDERED);
    }

    public static <A> Collector<SpanSet<A>, List<Span<A>>, SpanSet<A>> asIntersection() {
        return Collector.of(
                () -> {
                    List<Span<A>> list = new ArrayList<>();
                    list.add(new Span<>(Integer.MIN_VALUE, Integer.MAX_VALUE, null));
                    return list;
                },
                SpanSet::intersectListWithSpanSet,
                SpanSet::intersectSpanLists,
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

    private static <A> void intersectListWithSpanSet(List<Span<A>> spansA, SpanSet<A> spansB) {
        List<Span<A>> newSpans = intersectSpanLists(spansA, spansB.getSpans());
        spansA.clear();
        spansA.addAll(newSpans);
    }

    static <A> List<Span<A>> intersectSpanLists(List<Span<A>> spansA, List<Span<A>> spansB) {
        List<Span<A>> newList = new ArrayList<>();

        Iterator<Span<A>> iteratorA = spansA.iterator();
        Iterator<Span<A>> iteratorB = spansB.iterator();

        if (iteratorA.hasNext() && iteratorB.hasNext()) {
            loppThroughAndFindIntersections(newList, iteratorA, iteratorB);
        }

        return newList;
    }

    private static <A> void loppThroughAndFindIntersections(List<Span<A>> output, Iterator<Span<A>> intputA, Iterator<Span<A>> inputB) {
        Span<A> spanA = intputA.next();
        Span<A> spanB = inputB.next();

        while (spanA != null && spanB != null) {
            int overlapStart = max(spanA.getStart(), spanB.getStart());
            int overlapEnd = min(spanA.getEnd(), spanB.getEnd());
            A overlapActivity = overlapEnd == spanA.getEnd() ? spanA.getEndActivity() : spanB.getEndActivity();

            if (overlapStart < overlapEnd) {
                output.add(new Span<>(overlapStart, overlapEnd, overlapActivity));
            }

            if (spanA.getEnd() == overlapEnd) {
                spanA = getNextSpan(intputA);
            }
            if (spanB.getEnd() == overlapEnd) {
                spanB = getNextSpan(inputB);
            }
        }
    }

    private static <A> Span<A> getNextSpan(Iterator<Span<A>> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

}
