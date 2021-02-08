// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.analysis.*;
import jhi.curlywhirly.data.*;
import jhi.curlywhirly.util.*;
import scri.commons.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class CategoryGroupPanel extends JPanel implements TableModelListener
{
	private final WinMain winMain;
	private final SelectionPanelNB parent;

	private DataSet dataSet;
	private ArrayList<CategoryGroup> categoryGroups;

	private ButtonGroup buttonGroup;
	private ArrayList<CategoryPanel> categoryPanels;

	private int prefHeight;
	private Component vStrut;

	CategoryGroupPanel(final SelectionPanelNB parent, WinMain winMain)
	{
		this.winMain = winMain;
		this.parent = parent;

		addComponentListener();
	}

	// Listens for resize events and adjusts the size of the "vertical padding"
	// vStrut as required. Prevents unwanted whitespace appearing in CategoryPanels
	private void addComponentListener()
	{
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				super.componentResized(e);

				// If the scroll pane is larger than our components add a vertical strut
				// to push our components to the top.
				if (prefHeight < parent.getHeight())
				{
					// vStrut might not exist if this gets called as part of the panel setup
					if (vStrut != null)
						remove(vStrut);

					// Adjust the size of vStrut and add it to the panel again
					vStrut = Box.createVerticalStrut(parent.getHeight() - prefHeight);
					add(vStrut);
				}
			}
		});
	}

	void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		if (dataSet != null)
			categoryGroups = dataSet.getCategoryGroups();

		// Because we tweak the height of the component ourselves so that it
		// renders correctly we need to reset that here to make the scroll bar
		// disappear if it's a null dataset
		prefHeight = 0;
		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));

		setupCategoryGroupUI();
	}

	private void setupCategoryGroupUI()
	{
		if (dataSet != null)
		{
			createControls(categoryGroups);

			// Select the first scheme and colour by this scheme.
			if (buttonGroup.getElements().hasMoreElements())
				buttonGroup.getElements().nextElement().setSelected(true);

			dataSet.setCurrentCategoryGroup(categoryGroups.get(0));
		}
	}

	private void createControls(ArrayList<CategoryGroup> catGroups)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		categoryPanels = new ArrayList<CategoryPanel>();
		buttonGroup = new ButtonGroup();

		prefHeight = 0;

		for (CategoryGroup catGroup : catGroups)
			addCategoryPanel(catGroup);

		// If the scroll pane is larger than our components add a vertical strut
		// to push our components to the top.
		if (prefHeight < parent.getHeight())
		{
			vStrut = Box.createVerticalStrut(parent.getHeight() - prefHeight);
			add(vStrut);
		}

		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));
	}

	private void addCategoryPanel(CategoryGroup group)
	{
		CategoryPanel catPanel = new CategoryPanel(this, group, dataSet);
		add(catPanel);
		buttonGroup.add(catPanel.getNamePanel().getRadioButton());
		categoryPanels.add(catPanel);

		prefHeight += catPanel.getPreferredSize().height;
	}

	void selectAll()
	{
		categoryPanels.forEach(panel ->
		{
			panel.selectAll();
			panel.updateNamePanel();
		});
		dataSet.detectMultiSelectedPoints(Prefs.guiSelectionSphereSize);
	}

	void selectNone()
	{
		categoryPanels.forEach(panel ->
		{
			panel.selectNone();
			panel.updateNamePanel();
		});
		dataSet.detectMultiSelectedPoints(Prefs.guiSelectionSphereSize);
	}

	void updatePanelSize(CategoryPanel catPanel, JPanel tablePanel)
	{
		int tableHeight = catPanel.getTableHeight();
		prefHeight = tablePanel.isVisible() ? prefHeight + tableHeight : prefHeight - tableHeight;
		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));
	}

	// Respond to user interaction with the tables by updating their title
	// panels and updating the table in the dataPanel tab.
	@Override
	public void tableChanged(TableModelEvent e)
	{
		categoryPanels.forEach(panel -> panel.updateNamePanel());

		// Required to force the counts in tables other than the one that was
		// changed to update.
		repaint();

		winMain.getDataPanel().updateTableModel();
	}

	void exportCategories()
	{
		File saveAs = new File(Prefs.guiCurrentDir, "selectedcategories.txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the data to
		String filename = FileUtils.getSaveFilename(
			RB.getString("gui.CategoryGroupPanel.saveCategoryGroups.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		File categoryFile = new File(filename);
		CategoryGroupSaver saver = new CategoryGroupSaver(categoryFile, categoryGroups);

		ProgressDialog dialog = new ProgressDialog(saver,
			RB.getString("gui.CategoryGroupPanel.saveCategoryGroups.title"),
			RB.getString("gui.CategoryGroupPanel.saveCategoryGroups.label"),
			CurlyWhirly.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();

				TaskDialog.showOpenLog(RB.format("gui.CategoryGroupPanel.saveCategoryGroups.exception",
					dialog.getException()), null);
			}
		}
		else
			TaskDialog.showFileOpen(RB.getString("gui.CategoryGroupPanel.saveCategoryGroups.openFile"), TaskDialog.INF, categoryFile);
	}
}