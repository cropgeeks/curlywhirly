package curlywhirly.opengl;

import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.vecmath.*;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;
import curlywhirly.gui.viewer.*;

import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLPointerFunc.*;


// TODO: Check what exactly needs to be done with the animators at various points
public class OpenGLPanel extends GLJPanel implements GLEventListener
{
	public static int CANVAS_WIDTH = 800;
	public static int CANVAS_HEIGHT = 600;

	// Our target framerate
	private static final int FPS = 60;
	// The animator which updates the display at the desired framerate
	private static FPSAnimator animator;

	private CurlyWhirly frame;

	// Variables related automatic rotation of the model
	private boolean autoSpin = false;
	private float angle = 0f;
	private float speed = -0.1f;

	private GLU glu;

	// Rotation variables to allow compound rotations
	private Matrix4f lastRot = new Matrix4f();
	private Matrix4f currRot = new Matrix4f();
	private float[] matrix = new float[16];
	private final Object matrixLock = new Object();
	private Matrix4f autoRot = new Matrix4f();
	private float[] matRot = new float[16];

	// An custom made sphere object (faster than gluSphere)
	private IcoSphere sphere;
	// GL 1.5+, VBO ID for vertex data
	private int icosphereVertexID;
	// GL 1.5+, VBO ID for face data
	private int icosphereIndexID;

	private CanvasMouseListener mouseListener;
	private Color clearColor = Color.BLACK;

	private TextRenderer texRend;

	// For aspects of the viewing transform and zooming
	private int perspAngle = 45;
	private float aspect;
	boolean doZoom = false;

	public OpenGLPanel(CurlyWhirly frame)
	{
		this.frame = frame;

		addGLEventListener(this);

		mouseListener = new CanvasMouseListener(this);

		// Initialize mouse handling code
        lastRot.setIdentity();
        currRot.setIdentity();
		autoRot.setIdentity();
		get(matrix, currRot);

		texRend = new TextRenderer(new Font("MONOSPACED", Font.PLAIN, 12));
	}

	// Starts animation, this should be called when you want rendering to start
	public void startAnimator()
	{
		if (animator != null)
			animator.remove(this);

		animator = new FPSAnimator(this, FPS, true);
//		animator.setUpdateFPSFrames(200, System.out);
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
		gl.glEnable(GL_LINE_SMOOTH);
		gl.glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
		gl.glLineWidth(1.0f);
		gl.setSwapInterval(1);
		gl.glEnable(GL.GL_CULL_FACE);

		// Vertex buffers for our spheres
		int[] bufferID = new int[2];
		gl.glGenBuffers(2, bufferID, 0);
		icosphereVertexID = bufferID[0];
		icosphereIndexID = bufferID[1];

		// A single sphere object to be copied from
		sphere = new IcoSphere(1);

		lightScene(gl);
	}

	@Override
	public void dispose(GLAutoDrawable glad)
	{
	}

	float[] lRot = new float[16];

	@Override
	public void display(GLAutoDrawable drawable)
	{
		// Don't try to render when the animator is paused
		if (animator.isPaused())
			return;

		synchronized(matrixLock)
		{
//			currRot.mul(autoRot);
			get(matrix, currRot);
			get(matRot, autoRot);
		}

		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();

		if (doZoom)
			zoomPerspective(gl);

		clearColor(gl);
		// Clear the colour and depth buffers
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glPushMatrix();
		gl.glMultMatrixf(matRot, 0);
		gl.glMultMatrixf(matrix, 0);

		drawAxes(gl);
		drawSpheres(gl);

//		viewExtentCube(gl);

		gl.glPopMatrix();

		if (autoSpin)
		{
			angle += speed;
			rotateMatrix(angle);
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();

		height = height == 0 ? 1 : height;
		aspect = (float)width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matching viewport
		// Switch to the projection matrix and reset it.
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		// Field of view, aspect ratio, z plane near, z plane far
		glu.gluPerspective(perspAngle, aspect, 0, 100);
		glu.gluLookAt(1, 0, 2, 0, 0, 0, 0, 1, 0);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		// Rotate view so that it looks at the centre of the model
		gl.glRotatef(30, 0, 1, 0);

		mouseListener.initialiseArcBall(CANVAS_WIDTH, CANVAS_HEIGHT);
		animator.start();
	}

	// Carries out a zoom operation by altering the projection matrix
	// (specifically by narrowing and widening the viewing angle)
	private void zoomPerspective(GL2 gl)
	{
		gl.glPushMatrix();
		// Switch to and update the projection matrix
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(perspAngle, aspect, 0, 100);
		glu.gluLookAt(1, 0, 2, 0, 0, 0, 0, 1, 0);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		doZoom = false;
		gl.glPopMatrix();
	}

	private void lightScene(GL2 gl2)
	{
		// Set up lighting
		float[] lightAmbient = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] lightSpecular = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] lightPosition = { 1.0f, 1.0f, 1.0f, 1.0f };

