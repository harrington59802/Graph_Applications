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
import edu.princeton.cs.algs4.LinearProbingHashST;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordNet {
    private Digraph wordnet;
    private LinearProbingHashST<Integer, Bag<String>> synsetsByID;
    private LinearProbingHashST<String, Bag<Integer>> idsByNoun;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) throws IOException /* "throw" required for FileReader*/ {
       if (synsets == null || hypernyms == null) throw new NullPointerException();
       synsetsByID = new LinearProbingHashST<>();
       idsByNoun = new LinearProbingHashST<>();

       // Read in all synsets
       getSynsets(synsets);

       // Read in all hypernyms and create digraph
       getHypernyms(hypernyms);

   }

   // all WordNet nouns
   public Iterable<String> nouns() {
        return idsByNoun.keys();
   }


   // is the word a WordNet noun?
   public boolean isNoun(String word) {
       return idsByNoun.contains(word);
   }


   // a synset (second field of synsets.txt) that is a shortest common ancestor
   // of noun1 and noun2 (defined below)
   public String sca(String noun1, String noun2) throws IOException {
       if (noun1 == null || noun2 == null) throw new NullPointerException();
       if (!idsByNoun.contains(noun1) || !idsByNoun.contains(noun2)) throw new IllegalArgumentException();
       ShortestCommonAncestor SCA = new ShortestCommonAncestor(wordnet);
       Bag<Integer> one = idsByNoun.get(noun1);
       Bag<Integer> two = idsByNoun.get(noun2);

       int shortestCommonAncesotr = SCA.ancestor(one, two);

       return String.join("", synsetsByID.get(shortestCommonAncesotr));
   }

   // distance between noun1 and noun2 (defined below)
   public int distance(String noun1, String noun2) throws IOException {
       if (noun1 == null || noun2 == null) throw new NullPointerException();
       if (!idsByNoun.contains(noun1) || !idsByNoun.contains(noun2)) throw new IllegalArgumentException();
       ShortestCommonAncestor SCA = new ShortestCommonAncestor(wordnet);
       Bag<Integer> one = idsByNoun.get(noun1);
       Bag<Integer> two = idsByNoun.get(noun2);

       return SCA.length(one, two);
   }


    public void getSynsets(String synsets)  throws IOException{ /* "throw" required for FileReader*/
        // Read in all synsets (and do something with them)
        int lineCounter = 0;
        BufferedReader input = new BufferedReader(new FileReader(synsets));
        String line = input.readLine();
        while (line != null) {
            String parts[] = line.split(",");
            int synId = Integer.parseInt(parts[0]);
            String synStr = parts[1];
            String[] synset = synStr.split(" ");

            // put synset elements in bag
            Bag<String> synBag = new Bag<String>();
            for (int i = 0; i < synset.length; i++) {
                synBag.add(synset[i]);
            }
            // associate bag of sysnset elements with synID
            synsetsByID.put(synId, synBag);

            // get ST of nouns and Bag<Ids>
            // one noun may have many IDs
            // need to check if noun key entry already exists
            // and if it does, modify to include current id
            Bag<Integer> currIDbag = new Bag<Integer>();
            currIDbag.add(synId);
            for (String string : synset) {
                if (!idsByNoun.contains(string)) {
                    idsByNoun.put(string, currIDbag);
                }
                else if (idsByNoun.contains(string)) {
                    Bag<Integer> existingIDbag = idsByNoun.get(string);
                    existingIDbag.add(synId);
                    idsByNoun.put(string, existingIDbag);
                }
                else {
                    System.out.println("Something went wrong initializing idsByNoun");
                }
            }
                // Read next line and keep track of number of nouns for digraph initialization
            lineCounter++;
            line = input.readLine();
        }

        // initialize digraph to correct size
        wordnet = new Digraph(lineCounter);

        input.close();
    }

    public void getHypernyms(String hypernyms)  throws IOException{ /* "throw" required for FileReader*/
        // Read in all hypernyms
        BufferedReader input = new BufferedReader(new FileReader(hypernyms));
        String line = input.readLine();
        while (line != null) {
            String parts[] = line.split(",");
            int hypID = Integer.parseInt(parts[0]);
            // create digraph relating ids
                for (int i = 1; i < parts.length; i++) {
                    wordnet.addEdge(hypID, Integer.parseInt(parts[i]));
                }
            line = input.readLine();
        }
        input.close();
    }


    // do unit testing of this class
    public static void main(String[] args) throws IOException { //"throw" because the constructor throws.
        WordNet wnet = new WordNet("synsets.txt", "hypernyms.txt");
        // how to test
        int count = 0;
        for (String s : wnet.nouns()) {
            count++;
        }
        System.out.println(count);

        System.out.println(wnet.isNoun("dog"));
        System.out.println(wnet.isNoun("zdgewq"));
        System.out.println(wnet.isNoun("worm"));

    }
    }
