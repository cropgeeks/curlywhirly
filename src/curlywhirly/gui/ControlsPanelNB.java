// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import javax.swing.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.*;

import scri.commons.gui.*;

public class ControlsPanelNB extends JPanel
{
	private final WinMain winMain;
	private Axes axes;

    /** Creates new form ControlsPanelNB */
    public ControlsPanelNB(WinMain winMain)
	{
        initComponents();

		this.winMain = winMain;

		setupCoordinatesPanel();
		setupAdvancedPanel();
		setupDeselectedPanel();

		toggleEnabled(false);
    }

	private void setupCoordinatesPanel()
	{
		dataPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.ControlPanel.dataTitle")));
		RB.setText(lblX, "gui.ControlPanel.lblX");
		RB.setText(lblY, "gui.ControlPanel.lblY");
		RB.setText(lblZ, "gui.ControlPanel.lblZ");
	}

	private void setupAdvancedPanel()
	{
		advancedPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.ControlPanel.advancedOptionsTitle")));

		setupAxisLabelsCheckBox();
		setupDatasetLabelsCheckbox();
		setupAxisTicksCheckbox();
		setupAxisLabelSizeSlider();
		setupPointSizeSlider();
		setupDeselectedSlider();
		setupSphereDetailSlider();
		setupResetLabel();
	}

	private void setupAxisLabelsCheckBox()
	{
		RB.setText(chkAxisLabels, "gui.ControlPanel.axisLabels");
		chkAxisLabels.setSelected(Prefs.guiChkAxisLabels);
		chkAxisLabels.addActionListener(e -> axisLabelsListener());
	}

	private void setupDatasetLabelsCheckbox()
	{
		RB.setText(chkDatasetLabels, "gui.ControlPanel.datasetLabels");
		chkDatasetLabels.setSelected(Prefs.guiChkDatasetLabels);
		chkDatasetLabels.addActionListener(e -> dataSetLabelsListener());
	}

	private void setupAxisTicksCheckbox()
	{
		RB.setText(chkAxisTicks, "gui.ControlPanel.axisTicks");
		chkAxisTicks.setSelected(Prefs.guiDrawAxisTicks);
		chkAxisTicks.addActionListener(e -> axisTicksListener());
	}

	private void setupAxisLabelSizeSlider()
	{
		RB.setText(lblAxislLabelSize, "gui.ControlPanel.lblAxisLabelSize");
		axisLabelSizeSlider.setMinimum(1);
		axisLabelSizeSlider.setMaximum(100);
		axisLabelSizeSlider.setValue(50);
		axisLabelSizeSlider.addChangeListener(e -> axisLabelSizeSliderChanged());
	}

	private void setupPointSizeSlider()
	{
		RB.setText(lblSizeSlider, "gui.ControlPanel.lblSizeSlider");
		pointSizeSlider.setMinimum(1);
		pointSizeSlider.setMaximum(100);
		pointSizeSlider.setValue(50);
		pointSizeSlider.addChangeListener(e -> pointSizeSliderChanged());
	}

	private void setupDeselectedSlider()
	{
		RB.setText(lblDeselected, "gui.ControlPanel.lblDeselected");
		deselectedSizeSlider.setMinimum(1);
		deselectedSizeSlider.setMaximum(100);
		deselectedSizeSlider.setValue(50);
		deselectedSizeSlider.addChangeListener(e -> deselectedSliderChanged());
	}

	private void setupSphereDetailSlider()
	{
		RB.setText(lblSphereDetail, "gui.ControlPanel.lblSphereDetail");
		sphereDetailSlider.setMinimum(0);
		sphereDetailSlider.setMaximum(4);
		sphereDetailSlider.setValue(2);
		sphereDetailSlider.addChangeListener(e -> sphereDetailChanged());
	}

	private void setupResetLabel()
	{
		RB.setText(hlblReset, "gui.ControlPanel.hlblReset");
		hlblReset.addActionListener(e -> reset());
	}

	private void setupDeselectedPanel()
	{
		deselectedPanel.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.ControlPanel.deslectedPanelTitle")));

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbGrey);
		bg.add(rbTransparent);
		bg.add(rbInvisible);

