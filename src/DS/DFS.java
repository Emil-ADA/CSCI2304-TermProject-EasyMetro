package DS;


import java.util.Iterator;

import DS.Basic.ArrayList;
import DS.Basic.LinearProbingHashST;
import DS.Basic.Stack;

/**
 * Depth First Search Algorithm
 * 
 * @author Sadig Akhund
 *
 */
public class DFS {
    /** vertices in current path */
    private boolean[] onPath;

    /** the current path */
    private Stack<String> path;

    /** collector, for returning at the end */
    private ArrayList<Stack<String>> paths;

    /** The hash containing the indices of the stations */
    private LinearProbingHashST<String, Integer> hash;

    /** number of simple path */
    private int numberOfPaths;

    public ArrayList<Stack<String>> getAllPaths() {
	return paths;
    }

    /**
     * Shows all simple paths from s to t - use DFS
     * 
     * @param G
     *                 Graph
     * @param hash
     *                 Linear Probing Hash
     * @param s
     *                 Start
     * @param t
     *                 End
     */
    public DFS(Graph G, LinearProbingHashST<String, Integer> hash, String s, String t) {
	this.hash = hash;
	Graph copy = G.clone();
	Iterator<Edge> iter = copy.edges().iterator();

	/**
	 * This while loop is kind of a lazy programming, thats why deprecated. It gets
	 * all the edges from old graph and adds it to new plus reversed version, so
	 * that directed graph becomes sort of undirected.
	 */
	while (iter.hasNext()) {
	    Edge e = iter.next();
	    copy.addEdge(new Edge(hash.get(e.w_name), hash.get(e.v_name), e.weights).setVertexNames(e.w_name, e.v_name)
		    .setLine(e.getLine()));
	}

	onPath = new boolean[copy.V()];
	path = new Stack<String>();
	paths = new ArrayList<>();
	dfs(copy, s, t);
    }

    // use DFS
    private void dfs(Graph G, String a, String b) {
	int v = hash.get(a);
	int t = hash.get(b);

	// add v to current path
	path.push(a);
	onPath[v] = true;

	// found path from s to t
	if (v == t) {
	    processCurrentPath();
	    numberOfPaths++;
	}

	// consider all neighbors that would continue path with repeating a node
	else {
	    for (Edge edge : G.adj(v)) {
		int w = hash.get(edge.w_name);
		if (!onPath[w])
		    dfs(G, edge.w_name, b);
	    }
	}

	// done exploring from v, so remove from path
	path.pop();
	onPath[v] = false;
    }

    // this implementation just prints the path to standard output
    private void processCurrentPath() {
	Stack<String> reverse = new Stack<String>();
	for (String v : path)
	    reverse.push(v);

	paths.add(reverse);

    }

    // return number of simple paths between s and t
    public int numberOfPaths() {
	return numberOfPaths;
    }

    // test
    public static void main(String[] args) {
	Graph G = new Graph(7);
	// G.addEdge(new Edge(0, 1, 1).setVertexNames("A0", "A1").setLine("AA"));
	// G.addEdge(new Edge(0, 2, 1).setVertexNames("A0", "A2").setLine("AA"));
	// G.addEdge(new Edge(2, 3, 1).setVertexNames("A2", "A3").setLine("AA"));
	// G.addEdge(new Edge(3, 4, 1).setVertexNames("A3", "A4").setLine("AA"));
	// G.addEdge(new Edge(2, 5, 1).setVertexNames("A2", "A5").setLine("AA"));
	// G.addEdge(new Edge(1, 5, 1).setVertexNames("A1", "A5").setLine("AA"));
	// G.addEdge(new Edge(5, 4, 1).setVertexNames("A5", "A4").setLine("AA"));
	// G.addEdge(new Edge(3, 6, 1).setVertexNames("A3", "A6").setLine("AA"));
	// G.addEdge(new Edge(4, 6, 1).setVertexNames("A4", "A6").setLine("AA"));

	G.addEdge(new Edge(0, 1, 1).setVertexNames("A0", "A1").setLine("AA"));
	G.addEdge(new Edge(1, 2, 1).setVertexNames("A0", "A1").setLine("AA"));
	G.addEdge(new Edge(2, 3, 1).setVertexNames("A0", "A1").setLine("AA"));
	G.addEdge(new Edge(3, 4, 1).setVertexNames("A0", "A1").setLine("AA"));
	G.addEdge(new Edge(4, 5, 1).setVertexNames("A0", "A1").setLine("AA"));

	// StdOut.println(G);

	LinearProbingHashST<String, Integer> hash = new LinearProbingHashST<>();
	for (int i = 0; i < 7; i++)
	    hash.put("A" + i, i);
	System.out.println();
	System.out.println("all simple paths between 0 and 6:");
	DFS allpaths1 = new DFS(G, hash, "A0", "A6");
	System.out.println(allpaths1.paths.toString());
	System.out.println("# paths = " + allpaths1.numberOfPaths());
	System.out.println(allpaths1.getAllPaths().get(0).peek());
	System.out.println();
	System.out.println("all simple paths between 1 and 5:");
	DFS allpaths2 = new DFS(G, hash, "A0", "A1");
	System.out.println("# paths = " + allpaths2.numberOfPaths());
	System.out.println(allpaths2.paths.toString());

    }
}