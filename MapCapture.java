import java.awt.*;  
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MapCapture implements ActionListener {
   private CaptureMouse mc = null;
   JFrame frame = null;
   private JFrame parentFrame;

   public MapCapture(JFrame parentFrame) {
  	this.parentFrame = parentFrame;
   }
   public static void main(String args[]) throws AWTException, IOException, InterruptedException {
  	MapCapture cap = new MapCapture(null);
  	cap.lauch();
   }

   public void lauch() {
  	frame = new JFrame();
  	frame.setSize(1200, 1200);
  	frame.addWindowListener(
     	new WindowAdapter()
     	{
        	public void windowClosing(WindowEvent e)
        	{
           	frame.setVisible(false);
           	if (parentFrame != null) parentFrame.setVisible(true);
        	}
     	});
  
  	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	JMenuBar menuBar = new JMenuBar();
  	frame.setJMenuBar(menuBar);
  	JMenu menu = new JMenu("Study Area Locating and Capturing Tool  ");
	  // Add font info to the menu
  	menu.setFont(new java.awt.Font("sansserif", 1, 30));
  	menu.setOpaque(true);
  	menuBar.add(menu);
  
  	JMenuItem startMenuItem = new JMenuItem("Start a Capture");
  	startMenuItem.addActionListener(this);
  	startMenuItem.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(startMenuItem);
  
  	JMenuItem stopMenuItem = new JMenuItem("Stop a Capture");
  	stopMenuItem.addActionListener(this);
  	stopMenuItem.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(stopMenuItem);
  
  	JFXPanel panel = new JFXPanel();
  	frame.setContentPane(panel);
  	frame.setVisible(true);
  	Platform.runLater(
     	() -> {
        	WebView webView = new WebView();
        	panel.setScene(new Scene(webView));
        	webView.getEngine().load("https://www.google.com/maps/@38.8941312,-77.3898596,4141m/data=!3m1!1e3");
     	});
  	RectComponent rc = new RectComponent();
  	JLayeredPane layeredPane = frame.getRootPane().getLayeredPane();
  	layeredPane.add(rc, JLayeredPane.DRAG_LAYER);
  	rc.setBounds(0, 0, frame.getWidth(), frame.getHeight());
  
  	mc = new  CaptureMouse(rc);
  	panel.addMouseListener(mc);
  	panel.addMouseMotionListener(mc);  
  
   }

   @Override
   public void actionPerformed(ActionEvent ae) {
  	String choice = ae.getActionCommand();
  	if (choice.equals("Start a Capture")) {
     	mc.startCapture();
  	}
  	else if (choice.equals("Stop a Capture")) {
     	frame.setVisible(false);
     	if (parentFrame != null) parentFrame.setVisible(true);
  	}
  
   }

   private static class CaptureMouse extends MouseAdapter {
  	private boolean started = false;
  	int startX, startY;
  	int endX, endY;
  	private RectComponent rc;
  	private boolean captureStarted;
  	public CaptureMouse(RectComponent rc) {
     	this.rc = rc;
  	}
  	public void startCapture() {
     	this.captureStarted = true;
  	}
  	public void mouseClicked(MouseEvent me)
  	{
     	System.out.println("x = " + me.getX() + ",y=" + me.getY());
     	if (captureStarted) {
        	if (!started) {
           	started = true;
           	startX = me.getX();
           	startY = me.getY();
           	rc.startX = startX;
           	rc.startY = startY + 40;
        	} else {
           	started = false;
           	endX = me.getX();
           	endY = me.getY();
           	rc.endX = endX;
           	rc.endY = endY + 40;
           	//capture(startX + 50, startY + 100, endX + 37, endY + 87);
           	capture(startX + 50, startY + 100, endX+10, endY+75);
        	}
     	}
  	}
	  public void mouseMoved(MouseEvent me){
     	if (started) {
        	rc.endX = me.getX();
        	rc.endY = me.getY() + 40;
        	rc.repaint();
     	} else {
        	rc.startX = rc.endX = rc.startY = rc.endX = 0;
     	}
  	}
  	private void capture(int startX, int startY, int endX, int endY) {
  	
     	Rectangle frameRectangle = new Rectangle(startX, startY, endX - startX, endY - startY);
     	BufferedImage image;
     	try {
        	image = new Robot().createScreenCapture(new Rectangle(frameRectangle));
        	ImageIO.write(image, "png", new File("/tmp/screenshot.png"));
     	} catch (AWTException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
     	} catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
     	}
  	} 	
   }


   private static class RectComponent extends JComponent
   {
  	public int startX;
  	public int startY;
  	public int endX;
  	public int endY;
  
  	public RectComponent() {
     	this.setBackground(Color.blue);
  	}
  
  	// use the xy coordinates to update the mouse cursor text/label
  	protected void paintComponent(Graphics g)
  	{
    	 super.paintComponent(g);
     	g.setColor(Color.white);
     	g.drawRect(startX, startY, endX - startX, endY - startY);
  	}
   }
}
