package curlywhirly.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.*;
import curlywhirly.opengl.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private ControlPanel controlPanel;
	private WinMainToolBar toolbar;
	private StatusBar statusBar;
	private CanvasController controller;

	private int controlPanelWidth = 200;

	private OpenGLPanel canvas3D;
	private StartPanel startPanel;
	private JSplitPane splitPane;

	private JTabbedPane ctrlTabs;
	private DataPanel dataPanel;

	private Commands toolbarActions;

	private DataSet dataSet;

	WinMain()
	{
		setTitle(RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION);
		setIconImage(Icons.getIcon("curlywurly_icon32px").getImage());

		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(this, dropAdapter));

		toolbarActions = new Commands(this);
		new Actions(this);
		toolbar = new WinMainToolBar(this);
		add(toolbar, BorderLayout.NORTH);

		createTabbedPane();
		createCanvas();

		startPanel = new StartPanel(this);

		// main comp is split pane with control panel on the left and canvas on the right
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ctrlTabs, startPanel);
		splitPane.setOneTouchExpandable(true);
		add(splitPane);

		// status bar
		statusBar = new StatusBar();
		add(statusBar, BorderLayout.SOUTH);

		setSize(Prefs.guiWinMainW, Prefs.guiWinMainH);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		SwingUtils.positionWindow(
			this, null, Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		// Window listeners are added last so they don't interfere with the
		// maximization from above
		addListeners();
	}

	private void createTabbedPane()
	{
		ctrlTabs = new JTabbedPane();
		controlPanel = new ControlPanel(this);
		controlPanel.setPreferredSize(new Dimension(controlPanelWidth, Prefs.guiWinMainH));

		dataPanel = new DataPanel();
		dataPanel.setPreferredSize(new Dimension(controlPanelWidth, Prefs.guiWinMainH));

		ctrlTabs.add("", controlPanel);
		ctrlTabs.setIconAt(0, Icons.getIcon("CONTROLSTAB"));
		ctrlTabs.setToolTipTextAt(0, RB.getString("gui.WinMain.controlsTab"));
		ctrlTabs.add("", dataPanel);
		ctrlTabs.setIconAt(1, Icons.getIcon("DATATAB"));
		ctrlTabs.setToolTipTextAt(1, RB.getString("gui.WinMain.dataTab"));
	}

	private void createCanvas()
	{
		canvas3D = new OpenGLPanel(this);
		canvas3D.setPreferredSize(new Dimension((Prefs.guiWinMainW-controlPanelWidth), Prefs.guiWinMainH));
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
		controlPanel.setDataSet(dataSet);
		dataPanel.setDataSet(dataSet);

		controlPanel.addComboModels();

		//do the rest of the set up
		//set the title of the window to the name of the dataset
//		winMain.setTitle(winMain.titleString + "  --  " + dataSet.name);
		controlPanel.setUpCategoryLists();
		statusBar.setDefaultText();

		display3DCanvas();

		Actions.openedData();
		controlPanel.toggleEnabled(true);
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainW  = getSize().width;
					Prefs.guiWinMainH = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;
				}
			}
		});
	}

	public void display3DCanvas()
	{
		splitPane.setRightComponent(canvas3D);
		canvas3D.startAnimator();
//		repaint();
	}

	WinMainToolBar getToolbar()
		{ return toolbar; }

	public OpenGLPanel getOpenGLPanel()
		{ return canvas3D; }

	DataPanel getDataPanel()
		{ return dataPanel; }

	public CanvasController getCanvasController()
		{ return controller; }

	public DataSet getDataSet()
		{ return dataSet; }

	Commands getCommands()
		{ return toolbarActions; }

	public ControlPanel getControlPanel()
		{ return controlPanel; }

	public StatusBar getStatusBar()
		{ return statusBar; }
}