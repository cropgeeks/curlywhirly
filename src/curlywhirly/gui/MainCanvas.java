package curlywhirly.gui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.pickfast.behaviors.*;
import com.sun.j3d.utils.universe.*;
import curlywhirly.data.*;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 *
 * A 3D graph viewing Canvas
 */
public class MainCanvas extends Canvas3D
{

	// ==================================vars=============================

	// variables to adjust manually for now
	int boundsSize = 1000;
	float initialZ = 4;
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
	float axisLength = 1.0f ;

	// the size of the spheres to be used
	float sphereSize = axisLength / 100;

	// the background for the canvas
	Background background;

	// arrays that hold the sphere (point) objects and the corresponding category strings
	ArrayList<DataSphere> allDataSpheres = new ArrayList<DataSphere>();
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
	public long spinSpeed = 50000;

	//the alpha for the graph spin animation
	Alpha yRotationAlpha;

	//the corresponding interpolator
	public RotationInterpolator yRotator = null;

	//a boolean to show the graph is spinning
	public boolean isGraphSpinning = false;

	static GraphicsConfigTemplate3D template;

	//a map containing all the spheres as keys and their individual data labels as values
	HashMap<Sphere, String> spheresMap;

	//the parent frame
	CurlyWhirly frame;

	//this behaviour allows us to mouse over a sphere and detect its value
	MouseOverBehavior mouseOverBehaviour;

	//the default background colour for the canvas
	Color3f bgColour = new Color3f(Color.BLACK);
	
	//the categorization scheme we are currently using
	public static ClassificationScheme currentClassificationScheme;



	// ==================================c'tor=============================

	public MainCanvas(CurlyWhirly frame)
	{
		super(getGraphicsConfig());
		this.frame = frame;
		su = new SimpleUniverse(this);

		//key listener

		//this is for detecting key events
		addKeyListener(new CanvasKeyListener(this));
		setFocusable(true);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//colour the spheres by category
	public void colourSpheres()
	{
		try
		{
			// default colour to flag colour related problems
			Color3f colour = new Color3f(Color.RED);

			// for all spheres
			for (DataSphere dataSphere : allDataSpheres)
			{
				Shape3D shape3d = dataSphere.getShape();
				Material mat = new Material();
				
				//find out which category scheme is getting used at the moment and retrieve the appropriate value from the data entry object
				int categorySchemeIndex =  dataSet.classificationSchemes.indexOf(currentClassificationScheme);				
				Category category = dataSphere.dataEntry.categories.get(categorySchemeIndex);

				if (category != null)
				{
					if(category.highlight || highlightAllCategories)
						colour = category.colour;
					else
						colour = new Color3f(Color.DARK_GRAY);
				}

				Appearance app = new Appearance();
				mat.setDiffuseColor(colour);
				app.setMaterial(mat);
				app.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
				app.setCapability(AlternateAppearance.ALLOW_SCOPE_READ);
				shape3d.setAppearance(app);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//works out relative sizes required for the system
	public void calculateSizes()
	{
		//hard code this for now -- seems to be ok at a fixed value
		axisLength = 1.0f;

		// work out sphere size for the plot symbols
		sphereSize = axisLength / 100;

		// these can be hard coded because we have scaled all the data to be displayed
		boundsSize = 1000;
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
	public void createSceneGraph(boolean dataLoaded)
	{
		try
		{
			if(dataLoaded)
			{
				if(!isRendererRunning())
					startRenderer();
				
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
				rotateBehaviour.setTolerance(500);
				objRoot.addChild(rotateBehaviour);
				// zooming
				PickZoomBehavior zoomBehaviour = new PickZoomBehavior(objRoot, this, bounds);
				zoomBehaviour.setTolerance(500);
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
			else
			{
				System.out.println("creating scene graph without data");
				stopRenderer();
			}
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
		isGraphSpinning = true;

		// Create a new Behavior object that will perform the
		// desired operation on the specified transform and add
		// it into the scene graph.

		// rotate about the y axis
		Transform3D yAxis = new Transform3D();
		// yAxis.rotZ(45);
		yRotationAlpha = new Alpha(-1, spinSpeed);
		yRotator = new RotationInterpolator(yRotationAlpha, wholeObj, yAxis, 0.0f, (float) Math.PI * 2.0f);
		yRotator.setSchedulingBounds(bounds);
		rotatorGroup = new BranchGroup();
		rotatorGroup.addChild(yRotator);

		rotatorGroup.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.addChild(rotatorGroup);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	public void rotateGraph(float degrees)
	{
		//a transform for rotating the graph about the y axis
		Transform3D yRotationTransform = new Transform3D();
		yRotationTransform.rotY(Math.toRadians(degrees));
		wholeObj.setTransform(yRotationTransform);
	}

	// ---------------------------------------------------------------------------------------------------------------------

	//stop the graph from spinning
	public void stopSpinning()
	{
		isGraphSpinning = false;
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
//		System.out.println("updating graph");
		
		List selectedCategories = null;
		if (selectedObjects != null && selectedObjects.length > 0)
		{
			selectedCategories = Arrays.asList(selectedObjects);
		}

		// for each category
		for (Category category : currentClassificationScheme.categories)
		{
			if (selectedObjects != null && selectedObjects.length > 0)
			{
				// if it is contained in the selected items
				if (selectedCategories.contains(category.name))
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
//		allSpheres = new Shape3D[dataSet.numEntries];
//		categories = new String[dataSet.numEntries];

		// for each entry in the dataset
		for (DataEntry dataEntry : dataSet.dataEntries)
		{
			// get x, y and z coords for this data point
			float x = dataEntry.normalizedDataValues.get(currentXIndex);
			float y = dataEntry.normalizedDataValues.get(currentYIndex);
			float z = dataEntry.normalizedDataValues.get(currentZIndex);

			// apply this and make the sphere
			vec.set(x, y, z);
			translate.setTranslation(vec);
			TransformGroup sphereTG = new TransformGroup(translate);
			
			//get the sphere object for the current datapoint			
			dataEntry.dataSphere = new DataSphere(sphereSize);
			dataEntry.dataSphere.dataEntry = dataEntry;
			Shape3D shape = dataEntry.dataSphere.getShape();
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			
			//add the sphere to the list and to the scene graph
			allDataSpheres.add(dataEntry.dataSphere);
			sphereTG.addChild(dataEntry.dataSphere);
			allSpheresBG.addChild(sphereTG);
			spheresMap.put(dataEntry.dataSphere, dataEntry.label);
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

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		Graphics2D g2 = (Graphics2D)g;

		if(!frame.dataLoaded)
		{
			//if we don't have data loaded we just want to display a grey background and a label prompting the user to open a file
			setBackground(Color.LIGHT_GRAY);
			g2.setColor(Color.white);			
			Font font = (new Font("SANS_SERIF", Font.PLAIN, 18));	
			g2.setFont(font);
			FontMetrics fm = getFontMetrics(font);
			String label = "Open a data file to start";
			int stringWidth = fm.stringWidth(label);
			int x = (getWidth()/2) - (stringWidth/2);
			int y = getHeight()/2;			
			g2.drawString(label, x, y);
		}

	}

	// ---------------------------------------------------------------------------------------------------------------------


} // end of class

