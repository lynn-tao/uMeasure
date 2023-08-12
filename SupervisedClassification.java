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

public class SupervisedClassificationMain {
   public static final Color DEFAULT_COLOR = new Color (0,0,0);
   public static final int SCREEN_HEIGHT = 950;//1200;
   public static final int SCREEN_WIDTH = 950;//1200;
   JFrame frame = null;
   ImageGui myApp = null;
   private JFrame parentFrame;
  
   public SupervisedClassificationMain(JFrame parentFrame) {
  	this.parentFrame = parentFrame;
   }
  
   public static void main(String[] args) {
  	SupervisedClassificationMain main = new SupervisedClassificationMain(null);
  	String imageName = "screenshot.png";
  	File imageFile = new File("/tmp/" + imageName);
  	main.launch(imageFile);
   }
  
   public void launch(File imageFile) {
  	BufferedImage img = null;
  	BufferedImage classifiedImg = null;
  	frame = new JFrame("Classification Image");
  	frame.addWindowListener(
     	new WindowAdapter(){
        	public void windowClosing(WindowEvent e) {
           	frame.setVisible(false);
           	if (myApp != null) myApp.setVisible(false);
           	if (parentFrame != null) parentFrame.setVisible(true);
        	}
     	});
  
  	try {
     	img = ImageIO.read(imageFile);
  	} catch (IOException e) {
     	e.printStackTrace();
     	frame.setVisible(false);;
  	}
  	classifiedImg = copyImage(img);
  
  	JMenuBar menuBar = new JMenuBar();
  	frame.setJMenuBar(menuBar);
  	JMenu menu = new JMenu("Displaying Classified Image  ");
  	// Add font info to the menu
  	menu.setFont(new java.awt.Font("sansserif", 1, 30));
  	menu.setOpaque(true);
  	//menu.setBackground(Color.CYAN);
  	menuBar.add(menu);
  	
  
  	//open another window for the classified image.
  	ImagePanel im = new ImagePanel(classifiedImg);
  
  	im.addMouseListener(im);
  	JScrollPane sp = new JScrollPane(im);
  	frame.add(sp);
  	frame.pack();
  	frame.setVisible(true);
  
   	// create an instance of my custom mouse cursor component
  	MouseMoveComponent alsXYMouseLabel = new MouseMoveComponent();
  	MouseMoveComponent alsXYMouseLabel1 = new MouseMoveComponent();
      alsXYMouseLabel.setFriend(alsXYMouseLabel1);
      alsXYMouseLabel1.setFriend(alsXYMouseLabel);
  	JLayeredPane layeredPane = sp.getRootPane().getLayeredPane();
  	layeredPane.add(alsXYMouseLabel, JLayeredPane.DRAG_LAYER);
  	alsXYMouseLabel.setBounds(0, 0, sp.getWidth(), sp.getHeight());
  	im.addMouseMotionListener(
     	new MouseMotionAdapter() {
        	public void mouseMoved(MouseEvent me)
        	{
           	alsXYMouseLabel.x = me.getX();
           	alsXYMouseLabel.y = me.getY();
           	alsXYMouseLabel.repaint();
               alsXYMouseLabel.repaintFriend(me.getX(), me.getY());
        	}
     	});
  
  	myApp = new ImageGui("Supervised Classification GUI Application ", img, classifiedImg, im,
        	alsXYMouseLabel1, frame, parentFrame);
  	myApp.setVisible(true);
  	myApp.addWindowListener(
     	new WindowAdapter(){
        	public void windowClosing(WindowEvent e) {
           	frame.setVisible(false);
           	myApp.setVisible(false);
        	}
     	});
  	
   }
   public BufferedImage copyImage(BufferedImage source){
  	BufferedImage img = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
  	Graphics graph = img.getGraphics();
  	graph.drawImage(source, 0, 0, null);
  	graph.dispose();
  	int dColor = DEFAULT_COLOR.getRGB();
  	for (int i = 0; i < img.getWidth(); i++) {
     	for (int j = 0; j < img.getHeight(); j++) {
        	int rgb = img.getRGB(i, j);
        	Color c = new Color(rgb);
        	int r = c.getRed();
        	int g = c.getGreen();
        	int b = c.getBlue();
        	int sum = r + g + b;
        	if (sum < 5)
           	continue;//remove pixels outside circle.
        	img.setRGB(i, j, dColor);
     	}
  	}
  
  	return img;
   }

