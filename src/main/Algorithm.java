package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JOptionPane;

import DS.DFS;
import DS.Dijkstra;
import DS.Graph;
import DS.Edge;
import DS.Basic.LinearProbingHashST;
import DS.Basic.Queue;
import DS.Basic.Stack;
import Utils.JUtil;

public class Algorithm {
    /**
     * An instance of <code>StringBuilder</code> that will contain the full details
     * of the route (each station one-by-one).
     */
    public static StringBuilder searchResultsExtended;

    public static final char SEPERATOR_CHAR = '▬';

    public static void main(String[] args) {
	File selectedFile = new File("C:\\Users\\sadig\\Desktop\\hey.txt");

	FileWriter fWriter;
	try {
	    fWriter = new FileWriter(selectedFile);
	    PrintWriter pWriter = new PrintWriter(fWriter);
	    pWriter.println("hey");
	    pWriter.close();
	} catch (IOException e) {
	}
    }

    public static boolean hasTranfer(String path, String transfer) {
	boolean retval = false;

	retval = (retval || (path.contains(transfer)));

	String arr[] = transfer.split("/");
	/**
	 * We need to consider, for ex.: <br>
	 * Bayrampasa-Maltepe/Topkapi-Ulubatli/Fetihkapi<br>
	 * AND <br>
	 * Fetihkapi/Topkapi-Ulubatli/Bayrampasa-Maltepe
	 */
	transfer = arr[2] + "/" + arr[1] + "/" + arr[0];
	retval = (retval || (path.contains(transfer)));
	return retval;
    }


    private static int PREVIOUS_INDEX = 0;

    public static Queue<Edge> searchMin(String from, String to, Graph graph,
	    LinearProbingHashST<String, Integer> hash) {
	DFS dfs = new DFS(graph, hash, from, to);
	ArrayList<Stack<String>> ALL_PATHS = dfs.getAllPaths();
	if (ALL_PATHS.size() == 0) {
	    return null;
	}
	int size = ALL_PATHS.size();
	int W[] = new int[size];

	for (int j = 0; j < size; j++) {
	    String min_path_vertices[] = ALL_PATHS.get(j).toString().split("/");
	    for (int i = 0; i < min_path_vertices.length - 1; i++) {
		Iterator<Edge> edges = graph.edges().iterator();
		String prevline = edges.next().getLine();
		while (edges.hasNext()) {
		    if (!prevline.equals(prevline = edges.next().getLine())) {
			W[j]++;
		    }
		}
		for (Edge e : graph.edges()) {
		    String name = e.w_name + e.v_name;
		    if (name.equals(min_path_vertices[i] + min_path_vertices[i + 1])
			    || name.equals(min_path_vertices[i + 1] + min_path_vertices[i])) {

		    }
		}
	    }
	}
	/** Indices of the minimum paths */
	ArrayList<Integer> mindecies = new ArrayList<Integer>();
	int min = Integer.MAX_VALUE;
	int ind = 0;
	mindecies.add(ind);
	for (int i = 0; i < size; i++) {

	    if (W[i] < min) {
		min = W[i];
		ind = i;
		mindecies.clear();
	    }

	    if (W[i] == min) {
		mindecies.add(i);
	    }

	}
	PREVIOUS_INDEX++;
	if (PREVIOUS_INDEX >= mindecies.size())
	    PREVIOUS_INDEX = 0;

	String min_path_vertices[] = ALL_PATHS.get(mindecies.get(PREVIOUS_INDEX)).toString().split("/");

	Queue<Edge> path = new Queue<>();
	for (int i = 0; i < min_path_vertices.length - 1; i++) {
	    for (Edge e : graph.edges()) {
		String name = e.w_name + e.v_name;
		if (name.equals(min_path_vertices[i] + min_path_vertices[i + 1])
			|| name.equals(min_path_vertices[i + 1] + min_path_vertices[i])) {
		    path.enqueue(e);
		}
	    }
	}

	return path;

    }

