// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.analysis.*;
import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.dialog.*;
import jhi.curlywhirly.gui.viewer.*;
import jhi.curlywhirly.util.*;
import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class MultiSelectPanel extends JPanel
{
	private final MultiSelectionRenderer selectionRenderer;
	private DataSet dataSet;

	private JLabel lblAction;
	private JButton bCancel;
	private JButton bOk;
	private JLabel lblSelection;
	private JSlider selectionSlider;
	private JComboBox<String> selectionTypeCombo;
	private DefaultComboBoxModel<String> selectionTypeModel;
	private JLabel lblSelectionCount;
	private JLabel dbLinkSeparator = new JLabel(" | ");

	private HyperLinkLabel lblExport;
	private HyperLinkLabel lblDBLink;
	private HyperLinkLabel lblOptions;

	public static final int SELECT = 0;
	public static final int DESELECT = 1;
	public static final int TOGGLE = 2;

	public MultiSelectPanel(MultiSelectionRenderer selectionOverlay)
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		initComponents();

		JPanel centrePanel = setupCentrePanel();
		add(centrePanel, BorderLayout.CENTER);

		JPanel eastPanel = setupEastPanel();
		add(eastPanel, BorderLayout.EAST);

		this.selectionRenderer = selectionOverlay;
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		if (dataSet != null && !dataSet.getDbAssociation().isGroupPreviewEnabled())
		{
			dbLinkSeparator.setVisible(false);
			lblDBLink.setVisible(false);
		}
		else
		{
			dbLinkSeparator.setVisible(true);
			lblDBLink.setVisible(true);
		}
	}

	private void initComponents()
	{
		lblAction = new JLabel();
		RB.setText(lblAction, "gui.viewer.multiSelectPanel.lblAction");

		bOk = new JButton();
		RB.setText(bOk, "gui.text.ok");
		bOk.addActionListener(e -> okClicked());

		bCancel = new JButton();
		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(e -> cancelClicked());

		lblSelection = new JLabel();
		lblSelectionCount = new JLabel();

		lblExport = new HyperLinkLabel();
		RB.setText(lblExport, "gui.viewer.MultiSelectPanel.lblExport");
		lblExport.addActionListener(e -> exportToFile());
		lblExport.setForeground(new Color(68, 106, 156));

		lblDBLink = new HyperLinkLabel();
		RB.setText(lblDBLink, "gui.viewer.MultiSelectPanel.lblDBLink");
		lblDBLink.addActionListener(e -> exportToDatabase());
		lblDBLink.setForeground(new Color(68, 106, 156));

		lblOptions = new HyperLinkLabel();
		RB.setText(lblOptions, "gui.viewer.MultiSelectPanel.lblOptions");
		lblOptions.addActionListener(e -> optionsClicked());
		lblOptions.setForeground(new Color(68, 106, 156));

		setupSlider();
		setupComboBox();

		setVisible(false);
	}

	private JPanel setupCentrePanel()
	{
		final int buttonWidth = 60;

		// The GridBagLayout of the outer panel keeps the nested GroupLayout centred
		JPanel centrePanel = new JPanel();
		centrePanel.setLayout(new GridBagLayout());

		// Create a nested panel with a GroupLayout to get the right combination
		// of control sizing and placement
		JPanel nested = new JPanel();
		GroupLayout layout = new GroupLayout(nested);
		nested.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(lblSelection)
			.addComponent(selectionSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)
			.addComponent(lblSelectionCount)
			.addComponent(lblAction)
			.addComponent(selectionTypeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE)
			.addComponent(bOk, buttonWidth, buttonWidth, buttonWidth)
			.addComponent(bCancel));
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblSelection)
				.addComponent(selectionSlider)
				.addComponent(lblSelectionCount)
				.addComponent(lblAction)
				.addComponent(selectionTypeCombo)
				.addComponent(bOk)
				.addComponent(bCancel)));

		centrePanel.add(nested);

		return centrePanel;
	}

	private JPanel setupEastPanel()
	{
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new FlowLayout());
		// Without the border the elements in this panel sit out of line with
		// those in the centre panel
		eastPanel.setBorder(new EmptyBorder(6, 0, 0, 0));
		eastPanel.add(lblExport);

		// We only want the DBLink option to show up when the data file has a url in it
		eastPanel.add(dbLinkSeparator);
		eastPanel.add(lblDBLink);

		eastPanel.add(new JLabel(" | "));
		eastPanel.add(lblOptions);

		return eastPanel;
	}

	private void setupComboBox()
	{
		selectionTypeCombo = new JComboBox<String>();
		selectionTypeModel = new DefaultComboBoxModel<String>();
		selectionTypeModel.insertElementAt(RB.getString("gui.viewer.MultiSelectPanel.selectionTypeModel.select"), SELECT);
		selectionTypeModel.insertElementAt(RB.getString("gui.viewer.MultiSelectPanel.selectionTypeModel.deselect"), DESELECT);
		selectionTypeModel.insertElementAt(RB.getString("gui.viewer.MultiSelectPanel.selectionTypeModel.toggle"), TOGGLE);
		selectionTypeCombo.setModel(selectionTypeModel);
		selectionTypeCombo.setSelectedIndex(0);
	}

	private void setupSlider()
	{
		selectionSlider = new JSlider();
		RB.setText(lblSelection, "gui.viewer.MultiSelectPanel.lblSelection");
		selectionSlider.setValue(4);
		selectionSlider.addChangeListener(e -> sliderStateChanged());
	}

	@Override
	public void setVisible(boolean visible)
	{
		float pointSize = ((Prefs.guiSelectionSphereSize - 0.06f) / (2f - 0.06f) * (float) (selectionSlider.getMaximum() - selectionSlider.getMinimum()) + selectionSlider.getMinimum());
		selectionSlider.setValue((int) pointSize);
		super.setVisible(visible);
		if (selectionRenderer != null && visible)
		{
			dataSet.detectMultiSelectedPoints(Prefs.guiSelectionSphereSize);
			lblSelectionCount.setText(getSelectedPointsString());
		}
	}

	private void okClicked()
	{
		setVisible(false);
		selectionRenderer.finishedMultiSelect(selectionTypeCombo.getSelectedIndex());
	}

	private void cancelClicked()
	{
		setVisible(false);
		selectionRenderer.cancelMultiSelect();
	}

	private void optionsClicked()
	{
		new MultiSelectOptionsDialog();
	}

	private void exportToDatabase()
	{
		File exportFile = new File(Prefs.cacheFolder + System.getProperty("file.separator") + System.currentTimeMillis() + "-group.txt");
		exportFile.deleteOnExit();

		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(exportFile))))
		{
			dataSet.multiSelectedPoints().forEach(point -> writer.println(point.getName()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			dataSet.getDbAssociation().visitUrlForUpload(exportFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void exportToFile()
	{
		// Create a file with a default filename in the current directory
		File saveAs = new File(Prefs.guiCurrentDir, "selectedpoints.txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the data to
		String filename = FileUtils.getSaveFilename(
			RB.getString("gui.viewer.MultiSelectPanel.saveDataPoints.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename != null)
		{
			saveAs = new File(filename);

			DataPointSaver saver = new DataPointSaver(saveAs, dataSet.multiSelectedPoints().collect(Collectors.toCollection(ArrayList::new)), dataSet.getAxes().getXYZLabels());

			ProgressDialog dialog = new ProgressDialog(saver,
				RB.getString("gui.viewer.MultiSelectPanel.saveDataPoints.title"),
				RB.getString("gui.viewer.MultiSelectPanel.saveDataPoints.label"),
				CurlyWhirly.winMain);

			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				{
					dialog.getException().printStackTrace();

					TaskDialog.showOpenLog(RB.format("gui.viewer.MultiSelectPanel.saveDataPoints.exception",
						dialog.getException()), null);
				}
			}
			TaskDialog.showFileOpen(RB.getString("gui.viewer.MultiSelectPanel.saveDataPoints.openFile"), TaskDialog.INF, saveAs);
		}
	}

	private void sliderStateChanged()
	{
		if (selectionRenderer != null)
		{
			float sliderVal = selectionSlider.getValue();
			float min = selectionSlider.getMinimum();
			float max = selectionSlider.getMaximum();
			float pointSize = ((sliderVal - min) / (max - min) * (2f - 0.06f) + 0.06f);
			Prefs.guiSelectionSphereSize = pointSize;
			selectionRenderer.setSelectPointSize(pointSize);
			dataSet.detectMultiSelectedPoints(pointSize);

			lblSelectionCount.setText(getSelectedPointsString());
		}
	}

	private String getSelectedPointsString()
	{
		String selected = "" + dataSet.multiSelectedPoints().count();
		String total = "" + dataSet.size();
		int diff = total.length() - selected.length();
		StringBuilder builder = new StringBuilder();
		builder.append(("("));
		// Pad out the selected string with zeros. Prevents component from
		// resizing as the number increases / decreases
		for (int i = 0; i < diff; i++)
			builder.append('0');
		builder.append(selected).append(" / ").append(total).append(')');

		return builder.toString();
	}
}