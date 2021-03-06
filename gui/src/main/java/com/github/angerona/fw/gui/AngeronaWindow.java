package com.github.angerona.fw.gui;

import interactive.InteractiveAgentNextActionRequester;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;

import com.github.angerona.fw.Action;
import com.github.angerona.fw.Agent;
import com.github.angerona.fw.Angerona;
import com.github.angerona.fw.AngeronaEnvironment;
import com.github.angerona.fw.InteractiveAgent;
import com.github.angerona.fw.gui.base.ViewComponent;
import com.github.angerona.fw.gui.controller.SimulationTreeController;
import com.github.angerona.fw.gui.project.ProjectTreeMVPComponent;
import com.github.angerona.fw.gui.report.ReportTreeMVP;
import com.github.angerona.fw.gui.simctrl.SimulationControlBar;
import com.github.angerona.fw.gui.simctrl.SimulationControlBarMVPComponent;
import com.github.angerona.fw.gui.simctrl.SimulationControlMenu;
import com.github.angerona.fw.gui.simctrl.SimulationControlPresenter;
import com.github.angerona.fw.gui.view.ReportView;
import com.github.angerona.fw.gui.view.ResourcenView;
import com.github.angerona.fw.internal.PluginInstantiator;
import com.github.angerona.fw.internal.UIPluginInstatiator;
import com.github.angerona.fw.internal.ViewComponentFactory;
import com.github.angerona.fw.listener.FrameworkListener;
import com.github.angerona.fw.listener.SimulationListener;

/**
 * The main window of the Angerona UI - Extension. It is a Singleton. 
 * @author Tim Janus
 */