    /**
     * Method for displaying the path found by <code>searchMin(from, to)</code>
     * method. <br>
     * 
     * @param from
     *                 departure station
     * @param via
     *                 station to stop
     * @param to
     *                 destination station
     * @return Results in String form
     */
    public static StringBuilder displayMin(String from, String via, String to, Graph graph,
	    LinearProbingHashST<String, Integer> hash) {
	/*-------------------------------------------------------------------*/
	// I understand that this part is being repeated, but each case has a
	// minor difference that made it hard for me to short it out
	/*-------------------------------------------------------------------*/
	StringBuilder searchResults = new StringBuilder();
	searchResultsExtended = new StringBuilder();
	String str2Append = "";
	double time = 0;
	double distance = 0;
	/*-------------------------------------------------------------------*/
	/*----------------------------from→via-----------------------------*/
	/*-----------------------------------------------------------------*/

	/** if 'via' is missing, 'to' becomes 'via' */
	if (via == null) {
	    via = to;
	    to = null;
	}

	/** ALL SHORTEST PATHS FROM 'from' */
	Queue<Edge> pathTo = searchMin(from, via, graph, hash);

	/* Shortest path between 'from' and 'via */
	Edge e = null;
	int transfer = 0;
	int stations = 1; // Stations start at one, because oz mindiyin esseyi de saymaq lazimdir
	String line_changed = null;
	str2Append = "\nDeparture \t| ";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/* < DEPARTURE STATION > */
	e = pathTo.dequeue();
	str2Append = e + "\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	line_changed = e.getLine();
	stations++;
	time += e.getWeightAt(0);
	distance += e.getWeightAt(1);
	/* </DEPARTURE STATION > */

	while (!pathTo.isEmpty()) {
	    e = pathTo.dequeue();
	    time += e.getWeightAt(0);
	    distance += e.getWeightAt(1);
	    stations++;
	    str2Append = e + "\n";
	    searchResultsExtended.append('‣' + str2Append); // extended results saves all, while normal not
	    /* Display the only edge where the transfer has happened */
	    if (!e.getLine().equals(line_changed)) {
		transfer++;
		searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
		searchResultsExtended.replace(0, searchResultsExtended.toString().indexOf('‣' + str2Append),
			searchResultsExtended.toString() + "↳Transfer \t| " + line_changed + " → " + str2Append);
	    }
	    line_changed = e.getLine();

	}
	/*-------------------------------------------------------------------*/
	/*----------------------------via→to-----------------------------*/
	/*-----------------------------------------------------------------*/

	/* if via station has been given */
	if (to != null) {

	    pathTo = searchMin(via, to, graph, hash);

	    /* DEPARTURE STATION */
	    e = pathTo.dequeue();
	    stations++;
	    time += e.getWeightAt(0);
	    distance += e.getWeightAt(1);
	    str2Append = e + "\n";

	    /*
	     * Check if there is a transfer in between, a very particular case when the
	     * specified via station is the stop station
	     */
	    if (!line_changed.equals(e.getLine())) {
		transfer++;
		searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
	    }
	    str2Append = "Stop \t| " + e + "\n";
	    searchResults.append(str2Append);
	    searchResultsExtended.append(str2Append);
	    line_changed = e.getLine();

	    while (!pathTo.isEmpty()) {
		e = pathTo.dequeue();
		time += e.getWeightAt(0);
		distance += e.getWeightAt(1);
		stations++;
		str2Append = e + "\n";
		searchResultsExtended.append('‣' + str2Append);

		if (!e.getLine().equals(line_changed)) {
		    transfer++;
		    searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
		    searchResultsExtended.replace(0, searchResultsExtended.toString().indexOf('‣' + str2Append),
			    searchResultsExtended.toString() + "↳Transfer \t| " + line_changed + " → " + str2Append);
		}
		line_changed = e.getLine();
	    }

	}
	// append DESTINATION as well
	str2Append = "Destination \t| " + str2Append;
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	/*
	 * Adding travel info.
	 */
	str2Append = "\n" + JUtil.n_times_char(MainMenu.DISPLAY_PANE_LINE_LENGTH, SEPERATOR_CHAR);
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	try (Scanner sc = new Scanner(new File(MainMenu.DATA_REPO + MainMenu.CITY + "//fare.txt"))) {
	    String line = sc.nextLine();
	    /* Add total cost */
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    /**
	     * ↳Transfer + 1, because we pay when we enter the metro first time
	     */
	    str2Append = "Fare: ";
	    String cost = String.format("%.2f", Double.parseDouble(line) * (transfer + 1));
	    str2Append += cost;

	    /* Add currency */
	    line = sc.nextLine();
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    str2Append += " " + line;
	} catch (FileNotFoundException e1) {
	}

	/**
	 * This substring concationation is done only because, results are calculated
	 * only after traversing
	 */
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());
	/** Temporarily changing modes for displaying different parameters */
	int temp = MainMenu.MODE;

	/* Mode = 0, is for displaying time */
	MainMenu.MODE = 0;

	str2Append = "Lead Time: " + IOL.formatResult(time, MainMenu.MODE) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	/* Mode = 0, is for displaying distance */
	MainMenu.MODE = 1;

