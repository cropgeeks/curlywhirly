// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import jhi.curlywhirly.data.*;

import javax.media.opengl.*;
import java.awt.*;
import java.util.*;
import java.util.stream.*;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;

public class SelectedSphereRenderer extends AbstractSphereRenderer
{
	@Override
	public void renderSpheres(GL2 gl)
	{
		// Get the points by color, as this lets us set the color the minimum
		// number of times, rather than potentially changing the color for
		// each sphere.
		Map<Color, java.util.List<DataPoint>> pointsByColor = dataSet.selectedPoints().collect(Collectors.groupingBy(DataPoint::getColor));
		pointsByColor.forEach((color, points) ->
		{
			float[] rgba = color.getRGBColorComponents(new float[3]);
			gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
			gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);
			renderPoints(gl, points.stream());
		});
	}
}