/*
 * MTControlPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import curlywhirly.data.*;
import scri.commons.gui.*;

public class MTControlPanel extends javax.swing.JPanel implements ActionListener, ListSelectionListener
{
	
	// ==========================================vars============================================
	
	private int[] indexes;
	CurlyWhirly frame;
	private Vector<String> categories;
	Vector<Category> listItems;
	HashMap<JList, ClassificationScheme> selectorListsLookup = new HashMap<JList, ClassificationScheme>();
	
	// ==========================================c'tor============================================
	
	/** Creates new form MTControlPanel */
	public MTControlPanel(CurlyWhirly frame)
	{
		initComponents();
		this.frame = frame;
		
		if (SystemUtils.isMacOS())
			jLabel4.setText("<html>Click to select. Use CMD+click for multiple selections.");
	}
	
	// ==========================================methods============================================
	
	private void addComboModels()
	{
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel(frame.dataSet.dataHeaders));
		yCombo.setModel(new DefaultComboBoxModel(frame.dataSet.dataHeaders));
		zCombo.setModel(new DefaultComboBoxModel(frame.dataSet.dataHeaders));
	}
	
	public void resetComboBoxes()
	{
		addComboModels();
		
		// set the combos to display the currently selected index of the variables they display
		xCombo.setSelectedIndex(0);
		yCombo.setSelectedIndex(1);
		zCombo.setSelectedIndex(2);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void setUpCategoryLists()
	{
		//first set up a change listener on the tabbed pane so we can switch between classification schemes
		// Register a change listener
		categoryTabbedPane.addChangeListener(new ChangeListener() {
		    // This method is called whenever the selected tab changes
		    public void stateChanged(ChangeEvent evt) {
		        JTabbedPane pane = (JTabbedPane)evt.getSource();
		        
		        // Get current tab
		        String selectedTabName = pane.getTitleAt(pane.getSelectedIndex());		        
		        //find the corresponding classification scheme and select it
		        CurlyWhirly.canvas3D.currentClassificationScheme = CurlyWhirly.dataSet.categorizationSchemesLookup.get(selectedTabName);
		        
		        //clear everything that is currently selected
		        clearAllCategorySelections();
		    }
		});

		
		//make a new JList for each classification scheme
		for (ClassificationScheme scheme : CurlyWhirly.dataSet.classificationSchemes)
		{
			// get the list items' names and sort them
			Vector<String> listItems = new Vector<String>();			
			for(Category category : scheme.categories)
				listItems.add(category.name);
			Collections.sort(listItems);
			
			// table for selecting categories 
			JList selectorList = new JList();
			selectorListsLookup.put(selectorList, scheme);
			
			//now add the JList to the tabbed pane
			categoryTabbedPane.addTab(scheme.name,selectorList);
			categoryTabbedPane.revalidate();
			
			//set the data on the list
			selectorList.setListData(listItems);
			selectorList.addListSelectionListener(this);
			selectorList.setCellRenderer(new ColorListRenderer());
		}
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------

	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
			return;
		
		JList selectorList = (JList)e.getSource();
		
		if (selectorList.getSelectedValues().length == 0)
			return;
		Object[] selectedObjects = selectorList.getSelectedValues();
		frame.canvas3D.selectedObjects = selectedObjects;
		frame.canvas3D.highlightAllCategories = false;
		frame.canvas3D.updateGraph();
		
	}
	
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == xCombo && e.getActionCommand().equals("comboBoxChanged"))
		{
			int index = xCombo.getSelectedIndex();
			frame.canvas3D.currentXIndex = index;
		}
		if (e.getSource() == yCombo && e.getActionCommand().equals("comboBoxChanged"))
		{
			int index = yCombo.getSelectedIndex();
			frame.canvas3D.currentYIndex = index;
		}
		if (e.getSource() == zCombo && e.getActionCommand().equals("comboBoxChanged"))
		{
			int index = zCombo.getSelectedIndex();
			frame.canvas3D.currentZIndex = index;
		}
		
		// if we had data loaded already we must now update the graph
		if (frame.dataLoaded)
		{
			frame.canvas3D.highlightAllCategories = true;
			frame.canvas3D.updateGraph();
		}
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
		clearAllCategorySelections();
	}
	

	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void clearAllCategorySelections()
	{
		for(JList jList : selectorListsLookup.keySet())
			jList.clearSelection();
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph();
	}


	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	class ColorListRenderer extends DefaultListCellRenderer
	{
		
		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object o, int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);
			
			String categoryName = (String)o;		
			//retrieve the appropriate category object from the current classification scheme
			ClassificationScheme scheme = selectorListsLookup.get(list);
			Category category = scheme.getCategoryByName(categoryName);
			
			// Set the text
			setText(category.name);
			
			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			
			Color c1 = category.colour.get().brighter();
			Color c2 = category.colour.get().darker();
			
			g.setPaint(new GradientPaint(0, 0, c1, 20, 10, c2));
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();
			
			setIcon(new ImageIcon(image));
			
			return this;
		}
		
		public Insets getInsets(Insets i)
		{
			return new Insets(0, 3, 0, 0);
		}
	}
	
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
		resetViewButton = new javax.swing.JButton();
		spinButton = new javax.swing.JButton();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		spinSpeedSlider = new javax.swing.JSlider();
		jLabel7 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		resetColoursButton = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();
		categoryTabbedPane = new javax.swing.JTabbedPane();
		jPanel3 = new javax.swing.JPanel();
		bgCombo = new javax.swing.JComboBox();
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Display controls:"));
		
		jLabel1.setText("x-axis:");
		
		xCombo.setModel(new DefaultComboBoxModel());
		xCombo.setBorder(null);
		xCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
			}
		});
		
		jLabel2.setText("y-axis:");
		
		yCombo.setModel(new DefaultComboBoxModel());
		yCombo.setBorder(null);
		yCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
			}
		});
		
		jLabel3.setText("z-axis:");
		
		zCombo.setModel(new DefaultComboBoxModel());
		zCombo.setBorder(null);
		zCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				MTControlPanel.this.actionPerformed(evt);
			}
		});
		
		resetViewButton.setText("Reset viewpoint");
		resetViewButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				resetViewButtonActionPerformed(evt);
			}
		});
		
		spinButton.setText("Spin continuously");
		spinButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				spinButtonActionPerformed(evt);
			}
		});
		
		jLabel5.setText("slow spin");
		
		jLabel6.setText("fast spin");
		jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		jLabel6.setInheritsPopupMenu(false);
		
		spinSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent evt)
			{
				spinSpeedSliderStateChanged(evt);
			}
		});
		
		jLabel7.setText("Data to display:");
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(jLabel2).add(jLabel3)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(zCombo, 0, 135, Short.MAX_VALUE).add(yCombo, 0, 135, Short.MAX_VALUE).add(xCombo, 0, 135, Short.MAX_VALUE))).add(jLabel7)).addContainerGap()).add(jPanel1Layout.createSequentialGroup().add(32, 32, 32).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING, spinSpeedSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup().add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jLabel6)).add(org.jdesktop.layout.GroupLayout.LEADING, spinButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING, resetViewButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)).add(34, 34, 34)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().add(jLabel7).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel1).add(xCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel2).add(yCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel3).add(zCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(18, 18, 18).add(resetViewButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(spinButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel5).add(jLabel6)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(spinSpeedSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
		
		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Select categories:"));
		
		resetColoursButton.setText("Reset selection");
		resetColoursButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				resetColoursButtonActionPerformed(evt);
			}
		});
		
		jLabel4.setText("<html>Click to select. Use CTRL+click for multiple selections.");
		
		org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap().add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING, categoryTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.CENTER, resetColoursButton).add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup().add(jLabel4).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(categoryTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(resetColoursButton).addContainerGap()));
		
		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Select background colour:"));
		
		bgCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]
		{ "black", "dark grey", "light grey", "white" }));
		bgCombo.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bgComboActionPerformed(evt);
			}
		});
		
		org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap().add(bgCombo, 0, 173, Short.MAX_VALUE).addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup().addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(bgCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>
	//GEN-END:initComponents
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JComboBox bgCombo;
	private javax.swing.JTabbedPane categoryTabbedPane;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JButton resetColoursButton;
	private javax.swing.JButton resetViewButton;
	private javax.swing.JButton spinButton;
	private javax.swing.JSlider spinSpeedSlider;
	private javax.swing.JComboBox xCombo;
	private javax.swing.JComboBox yCombo;
	private javax.swing.JComboBox zCombo;
	// End of variables declaration//GEN-END:variables


	
}
