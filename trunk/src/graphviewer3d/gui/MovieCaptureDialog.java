/*
 * MovieCaptureDialog.java
 *
 * Created on __DATE__, __TIME__
 */

package graphviewer3d.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import scri.commons.gui.*;
import com.sun.org.apache.xml.internal.serialize.*;
import graphviewer3d.controller.*;

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
	GraphViewerFrame frame;
	
	final int spinSpeedSlow = 15;
	final int spinSpeedMedium = 8;
	final int spinSpeedFast = 3;
	
	/** Creates new form MovieCaptureDialog */
	public MovieCaptureDialog(GraphViewerFrame frame, boolean modal)
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
	
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents()
	{
		
		jLabel6 = new javax.swing.JLabel();
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
		cancelButton = new javax.swing.JButton();
		captureMovieButton = new javax.swing.JButton();
		radioButtonSlow = new javax.swing.JRadioButton();
		radioButtonMedium = new javax.swing.JRadioButton();
		radioButtonFast = new javax.swing.JRadioButton();
		
		jLabel6.setText("jLabel6");
		
		setTitle("Movie Settings");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Enter values:"));
		
		jLabel1.setText("Frame rate");
		
		jLabel2.setText("Graph spin speed");
		
		jLabel3.setText("Save as file");
		
		browseButton.setLabel("Browse...");
		browseButton.setPreferredSize(new java.awt.Dimension(115, 25));
		browseButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				browseButtonActionPerformed(evt);
			}
		});
		
		jLabel4.setText("Current file size (mb):");
		
		jLabel5.setText("<html>To reduce the size of the movie file, try reducing one or more of the following: window size, frame rate, graph spin speed. <br><br><b>Please note: during the capture process, do not resize or move the window.</b></html>");
		
		fileSizeLabel.setText("0");
		
		cancelButton.setLabel("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});
		
		captureMovieButton.setLabel("Capture movie");
		captureMovieButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				captureMovieButtonActionPerformed(evt);
			}
		});
		
		spinSpeedButtonGroup.add(radioButtonSlow);
		radioButtonSlow.setLabel("slow");
		
		spinSpeedButtonGroup.add(radioButtonMedium);
		radioButtonMedium.setSelected(true);
		radioButtonMedium.setLabel("medium");
		
		spinSpeedButtonGroup.add(radioButtonFast);
		radioButtonFast.setLabel("fast");
		
		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().addContainerGap().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE).add(jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(jLabel2).add(jLabel3)).add(36, 36, 36).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup().add(savedFileTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(browseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(frameRateSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jPanel1Layout.createSequentialGroup().add(radioButtonSlow).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(radioButtonMedium).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(radioButtonFast))).addContainerGap()).add(jPanel1Layout.createSequentialGroup().add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE).addContainerGap()).add(jPanel1Layout.createSequentialGroup().add(jLabel4).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(fileSizeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()).add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup().add(captureMovieButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(cancelButton).add(13, 13, 13)))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel1Layout.createSequentialGroup().add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(frameRateSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jLabel1)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel2).add(radioButtonSlow).add(radioButtonMedium).add(radioButtonFast)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel3).add(browseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(savedFileTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(1, 1, 1).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel4).add(fileSizeLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jLabel5).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 33, Short.MAX_VALUE).add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(captureMovieButton).add(cancelButton))));
		
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
		
		pack();
	}// </editor-fold>
	//GEN-END:initComponents
	
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
	
	//GEN-BEGIN:variables
	// Variables declaration - do not modify
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
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
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
		
		GraphViewer3DCanvas canvas = GraphViewerFrame.canvas3D;
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
	
	@Override
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
