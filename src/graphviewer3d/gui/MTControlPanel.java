/*
 * MTControlPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package graphviewer3d.gui;

import graphviewer3d.data.Category;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.Color3f;

public class MTControlPanel extends javax.swing.JPanel
{
	
	// ==========================================vars============================================
	
	private int[] indexes;
	GraphViewerFrame frame;
	private Vector<String> categories;
	Vector<Category> listItems;
	
	// ==========================================c'tor============================================
	
	/** Creates new form MTControlPanel */
	public MTControlPanel(GraphViewerFrame frame)
	{
		initComponents();
		this.frame = frame;
	}
	
	// ==========================================methods============================================
	
	public void doAdditionalComponentConfig()
	{
		// set the Vector with the data headers as the model for the combo boxes that allow selection of variables
		Vector dataHeaders = frame.dataSet.dataHeaders;
		xCombo.setModel(new DefaultComboBoxModel(dataHeaders));
		yCombo.setModel(new DefaultComboBoxModel(dataHeaders));
		zCombo.setModel(new DefaultComboBoxModel(dataHeaders));
		
		// set the combos to display the currently selected index of the variables they display
		xCombo.setSelectedIndex(frame.canvas3D.currentXIndex);
		yCombo.setSelectedIndex(frame.canvas3D.currentYIndex);
		zCombo.setSelectedIndex(frame.canvas3D.currentZIndex);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void setUpListData()
	{
		// get the list items
		listItems = new Vector(frame.dataSet.categoryMap.values());
		Collections.sort(listItems);
		// table for selecting categories to highlight
		selectorList.setListData(listItems);
		selectorList.setCellRenderer(new ColorListRenderer());
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void selectorListValueChanged(javax.swing.event.ListSelectionEvent evt)
	{
		if (evt.getValueIsAdjusting())
			return;
		Object [] selectedObjects = selectorList.getSelectedValues();
		System.out.println("===================");
		for (int i = 0; i < selectedObjects.length; i++)
		{
			Category cat = (Category)selectedObjects[i];
			System.out.println("selected object = " + cat.name);
		}
		frame.canvas3D.selectedObjects = selectedObjects;
		frame.canvas3D.highlightAllCategories = false;
		frame.canvas3D.updateGraph();	
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
		frame.canvas3D.updateGraph();
		selectorList.clearSelection();
		frame.canvas3D.colourSpheres();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void zComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		int index = zCombo.getSelectedIndex();
		frame.canvas3D.currentZIndex = index;
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
		System.out.println("z value changed to " + frame.canvas3D.currentZIndex);
	}
	
	private void yComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		int index = yCombo.getSelectedIndex();
		frame.canvas3D.currentYIndex = index;
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
		System.out.println("y value changed to " + frame.canvas3D.currentYIndex);
	}
	
	private void xComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		int index = xCombo.getSelectedIndex();
		frame.canvas3D.currentXIndex = index;
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
		System.out.println("x value changed to " + frame.canvas3D.currentXIndex);
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	class ColorListRenderer extends JLabel implements ListCellRenderer
	{
		ColorListRenderer()
		{
			// Don't paint behind the component
			setOpaque(true);
		}
		
		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object o, int i, boolean iss, boolean chf)
		{
		
			Category item = listItems.get(i);
			
			// Set the font
			setFont(list.getFont());
			
			// Set the text
			setText(item.name);
			
			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			
			g.setColor(item.colour.get());
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();
			
			setIcon(new ImageIcon(image));
			
			// Set background/foreground colours
			if (iss)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			return this;
		}
		
		public Insets getInsets(Insets i)
		{
			return new Insets(0, 3, 0, 0);
		}
	}
	
	// =========================form stuff here==============================
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		xCombo = new javax.swing.JComboBox();
		jLabel2 = new javax.swing.JLabel();
		yCombo = new javax.swing.JComboBox();
		jLabel3 = new javax.swing.JLabel();
		zCombo = new javax.swing.JComboBox();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		selectorList = new javax.swing.JList();
		resetButton = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		bgCombo = new javax.swing.JComboBox();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data to display:"));
		
		jLabel1.setText("x-axis:");
		
		xCombo.setBorder(null);
		xCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				xComboActionPerformed(evt);
			}
		});
		
		jLabel2.setText("y-axis:");
		
		yCombo.setBorder(null);
		yCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				yComboActionPerformed(evt);
			}
		});
		
		jLabel3.setText("z-axis:");
		
		zCombo.setBorder(null);
		zCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				zComboActionPerformed(evt);
			}
		});
		
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
														zCombo,
														0,
														162,
														Short.MAX_VALUE).add(
														yCombo,
														0,
														162,
														Short.MAX_VALUE).add(
														xCombo,
														0,
														162,
														Short.MAX_VALUE)).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel1Layout.createSequentialGroup().addContainerGap().add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel1).add(
														xCombo,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel2).add(
														yCombo,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel1Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel3).add(
														zCombo,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Highlight categories:"));
		
		selectorList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
		{
			public void valueChanged(javax.swing.event.ListSelectionEvent evt)
			{
				selectorListValueChanged(evt);
			}
		});
		jScrollPane1.setViewportView(selectorList);
		
		resetButton.setText("Restore colours");
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
		
		jPanel2.getAccessibleContext().setAccessibleName("Select categories:");
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JComboBox bgCombo;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton resetButton;
	private javax.swing.JList selectorList;
	private javax.swing.JComboBox xCombo;
	private javax.swing.JComboBox yCombo;
	private javax.swing.JComboBox zCombo;
	// End of variables declaration//GEN-END:variables
	
}
