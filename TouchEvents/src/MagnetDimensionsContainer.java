
public class MagnetDimensionsContainer 
{
  double topleftx;
  double toplefty;
  double toprightx;
  double toprighty;
  double bottomleftx;
  double bottomlefty;
  double bottomrightx;
  double bottomrighty;
  
  public MagnetDimensionsContainer(double knownTopLeftx, double knownTopLefty, double xSpacing, double ySpacing)
  {
	  this.topleftx = knownTopLeftx;
	  this.toplefty = knownTopLefty;
	  this.toprightx = knownTopLeftx + xSpacing;
	  this.toprighty = knownTopLefty;
	  this.bottomleftx = knownTopLeftx;
	  this.bottomlefty = knownTopLefty + ySpacing;
	  this.bottomrightx = knownTopLeftx + xSpacing;
	  this.bottomrighty = knownTopLefty + ySpacing;
  }
  
  public double gettopleftx()
  {
	return topleftx;
  }
  
  public double gettoplefty()
  {
	return toplefty;
  }
  
  public double gettoprightx()
  {
	return toprightx;
  }
  
  public double gettoprighty()
  {
	return toprighty;
  }
  
  public double getbottomleftx()
  {
	return bottomleftx;
  }
  
  public double getbottomlefty()
  {
	return bottomlefty;
  }
  
  public double getbottomrightx()
  {
	return bottomrightx;
  }
  
  public double getbottomrighty()
  {
	return bottomrighty;
  }
}
