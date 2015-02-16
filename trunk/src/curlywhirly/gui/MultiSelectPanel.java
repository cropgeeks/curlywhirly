package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import curlywhirly.analysis.*;
import curlywhirly.data.*;
import curlywhirly.gui.dialog.*;
import curlywhirly.gui.viewer.*;
import curlywhirly.util.FileUtils;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class MultiSelectPanel extends JPanel implements ActionListener, ChangeListener
{
	private final MultiSelectionRenderer selectionRenderer;
	private final OpenGLPanel glPanel;
	private DataSet dataSet;

	private JLabel lblAction;
    private JButton bCancel;
    private JButton bOk;
    private JLabel lblSelection;
    private JSlider selectionSlider;
    private JComboBox<String> selectionTypeCombo;
	private DefaultComboBoxModel<String> selectionTypeModel;
	private JLabel lblSelectionCount;

	private HyperLinkLabel lblExport;
	private HyperLinkLabel lblOptions;

	public static final int SELECT = 0;
	public static final int DESELECT = 1;
	public static final int TOGGLE = 2;

	public MultiSelectPanel(MultiSelectionRenderer selectionOverlay, OpenGLPanel glPanel)
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		initComponents();
		JPanel centrePanel = setupCentrePanel();
		add(centrePanel, BorderLayout.CENTER);

		JPanel eastPanel = setupEastPanel();
		add(eastPanel, BorderLayout.EAST);
//		add(lblOptions, BorderLayout.EAST);

		this.glPanel = glPanel;
		this.selectionRenderer = selectionOverlay;
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	private void initComponents()
	{
		lblAction = new JLabel();
		RB.setText(lblAction, "gui.viewer.multiSelectPanel.lblAction");

		bCancel = new JButton();
		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);

		bOk = new JButton();
		RB.setText(bOk, "gui.text.ok");
		bOk.addActionListener(this);

		lblSelection = new JLabel();
		lblSelectionCount = new JLabel();

		lblExport = new HyperLinkLabel();
		lblExport.setText("Export");
		lblExport.addActionListener(this);
		lblExport.setForeground(new Color(68, 106, 156));

		lblOptions = new HyperLinkLabel();
		RB.setText(lblOptions, "gui.viewer.MultiSelectPanel.lblOptions");
		lblOptions.addActionListener(this);
		lblOptions.setForeground(new Color(68, 106, 156));

		setupSlider();
		setupComboBox();

		setVisible(false);
	}

	private JPanel setupCentrePanel()
	{
		JPanel centrePanel = new JPanel();
		centrePanel.add(lblSelection);
		centrePanel.add(selectionSlider);
		centrePanel.add(lblSelectionCount);
		centrePanel.add(lblAction);
		centrePanel.add(selectionTypeCombo);
		centrePanel.add(bOk);
		centrePanel.add(bCancel);

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
		selectionTypeCombo.addActionListener(this);
	}

	private void setupSlider()
	{
		selectionSlider = new JSlider();
		RB.setText(lblSelection, "gui.viewer.MultiSelectPanel.lblSelection");
		selectionSlider.setValue(4);
		selectionSlider.addChangeListener(this);
	}

	@Override
	public void setVisible(boolean visible)
	{
		float pointSize = ((Prefs.guiSelectionSphereSize-0.06f)/(2f-0.06f) * (float)(selectionSlider.getMaximum()-selectionSlider.getMinimum()) + selectionSlider.getMinimum());
		selectionSlider.setValue((int) pointSize);
		super.setVisible(visible);
		if (selectionRenderer != null)
		{
			detectMultiSelectedPoints();
			lblSelectionCount.setText(getSelectedPointsString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOk)
		{
			setVisible(false);
			selectionRenderer.finishedMultiSelect(selectionTypeCombo.getSelectedIndex());
		}

		else if (e.getSource() == bCancel)
		{
			setVisible(false);
			selectionRenderer.cancelMultiSelect();
		}

		else if (e.getSource() == lblOptions)
			new MultiSelectOptionsDialog();

		else if (e.getSource() == lblExport)
			exportSelectedDatapoints();

	}

	private void exportSelectedDatapoints()
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

			HashSet<DataPoint> multiSelected = selectionRenderer.detectMultiSelectedPoints();

			DataPointSaver saver = new DataPointSaver(saveAs, new ArrayList<DataPoint>(multiSelected), dataSet.getAxes().getXYZ(), dataSet.getAxes().getXYZLabels());

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

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == selectionSlider)
		{
			if (selectionRenderer != null)
			{
				float sliderVal = selectionSlider.getValue();
				float min = selectionSlider.getMinimum();
				float max = selectionSlider.getMaximum();
				float pointSize = ((sliderVal-min)/(max-min) * (2f-0.06f) + 0.06f);
				Prefs.guiSelectionSphereSize = pointSize;
				selectionRenderer.setSelectPointSize(pointSize);
				detectMultiSelectedPoints();

				lblSelectionCount.setText(getSelectedPointsString());
			}
		}
	}

	private String getSelectedPointsString()
	{
		String selected = "" + selectionRenderer.getMultiSelectedPoints().size();
		String total = "" + dataSet.size();
		int diff = total.length() - selected.length();
		StringBuilder builder = new StringBuilder();
		builder.append(("("));
		// Pad out the selected string with zeros. Prevents component from
		// resizing as the number increases / decreases
		for (int i=0; i < diff; i++)
			builder.append('0');
		builder.append(selected).append(" / ").append(total).append(')');

		return builder.toString();
	}

	private void detectMultiSelectedPoints()
	{
		HashSet<DataPoint> multiSelected = selectionRenderer.detectMultiSelectedPoints();
		glPanel.getScene().setMultiSelected(multiSelected);
	}
}
