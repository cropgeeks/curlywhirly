package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
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
		jPanel1.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.MTControlPanel.jPanel1.title")));

		RB.setText(jLabel7, "gui.MTControlPanel.jLabel7");
		RB.setText(jLabel1, "gui.MTControlPanel.jLabel1");
		RB.setText(jLabel2, "gui.MTControlPanel.jLabel2");
		RB.setText(jLabel3, "gui.MTControlPanel.jLabel3");
		RB.setText(resetViewButton, "gui.MTControlPanel.resetViewButton");
		RB.setText(spinButton, "gui.MTControlPanel.spinButton");
		RB.setText(jLabel5, "gui.MTControlPanel.jLabel5");
		RB.setText(jLabel6, "gui.MTControlPanel.jLabel6");
		RB.setText(showLabelsCheckBox, "gui.MTControlPanel.showLabelsCheckBox");

		// Bottom panel components
		jPanel2.setBorder(BorderFactory.createTitledBorder(RB.getString("gui.MTControlPanel.jPanel2.title")));
		RB.setText(jLabel4, "gui.MTControlPanel.jLabel4");
		RB.setText(resetColoursButton, "gui.MTControlPanel.resetColoursButton");


		if (SystemUtils.isMacOS())
			jLabel4.setText("<html>Click to select. Use CMD+click for multiple selections.");

		addMouseAdapterToSelectorList();

		if (Prefs.showMouseOverLabels)
			showLabelsCheckBox.setSelected(true);
		else
			showLabelsCheckBox.setSelected(false);
	}

	public void addMouseAdapterToSelectorList()
	{
		selectorList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				List<String> list = selectorList.getSelectedValuesList();
				if (list.size() > 1)
					return;
				if (me.getClickCount() == 2)
				{
					String selectedValue = selectorList.getSelectedValue();
					//					System.out.println("double click on " + selectedValue);
					//retrieve the related Category object
					Category category = CurlyWhirly.canvas3D.currentClassificationScheme.getCategoryByName(selectedValue);

					// fire up a colour chooser
					Color newColor = JColorChooser.showDialog(CurlyWhirly.curlyWhirly, "Choose color for category", category.colour.get());
					if (newColor != null)
					{
						category.colour = new Color3f(newColor);
						CurlyWhirly.canvas3D.updateGraph(false);
					}
				}
			}
		});
	}

	public void addComboModels()
	{
		// set the data headers as the model for the combo boxes that allow selection of variables
		xCombo.setModel(new DefaultComboBoxModel<String>(frame.dataSet.dataHeaders));
		yCombo.setModel(new DefaultComboBoxModel<String>(frame.dataSet.dataHeaders));
		zCombo.setModel(new DefaultComboBoxModel<String>(frame.dataSet.dataHeaders));

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
			frame.canvas3D.updateGraph(true);
		}
	}

	private void resetViewButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		frame.canvas3D.resetOriginalView();
	}

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

	private void spinSpeedSliderStateChanged(javax.swing.event.ChangeEvent evt)
	{
		JSlider source = (JSlider) evt.getSource();
		if (!source.getValueIsAdjusting())
		{
			int speed = source.getValue();
			frame.canvas3D.setSpinSpeed(speed);
		}
	}

	private void resetColoursButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		clearAllCategorySelections();
	}

	private void clearAllCategorySelections()
	{
		selectorList.clearSelection();
		frame.canvas3D.highlightAllCategories = true;
		frame.canvas3D.updateGraph(false);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        xCombo = new javax.swing.JComboBox<String>();
        jLabel2 = new javax.swing.JLabel();
        yCombo = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        zCombo = new javax.swing.JComboBox<String>();
        resetViewButton = new javax.swing.JButton();
        spinButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spinSpeedSlider = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        showLabelsCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        resetColoursButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectorList = new javax.swing.JList<String>();
        schemeSelectorCombo = new javax.swing.JComboBox<String>();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Display controls:"));

        jLabel1.setText("x-axis:");

        xCombo.setBorder(null);
        xCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MTControlPanel.this.actionPerformed(evt);
            }
        });

        jLabel2.setText("y-axis:");

        yCombo.setBorder(null);
        yCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MTControlPanel.this.actionPerformed(evt);
            }
        });

        jLabel3.setText("z-axis:");

        zCombo.setBorder(null);
        zCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MTControlPanel.this.actionPerformed(evt);
            }
        });

        resetViewButton.setText("Reset viewpoint");
        resetViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetViewButtonActionPerformed(evt);
            }
        });

        spinButton.setText("Spin continuously");
        spinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spinButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("slow spin");

        jLabel6.setText("fast spin");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel6.setInheritsPopupMenu(false);

        spinSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSpeedSliderStateChanged(evt);
            }
        });

        jLabel7.setText("Data to display:");

        showLabelsCheckBox.setText("Show labels on mouseover");
        showLabelsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLabelsCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spinSpeedSlider, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addComponent(spinButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resetViewButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addGap(34, 34, 34))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(zCombo, 0, 131, Short.MAX_VALUE)
                            .addComponent(yCombo, 0, 131, Short.MAX_VALUE)
                            .addComponent(xCombo, 0, 131, Short.MAX_VALUE)))
                    .addComponent(jLabel7)
                    .addComponent(showLabelsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(xCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(yCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(zCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(resetViewButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinSpeedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showLabelsCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a category scheme:"));

        resetColoursButton.setText("Reset selection");
        resetColoursButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetColoursButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("<html>Single-click to select a category (multiple categories : CTRL+click) : ");

        selectorList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                selectorListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(selectorList);

        schemeSelectorCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schemeSelectorComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(resetColoursButton, javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(schemeSelectorCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 173, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(schemeSelectorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resetColoursButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

	private void selectorListValueChanged(javax.swing.event.ListSelectionEvent evt)
	{

		if (evt.getValueIsAdjusting())
			return;

		JList selectorList = (JList) evt.getSource();

		if (selectorList.getSelectedIndices().length == 0)
			return;

		int[] indices = selectorList.getSelectedIndices();
		Object[] selectedObjects = new Object[indices.length];
		for (int i = 0; i < indices.length; i++)
			selectedObjects[i] = selectorList.getModel().getElementAt(indices[i]);

//		Object[] selectedObjects = selectorList.getSelectedValues();
		frame.canvas3D.selectedObjects = selectedObjects;
		frame.canvas3D.highlightAllCategories = false;
		frame.canvas3D.updateGraph(false);
	}

	private void schemeSelectorComboActionPerformed(java.awt.event.ActionEvent evt)
	{
		// Get current selection
		String selectedSchemeName = (String) schemeSelectorCombo.getSelectedItem();
		if (selectedSchemeName != null)
		{
			//find the corresponding classification scheme and select it
			ClassificationScheme selectedScheme = CurlyWhirly.dataSet.categorizationSchemesLookup.get(selectedSchemeName);
			if (selectedScheme != null)
				CurlyWhirly.canvas3D.currentClassificationScheme = selectedScheme;

			//update the data model of the selector list
			selectorList.setListData(selectedScheme.categoryNamesVec);

			//update the tool tip text
//			schemeSelectorCombo.setToolTipText(selectedSchemeName);

			//add custom cell renderers
			selectorList.setCellRenderer(new ColorListRenderer());
		}
		//clear everything that is currently selected
		if (CurlyWhirly.dataLoaded)
			clearAllCategorySelections();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton resetColoursButton;
    private javax.swing.JButton resetViewButton;
    private javax.swing.JComboBox<String> schemeSelectorCombo;
    private javax.swing.JList<String> selectorList;
    private javax.swing.JCheckBox showLabelsCheckBox;
    private javax.swing.JButton spinButton;
    private javax.swing.JSlider spinSpeedSlider;
    private javax.swing.JComboBox<String> xCombo;
    private javax.swing.JComboBox<String> yCombo;
    private javax.swing.JComboBox<String> zCombo;
    // End of variables declaration//GEN-END:variables

	public javax.swing.JComboBox getSchemeSelectorCombo()
	{
		return schemeSelectorCombo;
	}
}