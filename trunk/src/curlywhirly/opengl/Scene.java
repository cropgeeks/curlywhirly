package curlywhirly.opengl;

import java.awt.*;
import java.nio.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;
import curlywhirly.gui.viewer.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.awt.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;
import static javax.media.opengl.fixedfunc.GLPointerFunc.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

public class Scene
{
    private static final int X_AXIS = 0;
	private static final int Y_AXIS = 1;
	private static final int Z_AXIS = 2;

    private DataSet dataSet;
    private Rotation rotation;

    private GLU glu;

    // An custom made sphere object (faster than gluSphere)
	private IcoSphere sphere;
	// GL 1.5+, VBO ID for vertex data
	private int icosphereVertexID;
	// GL 1.5+, VBO ID for face data
	private int icosphereIndexID;
	// Buffer to hold isosphere vertex data.
	private FloatBuffer vertexBuffer;
	// Buffer to hold faces, represented by 3 indices
	private IntBuffer indexBuffer;

    // For aspects of the viewing transform and zooming
	private int perspAngle;
	private float aspect;
    float[] proj = new float[16];

    private float pointSize = 1f;

    private TextRenderer renderer;
	private CollisionDetection detector;

    public Scene(DataSet dataSet, Rotation rotation, int perspAngle, float aspect, CollisionDetection detector)
    {
        this.dataSet = dataSet;
        this.rotation = rotation;
        this.perspAngle = perspAngle;
        this.aspect = aspect;
		this.detector = detector;

        glu = new GLU();
    }

