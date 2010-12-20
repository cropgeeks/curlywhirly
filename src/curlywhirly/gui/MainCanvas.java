package curlywhirly.gui;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.media.j3d.*;
import javax.swing.*;
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
	public float viewPointZCoord = 4;
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
	
	// the length of the coordinate system axes
	float axisLength = 1.0f ;
	
	// the size of the spheres to be used
	float sphereSize = axisLength / 100;
	
	// the background for the canvas
	Background background;
	
	// arrays that hold the sphere (point) objects and the corresponding category strings
	ArrayList<DataSphere> allDataSpheres = null;
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
	
	//allows us to click on a data point and open an annotation URL in a web browser
	MouseClickBehavior mouseClickBehavior;
	
	//the default background colour for the canvas
	Color3f bgColour = new Color3f(Color.BLACK);
	
	//the categorization scheme we are currently using
	public ClassificationScheme currentClassificationScheme;
	
	//the font colour used for the "Open file" label
	public Color openFileLabelColour = Color.DARK_GRAY;
	
	//a String we draw over a data point when we have moused over it
	public String mouseOverString = null;
	//the x and y coords for the current mouse over event
	public int mouseOverX, mouseOverY;
	//this boolean keeps tab of whether anything is being moused over or not
	public boolean isMouseOver = false;
	
	
	// ==================================c'tor=============================
	
	public MainCanvas(CurlyWhirly frame)
	{
		super(getGraphicsConfig());
		this.frame = frame;
		su = new SimpleUniverse(this);
		setBackground(CurlyWhirly.controlPanel.getBackground());
		
		//this is for detecting key events
		addKeyListener(new CanvasKeyListener(this));
		setFocusable(true);
		
	}

	
	// ---------------------------------------------------------------------------------------------------------------------
	
	public Dimension getPreferredSize()
	{
		return new Dimension(0,0);
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(0,0);
	}
	
	
	
	//colour the spheres by category
	public void colourSpheres()
	{
		//find out which category scheme is getting used at the moment and retrieve the appropriate value from the data entry object
		int categorySchemeIndex =  CurlyWhirly.dataSet.classificationSchemes.indexOf(currentClassificationScheme);	
		
		//		System.out.println("dataSet = " + CurlyWhirly.dataSet.name);
		//		System.out.println("currentClassificationScheme = " + currentClassificationScheme.name);
		//		System.out.println("categorySchemeIndex = " + categorySchemeIndex);
		
		try
		{
			// default colour to flag colour related problems
			Color3f colour = new Color3f(Color.RED);
			
			// for all spheres
			for (DataSphere dataSphere : allDataSpheres)
			{
				Shape3D shape3d = dataSphere.getShape();
				Material mat = new Material();
				
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
		viewPointZCoord = 4;
		
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
				//clicking with URL opening in web browser
				mouseClickBehavior = new MouseClickBehavior(frame, spheresMap, objRoot, sphereSize);
				mouseClickBehavior.setSchedulingBounds(bounds);
				objRoot.addChild(mouseClickBehavior);
				
				objRoot.setCapability(BranchGroup.ALLOW_DETACH);
				
				// Let Java 3D perform optimizations on this scene graph.
				objRoot.compile();
				
				// add this to the universe
				su.addBranchGraph(objRoot);
				
				setViewPoint();
			}
			else
			{
				stopRenderer();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	// this allows us to set the camera view point
	public void setViewPoint()
	{
		// su.getViewingPlatform().setNominalViewingTransform();
		Vector3f translate = new Vector3f();
		Transform3D t3d = new Transform3D();
		ViewingPlatform viewingPlatform = su.getViewingPlatform();
		TransformGroup vpTrans = viewingPlatform.getViewPlatformTransform();
		initialViewPoint = new Point3f(0, 0, viewPointZCoord);
		translate.set(initialViewPoint);
		t3d.rotX(Math.toRadians(viewingAngle));
		t3d.setTranslation(translate);
		vpTrans.setTransform(t3d);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	//restores the initial zoom factor and viewing angle
	public void resetOriginalView()
	{
		Transform3D trans = new Transform3D();
		wholeObj.setTransform(trans);
		trans.set(new Vector3f(0.0f, 0.0f, 0.0f));
		wholeObj.setTransform(trans);
		setViewPoint();
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
	public void updateGraph(boolean makeNewSpheres)
	{
//		System.out.println("updateGraph");
		
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
		
		if(makeNewSpheres)
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
		TransformGroup xLabelTG = Label.getLabel(CurlyWhirly.dataSet.dataHeaders.get(currentXIndex), labelBgColour,
						labelFontColour, new Vector3f(axisLength + labelSpacer, 0, 0), true);
		allLabelsBG.addChild(xLabelTG);
		
		// y
		TransformGroup yLabelTG = Label.getLabel(CurlyWhirly.dataSet.dataHeaders.get(currentYIndex), labelBgColour,
						labelFontColour, new Vector3f(0, axisLength + labelSpacer, 0), true);
		allLabelsBG.addChild(yLabelTG);
		
		// z
		TransformGroup zLabelTG = Label.getLabel(CurlyWhirly.dataSet.dataHeaders.get(currentZIndex), labelBgColour,
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
		
		allDataSpheres = new ArrayList<DataSphere>();
		
		// this group takes all the sphere objects
		allSpheresBG = new BranchGroup();
		allSpheresBG.setCapability(BranchGroup.ALLOW_DETACH);
		spheresMap = new HashMap<Sphere, String>();
		
		// make up the spheres that represent the data points
		Vector3f vec = new Vector3f();
		Transform3D translate = new Transform3D();
		
		// for each entry in the dataset
		for (DataEntry dataEntry : CurlyWhirly.dataSet.dataEntries)
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
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//if we don't have data loaded we just want to display a grey background and a label prompting the user to open a file	
		//label stuff
		Font font = (new Font("SANS_SERIF", Font.PLAIN, 18));	
		g2.setFont(font);
		FontMetrics fm = getFontMetrics(font);
		String label = "Open a data file to start";
		int stringWidth = fm.stringWidth(label);
		int x = (getWidth()/2) - (stringWidth/2);
		int y = getHeight()/2;	
		
		if(!frame.dataLoaded)
		{			
			g2.setColor(openFileLabelColour);						
			g2.drawString(label, x, y);
		}
		
	}
	
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	// Overriding repaint makes the worst flickering dissapear when we execute the postRender stuff on repaint
	public void repaint()
	{
		Graphics2D g = (Graphics2D) getGraphics();
		paint(g);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	@Override
	public void postRender()
	{
		//do we need to display a mouseover string for a data point
		if(mouseOverString !=null && Preferences.showMouseOverLabels)
		{		
			J3DGraphics2D g2 = this.getGraphics2D();
			
			//font stuff
			int fontHeight = 12;
			Font font = (new Font("SANS_SERIF", Font.PLAIN, fontHeight));	
			g2.setFont(font);
			FontMetrics fm = getFontMetrics(font);
			int stringWidth = fm.stringWidth(mouseOverString);
			
			//draw a rounded rectangle as a background for the label
			float arcSize = fontHeight / 1.5f;
			int horizontalGap = 3;
			int verticalGap = 4;
			RoundRectangle2D.Float backGroundRect = new RoundRectangle2D.Float(mouseOverX - horizontalGap, mouseOverY - fontHeight, stringWidth + horizontalGap * 2, fontHeight + verticalGap, arcSize, arcSize);
			g2.setColor(new Color(1,0,0, 0.5f));
			g2.fill(backGroundRect);
			
			//draw the label
			g2.setColor(Color.white);
			g2.drawString(mouseOverString,mouseOverX,mouseOverY);
			
			//don't flush or we won't see anything drawn
			g2.flush(false);
		}
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
		
	
	
} // end of class

