package DS.Extra;

import DS.Graph;
import DS.MWEdge;
import DS.UnionFind;
import DS.Basic.MinPQ;
import DS.Basic.Queue;

public class KruskalMST {
    private static final double FLOATING_POINT_EPSILON = 1E-12;

    private double weight; // weight of MST
    private Queue<MWEdge> mst = new Queue<MWEdge>(); // edges in MST

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * 
     * @param G
     *              the edge-weighted graph
     */
    public KruskalMST(Graph G, int w_in) {
	// more efficient to build heap by passing array of edges
	MinPQ<MWEdge> pq = new MinPQ<MWEdge>();
	for (MWEdge e : G.edges()) {
	    pq.insert(e);
	}

	// run greedy algorithm
	UnionFind uf = new UnionFind(G.V());
	while (!pq.isEmpty() && mst.size() < G.V() - 1) {
	    MWEdge e = pq.delMin();
	    int v = e.either();
	    int w = e.other(v);
	    if (uf.find(v) != uf.find(w)) { // v-w does not create a cycle
		uf.union(v, w); // merge v and w components
		mst.enqueue(e); // add edge e to mst
		weight += e.getWeightAt(w_in);
	    }
	}

	// check optimality conditions
	assert check(G, w_in);
    }

    /**
     * Returns the edges in a minimum spanning tree (or forest).
     * 
     * @return the edges in a minimum spanning tree (or forest) as an iterable of
     *         edges
     */
    public Iterable<MWEdge> edges() {
	return mst;
    }

    /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * 
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
    public double weight() {
	return weight;
    }

    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(Graph G, int w_in) {

	// check total weight
	double total = 0.0;
	for (MWEdge e : edges()) {
	    total += e.getWeightAt(w_in);
	}
	if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
	    System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
	    return false;
	}

	// check that it is acyclic
	UnionFind uf = new UnionFind(G.V());
	for (MWEdge e : edges()) {
	    int v = e.either(), w = e.other(v);
	    if (uf.find(v) == uf.find(w)) {
		System.err.println("Not a forest");
		return false;
	    }
	    uf.union(v, w);
	}

	// check that it is a spanning forest
	for (MWEdge e : G.edges()) {
	    int v = e.either(), w = e.other(v);
	    if (uf.find(v) != uf.find(w)) {
		System.err.println("Not a spanning forest");
		return false;
	    }
	}

	// check that it is a minimal spanning forest (cut optimality conditions)
	for (MWEdge e : edges()) {

	    // all edges in MST except e
	    uf = new UnionFind(G.V());
	    for (MWEdge f : mst) {
		int x = f.either(), y = f.other(x);
		if (f != e)
		    uf.union(x, y);
	    }

	    // check that e is min weight edge in crossing cut
	    for (MWEdge f : G.edges()) {
		int x = f.either(), y = f.other(x);
		if (uf.find(x) != uf.find(y)) {
		    if (f.getWeightAt(w_in) < e.getWeightAt(w_in)) {
			System.err.println("Edge " + f + " violates cut optimality conditions");
			return false;
		    }
		}
	    }

	}

	return true;
    }

    /**
     * Unit tests the {@code KruskalMST} data type.
     *
     * @param args
     *                 the command-line arguments
     */
    public static void main(String[] args) {
	
    }

}