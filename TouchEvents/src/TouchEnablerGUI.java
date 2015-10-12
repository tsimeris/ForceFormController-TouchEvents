import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;

import javax.comm.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.util.*;

/*
 * The main class for the touch events package, this hooks everything together
 * 
 * There are two calculations in this class - one is how much space a magnet actually takes up, and the second is the 20mm offset that we're using
 * 
 * @author Jess
 */
public class TouchEnablerGUI extends JPanel
{
  public double magnetScreenWidth = 0;
  public double magnetScreenHeight = 0;

  public int numberOfMagnetsWide = 4;
  public int numberOfMagnetsHigh = 4;
  
  public double widthOffset = 0;
  public double heightOffset = 0;
  
  public double percentOffsetInHeight = 0;
  public double percentOffsetInWidth = 0;
  
  public int screenWidth;
  public int screenHeight;
  public String stringToWriteToFile;
  
  public Vector<MagnetDimensionsContainer> rectangleLocations;
  public Vector<String> configSettings;
  
  public String pathToConfigFile = "C:\\Users\\Jess\\AppData\\Roaming\\MagnetConfig.txt";
  
  /*
   * An inital method to be called to mainly set up the GUI
   */
  public void init()
  {
	//set up the UI
    try 
    {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (InstantiationException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    
    //set up the UI
    //javax.swing.SwingUtilities.invokeLater(new Runnable() 
    //{
      //public void run() 
      //{
        JFrame myFrame = new JFrame("Touch Event Getter"); //make a frame
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	JComponent newContentPane = new TouchEnablerGUI(); //add one of our TouchEventGetters to the frame
    	newContentPane.setOpaque(true);
    	TouchEnablerMouseAndKeyListener myListener = new TouchEnablerMouseAndKeyListener(this);
    	newContentPane.addMouseListener(myListener);
    	newContentPane.addMouseMotionListener(myListener);
    	myFrame.addKeyListener(myListener);
    	newContentPane.addKeyListener(myListener);
    	myFrame.setSize(new Dimension(500,500));
    	newContentPane.setSize(new Dimension(500,500));
    	myFrame.setContentPane(newContentPane);
    	myFrame.pack();
    	myFrame.setVisible(true);
      //}
    //});
  }
  
  /*
   * Constructor for objects of type TouchEventGetter
   * 
   * Does some calculations so that we know where the magnets are, etc.
   * 
   */
  public TouchEnablerGUI()
  {
	//SerialPortReader spr = new SerialPortReader(); //commented out because we're not using it
	rectangleLocations = new Vector<MagnetDimensionsContainer>();
	//calculate how to draw the squares for each magnet
	//the hardcoded values are for the 15 inch cyclotouch overlay
	Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	screenWidth = screenSize.width;
	screenHeight = screenSize.height;
	//21 / 231
	double magnetDiameterRealLifePercentOfHeight = 0.0909090909;
	magnetScreenHeight = screenHeight * magnetDiameterRealLifePercentOfHeight;
	//21 / 306
	double magnetDiameterRealLifePercentOfWidth = 0.0686274509803922;
	magnetScreenWidth = screenWidth * magnetDiameterRealLifePercentOfWidth;
	
	//we can't start the array in the top corner, so let's assume a 20mm offset in each dimension
	//let's calculate what that is according to the screen size of this computer.
	//20 / 231
	percentOffsetInHeight = 0.0865800865800866;
	//20 / 306 (actual mm screensize of overlay)
	percentOffsetInWidth = 0.065359477124183;
	heightOffset = screenSize.height * percentOffsetInHeight;
	widthOffset = screenSize.width * percentOffsetInWidth;
	
	double currXPos = widthOffset;
	double currYPos = heightOffset;
	  
	//save the locations of all rectangles
	for (int j = 0; j < numberOfMagnetsHigh; j++)
	{
	  currXPos = widthOffset;
	  for (int i = 0; i < numberOfMagnetsWide; i++)
	  {
		rectangleLocations.add(new MagnetDimensionsContainer(currXPos, currYPos, magnetScreenWidth, magnetScreenHeight));
		currXPos = currXPos + magnetScreenWidth;
	  }
	  currYPos = currYPos + magnetScreenHeight;
	}
	
	//This program is only going to read in the file once, at the beginning, so that it can keep track of what changes are being made
	configSettings = new Vector<String>();
	File configFile = new File(pathToConfigFile);
	try 
	{
	  Scanner myScanner = new Scanner(configFile);
	  while (myScanner.hasNextLine()) 
	  {
        String line = myScanner.nextLine();
        configSettings.add(line);
        System.out.println("Added original magnet setting: " + line + " to the vector of magnet settings.");
      }
	}
	catch (FileNotFoundException e)
	{
		System.out.println("We were looking for: " + configFile.getName() + " but we couldn't find the file.");
		e.printStackTrace();
	}
  }
  
  /*
   * This is a paint method to draw the grid onto the screen
   * 
   * Called periodically, including when the window is resized.
   */
  public void paintComponent(Graphics g)
  {
	super.paintComponent(g);
	Graphics2D myGraphics = (Graphics2D) g;
	myGraphics.setColor(Color.blue);
	
	//draw the grid of magnets
	if ((magnetScreenHeight != 0) && (magnetScreenWidth != 0))
	{
	  double whereToDrawX = widthOffset;
	  double whereToDrawY = heightOffset;
	  
	  for (int j = 0; j < numberOfMagnetsHigh; j++)
	  {
		whereToDrawX = widthOffset;
	    for (int i = 0; i < numberOfMagnetsWide; i++)
	    {
		  myGraphics.drawRect(((int)Math.round(whereToDrawX)), ((int)Math.round(whereToDrawY)), ((int)Math.round(magnetScreenWidth)), ((int)Math.round(magnetScreenHeight)));
		  whereToDrawX = whereToDrawX + magnetScreenWidth;
	    }
	    whereToDrawY = whereToDrawY + magnetScreenHeight;
	  }
	  myGraphics.setColor(Color.red);
	  for (int k = 0; k < rectangleLocations.size(); k++)
	  {
	    myGraphics.drawLine(((int)Math.round(rectangleLocations.elementAt(k).gettopleftx())), ((int)Math.round(rectangleLocations.elementAt(k).gettoplefty())), ((int)Math.round(rectangleLocations.elementAt(k).gettoprightx())), ((int)Math.round(rectangleLocations.elementAt(k).gettoprighty())));
	    myGraphics.drawLine(((int)Math.round(rectangleLocations.elementAt(k).gettoprightx())), ((int)Math.round(rectangleLocations.elementAt(k).gettoprighty())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomrightx())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomrighty())));
	    myGraphics.drawLine(((int)Math.round(rectangleLocations.elementAt(k).getbottomrightx())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomrighty())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomleftx())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomlefty())));
	    myGraphics.drawLine(((int)Math.round(rectangleLocations.elementAt(k).getbottomleftx())), ((int)Math.round(rectangleLocations.elementAt(k).getbottomlefty())), ((int)Math.round(rectangleLocations.elementAt(k).gettopleftx())), ((int)Math.round(rectangleLocations.elementAt(k).gettoplefty())));
	  }
	}
  }
  
  /*
   * Simply a getter for the rectanglelocations vector, so that other classes, in particular the listeners, can access them
   */
  public Vector<MagnetDimensionsContainer> getRectangleLocations()
  {
	return this.rectangleLocations;
  }
  
  /*
   * Simply a getter for the configsettings vector, so that other classes, in particular the listeners, can access them
   */
  public Vector<String> getConfigSettings()
  {
	return this.configSettings;
  }
  
  /*
   * Simply a getter for the path of the config file
   */
  public String getPath()
  {
	return this.pathToConfigFile;
  }
}