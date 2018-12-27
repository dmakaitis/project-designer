package com.portkullis.projectdesigner.engine.impl

import spock.lang.Specification

class GraphTest extends Specification {

    def static A = new Node(1)
    def static B = new Node(2)
    def static C = new Node(3)
    def static D = new Node(4)
    def static E = new Node(5)
    def static F = new Node(6)
    def static G = new Node(7)

    def static AB = new Edge<String>(A, B)
    def static AC = new Edge<String>(A, C)
    def static AD = new Edge<String>(A, D)
    def static AE = new Edge<String>(A, E)
    def static BC = new Edge<String>(B, C)
    def static BD = new Edge<String>(B, D)
    def static BE = new Edge<String>(B, E)
    def static CD = new Edge<String>(C, D)
    def static CE = new Edge<String>(C, E)
    def static EF = new Edge<String>(E, F)
    def static EG = new Edge<String>(E, G)

    def "Collapsing edges should work"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(AB)
        graph.getEdges().add(BC)
        graph.getEdges().add(CD)

        when:
        graph.collapseEdge(BC)

        then:
        graph.getNodes().size() == 3
        graph.getEdges().size() == 2
        graph.getEdges().containsAll([AC, CD])
    }

    def "Collapsing diamonds should work"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(AB)
        graph.getEdges().add(AC)
        graph.getEdges().add(BD)
        graph.getEdges().add(CD)

        when:
        graph.collapseEdge(BD)

        then:
        graph.getNodes().size() == 3
        graph.getEdges().size() == 3
        graph.getEdges().containsAll([AC, AD, CD])
    }

    def "Merging nodes should work"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(new Edge<>(A, B, "AB"))
        graph.getEdges().add(new Edge<>(A, C, "AC"))
        graph.getEdges().add(BD)
        graph.getEdges().add(BE)
        graph.getEdges().add(CD)
        graph.getEdges().add(CE)
        graph.getEdges().add(new Edge<>(D, F, "DF"))
        graph.getEdges().add(new Edge<>(E, G, "EG"))

        when:
        graph.mergeNodes(D, E)

        then:
        graph.getNodes().size() == 6
        graph.getNodes().containsAll([A, B, C, E, F, G])
        graph.getEdges().size() == 6
        graph.getEdges().contains(AB)
        graph.getEdges().contains(AC)
        graph.getEdges().contains(BE)
        graph.getEdges().contains(CE)
        graph.getEdges().contains(EF)
        graph.getEdges().contains(EG)
    }

    def "A graph with one dummy followed by one activity should be simplified to a graph with a single activity"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(AB)
        graph.getEdges().add(new Edge<>(B, C, "Test"))

        when:
        graph.simplifyDummies()

        then:
        graph.getNodes().size() == 2
        graph.getEdges().size() == 1
        graph.getEdges().contains(BC)
    }

    def "A graph with one dummy preceeded by one activity should be simplified to a graph with a single activity"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(new Edge<>(A, B, "Test"))
        graph.getEdges().add(BC)

        when:
        graph.simplifyDummies()

        then:
        graph.getNodes().size() == 2
        graph.getEdges().size() == 1
        graph.getEdges().contains(AC)
    }

    def "A graph with multiple nodes with the same prerequisites should be simplified by merging those nodes"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(new Edge<>(A, B, "AB"))
        graph.getEdges().add(new Edge<>(A, C, "AC"))
        graph.getEdges().add(BD)
        graph.getEdges().add(BE)
        graph.getEdges().add(CD)
        graph.getEdges().add(CE)
        graph.getEdges().add(new Edge<>(D, F, "DF"))
        graph.getEdges().add(new Edge<>(E, G, "EG"))

        when:
        graph.simplifyDummies()

        then:
        graph.getNodes().size() == 5
        graph.getNodes().containsAll([A, C, E, F, G])
        graph.getEdges().size() == 5
        graph.getEdges().contains(AC)
        graph.getEdges().contains(AE)
        graph.getEdges().contains(CE)
        graph.getEdges().contains(EF)
        graph.getEdges().contains(EG)
    }

    def "Triangles can not be collapsed"() {
        given:
        Graph<String> graph = new Graph<>()
        graph.getEdges().add(new Edge<>(A, B, "AB"))
        graph.getEdges().add(new Edge<>(A, C, "AC"))
        graph.getEdges().add(BC)
        graph.getEdges().add(new Edge<>(C, D, "CD"))

        when:
        graph.simplifyDummies()

        then:
        graph.getNodes().containsAll(A, B, C, D)
        graph.getEdges().containsAll(AB, AC, BC, CD)
    }

}