	str2Append = "Distance: " + IOL.formatResult(distance, MainMenu.MODE) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	MainMenu.MODE = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s)." + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	return searchResults;
    }

    @SuppressWarnings({ "deprecation" })
    public static StringBuilder search(String from, String via, String to, Graph graph,
	    LinearProbingHashST<String, Integer> hash) {
	/** Output variables */
	StringBuilder searchResults = new StringBuilder();
	searchResultsExtended = new StringBuilder();
	String str2Append = "";
	double time = 0;
	double distance = 0;
	/*-------------------------------------------------------------------*/
	/*----------------------------from→via-----------------------------*/
	/*-----------------------------------------------------------------*/

	/** if 'via' is missing, 'to' becomes 'via' */
	if (via == null) {
	    via = to;
	    to = null;
	}
	/** ALL SHORTEST PATHS FROM 'from' */
	Dijkstra first = new Dijkstra(graph, hash.get(from), MainMenu.MODE);
	/** Shortest path between 'from' and 'via */
	Iterable<Edge> allStationsBetween = first.pathTo(hash.get(via));
	if (allStationsBetween == null) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "There is no route.");
	    return searchResults;
	}
	Iterator<Edge> pathTo = allStationsBetween.iterator();

	Edge e = null;
	int transfer = 0;
	int stations = 1; // Stations start at one, because oz mindiyin esseyi de saymaq lazimdir
	String line_changed = null;
	str2Append = "\nDeparture \t| ";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/* DEPARTURE STATION */
	e = pathTo.next();
	str2Append = e + "\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	line_changed = e.getLine();
	stations++;
	time += e.getWeightAt(0);
	distance += e.getWeightAt(1);

	while (pathTo.hasNext()) {
	    e = pathTo.next();
	    time += e.getWeightAt(0);
	    distance += e.getWeightAt(1);
	    stations++;
	    str2Append = e + "\n";
	    searchResultsExtended.append('‣' + str2Append); // extended results saves all, while normal not

	    /** Display the only edge where the transfer has happened */
	    if (!e.getLine().equals(line_changed)) {
		transfer++;
		searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
		searchResultsExtended.replace(0, searchResultsExtended.toString().indexOf('‣' + str2Append),
			searchResultsExtended.toString() + "↳Transfer \t| " + line_changed + " → " + str2Append);
	    }
	    line_changed = e.getLine();
	}

	/*-------------------------------------------------------------------*/
	/*----------------------------via→to-----------------------------*/
	/*-----------------------------------------------------------------*/
	Dijkstra second = null;

	/* if via station has been given */
	if (to != null) {
	    second = new Dijkstra(graph, hash.get(via), MainMenu.MODE);
	    allStationsBetween = second.pathTo(hash.get(to));
	    if (allStationsBetween == null) {
		JOptionPane.showMessageDialog(MainMenu.getFrame(), "There is no route.");
		return searchResults;
	    }
	    pathTo = allStationsBetween.iterator();

	    /*
	     * Check if there is a transfer in between, a very particular case when the
	     * specified via station is the stop station
	     */
	    if (!line_changed.equals(e.getLine())) {
		transfer++;
		searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
	    }
	    str2Append = "Stop \t| " + e + "\n";
	    searchResults.append(str2Append);
	    searchResultsExtended.append(str2Append);
	    line_changed = e.getLine();
	    while (pathTo.hasNext()) {
		e = pathTo.next();
		time += e.getWeightAt(0);
		distance += e.getWeightAt(1);
		stations++;
		str2Append = e + "\n";
		searchResultsExtended.append('‣' + str2Append);

		if (!e.getLine().equals(line_changed)) {
		    transfer++;
		    searchResults.append("↳Transfer \t| " + line_changed + " → " + str2Append);
		    searchResultsExtended.replace(0, searchResultsExtended.toString().indexOf('‣' + str2Append),
			    searchResultsExtended.toString() + "↳Transfer \t| " + line_changed + " → " + str2Append);
		}
		line_changed = e.getLine();
	    }

	}
	// append DESTINATION as well
	str2Append = "Destination \t| " + str2Append;
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	/*
	 * Adding travel info.
	 */
	str2Append = "\n" + JUtil.n_times_char(MainMenu.DISPLAY_PANE_LINE_LENGTH, SEPERATOR_CHAR);
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	try (Scanner sc = new Scanner(new File(MainMenu.DATA_REPO + MainMenu.CITY + "//fare.txt"))) {
	    String line = sc.nextLine();
	    /* Add total cost */
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    /**
	     * ↳Transfer + 1, because we pay when we enter the metro first time
	     */
	    str2Append = "Fare: ";
	    String cost = String.format("%.2f", Double.parseDouble(line) * (transfer + 1));
	    str2Append += cost;

	    /* Add currency */
	    line = sc.nextLine();
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    str2Append += " " + line;
	} catch (FileNotFoundException e1) {
	}

	/**
	 * This substring concationation is done only because, results are calculated
	 * only after traversing
	 */
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());
	/** Temporarily changing modes for displaying different parameters */
	int temp = MainMenu.MODE;

	/* Mode = 0, is for displaying time */
	MainMenu.MODE = 0;
	str2Append = "Lead Time: " + IOL.formatResult(time, MainMenu.MODE) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	/* Mode = 0, is for displaying distance */
	MainMenu.MODE = 1;
	str2Append = "Distance: " + IOL.formatResult(distance, MainMenu.MODE) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	MainMenu.MODE = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s)." + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.replace(0, searchResultsExtended.length(), str2Append + searchResultsExtended.toString());

	return searchResults;
    }

}
