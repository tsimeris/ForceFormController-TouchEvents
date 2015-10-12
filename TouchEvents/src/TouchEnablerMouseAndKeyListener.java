import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.event.MouseInputListener;

/**
 * This class enables key presses to change how ForceForm behaves. Mostly used for demos.
 * 
 * @author Jess
 *
 */


public class TouchEnablerMouseAndKeyListener implements MouseInputListener, KeyListener
{
  //booleans for which mode is activated when you press a key
  boolean dataOutputMode;
  boolean pushDownMode;
  boolean flatteningMode;
  boolean onePlaceRippleMode;
  boolean allOverRippleMode;
  boolean medicalMode;
  //experiment mode has been removed for online version of code.
  boolean experimentMode;
  
  //a boolean for the flattening mode to use from time to time
  boolean currentlyFlattening;
  TouchEnablerGUI instanceOfTouchEnabler;
  
  int lastMagnetChanged;
  String lastMagnetChangedSetting;
  
  //medical stuff
  int startingPos;
  int otherMagnetToEnergise;
  int currentlyOn;
  
  //modes for selection:
  //keypress a = alternate output configuration
  //keypress i = interesting output configuration
  //keypress s = slider output configuration
  //keypress f = flattening mode
  //keypress r = ripple all over mode
  //keypress m = medical mode
  //keypress e = experiment mode
  
  /*
   * This class is both a mouse listener and a key listener
   * Keys are pressed to set the current mode we are in - some of the user study modes have been removed.
   * The mouse events are needed for the actual interaction
   */
  public TouchEnablerMouseAndKeyListener(TouchEnablerGUI theInstance)
  {
	this.instanceOfTouchEnabler = theInstance;
	pushDownMode = true; //default is push down mode
	dataOutputMode = false;
	flatteningMode = false;
	onePlaceRippleMode = false;
	allOverRippleMode = false;
	medicalMode = false;
	
	lastMagnetChanged = -1;
	lastMagnetChangedSetting = "";
  }
	
  public void mouseClicked(MouseEvent mouseClick) 
  {
	  
  }

  public void mouseEntered(MouseEvent arg0) 
  {

  }

  public void mouseExited(MouseEvent arg0) 
  {
	  
  }

  public void mousePressed(MouseEvent mousePress) 
  {
	if (pushDownMode == true)
	{
      pushToGoDownPushToComeUpScenario(mousePress);
	}
	else if (flatteningMode == true)
	{
	  currentlyFlattening = true;
	}
	//else if (medicalMode == true) //edited today
	//{
	//  medicalScenario(mousePress);
	//}
  }

  public void mouseReleased(MouseEvent arg0)
  {
	currentlyFlattening = false;
	lastMagnetChangedSetting = "";
  }
  
  public void pushToGoDownPushToComeUpScenario(MouseEvent mousePress)
  {
	if (pushDownMode == true)
	{
	  Vector<MagnetDimensionsContainer> rectangleLocations = instanceOfTouchEnabler.getRectangleLocations();
	  Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  String stringToWriteToFile;
	  String pathToConfigFile = instanceOfTouchEnabler.getPath();
	  if (rectangleLocations.size() > 0)
	  {
	    //have to add the offset to these!
		//hacky and not perfect at all!
		int xMouseClick = mousePress.getLocationOnScreen().x-10;
		int yMouseClick = (mousePress.getLocationOnScreen().y-30);
		  
		for (int i = 0; i < rectangleLocations.size(); i++)
		{
	      if ((xMouseClick < rectangleLocations.elementAt(i).gettoprightx()) && (xMouseClick > rectangleLocations.elementAt(i).gettopleftx()))
		  {
		    if ((yMouseClick < rectangleLocations.elementAt(i).getbottomlefty()) && yMouseClick > rectangleLocations.elementAt(i).gettoplefty())
			{
		      String oldSetting = configSettings.elementAt(i);
				
			  if (oldSetting.equals("-10") || oldSetting.equals("000"))
			  {
			    //set to on
			    configSettings.set(i, "010");
			  }
			  else if (oldSetting.equals("010"))
			  {
			    //set to off
			    configSettings.set(i, "-10");
			  }
			  
			  try
			  {
			    //let's make a string to write to the file.
			    stringToWriteToFile = "";
			    //String newLineCharacter = System.getProperty("line.separator");
				for (int j = 0; j < configSettings.size(); j++)
				{
			      //we don't want a new line at the end
				  if (j == configSettings.size()-1)
				  {
				    stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString();
				  }
				  else
				  {
					stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString() + '\r' + '\n';
				  }
				}
				FileWriter fw = new FileWriter(pathToConfigFile);
			    BufferedWriter bw = new BufferedWriter(fw);
				bw.write(stringToWriteToFile);
				bw.close();
			  }
			  catch (IOException ioe)
			  {
			    System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
			    ioe.printStackTrace();
			  }
			}
	      }
		}
	  }
	}
  }
  
