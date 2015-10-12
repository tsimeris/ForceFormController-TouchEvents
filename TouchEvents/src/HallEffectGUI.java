import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.swing.*;

/**
 * HallEffectGUI is a simple program to display a moving gauge when the user presses down on the surface of ForceForm.
 * It also outputs a log to output.txt
 * ForceForm is a dynamically deformable interactive surface developed at the Australian National University
 * This program reads a Hall effect sensor value outputted to the serial port by the HallEffectSensor-Arduino repo available at https://github.com/tsimeris
 * This program assumes that you have fitted the Hall effect sensor beneath the magnetic surface and above the grid of computer controlled electromagnets.
 *  
 * @author Jess
 *
 */

public class HallEffectGUI 
{
  float currentHallEffectValue = 0;
  float maxPossibleHallEffectValue = 849;
  float minPossibleHallEffectValue = 612;
  float maxSubtractMin = maxPossibleHallEffectValue - minPossibleHallEffectValue;
  float oldHallEffectValue = 0;
  boolean foundRedLine = false;
  boolean returnedToTop = false;
  boolean taskSolved = true;
  boolean valueAccepted;
  int randomRedLineVal;
  int flippedIntLongwaysValue;
  int upperCounter = 0;
  int lowerCounter = 0;
  int redLineLevel = 0;
  
  BufferedWriter bufferedWrite;
  FileWriter fileWrite;
  String dateString;
  BufferedReader userStudySetReader;
  
  JFrame hallEffectFrame;
  
  public void init()
  {
	try 
	{
      //TODO - I have included a sample User Study Sets File but you should replace it as needed.
	  //The file is read in and each number becomes a task - the setting of the red line that the user must press down to.
	  userStudySetReader = new BufferedReader (new FileReader("UserStudySets.txt"));
	} 
	catch (FileNotFoundException e1) 
	{
	  System.out.println("HallEffectGUI: A File Not Found Exception has occured. Fix the user study sets file.");
	  //e1.printStackTrace();
	}
	
    hallEffectFrame = new JFrame("Hall Effect Sensor"); //make the second frame to monitor the hall effect sensor
    hallEffectFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    JComponent contentPane = new MyComponent();
    contentPane.setOpaque(true);
    contentPane.setSize(new Dimension(500,500));
    hallEffectFrame.setContentPane(contentPane);
    hallEffectFrame.pack();
    hallEffectFrame.setVisible(true);
    currentHallEffectValue = 0;
    
	try 
	{
      fileWrite = new FileWriter("Output.txt");
	  bufferedWrite = new BufferedWriter(fileWrite);
	} 
	catch (IOException e) 
	{
      System.out.println("HallEffectGUI: There was an error opening the file writer");
	  e.printStackTrace();
	}
    
    SerialPortReader spr = new SerialPortReader();
  }
  
  
  /*
   * class to open the socket and read the pushing down values
   *
   */
  public class SerialPortReader implements SerialPortEventListener
  {
    float longwaysValue;
    float tempValue;
    CommPortIdentifier portId;
	Enumeration portList;
	SerialPort serialPort;
	BufferedReader bufferedReader;
	
	public SerialPortReader()
	{
      try
      {
    	portList = CommPortIdentifier.getPortIdentifiers();
    	while (portList.hasMoreElements())
    	{
    	  portId = (CommPortIdentifier) portList.nextElement();
    	  if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
    	  {
    		if (portId.getName().equals("COM3"))
    		{
    	      System.out.println("HallEffectGUI: SerialPortReader: We have found COM3");
    	      //the open() method takes a name (arbitrary) and a timeout in milliseconds
    	      serialPort = (SerialPort) portId.open("TouchEnabler", 1000);
    	      //settings for the serial port connection
    	      serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    	      serialPort.addEventListener(this);
    	      serialPort.notifyOnDataAvailable(true);
    	      bufferedReader = new BufferedReader (new InputStreamReader(serialPort.getInputStream()));
    		}
    	  }
    	}
      }
      catch (Exception e)
      {
    	System.out.println("HallEffectGUI: SerialPortReader: An exception has occurred");
    	System.out.println(e);
      }
	}
	
