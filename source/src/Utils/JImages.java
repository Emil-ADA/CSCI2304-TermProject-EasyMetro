package Utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.PDFRenderer;
//import org.eclipse.swt.layout.FillLayout;
//import org.jcl.computer_utils.hard.JHardware;

public class JImages {
    public static void main(String[] args) throws IOException {

//	extractImageFromPDF(0, "D:\\SADIQ\\Library\\Engineering\\Computer_Science.(Course_Description).pdf");

    }

    /**
     * Writes an image using an arbitrary ImageWriterthat supports the given format
     * to a File. If there is already a File present, its contents are discarded.
     * Returns: Throws:
     * 
     * 
     * @param image
     *            a RenderedImage to be written.
     * @param fullPath
     *            the full location containing the name and the directory of a file
     * @param format
     *            - a String containing the informal name of the format.
     * 
     * @return false if no appropriate writer is found.
     * @throws IllegalArgumentException
     *             - if any parameter is null.<br>
     *             IOException - if an error occurs during writing.
     */
    public static boolean writeImageToFile(RenderedImage image, String fullPath, String format) {

	return writeImageToFile(image, fullPath.substring(0, fullPath.indexOf(JFilenames.getFileName(fullPath))),
		JFilenames.getFileName(fullPath), format);
    }

    /**
     * Writes an image using an arbitrary ImageWriterthat supports the given format
     * to a File. If there is already a File present, its contents are discarded.
     * Returns: Throws:
     * 
     * 
     * @param image
     *            a RenderedImage to be written.
     * @param Path
     *            the location containing only directory of a file
     * @param name
     *            the name of an image
     * @param format
     *            - a String containing the informal name of the format.
     * 
     * @return false if no appropriate writer is found.
     * @throws IllegalArgumentException
     *             - if any parameter is null.<br>
     *             IOException - if an error occurs during writing.
     */
    public static boolean writeImageToFile(RenderedImage image, String Path, String name, String format) {
	try {
	    System.out.println(Path + "\\" + name + "." + format);
	    return ImageIO.write(image, format, new File(Path + "\\" + name + "." + format));
	} catch (IOException e) {
	}
	return false;
    }

    /**
     * The method that allows to scale an image or change the size of it
     * 
     * @param srcImg
     *            the source of an image
     * @param w
     *            width to be set
     * @param h
     *            height to be set
     * @return scaled image
     */
    public static Image scaleImage(Image srcImg, int w, int h) {
	BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2 = resizedImg.createGraphics();

	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g2.drawImage(srcImg, 0, 0, w, h, null);
	g2.dispose();

	return resizedImg;
    }

    public static Color darkerColor(Color clr, double factor) {
	int newR = (int) (clr.getRed() * (1 - factor));
	int newG = (int) (clr.getGreen() * (1 - factor));
	int newB = (int) (clr.getBlue() * (1 - factor));
	return new Color(newR, newG, newB);
    }

//    public static BufferedImage extractImageFromPDF(int index, String fullPath) {
//	File file = new File(fullPath);
//	try (PDDocument document = PDDocument.load(file)) {
//
//	    String name = "Image No" + index + " - " + file.getAbsolutePath()
//		    .substring(file.getAbsolutePath().lastIndexOf("\\") + 1, file.getAbsolutePath().lastIndexOf("."));
//
//	    // Instantiating the PDFRenderer class
//	    PDFRenderer renderer = new PDFRenderer(document);
//
//	    // Rendering an image from the PDF document
//	    BufferedImage image = renderer.renderImage(index);
//
//	    return image;
//	} catch (Exception e) {
//	    return null;
//	}
//
//    }

    public static BufferedImage screenshot() {
	return screenshotBounds(new Rectangle(0, 0, (int) JHardware.getScreenSize().getWidth(),
		(int) JHardware.getScreenSize().getHeight()));
    }

    public static BufferedImage screenshotBounds(Rectangle rect) {
	Robot robot;
	try {
	    robot = new Robot();
	    return robot.createScreenCapture(rect);
	} catch (AWTException e) {
	} finally {
	    robot = null;
	}
	return null;
    }
}
