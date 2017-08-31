// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class FileDropAdapter extends DropTargetAdapter
{
	private final WinMain winMain;

	public FileDropAdapter(WinMain winMain)
	{
		this.winMain = winMain;
	}

	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		Transferable t = dtde.getTransferable();

		try
		{
			DataFlavor[] dataFlavors = t.getTransferDataFlavors();

			dtde.acceptDrop(DnDConstants.ACTION_COPY);

			for (int i = 0; i < dataFlavors.length; i++)
			{
				if (dataFlavors[i].isFlavorJavaFileListType())
				{
					List<?> list = (List<?>) t.getTransferData(dataFlavors[i]);
					final String filename = list.get(0).toString();

					// We thread this off, so that Windows doesn't hang after a
					// drag n drop from Explorer while CurlyWhirly actually loads
					// the data
					Runnable r = () -> winMain.getCommands().openFile(new File(filename));

					SwingUtilities.invokeLater(r);

					dtde.dropComplete(true);
					return;
				}
			}

			dtde.dropComplete(true);
		}
		catch (UnsupportedFlavorException | IOException e) { e.printStackTrace(); }
	}
}