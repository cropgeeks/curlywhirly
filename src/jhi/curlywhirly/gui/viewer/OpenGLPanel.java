// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.*;
import jhi.curlywhirly.gui.dialog.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import static javax.media.opengl.GL2.*;

// TODO: Check what exactly needs to be done with the animators at various points
public class OpenGLPanel extends GLJPanel implements GLEventListener
{
	public static int CANVAS_WIDTH = 800;
	public static int CANVAS_HEIGHT = 600;

	// The animator which updates the display at the desired framerate
	private FPSAnimator animator;

	private GLU glu;

	private Scene scene;

	private CanvasMouseListener mouseListener;

	// Collision detection variables
	private Point mousePoint = new Point(0, 0);
	private CollisionDetection detector;

	private final CloseOverlay closeOverlay;
	private final MultiSelectionRenderer multiSelectionRenderer;
	private final SelectedSphereRenderer sphereRenderer;
	private AbstractSphereRenderer deselectedSphereRenderer;
	private final AxesRenderer axesRenderer;
	private final MovieCaptureEventListener movieCapture;

	private DataPoint underMouse = null;
	private DataSet dataSet;

	private WinMain winMain;

	private Timer fpsTimer;

	public OpenGLPanel(WinMain winMain, GLCapabilities caps)
	{
		super(caps);

		this.winMain = winMain;

		closeOverlay = new CloseOverlay(winMain);
		multiSelectionRenderer = new MultiSelectionRenderer(winMain);
		axesRenderer = new AxesRenderer();
		sphereRenderer = new SelectedSphereRenderer();

		if (Prefs.guiDeselectedRenderer == Prefs.guiDeselectedGrey)
			deselectedSphereRenderer = new DeselectedSphereRendererGrey();
		else if (Prefs.guiDeselectedRenderer == Prefs.guiDeselectedTransparent)
			deselectedSphereRenderer = new DeselectedSphereRendererTransparent();
		else
			deselectedSphereRenderer = new NullSphereRenderer();

		movieCapture = new MovieCaptureEventListener(this);

		addGLEventListener(this);

		createKeyboardShortcuts();

		ToolTipManager.sharedInstance().setInitialDelay(0);
		setLayout(new BorderLayout());
	}

	public void setDataSet(DataSet dataSet)
	{
		removeGLListeners();

		this.dataSet = dataSet;
		Rotation rotation = new Rotation();
		detector = new CollisionDetection();
		int perspectiveAngle = 45;
		scene = new Scene(rotation, perspectiveAngle, (float) CANVAS_WIDTH / CANVAS_HEIGHT);
		if (dataSet == null)
		{
			this.removeMouseListener(mouseListener);
			this.removeMouseMotionListener(mouseListener);
			this.removeMouseWheelListener(mouseListener);
		}
		else
		{
			mouseListener = new CanvasMouseListener(this, rotation, dataSet, winMain);
		}

		multiSelectionRenderer.setDataSet(dataSet, rotation, detector);
		axesRenderer.setDataSet(dataSet, rotation);
		deselectedSphereRenderer.setDataSet(dataSet, rotation, detector);
		sphereRenderer.setDataSet(dataSet, rotation, detector);

		addGLListeners();

		scene.reset();
	}

	private void removeGLListeners()
	{
		removeGLEventListener(axesRenderer);
		removeGLEventListener(sphereRenderer);
		removeGLEventListener(deselectedSphereRenderer);
		removeGLEventListener(multiSelectionRenderer);
		removeGLEventListener(closeOverlay);
		removeGLEventListener(movieCapture);
	}

	private void addGLListeners()
	{
		addGLEventListener(axesRenderer);
		addGLEventListener(sphereRenderer);
		addGLEventListener(deselectedSphereRenderer);
		addGLEventListener(multiSelectionRenderer);
		addGLEventListener(closeOverlay);
		addGLEventListener(movieCapture);
	}

	// Starts animation, this should be called when you want rendering to start
	public void startAnimator()
	{
		if (animator != null)
			animator.remove(this);

		animator = new FPSAnimator(30);
		animator.add(this);
		animator.setUpdateFPSFrames(10, null);
		animator.setPrintExceptions(true);
		animator.start();

		setupFpsTimer();
	}

	// Stop the animator in situations where you need the display to pause
	// updating and to prevent unpredictable animation behaviour
	public void stopAnimator()
	{
		if (animator != null)
		{
			animator.stop();
			animator.pause();
			fpsTimer.stop();
		}
	}