   public void showClassificationImage() {
  	// TODO Auto-generated method stub
  	frame.setVisible(true);
  	//frame.setVisible(false);
  	JPanel pane =
     	new JPanel()
 	    {
        	@Override
        	protected void paintComponent(Graphics g)
        	{
           	try{
              	File initialImage = new File("/tmp/Classifiedimg.png");
              	BufferedImage image = ImageIO.read(initialImage);
              	g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);}
           	catch (IOException e) {
              	System.out.println("Exception occured :" + e.getMessage());
           	}
        	
        	}
     	};
     	
     	frame.add(pane);
   }

   public void runSupervisedClassification() {
      myApp.runSupervisedClassification();     
   }
}

class ImageGui extends JFrame implements ActionListener {
   private static final String START_SAMPLES = "Start Collecting Samples for a Training Set";
   private static final String STOP_SAMPLES = "Stop Collecting Samples for the Training Set";
   //private static final String CALCULATING_SURFACE_PERCENTAGE = "Calculate Impervious Surface Percentage";
   private static final String REMOVE_SAMPLE = "Remove an Impervious Surface Type";
   BufferedImage img;
   BufferedImage classifiedImg;
   ImagePanel classifiedImgPanel;
   MouseMoveComponent xyMouse;
  
   private ClassInfo currentClassInfo = null;

   private ImagePanel imgPanel = null;
   private Map<ClassInfo, List<Color>> samples = new LinkedHashMap<>();
   private JMenuItem startMenuItem = null;
   private JMenuItem stopMenuItem = null;
   private JMenuItem classifyMenuItem = null;
   private JMenuItem removeMenuItem = null;
   private JFrame frame;
   private JFrame parentFrame;
  
   public ImageGui(String title, BufferedImage img, BufferedImage classifiedImg, ImagePanel classifiedImgPanel,
     	MouseMoveComponent xyMouse, JFrame f, JFrame parentFrame) {
  	super(title);
  	this.frame = f;
  	this.parentFrame = parentFrame;
  	
  	addWindowListener(
     	new WindowAdapter(){
        	public void windowClosing(WindowEvent e) {
           	setVisible(false);
           	if (parentFrame != null) parentFrame.setVisible(true);
        	}
     	});
  
  	JMenuBar menuBar = new JMenuBar();
  	setJMenuBar(menuBar);
  	JMenu menu = new JMenu("Impervious Surface Supervised Classification Tool  ");
  	// Add font info to the menu
  	menu.setFont(new java.awt.Font("sansserif", 1, 30));
  	menu.setOpaque(true);
  	//menu.setBackground(Color.CYAN);
  	menuBar.add(menu);
  
  	startMenuItem = new JMenuItem(START_SAMPLES);
  	startMenuItem.addActionListener(this);
  	startMenuItem.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(startMenuItem);
  
  	stopMenuItem = new JMenuItem(STOP_SAMPLES);
  	stopMenuItem.setEnabled(false);
  	stopMenuItem.addActionListener(this);
  	stopMenuItem.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(stopMenuItem);
  
  	menu.addSeparator();
  
  	removeMenuItem = new JMenuItem(REMOVE_SAMPLE);
  	removeMenuItem.setEnabled(false);
  	removeMenuItem.addActionListener(this);
  	removeMenuItem.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(removeMenuItem);
  
  	menu.addSeparator();
  
  	JMenuItem quit = new JMenuItem("Exit Classification");
  	quit.addActionListener(this);
  	quit.setFont(new java.awt.Font("sansserif", 1, 22));
  	menu.add(quit);
  
  	this.img = img;
  	this.classifiedImg = classifiedImg;
  	this.classifiedImgPanel = classifiedImgPanel;
  	this.xyMouse = xyMouse;
  	
  	imgPanel = new ImagePanel(img);
      imgPanel.addMouseListener(imgPanel);
  	JScrollPane sp = new JScrollPane(imgPanel);
  	add(sp);
  	pack();
  	JLayeredPane layeredPane = sp.getRootPane().getLayeredPane();
      layeredPane.add(xyMouse, JLayeredPane.DRAG_LAYER);
  	xyMouse.setBounds(0, 0, sp.getWidth(), sp.getHeight());
  	xyMouse.setImagePanel(imgPanel);
  	imgPanel.addMouseMotionListener(
     	new MouseMotionAdapter() {
        	public void mouseMoved(MouseEvent me)
        	{
           	xyMouse.x = me.getX();
           	xyMouse.y = me.getY();
           	xyMouse.repaint();
               xyMouse.repaintFriend(me.getX(), me.getY());
        	}
     	});
   }
  
