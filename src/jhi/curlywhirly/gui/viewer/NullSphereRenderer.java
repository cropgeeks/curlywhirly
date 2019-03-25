// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.media.opengl.*;

/**
 * NullObject for use when deselected spheres should be rendered as invisible.
 * Removes the need for several messy null checks that would otherwise be
 * required.
 */
public class NullSphereRenderer extends AbstractSphereRenderer
{
	@Override
	public void renderSpheres(GL2 gl)
	{
	}
}