  /*
   * 
   * When a key has been pressed, this means that the user wants to change the current mode. We toggle a boolean to on to say that we are in that mode.
   * We also set all other modes to false, just to make sure that only one mode is operating.
   * Then methods are able to be run according to what booleans are on.
   * 
   */
  public void keyPressed(KeyEvent keyTypedEvent) 
  {
	if (keyTypedEvent.getKeyChar() == 'a' || keyTypedEvent.getKeyChar() == 'i' || keyTypedEvent.getKeyChar() == 's')
	{
	  pushDownMode = false;
	  flatteningMode = false;
	  dataOutputMode = true;
	  onePlaceRippleMode = false;
	  allOverRippleMode = false;
	  medicalMode = false;
	  experimentMode = false;
	
	  Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  String stringToWriteToFile;
	  String pathToConfigFile = instanceOfTouchEnabler.getPath();
	
	  if(keyTypedEvent.getKeyChar() == 'a')
	  {
		//alternate the hills and troughs
	    configSettings.set(0, "-10");
	    configSettings.set(1, "010");
	    configSettings.set(2, "-10");
	    configSettings.set(3, "010");
	    configSettings.set(4, "-10");
	    configSettings.set(5, "010");
	    configSettings.set(6, "-10");
	    configSettings.set(7, "010");
	    configSettings.set(8, "-10");
	    configSettings.set(9, "010");
	    configSettings.set(10, "-10");
	    configSettings.set(11, "010");
	    configSettings.set(12, "-10");
	    configSettings.set(13, "010");
	    configSettings.set(14, "-10");
	    configSettings.set(15, "010");
	  }
	  else if (keyTypedEvent.getKeyChar() == 'i')
	  {
		//show an interesting configuration
	    configSettings.set(0, "-10");
	    configSettings.set(1, "010");
	    configSettings.set(2, "010");
	    configSettings.set(3, "010");
	    configSettings.set(4, "-10");
	    configSettings.set(5, "-10");
	    configSettings.set(6, "010");
        configSettings.set(7, "010");
        configSettings.set(8, "010");
        configSettings.set(9, "010");
	    configSettings.set(10, "-10");
	    configSettings.set(11, "-10");
	    configSettings.set(12, "-10");
	    configSettings.set(13, "010");
	    configSettings.set(14, "-10");
	    configSettings.set(15, "-10");
	  }
	  else if (keyTypedEvent.getKeyChar() == 's')
	  {
		//show a slider
	    configSettings.set(0, "-10");
		configSettings.set(1, "-10");
		configSettings.set(2, "-10");
		configSettings.set(3, "010");
		configSettings.set(4, "010");
		configSettings.set(5, "010");
	    configSettings.set(6, "010");
	    configSettings.set(7, "010");
	    configSettings.set(8, "010");
	    configSettings.set(9, "010");
		configSettings.set(10, "010");
		configSettings.set(11, "010");
		configSettings.set(12, "010");
		configSettings.set(13, "010");
		configSettings.set(14, "010");
		configSettings.set(15, "010");
	  }
	  
	  try
	  {
	    //let's make a string to write to the file.
	    stringToWriteToFile = "";
	    //String newLineCharacter = System.getProperty("line.separator");
	    for (int j = 0; j < configSettings.size(); j++)
   	    {
          //we don't want a new line at the end
          if (j == configSettings.size()-1)
		  {
		    stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString();
		  }
		  else
		  {
		    stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString() + '\r' + '\n';
		  }
	    }
	    FileWriter fw = new FileWriter(pathToConfigFile);
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(stringToWriteToFile);
        bw.close();
	  }
	  catch (IOException ioe)
	  {
	    System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
	    ioe.printStackTrace();
	  }
    }
	else if (keyTypedEvent.getKeyChar() == 'f') //flattening mode has been turned on
	{
	  //then we are in flattening mode
      flatteningMode = true;
      pushDownMode = false;
      dataOutputMode = false;
	  onePlaceRippleMode = false;
	  allOverRippleMode = false;
	  medicalMode = false;
	  experimentMode = false;
	}
	else if (keyTypedEvent.getKeyChar() == 'r') //ripple mode has been turned on
	{
	  //then we are in all over ripple mode
	  flatteningMode = false;
	  pushDownMode = false;
	  dataOutputMode = false;
	  onePlaceRippleMode = false;
	  allOverRippleMode = true;
	  medicalMode = false;
	  experimentMode = false;
	  System.out.println("TouchEnablerMouseAndKeyListener: In All Over Ripple mode");
	  
	  //Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  //String stringToWriteToFile;
	  //String pathToConfigFile = instanceOfTouchEnabler.getPath();
	  
	  AllOverRippleThread myRippleThread = new AllOverRippleThread(instanceOfTouchEnabler.getConfigSettings(), instanceOfTouchEnabler.getPath());
	  myRippleThread.run();
    }
	else if (keyTypedEvent.getKeyChar() == 'o') //one ripple mode has been turned on
	{
      //then we are in one place ripple mode
      flatteningMode = false;
	  pushDownMode = false;
	  dataOutputMode = false;
	  allOverRippleMode = false;
	  onePlaceRippleMode = true;
	  medicalMode = false;
	  experimentMode = false;
	  System.out.println("TouchEnablerMouseAndKeyListener: In One Place Ripple mode");
		  
	  //Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  //String stringToWriteToFile;
	  //String pathToConfigFile = instanceOfTouchEnabler.getPath();
		  
	  OnePlaceRippleThread myOnePlaceRippleThread = new OnePlaceRippleThread(instanceOfTouchEnabler.getConfigSettings(), instanceOfTouchEnabler.getPath());
	  myOnePlaceRippleThread.run();
    }
	else if (keyTypedEvent.getKeyChar() == 'm') //medical mode has been turned on
	{
	  //then we are in medical mode
	  flatteningMode = false;
      pushDownMode = false;
      dataOutputMode = false;
	  allOverRippleMode = false;
	  onePlaceRippleMode = false;
	  medicalMode = true;
	  experimentMode = false;
	  System.out.println("TouchEnablerMouseAndKeyListener: In medical mode");
	  Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  
	  //Preliminary medical mode set up! Only needs to be done once.
	  
	  //set them all off
	  configSettings.set(0, "000");
	  configSettings.set(1, "000");
	  configSettings.set(2, "000");
	  configSettings.set(3, "000");
	  configSettings.set(4, "000");
	  configSettings.set(5, "000");
	  configSettings.set(6, "000");
	  configSettings.set(7, "000");
	  configSettings.set(8, "000");
	  configSettings.set(9, "000");
      configSettings.set(10, "000");
	  configSettings.set(11, "000");
	  configSettings.set(12, "000");
	  configSettings.set(13, "000");
	  configSettings.set(14, "000");
	  configSettings.set(15, "000");
	  
	//work out a random number between 0 and 15
	  Random randomNo = new Random();
	  startingPos = randomNo.nextInt(16);
	  
	  //what is adjacent to each magnet
	  Vector<int[]> vectorOfVectors = new Vector<int[]>();
	  int[] posArray0 = {1, 4};
	  int[] posArray1 = {0, 2, 5};
	  int[] posArray2 = {1, 3, 6};
	  int[] posArray3 = {2, 7};
	  int[] posArray4 = {0, 5, 8};
	  int[] posArray5 = {1, 4, 6, 9};
	  int[] posArray6 = {2, 5, 7, 10};
	  int[] posArray7 = {3, 6, 11};
	  int[] posArray8 = {4, 9, 12};
	  int[] posArray9 = {5, 8, 10, 13};
	  int[] posArray10 = {6, 9, 11, 14};
	  int[] posArray11 = {7, 10, 15};
	  int[] posArray12 = {8, 13};
	  int[] posArray13 = {9, 12, 14};
	  int[] posArray14 = {10, 13, 15};
	  int[] posArray15 = {11, 14};
	  
	  vectorOfVectors.add(0, posArray0);
	  vectorOfVectors.add(1, posArray1);
	  vectorOfVectors.add(2, posArray2);
	  vectorOfVectors.add(3, posArray3);
	  vectorOfVectors.add(4, posArray4);
	  vectorOfVectors.add(5, posArray5);
	  vectorOfVectors.add(6, posArray6);
	  vectorOfVectors.add(7, posArray7);
	  vectorOfVectors.add(8, posArray8);
	  vectorOfVectors.add(9, posArray9);
	  vectorOfVectors.add(10, posArray10);
	  vectorOfVectors.add(11, posArray11);
	  vectorOfVectors.add(12, posArray12);
	  vectorOfVectors.add(13, posArray13);
	  vectorOfVectors.add(14, posArray14);
	  vectorOfVectors.add(15, posArray15);
	  
	  int numberOfAdjacents = (vectorOfVectors.elementAt(startingPos).length);
	  int randomNumToArraySize = randomNo.nextInt(numberOfAdjacents);
	  otherMagnetToEnergise = vectorOfVectors.elementAt(startingPos)[randomNumToArraySize]; //second, alternating magnet
	  
	  System.out.println("TouchEnablerMouseAndKeyListener: We have worked out an initial magnet as: " + startingPos + " and the second magnet as the adjacent " + otherMagnetToEnergise + ". These won't change.");
	  
	  //energise the starting electromagnet.
	  configSettings.set(startingPos, "010");
	  
	  currentlyOn = startingPos;
	}
	
	else if (keyTypedEvent.getKeyChar() == KeyEvent.VK_ENTER)
	{
		System.out.println("TouchEnablerMouseAndKeyListener: Enter key functionality has been removed for online version of code");
	}

  }
  
