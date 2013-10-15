package curlywhirly.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.*;
import curlywhirly.opengl.*;

import scri.commons.gui.*;

public class WinMain extends JFrame
{
	private SelectionPanelNB selectionPanel;
	private ControlsPanelNB controlsPanel;
	private WinMainToolBar toolbar;
	private StatusBar statusBar;
	private CanvasController controller;

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
		splitPane.setDividerLocation(Prefs.guiSplitterLocation);
		add(splitPane);

		splitPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				Prefs.guiSplitterLocation = splitPane.getDividerLocation();
				JScrollPane sp = selectionPanel.getScrollPane();
				int w = sp.getWidth() - sp.getVerticalScrollBar().getWidth() - splitPane.getWidth();
				CategoryGroupPanel groupPanel = selectionPanel.getCategoryGroupPanel();
				if (groupPanel != null)
					groupPanel.setPreferredSize(new Dimension(w, groupPanel.getPreferredSize().height));
			}
		});

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
		selectionPanel = new SelectionPanelNB(this);
		selectionPanel.setPreferredSize(new Dimension(Prefs.guiSplitterLocation, Prefs.guiWinMainH));

		controlsPanel = new ControlsPanelNB(this);
		controlsPanel.setPreferredSize(new Dimension(Prefs.guiSplitterLocation, Prefs.guiWinMainH));

		dataPanel = new DataPanel();
		dataPanel.setPreferredSize(new Dimension(Prefs.guiSplitterLocation, Prefs.guiWinMainH));

		ctrlTabs.add("", selectionPanel);
		ctrlTabs.setIconAt(0, Icons.getIcon("SELECTIONTAB"));
		ctrlTabs.setToolTipTextAt(0, RB.getString("gui.WinMain.selectionTab"));
		ctrlTabs.add("", controlsPanel);
		ctrlTabs.setIconAt(1, Icons.getIcon("CONTROLSTAB"));
		ctrlTabs.setToolTipTextAt(1, "Controls");
		ctrlTabs.add("", dataPanel);
		ctrlTabs.setIconAt(2, Icons.getIcon("DATATAB"));
		ctrlTabs.setToolTipTextAt(2, RB.getString("gui.WinMain.dataTab"));
	}

	private void createCanvas()
	{
		canvas3D = new OpenGLPanel(this);
		canvas3D.setSize(new Dimension((Prefs.guiWinMainW-Prefs.guiSplitterLocation), Prefs.guiWinMainH));
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
		canvas3D.setDataSet(dataSet);
		selectionPanel.setDataSet(dataSet);
		dataPanel.setDataSet(dataSet);
		controlsPanel.setDataSet(dataSet);

		//do the rest of the set up
		//set the title of the window to the name of the dataset
		setTitle(RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION + "  --  " + dataSet.getName());
		selectionPanel.setUpCategoryLists();
		statusBar.setDefaultText();

		display3DCanvas();

		Actions.openedData();
		selectionPanel.toggleEnabled(true);
		controlsPanel.toggleEnabled(true);
		dataPanel.toggleEnabled(true);
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

	public SelectionPanelNB getControlPanel()
		{ return selectionPanel; }

	public StatusBar getStatusBar()
		{ return statusBar; }

	public JSplitPane getSplitPane()
	{
		return splitPane;
	}
}