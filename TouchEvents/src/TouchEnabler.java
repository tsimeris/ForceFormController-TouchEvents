
public class TouchEnabler 
{ 
  public static void main(String[] args)
  {
    //run the two UIs
	TouchEnablerGUI teg = new TouchEnablerGUI();
    teg.init();
    
    HallEffectGUI heg = new HallEffectGUI();
    heg.init();
  }
}
