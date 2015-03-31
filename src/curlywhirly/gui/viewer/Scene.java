package curlywhirly.gui.viewer;

import curlywhirly.util.ColorPrefs;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;

public class Scene
{
    private final Rotation rotation;

    private final GLU glu;

    // For aspects of the viewing transform and zooming
	private int perspAngle;
	private float aspect;
    float[] proj = new float[16];

    public Scene(Rotation rotation, int perspAngle, float aspect)
    {
        this.rotation = rotation;
        this.perspAngle = perspAngle;
        this.aspect = aspect;

        glu = new GLU();
    }

    void init(GL2 gl)
    {
		setupLighting(gl);
    }

    public void render(GL2 gl)
    {
		clearColor(gl);
		// Clear the colour and depth buffers
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		updatePerspective(gl);
    }

    private void clearColor(GL2 gl)
	{
		float[] clearColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.background");
		gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], 0);
	}

    // If the user has altered the perspective angle this carries out a zoom
	// operation by altering the projection matrix (specifically by narrowing
	// and widening the viewing angle)
	private void updatePerspective(GL2 gl)
	{
		gl.glPushMatrix();

		setPerspective(gl);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPopMatrix();
	}

    public void setPerspective(GL2 gl)
	{
		// Switch to the projection matrix and reset it.
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		// perspective, aspect ratio, zNear, zFar
		glu.gluPerspective(perspAngle, aspect, 1, 1000);
		glu.gluLookAt(0, 0, 4, 0, 0, 0, 0, 1, 0);

		// Store the projection matrix for use in gluUnproject calls
		gl.glGetFloatv(GL_PROJECTION_MATRIX, proj, 0);
	}

    void setupLighting(GL2 gl)
	{
		// Set up lighting
		float[] lightAmbient = {0.1f, 0.1f, 0.1f, 1f};
		float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1f};
		float[] lightSpecular = {0.8f, 0.8f, 0.8f, 1f};
		float[] lightPosition = { 0f, 0f, 10f, 0f };

		gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular, 0);

		// Enable lighting in GL.
		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
	}

    public void reset()
	{
		// Reset Rotation
		rotation.setIdentity();

		perspAngle = 45;
	}

	// Methods used to update the display from outwith the class

	public boolean toggleSpin()
	{
		rotation.toggleSpin();

		return rotation.isSpinning();
	}

	public float getRotationSpeed()
	{
		return rotation.getRotationSpeed();
	}

	public void setRotationSpeed(float speed)
	{
		rotation.setRotationSpeed(speed);
	}

	public void zoom(int zoom)
	{
		perspAngle += zoom;
		perspAngle = perspAngle < 1 ? 1 : perspAngle;
		perspAngle = perspAngle > 90 ? 90 : perspAngle;
	}

    void setAspect(float aspect)
    {
        this.aspect = aspect;
    }

    public float[] getProj()
        { return proj.clone(); }
}
