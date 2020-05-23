package DS;

import DS.Basic.IndexMinPQ;
import DS.Basic.Stack;

public class Dijkstra {
    /** distTo[v] = distance of shortest s->v path */
    private double[] distTo;
    /** edgeTo[v] = last edge on shortest s->v path */
    private Edge[] edgeTo;
    /** priority queue of vertices */
    private IndexMinPQ<Double> pq;

    /** The index showing which weight must be checked */
    private int weight_index;

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every
     * other vertex in the edge-weighted graph {@code G}.
     *
     * @param G
     *                   the edge-weighted digraph
     * @param s
     *                   the source vertex
     * @param weight
     *                   index showing which weight must be checked
     * 
     * @throws IllegalArgumentException
     *                                      if an edge weight is negative
     * @throws IllegalArgumentException
     *                                      unless {@code 0 <= s < V}
     */
    public Dijkstra(Graph G, int s, int weight) {
	for (Edge e : G.edges()) {
	    if (e.getWeightAt(weight) < 0)
		throw new IllegalArgumentException("edge " + e + " has negative weight");
	}

	weight_index = weight;
	distTo = new double[G.V()];
	edgeTo = new Edge[G.V()];

	validateVertex(s);

	for (int v = 0; v < G.V(); v++)
	    distTo[v] = Double.POSITIVE_INFINITY;
	distTo[s] = 0.0;

	// relax vertices in order of distance from s
	pq = new IndexMinPQ<Double>(G.V());
	pq.insert(s, distTo[s]);
	while (!pq.isEmpty()) {
	    int v = pq.delMin();
	    for (Edge e : G.adj(v))
		relax(e, v);
	}

	// check optimality conditions
	assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e, int v) {
	int w = e.other(v);
	if (distTo[w] > distTo[v] + e.getWeightAt(weight_index)) {
	    distTo[w] = distTo[v] + e.getWeightAt(weight_index);
	    edgeTo[w] = e;
	    if (pq.contains(w))
		pq.decreaseKey(w, distTo[w]);
	    else
		pq.insert(w, distTo[w]);
	}
    }

    /**
     * Returns the length of a shortest path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param v
     *              the destination vertex
     * @return the length of a shortest path between the source vertex {@code s} and
     *         the vertex {@code v}; {@code Double.POSITIVE_INFINITY} if no such
     *         path
     * @throws IllegalArgumentException
     *                                      unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
	validateVertex(v);
	return distTo[v];
    }

    /**
     * Returns true if there is a path between the source vertex {@code s} and
     * vertex {@code v}.
     *
     * @param v
     *              the destination vertex
     * @return {@code true} if there is a path between the source vertex {@code s}
     *         to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException
     *                                      unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
	validateVertex(v);
	return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path between the source vertex {@code s} and vertex
     * {@code v}.
     *
     * @param v
     *              the destination vertex
     * @return a shortest path between the source vertex {@code s} and vertex
     *         {@code v}; {@code null} if no such path
     * @throws IllegalArgumentException
     *                                      unless {@code 0 <= v < V}
     */
    public Iterable<Edge> pathTo(int v) {
	validateVertex(v);
	if (!hasPathTo(v))
	    return null;
	Stack<Edge> path = new Stack<Edge>();
	int x = v;
	for (Edge e = edgeTo[v]; e != null; e = edgeTo[x]) {
	    path.push(e);
	    x = e.other(x);
	}
	return path;
    }

    /*-------------------------------------------------------------------*/
    /*----------------credit to: Princeton University-------------------*/
    /*-----------------------------------------------------------------*/
    
    // check optimality conditions:
    // (i) for all edges e = v-w: distTo[w] <= distTo[v] + e.weight()
    // (ii) for all edge e = v-w on the SPT: distTo[w] == distTo[v] + e.weight()
    private boolean check(Graph G, int s) {

	// check that edge weights are nonnegative
	for (Edge e : G.edges()) {
	    if (e.getWeightAt(weight_index) < 0) {
		System.err.println("negative edge weight detected");
		return false;
	    }
	}

	// check that distTo[v] and edgeTo[v] are consistent
	if (distTo[s] != 0.0 || edgeTo[s] != null) {
	    System.err.println("distTo[s] and edgeTo[s] inconsistent");
	    return false;
	}
	for (int v = 0; v < G.V(); v++) {
	    if (v == s)
		continue;
	    if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
		System.err.println("distTo[] and edgeTo[] inconsistent");
		return false;
	    }
	}

	// check that all edges e = v-w satisfy distTo[w] <= distTo[v] + e.weight()
	for (int v = 0; v < G.V(); v++) {
	    for (Edge e : G.adj(v)) {
		int w = e.other(v);
		if (distTo[v] + e.getWeightAt(weight_index) < distTo[w]) {
		    System.err.println("edge " + e + " not relaxed");
		    return false;
		}
	    }
	}

	// check that all edges e = v-w on SPT satisfy distTo[w] == distTo[v] +
	// e.weight()
	for (int w = 0; w < G.V(); w++) {
	    if (edgeTo[w] == null)
		continue;
	    Edge e = edgeTo[w];
	    if (w != e.either() && w != e.other(e.either()))
		return false;
	    int v = e.other(w);
	    if (distTo[v] + e.getWeightAt(weight_index) != distTo[w]) {
		System.err.println("edge " + e + " on shortest path not tight");
		return false;
	    }
	}
	return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
	int V = distTo.length;
	if (v < 0 || v >= V)
	    throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /*-------------------------------------------------------------------*/
    /*------------------------------------------------------------------*/
    /*-----------------------------------------------------------------*/
    
    /**
     * Unit tests the {@code DijkstraUndirectedSP} data type.
     *
     * @param args
     *                 the command-line arguments
     */
    public static void main(String[] args) {
	Graph G = new Graph(7);
	String V[] = { "Bagcilar", "Gunestepe", "Yavuz Selim", "Gungoren", "Sirkeci", "Tophane", "Kabatas" };
	G.addEdge(new Edge(0, 1, 0.2, 1));
	G.addEdge(new Edge(1, 2, 0.5, 1));
	G.addEdge(new Edge(1, 3, 0.2, 1));
	G.addEdge(new Edge(1, 5, 0.9, 1));
	G.addEdge(new Edge(3, 5, 0.4, 1));
	G.addEdge(new Edge(2, 4, 0.3, 1));
	G.addEdge(new Edge(4, 5, 0.2, 1));
	G.addEdge(new Edge(4, 6, 0.1, 1));

	int s = 5;
	System.out.println("Searching for " + V[s] + "...");

	// compute shortest paths
	Dijkstra sp = new Dijkstra(G, s, 0);

	// print shortest path
	for (int t = 0; t < G.V(); t++) {
	    if (sp.hasPathTo(t)) {
		// System.out.print(V[s] + " to " + V[t] + sp.distTo(v));
		System.out.printf("%s to %s (%.2f) ", V[s], V[t], sp.distTo(t));
		// for (Edge e : sp.pathTo(t)) {
		// StdOut.print(e + " ");
		// }
		System.out.println();
	    } else {
		System.out.printf("%d to %d         no path\n", s, t);
	    }
	}
    }

}