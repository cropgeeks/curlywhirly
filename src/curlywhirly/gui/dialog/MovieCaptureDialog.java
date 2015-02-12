// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import curlywhirly.gui.*;
import curlywhirly.gui.viewer.*;

import scri.commons.gui.*;

public class MovieCaptureDialog extends JDialog implements ActionListener, ChangeListener
{
	private boolean isOK;
	private final WinMain winMain;

	public MovieCaptureDialog(WinMain winMain)
	{
		super(
			CurlyWhirly.winMain,
			RB.getString("gui.dialog.MovieCaptureDialog.title"),
			true
		);

		this.winMain = winMain;

		isOK = false;

		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bOK);

		pack();
		setLocationRelativeTo(CurlyWhirly.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
		RB.setText(bOK, "gui.text.ok");
		bOK.addActionListener(this);

		RB.setText(bCancel, "gui.text.cancel");
		bCancel.addActionListener(this);

		settingsPanel.setBorder(new TitledBorder(RB.getString("gui.dialog.MovieCaptureDialog.settingsPanel.title")));
		RB.setText(frameRateLabel, "gui.dialog.MovieCaptureDialog.frameRateLabel");
		RB.setText(lengthLabel, "gui.dialog.MovieCaptureDialog.lengthLabel");
		warningPanel.setBorder(new TitledBorder(RB.getString("gui.dialog.MovieCaptureDialog.warningPanel.title")));
		RB.setText(fileSizeLabel, "gui.dialog.MovieCaptureDialog.fileSizeLabel");
		RB.setText(filenameLabel, "gui.dialog.MovieCaptureDialog.fileNameLabel");
		RB.setText(bBrowse, "gui.text.browse");

		bBrowse.addActionListener(this);

		filenameTextField.setText(Prefs.guiCurrentDir
			+ System.getProperty("file.separator")
			+ winMain.getDataSet().getName()
			+ RB.getString("gui.MovieCaptureDialog.fileExtension"));

		frameRateSpinner.setModel(new SpinnerNumberModel(Prefs.guiMovieCaptureFrameRate, 1, 60, 1));
		lengthSpinner.setModel(new SpinnerNumberModel(Prefs.guiMovieCaptureLength, 5, 30, 1));

		frameRateSpinner.addChangeListener(this);
		lengthSpinner.addChangeListener(this);

		settingsPanel.setBackground(Color.WHITE);
		warningPanel.setBackground(Color.WHITE);

		chkColourKey.setSelected(Prefs.guiMovieChkColourKey);
		chkColourKey.setText(RB.getString("gui.dialog.MovieCaptureDialog.chkColourKey"));
		chkColourKey.addActionListener(this);

		updateFileSize();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			Prefs.guiMovieCaptureFrameRate = (int) frameRateSpinner.getValue();
			Prefs.guiMovieCaptureLength = (int) lengthSpinner.getValue();
			isOK = true;
			setVisible(false);
		}

		else if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == bBrowse)
		{
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.avi"), RB.getString("gui.MovieCaptureDialog.fileExtension"));
			String filename = CWUtils.getSaveFilename(RB.getString("gui.dialog.MovieCaptureDialog.saveAs"), null, filter);
			if (filename != null)
				filenameTextField.setText(filename);
		}

		else if (e.getSource() == chkColourKey)
			Prefs.guiMovieChkColourKey = !Prefs.guiMovieChkColourKey;
	}

	private void updateFileSize()
	{
		int frameRate = (Integer) frameRateSpinner.getValue();
		int length = (Integer) lengthSpinner.getValue();
		OpenGLPanel panel = winMain.getOpenGLPanel();

		long bytesPerFrame = panel.getWidth() * panel.getHeight() * 3;
		long totalNumFrames = frameRate * length;

		long compressionRation = 15;

		// Calculate file size in megabytes
		long fileSize = (bytesPerFrame * totalNumFrames) / compressionRation;
		int fileSizeMB = (int) (fileSize / 1024 / 1024);
		sizeLabel.setText(RB.format("gui.MovieCaptureDialog.sizeLabel", fileSizeMB));
	}


	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == frameRateSpinner || e.getSource() == lengthSpinner)
			updateFileSize();
	}

	public int getFrameRate()
	{
		return (Integer) frameRateSpinner.getValue();
	}

	public int getLength()
	{
		return (Integer) lengthSpinner.getValue();
	}

	public String getFilename()
	{
		return filenameTextField.getText();
	}

	public boolean isOk()
	{
		return isOK;
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

        dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
        bOK = new javax.swing.JButton();
        bCancel = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
        frameRateSpinner = new javax.swing.JSpinner();
        lengthLabel = new javax.swing.JLabel();
        frameRateLabel = new javax.swing.JLabel();
        lengthSpinner = new javax.swing.JSpinner();
        filenameLabel = new javax.swing.JLabel();
        bBrowse = new javax.swing.JButton();
        filenameTextField = new javax.swing.JTextField();
        sizeLabel = new javax.swing.JLabel();
        chkColourKey = new javax.swing.JCheckBox();
        warningPanel = new javax.swing.JPanel();
        fileSizeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        bOK.setText("OK");
        dialogPanel1.add(bOK);

        bCancel.setText("Cancel");
        dialogPanel1.add(bCancel);

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Capture settings:"));

        lengthLabel.setText("Length:");

        frameRateLabel.setText("Frame rate:");

        filenameLabel.setText("Filename:");

        bBrowse.setText("Browse...");

        filenameTextField.setText("C:\\Users\\blah\\dataset.avi");

        sizeLabel.setText("Estimated file size: 0 MB");

        chkColourKey.setText("Include colour key");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addComponent(filenameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filenameTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bBrowse))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(settingsPanelLayout.createSequentialGroup()
                                .addComponent(frameRateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(frameRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lengthLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sizeLabel)
                            .addComponent(chkColourKey))
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
                    .addComponent(lengthLabel)
                    .addComponent(lengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filenameLabel)
                    .addComponent(bBrowse)
                    .addComponent(filenameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sizeLabel)
                .addGap(18, 18, 18)
                .addComponent(chkColourKey)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        warningPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Notes:"));

        fileSizeLabel.setText("Decrease the window size, frame rate, or length to reduce file size.");

        javax.swing.GroupLayout warningPanelLayout = new javax.swing.GroupLayout(warningPanel);
        warningPanel.setLayout(warningPanelLayout);
        warningPanelLayout.setHorizontalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(warningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileSizeLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        warningPanelLayout.setVerticalGroup(
            warningPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fileSizeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOK;
    private javax.swing.JCheckBox chkColourKey;
    private scri.commons.gui.matisse.DialogPanel dialogPanel1;
    private javax.swing.JLabel fileSizeLabel;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JTextField filenameTextField;
    private javax.swing.JLabel frameRateLabel;
    private javax.swing.JSpinner frameRateSpinner;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JSpinner lengthSpinner;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JPanel warningPanel;
    // End of variables declaration//GEN-END:variables

}