package curlywhirly.controller;


import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import curlywhirly.gui.*;

/**
 * Thread class for saving screenshots of the canvas
 */
public class ScreenCaptureThread extends Thread
{
	File outfile;
	CurlyWhirly frame;
	String fileType;
	JFileChooser fileChooser;


	public ScreenCaptureThread(File outfile, CurlyWhirly frame,String fileType, JFileChooser fileChooser)
	{
		this.outfile = outfile;
		this.frame = frame;
		this.fileType = fileType;
		this.fileChooser = fileChooser;
	}

	//take a screenshot of the canvas only
	public void run()
	{
		MainCanvas canvas = frame.canvas3D;
		canvas.setVisible(true);
		Robot robot =null;
		try
		{
			robot = new Robot();
		}
		catch (AWTException e1)
		{
			e1.printStackTrace();
		}

		//take a screenshot of the canvas only
		Point p = canvas.getLocationOnScreen();
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		Rectangle screenRect = new Rectangle((int) p.getX(), (int) p.getY(), width, height);
		BufferedImage img = robot.createScreenCapture(screenRect);
		// Write that to disk....
		try
		{
			ImageIO.write(img, fileType, outfile);
			JOptionPane.showMessageDialog(frame, "saved image to file " + outfile.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}