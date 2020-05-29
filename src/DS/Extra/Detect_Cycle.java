package DS.Extra;

import DS.Graph;
import DS.Edge;
import DS.Basic.Stack;

/**
 * How to detect cycles in graph
 * 
 * @author Sadig Akhund
 *
 */
public class Detect_Cycle {
    private boolean[] marked;
    private int[] edgeTo;
    private Stack<Integer> cycle;

    /**
     * Determines whether the undirected graph {@code G} has a cycle and, if so,
     * finds such a cycle.
     *
     * @param G
     *              the undirected graph
     */
    public Detect_Cycle(Graph G) {
	if (hasSelfLoop(G))
	    return;
	if (hasParallelEdges(G))
	    return;
	marked = new boolean[G.V()];
	edgeTo = new int[G.V()];
	for (int v = 0; v < G.V(); v++)
	    if (!marked[v])
		dfs(G, -1, v);
    }

    // does this graph have a self loop?
    // side effect: initialize cycle to be self loop
    private boolean hasSelfLoop(Graph G) {
	for (int v = 0; v < G.V(); v++) {
	    for (Edge e : G.adj(v)) {
		int w = e.getW();
		if (v == w) {
		    cycle = new Stack<Integer>();
		    cycle.push(v);
		    cycle.push(v);
		    return true;
		}
	    }
	}
	return false;
    }

    // does this graph have two parallel edges?
    // side effect: initialize cycle to be two parallel edges
    private boolean hasParallelEdges(Graph G) {
	marked = new boolean[G.V()];

	for (int v = 0; v < G.V(); v++) {

	    // check for parallel edges incident to v
	    for (Edge e : G.adj(v)) {
		int w = e.getW();
		if (marked[w]) {
		    cycle = new Stack<Integer>();
		    cycle.push(v);
		    cycle.push(w);
		    cycle.push(v);
		    return true;
		}
		marked[w] = true;
	    }

	    // reset so marked[v] = false for all v
	    for (Edge e : G.adj(v)) {
		int w = e.getW();
		marked[w] = false;
	    }
	}
	return false;
    }

    /**
     * Returns true if the graph {@code G} has a cycle.
     *
     * @return {@code true} if the graph has a cycle; {@code false} otherwise
     */
    public boolean hasCycle() {
	return cycle != null;
    }

    /**
     * Returns a cycle in the graph {@code G}.
     * 
     * @return a cycle if the graph {@code G} has a cycle, and {@code null}
     *         otherwise
     */
    public Iterable<Integer> cycle() {
	return cycle;
    }

    private void dfs(Graph G, int u, int v) {
	marked[v] = true;
	for (Edge e : G.adj(v)) {
	    int w = e.getW();

	    // short circuit if cycle already found
	    if (cycle != null)
		return;

	    if (!marked[w]) {
		edgeTo[w] = v;
		dfs(G, v, w);
	    }

	    // check for cycle (but disregard reverse of edge leading to v)
	    else if (w != u) {
		cycle = new Stack<Integer>();
		for (int x = v; x != w; x = edgeTo[x]) {
		    cycle.push(x);
		}
		cycle.push(w);
		cycle.push(v);
	    }
	}
    }

    public static void main(String[] args) {

    }

}