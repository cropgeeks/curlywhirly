package curlywhirly.opengl;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import javax.vecmath.*;

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

	// Our target framerate
	private static final int FPS = 60;
	// The animator which updates the display at the desired framerate
	private static FPSAnimator animator;
	private WinMain winMain;

	// Variables related automatic rotation of the model
	private boolean autoSpin = false;
	private float speed = -0.1f;

	private GLU glu;

	// Rotation variables to allow compound rotations
	private Matrix4f lastRot = new Matrix4f();
	private Matrix4f currRot = new Matrix4f();
	private float[] matrix = new float[16];
	private final Object matrixLock = new Object();
	private Matrix4f autoRot = new Matrix4f();
	private Matrix4f combined = new Matrix4f();
	private float[] combArr = new float[16];

	// An custom made sphere object (faster than gluSphere)
	private IcoSphere sphere;
	// GL 1.5+, VBO ID for vertex data
	private int icosphereVertexID;
	// GL 1.5+, VBO ID for face data
	private int icosphereIndexID;

	private CanvasMouseListener mouseListener;

	private TextRenderer texRend;

	// For aspects of the viewing transform and zooming
	private int perspAngle = 45;
	private float aspect;
	boolean doZoom = false;

	private boolean isDragging = false;

	private AWTGLReadBufferUtil glBufferUtil;
	private BufferedImage screenShot;
	private boolean takeScreenshot = false;

	private Point mousePoint = new Point(0, 0);
	float[] proj = new float[16];

	// Keeps track of the translated locations of points in the display
	// used in ray tracing code to find points under the mouse.
	private HashMap<DataPoint, float[]> translatedPoints;

	private CollisionDetection detector;

	private DataSet dataSet;

	public OpenGLPanel(WinMain winMain)
	{
		this.winMain = winMain;

		addGLEventListener(this);

		mouseListener = new CanvasMouseListener(this);

		// Initialize mouse handling code
        lastRot.setIdentity();
        currRot.setIdentity();
		autoRot.setIdentity();
		combined.setIdentity();

		texRend = new TextRenderer(new Font("MONOSPACED", Font.PLAIN, 12));

		detector = new CollisionDetection();

		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		translatedPoints = new HashMap<>();
		reset();
	}

	// Starts animation, this should be called when you want rendering to start
	public void startAnimator()
	{
		if (animator != null)
			animator.remove(this);

		animator = new FPSAnimator(this, FPS, true);
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
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);
		gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glLineWidth(1.5f);
		gl.setSwapInterval(1);
		gl.glEnable(GL_RESCALE_NORMAL);
		gl.glEnable(GL_CULL_FACE);

		// Vertex buffers for our spheres
		int[] bufferID = new int[2];
		gl.glGenBuffers(2, bufferID, 0);
		icosphereVertexID = bufferID[0];
		icosphereIndexID = bufferID[1];

		// A single sphere object to be copied from
		sphere = new IcoSphere(1);

		lightScene(gl);

		glBufferUtil = new AWTGLReadBufferUtil(drawable.getGLProfile(), false);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		try
		{
			glBufferUtil.dispose(drawable.getGL());
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

		synchronized(matrixLock)
		{
			get(matrix, currRot);
			get(combArr, combined);
		}

		zoomPerspective(gl);

		clearColor(gl);
		// Clear the colour and depth buffers
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glPushMatrix();

		if (isDragging)
			gl.glMultMatrixf(matrix, 0);

		gl.glMultMatrixf(combArr, 0);

		drawAxes(gl);
		drawSpheres(gl);

//		viewExtentCube(gl);

		gl.glPopMatrix();

		if (autoSpin)
		{
			synchronized(matrixLock)
			{
				rotateMatrix(speed);
				// When carrying out automatic rotation we need to multiply the
				// automatic rotation by our combined rotation and set our
				// combined rotation to the result of this.
				combined.mul(autoRot, combined);
			}
		}

		if (takeScreenshot)
			screenShot = glBufferUtil.readPixelsToBufferedImage(gl, true);

		// Mouse over code looking for sphere's under the mouse
		Ray ray = getRay(gl);
		DataPoint found = detector.findSphereRayIntersection(ray, translatedPoints);

		// If we have found a spehre, display this sphere's name in a tooltip
		if (found != null)
			this.setToolTipText(found.getName());
		else
			this.setToolTipText(null);
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
		// Switch to the projection matrix and reset it.
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		// Field of view, aspect ratio, z plane near, z plane far
		glu.gluPerspective(perspAngle, aspect, 1, 1000);
		glu.gluLookAt(0, 0, 2, 0, 0, 0, 0, 1, 0);

		// Store the projection matrix for use in gluUnproject calls
		gl.glGetFloatv(GL_PROJECTION_MATRIX, proj, 0);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		// Rotate view so that it looks at the centre of the model
//		gl.glRotatef(30, 0, 1, 0);

		mouseListener.initialiseArcBall(CANVAS_WIDTH, CANVAS_HEIGHT);
		animator.start();
	}

	// Carries out a zoom operation by altering the projection matrix
	// (specifically by narrowing and widening the viewing angle)
	private void zoomPerspective(GL2 gl)
	{
		if (doZoom)
		{
			gl.glPushMatrix();
			// Switch to and update the projection matrix
			gl.glMatrixMode(GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(perspAngle, aspect, 1, 1000);
			glu.gluLookAt(0, 0, 2, 0, 0, 0, 0, 1, 0);

			// Store the projection matrix for use in gluUnproject calls
			gl.glGetFloatv(GL_PROJECTION_MATRIX, proj, 0);

			// Enable the model-view transform
			gl.glMatrixMode(GL_MODELVIEW);
			doZoom = false;
			gl.glPopMatrix();
		}
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

//		renderText(gl);
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

		float[] xAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw X-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(-0.5f, 0, 0);
		gl.glVertex3f(0.5f, 0, 0);
		gl.glEnd();

		float [] yAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw Y-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, -0.5f, 0);
		gl.glVertex3f(0, 0.5f, 0);
		gl.glEnd();

		float [] zAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw Z-axis
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, 0, -0.5f);
		gl.glVertex3f(0, 0, 0.5f);
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

		float[] xAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.xAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		// Draw the cylinders at the positive extent of each axis
		gl.glPushMatrix();
		gl.glTranslatef(0.5f, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		float [] yAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.yAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0.5f, 0);
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		float [] zAxisColor = getOpenGLColor(ColorPrefs.get("User.OpenGLPanel.zAxisColor"));
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 1);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.5f);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		glu.gluDeleteQuadric(quadric);
	}

	private void drawSpheres(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL_ARRAY_BUFFER, icosphereVertexID);
		gl.glBufferData(GL_ARRAY_BUFFER, sphere.vertexCount()*4,
			  sphere.vertexBuffer(), GL_STATIC_DRAW);
		gl.glVertexPointer(3, GL_FLOAT,0,0);
		gl.glNormalPointer(GL_FLOAT,0,0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, icosphereIndexID);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, sphere.indexCount()*4,
			  sphere.indexBuffer(), GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);

		// Color spheres appropriately
		for (DataPoint point : dataSet)
		{
			Color color = point.getColor(dataSet.getCurrentCategoryGroup());
			// Get each color component into the 0-1 range instead of 0-255
			float [] rgba = getOpenGLColor(color);
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
		gl.glScalef(0.005f, 0.005f, 0.005f);

		// Get the position information for each axis so that these can be used
		// to translate our spheres to the correct location
		float[] indices = point.getPosition(winMain.getDataSet().getCurrentAxes());
		// Bring our translations into the correct coordinate space as we've
		// scaled each point to 1/200 of its original size.
		gl.glTranslatef(map(indices[0])*200f, map(indices[1])*200f, map(indices[2])*200f);

		updateTranslatedPoints(gl, indices, point);

		// Draw the triangles using the isosphereIndexBuffer VBO for the
		// element data (as well as the isosphereVertexBuffer).
		gl.glDrawElements(GL_TRIANGLES, sphere.indexCount(), GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
	}

	// Our translations are stored in a -1 to 1 range and we want to re-map these
	// into our -0.5 to 0.5 coordinate space.
	private float map(float number)
	{
		return ((number-(-1f))/(1f-(-1f)) * (0.5f-(-0.5f)) + -0.5f);
	}

	// Keeps track of the translated positions of all of our DataPoints
	private void updateTranslatedPoints(GL2 gl, float[] indices, DataPoint point)
	{
		float[] modelView = new float[16];
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelView, 0);

		float x = (indices[0] * modelView[0]) +  (indices[1] * modelView[4]) + (indices[2] * modelView[8]) + modelView[12];
		float y = (indices[0] * modelView[1]) +  (indices[1] * modelView[5]) + (indices[2] * modelView[9]) + modelView[13];
		float z = (indices[0] * modelView[2]) +  (indices[1] * modelView[6]) + (indices[2] * modelView[10]) + modelView[14];

		translatedPoints.put(point, new float[] { x, y, z });
	}


	// Methods relating to arbitrary rotations

	void rotateMatrix(float angle)
	{
		angle = (float) ((angle * Math.PI) / 180);

		autoRot.m00 = (float) Math.cos(angle);
		autoRot.m01 = 0;
		autoRot.m02 = (float) - Math.sin(angle);
		autoRot.m03 = 0;

		autoRot.m10 = 0;
		autoRot.m11 = 1;
		autoRot.m12 = 0;
		autoRot.m13 = 0;

		autoRot.m20 = (float) Math.sin(angle);
		autoRot.m21 = 0;
		autoRot.m22 = (float) Math.cos(angle);
		autoRot.m23 = 0;

		autoRot.m30 = 0;
		autoRot.m31 = 0;
		autoRot.m32 = 0;
		autoRot.m33 = 1;
	}

	public void reset()
	{
		// Reset Rotation
		synchronized(matrixLock)
		{
			lastRot.setIdentity();
			currRot.setIdentity();
			autoRot.setIdentity();
			combined.setIdentity();
		}

		perspAngle = 45;
		doZoom = true;
	}

	public void updateLastRotation()
	{
		if (!autoSpin)
		{
			// Set Last Static Rotation To Last Dynamic One
			synchronized(matrixLock)
			{
				lastRot.set(currRot);
			}
		}
	}

	public void updateCurrentRotation(Quat4f rotQuat)
	{
		if (!autoSpin)
		{
			synchronized(matrixLock)
			{
				// Convert Quaternion Into Matrix3f
				currRot.setRotation(rotQuat);
				// Accumulate Last Rotation Into This One
				currRot.mul(currRot, lastRot);
			}
		}
	}

	private void get(float[] dest, Matrix4f matrix)
	{
		dest[0] = matrix.m00;
        dest[1] = matrix.m10;
        dest[2] = matrix.m20;
        dest[3] = matrix.m30;
        dest[4] = matrix.m01;
        dest[5] = matrix.m11;
        dest[6] = matrix.m21;
        dest[7] = matrix.m31;
        dest[8] = matrix.m02;
        dest[9] = matrix.m12;
        dest[10] = matrix.m22;
        dest[11] = matrix.m32;
        dest[12] = matrix.m03;
        dest[13] = matrix.m13;
        dest[14] = matrix.m23;
        dest[15] = matrix.m33;
	}

	// Methods used to update the display from outwith the class

	public void toggleSpin()
	{
		autoSpin = !autoSpin;
	}

	public void toggleDragging()
	{
		isDragging = !isDragging;
	}

	// When the mouse is released froma drag operation we need to carefully
	// manage the state of our rotation matrices.
	public void mouseUp()
	{
		synchronized (matrixLock)
		{
			// Multiply the current mouse rotation matrix by the combined matrix
			// and store as the new combined matrix (multiply in this order as
			// our mouse rotations were being applied first while dragging).
			combined.mul(currRot, combined);
			// Reset currRot to prevent mouse clicks rotating the model
			currRot.setIdentity();
		}
	}

	private void clearColor(GL2 gl)
	{
		Color clearColor = ColorPrefs.get("User.OpenGLPanel.background");
		// Divide down to a 0-1 range from a 0-255 range
		gl.glClearColor(clearColor.getRed()/255f, clearColor.getGreen()/255f, clearColor.getBlue()/255f, 0);
	}

	public void setSpeed(float speed)
	{
		this.speed = ((speed-0f)/(100f-0f) * (-1.0f-(-0.1f)) + -0.1f);
	}

	public void zoom(int zoom)
	{
		perspAngle += zoom;
		perspAngle = perspAngle < 1 ? 1 : perspAngle;
		perspAngle = perspAngle > 90 ? 90 : perspAngle;
		doZoom = true;
	}

	private void viewExtentCube(GL2 gl)
	{
		gl.glBegin(GL_QUADS);

		// Top-face
		gl.glVertex3f(0.5f, 0.5f, -0.5f);
		gl.glVertex3f(-0.5f, 0.5f, -0.5f);
		gl.glVertex3f(-0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0.5f, 0.5f, 0.5f);

		// Bottom-face
		gl.glVertex3f(0.5f, -0.5f, 0.5f);
		gl.glVertex3f(-0.5f, -0.5f, 0.5f);
		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(0.5f, -0.5f, -0.5f);

		// Front-face
		gl.glVertex3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f, -0.5f, 0.5f);
		gl.glVertex3f(0.5f, -0.5f, 0.5f);

		// Back-face
		gl.glVertex3f(0.5f, -0.5f, -0.5f);
		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(-0.5f, 0.5f, -0.5f);
		gl.glVertex3f(0.5f, 0.5f, -0.5f);

		// Left-face
		gl.glVertex3f(-0.5f, 0.5f, 0.5f);
		gl.glVertex3f(-0.5f, 0.5f, -0.5f);
		gl.glVertex3f(-0.5f, -0.5f, -0.5f);
		gl.glVertex3f(-0.5f, -0.5f, 0.5f);

		// Right-face
		gl.glVertex3f(0.5f, 0.5f, -0.5f);
		gl.glVertex3f(0.5f, 0.5f, 0.5f);
		gl.glVertex3f(0.5f, -0.5f, 0.5f);
		gl.glVertex3f(0.5f, -0.5f, -0.5f);

		gl.glEnd();
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

	private float[] getOpenGLColor(Color color)
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
		Vector3f eye = new Vector3f(0, 0, 2);

		Ray ray = new Ray(dir, eye);

		return ray;
	}
}