  public void medicalScenario(MouseEvent mousePress)
  {
	//medical mode simulates a lump which moves beneath the skin.
	if (medicalMode)
	{
	  Vector<MagnetDimensionsContainer> rectangleLocations = instanceOfTouchEnabler.getRectangleLocations();
	  Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
	  String stringToWriteToFile;
	  String pathToConfigFile = instanceOfTouchEnabler.getPath();
	  
	  if (rectangleLocations.size() > 0)
	  {
	    //have to add the offset to these!
		//hacky and not perfect at all!
		int xMouseClick = mousePress.getLocationOnScreen().x-10;
		int yMouseClick = (mousePress.getLocationOnScreen().y-30);
		  
		for (int i = 0; i < rectangleLocations.size(); i++)
		{
	      if ((xMouseClick < rectangleLocations.elementAt(i).gettoprightx()) && (xMouseClick > rectangleLocations.elementAt(i).gettopleftx()))
		  {
		    if ((yMouseClick < rectangleLocations.elementAt(i).getbottomlefty()) && yMouseClick > rectangleLocations.elementAt(i).gettoplefty())
			{
			  //i is where they clicked
		      if (i == startingPos && currentlyOn == startingPos)
		      {
		    	System.out.println("TouchEnablerMouseAndKeyListener: The user found startingpos, sleeping then switching to othermagnet");
		    	try
		  		{
		  		  Thread.sleep(500);
		  		} 
		  		catch (InterruptedException e) 
		  		{
		  		  e.printStackTrace();
		  		}
		    	//flip
		    	configSettings.set(otherMagnetToEnergise, "-10");
		    	configSettings.set(startingPos, "000");
		    	
		    	currentlyOn = otherMagnetToEnergise;
		      }
		      else if (i == otherMagnetToEnergise && currentlyOn == otherMagnetToEnergise)
		      {
		    	System.out.println("TouchEnablerMouseAndKeyListener: The user found othermagnet, sleeping then switching to startingpos");
			    try
			    {
			      Thread.sleep(500);
			    } 
			    catch (InterruptedException e) 
			  	{
			  	  e.printStackTrace();
			  	}
			    //flip
		    	configSettings.set(startingPos, "-10");
		    	configSettings.set(otherMagnetToEnergise, "000");
		    	
		    	currentlyOn = startingPos;
		      }
			  
			  try
			  {
			    //let's make a string to write to the file.
				  stringToWriteToFile = "";
			    //String newLineCharacter = System.getProperty("line.separator");
				for (int j = 0; j < configSettings.size(); j++)
				{
			      //we don't want a new line at the end
				  if (j == configSettings.size()-1)
				  {
					  stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString();
				  }
				  else
				  {
					  stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString() + '\r' + '\n';
				  }
				}
				FileWriter fw = new FileWriter(pathToConfigFile);
			    BufferedWriter bw = new BufferedWriter(fw);
				bw.write(stringToWriteToFile);
				bw.close();
			  }
			  catch (IOException ioe)
			  {
			    System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
			    ioe.printStackTrace();
			  }
			}
	      }
		}
	  }
	}
  }

