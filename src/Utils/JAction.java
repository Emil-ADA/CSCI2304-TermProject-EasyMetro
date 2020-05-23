package Utils;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;




public class JAction {
 
    /**
     * The instance of the Robot Class, that will be used for input manipulations
     */
    private static Robot ROBOT;

    /**
     * Auto-Delay Time for the Instance of Robot
     */
    @SuppressWarnings("unused")
    private static int ROBOT_AUTODELAY = 0;

    /**
     * Boolean for identifying whether or not to wait for idle for the Instance of
     * Robot
     */
    @SuppressWarnings("unused")
    private static boolean ROBOT_WAITFORIDLE = false;

    public static void main(String[] args) throws AWTException {

    }

    /**<p style = "background-color:red">
     * <h2>Method for rotating the wheel of the mouse</h2>
     * 
     * @param Notches
     *            how many times to rotate, if it is negative it will be rotated
     *            upwards, else if positive downwards
     * @throws UnconfiguredRobotException
     *             exception is thrown when the instance of the Robot has not been
     *             initialized, <i> <hey class = "test">configureRobot(~)
     *             </hey></i> is called in order to configure the instance of the
     *             Robot
     * <p>
     */
    public static void rotateMouseWheel(int Notches) {
	if (ROBOT == null)
	    throw new UnconfiguredRobotException();
	ROBOT.mouseWheel(Notches);
    }

    /**
     * Method for pressing a certain key
     * 
     * @param keycode
     *            - a corresponding key code for a key
     */
    public static void keyPress(int keycode) {
	if (ROBOT == null)
	    throw new UnconfiguredRobotException();
	ROBOT.keyPress(keycode);
    }

    /**
     * Method for releasing a certain key
     * 
     * @param keycode
     *            - a corresponding key code for a key
     */
    public static void keyRelease(int keycode) {

	if (ROBOT == null)
	    throw new UnconfiguredRobotException();

	ROBOT.keyRelease(keycode);
    }

    /**
     * Method for typing a certain key
     * 
     * @param keycode
     *            - a corresponding key code for a key
     */
    public static void keyType(int keycode) {
	keyPress(keycode);
	keyRelease(keycode);
    }

    /**
     * Method for pressing a certain key
     * 
     * @param keyChar
     *            - a corresponding key character for a key
     */
    public static void keyPress(char keyChar) {
	keyPress(KeyEvent.getExtendedKeyCodeForChar(keyChar));
    }

    /**
     * Method for releasing a certain key
     * 
     * @param keyChar
     *            - a corresponding key character for a key
     */
    public static void keyRelease(char keyChar) {
	keyRelease(KeyEvent.getExtendedKeyCodeForChar(keyChar));
    }

    /**
     * Method for typing a certain key
     * 
     * @param keyChar
     *            - a corresponding key character for a key
     */
    public static void keyType(char keyChar) {
	keyType(KeyEvent.getExtendedKeyCodeForChar(keyChar));
    }

    /**
     * Method for setting the location of mouse cursor
     * 
     * @param x
     *            - x coordinate
     * @param y
     *            - y coordinate
     */
    public static void setMouseLocation(int x, int y) {
	if (ROBOT == null)
	    throw new UnconfiguredRobotException();
	ROBOT.mouseMove(x, y);
    }

    /**
     * Method for pressing a given button of the mouse
     * 
     * @param button
     *            - button to be pressed
     */
    public static void mousePress(int button) {

	if (ROBOT == null)
	    throw new UnconfiguredRobotException();

	ROBOT.mousePress(identifyButtonMask(button));
    }

    /**
     * Method for releasing a given button of the mouse
     * 
     * @param button
     *            - button to be pressed
     */
    public static void mouseRelease(int button) {

	if (ROBOT == null)
	    throw new UnconfiguredRobotException();

	ROBOT.mouseRelease(identifyButtonMask(button));

    }

    /**
     * <h1>Method that identify the button mask from an integer, <br>
     * 1 = BUTTON1_DOWN_MASK,<br>
     * 2 = BUTTON2_DOWN_MASK,<br>
     * 3 = BUTTON3_DOWN_MASK<br>
     * <br>
     * </h1>
     * 
     * @param button
     *            - a given integer representing a corresponding button mask
     * @return - a corresponding button mask
     */
    private static int identifyButtonMask(int button) {
	int mask = button;

	switch (button) {
	case 3:
	    mask = InputEvent.BUTTON3_DOWN_MASK;
	    break;
	case 1:
	    mask = InputEvent.BUTTON1_DOWN_MASK;
	    break;
	case 2:
	    mask = InputEvent.BUTTON2_DOWN_MASK;
	    break;
	}
	return mask;
    }

    /**
     * Method for clicking a given button of the mouse
     * 
     * @param button
     *            - button to be pressed
     */
    public static void mouseClick(int button) {
	mousePress(button);
	mouseRelease(button);
    }

