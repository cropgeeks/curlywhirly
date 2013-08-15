package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import scri.commons.gui.*;

public class MovieCaptureDialog extends JDialog implements ActionListener, ChangeListener
{
	private JButton bCancel;
	private JButton bCapture;

	private CurlyWhirly winMain;
	private MovieCapturePanelNB nbPanel;

	public static final int SPIN_SLOW = 15;
	public static final int SPIN_MEDIUM = 8;
	public static final int SPIN_FAST = 3;

	private File movieFile;

	public MovieCaptureDialog(CurlyWhirly winMain)
	{
		super(
			winMain,
			RB.getString("gui.MovieCaptureDialog.title"),
			true
		);

		this.winMain = winMain;

		add(nbPanel = new MovieCapturePanelNB(winMain, this), BorderLayout.CENTER);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bCancel);
		SwingUtils.addCloseHandler(this, bCancel);

		updateFileSize();

		pack();
		setLocationRelativeTo(winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bCancel = SwingUtils.getButton(RB.getString("gui.text.close"));
		bCancel.addActionListener(this);
		bCapture = SwingUtils.getButton(RB.getString("gui.MovieCaptureDialog.captureMovieButton"));
		bCapture.addActionListener(this);

		JPanel p1 = CWUtils.getButtonPanel();
		p1.add(bCapture);
		p1.add(bCancel);

		return p1;
	}

	private void updateFileSize()
	{
//		MainCanvas canvas = CurlyWhirly.canvas3D;
//		int frameRate = (Integer) nbPanel.frameRateSpinner.getValue();
//
//		int bytesPerFrame = canvas.getWidth() * canvas.getHeight() * 3;
//		int totalNumFrames = frameRate * getSpinSpeed();
//
//		// Calculate file size in megabytes
//		int fileSize = (bytesPerFrame * totalNumFrames) / 1024 / 1024;
//		nbPanel.sizeLabel.setText(RB.format("gui.MovieCaptureDialog.sizeLabel", fileSize));
	}

	private void captureMovie()
	{
//		setVisible(false);
//		int frameRate = (Integer) nbPanel.frameRateSpinner.getValue();
//
//		winMain.frameListener.windowMoved = false;
//		winMain.statusBar.setMessage(RB.getString("gui.MovieCaptureDialog.captureMessage"));
//		new MovieCaptureThread(winMain, movieFile, frameRate, getSpinSpeed()).start();
	}

	private boolean promptForFilename()
	{
		File basename = new File(Prefs.guiCurrentDir, "movie.avi");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.avi"), "avi");

		String filename = CWUtils.getSaveFilename(
			RB.getString("gui.MovieCaptureDialog.saveDialog"), basename, filter);

		if (filename != null)
			movieFile = new File(filename);

		return movieFile != null;
	}

	private int getSpinSpeed()
	{
		int index = nbPanel.spinSpeedCombo.getSelectedIndex();

		if (index == 0)
			return SPIN_SLOW;
		else if (index == 1)
			return SPIN_MEDIUM;
		else
			return SPIN_FAST;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bCapture && promptForFilename())
		{
			captureMovie();
			Prefs.guiMovieCaptureFrameRate = (Integer) nbPanel.frameRateSpinner.getValue();
			Prefs.guiMovieCaptureSpinSpeedIndex = nbPanel.spinSpeedCombo.getSelectedIndex();
		}

		else if (e.getSource() == bCancel)
			setVisible(false);

		else if (e.getSource() == nbPanel.spinSpeedCombo)
			updateFileSize();
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == nbPanel.frameRateSpinner)
			updateFileSize();
	}
}