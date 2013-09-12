package curlywhirly.gui;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

import curlywhirly.data.*;

public class ControlPanel extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private WinMain winMain;

	public ControlPanel(WinMain winMain)
	{
		initComponents();

		this.winMain = winMain;

		RB.setText(lblAxesTitle, "gui.ControlPanel.axesTitle");
		RB.setText(lblX, "gui.ControlPanel.lblX");
		RB.setText(lblY, "gui.ControlPanel.lblY");
		RB.setText(lblZ, "gui.ControlPanel.lblZ");
		RB.setText(lblCategory, "gui.ControlPanel.lblCategory");

		toggleEnabled(false);
	}

	public void addComboModels()
	{
		String[] axisLabels = dataSet.getAxisLabels();
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		yCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));
		zCombo.setModel(new DefaultComboBoxModel<String>(axisLabels));

		resetComboBoxes();
	}

	public void resetComboBoxes()
	{
		// set the combos to display the currently selected index of the variables they display
		xCombo.setSelectedIndex(0);
		yCombo.setSelectedIndex(1);
		zCombo.setSelectedIndex(2);
	}

	public void setUpCategoryLists()
	{
		ArrayList<CategoryGroup> schemes = dataSet.getCategoryGroups();
		CategoryGroupPanel container = new CategoryGroupPanel(winMain, schemes, dataSet);

		categorySP.setViewportView(container.getPanel());
		categorySP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == xCombo)
		{
			int index = xCombo.getSelectedIndex();
			dataSet.setCurrX(index);
		}

		else if (e.getSource() == yCombo)
		{
			int index = yCombo.getSelectedIndex();
			dataSet.setCurrY(index);
		}

		else if (e.getSource() == zCombo)
		{
			int index = zCombo.getSelectedIndex();
			dataSet.setCurrZ(index);
		}
	}

	public void toggleEnabled(boolean enabled)
	{
		lblX.setEnabled(enabled);
		lblY.setEnabled(enabled);
		lblZ.setEnabled(enabled);
		lblAxesTitle.setEnabled(enabled);
		lblCategory.setEnabled(enabled);
		xCombo.setEnabled(enabled);
		yCombo.setEnabled(enabled);
		zCombo.setEnabled(enabled);
		categorySP.setEnabled(enabled);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        lblAxesTitle = new javax.swing.JLabel();
        lblX = new javax.swing.JLabel();
        lblY = new javax.swing.JLabel();
        xCombo = new javax.swing.JComboBox<String>();
        yCombo = new javax.swing.JComboBox<String>();
        lblZ = new javax.swing.JLabel();
        zCombo = new javax.swing.JComboBox<String>();
        categorySP = new javax.swing.JScrollPane();
        lblCategory = new javax.swing.JLabel();

        lblAxesTitle.setText("Data to display:");

        lblX.setText("x-axis:");

        lblY.setText("y-axis:");

        xCombo.setBorder(null);
        xCombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ControlPanel.this.actionPerformed(evt);
            }
        });

        yCombo.setBorder(null);
        yCombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ControlPanel.this.actionPerformed(evt);
            }
        });

        lblZ.setText("z-axis:");

        zCombo.setBorder(null);
        zCombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ControlPanel.this.actionPerformed(evt);
            }
        });

        lblCategory.setText("Category selection:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categorySP, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblX)
                            .addComponent(lblY)
                            .addComponent(lblZ))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(zCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(xCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAxesTitle)
                            .addComponent(lblCategory))
                        .addGap(0, 111, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAxesTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblX)
                    .addComponent(xCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblY)
                    .addComponent(yCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblZ)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(lblCategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categorySP, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane categorySP;
    private javax.swing.JLabel lblAxesTitle;
    javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JLabel lblZ;
    private javax.swing.JComboBox<String> xCombo;
    private javax.swing.JComboBox<String> yCombo;
    private javax.swing.JComboBox<String> zCombo;
    // End of variables declaration//GEN-END:variables

}