public class AngeronaWindow extends WindowAdapter
	implements  
	SimulationListener,
	FrameworkListener
	{

	/** the root window of the application */
	private JFrame mainWindow;
	
	/** text are used to show error messages */
	private JTextArea txtErrorMsg;
	
	/** scroll pane that embeds the text are to show error messages */
	private JScrollPane errorMsgScroll;
	
	private DockController control;
	
	private StackDockStation mainStack;
	
	private SplitDockStation parentStation;
	
	private ReportView reportView;
	
	private List<Dockable> resMap = new LinkedList<>();
	
	/** a bar allowing the loading, running and initalization of simulations */
	private SimulationControlBar simLoadBar;
	
	/** logging facility */
	private static Logger LOG = LoggerFactory.getLogger(AngeronaWindow.class);
	
	/** unique instance of the AngeronaWindow (Singleton) */
	private static AngeronaWindow instance;
	
	/** @return reference to the unique instance of the AngeronaWindow */
	public static AngeronaWindow get() {
		if(instance == null) {
			instance = new AngeronaWindow();
		}
		return instance;
	}
	
	/** 
	 * Private Ctor: Singleton pattern.
	 */
	private AngeronaWindow() {}

	public void init() throws ParserConfigurationException, SAXException,
			IOException {
		LOG.trace("init() AngeronaWindow");
		
		// setup main window:
		mainWindow = new JFrame();
		mainWindow.addWindowListener(this);
		mainWindow.setTitle("Angerona");
		mainWindow.setBounds(100, 100, 400, 300);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setExtendedState(mainWindow.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		mainWindow.setIconImage(readImage("/angerona/fw/gui/icons/font.png"));
		
		// create the text area and the scroll pane for the error message dialog:
		txtErrorMsg = new JTextArea();
		txtErrorMsg.setLineWrap(true);
		txtErrorMsg.setWrapStyleWord(true);
		txtErrorMsg.setRows(5);
		txtErrorMsg.setColumns(40);
		errorMsgScroll = new JScrollPane(txtErrorMsg);
		
		// create the menu.
		createMenu();
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainWindow.dispose();
			}
		});
		menuFile.add(exit);
		menuBar.add(menuFile);
		
		JMenu simulationMenu = new JMenu("Simulation");
		SimulationControlMenu view = new SimulationControlMenu(simulationMenu);
		new SimulationControlPresenter(AngeronaGUIDataStorage.get().getSimulationControl(), view);
		menuBar.add(simulationMenu);
		
		JMenu menuWindow = new JMenu("Windows");
		JMenuItem miCreate = new JMenuItem("Create...");
		miCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				onCreateWindowClicked();
			}
		});
		
		JMenuItem miReset = new JMenuItem("Reset to default");
		miReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createDefaultPerspective();
			}
		});
		
		menuWindow.add(miCreate);
		menuWindow.add(miReset);
		menuBar.add(menuWindow);
		mainWindow.setJMenuBar(menuBar);
	}

	private void initAngeronaFramework() throws ParserConfigurationException,
			SAXException, IOException {
		PluginInstantiator pi = PluginInstantiator.getInstance();
		pi.addListener(UIPluginInstatiator.getInstance());
		
		Angerona angerona = Angerona.getInstance();
		angerona.addFrameworkListener(this);
		angerona.addSimulationListener(this);

		angerona.bootstrap();
		pi.registerPlugin(new DefaultUIPlugin());
		
		angerona.getProject().addDirectory(new File("config/agents"));
		angerona.getProject().addDirectory(new File("config/beliefbases"));
		angerona.getProject().addDirectory(new File("examples"));
	}
	
	/**
	 * @todo move somewhere else
	 * @param view
	 * @param title
	 * @return
	 */
	public Dockable openView(ViewComponent view) {
		DefaultDockable dd = new DefaultDockable(view.getPanel());
		view.decorate(dd);
		
		// easy if the center is a stack already, only adding the Dockable to the stack.
		if(mainStack.getController() != null) {
			mainStack.drop(dd);
		} else {
			// otherwise find the control with the most are and make it as new main stack.
			double maxArea = -1;
			SplitDockProperty curProp = null;
			Dockable centerDock = null;
			
			for(int i=0; i<parentStation.getDockableCount(); ++i) {
				Dockable cur = parentStation.getDockable(i);
				SplitDockProperty sdp = parentStation.getDockableLocationProperty(cur);
				double area = sdp.getWidth() * sdp.getHeight();
				if(area > maxArea) {
					maxArea = area;
					curProp = sdp;
					centerDock = cur;
				}
			}
			
			// remove the old dockable, drop it to the stack, drop the new dockable to the stack
			// and then put the stack on the old location of the main dockable.
			parentStation.removeDockable(centerDock);
			mainStack.drop(centerDock);
			mainStack.drop(dd);
			parentStation.drop(mainStack, curProp);
		}
		return dd;
	}
	
	/** helper method: called if the 'Create Window...' menu item is clicked */
	private void onCreateWindowClicked() {
		UIPluginInstatiator uip = UIPluginInstatiator.getInstance();
		Map<String, Class<? extends ViewComponent>> viewMap = uip.getViewMap();
		String str = (String) JOptionPane.showInputDialog(null, 
				"Select a Window to create...",
				"Create Window",
				JOptionPane.PLAIN_MESSAGE,
				null,
				viewMap.keySet().toArray(),
				null);
		if(viewMap.containsKey(str)) {
			try {
				ViewComponent bc = viewMap.get(str).newInstance();
				openView(bc);
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows an error message box if its receives error messages from the Angerona Framework.
	 */
	@Override
	public void onError(String errorTitle, String errorMessage) {
		txtErrorMsg.setText(errorMessage);
		txtErrorMsg.setCaretPosition(0);
		JOptionPane.showMessageDialog(mainWindow, errorMsgScroll, 
				errorTitle, JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void onBootstrapDone() {	
	}

	/**
	 * The content for the docking frame framework is generated in the
	 * windowOpened method, this is called if the Angerona main window
	 * was initialized and setVisible the first time. It is important that
	 * every change made in the docking frames framework occurs in the event
	 * handling thread of swing.
	 */
	@Override
	public void windowOpened(WindowEvent arg0) {
		// init docking frames
		control = new DockController();
		control.setRootWindow(mainWindow);
		control.setTheme(new NoStackTheme(new SmoothTheme()));
		
		parentStation = new SplitDockStation();
		parentStation.setExpandOnDoubleclick(false);
		mainStack = new StackDockStation();
		mainStack.setTabPlacement(TabPlacement.TOP_OF_DOCKABLE);
		
		control.add(parentStation);
		mainWindow.add(parentStation);
		parentStation.setVisible(true);
		
		String iconFolder = "/com/github/angerona/fw/gui/icons/";
		registerIcon("ico_main", iconFolder + "font.png");
		registerIcon("close", iconFolder + "cross.png");
		registerIcon("report", iconFolder + "table.png");
		registerIcon("resources", iconFolder + "drive_link.png");
		registerIcon("monitor", iconFolder + "monitor.png");
		registerIcon("simulation", iconFolder + "world.png");
		registerIcon("report", iconFolder + "report.png");
		registerIcon("report_attachment", iconFolder + "report_disk.png");
		registerIcon("page_white", iconFolder + "page_white.png");
		registerIcon("agent", iconFolder + "user.png");
		
		// init Angerona
		try {
			initAngeronaFramework();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			onError("Angerona Initialization Failure", e.getMessage());
			e.printStackTrace();
		}
		
		
		reportView = new ReportView();
		
		createDefaultPerspective();
	}

	private boolean registerIcon(String id, String resUrl) {
		BufferedImage image = readImage(resUrl);
		if(image != null) {
			ImageIcon icon = new ImageIcon(image);
			control.getIcons().setIcon(id, Priority.CLIENT, icon);
			return true;
		}
		return false;
	}

	public IconManager getIcons() {
		return control.getIcons();
	}
	
	private BufferedImage readImage(String resUrl) {
		InputStream is = AngeronaWindow.class.getResourceAsStream(resUrl);
		if(is == null) {
			LOG.warn("Cannot found resource '{}' in JAR", resUrl);
			return null;
		}
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			LOG.error("Cannot load '{}' from JAR: '{}'", resUrl, e.getMessage());
			e.printStackTrace();
		}
		return image;
	}
	
	private void createDefaultPerspective() {
		parentStation.removeAllDockables();
		
		ViewComponent viewComp = ViewComponentFactory.get().createViewComponent(ProjectTreeMVPComponent.class);
		DefaultDockable dd = new DefaultDockable(viewComp.getPanel());
		viewComp.decorate(dd);
		parentStation.drop(dd, new SplitDockProperty(0, 0, 0.25, 0.9));
		
		dd = new DefaultDockable(reportView);
		reportView.decorate(dd);
		parentStation.drop(dd, new SplitDockProperty(0.25, 0, 0.75, 0.9));
		
		ReportTreeMVP reportTree = ViewComponentFactory.get().createViewComponent(ReportTreeMVP.class);
		dd = new DefaultDockable(reportTree.getPanel());
		reportTree.decorate(dd);
		parentStation.drop(dd, new SplitDockProperty(0.25, 0, 0.75, 0.9));
		
		viewComp = ViewComponentFactory.get().createViewComponent(SimulationControlBarMVPComponent.class);
		dd = new DefaultDockable(viewComp.getPanel());
		/*
		dd.setActionOffers(null);
		HierarchyDockActionSource hdas = (HierarchyDockActionSource)dd.getGlobalActionOffers();
		hdas.unbind();
		*/
		parentStation.drop(dd, new SplitDockProperty(0, 0.9, 1, 0.1));		
	}
	
	@Override
	public void simulationStarted(AngeronaEnvironment simulationEnvironment) {
		SimulationTreeController stc = new SimulationTreeController(new JTree());
		stc.simulationStarted(simulationEnvironment);
		
		ResourcenView rv = new ResourcenView(stc);
		DefaultDockable dd = new DefaultDockable(new JScrollPane(rv));
		dd.setTitleText("Entities of '" + simulationEnvironment.getName() + "'");
		dd.setTitleIcon(control.getIcons().get("simulation"));
		parentStation.drop(dd, SplitDockProperty.WEST);
		resMap.add(dd);
		
		//check if the scenario has an interactive agent and open the interactive View if needed
		for(Agent a : simulationEnvironment.getAgents()){
			if (a instanceof InteractiveAgent){
				((InteractiveAgent) a).setNextActionRequester(new InteractiveAgentNextActionRequester(simulationEnvironment));
			}
		}
	}

	@Override
	public void simulationDestroyed(AngeronaEnvironment simulationEnvironment) {
		for(Dockable d : resMap) {
			DockStation st = d.getDockParent();
			if(st != null) {
				st.drag(d);
			}
		}
		resMap.clear();
	}
	
	public void registerDockableForCurrentSimulation(Dockable d) {
		resMap.add(d);
	}

	@Override
	public void agentAdded(final AngeronaEnvironment simulationEnvironment,
			Agent added) {
	}

	@Override
	public void agentRemoved(AngeronaEnvironment simulationEnvironment,
			Agent removed) {
	}

	@Override
	public void tickDone(AngeronaEnvironment simulationEnvironment) {
	}

	@Override
	public void actionPerformed(Agent agent, Action act) {
	}
	
	public JFrame getMainWindow(){
		return mainWindow;
	}

	@Override
	public void tickStarting(AngeronaEnvironment simulationEnvironment) {
		// TODO Auto-generated method stub
		
	}
}
