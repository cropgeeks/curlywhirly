// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.opengl;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import javax.vecmath.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;
import curlywhirly.gui.viewer.*;

import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;


// TODO: Check what exactly needs to be done with the animators at various points
public class OpenGLPanel extends GLJPanel implements GLEventListener
{
	public static int CANVAS_WIDTH = 800;
	public static int CANVAS_HEIGHT = 600;

	private static final int X_AXIS = 0;
	private static final int Y_AXIS = 1;
	private static final int Z_AXIS = 2;

	private WinMain winMain;

	// The animator which updates the display at the desired framerate
	private Animator animator;

	private GLU glu;

	private final Rotation rotation;

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

	private CanvasMouseListener mouseListener;

	// For aspects of the viewing transform and zooming
	private int perspAngle = 45;
	private float aspect;

	// Screenshot variables
	private AWTGLReadBufferUtil glBufferUtil;
	private BufferedImage screenShot;
	private boolean takeScreenshot = false;

	// Collision detection variables
	private Point mousePoint = new Point(0, 0);
	float[] proj = new float[16];
	private CollisionDetection detector;

	private DataSet dataSet;

	private TextRenderer renderer;

	private final CloseOverlay closeOverlay;

	private float pointSize = 1f;

	public OpenGLPanel(WinMain winMain, GLCapabilities caps)
	{
		super(caps);

		this.winMain = winMain;
		closeOverlay = new CloseOverlay(winMain);

		addGLEventListener(this);
		addGLEventListener(closeOverlay);

		rotation = new Rotation();

		mouseListener = new CanvasMouseListener(this, rotation);

		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		detector = new CollisionDetection();
		reset();
	}

	// Starts animation, this should be called when you want rendering to start
	public void startAnimator()
	{
		if (animator != null)
			animator.remove(this);

		animator = new Animator(this);
		animator.setUpdateFPSFrames(200, System.out);
		animator.setPrintExceptions(true);
		animator.start();
	}

	// Stop the animator in situations where you need the display to pause
	// updating and to prevent unpredictable animation behaviour
	public void stopAnimator()
	{
		if (animator != null)
		{
			animator.stop();
			animator.pause();
		}
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		// Our basic GL setup configuration
		GL2 gl = drawable.getGL().getGL2();

		glu = new GLU();
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthMask(true);
		gl.glDepthRangef(0f, 1f);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);
		gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glLineWidth(1.5f);
		gl.setSwapInterval(1);
		gl.glEnable(GL_RESCALE_NORMAL);
		gl.glEnable(GL_CULL_FACE);

		createSphereVertexBuffer(gl);

		lightScene(gl);

		glBufferUtil = new AWTGLReadBufferUtil(drawable.getGLProfile(), false);

		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36), true, false);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		try
		{
			glBufferUtil.dispose(drawable.getGL());

			renderer = null;
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		// Don't try to render when the animator is paused
		if (animator.isPaused())
			return;

		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();
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

		if (takeScreenshot)
			screenShot = glBufferUtil.readPixelsToBufferedImage(gl, true);

		drawTooltip(gl);
	}

	private void applyUserRotations(GL2 gl)
	{
		// Apply user rotation first.
		gl.glMultMatrixf(rotation.getDragArray(), 0);
		gl.glMultMatrixf(rotation.getCumulativeRotationArray(), 0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();

		height = height == 0 ? 1 : height;
		aspect = (float)width / height;

		CANVAS_WIDTH = width;
		CANVAS_HEIGHT = height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matching viewport
		setPerspective(gl);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		mouseListener.initialiseArcBall(CANVAS_WIDTH, CANVAS_HEIGHT);
		animator.start();
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

	private void setPerspective(GL2 gl)
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

	private void lightScene(GL2 gl)
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

		float[] xAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw X-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(-50f, 0, 0);
		gl.glVertex3f(50f, 0, 0);
		gl.glEnd();

		float [] yAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw Y-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, -50f, 0);
		gl.glVertex3f(0, 50f, 0);
		gl.glEnd();

		float [] zAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
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

		float[] xAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
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

		float [] yAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
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

		float [] zAxisColor = convertRgbToGl(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
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
			float [] rgba = convertRgbToGl(color);
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
		float[] axes = point.getPosition(winMain.getDataSet().getCurrentAxes());
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

	private void createSphereVertexBuffer(GL2 gl)
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

	// Our translations are stored in a -1 to 1 range and we want to re-map these
	// into our -50 to 50 coordinate space.
	private float map(float number)
	{
		return ((number-(-1f))/(1f-(-1f)) * (50f-(-50f)) + -50f);
	}

	public void reset()
	{
		// Reset Rotation
		rotation.setIdentity();

		perspAngle = 45;
	}

	// Methods used to update the display from outwith the class

	public void toggleSpin()
	{
		rotation.toggleSpin();
	}

	private void clearColor(GL2 gl)
	{
		Color clearColor = ColorPrefs.get("User.OpenGLPanel.background");
		// Divide down to a 0-1 range from a 0-255 range
		gl.glClearColor(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0);
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

	// Toggles the state of takeScreenshot and manually calls display to get
	// a screenshot without negatively impacting frame rate.
	public BufferedImage getScreenShot()
	{
		takeScreenshot = true;

		display();

		takeScreenshot = false;

		return screenShot;
	}

	private float[] convertRgbToGl(Color color)
	{
		return new float[] { color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1f };
	}

	public void setMousePoint(Point point)
	{
		this.mousePoint = point;
	}

	private Ray getRay(GL2 gl)
	{
		int[] view = new int[4];
		float[] model = new float[16];

		gl.glGetIntegerv(GL.GL_VIEWPORT, view, 0);
		// Get the current model view matrix
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, model, 0);
		float winX = mousePoint.x;
		// Adjust into opengl y space
		float winY = CANVAS_HEIGHT - (float)mousePoint.y -1;

		// Used to store the result of gluUnproject for the near clipping plane
		float[] near = new float[4];
		glu.gluUnProject(winX, winY, 0, model, 0, proj, 0, view, 0, near, 0);
		Vector3f nVec = new Vector3f(near);

		// Used to store the result of gluUnproject for the far clipping plane
		float[] far = new float[4];
		glu.gluUnProject(winX, winY, 1, model, 0, proj, 0, view, 0, far, 0);
		Vector3f fVec = new Vector3f(far);

		// Subtract the near clipping plane vector from the far clipping plane
		// vector to establish the direction of our ray
		Vector3f dir = new Vector3f(fVec.x - nVec.x, fVec.y - nVec.y, fVec.z - nVec.z);
		Vector3f eye = new Vector3f(0, 0, 200);

		Ray ray = new Ray(dir, eye);

		return ray;
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

	private void drawTooltip(GL2 gl)
	{
		// Mouse over code looking for sphere's under the mouse
		Ray ray = getRay(gl);
		DataPoint found = detector.findSphereRayIntersection(ray, pointSize);

		// If we have found a spehre, display this sphere's name in a tooltip
		String text = found != null ? found.getName() : null;
		setToolTipText(text);
	}

	private String getAxisLabel(int axis)
	{
		switch (axis)
		{
			case X_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getAxisLabels()[0] : "X";
			case Y_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getAxisLabels()[1] : "Y";
			case Z_AXIS: return Prefs.guiChkDatasetLabels ? dataSet.getAxisLabels()[2] : "Z";

			default: return null;
		}
	}

	public CloseOverlay getCloseOverlay()
		{ return closeOverlay; }
}