  public class OnePlaceRippleThread implements Runnable
  {
	Vector<String> configSettings;
	String pathToConfigFile;
	String stringToWriteToFileInThread;
	
    public OnePlaceRippleThread(Vector<String> configSettingsIn, String pathToConfigFileIn) 
	{
      this.configSettings = configSettingsIn;
      this.pathToConfigFile = pathToConfigFileIn;
	}
	
    public void run() 
    {
	  while (true)
	  {
	    configSettings.set(0, "000");
	    configSettings.set(1, "000");
	    configSettings.set(2, "-10");
	    configSettings.set(3, "000");
	    configSettings.set(4, "000");
	    configSettings.set(5, "000");
	    configSettings.set(6, "000");
	    configSettings.set(7, "000");
	    configSettings.set(8, "000");
	    configSettings.set(9, "000");
		configSettings.set(10, "000");
		configSettings.set(11, "000");
		configSettings.set(12, "000");
		configSettings.set(13, "000");
		configSettings.set(14, "000");
		configSettings.set(15, "000");
		
		//FIRST SLEEPING IS HERE
		try
		{
		  Thread.sleep(100);
		} 
		catch (InterruptedException e) {
		  e.printStackTrace();
		}
		
	    try
	    {
	      //let's make a string to write to the file.
	      stringToWriteToFileInThread = "";
	      //String newLineCharacter = System.getProperty("line.separator");
	      for (int j = 0; j < configSettings.size(); j++)
   	      {
            //we don't want a new line at the end
            if (j == configSettings.size()-1)
		    {
		      stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString();
		    }
		    else
		    {
		      stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString() + '\r' + '\n';
		    }
	      }
	      FileWriter fw = new FileWriter(pathToConfigFile);
	      BufferedWriter bw = new BufferedWriter(fw);
	      bw.write(stringToWriteToFileInThread);
          bw.close();
	    }
	    catch (IOException ioe)
	    {
	      System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
	      ioe.printStackTrace();
	    }
	    
	    configSettings.set(0, "000");
	    configSettings.set(1, "000");
	    configSettings.set(2, "000");
	    configSettings.set(3, "000");
	    configSettings.set(4, "000");
	    configSettings.set(5, "010");
	    configSettings.set(6, "000");
	    configSettings.set(7, "000");
	    configSettings.set(8, "000");
	    configSettings.set(9, "000");
		configSettings.set(10, "000");
		configSettings.set(11, "000");
		configSettings.set(12, "000");
		configSettings.set(13, "000");
		configSettings.set(14, "000");
		configSettings.set(15, "000");
		
		//SLEEPING IS HERE
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    try
	    {
	      //let's make a string to write to the file.
	      stringToWriteToFileInThread = "";
	      //String newLineCharacter = System.getProperty("line.separator");
	      for (int j = 0; j < configSettings.size(); j++)
   	      {
            //we don't want a new line at the end
            if (j == configSettings.size()-1)
		    {
              stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString();
		    }
		    else
		    {
		    	stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString() + '\r' + '\n';
		    }
	      }
	      FileWriter fw = new FileWriter(pathToConfigFile);
	      BufferedWriter bw = new BufferedWriter(fw);
	      bw.write(stringToWriteToFileInThread);
          bw.close();
	    }
	    catch (IOException ioe)
	    {
	      System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
	      ioe.printStackTrace();
	    }
	  }
    }
  }
  