    void init(GL2 gl)
    {
        createSphereVertexBuffer(gl);
		setupLighting(gl);

		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36), true, false);
    }

    public void render(GL2 gl)
    {
		clearColor(gl);
		// Clear the colour and depth buffers
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		updatePerspective(gl);

		gl.glPushMatrix();

		applyUserRotations(gl);

		// Render spheres first as if they are rendered after axis labels
		// the spheres become obscured by the axis labels.
		drawSpheres(gl);
		drawAxes(gl);
//		viewExtentCube(gl);

		gl.glPopMatrix();

		rotation.automaticallyRotate();
    }

    private void clearColor(GL2 gl)
	{
		Color clearColor = ColorPrefs.get("User.OpenGLPanel.background");
		// Divide down to a 0-1 range from a 0-255 range
		gl.glClearColor(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0);
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
		glu.gluLookAt(0, 0, 200, 0, 0, 0, 0, 1, 0);

		// Store the projection matrix for use in gluUnproject calls
		gl.glGetFloatv(GL_PROJECTION_MATRIX, proj, 0);
	}

    private void applyUserRotations(GL2 gl)
	{
		// Apply user rotation first.
		gl.glMultMatrixf(rotation.getDragArray(), 0);
		gl.glMultMatrixf(rotation.getCumulativeRotationArray(), 0);
	}

    	private void drawSpheres(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL_ARRAY_BUFFER, icosphereVertexID);
		gl.glBufferData(GL_ARRAY_BUFFER, sphere.vertexCount()*4, vertexBuffer, GL_STATIC_DRAW);
		gl.glVertexPointer(3, GL_FLOAT,0,0);
		gl.glNormalPointer(GL_FLOAT,0,0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, icosphereIndexID);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, sphere.faceNormalCount()*4, indexBuffer, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);

		// Color spheres appropriately
		for (DataPoint point : dataSet)
		{
			Color color = point.getColor(dataSet.getCurrentCategoryGroup());
			// Get each color component into the 0-1 range instead of 0-255
			float [] rgba = CWUtils.convertRgbToGl(color);
			gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
			gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);
			drawSphere(gl, point);
		}

		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawSphere(GL2 gl, DataPoint point)
	{
		gl.glPushMatrix();
		// Scale our unit sphere down to a more manageable scale
		gl.glScalef(pointSize, pointSize, pointSize);

		// Get the position information for each axis so that these can be used
		// to translate our spheres to the correct location
		float[] axes = point.getPosition(dataSet.getCurrentAxes());
		// Bring our translations into the correct coordinate space as we've
		// scaled each point to 1/200 of its original size.
		float translationScale = 1/pointSize;
		gl.glTranslatef(map(axes[0])*translationScale, map(axes[1])*translationScale, map(axes[2])*translationScale);

		float[] modelView = new float[16];
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelView, 0);
		detector.updatePointLocation(modelView, axes, point);

		// Draw the triangles using the isosphereIndexBuffer VBO for the
		// element data (as well as the isosphereVertexBuffer).
		gl.glDrawElements(GL_TRIANGLES, sphere.faceNormalCount(), GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
	}

	void createSphereVertexBuffer(GL2 gl)
	{
		// Vertex buffers for our spheres
		int[] bufferID = new int[2];
		gl.glGenBuffers(2, bufferID, 0);
		icosphereVertexID = bufferID[0];
		icosphereIndexID = bufferID[1];

		// A single sphere object to be copied from
		sphere = new IcoSphere(1);

		vertexBuffer = Buffers.newDirectFloatBuffer(sphere.vertexCount());
		for (float vertex : sphere.getVertices())
			vertexBuffer.put(vertex);
		vertexBuffer.rewind();

		indexBuffer = Buffers.newDirectIntBuffer(sphere.faceNormalCount());
		for (int face : sphere.getFaceNormals())
			indexBuffer.put(face);
		indexBuffer.rewind();
	}

    private void drawAxes(GL2 gl)
	{
		drawAxesLines(gl);
		drawAxesCones(gl);
	}

	private void drawAxesLines(GL2 gl)
	{
		// Enable / disable antialiasing for lines based on user preference.
		if (Prefs.guiAntialiasAxes)
		{
			gl.glEnable(GL_BLEND);
			gl.glEnable(GL_LINE_SMOOTH);
		}
		else
		{
			gl.glDisable(GL_BLEND);
			gl.glDisable(GL_LINE_SMOOTH);
		}

		float[] xAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw X-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(-50f, 0, 0);
		gl.glVertex3f(50f, 0, 0);
		gl.glEnd();

		float [] yAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw Y-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, -50f, 0);
		gl.glVertex3f(0, 50f, 0);
		gl.glEnd();

		float [] zAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw Z-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, 0, -50f);
		gl.glVertex3f(0, 0, 50f);
		gl.glEnd();

		// If the user has chosen to antialias axes we must disable this when
		// we are done.
		if (Prefs.guiAntialiasAxes)
		{
			gl.glDisable(GL_BLEND);
			gl.glDisable(GL_LINE_SMOOTH);
		}
	}

	private void drawAxesCones(GL2 gl)
	{
		GLUquadric quadric = glu.gluNewQuadric();

		float[] xAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		// Draw the cylinders at the positive extent of each axis
		gl.glPushMatrix();
		gl.glTranslatef(50f, 0, 0);
		gl.glPushMatrix();
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);
		gl.glPopMatrix();

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(X_AXIS));
		gl.glPopMatrix();

		float [] yAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		gl.glPushMatrix();
		gl.glTranslatef(0, 50f, 0);
		gl.glPushMatrix();
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);
		gl.glPopMatrix();

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(Y_AXIS));
		gl.glPopMatrix();

		float [] zAxisColor = CWUtils.convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 50f);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(Z_AXIS));
		gl.glPopMatrix();

		glu.gluDeleteQuadric(quadric);
	}

    private String getAxisLabel(int axis)
	{
		switch (axis)
		{
			case X_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getCurrentAxisLabels()[0] : "X";
			case Y_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getCurrentAxisLabels()[1] : "Y";
			case Z_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getCurrentAxisLabels()[2] : "Z";

			default: return null;
		}
	}

    // To render billboarded text we must rotate the text that we are drawing
	// by the inverse of the rotations that are applied to the camera. This
	// keeps the text facing the camera at all times.
	private void billboardText(GL2 gl, String text)
		throws GLException
	{
		float[] invMat = rotation.getInverseDragArray();
		float[] invCombArr = rotation.getInverseCumulativeArray();

		// The order that we apply the combined and mouse rotations is reversed
		// from the order in the display code. This is because we need these
		// rotations to happen in the reverse order.
		gl.glMultMatrixf(invCombArr, 0);
		gl.glMultMatrixf(invMat, 0);

		renderer.begin3DRendering();
		renderer.setColor(ColorPrefs.get("User.OpenGLPanel.textColor"));
		renderer.draw3D(text, 2f, -1f, 0, 0.1f);
		renderer.end3DRendering();
	}

    void setupLighting(GL2 gl)
	{
		// Set up lighting
		float[] lightAmbient = {0.4f, 0.4f, 0.4f, 1f};
		float[] lightSpecular = {0.3f, 0.3f, 0.3f, 1f};
		float[] lightPosition = { 1, 0.0f, 0, 1.0f };

		gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);
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

    // Our translations are stored in a -1 to 1 range and we want to re-map these
	// into our -50 to 50 coordinate space.
	private float map(float number)
	{
		return ((number-(-1f))/(1f-(-1f)) * (50f-(-50f)) + -50f);
	}

    void setAspect(float aspect)
    {
        this.aspect = aspect;
    }

    public void setPointSize(float pointSize)
	{
		this.pointSize = pointSize;
	}

    public float getPointSize()
        { return pointSize; }

    public float[] getProj()
        { return proj; }
}
