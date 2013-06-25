package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.vecmath.*;

import scri.commons.gui.*;

import curlywhirly.data.*;

public class MTControlPanel extends JPanel implements ActionListener
{
	CurlyWhirly frame;
	ArrayList<Category> listItems;

	/** Creates new form MTControlPanel */
	public MTControlPanel(CurlyWhirly frame)
	{
		initComponents();
		this.frame = frame;

		// Middle panel components
//		jPanel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.MTControlPanel.jPanel1.title")));

		RB.setText(jLabel7, "gui.MTControlPanel.jLabel7");
		RB.setText(jLabel1, "gui.MTControlPanel.jLabel1");
		RB.setText(jLabel2, "gui.MTControlPanel.jLabel2");
		RB.setText(jLabel3, "gui.MTControlPanel.jLabel3");
//		RB.setText(jLabel5, "gui.MTControlPanel.jLabel5");
//		RB.setText(jLabel6, "gui.MTControlPanel.jLabel6");
		RB.setText(jLabel8, "gui.MTControlPanel.jLabel8");
		RB.setText(showLabelsCheckBox, "gui.MTControlPanel.showLabelsCheckBox");

		// Bottom panel components
//		jPanel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.MTControlPanel.jPanel2.title")));
//		RB.setText(jLabel4, "gui.MTControlPanel.jLabel4");
		RB.setText(resetColoursButton, "gui.MTControlPanel.resetColoursButton");


//		if (SystemUtils.isMacOS())
//			jLabel4.setText("<html>Click to select. Use CMD+click for multiple selections.");

		addMouseAdapterToSelectorList();

		if (Prefs.showMouseOverLabels)
			showLabelsCheckBox.setSelected(true);
		else
			showLabelsCheckBox.setSelected(false);

		toggleEnabled(false);

		showLabelsCheckBox.setVisible(false);

		schemeSelectorCombo.addActionListener(this);
	}

	public void addMouseAdapterToSelectorList()
	{
		selectorList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				if (selectorList.getSelectedIndices().length > 1)
					return;
				if (me.getClickCount() == 2)
				{
					String selectedValue = selectorList.getSelectedValue();
//					System.out.println("double click on " + selectedValue);
					//retrieve the related Category object
					Category category = frame.getDataSet().currentClassificationScheme.getCategoryByName(selectedValue);

					// fire up a colour chooser
					Color newColor = JColorChooser.showDialog(CurlyWhirly.curlyWhirly, "Choose color for category", category.colour.get());
					if (newColor != null)
					{
						category.colour = new Color3f(newColor);
//						CurlyWhirly.canvas3D.updateGraph(false);
					}
				}
			}
		});
	}

	public void addComboModels()
	{
		Vector<String> dataHeaders = frame.getDataSet().dataHeaders;
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel<String>(dataHeaders));
		yCombo.setModel(new DefaultComboBoxModel<String>(dataHeaders));
		zCombo.setModel(new DefaultComboBoxModel<String>(dataHeaders));

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
		//make a vector of all the classification schemes' names
		Vector<String> schemeNames = new Vector<String>();
		for (ClassificationScheme scheme : CurlyWhirly.dataSet.classificationSchemes)
			schemeNames.add(scheme.name);

		//sort it and set it as the current model for the combo
		Collections.sort(schemeNames, new CaseInsensitiveComparator());
		schemeSelectorCombo.setModel(new DefaultComboBoxModel<String>(schemeNames));
		schemeSelectorCombo.setSelectedIndex(0);
//		schemeSelectorCombo.setToolTipText(selectedSchemeName);

		//set a custom renderer on this combo box so we can see tooltips for each item on the drop-down list
