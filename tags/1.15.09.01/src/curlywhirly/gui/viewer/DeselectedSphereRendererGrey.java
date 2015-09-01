// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.viewer;

import java.awt.*;
import javax.media.opengl.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

public class DeselectedSphereRendererGrey extends AbstractSphereRenderer
{
	@Override
	public void renderSpheres(GL2 gl)
	{
		// Get each color component into the 0-1 range instead of 0-255
		float [] rgba = Color.DARK_GRAY.getRGBColorComponents(new float[3]);
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);

		renderPoints(gl, dataSet.deselectedPoints());
	}
}