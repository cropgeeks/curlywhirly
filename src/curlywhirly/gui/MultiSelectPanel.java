package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;
import curlywhirly.gui.dialog.*;
import curlywhirly.gui.viewer.opengl.*;

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
		add(lblOptions, BorderLayout.EAST);

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

		lblOptions = new HyperLinkLabel();
		RB.setText(lblOptions, "gui.viewer.MultiSelectPanel.lblOptions");
		lblOptions.addActionListener(this);

		setupSlider();
		setupComboBox();

		setVisible(false);
	}

	private JPanel setupCentrePanel()
	{
		JPanel centrePanel = new JPanel();
		centrePanel.setLayout(new FlowLayout());
		centrePanel.add(lblSelection);
		centrePanel.add(selectionSlider);
		centrePanel.add(lblSelectionCount);
		centrePanel.add(lblAction);
		centrePanel.add(selectionTypeCombo);
		centrePanel.add(bOk);
		centrePanel.add(bCancel);

		return centrePanel;
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
		selectionSlider.setValue((int) Prefs.guiSelectionSphereSize);
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
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == selectionSlider)
		{
			Prefs.guiSelectionSphereSize = selectionSlider.getValue();
			if (selectionRenderer != null)
			{
				selectionRenderer.setSelectPointSize(selectionSlider.getValue());
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
			builder.append("0");
		builder.append(selected).append(" / ").append(total).append(")");

		return builder.toString();
	}

	private void detectMultiSelectedPoints()
	{
		HashSet<DataPoint> multiSelected = selectionRenderer.detectMultiSelectedPoints();
		glPanel.getScene().setMultiSelected(multiSelected);
	}
}
