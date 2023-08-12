import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import java.awt.Color;

public class Calculate {
   private BufferedImage image;
   private JFrame frame;
   private JFrame parentFrame;
   private JPanel pane;

   public Calculate(JFrame parentFrame) {
  	this.parentFrame = parentFrame;
   }

   public static void main(String[] args) throws IOException {
  	Calculate cal = new Calculate(null);
  	cal.launch();
   }
	
   public JFrame buildFrame()
   {
  	frame = new JFrame();
  	//frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  	frame.setLocation(300, 0);
  	frame.setSize(1000, 1000);
  	frame.setVisible(true);
  	frame.setResizable(true);
  	frame.addWindowListener(
  	   new WindowAdapter()
     	{
        	public void windowClosing(WindowEvent e)
        	{
           	frame.setVisible(false);
           	if (parentFrame != null) parentFrame.setVisible(true);
        	}
     	});
  	return frame;
   }

   public void launch()
   {
  	frame = buildFrame();
  	
  	File initialImage = new File("/tmp/classifiedimg.png");
  	try{
     	image = ImageIO.read(initialImage);
     	drawImage();
     	double pt = calculatePercent();
     	if (pt >= 0) {
      	calculateEcologicalNumber(pt);
     	}
     	ImageIO.write(image, "png", new File("/tmp/ClassifiedColorImg.png"));
     	
  	}
  	catch (IOException e) {
     	System.out.println("Exception occured :" + e.getMessage());
  	}
  	
   }    
   public void drawImage()
   {   
  	pane =
     	new JPanel() {
        	@Override
        	protected void paintComponent(Graphics g)
        	{
           	super.paintComponent(g);
           	g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        	}
     	};
     	
  	frame.add(pane); 
   }    
 	
   public double calculatePercent()
   {
  	String text = "<html><font color=blue><b><span style='font-size:16'> Are you ready to calculate the percentage of the impervious surfaces now?   </span></font>"
                         	+ "</b><br><br></html>"; 
     	
  	int selectedOption = JOptionPane.showConfirmDialog(null, text,     	    
                    	"Choose",
                        JOptionPane.YES_NO_OPTION);
  	int imperv = 0;
  	int total = 0;
  	double percentage = -1.0;
  	if (selectedOption == JOptionPane.YES_OPTION) {
            	
     	for ( int i = 0; i < image.getWidth(); i++ ) {
        	for ( int j = 0; j < image.getHeight(); j++ ) {
           	int rgb = image.getRGB(i, j);
           	if (rgb != Color.BLACK.getRGB()) {
                        	// Set the pixel colour of the image n.b. x = cc, y = rc
              	image.setRGB(i, j, Color.RED.getRGB() );
              	imperv++;
           	
           	} else {
              	image.setRGB(i, j, Color.cyan.getRGB() );
           	}
           	total++;
                    	
        	}//for cols
     	}//for rows
     	percentage = ((double)imperv)/total * 100;
     	System.out.println(""+imperv);
     	System.out.println(""+total);
     	pane.repaint();
     	System.out.println("Percentage of impervious surface type is: " + percentage);
     	String text1 = "<html><font color=blue><b><span style='font-size:16'> Percentage of impervious surface type is: " +  percentage +   "</span></font>"
                            	+ "</b><br><br></html>";
         JOptionPane.showMessageDialog(pane, text1);
  	}
  	
  	return percentage;
  	
   }
  
   public void calculateEcologicalNumber(double pct)
   {
  	String text = "<html><font color=blue><b><span style='font-size:16'> Do You Want to Calculate the Ecological Number of the Local Stream?  </span></font>"
                         	+ "</b><br><br></html>"; 
     	
  	int selectedOption = JOptionPane.showConfirmDialog(null, text,            
              	      "Choose",
                        JOptionPane.YES_NO_OPTION);
  	if (selectedOption == JOptionPane.YES_OPTION) {
    	double en = -0.110242 * pct + 12.07060;
      	System.out.println("Ecological Number of the Local Stream is: " + en);
          String text1 = "<html><font color=blue><b><span style='font-size:16'> Ecological Number of the Local Stream is: " + en +   "</span></font>"
                             	+ "</b><br><br></html>";
          JOptionPane.showMessageDialog(pane, text1);
  	}
   }
} 