		RB.setText(rbGrey, "gui.ControlPanel.rbGrey");
		rbGrey.addActionListener(e -> rbGreyListener());

		RB.setText(rbTransparent, "gui.ControlPanel.rbTransparent");
		rbTransparent.addActionListener(e -> rbTransparentListener());

		RB.setText(rbInvisible, "gui.ControlPanel.rbInvisible");
		rbInvisible.addActionListener(e -> rbInvisibleListener());

		rbGrey.setSelected(true);
	}

	public void setDataSet(DataSet dataSet)
	{
		if (dataSet == null)
		{
			// Blank out the combo boxes if we don't have a dataset
			xCombo.setModel(new DefaultComboBoxModel<String>());
			yCombo.setModel(new DefaultComboBoxModel<String>());
			zCombo.setModel(new DefaultComboBoxModel<String>());
		}
		else
		{
			this.axes = dataSet.getAxes();
			addComboModels();
		}

		toggleEnabled(dataSet != null);
	}

	private void addComboModels()
	{
		setupXComboBox(axes.getAxisLabels());
		setupYComboBox(axes.getAxisLabels());
		setupZComboBox(axes.getAxisLabels());

		resetComboBoxes();
	}

	private void setupXComboBox(String[] axisLabels)
	{
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		xCombo.addActionListener(e -> xComboListener());
	}

	private void setupYComboBox(String[] axisLabels)
	{
		yCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		yCombo.addActionListener(e -> yComboListener());
	}

	private void setupZComboBox(String[] axisLabels)
	{
		zCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		zCombo.addActionListener(e -> zComboListener());
	}

	public void resetComboBoxes()
	{
		// set the combos to display the currently selected index of the variables they display
		xCombo.setSelectedIndex(axes.getX());
		yCombo.setSelectedIndex(axes.getY());
		zCombo.setSelectedIndex(axes.getZ());
	}

	private void reset()
	{
		axisLabelSizeSlider.setValue(50);
		pointSizeSlider.setValue(50);
		deselectedSizeSlider.setValue(50);
		sphereDetailSlider.setValue(2);
	}

	private void toggleEnabled(boolean enabled)
	{
		// Data panel variables
		lblX.setEnabled(enabled);
		lblY.setEnabled(enabled);
		lblZ.setEnabled(enabled);
		dataPanel.setEnabled(enabled);
		xCombo.setEnabled(enabled);
		yCombo.setEnabled(enabled);
		zCombo.setEnabled(enabled);
		// Advanced panel variables
		advancedPanel.setEnabled(enabled);
		chkAxisLabels.setEnabled(enabled);
		chkDatasetLabels.setEnabled(enabled);
		lblAxislLabelSize.setEnabled(enabled);
		axisLabelSizeSlider.setEnabled(enabled);
        pointSizeSlider.setEnabled(enabled);
        lblSizeSlider.setEnabled(enabled);
		deselectedSizeSlider.setEnabled(enabled);
		lblDeselected.setEnabled(enabled);
		lblSphereDetail.setEnabled(enabled);
		sphereDetailSlider.setEnabled(enabled);
		hlblReset.setEnabled(enabled);
		// Deselected panel variables
		deselectedPanel.setEnabled(enabled);
		rbGrey.setEnabled(enabled);
		rbTransparent.setEnabled(enabled);
		rbInvisible.setEnabled(enabled);

		if (enabled)
			chkDatasetLabels.setEnabled(Prefs.guiChkAxisLabels);
	}

	private float scaleSliderValue(JSlider slider, float sceneMin, float sceneMax)
	{
		float sliderVal = slider.getValue();
		float min = slider.getMinimum();
		float max = slider.getMaximum();
		float size = ((sliderVal-min)/(max-1f) * (sceneMax-sceneMin) + sceneMin);

		return size;
	}

	// ActionEvent listeners

	private void xComboListener()
	{
		axes.setX(xCombo.getSelectedIndex());
		winMain.getDataSet().updatePointPositions();
		winMain.getDataPanel().updateTableModel();
	}

	private void yComboListener()
	{
		axes.setY(yCombo.getSelectedIndex());
		winMain.getDataSet().updatePointPositions();
		winMain.getDataPanel().updateTableModel();
	}

	private void zComboListener()
	{
		axes.setZ(zCombo.getSelectedIndex());
		winMain.getDataSet().updatePointPositions();
		winMain.getDataPanel().updateTableModel();
	}

	private void axisLabelsListener()
	{
		Prefs.guiChkAxisLabels = !Prefs.guiChkAxisLabels;
		chkDatasetLabels.setEnabled(Prefs.guiChkAxisLabels);
	}

	private void dataSetLabelsListener()
	{
		Prefs.guiChkDatasetLabels = !Prefs.guiChkDatasetLabels;
	}

	private void axisTicksListener()
	{
		Prefs.guiDrawAxisTicks = !Prefs.guiDrawAxisTicks;
	}

	private void rbGreyListener()
	{
		winMain.getOpenGLPanel().setDeselectedSphereRenderer(new DeselectedSphereRendererGrey());
	}

	private void rbTransparentListener()
	{
		winMain.getOpenGLPanel().setDeselectedSphereRenderer(new DeselectedSphereRendererTransparent());
	}

	private void rbInvisibleListener()
	{
		winMain.getOpenGLPanel().setDeselectedSphereRenderer(new NullSphereRenderer());
	}

	// ChangeEvent listeners

	private void axisLabelSizeSliderChanged()
	{
		float size = scaleSliderValue(axisLabelSizeSlider, 0.01f, 0.06f);
		winMain.getOpenGLPanel().getAxesRenderer().setAxisLabelSize(size);
	}

	private void pointSizeSliderChanged()
	{
		float size = scaleSliderValue(pointSizeSlider, 0.002f, 0.05f);
		winMain.getOpenGLPanel().getSphereRenderer().setPointSize(size);

//		deselectedSizeSlider.setValue(pointSizeSlider.getValue());
	}

	private void deselectedSliderChanged()
	{
		float size = scaleSliderValue(deselectedSizeSlider, 0.002f, 0.05f);
		winMain.getOpenGLPanel().getDeselectedSphereRenderer().setPointSize(size);

//		pointSizeSlider.setValue(deselectedSizeSlider.getValue());
	}

	private void sphereDetailChanged()
	{
		winMain.getOpenGLPanel().getSphereRenderer().setSphereDetailLevel(sphereDetailSlider.getValue());
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        dataPanel = new javax.swing.JPanel();
        lblX = new javax.swing.JLabel();
        xCombo = new javax.swing.JComboBox<String>();
        lblZ = new javax.swing.JLabel();
        yCombo = new javax.swing.JComboBox<String>();
        lblY = new javax.swing.JLabel();
        zCombo = new javax.swing.JComboBox<String>();
        advancedPanel = new javax.swing.JPanel();
        chkAxisLabels = new javax.swing.JCheckBox();
        chkDatasetLabels = new javax.swing.JCheckBox();
        lblAxislLabelSize = new javax.swing.JLabel();
        axisLabelSizeSlider = new javax.swing.JSlider();
        lblSizeSlider = new javax.swing.JLabel();
        pointSizeSlider = new javax.swing.JSlider();
        lblDeselected = new javax.swing.JLabel();
        deselectedSizeSlider = new javax.swing.JSlider();
        lblSphereDetail = new javax.swing.JLabel();
        sphereDetailSlider = new javax.swing.JSlider();
        hlblReset = new scri.commons.gui.matisse.HyperLinkLabel();
        chkAxisTicks = new javax.swing.JCheckBox();
        deselectedPanel = new javax.swing.JPanel();
        rbTransparent = new javax.swing.JRadioButton();
        rbGrey = new javax.swing.JRadioButton();
        rbInvisible = new javax.swing.JRadioButton();

        dataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Coordinate options:"));

        lblX.setText("x-axis:");

        xCombo.setBorder(null);

        lblZ.setText("z-axis:");

        yCombo.setBorder(null);

        lblY.setText("y-axis:");

        zCombo.setBorder(null);

        javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(dataPanel);
        dataPanel.setLayout(dataPanelLayout);
        dataPanelLayout.setHorizontalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblX)
                    .addComponent(lblY)
                    .addComponent(lblZ))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        dataPanelLayout.setVerticalGroup(
            dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblX)
                    .addComponent(xCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblY)
                    .addComponent(yCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblZ)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        advancedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced options:"));

        chkAxisLabels.setText("Show axis labels");

        chkDatasetLabels.setText("Use dataset coordinate names");

        lblAxislLabelSize.setText("Axis labels size:");

        lblSizeSlider.setLabelFor(pointSizeSlider);
        lblSizeSlider.setText("Selected point size:");

        lblDeselected.setLabelFor(deselectedSizeSlider);
        lblDeselected.setText("Deselected point size:");

        lblSphereDetail.setLabelFor(sphereDetailSlider);
        lblSphereDetail.setText("Point quality:");

        hlblReset.setForeground(new java.awt.Color(68, 106, 156));
        hlblReset.setText("Reset to defaults");

        chkAxisTicks.setText("Show axis ticks");

        javax.swing.GroupLayout advancedPanelLayout = new javax.swing.GroupLayout(advancedPanel);
        advancedPanel.setLayout(advancedPanelLayout);
        advancedPanelLayout.setHorizontalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(hlblReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(advancedPanelLayout.createSequentialGroup()
                        .addComponent(lblSphereDetail)
                        .addGap(60, 60, 60)
                        .addComponent(sphereDetailSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(advancedPanelLayout.createSequentialGroup()
                        .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSizeSlider)
                            .addComponent(lblDeselected))
                        .addGap(18, 18, 18)
                        .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(axisLabelSizeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(pointSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(deselectedSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(advancedPanelLayout.createSequentialGroup()
                        .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkAxisTicks)
                            .addComponent(chkAxisLabels)
                            .addComponent(chkDatasetLabels)
                            .addComponent(lblAxislLabelSize))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        advancedPanelLayout.setVerticalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAxisLabels)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDatasetLabels)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAxisTicks)
                .addGap(18, 18, 18)
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAxislLabelSize)
                    .addComponent(axisLabelSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSizeSlider, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pointSizeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(deselectedSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDeselected))
                .addGap(18, 18, 18)
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSphereDetail)
                    .addComponent(sphereDetailSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hlblReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        deselectedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Deselected points:"));

        rbTransparent.setText("Transparent");

        rbGrey.setText("Greyscale");

        rbInvisible.setText("Invisible");

        javax.swing.GroupLayout deselectedPanelLayout = new javax.swing.GroupLayout(deselectedPanel);
        deselectedPanel.setLayout(deselectedPanelLayout);
        deselectedPanelLayout.setHorizontalGroup(
            deselectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deselectedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deselectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbInvisible)
                    .addComponent(rbTransparent)
                    .addComponent(rbGrey))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        deselectedPanelLayout.setVerticalGroup(
            deselectedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deselectedPanelLayout.createSequentialGroup()
                .addComponent(rbGrey)
                .addGap(3, 3, 3)
                .addComponent(rbTransparent)
                .addGap(4, 4, 4)
                .addComponent(rbInvisible)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(advancedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deselectedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(advancedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deselectedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JSlider axisLabelSizeSlider;
    private javax.swing.JCheckBox chkAxisLabels;
    private javax.swing.JCheckBox chkAxisTicks;
    private javax.swing.JCheckBox chkDatasetLabels;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel deselectedPanel;
    private javax.swing.JSlider deselectedSizeSlider;
    private scri.commons.gui.matisse.HyperLinkLabel hlblReset;
    private javax.swing.JLabel lblAxislLabelSize;
    private javax.swing.JLabel lblDeselected;
    private javax.swing.JLabel lblSizeSlider;
    private javax.swing.JLabel lblSphereDetail;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JLabel lblZ;
    private javax.swing.JSlider pointSizeSlider;
    private javax.swing.JRadioButton rbGrey;
    private javax.swing.JRadioButton rbInvisible;
    private javax.swing.JRadioButton rbTransparent;
    private javax.swing.JSlider sphereDetailSlider;
    private javax.swing.JComboBox<String> xCombo;
    private javax.swing.JComboBox<String> yCombo;
    private javax.swing.JComboBox<String> zCombo;
    // End of variables declaration//GEN-END:variables

}