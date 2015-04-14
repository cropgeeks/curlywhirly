// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import curlywhirly.gui.dialog.*;
import curlywhirly.io.*;

import scri.commons.io.*;
import scri.commons.gui.*;

public class Commands
{
	private final WinMain winMain;

	Commands(WinMain winMain)
	{
		this.winMain = winMain;
	}

	void open()
	{
		// file chooser
		JFileChooser fc = new JFileChooser(Prefs.guiCurrentDir);

		int returnVal = fc.showOpenDialog(winMain);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			winMain.getOpenGLPanel().stopAnimator();
			openFile(fc.getSelectedFile());
		}
	}

	void openFile(File file)
	{
		if (file == null)
			open();
		else
		{
			try
			{
				Prefs.guiCurrentDir = "" + file.getParent();

				// The input file could be:
				//  - a tab-delimited data file
				//  - an xml .curlywhirly file
				// For now; let's cheat and just use the file extension
				if (file.getPath().toLowerCase().endsWith(".curlywhirly"))
					file = CurlyWhirlyFileHandler.createFromXML(file).dataFile;

				DataImporter importer = new DataImporter(file);
				ProgressDialog dialog = new ProgressDialog(importer, "", "", winMain);
				if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
				{
					if (dialog.getResult() == ProgressDialog.JOB_FAILED)
					{
						TaskDialog.error(
							RB.format("gui.Commands.import.error",
							dialog.getException()),
							RB.getString("gui.text.close"));
					}

					return;
				}

				winMain.setDataSet(importer.getDataSet());
				CurlyWhirlyFileHandler.addAsMostRecent(importer.getFile());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	void openSample()
	{
		File dir = FileUtils.getTempUserDirectory("scri-curlywhirly");
		File sample = new File(dir, "sample.txt");

		winMain.getOpenGLPanel().stopAnimator();

		try
		{
			FileUtils.writeFile(sample, getClass().getResourceAsStream("/data/randomData.txt"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		// load the example dataset provided with the application
		openFile(sample);
	}

    void exportDataSet()
	{
		File saveAs = new File(Prefs.guiCurrentDir, winMain.getDataSet().getName() + ".txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.Commands.exportDataSet.txtFiles"), "txt");

		// Ask the user for a filename to save the current view as
		String filename = curlywhirly.util.FileUtils.getSaveFilename(
			RB.getString("gui.Commands.exportDataSet.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		try
		{
            File exportedData = new File(filename);
            // Replace these lines with dataset export code
            DataExporter exporter = new DataExporter(winMain.getDataSet(), exportedData);
            exporter.runJob(0);

			TaskDialog.showFileOpen(
				RB.format("gui.Commands.exportDataSet.success", filename),
				TaskDialog.INF, exportedData);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			TaskDialog.showOpenLog(RB.format("gui.Commands.exportDataSet.exception",
				e), null);
		}
	}

	void reset()
	{
		winMain.getOpenGLPanel().getScene().reset();
	}

	void spin()
	{
		// Start or stop the spinning
		boolean isSpinning = winMain.getOpenGLPanel().getScene().toggleSpin();

		JSlider slider = winMain.getToolbar().getSlider();
		slider.setEnabled(isSpinning);

		// And enabled/disable the speed slider based on the state
		winMain.getOpenGLPanel().getScene().setRotationSpeed(Prefs.guiRotationSpeed);
	}

	void showPrefs()
	{
		new PreferencesDialog(winMain);
	}

	void showAbout()
	{
		new AboutDialog();
	}

	void screenshot()
	{
		ExportScreenshotDialog screenshotDialog = new ExportScreenshotDialog(winMain);

		if (screenshotDialog.isOk())
		{
			// Ask the user for a filename to save the current view as
			String filename = screenshotDialog.getFilename();

			// Quit if the user cancelled the file selection
			if (filename == null)
				return;

			try
			{
				File imageFile = new File(filename);
				ImageIO.write(screenshotDialog.getImage(), "png", imageFile);

				TaskDialog.showFileOpen(
					RB.format("gui.Commands.exportImage.success", filename),
					TaskDialog.INF, imageFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();

				TaskDialog.showOpenLog(RB.format("gui.Commands.exportImage.exception",
					e), null);
			}
		}
	}

	public void captureMovie()
	{
		MovieCaptureDialog captureDialog = new MovieCaptureDialog(winMain);

		if (captureDialog.isOk())
		{
			int frameRate = captureDialog.getFrameRate();
			int length = captureDialog.getLength();
			String name = captureDialog.getFilename();
			MovieExporter exporter = new MovieExporter(winMain.getOpenGLPanel(), frameRate, length, name);

			ProgressDialog dialog = new ProgressDialog(exporter,
				RB.getString("gui.Commands.captureMovie.progressDialog.title"),
				RB.getString("gui.Commands.captureMovie.progressDialog.message"),
				winMain);
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				TaskDialog.error(
					RB.format("gui.Commands.captureMovie.error",
					dialog.getException()),
					RB.getString("gui.text.close"));
			}
		}
	}
}