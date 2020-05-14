package DS;

// MULTI-WEIGHTED EDGE
public class Edge implements Comparable<Edge> {
    public static int MAX_NAME_LENGTH = 1;
    final int v;
    final int w;
    public String v_name;
    public String w_name;
    public String line;
    public final double[] weights;

    public Edge(int v, int w, double... weights) {
	if (v < 0)
	    throw new IllegalArgumentException("vertex index must be a nonnegative integer");
	if (w < 0)
	    throw new IllegalArgumentException("vertex index must be a nonnegative integer");

	for (double weight : weights) {
	    if (Double.isNaN(weight))
		throw new IllegalArgumentException("Weight is NaN");
	}
	this.v = v;
	this.w = w;
	this.weights = weights;
    }

    public Edge setLine(String line) {
	this.line = line;
	return this;
    }

    public String getLine() {
	return line;
    }

    public Edge setVertexNames(String vertex_v, String vertex_w) {
	MAX_NAME_LENGTH = Math.max(MAX_NAME_LENGTH, stringify(vertex_v, vertex_w).length() + 1); // +1 is important
	v_name = vertex_v;
	w_name = vertex_w;
	return this;
    }

    public String stringify(String a, String b) {
	return a + " - " + b;
    }

    public double weight(int in) {
	return weights[in];
    }

    /**
     * Returns either endpoint of this edge.
     *
     * @return either endpoint of this edge
     */
    public int either() {
	return v;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex.
     *
     * @param vertex
     *                   one endpoint of this edge
     * @return the other endpoint of this edge
     * @throws IllegalArgumentException
     *                                      if the vertex is not one of the
     *                                      endpoints of this edge
     */
    public int other(int vertex) {
	if (vertex == v)
	    return w;
	else if (vertex == w)
	    return v;
	else
	    throw new IllegalArgumentException("Illegal endpoint");
    }

    public int compareTo(Edge that, int i) {
	if (this.weights.length != that.weights.length)
	    throw new IllegalArgumentException("Edges are not compatible");

	return Double.compare(this.weights[i], that.weights[i]);

    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
    public String toString() {
	StringBuilder sb = new StringBuilder();
	try {
	    sb.append(getLine().toUpperCase() + ":");
	} catch (NullPointerException e) {
	    System.err.println("Please set Edge-Line name");
	}
	sb.append(stringify(v_name, w_name));
	return sb.toString();
    }

    @Override
    public int compareTo(Edge arg0) {
	return compareTo(arg0, 0); // Default: Compares time
    }

    public static void main(String[] args) {
	Edge e = new Edge(0, 1, 1, 2, 3, 4, 5, 6, 7);
	Edge e2 = new Edge(0, 1, 1, 2, 3, 4, 5);
	e.setVertexNames("aaaaaaa", "a");
	e2.setVertexNames("b", "bb");
	System.out.println(e);
	System.out.println(e2);
    }

}