// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;


import jhi.curlywhirly.data.*;

import javax.media.opengl.*;
import java.awt.*;
import java.util.*;
import java.util.stream.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;

public class DeselectedSphereRendererTransparent extends AbstractSphereRenderer
{
	@Override
	public void renderSpheres(GL2 gl)
	{
		// Ensure OpenGL is set up correctly for transparency
		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL_CULL_FACE);
		gl.glDepthFunc(GL_LEQUAL);

		// Get the points by color, as this lets us set the color the minimum
		// number of times, rather than potentially changing the color for
		// each sphere.
		Map<Color, java.util.List<DataPoint>> pointsByColor = dataSet.deselectedPoints().collect(Collectors.groupingBy(DataPoint::getColor));
		pointsByColor.forEach((color, points) ->
		{
			float[] rgba = color.getRGBColorComponents(new float[4]);
			rgba[3] = transparencyAlpha;

			gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
			gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);
			renderPoints(gl, points.stream());
		});

		// Return OpenGL back to a state for normal (i.e. non-transparent) drawing
		gl.glEnable(GL_CULL_FACE);
		gl.glDisable(GL_BLEND);
	}
}