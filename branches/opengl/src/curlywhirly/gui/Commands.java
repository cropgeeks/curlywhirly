package curlywhirly.gui;

import java.io.*;
import javax.swing.*;

import curlywhirly.io.*;

import scri.commons.gui.*;
import scri.commons.file.*;

public class Commands
{
	private WinMain winMain;

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
				Prefs.setRecentDocument(importer.getFile());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	void openSample()
	{
		File dir = SystemUtils.getTempUserDirectory("scri-curlywhirly");
		File sample = new File(dir, "sample.txt");

		winMain.getOpenGLPanel().stopAnimator();

		try
		{
			FileUtils.writeFile(sample, getClass().getResourceAsStream("/data/randomData.txt"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// load the example dataset provided with the application
		openFile(sample);
	}

	void reset()
	{
		winMain.getOpenGLPanel().reset();
	}

	void spin()
	{
		// Start or stop the spinning
		winMain.getOpenGLPanel().toggleSpin();

		JSlider slider = winMain.getToolbar().getSlider();

		// And enabled/disable the speed slider based on the state
		slider.setEnabled(winMain.getToolbar().getSpin().isSelected());
		winMain.getOpenGLPanel().setSpeed(slider.getValue());
	}

	void showPrefs()
	{
		new PreferencesDialog();
	}

	void showAbout()
	{
		System.out.println("Show about");
		new AboutDialog();
	}
}