    @Override
    public void serialEvent(SerialPortEvent event)
    {
      String data;
      switch (event.getEventType())
      {
        case SerialPortEvent.DATA_AVAILABLE:
          try 
          {
            while (((data = bufferedReader.readLine()) != null)) 
            {
              int indexOfEquals = data.indexOf('=');
              
              String numberOnly = data.substring(0, indexOfEquals);
              
              currentHallEffectValue = Integer.parseInt(numberOnly);
              
              if (currentHallEffectValue != oldHallEffectValue)
              {
            	System.out.println("HallEffectGUI: Hall effect value is " + currentHallEffectValue);
                
            	tempValue = currentHallEffectValue - minPossibleHallEffectValue;
            	longwaysValue = (tempValue/maxSubtractMin)*100;
            	flippedIntLongwaysValue = (int)(100-longwaysValue);
            	
              
                MyComponent myComp = (MyComponent)hallEffectFrame.getContentPane();
                
                //This is where we ask the new hall effect value to be drawn
                myComp.drawBlackLine(20, (int)(flippedIntLongwaysValue+20), 95, (int)(flippedIntLongwaysValue+20));
                
                oldHallEffectValue = currentHallEffectValue;
                
                //check if we have reached the red line
                if ((flippedIntLongwaysValue == randomRedLineVal) || 
                    (flippedIntLongwaysValue == randomRedLineVal+1) ||
                    (flippedIntLongwaysValue == randomRedLineVal-1))
                {
                  foundRedLine = true;
                  myComp.resetCanvas();
                }
                
                if (foundRedLine)
                {
                  if ((flippedIntLongwaysValue == 0) || (flippedIntLongwaysValue == 1))
                  //then we have returned back home
                  returnedToTop = true;
                }
                
                if (foundRedLine && returnedToTop)
                {
              	  taskSolved = true;
              	  bufferedWrite.newLine();
              	  bufferedWrite.newLine();
            	  bufferedWrite.newLine(); //when the task is finished, put some spaces.
                }
                
                //This section is commented out as it randomly generates the red line of the UI gauge.
                //Feel free to uncomment if you would like to implement this functionality rather than reading in from a file.
                /**if (taskSolved)
                {
              	  //if the task is solved, let's make another
                  taskSolved = false;
                  //working out where the red line should be
                  
                  if ((lowerCounter - upperCounter) >= 2) //lower is ahead
                  {
                	randomRedLineVal = (int)(Math.random() * (100 - 51)) + 51;
                	upperCounter++;
                  }
                  else if ((upperCounter - lowerCounter) >= 2) //upper is ahead
                  {
                	randomRedLineVal = (int)(Math.random() * (50 - 1)) + 1;
                	lowerCounter++;
                  }
                  else 
                  {
                	randomRedLineVal = (int)(Math.random() * (100 - 51)) + 51;
                  	upperCounter++;
                  }
                  
                  //randomRedLineVal = (int)(Math.random() * (100 - 1)) + 1;
                  foundRedLine = false;
                  returnedToTop = false;
                }
                **/
                
                //new code to work out the red line -- NEW CODE
                if (taskSolved)
                {
                  //if the task is solved, let's make another
                  randomRedLineVal = Integer.parseInt(userStudySetReader.readLine());
                  taskSolved = false;
                  foundRedLine = false;
                  returnedToTop = false;
                }
                //draw the red line
                //the 95 is 20 plus 75 width
                myComp.drawRedLine(20, (randomRedLineVal+20), 95, (randomRedLineVal+20));
                //note: the red line won't be drawn in the paint method if the task is solved
              }
              
              //what do we print to the file
              if (!taskSolved && currentHallEffectValue != 0)
              {
            	 //boolean for task beginning.
            	 String toWriteToFile = Integer.toString((int)currentHallEffectValue) + '\n';
            	 bufferedWrite.write("Looking for: " + randomRedLineVal + " and the black line value is " + flippedIntLongwaysValue + ". Max HE: " + maxPossibleHallEffectValue + " and min HE: " + minPossibleHallEffectValue);
            	 bufferedWrite.newLine();
            	 bufferedWrite.write(String.valueOf(System.currentTimeMillis())); //writes the time in milliseconds. There are websites that can decode this.
            	 bufferedWrite.newLine();
            	 bufferedWrite.write(toWriteToFile);
            	 bufferedWrite.newLine();
            	 bufferedWrite.flush();
              }
            }
          } catch (Exception ex)
          {
        	System.out.println("HallEffectGUI: An exception has occurred when outputting a log file.");
            System.out.println(ex);
          }
        break;
      }
    }
  }
 
class MyComponent extends JComponent
  {
	int boxToDrawX;
	int boxToDrawY;
	int boxToDrawWidth;
	int boxToDrawHeight;
    int blackLineToDrawx1;
    int blackLineToDrawy1;
    int blackLineToDrawx2;
    int blackLineToDrawy2;
    int redLineToDrawx1;
    int redLineToDrawy1;
    int redLineToDrawx2;
    int redLineToDrawy2;
    Graphics2D g2;
	  
    public void paint(Graphics g) 
    { 
      g2 = (Graphics2D) g;
      g2.scale(4.0, 4.0);

      //reset canvas
      this.resetCanvas();
      
      //draw the box outline again
      g2.setColor(Color.black);
      //g2.setStroke(new BasicStroke(1));
      g2.drawRect(20, 20, 75, 100);
      
      //draw current line rectangle - from the current hall effect value
      //g2.setStroke(new BasicStroke(3));
      //g2.drawRect(boxToDrawX, boxToDrawY, boxToDrawWidth, boxToDrawHeight); //i forgot to replace this
      g2.drawLine(blackLineToDrawx1, blackLineToDrawy1, blackLineToDrawx2, blackLineToDrawy2);
  
      //The rest of the method is checking whether to draw the random line, and what colour to draw it
      
      //if we have found the red line and haven't yet returned to the top, make the line gray
      if ((foundRedLine) && (!returnedToTop))
      {
        //drawing a gray line
        g2.setColor(Color.gray);
        g2.draw(new Line2D.Double(redLineToDrawx1, redLineToDrawy1, redLineToDrawx2, redLineToDrawy2));
        g2.setColor(Color.black);
      }
      //if neither the red line has been found, nor we have returned to the top, then continue to draw the red line
      else if (!foundRedLine && !returnedToTop)
      {
        //drawing a red line
        g2.setColor(Color.red);
        g2.draw(new Line2D.Double(redLineToDrawx1, redLineToDrawy1, redLineToDrawx2, redLineToDrawy2));
        g2.setColor(Color.black);
      }
      
    }
    
    public void drawBlackLine(int x1, int y1, int x2, int y2)
    {
      blackLineToDrawx1 = x1;
      blackLineToDrawy1 = y1;
      blackLineToDrawx2 = x2;
      blackLineToDrawy2 = y2;
      repaint();
    }
    
    public void drawRedLine(int x1, int y1, int x2, int y2)
    {
      redLineToDrawx1 = x1;
      redLineToDrawy1 = y1;
      redLineToDrawx2 = x2;
      redLineToDrawy2 = y2;
      repaint();
    }
    
    public void resetCanvas()
    {
      //reset the canvas
      g2.setColor(getBackground());
      g2.fillRect (0, 0, getWidth(), getHeight());
    }
  } 
}
