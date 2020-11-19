/*
A Programming Assignment for Data Structures and Algorithms completed
by John Harrington in November, 2020.

This assignment is being shared in order to demonstrate my work to future
employers. I do not authorize nor encourage any academic misconduct that may
result from sharing this content online.

This assignment was designed by Robert Sedgewick and Kevin Wayne as part of
their Algorithms Coursera Course partnered with Princeton University.

This program contains three classes: Wordnet, ShortestCommonAncestor, and Outcast.

Wordnet will accept a list of numbered nouns and a separate list describing how
these nouns are related. A directed graph will be created representing these
semantic relationships.

ShortestCommonAncestor will determine the vertex shared by two other vertices,
if any, within the graph that is the shortest common ancestor of two specified
nouns (represented as vertices), and the total distance (number of edges)
between these nouns.

Outcast will accept a list of nouns and determine which noun is the 'outcast,'
that is which noun is least semantically related to the others.

Supporting code for this assignment is part of the Algorithms Fourth Edition
Library and can be found here: https://algs4.cs.princeton.edu/code/
 */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Topological;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class ShortestCommonAncestor {
    private Digraph DAG;                    // digraph in question
    private ArrayList<Integer> keysToNull; // keep track of vertex indexed array indices which have been modified
    private Queue<Integer> firstQ;         // Queue for BFS on first vertex or subset
    private Queue<Integer> secondQ;        // Queue for BFS on second vertex or subset
    private int[] distV;                   // each index represents a vertex in graph, it's value represents the distance that vertex is from v
    private int[] distW;                   // each index represents a vertex in graph, it's value represents the distance that vertex is from w

    private LinearProbingHashST<Integer, Integer> distanceByReachables;


    // constructor takes a rooted DAG as argument
    public ShortestCommonAncestor(Digraph G) {
        this.DAG = new Digraph(G);
        Topological T = new Topological(DAG);
        if (!T.hasOrder()) throw new IllegalArgumentException();
        keysToNull = new ArrayList<Integer>();
        firstQ = new Queue<Integer>();
        secondQ = new Queue<Integer>();

        // initialize all vertices to value -1 to indicate they have not been visited or passed in as parameters
        distV = new int[DAG.V()];
        for (int i = 0; i < distV.length; i++) {
            distV[i] = -1;
        }

        distW = new int[DAG.V()];
        for (int i = 0; i < distW.length; i++) {
            distW[i] = -1;
        }

        distanceByReachables = new LinearProbingHashST<Integer, Integer>();
    }

    // length of shortest ancestral path between v and w
    public int length(int v, int w) {
        checkVertex(v);
        checkVertex(w);

        if (v == w) return 0;

        // Operations on v
        distV[v] = 0;               // v has distance 0 from itself
        keysToNull.add(v);          // add v to list of indices to reset to -1 upon reentry to this function
        firstQ.enqueue(v);          // place v in queue
        while (!firstQ.isEmpty()) {
            int nextInQueue = firstQ.dequeue();
            for (int adjacent : DAG
                    .adj(nextInQueue)) {     // for each vertex adjacent to v (or reachable from v)
                distV[adjacent] = distV[nextInQueue]
                        + 1; // set distance of that vertex from v to the number of BFS iterations that got us here
                keysToNull.add(adjacent);                 // keep track of indices to reset to -1
                firstQ.enqueue(adjacent);                 // place adjacent vertex on queue
            }
        }

        // Operations on w
        distW[w] = 0;
        keysToNull.add(w);
        secondQ.enqueue(w);
        while (!secondQ.isEmpty()) {
            int nextInQueue = secondQ.dequeue();
            for (int adjacent : DAG.adj(nextInQueue)) {
                distW[adjacent] = distW[nextInQueue] + 1;
                keysToNull.add(adjacent);
                secondQ.enqueue(adjacent);
                if (distV[adjacent] != -1) {  // optimization: why not find distance here?
                    distanceByReachables.put(adjacent, (distV[adjacent]) + distW[adjacent]);
                }
            }
        }

        int shortest = DAG.E();

        for (int key : distanceByReachables.keys()) { // for each index reachable from both v and w
            if (distanceByReachables.get(key) < shortest) { // find the shortest distance
                shortest = distanceByReachables.get(key);
            }
        }

        // CLEAR WORK DONE IN CALL
        for (int key : keysToNull) {
            distV[key] = -1;
            distW[key] = -1;
        }

        for (int key : distanceByReachables.keys()) {
            distanceByReachables.delete(key);
        }

        keysToNull.clear();

        return shortest;
    }

    // a shortest common ancestor of vertices v and w
    public int ancestor(int v, int w) {
        checkVertex(v);
        checkVertex(w);

        if (v == w) return v;

        // Operations on v
        distV[v] = 0;               // v has distance 0 from itself
        keysToNull.add(v);          // add v to list of indices to reset to -1 upon reentry to this function
        firstQ.enqueue(v);          // place v in queue
        while (!firstQ.isEmpty()) {
            int nextInQueue = firstQ.dequeue();
            for (int adjacent : DAG.adj(nextInQueue)) {     // for each vertex adjacent to v (or reachable from v)
                distV[adjacent] = distV[nextInQueue] + 1; // set distance of that vertex from v to the number of BFS iterations that got us here
                keysToNull.add(adjacent);                 // keep track of indices to reset to -1
                firstQ.enqueue(adjacent);                 // place adjacent vertex on queue
            }
        }

        // Operations on w
        distW[w] = 0;
        keysToNull.add(w);
        secondQ.enqueue(w);
        while (!secondQ.isEmpty()) {
            int nextInQueue = secondQ.dequeue();
            for (int adjacent : DAG.adj(nextInQueue)) {
                distW[adjacent] = distW[nextInQueue] + 1;
                keysToNull.add(adjacent);
                secondQ.enqueue(adjacent);
                if (distV[adjacent] != -1) {  // optimization: why not find distance here?
                    distanceByReachables.put(adjacent, (distV[adjacent]) + distW[adjacent]);
                }
            }
        }

        int ancestor = -999;
        int shortest = DAG.E();
        for (int key : distanceByReachables.keys()) { // for each index reachable from both v and w
            if (distanceByReachables.get(key) <= shortest) { // find the shortest distance
                shortest = distanceByReachables.get(key);
                ancestor = key;
            }
        }

        // CLEAR WORK DONE IN CALL
        for (int key : keysToNull) {
            distV[key] = -1;
            distW[key] = -1;
        }

        for (int key : distanceByReachables.keys()) {
            distanceByReachables.delete(key);
        }

        keysToNull.clear();

        return ancestor;
    }

    // length of shortest ancestral path of vertex subsets A and B
    public int length(Iterable<Integer> subsetA, Iterable<Integer> subsetB) throws IOException {
        // Output shortest length of all pairs
        if (subsetA == null || subsetB == null) throw new NullPointerException();
        Iterator<Integer> aIT = subsetA.iterator();
        Iterator<Integer> bIT = subsetB.iterator();
        if ((!aIT.hasNext()) || !bIT.hasNext()) throw new IllegalArgumentException();

        // operations for first subset
        for (int s : subsetA) {
            distV[s] = 0;           // set all subset indices to visited reachable (0)
            keysToNull.add(s);      // add to reset list
            firstQ.enqueue(s);      // add all elements of subset to the queue
        }

        while (!firstQ.isEmpty()) {
            int next = firstQ.dequeue();
            for (int adjacent : DAG.adj(next)) {
                distV[adjacent] = distV[next] + 1;
                keysToNull.add(adjacent);
                firstQ.enqueue(adjacent);
            }
        }

        // operations for second subset
        for (int s : subsetB) {
            distW[s] = 0;
            keysToNull.add(s);
            secondQ.enqueue(s);
        }

        while (!secondQ.isEmpty()) {
            int nextInQueue = secondQ.dequeue();
            for (int adjacent : DAG.adj(nextInQueue)) {
                distW[adjacent] = distW[nextInQueue] + 1;
                keysToNull.add(adjacent);
                secondQ.enqueue(adjacent);
                if (distV[adjacent] != -1) { // find distance here, reduce iterations later
                    distanceByReachables.put(adjacent, (distV[adjacent]) + distW[adjacent]);
                }
            }
        }

        int shortest = DAG.E();

        for (int key : distanceByReachables.keys()) { // for each index reachable from both v and w
            if (distanceByReachables.get(key) < shortest) { // find the shortest distance
                shortest = distanceByReachables.get(key);
            }
        }

        // CLEAR WORK DONE IN CALL
        for (int key : keysToNull) {
            distV[key] = -1;
            distW[key] = -1;
        }

        for (int key : distanceByReachables.keys()) {
            distanceByReachables.delete(key);
        }

        keysToNull.clear();

        return shortest;
    }

    // a shortest common ancestor of vertex subsets A and B
    public int ancestor(Iterable<Integer> subsetA, Iterable<Integer> subsetB) throws IOException {
        // Output shortest common ancestor of all pairs
        if (subsetA == null || subsetB == null) throw new NullPointerException();
        Iterator<Integer> aIT = subsetA.iterator();
        Iterator<Integer> bIT = subsetB.iterator();
        if ((!aIT.hasNext()) || !bIT.hasNext()) throw new IllegalArgumentException();

        // operations for first subset
        for (int s : subsetA) {
            distV[s] = 0;           // set all subset indices to visited reachable (0)
            keysToNull.add(s);      // add to reset list
            firstQ.enqueue(s);      // add all elements of subset to the queue
        }

        while (!firstQ.isEmpty()) {
            int next = firstQ.dequeue();
            for (int adjacent : DAG.adj(next)) {
                distV[adjacent] = distV[next] + 1;
                keysToNull.add(adjacent);
                firstQ.enqueue(adjacent);
            }
        }

        // operations for second subset
        for (int s : subsetB) {
            distW[s] = 0;
            keysToNull.add(s);
            secondQ.enqueue(s);
        }

        while (!secondQ.isEmpty()) {
            int nextInQueue = secondQ.dequeue();
            for (int adjacent : DAG.adj(nextInQueue)) {
                distW[adjacent] = distW[nextInQueue] + 1;
                keysToNull.add(adjacent);
                secondQ.enqueue(adjacent);
                if (distV[adjacent] != -1) { // find distance here, reduce iterations later
                    distanceByReachables.put(adjacent, (distV[adjacent]) + distW[adjacent]);
                }
            }
        }
        int ancestor = -999;
        int shortest = DAG.E();
        for (int key : distanceByReachables.keys()) { // for each vertex reachable from v and w
            if (distanceByReachables.get(key) <= shortest) { // find the shortest distance
                shortest = distanceByReachables.get(key);
                ancestor = key;
            }
        }

        // CLEAR WORK DONE IN CALL
        for (int key : keysToNull) {
            distV[key] = -1;
            distW[key] = -1;
        }

        for (int key : distanceByReachables.keys()) {
            distanceByReachables.delete(key);
        }

        keysToNull.clear();

        return ancestor;
    }


    public void checkVertex(int v) {
        int max = DAG.V();
        if (v < 0 || v > max) throw new IndexOutOfBoundsException();
    }


    // do unit testing of this class
    public static void main(String[] args) throws IOException {

        // Build unit tests
        if (args.length < 1) {
            manualUnitTest();
        } else {
            In in = new In(args[0]);
            Digraph G = new Digraph(in);
            ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
            while (!StdIn.isEmpty()) {
                int v = StdIn.readInt();
                int w = StdIn.readInt();
                int length   = sca.length(v, w);
                int ancestor = sca.ancestor(v, w);
                StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
            }
        }
    }

    // Unit test made by me
    public static void manualUnitTest() throws IOException {
        // Basic tree test
        int numVertices = 5;// or whatever
        Digraph d1 = new Digraph(numVertices);
        d1.addEdge(2, 0); // add a bunch of these, to form some tree-like shape, e.g.:
        d1.addEdge(1, 0);
        d1.addEdge(4, 2);
        d1.addEdge(3, 1);

        ShortestCommonAncestor sca = new ShortestCommonAncestor(d1);
        int a = 0;
        int b = 1;
        int c = 2;
        int d = 3;
        int e = 4;

        StdOut.println("result: " + sca.length(c, c));



        Bag<Integer> b1 = new Bag<Integer>();
        Bag<Integer> b2 = new Bag<Integer>();

        b1.add(b);
        b1.add(d);
        b2.add(c);
        b2.add(e);

        StdOut.println("Testing Case: 2");
        StdOut.println("length: " + sca.length(b1, b2));
        StdOut.println("ancestor: " + sca.ancestor(b1, b2));

    }
}
