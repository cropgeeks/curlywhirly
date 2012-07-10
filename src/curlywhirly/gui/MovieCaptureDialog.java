/*
 * MovieCaptureDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import scri.commons.gui.*;
import com.sun.org.apache.xml.internal.serialize.*;
import curlywhirly.controller.*;

/**
 *
 * @author __USER__
 */
public class MovieCaptureDialog extends javax.swing.JDialog implements ChangeListener
{
	JFileChooser fc;
	public File movieFile;
	int defaultFrameRate = 20;
	int defaultAnimationTime = 5;
	CurlyWhirly frame;

	final int spinSpeedSlow = 15;
	final int spinSpeedMedium = 8;
	final int spinSpeedFast = 3;

	/** Creates new form MovieCaptureDialog */
	public MovieCaptureDialog(CurlyWhirly frame, boolean modal)
	{
		super(frame, modal);
		initComponents();

		this.frame = frame;

		fc = new JFileChooser(System.getProperty("user.home"));
		fc.addChoosableFileFilter(new AviFilter());
		fc.setAcceptAllFileFilterUsed(false);

		//init defaults
		frameRateSpinner.setValue(defaultFrameRate);

		//listeners
		frameRateSpinner.addChangeListener(this);
		radioButtonSlow.addChangeListener(this);
		radioButtonMedium.addChangeListener(this);
		radioButtonFast.addChangeListener(this);

		//mnemonics
		captureMovieButton.setMnemonic(KeyEvent.VK_C);
		browseButton.setMnemonic(KeyEvent.VK_B);

		updateFileFileSize();
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spinSpeedButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        frameRateSpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        fileSizeLabel = new javax.swing.JLabel();
        savedFileTF = new javax.swing.JTextField();
        radioButtonSlow = new javax.swing.JRadioButton();
        radioButtonMedium = new javax.swing.JRadioButton();
        radioButtonFast = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        captureMovieButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movie Settings");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter values:"));

        jLabel1.setText("Frame rate");

        jLabel2.setText("Graph spin speed");

        jLabel3.setText("Save as file");

        browseButton.setText("Browse...");
        browseButton.setPreferredSize(new java.awt.Dimension(115, 25));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Current file size (mb):");

        jLabel5.setText("<html>To reduce the size of the movie file, try reducing one or more of the following: window size, frame rate, graph spin speed. <br><br><b>Please note: during the capture process, do not resize or move the window.</b></html>");

        fileSizeLabel.setText("0");

        spinSpeedButtonGroup.add(radioButtonSlow);
        radioButtonSlow.setText("Slow");

        spinSpeedButtonGroup.add(radioButtonMedium);
        radioButtonMedium.setSelected(true);
        radioButtonMedium.setText("Medium");

        spinSpeedButtonGroup.add(radioButtonFast);
        radioButtonFast.setText("Fast");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(savedFileTF, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(frameRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(radioButtonSlow)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radioButtonMedium)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radioButtonFast))))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileSizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(frameRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(radioButtonSlow)
                    .addComponent(radioButtonMedium)
                    .addComponent(radioButtonFast))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(browseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(savedFileTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fileSizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        captureMovieButton.setText("Capture movie");
        captureMovieButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                captureMovieButtonActionPerformed(evt);
            }
        });
        jPanel2.add(captureMovieButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cancelButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		if(movieFile != null)
			movieFile.delete();
		setVisible(false);
	}

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			movieFile = fc.getSelectedFile();

			//check file extension
			String extension = getExtension(movieFile);
			if (extension == null)
				movieFile = new File(fc.getSelectedFile().getAbsolutePath() + ".avi");

			if (movieFile.exists())
			{
				String warningMessage = "This will overwrite the existing file. Are you sure you want to proceed?";
				if (JOptionPane.showConfirmDialog(this, warningMessage) == JOptionPane.YES_OPTION)
				{
					//remove existing file
					movieFile.delete();
				}
			}

			boolean fileCreated = false;
			try
			{
				fileCreated = movieFile.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (!fileCreated)
			{
				savedFileTF.setText("");
				captureMovieButton.setEnabled(false);
				TaskDialog.error("Error: existing file could not be overwritten -- delete existing file or save file under different name", "Close");
			}
			else
			{
				savedFileTF.setText(movieFile.getAbsolutePath());
				captureMovieButton.setEnabled(true);
			}
		}
	}

	private void captureMovieButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		//hide this dialog
		setVisible(false);

		//pick up the values the user entered
		int frameRate = (Integer) frameRateSpinner.getValue();
		int rotationTime = getSelectedSpinSpeed();

		//start the movie capture
		if (movieFile != null)
		{
			frame.frameListener.windowMoved = false;
			frame.statusBar.setMessage("Capturing movie -- press Esc to abort");
			new MovieCaptureThread(frame, movieFile, frameRate, rotationTime).start();
		}
		else
		{
			TaskDialog.error("No output file specified", "Close");
			//show this dialog again
			setVisible(true);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton captureMovieButton;
    private javax.swing.JLabel fileSizeLabel;
    private javax.swing.JSpinner frameRateSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton radioButtonFast;
    private javax.swing.JRadioButton radioButtonMedium;
    private javax.swing.JRadioButton radioButtonSlow;
    private javax.swing.JTextField savedFileTF;
    private javax.swing.ButtonGroup spinSpeedButtonGroup;
    // End of variables declaration//GEN-END:variables

	public static String getExtension(File f)
	{
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1)
		{
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public void updateFileFileSize()
	{
		int fileSize = 0;

		MainCanvas canvas = CurlyWhirly.canvas3D;
		int canvasWidth = canvas.getWidth();
		int canvasHeight = canvas.getHeight();

		//pick up the values the user entered
		int frameRate = (Integer) frameRateSpinner.getValue();
		int rotationTime = getSelectedSpinSpeed();

		int bytesPerFrame = canvasWidth * canvasHeight * 3;
		int totalNumFrames = frameRate * rotationTime;

		fileSize = bytesPerFrame * totalNumFrames;

		//convert this to megabytes
		fileSize = fileSize / 1024 / 1024;
		fileSizeLabel.setText(String.valueOf(fileSize));
	}

	//------------------------------------------------------------------------------------------------------------------

	private class AviFilter extends javax.swing.filechooser.FileFilter
	{
		// Accept all directories and all gif, jpg, tiff, or png files.
		public boolean accept(File f)
		{
			String extension = getExtension(f);
			if (extension != null)
			{
				if (extension.equals("avi"))
					return true;
				else
					return false;
			}

			return false;
		}

		public String getDescription()
		{
			return "avi files";
		}

	}

	public void stateChanged(ChangeEvent arg0)
	{
		updateFileFileSize();
	}

	private int getSelectedSpinSpeed()
	{
		if (radioButtonSlow.isSelected())
			return spinSpeedSlow;
		else if (radioButtonMedium.isSelected())
			return spinSpeedMedium;
		else if (radioButtonFast.isSelected())
			return spinSpeedFast;
		else
			return -1;
	}

	public javax.swing.JTextField getSavedFileTF()
	{
		return savedFileTF;
	}

}