    /**
     * Method for clicking the right button of the mouse
     */
    public static void mouseRightClick() {
	mouseClick(InputEvent.BUTTON3_DOWN_MASK);
    }

    /**
     * Method for clicking the left button of the mouse
     */
    public static void mouseLeftClick() {
	mouseClick(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Method for taking a pixel color of the screen at a given location
     * 
     * @param x
     *            - x coordinate
     * @param y
     *            - y coordinate
     * @return - color of the point
     */
    public static Color pixelColorAt(int x, int y) {
	return ROBOT.getPixelColor(x, y);
    }

    /**
     * Method that must be called before utilizing regarding methods
     */
    public static void configureRobot() {
	try {
	    ROBOT = null;
	    ROBOT = new Robot();
	} catch (AWTException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Method that must be called before utilizing regarding methods, which also
     * sets some settings of the robot
     * 
     * @param autodelayPerMs
     *            - auto delay to be set
     * @param waitForIdle
     *            - boolean that identifies, whether robot will wait for idle or not
     */
    public static void configureRobot(int autodelayPerMs, boolean waitForIdle) {
	ROBOT_AUTODELAY = autodelayPerMs;
	ROBOT_WAITFORIDLE = waitForIdle;
	configureRobot();
    }

    /**
     * Method for shutting down the computer
     * 
     * @param forcequit
     *            - whether to quit forcefully or not
     */
    public static void shutdown(boolean forcequit) {
	if (forcequit) {
	    executeCommand("Shutdown.exe -s -t 00");
	} else {
	    executeCommand("Shutdown.exe -s");
	}
    }

    public static void setHibernateMode(boolean hiber) {
	executeCommand("powercfg -hibernate " + ((hiber) ? "on" : "off"));

    }

    public static void restart() {
	executeCommand("Shutdown.exe -r -t 00");
    }

    public static void hibernate() {
	executeCommand("Rundll32.exe Powrprof.dll,SetSuspendState");
    }

    public static void lockComputer() {
	executeCommand("Rundll32.exe user32.dll,LockWorkStation");
    }

    public static void sleep() {
	executeCommand("Rundll32.exe powrprof.dll,SetSuspendState Sleep");
    }

    public static String getCommandExecutionOutput(String command) {
	String s = "";
	try {

	    InputStream input = executeCommand(command).getInputStream();
	    BufferedInputStream buffer = new BufferedInputStream(input);
	    BufferedReader commandResult = new BufferedReader(new InputStreamReader(buffer));

	    for (String line = ""; line != null; line = commandResult.readLine()) {
		s += line.trim() + "\n";
	    }
	} catch (Exception e) {
	}
	return s.trim();
    }

    public static Process executeCommand(String command) {
	Runtime runtime = Runtime.getRuntime();
	try {
	    return runtime.exec(command);
	} catch (IOException e) {
	}
	return null;
    }

    public static Process executeCommand(String[] command) {
	Runtime runtime = Runtime.getRuntime();
	try {
	    return runtime.exec(command);
	} catch (IOException e) {
	}
	return null;
    }

    /**
     * To create a directory in Java <br>
     * P.S Both method mkdir() and mkdirs() are returning a boolean value to
     * indicate the operation status : true if succeed, false otherwise.
     * 
     * @param directory
     *            - the path of directory to be created
     * @throws NullPointerException
     *             - if the given parameter directory is null
     */
    public static void mkdirect(String directory) throws NullPointerException {
	// TODO MAKE CREATE DIRECTORY METHOD IN JFILES
	// 1.1 Creates a single directory.
	new File(directory).mkdirs();

	// 1.2 Creates a directory and all its sub-directories together.
	// new File("C:\\Directory2\\Sub2\\Sub-Sub2").mkdirs();

    }

    /**
     * Method that opens the given file using Runtime method
     * 
     * @param file
     *            the file to be opened
     */
    public static boolean open(File file) {
	if (file.exists()) {
	    try {
		Runtime.getRuntime().exec(file.getAbsolutePath());
	    } catch (IOException e) {
		System.err.println("@SFiles$openFile(File) : boolean - " + e.getMessage());
	    }
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Method for the parent thread to sleep
     * 
     * @param time
     *            the amount of time to sleep
     */
    public static void pause(int time) {
	try {
	    Thread.sleep(time);
	} catch (InterruptedException e) {
	    System.err.println("@SFiles$pause(int) : void - " + e.getMessage());
	}
    }
}

@SuppressWarnings("serial")
class UnconfiguredRobotException extends Error {
    public UnconfiguredRobotException() {
	super();
    }

    @Override
    public String getMessage() {
	// TODO Auto-generated method stub
	return "\nConfigure Robot before using: " + super.getMessage() + "\nCall configureRobot(...)";
    }
}
