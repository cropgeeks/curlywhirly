// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.viewer.*;
import scri.commons.gui.*;

import javax.media.opengl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;

public class WinMain extends JFrame
{
	private SelectionPanelNB selectionPanel;
	private ControlsPanelNB controlsPanel;
	private WinMainToolBar toolbar;
	private StatusBar statusBar;

	private OpenGLPanel canvas3D;
	private StartPanel startPanel;
	private JSplitPane splitPane;

	private JTabbedPane ctrlTabs;
	private DataPanel dataPanel;

	private Commands toolbarActions;

	private DataSet dataSet;

	private GLCapabilities caps;
	private JPanel canvasContainer;
	private MultiSelectPanel multiSelectPanel;

	private ColourKeyCreator colourKeyCreator;

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

		splitPane.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
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
		ctrlTabs.add("", dataPanel);
		ctrlTabs.setIconAt(1, Icons.getIcon("DATATAB"));
		ctrlTabs.setToolTipTextAt(1, RB.getString("gui.WinMain.dataTab"));
		ctrlTabs.add("", controlsPanel);
		ctrlTabs.setIconAt(2, Icons.getIcon("CONTROLSTAB"));
		ctrlTabs.setToolTipTextAt(2, RB.getString("gui.WinMain.controlsTab"));
	}

	private void createCanvas()
	{
		GLProfile profile = GLProfile.getDefault();
		caps = new GLCapabilities(profile);

		caps.setSampleBuffers(true);
		caps.setNumSamples(2);
		canvas3D = new OpenGLPanel(this, caps);
		canvas3D.setSize(new Dimension((Prefs.guiWinMainW - Prefs.guiSplitterLocation), Prefs.guiWinMainH));

		canvasContainer = new JPanel();
		canvasContainer.setLayout(new BorderLayout());
		canvasContainer.add(canvas3D);
		multiSelectPanel = new MultiSelectPanel(canvas3D.getMultiSelectionRenderer());
		canvasContainer.add(multiSelectPanel, BorderLayout.SOUTH);

		colourKeyCreator = new ColourKeyCreator();
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
		canvas3D.setDataSet(dataSet);
		selectionPanel.setDataSet(dataSet);
		dataPanel.setDataSet(dataSet);
		controlsPanel.setDataSet(dataSet);
		multiSelectPanel.setDataSet(dataSet);
		colourKeyCreator.setDataSet(dataSet);

		//do the rest of the set up
		//set the title of the window to the name of the dataset
		if (dataSet != null)
		{
			setTitle(dataSet.getName() + " - " + RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION);
			statusBar.setDefaultText();

			display3DCanvas();

			Actions.openedData();
		}
		else
		{
			statusBar.clearFps();
			setTitle(RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION);
			Actions.openedNoData();
		}
	}

	private void addListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Prefs.guiWinMainW = getSize().width;
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
		splitPane.setRightComponent(canvasContainer);
		canvas3D.startAnimator();
	}

	// Sets the display back to its default (no data loaded) state
	public void closeDataSet()
	{
		int location = splitPane.getDividerLocation();

		splitPane.setRightComponent(startPanel);

		splitPane.setDividerLocation(location);

		// Ensure the multiselect panel is hidden on the next dataset load
		multiSelectPanel.setVisible(false);

		setDataSet(null);
	}

	// Checks with the user if it is definitely okay to close the currently
	// loaded dataset
	public boolean okToClose()
	{
		if (Prefs.guiWarnOnClose == false)
			return true;

		// If no assembly is loaded, it's fine too
		if (dataSet == null)
			return true;

		// For all other situations, we need to prompt...
		String msg = RB.getString("gui.WinMain.okToCloseMsg");
		JCheckBox checkbox = new JCheckBox();
		RB.setText(checkbox, "gui.WinMain.warnOnClose");

		String[] options = new String[]{
			RB.getString("gui.text.yes"),
			RB.getString("gui.text.no")};

		int response = TaskDialog.show(msg, TaskDialog.QST, 1, checkbox, options);

		Prefs.guiWarnOnClose = !checkbox.isSelected();

		return response == 0;
	}

	// Check with the user that it is definitely okay to close the application
	public boolean okToExit()
	{
		// If the user doesn't care, just allow it
		if (Prefs.guiWarnOnExit == false)
			return true;

		// If no assembly is loaded, it's fine too
		if (dataSet == null)
			return true;

		// For all other situations, we need to prompt...
		String msg = RB.getString("gui.WinMain.okToExitMsg");
		JCheckBox checkbox = new JCheckBox();
		RB.setText(checkbox, "gui.WinMain.warnOnExit");

		String[] options = new String[]{
			RB.getString("gui.text.yes"),
			RB.getString("gui.text.no")};

		int response = TaskDialog.show(msg, TaskDialog.QST, 1, checkbox, options);

		Prefs.guiWarnOnExit = !checkbox.isSelected();

		return response == 0;
	}

	public void updateStatusBarFps(int fps)
	{
		statusBar.updateFps(fps);
	}

	WinMainToolBar getToolbar()
	{
		return toolbar;
	}

	public OpenGLPanel getOpenGLPanel()
	{
		return canvas3D;
	}

	public DataPanel getDataPanel()
	{
		return dataPanel;
	}

	public DataSet getDataSet()
	{
		return dataSet;
	}

	Commands getCommands()
	{
		return toolbarActions;
	}

	public SelectionPanelNB getControlPanel()
	{
		return selectionPanel;
	}

	public JSplitPane getSplitPane()
	{
		return splitPane;
	}

	public GLCapabilities getCapabilities()
	{
		return caps;
	}

	public MultiSelectPanel getMultiSelectPanel()
	{
		return multiSelectPanel;
	}

	public ColourKeyCreator getColourKeyCreator()
	{
		return colourKeyCreator;
	}
}