package com.portkullis.projectdesigner.model

import spock.lang.Specification

import java.util.stream.Stream

import static com.portkullis.projectdesigner.model.SpanSet.*

class SpanSetTest extends Specification {

    def "Adding a span to an empty list should result in a list containing only that span"() {
        given:
        def spans = []

        def newSpan = new Span<>(1, 5, "Test")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [newSpan]
    }

    def "Adding a span to a list that contains a non-overlapping span that preceeds the span should result in the old span followed by the new span"() {
        given:
        def oldSpan = new Span<>(1, 10, "SpanA")
        def spans = [oldSpan]

        def newSpan = new Span<>(20, 30, "SpanB")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [oldSpan, newSpan]
    }

    def "Adding a span to a list that contains a non-overlapping span that follows the span should result in the new span followed by the old span"() {
        given:
        def oldSpan = new Span<>(20, 30, "SpanB")
        def spans = [oldSpan]

        def newSpan = new Span<>(1, 10, "SpanA")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [newSpan, oldSpan]
    }

    def "Adding a span to a list where the new span is completely within an existing span in the list should result in a list containing only the original span"() {
        given:
        def oldSpan = new Span<>(1, 100, "SpanA")
        def spans = [oldSpan]

        def newSpan = new Span<>(20, 30, "SpanB")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [oldSpan]
    }

    def "Adding a span to a list where the old span is completely within the new span should result in the list containing only the new span"() {
        given:
        def oldSpan = new Span<>(20, 30, "SpanB")
        def spans = [oldSpan]

        def newSpan = new Span<>(1, 100, "SpanA")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [newSpan]
    }

    def "Adding a span where the new span immediately follows the old span should result in a single span covering the entire time range with the activity from the new span"() {
        def oldSpan = new Span<>(1, 20, "Old Activity")
        def spans = [oldSpan]

        def newSpan = new Span<>(20, 40, "New Activity")

        def expected = new Span<>(1, 40, "New Activity")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [expected]
    }

    def "Adding a span that bridges the gap between two existing spans should result in a single span covering the entire range"() {
        def oldSpanA = new Span<>(1, 20, "Span A")
        def oldSpanB = new Span<>(30, 50, "Span B")
        def spans = [oldSpanA, oldSpanB]

        def newSpan = new Span<>(20, 30, "New Span")

        when:
        addSpan(spans, newSpan)

        then:
        spans == [new Span<>(1, 50, "Span B")]
    }

    def "Merging two sets of spans should work"() {
        given:
        def spanA1 = new Span<>(1, 20, "Span A1")
        def spanA2 = new Span<>(30, 45, "Span A2")
        def spansA = [spanA1, spanA2]

        def spanB1 = new Span<>(10, 15, "Span B1")
        def spanB2 = new Span<>(20, 30, "Span B2")
        def spanB3 = new Span<>(50, 60, "Span B3")
        def spansB = [spanB1, spanB2, spanB3]

        when:
        def result = combineSpans(spansA, spansB)

        then:
        result == [new Span<>(1, 45, "Span A2"), spanB3]
    }

    def "Collecting multiple spans into a span set should work"() {
        given:
        def span1 = new Span<>(1, 20, "Span 1")
        def span2 = new Span<>(10, 15, "Span 2")
        def span3 = new Span<>(20, 30, "Span 3")
        def span4 = new Span<>(30, 45, "Span 4")
        def span5 = new Span<>(50, 60, "Span 5")

        def expected = new SpanSet<>([new Span<>(1, 45, "Span 4"), span5])

        when:
        def result = Stream.of(span1, span2, span3, span4, span5).collect(asUnion())

        then:
        result == expected
    }

    def "The intersection of two span sets with no overlapping spans is an empty span set"() {
        given:
        def span1 = new Span<>(10, 20, "Span 1")
        def spanSetA = [span1]

        def span2 = new Span<>(30, 50, "Span 2")
        def spanSetB = [span2]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == []
    }

    def "The intersection of two spans where the first span is completely within the second is the first span"() {
        given:
        def span1 = new Span<>(10, 20, "Span 1")
        def spanSetA = [span1]

        def span2 = new Span<>(0, 30, "Span 2")
        def spanSetB = [span2]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == [span1]
    }

    def "The intersection of two spans where the second span is completely within the first is the second span"() {
        given:
        def span1 = new Span<>(0, 30, "Span 1")
        def spanSetA = [span1]

        def span2 = new Span<>(10, 20, "Span 2")
        def spanSetB = [span2]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == [span2]
    }

    def "The intersection of two spans where the first span preceeds and partially overlaps the second span is the overlapping portion of the two spans"() {
        given:
        def span1 = new Span<>(0, 30, "Span 1")
        def spanSetA = [span1]

        def span2 = new Span<>(10, 50, "Span 2")
        def spanSetB = [span2]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == [new Span<>(10, 30, "Span 1")]
    }

    def "The intersection of two spans where the first span follows and partially overlaps the second span is the overlapping portion of the two spans"() {
        given:
        def span1 = new Span<>(10, 50, "Span 1")
        def spanSetA = [span1]

        def span2 = new Span<>(0, 30, "Span 2")
        def spanSetB = [span2]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == [new Span<>(10, 30, "Span 2")]
    }

    def "The intersection of two span set lists with multiple overlapping spans is the overlapping portions of the combined span sets"() {
        given:
        def span1 = new Span<>(10, 20, "Span 1")
        def span2 = new Span<>(30, 50, "Span 2")
        def span3 = new Span<>(70, 100, "Span 3")
        def spanSetA = [span1, span2, span3]

        def span4 = new Span<>(0, 15, "Span 4")
        def span5 = new Span<>(45, 55, "Span 5")
        def span6 = new Span<>(80, 90, "Span 6")
        def spanSetB = [span4, span5, span6]

        expect:
        intersectSpanLists(spanSetA, spanSetB) == [
                new Span<>(10, 15, "Span 4"),
                new Span<>(45, 50, "Span 2"),
                new Span<>(80, 90, "Span 6")
        ]
    }

    def "The intersection of two span sets with multiple overlapping spans is the overlapping portions of the combined span sets"() {
        given:
        def span1 = new Span<>(10, 20, "Span 1")
        def span2 = new Span<>(30, 50, "Span 2")
        def span3 = new Span<>(70, 100, "Span 3")
        def spanSetA = [span1, span2, span3].stream().collect(asUnion())

        def span4 = new Span<>(0, 15, "Span 4")
        def span5 = new Span<>(45, 55, "Span 5")
        def span6 = new Span<>(80, 90, "Span 6")
        def spanSetB = [span4, span5, span6].stream().collect(asUnion())

        def expected = [
                new Span<>(10, 15, "Span 4"),
                new Span<>(45, 50, "Span 2"),
                new Span<>(80, 90, "Span 6")
        ].stream().collect(asUnion())

        when:
        def result = [spanSetA, spanSetB].stream().collect(asIntersection())

        then:
        result == expected
    }

}