  public class AllOverRippleThread implements Runnable
  {
	Vector<String> configSettings;
	String pathToConfigFile;
	String stringToWriteToFileInThread;
	
    public AllOverRippleThread(Vector<String> configSettingsIn, String pathToConfigFileIn) 
	{
      this.configSettings = configSettingsIn;
      this.pathToConfigFile = pathToConfigFileIn;
	}
	
    public void run() 
    {
	  while (true)
	  {
	    configSettings.set(0, "-10");
	    configSettings.set(1, "010");
	    configSettings.set(2, "-10");
	    configSettings.set(3, "010");
	    configSettings.set(4, "-10");
	    configSettings.set(5, "010");
	    configSettings.set(6, "-10");
	    configSettings.set(7, "010");
	    configSettings.set(8, "-10");
	    configSettings.set(9, "010");
		configSettings.set(10, "-10");
		configSettings.set(11, "010");
		configSettings.set(12, "-10");
		configSettings.set(13, "010");
		configSettings.set(14, "-10");
		configSettings.set(15, "010");
	  
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    try
	    {
	      //let's make a string to write to the file.
	      stringToWriteToFileInThread = "";
	      //String newLineCharacter = System.getProperty("line.separator");
	      for (int j = 0; j < configSettings.size(); j++)
   	      {
            //we don't want a new line at the end
            if (j == configSettings.size()-1)
		    {
		      stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString();
		    }
		    else
		    {
		      stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString() + '\r' + '\n';
		    }
	      }
	      FileWriter fw = new FileWriter(pathToConfigFile);
	      BufferedWriter bw = new BufferedWriter(fw);
	      bw.write(stringToWriteToFileInThread);
          bw.close();
	    }
	    catch (IOException ioe)
	    {
	      System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
	      ioe.printStackTrace();
	    }
	    
	    configSettings.set(0, "010");
	    configSettings.set(1, "-10");
	    configSettings.set(2, "010");
	    configSettings.set(3, "-10");
	    configSettings.set(4, "010");
	    configSettings.set(5, "-10");
	    configSettings.set(6, "010");
	    configSettings.set(7, "-10");
	    configSettings.set(8, "010");
	    configSettings.set(9, "-10");
		configSettings.set(10, "010");
		configSettings.set(11, "-10");
		configSettings.set(12, "010");
		configSettings.set(13, "-10");
		configSettings.set(14, "010");
		configSettings.set(15, "-10");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    try
	    {
	      //let's make a string to write to the file.
	      stringToWriteToFileInThread = "";
	      //String newLineCharacter = System.getProperty("line.separator");
	      for (int j = 0; j < configSettings.size(); j++)
   	      {
            //we don't want a new line at the end
            if (j == configSettings.size()-1)
		    {
              stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString();
		    }
		    else
		    {
		    	stringToWriteToFileInThread = stringToWriteToFileInThread + configSettings.elementAt(j).toString() + '\r' + '\n';
		    }
	      }
	      FileWriter fw = new FileWriter(pathToConfigFile);
	      BufferedWriter bw = new BufferedWriter(fw);
	      bw.write(stringToWriteToFileInThread);
          bw.close();
	    }
	    catch (IOException ioe)
	    {
	      System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
	      ioe.printStackTrace();
	    }
	  }
    }
  }
		
