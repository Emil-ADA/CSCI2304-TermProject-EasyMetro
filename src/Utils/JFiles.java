package Utils;

import java.awt.Desktop;
import java.awt.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

public class JFiles {
    public static void main(String[] args) {
	
    }
    public static void openInExplorer(File file) {
	try {
	    Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());

	    // Or another way
	    // Process p = new ProcessBuilder("explorer.exe", "/select,C:\\" +
	    // file.getAbsolutePath() ).start();
	} catch (IOException e) {
	}
    }

    public static Image getFileIcon(File file) {
	OsDetector.OSType ostype = OsDetector.getOperatingSystemType();

	switch (ostype) {
	case Unix:
	    final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
	    return ((ImageIcon) fc.getUI().getFileView(fc).getIcon(file)).getImage();
	default:
	    return ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
	}

    }

    /**
     * Method that deletes the file from the given directory
     * 
     * @param file
     *            the File to be deleted
     * @throws IOException
     *             exception is thrown when the specified file does not exist
     */
    public static void deleteUsingFileClass(File file, boolean permanently) {
	// TODO
	if (permanently && file.exists()) {
	    file.delete();
	} else {

	}
    }

    /**
     * Method that deletes the file from the given directory
     * 
     * @param file
     *            the File to be deleted
     * @throws IOException
     *             exception is thrown when the specified file does not exist
     */
    public static void deleteUsingFiles_FileSystemsClasses(File file) throws IOException {

	Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
	/* or */
	// Path path = Paths.get(directory + file);

	// Files.delete(path);

	Files.deleteIfExists(path);

    }

    public static String getLastModified(File file, String pattern) {
	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	return sdf.format(file.lastModified());
    }

    public static String getLastModified(File file) {
	return getLastModified(file, "MM/dd/yyyy hh:mm a");
    }

    /**
     * Enters to the source file if it is folder(otherwise if is file returns it),
     * then looks for last folder in that folder then recursively check that last
     * folder in the same way. However if the file precedes the folder priority will
     * be given to the folder; that is if the last file in the folder is not
     * directory then the final result will be that of returned by recursion
     * 
     * @param source
     *            the source where to search
     * @return the last file of the last folder of a source file
     */
    public static String getLastLastFolder(File source) {
	File[] files = source.listFiles();

	for (int i = files.length - 1; i >= 0; i--) {
	    if (files[i].isDirectory()) {
		return getLastLastFolder(files[i]);
	    }
	    if (i == 0 && !files[i].isDirectory()) {
		return files[i].getAbsolutePath();
	    }
	}
	return null;

    }

    /**
     * Enters to the source file if it is folder(otherwise if is file returns it),
     * then looks for last folder in that folder then recursively check that last
     * folder in the same way. However if the file precedes the folder priority will
     * be given to the files; that is if the last file in the folder is not
     * directory then the final result will be that file
     * 
     * @param source
     *            the source where to search
     * @return the last file of the last folder of a source file
     */
    public static String getLastLastFile(File source) {
	File[] files = source.listFiles();

	if (files[files.length - 1].isDirectory()) {
	    return getLastLastFile(files[files.length - 1]);
	} else {
	    return files[files.length - 1].getAbsolutePath();

	}

    }

    /**
     * To rename the given file to the given name
     * 
     * @param oldfile
     *            the File to be renamed
     * @param name
     */
    public static boolean rename(File oldfile, String name) {
	return renameTo(oldfile,
		oldfile.getParentFile() + "\\" + name + "." + FilenameUtils.getExtension(oldfile.getName()));
    }

    private static boolean renameTo(File oldfile, String name) {
	return oldfile.renameTo(new File(name));
    }

    /**
     * Method that opens the given file using Desktop method
     * 
     * @param file
     *            the file to be opened
     */
    public static boolean openUsingDesktop(File file) {

	// first check if Desktop is supported by Platform or not
	if (!Desktop.isDesktopSupported()) {
	    System.out.println("Desktop is not supported");
	    return false;
	}

	if (file.exists()) {
	    try {
		Desktop.getDesktop().open(file);
		return true;
	    } catch (IOException e) {
		System.err.println("@SFiles$open(File) : boolean - " + e.getMessage());
	    }
	}
	return false;

    }

    /**
     * The method that move the file - "oldFile" to the destination - newDirectory
     * 
     * @param oldfile
     *            - the source
     * @param newDirectory
     *            - the destination
     */
    public static void move(File oldfile, String newDirectory) {
	renameTo(oldfile, newDirectory + "\\" + oldfile.getName());
    }

    /**
     * The method that write "text" into the "file" (if "file" does not exists it
     * creates one)
     * 
     * @param text
     *            - the text to write
     * @param file
     *            - the file to write into
     * @param append
     *            - append to the end of the existing content if it is true, clears
     *            and then writes if otherwise
     * @return if the writing process is successful returns true, false otherwise
     */
    public static boolean writeToFile(String text, String file, boolean append) {
	if (file == null)
	    return false;

	try (PrintWriter pw = new PrintWriter(new FileWriter(file, append));) {
	    /*
	     * When append is false then the given file will be truncated, which means that
	     * everything in the existing file will be deleted and written from the
	     * beginning. When append is true then everything will be written to the end of
	     * the file.
	     */
	    pw.println(text);

	    // Just in case
	    pw.close();
	    return true;
	} catch (IOException e) {
	    System.err.println("@SFiles$write(String, String, boolean) : boolean - " + e.getMessage());
	}
	return false;

    }

    /**
     * The method that returns the specified file's contents as one String
     * 
     * @param file
     *            - the Repository of the file
     * @return String - the Contents of the file
     */
    public static String readFile(String file) {
	// Accumulator
	String text = "";

	// Initializing Scanner
	Scanner sc = null;
	try {
	    sc = new Scanner(new File(file));
	} catch (FileNotFoundException e) {
	}

	// While file has content
	while (sc.hasNext()) {

	    // Take line by line
	    text += sc.next() + "\n";
	}
	return text;
    }

    /**
     * The method that returns the specified file's contents as an Array of Strings
     * 
     * @param file
     *            - the Repository of the file
     * @return Array of Strings - the Contents of the file
     */
    public static String[] readFileArraywise(String file) {

	// ArrayList for collecting text
	ArrayList<String> txts = new ArrayList<>();

	// Initializing Scanner
	Scanner sc = null;
	try {
	    sc = new Scanner(new File(file));
	} catch (FileNotFoundException e) {
	}

	// While file has content
	while (sc.hasNext()) {

	    // Take line by line
	    txts.add(sc.nextLine());
	}

	// Initializing the String Array
	String[] str = new String[txts.size()];

	// Converting ArrayList to the String array
	str = txts.toArray(str);

	return str;
    }
}
