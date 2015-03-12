package curlywhirly.gui.viewer;

import javax.media.opengl.*;

public abstract class SceneRenderable
{
	private void applyUserRotations(GL2 gl, Rotation rotation)
	{
		// Apply user rotation first.
		gl.glMultMatrixf(rotation.getDragArray(), 0);
		gl.glMultMatrixf(rotation.getCumulativeRotationArray(), 0);
	}

	public void displayRenderable(GLAutoDrawable drawable, Rotation rotation)
	{
		GL2 gl = drawable.getGL().getGL2();

		// We need to apply rotations in this renderer as well as the main scene
		gl.glPushMatrix();
		applyUserRotations(gl, rotation);

		render(gl);

		gl.glPopMatrix();

		// Does the automatic spinning of the scene
		rotation.automaticallyRotate();
	}

	public abstract void render(GL2 gl);
}