   public ImagePanel getImagePanel() {
  	return imgPanel;
   }
   public void actionPerformed(ActionEvent ae) {
  	String choice = ae.getActionCommand();
  	if (choice.equals("Exit Classification")) {
     	frame.setVisible(false);
     	try{
   	     ImageIO.write(classifiedImg, "png", new File("/tmp/Classifiedimg.png"));}
     	catch (IOException e) {
        	System.out.println("Exception occured :" + e.getMessage());
     	}
     	if (parentFrame != null) parentFrame.setVisible(true);
     	this.setVisible(false);
  	} else if (choice.equals(START_SAMPLES)) {
     	ClassInfo classInfo = getClassInfoFromDialog();
     	if (classInfo != null) {
        	currentClassInfo = classInfo;
        	//grey out the current menu
        	startMenuItem.setEnabled(false);
        	stopMenuItem.setEnabled(true);
            imgPanel.startCollectSample();
        	//classifyMenuItem.setEnabled(false);
     	}
  	} else if (choice.equals(STOP_SAMPLES)) {
     	String text="<html><span style='font-size:18'>Do you want to stop picking more samples for current type?</span></html>";
     	int selectedOption = JOptionPane.showConfirmDialog(null,
           	text,
           	"Choose",
               JOptionPane.YES_NO_OPTION);
     	if (selectedOption == JOptionPane.YES_OPTION) {
        	List<Color> sam = imgPanel.stopCollectingSample();
        	samples.put(currentClassInfo, sam);
        	startMenuItem.setEnabled(true);
        	stopMenuItem.setEnabled(false);
        	removeMenuItem.setEnabled(true);
            updateClassificationImage(currentClassInfo, sam, samples);
        	currentClassInfo = null;
        	if (samples.size() > 1) {
           	classifyMenuItem.setEnabled(true);
        	}
     	}
  	}  else if (choice.equals(REMOVE_SAMPLE)) {
     	List<String> ops = samples.keySet().stream().map(it -> it.getType()).collect(Collectors.toList());
     	ops.add(0, "Select");
     	JComboBox jcd = new JComboBox(ops.toArray());
      	
        	//The panel to display within the Dialog
        	JPanel jp = new JPanel();
        	jp.setLayout(new BorderLayout());   // Panel layout manager

        	// JLabel to hold the dialog text. HTML is used to add
        	JLabel jl = new JLabel(
                	"<html><font color=blue><b><span style='font-size:16'>Select the desired landuse type to remove    </span></font>:"
                	+ "</b><br><br></html>"); 
   	     // Desired font, style, and size for Message
        	Font font = new Font("Arial", Font.PLAIN, 14);
        	jl.setFont(font);	// Set the font to JLabel (the msg)
        	jp.add(jl, BorderLayout.NORTH); 	// Add JLabel to top of Dialog
        	jp.add(jcd, BorderLayout.SOUTH);	// Add JComboBox to bottom of Dialog

    	
     	/* Display the custom Input Box Dialog which is actually a
           	customized Confirm Dialog Box with the above JPanel supplied
           	as the message content.  Also, if the OK button was selected
           	then fill the valueSelected string variable declared above
           	with the Combo selection. 100% has been set as default.*/
         	JOptionPane.showConfirmDialog(this, jp, "Removal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
  	
     	
     	String selected = (String)jcd.getSelectedItem();
     	System.out.println("selected = " + selected);
     	if (selected!= null && !selected.equals("Select")) {
        	samples.entrySet()
           	.removeIf(
               	entry -> ( selected != null && selected.equals(entry.getKey().getType())));
            runSupervisedClassification(samples, false);
     	}
  	}
   }

   private void updateClassificationImage(ClassInfo classInfo, List<Color> sampleValues,
     	Map<ClassInfo, List<Color>> allSamples) {
  	// calculate mean and std standard deviation
  	double[][] rgbStats = getSampleStats(sampleValues);
  
  	//now go over the source image and try to classify the image and put the values
  	// classified image
  	int colorToSet = new Color((int)rgbStats[0][0], (int)rgbStats[1][0], (int)rgbStats[2][0]).getRGB();
  	for (int i = 0; i < img.getWidth(); i++) {
     	for (int j = 0; j < img.getHeight(); j++) {
        	int rgb = img.getRGB(i, j);
        	Color c = new Color(rgb);
        	if (isSimilarToSample(c, rgbStats)) {
           	classifiedImg.setRGB(i, j, colorToSet);
        	}
         }
  	}
  	classifiedImgPanel.repaint();
   }

   private boolean isOutside(Color c) {
  	int r = c.getRed();
  	int g = c.getGreen();
  	int b = c.getBlue();
  	int sum = r + g + b;
  	if (sum < 5)
     	return true;
  	return false;
   }
  
   //return true if the color is within 1 std above and below average on RGB
   private boolean isSimilarToSample(Color c, double[][] rgbStats) {
  	int multiple = 2;
  	double r = c.getRed();
  	double g = c.getGreen();
      double b = c.getBlue();
  	double sum = r + g + b;
  	if (sum < 5.0)
     	return false;//remove pixels outside circle.
  
  	return r >= rgbStats[0][0] - multiple * rgbStats[0][1] &&
        	r <= rgbStats[0][0] + multiple * rgbStats[0][1] &&
        	g >= rgbStats[1][0] - multiple * rgbStats[1][1] &&
        	g <= rgbStats[1][0] + multiple * rgbStats[1][1] &&
        	b >= rgbStats[2][0] - multiple * rgbStats[2][1] &&
	        b <= rgbStats[2][0] + multiple * rgbStats[2][1];
   }

   private double[][] getSampleStats(List<Color> sampleValues) {
  	double[][] rgb = new double[3][2];//keep mean and std for r, g, b
  	List<Double> red = new ArrayList<Double>();
  	List<Double> green = new ArrayList<Double>();
  	List<Double> blue = new ArrayList<Double>();
  	for (Color c: sampleValues) {
     	red.add((double)c.getRed());
     	green.add((double)c.getGreen());
     	blue.add((double)c.getBlue());
      }
  
  	//red
  	rgb[0][0] = calculateMean(red);
  	rgb[0][1] = calculateSD(red);
  	//green
  	rgb[1][0] = calculateMean(green);
  	rgb[1][1] = calculateSD(green);
  	//blue
  	rgb[2][0] = calculateMean(blue);
  	rgb[2][1] = calculateSD(blue);
  
  	return rgb;
   }
   private double calculateSD(List<Double> values)
   {
  	double sum = 0.0, standardDeviation = 0.0;
  	int length = values.size();
  	for(double num : values) {
     	sum += num;
  	}
  	double mean = sum/length;
  	for(double num: values) {
     	standardDeviation += Math.pow(num - mean, 2);
  	}
  	return Math.sqrt(standardDeviation/length);
   }

   private double calculateMean(List<Double> values)
   {
  	double sum = 0.0, standardDeviation = 0.0;
  	int length = values.size();
  	for(double num : values) {
     	sum += num;
  	}
  	return sum/length;
   }
   public void runSupervisedClassification() {
      runSupervisedClassification(samples, true);
   }
   private void runSupervisedClassification(Map<ClassInfo, List<Color>> samples, boolean showDiag) {
  	Map<ClassInfo, double[][]> stats = new HashMap<>();
  	//calculate stats for all sample types
  	for (ClassInfo ci: samples.keySet()) {
     	stats.put(ci, getSampleStats(samples.get(ci)));
  	}
  	long total = 0;
  	long imperviousTotal = 0;
  	for (int i = 0; i < img.getWidth(); i++) {
     	for (int j = 0; j < img.getHeight(); j++) {
        	int rgb = img.getRGB(i, j);
        	Color c = new Color(rgb);
        	if (isOutside(c))
           	continue;
        	//check against each sample type
        	total++;
        	int dColor = SupervisedClassificationMain.DEFAULT_COLOR.getRGB();
        	classifiedImg.setRGB(i, j, dColor);
        	boolean impervious = true;
        	for (double[][] rgbStats:stats.values()) {
           	int colorToSet = new Color((int)rgbStats[0][0], (int)rgbStats[1][0], (int)rgbStats[2][0]).getRGB();
           	if (isSimilarToSample(c, rgbStats)) {
              	classifiedImg.setRGB(i, j, colorToSet);
              	impervious = false;
              	break;
           	} 
        	}
        	if (impervious) imperviousTotal++;
     	}
  	}
  	//get percentage
  	double percentage = ((double)imperviousTotal)/total * 100;
  	System.out.println("Percentage of impervious surface type is: " + (100. - percentage));
  	classifiedImgPanel.repaint();
  	if (showDiag) {
         JOptionPane.showMessageDialog(classifiedImgPanel, "Percentage of impervious surface type is: " + (100. - percentage));
  	}
   }
   public ClassInfo getClassInfoFromDialog() {
  	JPanel pane = new JPanel();
  	pane.setLayout(new GridLayout(0, 1, 1, 1));
  
  	JTextField nameField = new JTextField(5);
  	pane.add(new JLabel("<html><span style='font-size:18'>Name of this surface type (e.g. road, building)?</span></html>"));
  	pane.add(nameField);
  
  	int option = JOptionPane.showConfirmDialog(null, pane, "Please fill all the fields", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
  	
  	if (option == JOptionPane.OK_OPTION) {
     	String nameInput = nameField.getText();
  	//       String imperviousInput = impervious.getText();
     	return new ClassInfo(nameInput, false);
  	} else {
     	return null;
  	}
   } 
}

class ClassInfo {
   private String type;
   private boolean impervious;

   public ClassInfo(String type, boolean impervious) {
  	super();
  	this.type = type;
  	this.impervious = impervious;
   }
   public String getType() {
  	return type;
   }
   public void setType(String type) {
  	this.type = type;
   }
   public boolean isImpervious() {
  	return impervious;
   }
   public void setImpervious(boolean impervious) {
  	this.impervious = impervious;
   }

}

class ImagePanel extends Component implements MouseListener, ActionListener {
   private BufferedImage img;
   private List<Color> samples = new ArrayList<>();
   private JPopupMenu popup = null;
   private boolean collecting = false;
   private double xscale = 1.0;
   private double yscale = 1.0;
   private static final String REMOVE_SAMPLE = "remove last sample point";
   private static final String CLEAR_SAMPLE_SET = "clear current sample set";
   public void paint(Graphics g) {
  	g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
   }

   public List<Color> stopCollectingSample() {
  	List<Color> ret = new ArrayList();
  	ret.addAll(samples);
  	samples.clear();
  	collecting = false;
  	return ret;
   }

   public void startCollectSample() {
  	samples = new ArrayList<>();
  	collecting = true;
   }

   public ImagePanel(BufferedImage img) {
  	this.img = img;
  	popup = new JPopupMenu();
  	JMenuItem menuItem = new JMenuItem(REMOVE_SAMPLE);
  	menuItem.addActionListener(this);
  	popup.add(menuItem);
  	menuItem = new JMenuItem(CLEAR_SAMPLE_SET);
  	menuItem.addActionListener(this);
  	popup.add(menuItem);
  	xscale = (double)img.getWidth()/SupervisedClassificationMain.SCREEN_WIDTH;
  	yscale = (double)img.getHeight()/SupervisedClassificationMain.SCREEN_HEIGHT;
  
   }

   public Dimension getPreferredSize() {
  	return new Dimension(SupervisedClassificationMain.SCREEN_WIDTH,SupervisedClassificationMain.SCREEN_HEIGHT);
   }

   @Override
   public void mouseClicked(MouseEvent e) {
  	if (!e.isPopupTrigger()) {
     	int x = (int)(e.getX() * xscale);
     	int y = (int) ((e.getY() - 40) * yscale);
     	if (y < 0) y = 0;
     	System.out.println("mouseClicked x=" + e.getX() + ", y=" + e.getY() + ",scaled x= " + x + ", y=" + y);
     	int rgb = img.getRGB(x, y);
     	Color c = new Color(rgb);
     	System.out.println("Color R=" + c.getRed() + ",G=" + c.getGreen() + ",B=" + c.getBlue());
     	if (collecting)
        	samples.add(c);
  	}
   }

   @Override
   public void mousePressed(MouseEvent e) {
  	maybeShowPopup(e);  	
   }

   @Override
   public void mouseReleased(MouseEvent e) {
  	maybeShowPopup(e);  	
   }

   @Override
   public void mouseEntered(MouseEvent e) {
  	e.isPopupTrigger();
   }

   @Override
   public void mouseExited(MouseEvent e) {
  	// TODO Auto-generated method stub
  
   }

   public int getSampleSize() {
  	return samples.size();
   } 
  
   private void maybeShowPopup(MouseEvent e) {
  	if (e.isPopupTrigger()) {
     	popup.show(e.getComponent(),
                   	e.getX(), e.getY());
  	}
   }

   @Override
   public void actionPerformed(ActionEvent ae) {
  	String choice = ae.getActionCommand();
  	if (choice.equals(REMOVE_SAMPLE)) {
     	if (samples.size() > 0)
        	samples.remove(samples.size() - 1);
  	} else if (choice.equals(CLEAR_SAMPLE_SET)) {
     	startCollectSample();
  	} 	
   }
}

class MouseMoveComponent extends JComponent
{
   public int x;
   public int y;
   private MouseMoveComponent friend;
   private ImagePanel imgPanel = null;
  
   public MouseMoveComponent() {
  	this.setBackground(Color.blue);
   }

   public void setImagePanel(ImagePanel imgPanel) {
  	this.imgPanel = imgPanel;
   }

   public void setFriend(MouseMoveComponent friend) {
  	this.friend = friend;  
   }
  
   public void repaintFriend(int x, int y) {
  	if (friend != null) {
     	friend.x = x;
     	friend.y = y;
     	friend.repaint();
  	}
   }
   // use the xy coordinates to update the mouse cursor text/label
   protected void paintComponent(Graphics g)
   {
  	super.paintComponent(g);
  	if (y >40) {
     	g.setColor(Color.white);
     	g.drawLine(x-20, y, x+20, y);
     	g.drawLine(x, y-20, x,y+20);
     	if (imgPanel != null) {
        	g.drawString("" + imgPanel.getSampleSize(), x + 20, y + 20);
        	
     	}
  	}
   }
}
 
