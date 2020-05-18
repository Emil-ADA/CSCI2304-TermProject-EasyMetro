package main;

import java.util.ArrayList;
import java.util.List;

public class Autocompletion {

    public static List<String> query(String queryStr, List<String> list) {
	List<String> suggestion = new ArrayList<>();
	list.forEach(std -> {
	    if (isMatched(queryStr, std)) {
		suggestion.add(String.valueOf(std));
	    }
	});

	return suggestion;
    }

    public static void main(String[] args) {

	List<String> list = new ArrayList<>();
	list.add("Basaksehir Metrokent");
	list.add("Basak Konutlari");
	list.add("Siteler");
	list.add("Turgut Ozal");
	list.add("Ziya Gokalp Mah");
	list.add("Olimpiyat");
	list.add("Ikitelli Sanay");
	list.add("Mahmutbey");
	list.add("Yenimahalle");
	List<String> sugg = query("", list);
	sugg.forEach(System.out::println);
    }

    private static boolean isMatched(String query, String text) {
	return text.toLowerCase().contains(query.toLowerCase());
    }
}
