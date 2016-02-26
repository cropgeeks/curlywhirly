// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.media.opengl.*;

public class SphereFactory
{
	public Sphere createSphere(GL gl, int sphereDetailLevel)
	{
		if (isAccelerated(gl))
			return new GLIcoSphere(gl, sphereDetailLevel);
		else
			return new GlutSphere(sphereDetailLevel);
	}

	private boolean isAccelerated(GL gl)
	{
		return GLDrawableFactory.getDesktopFactory().canCreateGLPbuffer(GLDrawableFactory.getDesktopFactory().getDefaultDevice(), gl.getGLProfile());
	}
}