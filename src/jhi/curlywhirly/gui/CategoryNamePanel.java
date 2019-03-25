// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.data.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class CategoryNamePanel extends JPanel
{
	private final JRadioButton radioButton;
	private final JLabel lblCount;
	private final JRadioButton expandButton;

	private final CategoryPanel parent;

	private final CategoryGroup catGroup;

	private static final Icon EXPANDED = ((Icon) UIManager.get("Tree.expandedIcon"));
	private static final Icon COLLAPSED = ((Icon) UIManager.get("Tree.collapsedIcon"));

	CategoryNamePanel(CategoryGroup catGroup, CategoryPanel parent)
	{
		this.catGroup = catGroup;
		this.parent = parent;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		GridBagConstraints con = new GridBagConstraints();
		// The expand / contract button for this CategoryPanel
		expandButton = createExpandButton(con);
		add(expandButton, con);

		// The radio button for choosing if this is the selected category group
		radioButton = createRadioButton(con);
		add(radioButton, con);

		lblCount = createCountLabel(con);
		add(lblCount, con);

		// This prevents the panel changing size when expandButton's state
		// changes.
		setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
	}

	// Expands or contracts this category panel. Uses custom icons (taken from
	// the JTree control in a JRadioButton to ape the look of a JTree.
	private JRadioButton createExpandButton(GridBagConstraints con)
	{
		con.gridy = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.anchor = GridBagConstraints.LINE_START;
		con.gridy = 0;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 0;

		// Need to set the icon and rollover icon for each state
		JRadioButton button = new JRadioButton();
		button.setIcon(EXPANDED);
		button.setRolloverIcon(EXPANDED);
		button.setSelectedIcon(COLLAPSED);
		button.setRolloverSelectedIcon(COLLAPSED);
		button.setBorder(new EmptyBorder(0, 0, 0, -8));

		button.addActionListener(parent);

		return button;
	}

	private JRadioButton createRadioButton(GridBagConstraints con)
	{
		con.gridy = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 1;
		con.anchor = GridBagConstraints.LINE_START;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 1;

		JRadioButton button = new JRadioButton();
		button.setText(getName());
		button.setToolTipText(getCategoryTooltip());
		button.addActionListener(parent);

		return button;
	}

	private JLabel createCountLabel(GridBagConstraints con)
	{
		con.gridx = 2;
		con.anchor = GridBagConstraints.LINE_END;
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.gridy = 0;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 0;

		JLabel label = new JLabel();
		label.setText(getCountString());

		return label;
	}

	private String getCountString()
	{
		return "" + catGroup.selectedDataPointCount() + "/" + catGroup.totalDataPoints();
	}

	private String getCategoryTooltip()
	{
		return catGroup.getName() + ": contains " + catGroup.selectedCategoriesCount() + " selected categories, out of a total of " + catGroup.getCategories().size();
	}

	void updateCountLabel()
	{
		radioButton.setText(getName());
		radioButton.setToolTipText(getCategoryTooltip());
		lblCount.setText(getCountString());
	}

	JRadioButton getRadioButton()
	{
		return radioButton;
	}

	JRadioButton getExpandButton()
	{
		return expandButton;
	}

	@Override
	public String getName()
	{
		String name = catGroup == null ? "" : String.format("%s (%s/%s)", catGroup.getName(), catGroup.selectedCategoriesCount(), catGroup.getCategoryCount());
		return name;
	}
}