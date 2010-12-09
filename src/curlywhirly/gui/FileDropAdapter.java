// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

public class FileDropAdapter extends DropTargetAdapter
{
	private final CurlyWhirly winMain;

	public FileDropAdapter(CurlyWhirly winMain)
	{
		this.winMain = winMain;
	}

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

					//open the file
					CurlyWhirly.dragAndDropDataLoad = true;
					CurlyWhirly.dataLoader.loadDataInThread(new File(list.get(0).toString()));
					//now reset this flag so that user can open another file by different means
					CurlyWhirly.dragAndDropDataLoad = false;


					dtde.dropComplete(true);
					return;
				}
			}

			dtde.dropComplete(true);
		}
		catch (Exception e) {}
	}
}