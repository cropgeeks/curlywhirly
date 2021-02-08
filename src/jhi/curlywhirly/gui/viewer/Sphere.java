// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.media.opengl.*;

public interface Sphere
{
	void preRender(GL2 gl);

	void render(GL2 gl);

	void postRender(GL2 gl);
}