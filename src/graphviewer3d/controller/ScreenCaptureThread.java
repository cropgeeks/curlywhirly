package graphviewer3d.controller;

import graphviewer3d.gui.GraphViewer3DCanvas;
import graphviewer3d.gui.GraphViewerFrame;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Thread class for saving screenshots of the canvas
 */
public class ScreenCaptureThread extends Thread
{
	File outfile;
	GraphViewerFrame frame;
	String fileType;
	JFileChooser fileChooser;
	
	
	public ScreenCaptureThread(File outfile, GraphViewerFrame frame,String fileType, JFileChooser fileChooser)
	{
		this.outfile = outfile;
		this.frame = frame;
		this.fileType = fileType;
		this.fileChooser = fileChooser;
	}
	
	//take a screenshot of the canvas only
	public void run()
	{		
		GraphViewer3DCanvas canvas = frame.canvas3D;
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
