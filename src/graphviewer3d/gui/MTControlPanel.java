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
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.Color3f;

public class MTControlPanel extends javax.swing.JPanel implements ActionListener
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
		
		// now register the action listener on these components
		xCombo.addActionListener(this);
		yCombo.addActionListener(this);
		zCombo.addActionListener(this);
		
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
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == xCombo)
		{
			int index = xCombo.getSelectedIndex();
			frame.canvas3D.currentXIndex = index;
		}
		if (e.getSource() == yCombo)
		{
			int index = yCombo.getSelectedIndex();
			frame.canvas3D.currentYIndex = index;
		}
		if (e.getSource() == zCombo)
		{
			int index = zCombo.getSelectedIndex();
			frame.canvas3D.currentZIndex = index;
		}
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void selectorListValueChanged(javax.swing.event.ListSelectionEvent evt)
	{
		if (evt.getValueIsAdjusting())
			return;
		if (selectorList.getSelectedValues().length == 0)
			return;
		Object[] selectedObjects = selectorList.getSelectedValues();
		frame.canvas3D.selectedObjects = selectedObjects;
		frame.canvas3D.highlightAllCategories = false;
		frame.canvas3D.updateGraph();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void bgComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		int bgColour = bgCombo.getSelectedIndex();
		frame.canvas3D.setBackgroundColour(bgColour);
		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void resetViewButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		frame.canvas3D.resetOriginalView();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void spinButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (spinButton.getText().equals("Spin continuously"))
		{
			frame.canvas3D.spin();
			spinButton.setText("Stop spinning");
		}
		else
		{
			frame.canvas3D.stopSpinning();
			spinButton.setText("Spin continuously");
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void spinSpeedSliderStateChanged(javax.swing.event.ChangeEvent evt)
	{
		JSlider source = (JSlider) evt.getSource();
		if (!source.getValueIsAdjusting())
		{
			int speed = source.getValue();
			frame.canvas3D.setSpinSpeed(speed);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void resetColoursButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		selectorList.clearSelection();
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
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
	
	// GEN-BEGIN:initComponents
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
		resetColoursButton = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		bgCombo = new javax.swing.JComboBox();
		jPanel4 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		spinButton = new javax.swing.JButton();
		spinSpeedSlider = new javax.swing.JSlider();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		resetViewButton = new javax.swing.JButton();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data to display:"));
		
		jLabel1.setText("x-axis:");
		
		xCombo.setBorder(null);
		xCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
			}
		});
		
		jLabel2.setText("y-axis:");
		
		yCombo.setBorder(null);
		yCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
			}
		});
		
		jLabel3.setText("z-axis:");
		
		zCombo.setBorder(null);
		zCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
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
														120,
														Short.MAX_VALUE).add(
														yCombo,
														0,
														120,
														Short.MAX_VALUE).add(
														xCombo,
														0,
														120,
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
		
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Select categories:"));
		
		selectorList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
		{
			public void valueChanged(javax.swing.event.ListSelectionEvent evt)
			{
				selectorListValueChanged(evt);
			}
		});
		jScrollPane1.setViewportView(selectorList);
		
		resetColoursButton.setText("Reset selection");
		resetColoursButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				resetColoursButtonActionPerformed(evt);
			}
		});
		
		jLabel4.setText("<html>Click to select a category (Ctrl + click to select multiple).");
		
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel2Layout.createSequentialGroup().addContainerGap().add(
										jPanel2Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														org.jdesktop.layout.GroupLayout.CENTER,
														resetColoursButton).add(
														org.jdesktop.layout.GroupLayout.CENTER,
														jScrollPane1,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														157,
														Short.MAX_VALUE).add(
														jLabel4,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														157,
														Short.MAX_VALUE)).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						jPanel2Layout.createSequentialGroup().add(jLabel4).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED).add(
										jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										346, Short.MAX_VALUE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										resetColoursButton).addContainerGap()));
		
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
										157, Short.MAX_VALUE).addContainerGap()));
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
		
		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Other view controls:"));
		
		spinButton.setText("Spin continuously");
		spinButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				spinButtonActionPerformed(evt);
			}
		});
		
		spinSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				spinSpeedSliderStateChanged(evt);
			}
		});
		
		jLabel5.setText("slow spin");
		
		jLabel6.setText("fast spin");
		
		org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel6Layout.createSequentialGroup().addContainerGap().add(
										jPanel6Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														jPanel6Layout.createSequentialGroup().add(
																		jLabel5).addPreferredGap(
																		org.jdesktop.layout.LayoutStyle.RELATED,
																		31,
																		Short.MAX_VALUE).add(
																		jLabel6)).add(
														spinButton).add(
														spinSpeedSlider,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														115,
														Short.MAX_VALUE)).addContainerGap()));
		jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						org.jdesktop.layout.GroupLayout.TRAILING,
						jPanel6Layout.createSequentialGroup().add(spinButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).add(
										jPanel6Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.BASELINE).add(
														jLabel5).add(
														jLabel6)).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										spinSpeedSlider,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		resetViewButton.setText("Reset viewpoint");
		resetViewButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				resetViewButtonActionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel5Layout.createSequentialGroup().addContainerGap().add(
										jPanel5Layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.CENTER).add(
														resetViewButton,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														115,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
														jPanel6,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel5Layout.createSequentialGroup().add(resetViewButton).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED,
										8, Short.MAX_VALUE).add(
										jPanel6,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										74,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(
						jPanel4Layout.createSequentialGroup().add(12, 12, 12).add(
										jPanel5,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
						org.jdesktop.layout.GroupLayout.LEADING).add(jPanel5,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
						layout.createSequentialGroup().addContainerGap().add(
										layout.createParallelGroup(
														org.jdesktop.layout.GroupLayout.LEADING).add(
														org.jdesktop.layout.GroupLayout.TRAILING,
														jPanel2,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).add(
														org.jdesktop.layout.GroupLayout.TRAILING,
														jPanel4,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).add(
														jPanel3,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE).add(
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
										jPanel4,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED).add(
										jPanel2,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	// GEN-END:initComponents
	
	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JComboBox bgCombo;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton resetColoursButton;
	private javax.swing.JButton resetViewButton;
	private javax.swing.JList selectorList;
	private javax.swing.JButton spinButton;
	private javax.swing.JSlider spinSpeedSlider;
	private javax.swing.JComboBox xCombo;
	private javax.swing.JComboBox yCombo;
	private javax.swing.JComboBox zCombo;
	// End of variables declaration//GEN-END:variables
	
}
