package DS;

import Dependencies.StdOut;

/******************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *  Dependencies: StdOut.java
 *
 *  Immutable weighted directed edge.
 *
 ******************************************************************************/
/**
 * The {@code DirectedEdge} class represents a weighted edge in an
 * {@link EdgeWeightedDigraph}. Each edge consists of two integers (naming the
 * two vertices) and a real-value weight. The data type provides methods for
 * accessing the two endpoints of the directed edge and the weight.
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */

public class DirectedEdge extends Edge {

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code weight}.
     * 
     * @param v
     *                   the tail vertex
     * @param w
     *                   the head vertex
     * @param weight
     *                   the weight of the directed edge
     * @throws IllegalArgumentException
     *                                      if either {@code v} or {@code w} is a
     *                                      negative integer
     * @throws IllegalArgumentException
     *                                      if {@code weight} is {@code NaN}
     */
    public DirectedEdge(String v_name, String w_name, int v, int w, double weight) {
	super(v_name, w_name, v, w, weight);
    }

    /**
     * Returns the tail vertex of the directed edge.
     * 
     * @return the tail vertex of the directed edge
     */
    public int from() {
	return v;
    }

    /**
     * Returns the head vertex of the directed edge.
     * 
     * @return the head vertex of the directed edge
     */
    public int to() {
	return w;
    }

    /**
     * Returns the weight of the directed edge.
     * 
     * @return the weight of the directed edge
     */
    public double weight() {
	return weight;
    }

    /**
     * Returns a string representation of the directed edge.
     * 
     * @return a string representation of the directed edge
     */
    public String toString() {
	// + ":" + v + "->" + w + " "
	return v_name + "->" + w_name + String.format("%5.2f", weight);
    }

    public static void main(String[] args) {
//	DirectedEdge e = new DirectedEdge("Istanbul", 12, 34, 5.67);
//	StdOut.println(e);
    }
}