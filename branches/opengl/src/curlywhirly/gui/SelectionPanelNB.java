package curlywhirly.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import scri.commons.gui.*;

import curlywhirly.data.*;

public class SelectionPanelNB extends JPanel
{
	private DataSet dataSet;

	private CategoryGroupPanel container;

	private WinMain winMain;

	public SelectionPanelNB(WinMain winMain)
	{
		initComponents();

		this.winMain = winMain;

		RB.setText(lblCategory, "gui.ControlPanel.lblCategory");

		toggleEnabled(false);
	}

	public void setUpCategoryLists()
	{
		ArrayList<CategoryGroup> schemes = dataSet.getCategoryGroups();
		container = new CategoryGroupPanel(this, winMain, schemes, dataSet);

		categorySP.setViewportView(container);
		categorySP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	public void toggleEnabled(boolean enabled)
	{
		lblCategory.setEnabled(enabled);
		categorySP.setEnabled(enabled);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	JScrollPane getScrollPane()
	{
		return categorySP;
	}

	CategoryGroupPanel getCategoryGroupPanel()
	{
		return container;
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        categorySP = new javax.swing.JScrollPane();
        lblCategory = new javax.swing.JLabel();

        lblCategory.setText("Category selection:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categorySP, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCategory)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categorySP, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane categorySP;
    javax.swing.JLabel lblCategory;
    // End of variables declaration//GEN-END:variables

}