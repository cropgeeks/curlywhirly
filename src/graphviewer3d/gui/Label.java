package graphviewer3d.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Raster;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Iain Milne, Scottish Crop Research Institute; modified by Micha Bayer, Scottish Crop Research Institute
 */
public class Label
{
	
	// ====================================vars==============================
	
	private static Font labelFont;
	private static FontMetrics fm;
	
	private static Appearance m_solidApp = new Appearance();
	private static Appearance m_transApp = new Appearance();
	
	// ====================================methods==============================
	
	static
	{
		TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 0.7f);
		
		m_transApp.setTransparencyAttributes(ta);
		updateFont(10);
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	public static void updateFont(int size)
	{
		labelFont = new Font("Helvetica", Font.PLAIN, size);
		
		fm = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB).getGraphics().getFontMetrics(
						labelFont);
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	private static Shape3D getText(String name, Color bg, Color fg, boolean transparent)
	{
		String text = "  " + name + "  ";
		
		// Work out how wide the graphic needs to be
		FontRenderContext frc = new FontRenderContext(null, false, false);
		int w = (int) labelFont.getStringBounds(text, frc).getWidth();
		int h = fm.getHeight() + 2;
		
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();
		
		g.setFont(labelFont);
		g.setColor(bg);
		g.fillRect(0, 0, w, h);
		if (name.endsWith("<this>"))
			g.setColor(Color.RED);
		else
			g.setColor(fg);
		g.drawString(text, 0, fm.getHeight() - fm.getMaxDescent());
		g.setColor(fg);
		g.drawRect(0, 0, w - 1, h - 1);
		g.dispose();
		
		ImageComponent2D ic2d = null;
		if (transparent)
			ic2d = new ImageComponent2D(ImageComponent.FORMAT_RGBA, image);
		else
			// No need for the alpha channel if it doesn't need to be transparent
			ic2d = new ImageComponent2D(ImageComponent.FORMAT_RGB, image);
		
		Point3f pos = new Point3f(0f, 0f, 0f);
		Raster raster = new Raster(pos, Raster.RASTER_COLOR, 0, 0, w, h, ic2d, null);
		
		Shape3D s3d = null;
		if (transparent)
			s3d = new Shape3D(raster, m_transApp);
		else
			s3d = new Shape3D(raster, m_solidApp);
		
		return s3d;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Creates a 2d label that is always readable from whatever angle
	 */
	public static TransformGroup getLabel(String tx, Color bg, Color fg, Vector3f p, boolean t)
	{
		TransformGroup textGroup = new TransformGroup();
		Transform3D text3D = new Transform3D();
		text3D.set(p);
		textGroup.setTransform(text3D);
		textGroup.addChild(getText(tx, bg, fg, t));
		
		return textGroup;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