		gl2.glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient, 0);
		gl2.glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse, 0);
		gl2.glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular, 0);
		gl2.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);

		// Enable lighting in GL.
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glEnable(GL2.GL_LIGHT0);
	}

	private void drawAxes(GL2 gl)
	{
		// Extra push matrix needed to keep axis cones on axes...
		gl.glPushMatrix();

		gl.glPushMatrix();
//		gl.glMultMatrixf(matrix, 0);

		// Set material properties.
		float[] rgba = {0.2f, 1f, 0.2f};
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 50);
		gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, rgba, 0);
		gl.glMaterialfv( GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);

		drawAxesLines(gl);
		drawAxesCones(gl);

		gl.glPopMatrix();

//		renderText(gl);
	}

	private void drawAxesLines(GL2 gl)
	{
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1f, 0, 0);

		// X-axis
		gl.glVertex3f(-0.5f, 0, 0);
		gl.glVertex3f(0.5f, 0, 0);

		// Y-axis
		gl.glVertex3f(0, -0.5f, 0);
		gl.glVertex3f(0, 0.5f, 0);

		// Z-axis
		gl.glVertex3f(0, 0, -0.5f);
		gl.glVertex3f(0, 0, 0.5f);
		gl.glEnd();
	}

	private void drawAxesCones(GL2 gl)
	{
		GLUquadric quadric = glu.gluNewQuadric();

		// Draw the cylinders at the positive extent of each axis
		gl.glPushMatrix();
		gl.glTranslatef(0, 0.5f, 0);
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0.5f, 0, 0);
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.5f);
		glu.gluCylinder(quadric, 0.01, 0, 0.02, 6, 6);
		gl.glPopMatrix();

		glu.gluDeleteQuadric(quadric);
	}

	private void drawSpheres(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, icosphereVertexID);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, sphere.vertexCount()*4,
			  sphere.vertexBuffer(), GL.GL_STATIC_DRAW);
		gl.glVertexPointer(3,GL.GL_FLOAT,0,0);
		gl.glNormalPointer(GL.GL_FLOAT,0,0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, icosphereIndexID);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sphere.indexCount()*4,
			  sphere.indexBuffer(), GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);

		// Color spheres appropriately
		DataSet dataSet = frame.getDataSet();
		int currentCategory = dataSet.getCategorySchemeIndex();
		boolean highlightAll = dataSet.highlightAllCategories;

		float[] rgba;
		for (DataEntry dataEntry : frame.getDataSet().dataEntries)
		{
			Color3f color = dataEntry.getColor(currentCategory, highlightAll);
			rgba = new float[] { color.x, color.y, color.z, 1f };
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
			gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 50);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, rgba, 0);
			gl.glMaterialfv( GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
			drawSphere(gl, dataEntry);
		}

		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawSphere(GL2 gl, DataEntry dataEntry)
	{
		gl.glPushMatrix();
		// Scale our unit sphere down to a more manageable scale
		gl.glScalef(0.005f, 0.005f, 0.005f);

		// Mouse based rotations
//		gl.glMultMatrixf(matrix, 0);
		float[] indices = dataEntry.getPosition(frame.getDataSet().getCurrentAxes());
		// Bring our translations into the correct coordinate space
		gl.glTranslatef(map(indices[0])*200f, map(indices[1])*200f, map(indices[2])*200f);

		// Draw the triangles using the isosphereIndexBuffer VBO for the
		// element data (as well as the isosphereVertexBuffer).
		gl.glDrawElements(GL.GL_TRIANGLES, sphere.indexCount(), GL.GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
	}

	private float map(float number)
	{
		return ((number-(-1f))/(1f-(-1f)) * (0.5f-(-0.5f)) + -0.5f);
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
			angle = 0;
//			matrix = new float[16];
			mouseListener.initialiseArcBall(getWidth(), getHeight());
		}
	}

	public void updateLastRotation()
	{
		// Set Last Static Rotation To Last Dynamic One
		synchronized(matrixLock)
		{
			lastRot.set(currRot);
		}
	}

	public void updateCurrentRotation(Quat4f rotQuat)
	{
//		Quat4f auto = new Quat4f();

		// Get rotation as quaternion and multiply by our automatic rotation
//		autoRot.get(auto);
//		auto.mul(rotQuat);
//		rotQuat.mul(auto);
//		auto.mulInverse(rotQuat);
		synchronized(matrixLock)
		{
			// Convert Quaternion Into Matrix3fT
			currRot.setRotation(rotQuat);
			// Accumulate Last Rotation Into This One
			currRot.mul(currRot, lastRot);
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

	public void setClearColor(Color color)
	{
		clearColor = color;
	}

	private void clearColor(GL2 gl)
	{
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
		gl.glBegin(GL_QUADS); // of the color cube

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

		gl.glEnd(); // of the color cube
	}
}