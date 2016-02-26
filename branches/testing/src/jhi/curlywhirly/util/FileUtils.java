// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.util;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import jhi.curlywhirly.gui.*;

import scri.commons.gui.*;

public class FileUtils
{
	/**
	 * Shows a SAVE file dialog, returning the path to the file selected as a
	 * string. Also prompts to ensure the user really does want to overwrite an
	 * existing file if one is chosen.
	 */
	public static String getSaveFilename(String title, File file, FileNameExtensionFilter filter)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(title);
		fc.setCurrentDirectory(new File(Prefs.guiCurrentDir));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(filter);
		if (file != null)
			fc.setSelectedFile(file);
		while (fc.showSaveDialog(CurlyWhirly.winMain) == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();
			if (file.exists() == false)
				if (!file.getName().contains("."))
					file = new File(file.getPath() + "." + filter.getExtensions()[0]);
			if (file.exists())
			{
				String msg = RB.format("gui.CWUtils.getSaveFilename.confirm", file);
				String[] options = new String[]{RB.getString("gui.CWUtils.getSaveFilename.overwrite"), RB.getString("gui.CWUtils.getSaveFilename.rename"), RB.getString("gui.text.cancel")};
				int response = TaskDialog.show(msg, TaskDialog.WAR, 1, options);
				if (response == 1)
					continue;
					// Closed dialog or clicked cancel...
				else if (response == -1 || response == 2)
					return null;
			}
			Prefs.guiCurrentDir = fc.getCurrentDirectory().getPath();
			return file.getPath();
		}
		return null;
	}
}