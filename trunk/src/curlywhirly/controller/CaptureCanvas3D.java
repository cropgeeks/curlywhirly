package curlywhirly.controller;
//package graphviewer3d.controller;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.text.*;
//
//import javax.imageio.ImageIO;
//import javax.media.j3d.*;
//import javax.swing.JOptionPane;
//import javax.swing.SwingUtilities;
//import javax.vecmath.*;
//
//import com.sun.image.codec.jpeg.*;
//
//public class CaptureCanvas3D extends Canvas3D
//{
//	public boolean writeJPEG;
//	public File outputFile;
//	public int x, y;
//	
//	public CaptureCanvas3D(GraphicsConfiguration gc)
//	{
//		super(gc);
//	}
//	
//	public void postSwap()
//	{
//		if (writeJPEG)
//		{
//			
//			GraphicsContext3D ctx = getGraphicsContext3D();			
//			x = this.getWidth();
//			y = this.getHeight();			
//			Raster ras = new Raster(new Point3f(-1.0f, -1.0f, -1.0f), Raster.RASTER_COLOR, 0, 0, x, y, new ImageComponent2D(ImageComponent.FORMAT_RGB, new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB)), null);
//			ctx.readRaster(ras);
//			// Now strip out the image info
//			BufferedImage img = ras.getImage().getImage();
//
//			// Write that to disk....
//			try
//			{
//				//ImageIO.write(img, "jpg", outputFile);
//				System.out.println(" CC edt = " +  SwingUtilities.isEventDispatchThread());
//				FileOutputStream out = new FileOutputStream(outputFile);
//				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//				JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
//				param.setQuality(1.0f, false);
//				encoder.setJPEGEncodeParam(param);
//				encoder.encode(img);
//				out.close();
//
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//			
//			writeJPEG = false;
//		}
//	}
//	
//}