//		schemeSelectorCombo.setRenderer(new ComboBoxWithToolTipsRenderer());
	}

	class ComboBoxWithToolTipsRenderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index)
				{
					list.setToolTipText((String) list.getSelectedValue());
				}
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	class CaseInsensitiveComparator implements Comparator<String>
	{
		public int compare(String strA, String strB)
		{
			return strA.compareToIgnoreCase(strB);
		}
	}

	class ColorListRenderer extends DefaultListCellRenderer
	{

		// Set the attributes of the class and return a reference
		public Component getListCellRendererComponent(JList list, Object o, int i, boolean iss, boolean chf)
		{
			super.getListCellRendererComponent(list, o, i, iss, chf);

			String categoryName = (String) o;
			//retrieve the appropriate category object from the current classification scheme
			String selectedSchemeName = (String) schemeSelectorCombo.getSelectedItem();
			//find the corresponding classification scheme and select it
			ClassificationScheme selectedScheme = CurlyWhirly.dataSet.categorizationSchemesLookup.get(selectedSchemeName);
			Category category = selectedScheme.getCategoryByName(categoryName);

			// Set the text
			setText(category.name);

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setPaint(category.colour.get());
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

	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == xCombo)
		{
			int index = xCombo.getSelectedIndex();
			frame.getDataSet().setCurrX(index);
		}

		else if (e.getSource() == yCombo)
		{
			int index = yCombo.getSelectedIndex();
			frame.getDataSet().setCurrY(index);
		}

		else if (e.getSource() == zCombo)
		{
			int index = zCombo.getSelectedIndex();
			frame.getDataSet().setCurrZ(index);
		}

		else if (e.getSource() == schemeSelectorCombo)
			updateSelectedScheme();

		// if we had data loaded already we must now update the graph
		if (frame.dataLoaded)
		{
//			frame.canvas3D.highlightAllCategories = true;
		}
	}

	private void updateSelectedScheme()
	{
		// Get current selection
		String selectedSchemeName = (String) schemeSelectorCombo.getSelectedItem();
		if (selectedSchemeName != null)
		{
			//find the corresponding classification scheme and select it
			ClassificationScheme selectedScheme = frame.getDataSet().categorizationSchemesLookup.get(selectedSchemeName);
			if (selectedScheme != null)
				frame.getDataSet().setCurrentClassificationScheme(selectedScheme);

			//update the data model of the selector list
			selectorList.setListData(selectedScheme.categoryNamesVec);

			//update the tool tip text
			schemeSelectorCombo.setToolTipText(selectedSchemeName);

			//add custom cell renderers
			selectorList.setCellRenderer(new ColorListRenderer());
		}
		//clear everything that is currently selected
		if (CurlyWhirly.dataLoaded)
			clearAllCategorySelections();
	}

	private void resetColoursButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		clearAllCategorySelections();
	}

	private void clearAllCategorySelections()
	{
		selectorList.clearSelection();
		frame.getDataSet().setHighlightAllCategories(true);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xCombo = new javax.swing.JComboBox<String>();
        yCombo = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        zCombo = new javax.swing.JComboBox<String>();
        showLabelsCheckBox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        schemeSelectorCombo = new javax.swing.JComboBox<String>();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectorList = new javax.swing.JList<String>();
        resetColoursButton = new javax.swing.JButton();

        jLabel7.setText("Data to display:");

        jLabel1.setText("x-axis:");

        jLabel2.setText("y-axis:");

        xCombo.setBorder(null);
        xCombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                MTControlPanel.this.actionPerformed(evt);
            }
        });

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

        showLabelsCheckBox.setText("Show labels on mouseover");
        showLabelsCheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showLabelsCheckBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("Category scheme:");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(zCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(xCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(showLabelsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(schemeSelectorCombo, 0, 205, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(resetColoursButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(xCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(yCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(showLabelsCheckBox)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(schemeSelectorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetColoursButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void showLabelsCheckBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (showLabelsCheckBox.isSelected())
			Prefs.showMouseOverLabels = true;
		else
			Prefs.showMouseOverLabels = false;
	}

	private void selectorListValueChanged(ListSelectionEvent e)
	{

		if (e.getValueIsAdjusting())
			return;

		JList selectorList = (JList) e.getSource();

		if (selectorList.getSelectedIndices().length == 0)
			return;

		ArrayList<String> selectedNames = (ArrayList<String>) selectorList.getSelectedValuesList();

		frame.getCanvasController().updateSelected(selectedNames);
		frame.getDataSet().setHighlightAllCategories(false);
	}

	public void toggleEnabled(boolean enabled)
	{
		jLabel1.setEnabled(enabled);
		jLabel2.setEnabled(enabled);
		jLabel3.setEnabled(enabled);
//		jLabel4.setEnabled(enabled);
//		jLabel5.setEnabled(enabled);
//		jLabel6.setEnabled(enabled);
		jLabel7.setEnabled(enabled);
//		jPanel1.setEnabled(enabled);
//		jPanel2.setEnabled(enabled);
		resetColoursButton.setEnabled(enabled);
		schemeSelectorCombo.setEnabled(enabled);
		selectorList.setEnabled(enabled);
		showLabelsCheckBox.setEnabled(enabled);
//		spinSpeedSlider.setEnabled(enabled);
		xCombo.setEnabled(enabled);
		yCombo.setEnabled(enabled);
		zCombo.setEnabled(enabled);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton resetColoursButton;
    private javax.swing.JComboBox<String> schemeSelectorCombo;
    private javax.swing.JList<String> selectorList;
    private javax.swing.JCheckBox showLabelsCheckBox;
    private javax.swing.JComboBox<String> xCombo;
    private javax.swing.JComboBox<String> yCombo;
    private javax.swing.JComboBox<String> zCombo;
    // End of variables declaration//GEN-END:variables

}