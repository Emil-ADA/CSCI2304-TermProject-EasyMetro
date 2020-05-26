package Utils;

public class JFilenames extends FilenameUtils{
    public static void main(String[] args) {
	
    }
    
    public static String getFileName(String FullPath) {
 	return FullPath.substring(FullPath.lastIndexOf("\\") + 1, FullPath.length());
     }
    /**
     * Method to format bytes in human readable format
     * 
     * @param bytes
     *            - the value in bytes
     * @param digits
     *            - number of decimals to be displayed
     * @return human readable format string .
     */
    public static String formatFileSize(double bytes, int digits) {

	String[] dictionary = { "bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

	int index = 0;

	for (; index < dictionary.length; index++) {
	    if (bytes < 1024) {
		break;
	    }
	    bytes = bytes / 1024;
	}
	return String.format("%." + digits + "f", bytes) + " " + dictionary[index];
    }
}