	private void setupFpsTimer()
	{
		fpsTimer = new Timer(2500, e -> winMain.updateStatusBarFps((int) animator.getLastFPS()));
		fpsTimer.setInitialDelay(0);
		fpsTimer.start();
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
		gl.glLineWidth(1.5f);
		gl.setSwapInterval(1);
		gl.glEnable(GL_NORMALIZE);
		gl.glEnable(GL_CULL_FACE);

		scene.init(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		// Don't try to render when the animator is paused
		if (animator.isPaused())
			return;

		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();
		scene.render(gl);
		drawTooltip(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Get the graphics context
		GL2 gl = drawable.getGL().getGL2();

		height = height == 0 ? 1 : height;
		float aspect = (float) width / height;

		scene.setAspect(aspect);

		CANVAS_WIDTH = width;
		CANVAS_HEIGHT = height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Setup perspective projection, with aspect ratio matching viewport
		scene.setPerspective(gl);

		// Enable the model-view transform
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		mouseListener.initialiseArcBall(CANVAS_WIDTH, CANVAS_HEIGHT);
		animator.start();
	}

	// Uses GLPanel's method of getting a screenshot on the EDT, other methods
	// of getting screenshots could clutter up the rendering code.
	public BufferedImage getScreenShot()
	{
		// Create an image of the correct proportions
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		// "Print" the current state of the panel to that image
		setupPrint(1, 1, 1, getWidth(), getHeight());
		printAll(g);
		image.flush();
		// Called to release the panel from the EDT.
		releasePrint();

		return image;
	}

	// Rather than passing a boolean into getScreenshot, we can have two separate methods, with getScreenShotWithKey()
	// calling the original getScreenshot() then drawing the colour key over the top of it.
	public BufferedImage getScreenShotWithKey()
	{
		BufferedImage image = getScreenShot();
		Graphics2D g = image.createGraphics();

		winMain.getColourKeyCreator().drawColorKey(dataSet.getCurrentCategoryGroup(), g);

		return image;
	}

	// Screenshot capture for exporting movies, not screenshots (which utilise
	// the JOGL EDT Screenshot method)
	public BufferedImage snapshot(GL gl, AWTGLReadBufferUtil readBufferUtil, File imageDir)
	{
		gl.glFinish();

		// Output the colour key on top of the movie's frames
		BufferedImage image = readBufferUtil.readPixelsToBufferedImage(gl, true);
		Graphics2D g = image.createGraphics();

		if (Prefs.guiMovieChkColourKey)
			winMain.getColourKeyCreator().drawColorKey(dataSet.getCurrentCategoryGroup(), g);

		return image;
	}

	public void setMousePoint(Point point)
	{
		this.mousePoint = point;
	}

	private Ray getRay(GL2 gl)
	{
		int[] view = new int[4];
		float[] model = new float[16];

		float[] proj = scene.getProj();

		gl.glGetIntegerv(GL.GL_VIEWPORT, view, 0);
		// Get the current model view matrix
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, model, 0);
		float winX = mousePoint.x;
		// Adjust into opengl y space
		float winY = view[3] - (float) mousePoint.y;

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
		Vector3f dir = new Vector3f();
		dir.sub(fVec, nVec);
		Vector3f eye = new Vector3f(0, 0, 4);

		Ray ray = new Ray(dir, eye);

		return ray;
	}

	private void drawTooltip(GL2 gl)
	{
		// Mouse over code looking for sphere's under the mouse
		Ray ray = getRay(gl);
		underMouse = detector.findSphereRayIntersection(ray, sphereRenderer.getPointSize());

		// If we have found a spehre, display this sphere's name in a tooltip
		String text = underMouse != null ? underMouse.getName() : null;
		setToolTipText(text);
	}

	public void visitUrl()
		throws UnsupportedEncodingException
	{
		if (underMouse != null)
			dataSet.getDbAssociation().visitUrlForPoint(underMouse.getName());
	}

	public void selectPoint()
	{
		if (underMouse != null && multiSelectionRenderer.isMultiSelecting() == false)
		{
			underMouse.toggleSelection();
			winMain.getControlPanel().repaint();
		}
	}

	private void createKeyboardShortcuts()
	{
		int menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		// Zoom-in
		Action zoomIn = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				scene.zoom(-1);
			}
		};
		mapKeyToAction(zoomIn, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, menuShortcut), "zoomInMain");
		mapKeyToAction(zoomIn, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, menuShortcut), "zoomInNumPad");

		// Zoom-out
		Action zoomOut = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				scene.zoom(1);
			}
		};
		mapKeyToAction(zoomOut, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, menuShortcut), "zoomOutMain");
		mapKeyToAction(zoomOut, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, menuShortcut), "zoomOutNumPad");
	}

	private void mapKeyToAction(Action action, KeyStroke keyStroke, String command)
	{
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			keyStroke, command);
		getActionMap().put(command, action);
	}

	public void setDeselectedSphereRenderer(AbstractSphereRenderer deselectedSphereRenderer)
	{
		removeGLEventListener(this.deselectedSphereRenderer);

		float pointSize = this.deselectedSphereRenderer.getPointSize();

		this.deselectedSphereRenderer = deselectedSphereRenderer;
		this.deselectedSphereRenderer.setPointSize(pointSize);
		this.deselectedSphereRenderer.setDataSet(dataSet, sphereRenderer.getRotation(), detector);

		addGLEventListener(this.deselectedSphereRenderer);
	}

	public CloseOverlay getCloseOverlay()
	{
		return closeOverlay;
	}

	public MultiSelectionRenderer getMultiSelectionRenderer()
	{
		return multiSelectionRenderer;
	}

	public MovieCaptureEventListener getMovieCapture()
	{
		return movieCapture;
	}

	public SelectedSphereRenderer getSphereRenderer()
	{
		return sphereRenderer;
	}

	public AbstractSphereRenderer getDeselectedSphereRenderer()
	{
		return deselectedSphereRenderer;
	}

	public AxesRenderer getAxesRenderer()
	{
		return axesRenderer;
	}

	public Scene getScene()
	{
		return scene;
	}

	public void showDataPointDialog(DataPoint point)
	{
		new DataPointInformationDialog(dataSet, point);
	}

	public DataPoint getUnderMouse()
	{
		return underMouse;
	}
}