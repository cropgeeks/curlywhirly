// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class ControlsPanelNB extends JPanel implements ActionListener, ChangeListener
{
	private final WinMain winMain;
	private Axes axes;

    /** Creates new form ControlsPanelNB */
    public ControlsPanelNB(WinMain winMain)
	{
        initComponents();

		this.winMain = winMain;

		axisPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		RB.setText(lblAxesTitle, "gui.ControlPanel.axesTitle");
		RB.setText(lblX, "gui.ControlPanel.lblX");
		RB.setText(lblY, "gui.ControlPanel.lblY");
		RB.setText(lblZ, "gui.ControlPanel.lblZ");

		axisLabelPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		RB.setText(lblAxisLabelOptions, "gui.ControlPanel.axisLabelOptions");
		RB.setText(chkAxisLabels, "gui.ControlPanel.axisLabels");
		RB.setText(chkDatasetLabels, "gui.ControlPanel.datasetLabels");

		chkAxisLabels.setSelected(Prefs.guiChkAxisLabels);
		chkDatasetLabels.setSelected(Prefs.guiChkDatasetLabels);
		chkAxisLabels.addActionListener(this);
		chkDatasetLabels.addActionListener(this);


		RB.setText(lblLabelSizeSlider, "gui.ControlPanel.lblLabelSizeSlider");
		labelSizeSlider.setMinimum(1);
		labelSizeSlider.setMaximum(100);
		labelSizeSlider.setValue(50);
		labelSizeSlider.addChangeListener(this);

        spherePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		RB.setText(lblSizeSlider, "gui.ControlPanel.lblSizeSlider");
		pointSizeSlider.setMinimum(1);
		pointSizeSlider.setMaximum(100);
		pointSizeSlider.setValue(50);
		pointSizeSlider.addChangeListener(this);

		RB.setText(lblDeselected, "gui.ControlPanel.lblDeselected");
		deselectedSizeSlider.setMinimum(1);
		deselectedSizeSlider.setMaximum(100);
		deselectedSizeSlider.setValue(50);
		deselectedSizeSlider.addChangeListener(this);

		chkLinkPointSizes.setText(RB.getString("gui.ControlPanel.chkLinkPointSizes"));
		chkLinkPointSizes.setSelected(Prefs.guiChkLinkPointSizes);
		chkLinkPointSizes.addActionListener(this);

		toggleEnabled(false);
    }

	private void addComboModels()
	{
		String[] axisLabels = axes.getAxisLabels();
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		yCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		zCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));

		xCombo.addActionListener(this);
		yCombo.addActionListener(this);
		zCombo.addActionListener(this);

		resetComboBoxes();
	}

	public void resetComboBoxes()
	{
		// set the combos to display the currently selected index of the variables they display
		xCombo.setSelectedIndex(axes.getX());
		yCombo.setSelectedIndex(axes.getY());
		zCombo.setSelectedIndex(axes.getZ());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == xCombo)
		{
			int index = xCombo.getSelectedIndex();
			axes.setX(index);
			winMain.getDataSet().updatePointPositions();
			winMain.getDataPanel().updateTableModel();
		}

		else if (e.getSource() == yCombo)
		{
			int index = yCombo.getSelectedIndex();
			axes.setY(index);
			winMain.getDataSet().updatePointPositions();
			winMain.getDataPanel().updateTableModel();
		}

		else if (e.getSource() == zCombo)
		{
			int index = zCombo.getSelectedIndex();
			axes.setZ(index);
			winMain.getDataSet().updatePointPositions();
			winMain.getDataPanel().updateTableModel();
		}

		else if (e.getSource() == chkAxisLabels)
		{
			Prefs.guiChkAxisLabels = !Prefs.guiChkAxisLabels;
			chkDatasetLabels.setEnabled(Prefs.guiChkAxisLabels);
		}

		else if (e.getSource() == chkDatasetLabels)
			Prefs.guiChkDatasetLabels = !Prefs.guiChkDatasetLabels;

		else if (e.getSource() == chkLinkPointSizes)
		{
			Prefs.guiChkLinkPointSizes = !Prefs.guiChkLinkPointSizes;
			deselectedSizeSlider.setValue(pointSizeSlider.getValue());
		}
	}

	public void toggleEnabled(boolean enabled)
	{
		lblX.setEnabled(enabled);
		lblY.setEnabled(enabled);
		lblZ.setEnabled(enabled);
		axisPanel.setEnabled(enabled);
		lblAxesTitle.setEnabled(enabled);
		xCombo.setEnabled(enabled);
		yCombo.setEnabled(enabled);
		zCombo.setEnabled(enabled);
		lblAxisLabelOptions.setEnabled(enabled);
		axisPanel.setEnabled(enabled);
		chkAxisLabels.setEnabled(enabled);
		chkDatasetLabels.setEnabled(enabled);
		labelSizeSlider.setEnabled(enabled);
        pointSizeSlider.setEnabled(enabled);
        spherePanelLabel.setEnabled(enabled);
        spherePanel.setEnabled(enabled);
        lblSizeSlider.setEnabled(enabled);
		chkLinkPointSizes.setEnabled(enabled);
		deselectedSizeSlider.setEnabled(enabled);
		lblDeselected.setEnabled(enabled);

		if (enabled)
			chkDatasetLabels.setEnabled(Prefs.guiChkAxisLabels);
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

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == pointSizeSlider)
		{
			float size = scaleSliderValue(pointSizeSlider, 0.05f, 0.009f);
			winMain.getOpenGLPanel().getSphereRenderer().setPointSize(size);

			if (Prefs.guiChkLinkPointSizes)
				deselectedSizeSlider.setValue(pointSizeSlider.getValue());
		}

		else if (e.getSource() == deselectedSizeSlider)
		{
			float size = scaleSliderValue(deselectedSizeSlider, 0.05f, 0.009f);
			winMain.getOpenGLPanel().getSphereRenderer().setDeselectedSize(size);

			if (Prefs.guiChkLinkPointSizes)
				pointSizeSlider.setValue(deselectedSizeSlider.getValue());
		}

		else if (e.getSource() == labelSizeSlider)
		{
			float size = scaleSliderValue(labelSizeSlider, 0.06f, 0.01f);
			winMain.getOpenGLPanel().getAxesRenderer().setAxisLabelSize(size);
		}
	}

	private float scaleSliderValue(JSlider slider, float sceneMax, float sceneMin)
	{
		float sliderVal = slider.getValue();
		float min = slider.getMinimum();
		float max = slider.getMaximum();
		float size = ((sliderVal-min)/(max-1f) * (sceneMax-sceneMin) + sceneMin);

		return size;
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

        lblAxesTitle = new javax.swing.JLabel();
        axisPanel = new javax.swing.JPanel();
        lblX = new javax.swing.JLabel();
        xCombo = new javax.swing.JComboBox<String>();
        lblZ = new javax.swing.JLabel();
        yCombo = new javax.swing.JComboBox<String>();
        lblY = new javax.swing.JLabel();
        zCombo = new javax.swing.JComboBox<String>();
        axisLabelPanel = new javax.swing.JPanel();
        chkAxisLabels = new javax.swing.JCheckBox();
        chkDatasetLabels = new javax.swing.JCheckBox();
        lblLabelSizeSlider = new javax.swing.JLabel();
        labelSizeSlider = new javax.swing.JSlider();
        lblAxisLabelOptions = new javax.swing.JLabel();
        spherePanelLabel = new javax.swing.JLabel();
        spherePanel = new javax.swing.JPanel();
        pointSizeSlider = new javax.swing.JSlider();
        lblSizeSlider = new javax.swing.JLabel();
        chkLinkPointSizes = new javax.swing.JCheckBox();
        lblDeselected = new javax.swing.JLabel();
        deselectedSizeSlider = new javax.swing.JSlider();

        lblAxesTitle.setText("Data to display:");

        axisPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblX.setText("x-axis:");

        xCombo.setBorder(null);

        lblZ.setText("z-axis:");

        yCombo.setBorder(null);

        lblY.setText("y-axis:");

        zCombo.setBorder(null);

        javax.swing.GroupLayout axisPanelLayout = new javax.swing.GroupLayout(axisPanel);
        axisPanel.setLayout(axisPanelLayout);
        axisPanelLayout.setHorizontalGroup(
            axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(axisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblX)
                    .addComponent(lblY)
                    .addComponent(lblZ))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(yCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        axisPanelLayout.setVerticalGroup(
            axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(axisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblX)
                    .addComponent(xCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblY)
                    .addComponent(yCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(axisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblZ)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        axisLabelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        chkAxisLabels.setText("Show axis labels");

        chkDatasetLabels.setText("Use dataset labels");

        lblLabelSizeSlider.setText("Size:");

        javax.swing.GroupLayout axisLabelPanelLayout = new javax.swing.GroupLayout(axisLabelPanel);
        axisLabelPanel.setLayout(axisLabelPanelLayout);
        axisLabelPanelLayout.setHorizontalGroup(
            axisLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(axisLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(axisLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(axisLabelPanelLayout.createSequentialGroup()
                        .addComponent(lblLabelSizeSlider)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(axisLabelPanelLayout.createSequentialGroup()
                        .addGroup(axisLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkAxisLabels)
                            .addComponent(chkDatasetLabels))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
        );
        axisLabelPanelLayout.setVerticalGroup(
            axisLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(axisLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAxisLabels)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDatasetLabels)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(axisLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLabelSizeSlider)
                    .addComponent(labelSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblAxisLabelOptions.setText("Axis label options:");

        spherePanelLabel.setText("Sphere options:");

        spherePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblSizeSlider.setLabelFor(pointSizeSlider);
        lblSizeSlider.setText("Selected size:");

        chkLinkPointSizes.setText("Link");

        lblDeselected.setLabelFor(deselectedSizeSlider);
        lblDeselected.setText("Deselected size:");

        javax.swing.GroupLayout spherePanelLayout = new javax.swing.GroupLayout(spherePanel);
        spherePanel.setLayout(spherePanelLayout);
        spherePanelLayout.setHorizontalGroup(
            spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spherePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSizeSlider)
                    .addComponent(chkLinkPointSizes)
                    .addComponent(lblDeselected))
                .addGap(18, 18, 18)
                .addGroup(spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pointSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(deselectedSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        spherePanelLayout.setVerticalGroup(
            spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spherePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spherePanelLayout.createSequentialGroup()
                        .addComponent(chkLinkPointSizes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSizeSlider))
                    .addComponent(pointSizeSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(spherePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(deselectedSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDeselected))
                .addGap(50, 50, 50))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(axisPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblAxesTitle, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAxisLabelOptions, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spherePanelLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(axisLabelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spherePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAxesTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axisPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblAxisLabelOptions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(axisLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(spherePanelLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spherePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel axisLabelPanel;
    private javax.swing.JPanel axisPanel;
    private javax.swing.JCheckBox chkAxisLabels;
    private javax.swing.JCheckBox chkDatasetLabels;
    private javax.swing.JCheckBox chkLinkPointSizes;
    private javax.swing.JSlider deselectedSizeSlider;
    private javax.swing.JSlider labelSizeSlider;
    private javax.swing.JLabel lblAxesTitle;
    private javax.swing.JLabel lblAxisLabelOptions;
    private javax.swing.JLabel lblDeselected;
    private javax.swing.JLabel lblLabelSizeSlider;
    private javax.swing.JLabel lblSizeSlider;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JLabel lblZ;
    javax.swing.JSlider pointSizeSlider;
    private javax.swing.JPanel spherePanel;
    private javax.swing.JLabel spherePanelLabel;
    private javax.swing.JComboBox<String> xCombo;
    private javax.swing.JComboBox<String> yCombo;
    private javax.swing.JComboBox<String> zCombo;
    // End of variables declaration//GEN-END:variables

}