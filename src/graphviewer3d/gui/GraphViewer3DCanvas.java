package graphviewer3d.gui;

import graphviewer3d.data.Category;
import graphviewer3d.data.DataSet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.media.j3d.Alpha;
import javax.media.j3d.AlternateAppearance;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 * A 3D graph viewing Canvas
 */
public class GraphViewer3DCanvas extends Canvas3D
{

	// ==================================vars=============================

	// variables to adjust manually for now
	int boundsSize;
	float initialZ;
	static boolean antiAlias = true;

	// infrastructure
	public SimpleUniverse su = null;

	// this is the initial position of the camera/viewer's eye
	// a rotation will be applied to this position to create the correct viewing angle so the user looks down onto the object
	Point3f initialViewPoint;
	int viewingAngle = 0;

	// the bounds for the scene
	BoundingSphere bounds;

	// this is the root of the object part of the scene
	BranchGroup objRoot = null;

	BranchGroup allSpheresBG;

	// this holds all the objects
	private TransformGroup wholeObj = null;

	// the dataset with the data to plot
	DataSet dataSet;

	// the length of the coordinate system axes
	float axisLength = 0;

	// the size of the spheres to be used
	float sphereSize = 0;

	// the background for the canvas
	Background background;

	// arrays that hold the sphere (point) objects and the corresponding category strings
	Shape3D[] allSpheres;
	String[] categories;

	public Vector<Category> selectorListItems;
	public Object[] selectedObjects;

	// the index of the float array in the above vector which is currently selected for display on the x axis
	public int currentXIndex = 0;
	// the index of the float array in the above vector which is currently selected for display on the y axis
	public int currentYIndex = 1;
	// the index of the float array in the above vector which is currently selected for display on the z axis
	public int currentZIndex = 2;
	// these default to the first three columns of data in the dataset

	// this flag is set to true when we want all data points coloured in
	boolean highlightAllCategories = true;

	// branch group containing all axis labels
	BranchGroup allLabelsBG;

	// branch group for the automatic rotation of the graph
	BranchGroup rotatorGroup;

	// speed at which graph spins automatically
	long spinSpeed = 50000;

	//the alpha for the graph spin animation
	Alpha yRotationAlpha;

	static GraphicsConfigTemplate3D template;

	//a map containing all the spheres as keys and their individual data labels as values
	HashMap<Sphere, String> spheresMap;

	//the parent frame
	GraphViewerFrame frame;

	//this behaviour allows us to mouse over a sphere and detect its value
	MouseOverBehavior mouseOverBehaviour;

	//the default background colour for the canvas
	Color3f bgColour = new Color3f(Color.BLACK);

	// ==================================c'tor=============================

