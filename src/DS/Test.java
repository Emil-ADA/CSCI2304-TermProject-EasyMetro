package DS;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import Dependencies.StdOut;

public class Test {

    public static void main(String[] args) {
	LinearProbingHashST<String, Integer> hash = new LinearProbingHashST<>();
	String repo = ".//data//Cities//Istanbul//";

	File[] roadlines = { new File(repo + "blue.txt"), new File(repo + "green.txt"), new File(repo + "mavi.txt"),
		new File(repo + "orange.txt"), new File(repo + "red.txt") };

	int ID = 0;
	for (File line : roadlines) {
	    try (Scanner sc = new Scanner(line);) {
		sc.nextLine();
		while (sc.hasNext()) {
		    String key = sc.nextLine();
		    if (!hash.contains(key))
			hash.put(key, ID++);
		}
	    } catch (FileNotFoundException e) {
	    }
	}

	 for (String s : hash.keys()) {
	 StdOut.println(hash.get(s) + ":\t" + s);
	 }

	Graph map = new Graph(hash.size());

	for (File line : roadlines) {
	    try {
		Scanner sc = new Scanner(line);
		String[] first_line = sc.nextLine().split(" ");
		double time = Double.parseDouble(first_line[0]);
		double distance = Double.parseDouble(first_line[1]);

		String prev = sc.nextLine();

		while (sc.hasNext()) {
		    String key = sc.nextLine();

		    if (key.equals("CR/LF")) {
			prev = sc.nextLine();
			key = sc.nextLine();
		    }
		    Edge newEdge = new Edge(hash.get(prev), hash.get(key), time, distance);
		    newEdge.setVertexNames(prev, key);
		    map.addEdge(newEdge);
		    prev = key;
		}
	    } catch (FileNotFoundException e) {
	    }
	}

	
	StdOut.println("From Zeytinburnu To Menderes");
	DijkstraUndirectedSP sp = new DijkstraUndirectedSP(map, hash.get("Zeytinburnu"), 0);
	StdOut.print(sp.distTo(hash.get("Menderes")) + " dakika ");
	sp = new DijkstraUndirectedSP(map, hash.get("Zeytinburnu"), 1);
	StdOut.println(sp.distTo(hash.get("Menderes")) + " km");
	for (Edge e : sp.pathTo(hash.get("Menderes"))) {
	    StdOut.println(e + " ");
	}


	
    }

    /**
     * MD5 SHA-1 SHA-256
     */
    public static String hashString(String s) throws NoSuchAlgorithmException {
	byte[] hash = null;
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    hash = md.digest(s.getBytes());

	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < hash.length; ++i) {
	    String hex = Integer.toHexString(hash[i]);
	    if (hex.length() == 1) {
		sb.append(0);
		sb.append(hex.charAt(hex.length() - 1));
	    } else {
		sb.append(hex.substring(hex.length() - 2));
	    }
	}
	return sb.toString();
    }
}
