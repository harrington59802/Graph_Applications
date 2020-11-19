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

import edu.princeton.cs.algs4.Stopwatch;

import java.io.IOException;

public class Outcast {
   WordNet wordnet;
   // constructor takes a WordNet object
   public Outcast(WordNet wordnet) {    
       this.wordnet = wordnet;
   }
   
   // given an array of WordNet nouns, return an outcast
   public String outcast(String[] nouns) throws IOException {
       int maxDistance = 0;
       int outcast_id = -999;

       for (int i = 0; i < nouns.length; i++) {
           int relDistance = 0;
           int totalDistance = 0;

           for (int j = 0; j < nouns.length; j++) {
               if (nouns[i].equals(nouns[j])) continue;
               relDistance = wordnet.distance(nouns[i], nouns[j]);
               totalDistance += relDistance;

               if (totalDistance > maxDistance) {
                   outcast_id = i;
                   maxDistance = totalDistance;
               }
           }
       }
       return nouns[outcast_id];
   }
   
   // Unit Test client with Stopwatch()
   public static void main(String[] args) throws IOException { //throw because WordNet throws
       WordNet wordnet = new WordNet(args[0], args[1]);
       Outcast outcast = new Outcast(wordnet);
       for (int t = 2; t < args.length; t++) {
           In in = new In(args[t]);
           String[] nouns = in.readAllStrings();
           Stopwatch stopwatch = new Stopwatch();
           StdOut.println(args[t] + ": " + outcast.outcast(nouns));
           System.out.println(stopwatch.elapsedTime());

       }
   }
}