	public GraphViewer3DCanvas(GraphViewerFrame frame)
	{
		super(getGraphicsConfig());
		this.frame = frame;
		su = new SimpleUniverse(this);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//colour the spheres by category
	public void colourSpheres()
	{
		try
		{
			// default colour to flag colour related problems
			Color3f colour = new Color3f(Color.RED);

			// a hashmap containing category names as keys and colors as values
			HashMap<String, Category> categoryMap = dataSet.categoryMap;

			// for all spheres
			for (int i = 0; i < allSpheres.length; i++)
			{
				Material mat = new Material();

				// the name of the current category
				String category = categories[i];

				Category categoryItem = categoryMap.get(category);
				if (categoryItem != null)
				{
					if(categoryItem.highlight || highlightAllCategories)
						colour = categoryItem.colour;
					else
						colour = new Color3f(Color.DARK_GRAY);
				}

				Appearance app = new Appearance();
				mat.setDiffuseColor(colour);
				app.setMaterial(mat);
				app.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
				app.setCapability(AlternateAppearance.ALLOW_SCOPE_READ);
				allSpheres[i].setAppearance(app);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//works out relative sizes required for the system
	private void calculateSizes()
	{
		//hard code this for now -- seems to be ok at a fixed value
		axisLength = 1.0f;

		// work out sphere size for the plot symbols
		sphereSize = axisLength / 100;

		// these can be hard coded because we have scaled all the data to be displayed
		boundsSize = 100;
		initialZ = 4;

	}

	// ---------------------------------------------------------------------------------------------------------------------

	//sets up ambient and directional light
	private void setUpLights()
	{

		// Set up the global, ambient light
		Color3f alColor = new Color3f(1.0f, 1.0f, 1.0f);
		AmbientLight aLgt = new AmbientLight(alColor);
		aLgt.setInfluencingBounds(bounds);
		objRoot.addChild(aLgt);

		// Set up the directional (infinite) light source
		Color3f lColor1 = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f lDir1 = new Vector3f(0.0f, 0.0f, -20.0f);
		DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
		lgt1.setInfluencingBounds(bounds);
		objRoot.addChild(lgt1);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a scene graph and returns it in the shape of a single BranchGroup object
	 */
	public void createSceneGraph()
	{

		template.setSceneAntialiasing(GraphicsConfigTemplate3D.REQUIRED);

		objRoot = new BranchGroup();
		wholeObj = new TransformGroup();

		// set the appropriate capabilities
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objRoot.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		wholeObj.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		wholeObj.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		wholeObj.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

		try
		{
			calculateSizes();

			bounds = new BoundingSphere(new Point3d(0, 0, 0), boundsSize);

			addBackground();

			// this creates an ambient plus a directional light source to provide some shading
			setUpLights();

			// draw the coordinate system
			drawCoordinateSystem();

			// place labels on the axes
			makeAxisLabels();

			// position the central cylinder and the peripheral ones
			makeSpheres();

			// colour them in
			colourSpheres();

			// add the whole Object to the root
			objRoot.addChild(wholeObj);

			// now add behaviours
			// rotation
			PickRotateBehavior rotateBehaviour = new PickRotateBehavior(objRoot, this, bounds);
			rotateBehaviour.setTolerance(50);
			objRoot.addChild(rotateBehaviour);
			// zooming
			PickZoomBehavior zoomBehaviour = new PickZoomBehavior(objRoot, this, bounds);
			zoomBehaviour.setTolerance(50);
			objRoot.addChild(zoomBehaviour);
			// selective highlighting
			mouseOverBehaviour = new MouseOverBehavior(frame, spheresMap, objRoot, sphereSize);
			mouseOverBehaviour.setSchedulingBounds(bounds);
			objRoot.addChild(mouseOverBehaviour);

			objRoot.setCapability(BranchGroup.ALLOW_DETACH);

			// Let Java 3D perform optimizations on this scene graph.
			objRoot.compile();

			// add this to the universe
			su.addBranchGraph(objRoot);

			setInitialViewPoint();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

	}

	// ---------------------------------------------------------------------------------------------------------------------

	// this allows us to set the initial camera view point
	public void setInitialViewPoint()
	{
		// su.getViewingPlatform().setNominalViewingTransform();
		Vector3f translate = new Vector3f();
		Transform3D T3D = new Transform3D();
		ViewingPlatform viewingPlatform = su.getViewingPlatform();
		TransformGroup vpTrans = viewingPlatform.getViewPlatformTransform();
		initialViewPoint = new Point3f(0, 0, initialZ);
		translate.set(initialViewPoint);
		T3D.rotX(Math.toRadians(viewingAngle));
		T3D.setTranslation(translate);
		vpTrans.setTransform(T3D);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//restores the initial zoom factor and viewing angle
	public void resetOriginalView()
	{
		Transform3D trans = new Transform3D();
		wholeObj.setTransform(trans);
		trans.set(new Vector3f(0.0f, 0.0f, 0.0f));
		wholeObj.setTransform(trans);
		setInitialViewPoint();
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//puts the graph into an indefinite spin
	public void spin()
	{
		// Create a new Behavior object that will perform the
		// desired operation on the specified transform and add
		// it into the scene graph.

		// rotate about the y axis
		Transform3D yAxis = new Transform3D();
		// yAxis.rotZ(45);
		yRotationAlpha = new Alpha(-1, spinSpeed);
		RotationInterpolator yRotator = new RotationInterpolator(yRotationAlpha, wholeObj, yAxis, 0.0f, (float) Math.PI * 2.0f);
		yRotator.setSchedulingBounds(bounds);
		rotatorGroup = new BranchGroup();
		rotatorGroup.addChild(yRotator);

		rotatorGroup.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.addChild(rotatorGroup);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//stop the graph from spinning
	public void stopSpinning()
	{
		rotatorGroup.detach();
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//sets the spin speed
	public void setSpinSpeed(long speed)
	{
		// subtract this from 100 because it is the wrong way round otherwise
		speed = (100 - speed);
		// can't have a value of 0 so set it to at least 1
		if (speed == 0)
			speed = 1;
		if (yRotationAlpha != null)
			yRotationAlpha.setIncreasingAlphaDuration(speed * 1000);
		spinSpeed = speed * 1000;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//get rid of the current scene graph
	public void clearCurrentView()
	{
		if (objRoot != null)
			objRoot.detach();
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//update the current scene graph with new settings
	public void updateGraph()
	{
		List selectedCategories = null;
		if (selectedObjects != null && selectedObjects.length > 0)
		{
			selectedCategories = Arrays.asList(selectedObjects);
		}

		// for each category
		for (Category category : dataSet.categoryMap.values())
		{
			if (selectedObjects != null && selectedObjects.length > 0)
			{
				// if it is contained in the selected items
				if (selectedCategories.contains(category))
				{
					// set its highlight flag to true
					category.highlight = true;
				}
				else
				{
					category.highlight = false;
				}
			}
			else
			{
				category.highlight = false;
			}
		}

		makeSpheres();
		colourSpheres();
		makeAxisLabels();
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//sticks a label on each of the 3 axes
	private void makeAxisLabels()
	{
		if (allLabelsBG != null)
			allLabelsBG.detach();

		allLabelsBG = new BranchGroup();
		allLabelsBG.setCapability(BranchGroup.ALLOW_DETACH);
		allLabelsBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		allLabelsBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		// colours for the axis labels
		Color labelFontColour = Color.BLACK;
		Color labelBgColour = Color.LIGHT_GRAY;

		// amount by which we want to move the label away from the axis end point
		float labelSpacer = axisLength * 0.10f;

		// x
		TransformGroup xLabelTG = Label.getLabel(dataSet.dataHeaders.get(currentXIndex), labelBgColour,
						labelFontColour, new Vector3f(axisLength + labelSpacer, 0, 0), true);
		allLabelsBG.addChild(xLabelTG);

		// y
		TransformGroup yLabelTG = Label.getLabel(dataSet.dataHeaders.get(currentYIndex), labelBgColour,
						labelFontColour, new Vector3f(0, axisLength + labelSpacer, 0), true);
		allLabelsBG.addChild(yLabelTG);

		// z
		TransformGroup zLabelTG = Label.getLabel(dataSet.dataHeaders.get(currentZIndex), labelBgColour,
						labelFontColour, new Vector3f(0, 0, axisLength + labelSpacer), true);
		allLabelsBG.addChild(zLabelTG);

		wholeObj.addChild(allLabelsBG);

	}

	// ---------------------------------------------------------------------------------------------------------------------
	/**
	 *sets up all the sphere (data point) objects
	 */
	public void makeSpheres()
	{
		if (allSpheresBG != null)
			allSpheresBG.detach();

		// this group takes all the sphere objects
		allSpheresBG = new BranchGroup();
		allSpheresBG.setCapability(BranchGroup.ALLOW_DETACH);
		spheresMap = new HashMap<Sphere, String>();

		// make up the spheres that represent the data points
		Vector3f vec = new Vector3f();
		Transform3D translate = new Transform3D();
		allSpheres = new Shape3D[dataSet.numEntries];
		categories = new String[dataSet.numEntries];

		// for each entry in the dataset
		for (int i = 0; i < dataSet.numEntries; i++)
		{
			// get x, y and z coords for this data point
			float x, y, z;
			float[] xData = dataSet.data.get(currentXIndex);
			float[] yData = dataSet.data.get(currentYIndex);
			float[] zData = dataSet.data.get(currentZIndex);
			x = xData[i];
			y = yData[i];
			z = zData[i];

			String category = dataSet.groupIds[i];

			// apply this and make the sphere
			vec.set(x, y, z);
			translate.setTranslation(vec);
			TransformGroup sphereTG = new TransformGroup(translate);
			Sphere sphere = new Sphere(sphereSize);
			Shape3D shape = sphere.getShape();
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			// store the sphere shape and the corresponding category in their respective arrays so we can access them easily later
			allSpheres[i] = shape;
			categories[i] = category;
			sphereTG.addChild(sphere);
			allSpheresBG.addChild(sphereTG);
			spheresMap.put(sphere, dataSet.groupLabels[i]);
		}
		wholeObj.addChild(allSpheresBG);

		// need to pass the new map of spheres and labels to the mouse over behaviour class now
		if (mouseOverBehaviour != null)
			mouseOverBehaviour.namesHashT = spheresMap;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//creates pointy tips for the axes
	private void placeCone(Vector3f position, TransformGroup wholeObj, float radius, float height, char axis)
	{
		// appearance
		Appearance ap = new Appearance();
		Color3f col = new Color3f(Color.BLUE);
		ColoringAttributes ca = new ColoringAttributes(col, ColoringAttributes.NICEST);
		ap.setColoringAttributes(ca);

		// Create a transform, a transform group and an object
		Transform3D t3d = new Transform3D();
		if (axis == 'x')
			t3d.rotZ(Math.toRadians(270));
		else
			if (axis == 'z')
				t3d.rotX(Math.toRadians(90));

		TransformGroup tg = new TransformGroup();
		Cone cone = new Cone(radius, height);
		cone.setAppearance(ap);
		// Set the transform to move (translate) the object to that location
		t3d.setTranslation(position);
		// Add the transform to the transform group
		tg.setTransform(t3d);
		// Add the object to the transform group
		tg.addChild(cone);
		// add this to the whole object
		wholeObj.addChild(tg);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//darws the lines that make up the coordinate system
	private void drawCoordinateSystem()
	{
		LineArray coordinateAxes = new LineArray(12, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

		// colours for the axes
		Color3f blue = new Color3f(0, 0, 255);
		Color3f red = new Color3f(255, 0, 0);

		// arrow head params
		float arrowHeadHeight = axisLength / 20;
		float arrowHeadRadius = 0.01f;

		// line start and end points
		float[] lineStart = new float[]
		                              { 0, 0, 0 };
		float[] lineEndX = new float[]
		                             { axisLength, 0, 0 };
		float[] lineEndY = new float[]
		                             { 0, axisLength, 0 };
		float[] lineEndZ = new float[]
		                             { 0, 0, axisLength };

		// x positive axis
		// line start point
		coordinateAxes.setCoordinate(0, new Point3f(lineStart));
		coordinateAxes.setColor(0, blue);
		// line end point
		coordinateAxes.setCoordinate(1, new Point3f(lineEndX));
		coordinateAxes.setColor(1, blue);
		// add cone for arrowhead
		placeCone(new Vector3f(lineEndX), wholeObj, arrowHeadRadius, arrowHeadHeight, 'x');

		// y positive
		// line start point
		coordinateAxes.setCoordinate(2, new Point3f(lineStart));
		coordinateAxes.setColor(2, blue);
		// line end point
		coordinateAxes.setCoordinate(3, new Point3f(lineEndY));
		coordinateAxes.setColor(3, blue);
		// add cone for arrowhead
		placeCone(new Vector3f(lineEndY), wholeObj, arrowHeadRadius, arrowHeadHeight, 'y');

		// z positive
		// line start point
		coordinateAxes.setCoordinate(4, new Point3f(lineStart));
		coordinateAxes.setColor(4, blue);
		// line end point
		coordinateAxes.setCoordinate(5, new Point3f(lineEndZ));
		coordinateAxes.setColor(5, blue);
		// add cone for arrowhead
		placeCone(new Vector3f(lineEndZ), wholeObj, arrowHeadRadius, arrowHeadHeight, 'z');

		// x negative
		// line start point
		coordinateAxes.setCoordinate(6, new Point3f(lineStart));
		coordinateAxes.setColor(6, red);
		// line end point
		coordinateAxes.setCoordinate(7, new Point3f(-axisLength, 0, 0));
		coordinateAxes.setColor(7, red);

		// y negative
		// line start point
		coordinateAxes.setCoordinate(8, new Point3f(lineStart));
		coordinateAxes.setColor(8, red);
		// line end point
		coordinateAxes.setCoordinate(9, new Point3f(0, -axisLength, 0));
		coordinateAxes.setColor(9, red);

		// z negative
		// line start point
		coordinateAxes.setCoordinate(10, new Point3f(0, 0, 0));
		coordinateAxes.setColor(10, red);
		// line end point
		coordinateAxes.setCoordinate(11, new Point3f(0, 0, -axisLength));
		coordinateAxes.setColor(11, red);

		Shape3D s3d = new Shape3D(coordinateAxes);
		s3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);

		wholeObj.addChild(s3d);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//adds a mutable background to the scene
	public void addBackground()
	{
		background = new Background(bgColour);
		background.setCapability(Background.ALLOW_COLOR_WRITE);
		background.setApplicationBounds(bounds);
		objRoot.addChild(background);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//changes the background colour
	public void setBackgroundColour(int newColour)
	{
		switch (newColour)
		{
			case 0:
				bgColour = new Color3f(Color.BLACK);
				break;
			case 1:
				bgColour = new Color3f(Color.DARK_GRAY);
				break;
			case 2:
				bgColour = new Color3f(Color.LIGHT_GRAY);
				break;
			case 3:
				bgColour = new Color3f(Color.WHITE);
				break;
		}
		frame.canvasPanel.setBackground(bgColour.get());
		if (background != null)
			background.setColor(bgColour);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		template = new GraphicsConfigTemplate3D();
		if (antiAlias)
		{
			template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
		}
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
						template);
		return gcfg;
	}

	// ---------------------------------------------------------------------------------------------------------------------

} // end of class

