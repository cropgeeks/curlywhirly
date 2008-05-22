/*
 * MTControlPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package graphviewer3d.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  __USER__
 */
public class MTControlPanel extends javax.swing.JPanel implements ListSelectionListener
{
	
	//==========================================vars============================================	
	
	public int[] indexes;
	GraphViewerFrame frame;
	Vector<String> categories;
	
	//==========================================c'tor============================================	
	
	/** Creates new form MTControlPanel */
	public MTControlPanel(GraphViewerFrame frame)
	{
		initComponents();
		this.frame = frame;
		setUpListData();
	}
	
	//==========================================methods============================================
	
	private void setUpListData()
	{
		//get the categories and sort them into a usable array for this
		categories = frame.dataSet.getCategories();
		Collections.sort(categories);
		String[] categoriesArray = new String[categories.size()];
		categories.toArray(categoriesArray);
		
		// table for selecting categories to highlight
		selectorList.setListData(categoriesArray);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void selectorListValueChanged(javax.swing.event.ListSelectionEvent evt)
	{
		if (evt.getValueIsAdjusting())
			return;
		updateColourCoding();
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void bgComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		int bgColour = bgCombo.getSelectedIndex();
		frame.canvas3D.setBackgroundColour(bgColour);
		
		// --------------------------------------------------------------------------------------------------------------------------------------------------
	}
	
	private void resetButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		selectorList.clearSelection();
		frame.canvas3D.colourSpheres(null);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void updateColourCoding()
	{
		indexes = selectorList.getSelectedIndices();
		//get the view to update itself
		//pass it a vector of categories to highlight
		Vector<String> updatableCategories = new Vector();
		for (int i = 0; i < indexes.length; i++)
		{
			updatableCategories.add((String) categories.get(indexes[i]));
		}
		frame.canvas3D.colourSpheres(updatableCategories);
	}
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();
		jLabel2 = new javax.swing.JLabel();
		jComboBox2 = new javax.swing.JComboBox();
		jLabel3 = new javax.swing.JLabel();
		jComboBox3 = new javax.swing.JComboBox();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		selectorList = new javax.swing.JList();
		resetButton = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		bgCombo = new javax.swing.JComboBox();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data to display:"));
		
		jLabel1.setText("x-axis:");
		
		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		jComboBox1.setBorder(null);
		
		jLabel2.setText("y-axis:");
		
		jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		jComboBox2.setBorder(null);
		
		jLabel3.setText("z-axis:");
		
		jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "Item 1", "Item 2", "Item 3", "Item 4" }));
		jComboBox3.setBorder(null);
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel1Layout.createSequentialGroup().addContainerGap().add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														jLabel1).add(
														jLabel2).add(
														jLabel3)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														jComboBox3,
														0,
														162,
														Short.MAX_VALUE).add(
														jComboBox2,
														0,
														162,
														Short.MAX_VALUE).add(
														jComboBox1,
														0,
														162,
														Short.MAX_VALUE)).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel1Layout.createSequentialGroup().addContainerGap().add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel1).add(
														jComboBox1,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel2).add(
														jComboBox2,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel3).add(
														jComboBox3,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Highlight categories:"));
		
		selectorList.setModel(new javax.swing.AbstractListModel()
		{
			String[] strings =
			{ "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
			
			public int getSize()
			{
				return strings.length;
			}
			
			public Object getElementAt(int i)
			{
				return strings[i];
			}
		});
		selectorList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
		{
			public void valueChanged(javax.swing.event.ListSelectionEvent evt)
			{
				selectorListValueChanged(evt);
			}
		});
		jScrollPane1.setViewportView(selectorList);
		
		resetButton.setText("Restore all colours");
		resetButton.setName("null");
		resetButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				resetButtonActionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel2Layout.createSequentialGroup().addContainerGap().add(
										jPanel2Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.CENTER).add(
														resetButton).add(
														jScrollPane1,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														199,
														Short.MAX_VALUE)).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						jPanel2Layout.createSequentialGroup().addContainerGap().add(
										jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										301, Short.MAX_VALUE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										resetButton)));
		
		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Select background colour:"));
		
		bgCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "light grey", "dark grey", "black", "white" }));
		bgCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bgComboActionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						jPanel3Layout.createSequentialGroup().addContainerGap().add(bgCombo, 0,
										199, Short.MAX_VALUE).addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						jPanel3Layout.createSequentialGroup().addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).add(
										bgCombo,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						layout.createSequentialGroup().addContainerGap().add(
										layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.TRAILING).add(
														org.jdesktop.layout.GroupLayout.LEADING,
														jPanel3,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).add(
														org.jdesktop.layout.GroupLayout.LEADING,
														jPanel2,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).add(
														org.jdesktop.layout.GroupLayout.LEADING,
														jPanel1,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										jPanel3,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel2,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JComboBox bgCombo;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JComboBox jComboBox2;
	private javax.swing.JComboBox jComboBox3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton resetButton;
	private javax.swing.JList selectorList;
	// End of variables declaration//GEN-END:variables
	
}
