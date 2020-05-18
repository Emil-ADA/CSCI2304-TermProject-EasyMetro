package DS;

// MULTI-WEIGHTED EDGE
public class MWEdge implements Comparable<MWEdge> {
    /** The maximum length of the name that ever added */
    public static int MAX_NAME_LENGTH = 1;
    /** Vertex v */
    final int v;
    /** Vertex w */
    final int w;
    /** The name of Vertex v */
    public String v_name;
    /** The name of Vertex w */
    public String w_name;
    /** The name of line which this edge is on */
    public String line;
    /** An array that makes it multi-weighted Edge */
    public final double[] weights;

    public MWEdge(int v, int w, double... weights) {
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

    public int compareTo(MWEdge that, int i) {
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
	sb.append(v_name + " - " + w_name);
	return sb.toString();
    }

    @Override
    public int compareTo(MWEdge arg0) {
	return compareTo(arg0, 0); // Default: Compares time
    }

    public static void main(String[] args) {
	MWEdge e = new MWEdge(0, 1, 1, 2, 3, 4, 5, 6, 7);
	MWEdge e2 = new MWEdge(0, 1, 1, 2, 3, 4, 5);
	e.setVertexNames("aaaaaaa", "a");
	e2.setVertexNames("b", "bb");
	System.out.println(e);
	System.out.println(e2);
    }

    /** Setter for line name */
    public MWEdge setLine(String line) {
	this.line = line;
	return this;
    }

    /** Getter for line name */
    public String getLine() {
	return line;
    }

    /** Setter for vertex names */
    public MWEdge setVertexNames(String vertex_v, String vertex_w) {
	MAX_NAME_LENGTH = Math.max(MAX_NAME_LENGTH, (vertex_v + " - " + vertex_w).length() + 1); // +1 is important
	v_name = vertex_v;
	w_name = vertex_w;
	return this;
    }

    /**
     * Getter for weight
     * 
     * @param in
     *               an index of edge's current weight
     * @return in'th weight
     */
    public double getWeightAt(int in) {
	return weights[in];
    }

    /**
     * @return the v
     */
    public int getV() {
        return v;
    }

    /**
     * @return the w
     */
    public int getW() {
        return w;
    }
    
}