// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.dialog;

import curlywhirly.gui.CurlyWhirly;
import curlywhirly.gui.Prefs;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import scri.commons.gui.*;

public class MovieCapturePanelNB extends JPanel
{
	private CurlyWhirly winMain;
	private MovieCaptureDialog parent;

    /** Creates new form MovieCapturePanelNB */
    public MovieCapturePanelNB(CurlyWhirly winMain, MovieCaptureDialog parent)
	{
		this.winMain = winMain;
		this.parent = parent;

        initComponents();

		setBackground(Color.WHITE);
		settingsPanel.setBackground(Color.WHITE);
		warningPanel.setBackground(Color.WHITE);

		settingsPanel.setBorder(new TitledBorder(RB.getString("gui.MovieCaptureDialog.settingsPanelTitle")));
		warningPanel.setBorder(new TitledBorder(RB.getString("gui.MovieCaptureDialog.warningPanelTitle")));
		RB.setText(frameRateLabel, "gui.MovieCaptureDialog.frameRate");
		RB.setText(spinSpeedLabel, "gui.MovieCaptureDialog.spinSpeed");
		spinSpeedCombo.addItem(RB.getString("gui.MovieCaptureDialog.radioButtonSlow"));
		spinSpeedCombo.addItem(RB.getString("gui.MovieCaptureDialog.radioButtonMedium"));
		spinSpeedCombo.addItem(RB.getString("gui.MovieCaptureDialog.radioButtonFast"));
		spinSpeedCombo.setSelectedIndex(Prefs.guiMovieCaptureSpinSpeedIndex);
		RB.setText(fileSizeLabel, "gui.MovieCaptureDialog.fileSizeLabel");
		RB.setText(warningLabel, "gui.MovieCaptureDialog.warningLabel");

		frameRateSpinner.setModel(new SpinnerNumberModel(Prefs.guiMovieCaptureFrameRate, 1, 60, 1));

		spinSpeedCombo.addActionListener(parent);
		frameRateSpinner.addChangeListener(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        settingsPanel = new javax.swing.JPanel();
        frameRateSpinner = new javax.swing.JSpinner();
        spinSpeedLabel = new javax.swing.JLabel();
        frameRateLabel = new javax.swing.JLabel();
        spinSpeedCombo = new javax.swing.JComboBox<String>();
        sizeLabel = new javax.swing.JLabel();
        warningPanel = new javax.swing.JPanel();
        fileSizeLabel = new javax.swing.JLabel();
        warningLabel = new javax.swing.JLabel();

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Capture settings:"));

        spinSpeedLabel.setLabelFor(spinSpeedCombo);
        spinSpeedLabel.setText("Spin speed:");

        frameRateLabel.setLabelFor(frameRateSpinner);
        frameRateLabel.setText("Frame rate:");

        spinSpeedCombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                spinSpeedComboActionPerformed(evt);
            }
        });

        sizeLabel.setText("Estimated file size: 0 MB");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(frameRateLabel)
                        .addGap(4, 4, 4)
                        .addComponent(frameRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(spinSpeedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinSpeedCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(sizeLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameRateLabel)
                    .addComponent(frameRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinSpeedCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinSpeedLabel))
                .addGap(18, 18, 18)
                .addComponent(sizeLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        warningPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes:"));

        fileSizeLabel.setText("Decrease the window size, frame rate, or spin speed to produce a smaller file.");

        warningLabel.setText("During the capture process, ensure you do not resize or move the window.");

        javax.swing.GroupLayout warningPanelLayout = new javax.swing.GroupLayout(warningPanel);
        warningPanel.setLayout(warningPanelLayout);
        warningPanelLayout.setHorizontalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(warningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileSizeLabel)
                    .addComponent(warningLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        warningPanelLayout.setVerticalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(warningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileSizeLabel)
                .addGap(18, 18, 18)
                .addComponent(warningLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spinSpeedComboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_spinSpeedComboActionPerformed
    {//GEN-HEADEREND:event_spinSpeedComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_spinSpeedComboActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JLabel fileSizeLabel;
    javax.swing.JLabel frameRateLabel;
    javax.swing.JSpinner frameRateSpinner;
    javax.swing.JPanel settingsPanel;
    javax.swing.JLabel sizeLabel;
    javax.swing.JComboBox<String> spinSpeedCombo;
    javax.swing.JLabel spinSpeedLabel;
    javax.swing.JLabel warningLabel;
    javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}