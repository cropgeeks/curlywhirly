package curlywhirly.controller;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.media.j3d.*;

import curlywhirly.gui.*;

import scri.commons.gui.*;

public class ImageExporter extends SimpleJob
{
	private File file;
	private CurlyWhirly frame;

	public ImageExporter(File outfile, CurlyWhirly frame)
	{
		this.file = outfile;
		this.frame = frame;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		BufferedImage image = getScreenShot(frame.canvas3D);
		try
		{
			ImageIO.write(image, "png", file);
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	private BufferedImage getScreenShot(Canvas3D canvas3d)
	{
		GraphicsContext3D context = canvas3d.getGraphicsContext3D();
		Dimension dimension = canvas3d.getSize();

		ImageComponent2D image = new ImageComponent2D(ImageComponent.FORMAT_RGB,
			dimension.width, dimension.height);

		Raster ras = new Raster();
		ras.setType(Raster.RASTER_COLOR);
		ras.setCapability(Raster.ALLOW_IMAGE_READ);
		ras.setCapability(Raster.ALLOW_IMAGE_WRITE);

		ras.setSize(dimension);
		ras.setImage(image);

		context.readRaster(ras);
		BufferedImage img = ras.getImage().getImage();

		return img;
	}
}