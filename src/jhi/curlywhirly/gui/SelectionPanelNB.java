// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.data.*;
import scri.commons.gui.*;

import javax.swing.*;
import java.awt.event.*;

public class SelectionPanelNB extends JPanel implements ActionListener
{
	private DataSet dataSet;

	private CategoryGroupPanel container;

	private final WinMain winMain;

	public SelectionPanelNB(WinMain winMain)
	{
		initComponents();

		this.winMain = winMain;

		lblSelectAll.addActionListener(this);
		lblSelectNone.addActionListener(this);
		lblExport.addActionListener(this);

		RB.setText(lblCategory, "gui.SelectionPanelNB.lblCategory");
		RB.setText(lblSelectAll, "gui.SelectionPanelNB.selectAll");
		RB.setText(lblSelectNone, "gui.SelectionPanelNB.selectNone");
		RB.setText(lblExport, "gui.SelectionPanelNB.export");

		toggleEnabled(false);
	}

	public void setUpCategoryLists()
	{
		container = new CategoryGroupPanel(this, winMain);
		container.setDataSet(dataSet);

		categorySP.setViewportView(container);
		categorySP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		categorySP.getVerticalScrollBar().setUnitIncrement(15);
	}

	public void toggleEnabled(boolean enabled)
	{
		lblCategory.setEnabled(enabled);
		categorySP.setEnabled(enabled);
		lblSelectAll.setEnabled(enabled);
		lblSelectNone.setEnabled(enabled);
		lblPipe.setEnabled(enabled);
		lblExport.setEnabled(enabled);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		setUpCategoryLists();

		toggleEnabled(dataSet != null);
	}

	JScrollPane getScrollPane()
	{
		return categorySP;
	}

	CategoryGroupPanel getCategoryGroupPanel()
	{
		return container;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == lblSelectAll)
			container.selectAll();

		else if (e.getSource() == lblSelectNone)
			container.selectNone();

		else if (e.getSource() == lblExport)
			container.exportCategories();
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		categorySP = new javax.swing.JScrollPane();
		lblCategory = new javax.swing.JLabel();
		lblSelectAll = new scri.commons.gui.matisse.HyperLinkLabel();
		lblPipe = new javax.swing.JLabel();
		lblSelectNone = new scri.commons.gui.matisse.HyperLinkLabel();
		lblExport = new scri.commons.gui.matisse.HyperLinkLabel();

		lblCategory.setText("Category selection:");

		lblSelectAll.setForeground(new java.awt.Color(68, 106, 156));
		lblSelectAll.setText("Select all");

		lblPipe.setText("|");

		lblSelectNone.setForeground(new java.awt.Color(68, 106, 156));
		lblSelectNone.setText("Select none");

		lblExport.setForeground(new java.awt.Color(68, 106, 156));
		lblExport.setText("Export");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(categorySP, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblCategory)
								.addGroup(layout.createSequentialGroup()
									.addComponent(lblSelectAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(lblPipe)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(lblSelectNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(lblCategory)
						.addComponent(lblExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(categorySP, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(lblSelectAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addComponent(lblPipe))
						.addComponent(lblSelectNone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane categorySP;
	javax.swing.JLabel lblCategory;
	private scri.commons.gui.matisse.HyperLinkLabel lblExport;
	private javax.swing.JLabel lblPipe;
	private scri.commons.gui.matisse.HyperLinkLabel lblSelectAll;
	private scri.commons.gui.matisse.HyperLinkLabel lblSelectNone;
	// End of variables declaration//GEN-END:variables

}