  public void keyReleased(KeyEvent keyTypedEvent) 
  {
	  
  }
		
  public void keyTyped(KeyEvent keyTypedEvent) 
  {
    
  }

  @Override
  public void mouseDragged(MouseEvent arg0) 
  {
	if (flatteningMode && currentlyFlattening)
	{
      flatteningScenario(arg0);
	}
	
	  if (medicalMode == true)
	  {
		  medicalScenario(arg0);
	  }
  }

  @Override
  public void mouseMoved(MouseEvent arg0)
  {

  }
  
  public void flatteningScenario(MouseEvent moveEvent)
  {
    if (flatteningMode)
    {
	  if (currentlyFlattening)
	  {
	    Vector<MagnetDimensionsContainer> rectangleLocations = instanceOfTouchEnabler.getRectangleLocations();
		Vector<String> configSettings = instanceOfTouchEnabler.getConfigSettings();
		String stringToWriteToFile;
		String pathToConfigFile = instanceOfTouchEnabler.getPath();
		if (rectangleLocations.size() > 0)
		{
		  //have to add the offset to these!
		  //hacky and not perfect at all!
		  int xMouseClick = moveEvent.getLocationOnScreen().x-10;
		  int yMouseClick = (moveEvent.getLocationOnScreen().y-30);
		  for (int i = 0; i < rectangleLocations.size(); i++)
		  {
		    if ((xMouseClick < rectangleLocations.elementAt(i).gettoprightx()) && (xMouseClick > rectangleLocations.elementAt(i).gettopleftx()))
			{
			  if ((yMouseClick < rectangleLocations.elementAt(i).getbottomlefty()) && yMouseClick > rectangleLocations.elementAt(i).gettoplefty())
			  {
				//here's where we can alter the magnet settings with our new value for one of the magnets, save to the file to be executed by the other program						  
			    String oldSetting = configSettings.elementAt(i);
				
			    if (lastMagnetChanged != i)
			    {
			      //if the old setting was -10, or 000 (starting value) AND the last magnet changed was equal to the same thing we want to change this to..
			      if ((oldSetting.equals("-10") || oldSetting.equals("000")) && !(lastMagnetChangedSetting.equals("-10")))
				  {
				    //set to on
				    configSettings.set(i, "010");
				    lastMagnetChangedSetting = "010";
				    System.out.println("TouchEnablerMouseAndKeyListener: changed magnet " + i + "to on");
				  }
				  else if ((oldSetting.equals("010")) && !(lastMagnetChangedSetting.equals("010")))
				  {
				    //set to off
				    configSettings.set(i, "-10");
				    lastMagnetChangedSetting = "-10";
				    System.out.println("TouchEnablerMouseAndKeyListener: changed magnet " + i + "to off");
				  }
				  else
				  {
					return;
				  }
						  
				  try
				  {
				    //let's make a string to write to the file.
				    stringToWriteToFile = "";
				    //String newLineCharacter = System.getProperty("line.separator");
				    for (int j = 0; j < configSettings.size(); j++)
				    {
				      //we don't want a new line at the end
					  if (j == configSettings.size()-1)
					  {
					    stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString();
					  }
					  else
					  {
					    stringToWriteToFile = stringToWriteToFile + configSettings.elementAt(j).toString() + '\r' + '\n';
					  }
				    }
				    FileWriter fw = new FileWriter(pathToConfigFile);
				    BufferedWriter bw = new BufferedWriter(fw);
				    bw.write(stringToWriteToFile);
				    bw.close();
				    lastMagnetChanged = i;
				  }
				  catch (IOException ioe)
				  {
				    System.out.println("TouchEnablerMouseAndKeyListener: Something went wrong when trying to update the config file");
				    ioe.printStackTrace();
				  }
			    }
			  }
			}
		  }
	    } 
	  }
	}
  }
}
