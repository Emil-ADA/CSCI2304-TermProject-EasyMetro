package DS.Extra;

import java.io.File;

import DS.Graph;
import DS.UnionFind;
import DS.Basic.Edge;
import DS.Basic.IndexMinPQ;
import DS.Basic.Queue;
import Dependencies.In;
import Dependencies.StdOut;

public class Prim {
    private static final double FLOATING_POINT_EPSILON = 1E-12;

    private Edge[] edgeTo; // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo; // distTo[v] = weight of shortest such edge
    private boolean[] marked; // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ<Double> pq;
    int weight_index = 0;

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * 
     * @param G
     *              the edge-weighted graph
     */
    public Prim(Graph G, int w_in) {
	edgeTo = new Edge[G.V()];
	distTo = new double[G.V()];
	marked = new boolean[G.V()];
	weight_index = w_in;
	pq = new IndexMinPQ<Double>(G.V());
	for (int v = 0; v < G.V(); v++)
	    distTo[v] = Double.POSITIVE_INFINITY;

	for (int v = 0; v < G.V(); v++) // run from each vertex to find
	    if (!marked[v])
		prim(G, v, w_in); // minimum spanning forest

	// check optimality conditions
	assert check(G, w_in);
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(Graph G, int s, int w_in) {
	distTo[s] = 0.0;
	pq.insert(s, distTo[s]);
	while (!pq.isEmpty()) {
	    int v = pq.delMin();
	    scan(G, v, w_in);
	}
    }

    // scan vertex v
    private void scan(Graph G, int v, int w_in) {
	marked[v] = true;
	for (Edge e : G.adj(v)) {
	    int w = e.other(v);
	    if (marked[w])
		continue; // v-w is obsolete edge
	    if (e.weight(w_in) < distTo[w]) {
		distTo[w] = e.weight(w_in);
		edgeTo[w] = e;
		if (pq.contains(w))
		    pq.decreaseKey(w, distTo[w]);
		else
		    pq.insert(w, distTo[w]);
	    }
	}
    }

    /**
     * Returns the edges in a minimum spanning tree (or forest).
     * 
     * @return the edges in a minimum spanning tree (or forest) as an iterable of
     *         edges
     */
    public Iterable<Edge> edges() {
	Queue<Edge> mst = new Queue<Edge>();
	for (int v = 0; v < edgeTo.length; v++) {
	    Edge e = edgeTo[v];
	    if (e != null) {
		mst.enqueue(e);
	    }
	}
	return mst;
    }

    /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * 
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
    public double weight() {
	double weight = 0.0;
	for (Edge e : edges())
	    weight += e.weight(weight_index);
	return weight;
    }

    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(Graph G, int w_in) {

	// check weight
	double totalWeight = 0.0;
	for (Edge e : edges()) {
	    totalWeight += e.weight(w_in);
	}
	if (Math.abs(totalWeight - weight()) > FLOATING_POINT_EPSILON) {
	    System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", totalWeight, weight());
	    return false;
	}

	// check that it is acyclic
	UnionFind uf = new UnionFind(G.V());
	for (Edge e : edges()) {
	    int v = e.either(), w = e.other(v);
	    if (uf.find(v) == uf.find(w)) {
		System.err.println("Not a forest");
		return false;
	    }
	    uf.union(v, w);
	}

	// check that it is a spanning forest
	for (Edge e : G.edges()) {
	    int v = e.either(), w = e.other(v);
	    if (uf.find(v) != uf.find(w)) {
		System.err.println("Not a spanning forest");
		return false;
	    }
	}

	// check that it is a minimal spanning forest (cut optimality conditions)
	for (Edge e : edges()) {

	    // all edges in MST except e
	    uf = new UnionFind(G.V());
	    for (Edge f : edges()) {
		int x = f.either(), y = f.other(x);
		if (f != e)
		    uf.union(x, y);
	    }

	    // check that e is min weight edge in crossing cut
	    for (Edge f : G.edges()) {
		int x = f.either(), y = f.other(x);
		if (uf.find(x) != uf.find(y)) {
		    if (f.weight(w_in) < e.weight(w_in)) {
			System.err.println("Edge " + f + " violates cut optimality conditions");
			return false;
		    }
		}
	    }

	}

	return true;
    }

    /**
     * Unit tests the {@code PrimMST} data type.
     *
     * @param args
     *                 the command-line arguments
     */
    public static void main(String[] args) {
	Graph G = new Graph(7);
	G.addEdge(new Edge(0, 1, 1).setVertexNames("A0", "A1").setLine("AA"));
	G.addEdge(new Edge(0, 2, 1).setVertexNames("A0", "A2").setLine("AA"));
	G.addEdge(new Edge(2, 3, 1).setVertexNames("A2", "A3").setLine("AA"));
	G.addEdge(new Edge(3, 4, 1).setVertexNames("A3", "A4").setLine("AA"));
	G.addEdge(new Edge(2, 5, 1).setVertexNames("A2", "A5").setLine("AA"));
	G.addEdge(new Edge(1, 5, 1).setVertexNames("A1", "A5").setLine("AA"));
	G.addEdge(new Edge(5, 4, 1).setVertexNames("A5", "A4").setLine("AA"));
	G.addEdge(new Edge(3, 6, 1).setVertexNames("A3", "A6").setLine("AA"));
	G.addEdge(new Edge(4, 6, 1).setVertexNames("A4", "A6").setLine("AA"));
	Prim mst = new Prim(G, 0);
	for (Edge e : mst.edges()) {
	    StdOut.println(e);
	}
	StdOut.printf("%.5f\n", mst.weight());
    }

}