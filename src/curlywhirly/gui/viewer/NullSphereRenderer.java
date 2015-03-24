package curlywhirly.gui.viewer;

import javax.media.opengl.GL2;

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