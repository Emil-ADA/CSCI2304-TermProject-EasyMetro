package DS;

import java.util.ArrayList;
import java.util.Iterator;

import Dependencies.StdOut;

public class DepthFirstSearch {
    private boolean[] onPath; // vertices in current path
    private Stack<String> path; // the current path
    private ArrayList<Stack<String>> paths;
    LinearProbingHashST<String, Integer> hash;
    private int numberOfPaths; // number of simple path

    public ArrayList<Stack<String>> getAllPaths() {
	return paths;
    }

    // show all simple paths from s to t - use DFS
    public DepthFirstSearch(Graph G, LinearProbingHashST<String, Integer> hash, String s, String t) {
	this.hash = hash;
	onPath = new boolean[G.V()];
	path = new Stack<String>();
	paths = new ArrayList<>();
	dfs(G, s, t);
    }

    // use DFS
    private void dfs(Graph G, String a, String b) {
	int s = hash.get(a);
	int t = hash.get(b);
	onPath = new boolean[G.V()];

	validateVertex(s);

	// to be able to iterate over each adjacency list, keeping track of which
	// vertex in each adjacency list needs to be explored next
	Iterator<Edge>[] adj = (Iterator<Edge>[]) new Iterator[G.V()];
	for (int v = 0; v < G.V(); v++)
	    adj[v] = G.adj(v).iterator();

	// depth-first search using an explicit stack
	Stack<Integer> stack = new Stack<Integer>();

	onPath[s] = true;
	stack.push(s);
	while (!stack.isEmpty()) {
	    int v = stack.peek();
	    if (adj[v].hasNext()) {
		int w = adj[v].next().w;
		// StdOut.printf("check %d\n", w);
		if (!onPath[w]) {
		    // discovered vertex w for the first time
		    onPath[w] = true;
		    // edgeTo[w] = v;
		    stack.push(w);
		    // StdOut.printf("dfs(%d)\n", w);
		}
	    } else {
		// StdOut.printf("%d done\n", v);
		stack.pop();
	    }
	}

	///////////////////////////////////////////////////////

	// add v to current path
	// path.push(a);
	// onPath[v] = true;
	//
	// // found path from s to t
	// if (v == t) {
	// processCurrentPath();
	// numberOfPaths++;
	// }
	//
	// // consider all neighbors that would continue path with repeating a node
	// else {
	// for (Edge edge : G.adj(v)) {
	// int w = edge.w;
	// if (!onPath[w])
	// dfs(G, edge.w_name, b);
	// }
	// }
	//
	// // done exploring from v, so remove from path
	// path.pop();
	// onPath[v] = false;
    }

    /**
     * Is vertex {@code v} connected to the source vertex {@code s}?
     * 
     * @param v
     *              the vertex
     * @return {@code true} if vertex {@code v} is connected to the source vertex
     *         {@code s}, and {@code false} otherwise
     * @throws IllegalArgumentException
     *                                      unless {@code 0 <= v < V}
     */
    public boolean marked(int v) {
	validateVertex(v);
	return onPath[v];
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
	int V = onPath.length;
	if (v < 0 || v >= V)
	    throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
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

    // test client
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
	// StdOut.println(G);
	Prim prim = new Prim(G, 0);
	KruskalMST kruskal = new KruskalMST(G);
//	Iterator<Edge> iter = prim.edges().iterator();

//	Iterator<Edge> iter = kruskal.edges().iterator();
	while(iter.hasNext())
	System.out.println(iter.next());

	LinearProbingHashST<String, Integer> hash = new LinearProbingHashST<>();
	for (int i = 0; i < 7; i++)
	    hash.put("A" + i, i);
	// StdOut.println();
	// StdOut.println("all simple paths between 0 and 6:");
	DepthFirstSearch allpaths1 = new DepthFirstSearch(G, hash, "A0", "A6");
	// StdOut.println(allpaths1.paths.toString());
	// StdOut.println("# paths = " + allpaths1.numberOfPaths());
	allpaths1.dfs(G, "A0", "A6");
	// StdOut.println();
	// StdOut.println("all simple paths between 1 and 5:");
	DepthFirstSearch allpaths2 = new DepthFirstSearch(G, hash, "A0", "A5");
	// StdOut.println("# paths = " + allpaths2.numberOfPaths());
	//
	// StdOut.println(allpaths2.paths.toString());
	//
	// StdOut.println(G);
    }
}