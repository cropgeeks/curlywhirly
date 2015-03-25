package curlywhirly.gui.viewer;

import javax.media.opengl.GL2;

public interface Sphere
{
	void preRender(GL2 gl);
	void render(GL2 gl);
	void postRender(GL2 gl);
}
