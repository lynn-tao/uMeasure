import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

public class DesignPanel extends JPanel
{

   private JFrame parentFrame;  // from later
   private SupervisedClassificationMain scm;  // from later

   public DesignPanel(JFrame parentFrame)
   {
  	this.parentFrame = parentFrame;// from later
  	
  	setLayout(new BorderLayout());  
  	scm = new SupervisedClassificationMain(parentFrame); // from later
  
  	//for buttons
  	JPanel sub = new JPanel();
  	sub.setLayout(new GridLayout(1,3));  // from later
  	sub.setBackground(new Color(135, 206, 235));
  	add(sub, BorderLayout.SOUTH);
  	
  	JButton button1 = new JButton("Locate AOI");
  	button1.setFont(new Font("Serif", Font.BOLD+Font.ITALIC, 40));
  	button1.setPreferredSize(new Dimension(150, 75));
  	button1.setForeground(new Color(0, 0, 0));
  	button1.addActionListener(new Listener1(parentFrame)); // from later
  	sub.add(button1);
  
  	JButton button2 = new JButton("Classification");
  	button2.setPreferredSize(new Dimension(150, 75));
  	button2.setFont(new Font("Serif", Font.BOLD+Font.ITALIC, 40));
  	button2.setForeground(new Color(0, 0, 0));
  	button2.addActionListener(new Listener2(parentFrame, scm));// from later
  	sub.add(button2);
  	
  	JButton button3 = new JButton("Calculate"); // from later
  	button3.setPreferredSize(new Dimension(150, 75));
  	button3.setFont(new Font("Serif", Font.BOLD+Font.ITALIC, 40));
  	button3.setForeground(new Color(0, 0, 0));
  	button3.addActionListener(new Listener3(parentFrame, scm)); // from later
  	sub.add(button3);
   }
  
   private class Listener1 implements ActionListener
   {
  	private JFrame parentFrame; // from later
  	public Listener1(JFrame parentFrame) {
     	this.parentFrame = parentFrame;
  	}  // from later
  	public void actionPerformed(ActionEvent e)
  	{
     	MapCapture cap = new MapCapture(parentFrame);
     	parentFrame.setVisible(false);
     	cap.lauch();
     	
  	}
   }
   private class Listener2 implements ActionListener
   {
  	private JFrame parentFrame; // from later
  	private SupervisedClassificationMain scm; // from later
  	
  	public Listener2(JFrame parentFrame, SupervisedClassificationMain scm) {
     	this.parentFrame = parentFrame;
     	this.scm = scm;
  	}  // from later
  	
  	public void actionPerformed(ActionEvent e)
  	{
  	//   SupervisedClassificationMain main = new SupervisedClassificationMain();
     	File imageFile = new File("/tmp/screenshot.png"); // from later
     	parentFrame.setVisible(false); // from later
     	scm.launch(imageFile); // from later
        	
    	}
   }
  
   private class Listener3 implements ActionListener
   {
  	private JFrame parentFrame;  // from later
  	private SupervisedClassificationMain scm;  // from later
  
  	public Listener3(JFrame parentFrame, SupervisedClassificationMain scm) {
     	this.parentFrame = parentFrame;
  	//       this.scm = scm;
  	} // from later
  	
  	public void actionPerformed(ActionEvent e)
  	{
     	Calculate cal = new Calculate(parentFrame);
     	parentFrame.setVisible(false);
     	cal.launch();
  	}
   }
   @Override
   public void paintComponent(Graphics g)
   {
  	super.paintComponent(g);
  	ImageIcon img = new ImageIcon("/tmp/uMeasurecover.png");
      g.drawImage(img.getImage(), 0, 0, this.getWidth(), this.getHeight() - 50, null);
   }
    }