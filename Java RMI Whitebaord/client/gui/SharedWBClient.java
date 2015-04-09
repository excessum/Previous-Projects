package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JCheckBox;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JToggleButton;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import remote.interfaces.WhiteBoard;
import remote.interfaces.WhiteBoardHolder;
import remote.objects.ClientCallbackObject;
import client.DrawingActionListener;

public class SharedWBClient {
	public static final Dimension buttonSize = new Dimension(30, 30);
	private JFrame frame;
	private ButtonGroup graphicalObjectChoice = new ButtonGroup();
	private DrawingPanel drawingPanel;
	private JButton btnLineColorChooser;
	private JButton btnFillColorChooser;
	private JSpinner spinnerStrokeSize;
	private JCheckBox chckbxFillShape;
	private WhiteBoardHolder wbLists;
	private String wbKey = null;
	private WhiteBoard currentWb;
	private String hostName;
	private int portNumber;
	private ClientCallbackObject callBackObject;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new SharedWBClient();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Window adapter action listener that detects whenever a whiteboard is closed to deregister callback
	 */
	WindowAdapter windowAdapterOnDispose = new WindowAdapter()
	{
	    public void windowClosing(WindowEvent e)
	    {
	    		removeCallback();
	    		((Window) e.getSource()).dispose();
	    }
	    

		private void removeCallback() {
			try{
				currentWb.deregister(callBackObject);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Create/open a shared whiteboard with a new host (including first run)
	 */
	public SharedWBClient() {
		try {
			connectToServer();
			connectToRMI();
			chooseWhiteboard();
			setUpCallback();
		} catch (RemoteException e) {
			if(e instanceof ConnectException){
				JOptionPane.showMessageDialog(null, "No server could be found, please ensure the server you are trying to connect to is avaliable");
			}else{
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

		frame.addWindowListener(windowAdapterOnDispose);
	}
	
	/**
	 * Create/open a shared whiteboard on the same host
	 * @param hostName
	 * @param portNumber
	 */
	public SharedWBClient(String hostName, int portNumber) {
		this.hostName = hostName; 
		this.portNumber = portNumber;
		try {
			connectToRMI();
			chooseWhiteboard();
			setUpCallback();
		} catch (RemoteException e) {
			if(e instanceof ConnectException){
				JOptionPane.showMessageDialog(null, "No server could be found, please ensure the server you are trying to connect to is avaliable");
			}else{
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}	
		
		frame.addWindowListener(windowAdapterOnDispose);
	}
	
	/**
	 * Connect to the RMI server specified by the user, defaulting to local ipv4 address with port 1099 if nothing is specified
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 * @throws UnknownHostException
	 */
	private void connectToServer() throws RemoteException,
			MalformedURLException, NotBoundException, UnknownHostException {
		hostName = JOptionPane
				.showInputDialog("Enter a host name to connect to (default: localipv4)");
		if (hostName.equalsIgnoreCase("") || hostName == null) {

			hostName = Inet4Address.getLocalHost().getHostAddress();

		}

		String portInput = JOptionPane
				.showInputDialog("Enter a port to connect to (default: 1099)");
		if (portInput.equalsIgnoreCase("")) {
			portNumber = 1099;
		} else {
			portNumber = Integer.parseInt(portInput);
		}
	}
	
	/**
	 * Connect to the host specified in String hostName and int portNumber of this class and get a list of whiteboards
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void connectToRMI() throws RemoteException, MalformedURLException,
			NotBoundException {
		String registry = "rmi://" + hostName + ":" + portNumber
				+ "/WhiteBoard";
		wbLists = (WhiteBoardHolder) Naming.lookup(registry);
	}

	
	/**
	 * Create a JOptionPane requiring user input to either create or join an existing whiteboard based on a String which acts as a URL. If no input
	 * is supplied a whiteboard is auto generated for the user.
	 * @throws RemoteException
	 */
	private void chooseWhiteboard() throws RemoteException {
		WhiteboardChooserPanel wbc = new WhiteboardChooserPanel(wbLists);
		int result = JOptionPane.showConfirmDialog(null, wbc,
				"Please choose a whiteboard", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			wbKey = wbc.getKey();
		}
		boolean isPublic = wbc.isPublic();
		currentWb = wbLists.getWhiteboard(wbKey, isPublic);

		initialize();
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setTitle("Shared whiteboard");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Method used to register for callback and creating a background to redraw when a callback is detected
	 * @throws RemoteException
	 */
	private void setUpCallback() throws RemoteException {
		callBackObject = new ClientCallbackObject(drawingPanel);
		currentWb.register(callBackObject);
		/**
		TimerTask updateThread = new TimerTask() {
			@Override
			public void run() {
				if (currentVersion < callBackObject.getLatestVersion()) {
					drawingPanel.repaint();
				}
			}
		};
		

		timer = new Timer();
		timer.schedule(updateThread, 0l, 500l); // call every half second	
		*/
	}
	
	/**
	 * ActionListener used with the colour picking JButtons to choose a new colour
	 */
	private ActionListener colorActionListner = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			((JButton) arg0.getSource())
					.setBackground(chooseColor(((JButton) arg0.getSource())
							.getBackground()));
		}
	};
	

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		// Set up JFrame
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Setup menu bar and all its items
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem mntmOpenWhiteboard = new JMenuItem("Open whiteboard");
		mnFile.add(mntmOpenWhiteboard);

		JMenuItem mntmClearWhiteboard = new JMenuItem("Clear whiteboard");
		mnFile.add(mntmClearWhiteboard);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmSaveBoard = new JMenuItem("Save image");
		mnFile.add(mntmSaveBoard);

		JSeparator sep2 = new JSeparator();
		mnFile.add(sep2);

		JMenuItem mntmWhiteboardDetails = new JMenuItem("Whiteboard Details");
		mnFile.add(mntmWhiteboardDetails);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem mntmServerConfig = new JMenuItem("RMI Server settings");
		mnSettings.add(mntmServerConfig);

		// Setup the top toolBar and all its items
		JToolBar toolBar = new JToolBar();
		FlowLayout fl_toolBar = new FlowLayout();
		fl_toolBar.setAlignment(FlowLayout.LEFT);
		toolBar.setLayout(fl_toolBar);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		JLabel lblStrokeSize = new JLabel("Stroke size");
		lblStrokeSize.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolBar.add(lblStrokeSize);
		spinnerStrokeSize = new JSpinner(
				new SpinnerNumberModel(1, 0.5, 32, 0.5));
		spinnerStrokeSize.setEditor(new JSpinner.DefaultEditor(
				spinnerStrokeSize));
		// spinnerStrokeSize.setBorder(new EmptyBorder(5, 5, 5, 5));
		spinnerStrokeSize.setPreferredSize(new Dimension(40, 25));
		toolBar.add(spinnerStrokeSize);

		JLabel lblLineColor = new JLabel("Line color");
		lblLineColor.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolBar.add(lblLineColor);
		btnLineColorChooser = new JButton("");
		btnLineColorChooser.setPreferredSize(new Dimension(30, 30));
		btnLineColorChooser.setBackground(Color.BLACK);
		btnLineColorChooser.addActionListener(colorActionListner);
		toolBar.add(btnLineColorChooser);

		chckbxFillShape = new JCheckBox("Fill shape");
		chckbxFillShape.setHorizontalTextPosition(SwingConstants.LEFT);
		toolBar.add(chckbxFillShape);

		JLabel lblFillColor = new JLabel("Fill color");
		lblFillColor.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolBar.add(lblFillColor);
		btnFillColorChooser = new JButton("");
		btnFillColorChooser.setPreferredSize(new Dimension(30, 30));
		btnFillColorChooser.setBackground(Color.RED);
		btnFillColorChooser.addActionListener(colorActionListner);
		toolBar.add(btnFillColorChooser);

		// Set up the splitPane to provide a toolBar and working area
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		// Create the graphicalObject choice toolBar and all it's toggleButtons, adding it to the left splitPane
		JToolBar toolBarPaintOption = new JToolBar();
		toolBarPaintOption.setOrientation(SwingConstants.VERTICAL);
		toolBarPaintOption.setFloatable(false);
		splitPane.setLeftComponent(toolBarPaintOption);

		JToggleButton toggleButtonPencil = new JToggleButton("");
		toggleButtonPencil.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Pencil.png")));
		toggleButtonPencil.setActionCommand("pencil");
		toggleButtonPencil.setSelected(true);
		graphicalObjectChoice.add(toggleButtonPencil);
		toolBarPaintOption.add(toggleButtonPencil);
		
		JToggleButton toggleButtonCircle = new JToggleButton("");
		toggleButtonCircle.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Circle.png")));
		
		toggleButtonCircle.setActionCommand("circle");
		graphicalObjectChoice.add(toggleButtonCircle);
		toolBarPaintOption.add(toggleButtonCircle);

		JToggleButton toggleButtonEllipse = new JToggleButton("");
		toggleButtonEllipse.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Ellipse.png")));
		toggleButtonEllipse.setActionCommand("ellipse");
		graphicalObjectChoice.add(toggleButtonEllipse);
		toolBarPaintOption.add(toggleButtonEllipse);

		JToggleButton toggleButtonLine = new JToggleButton("");
		toggleButtonLine.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Line.png")));
		toggleButtonLine.setActionCommand("line");
		graphicalObjectChoice.add(toggleButtonLine);
		toolBarPaintOption.add(toggleButtonLine);

		JToggleButton toggleButtonRectangle = new JToggleButton("");
		toggleButtonRectangle.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Rectangle.png")));
		toggleButtonRectangle.setActionCommand("rectangle");
		graphicalObjectChoice.add(toggleButtonRectangle);
		toolBarPaintOption.add(toggleButtonRectangle);

		JToggleButton toggleButtonSquare = new JToggleButton("");
		toggleButtonSquare.setIcon(new ImageIcon(SharedWBClient.class
				.getResource("/images/Square.png")));
		toggleButtonSquare.setActionCommand("square");
		graphicalObjectChoice.add(toggleButtonSquare);
		toolBarPaintOption.add(toggleButtonSquare);
		
		JToggleButton toggleButtonPoly = new JToggleButton("");
		toggleButtonPoly.setIcon(new ImageIcon(SharedWBClient.class.getResource("/images/Polygon.png")));
		toggleButtonPoly.setActionCommand("poly");
		graphicalObjectChoice.add(toggleButtonPoly);
		toolBarPaintOption.add(toggleButtonPoly);

		

		//Create a drawingPanel(Custom JComponent) and add it to the right panel
		drawingPanel = new DrawingPanel(currentWb);
		MouseListener drawingListener = new DrawingActionListener(this);
		drawingPanel.addMouseListener(drawingListener);
		drawingPanel
				.addMouseMotionListener((MouseMotionListener) drawingListener);
		splitPane.setRightComponent(drawingPanel);

		/*
		 * Use anonymous class action listener for the different menu buttons to perform their desired actions
		 */
		
		//Server details menu item clicked, display current host:port and offer to change. If change requested will then soft restart the application. 
		mntmServerConfig.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane.showConfirmDialog(frame,
						"Your current host:port is : \n" + hostName + ":"
								+ portNumber
								+ "\n\nWould you like to change host/port?",
						"Copy to clipboard?", JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					new SharedWBClient();
					frame.dispose();
				}
			}

		});
		
		//Whiteboard details menu item clicked, show current whiteboard URL and offer to copy to clipboard
		mntmWhiteboardDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane
						.showConfirmDialog(
								frame,
								"Your whiteboard key is : \n"
										+ wbKey
										+ "\n\nWould you like to copy it to clipboard?",
								"Copy to clipboard?",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					StringSelection stringSelection = new StringSelection(wbKey);
					Clipboard clpbrd = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					clpbrd.setContents(stringSelection, null);
				}
			}

		});
		
		//Clear whiteboard menu item clicked, Delete all graphical objects off current whiteboard
		mntmClearWhiteboard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					currentWb.clearList();
					drawingPanel.repaint();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		});
		
		//Open whiteboard menu item clicked, allow the user to specify a new whiteboard URL and open in a new window. 
		mntmOpenWhiteboard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SharedWBClient(hostName, portNumber);
			}

		});
		
		//Save image menu item clicked, allow the user to save the file as a .png where they desire
		mntmSaveBoard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser(System
						.getProperty("user.dir"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"png Images", "png");
				fileChooser.setFileFilter(filter);
				int returnVal = fileChooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.getSelectedFile() != null) {
						drawingPanel.saveImage(fileChooser.getSelectedFile()
								.getAbsolutePath());
					}
				}
			}

		});
	}
	
	/**
	 * Method used by the chooseColorActionListener to get a newly chosen color
	 * @param initialColor
	 * @return
	 */
	private Color chooseColor(Color initialColor) {
		Color toReturn = JColorChooser.showDialog(null, "Choose new colour",
				initialColor);
		return (toReturn != null) ? toReturn : initialColor;
	}
	
	/*
	 * Getters & Setters past this point used to keep encapsulation and allow good design practices. 
	 */
	
	
	/**
	 * Retrieve the current drawing area this whiteboard client
	 * @return
	 */
	public DrawingPanel getDrawingArea() {
		return this.drawingPanel;
	}

	/**
	 * Retrieve the command of the user by checking what toggle button is pressed, default button is Pencil mode
	 * @return
	 */
	public String getSelectedCommand() {
		return this.graphicalObjectChoice.getSelection().getActionCommand();
	}
	
	/**
	 * Retrieve currently selected line colour
	 * @return
	 */
	public Color getLineColor() {
		return this.btnLineColorChooser.getBackground();
	}

	/**
	 * Retrieve currently selected fill colour
	 * @return
	 */
	public Color getFillColor() {
		return this.btnFillColorChooser.getBackground();
	}

	/**
	 * Return current stroke size, currently only standard basic strokes is supported
	 * @return
	 */
	public int getStrokeSize() {
		return ((Double) this.spinnerStrokeSize.getValue()).intValue();
	}

	/**
	 * Return whether the user wants the graphical object they draw to be filled with the fill colour. 
	 * @return
	 */
	public boolean getIsFilled() {
		return this.chckbxFillShape.isSelected();
	}

	/**
	 * Get the current whiteboard. 
	 * @return
	 */
	public WhiteBoard getShapeList() {
		return this.currentWb;
	}

}
