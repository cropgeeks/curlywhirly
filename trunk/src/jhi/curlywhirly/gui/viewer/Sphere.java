// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.media.opengl.GL2;

public interface Sphere
{
	void preRender(GL2 gl);
	void render(GL2 gl);
	void postRender(GL2 gl);
}