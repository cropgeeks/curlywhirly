package graphviewer3d.gui;

import graphviewer3d.data.DataSet;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Vector;

import javax.media.j3d.AlternateAppearance;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickZoomBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 * 
 * A 3D graph viewing Canvas
 */
public class GraphViewer3DCanvas extends Canvas3D
{
	
	// ==================================vars=============================
	
	// variables to adjust manually for now
	int boundsSize = 10;
	int initialZ = 2;
	static boolean antiAlias = true;
	
	// infrastructure
	private SimpleUniverse su = null;
	
	// this is the initial position of the camera/viewer's eye
	// a rotation will be applied to this position to create the correct viewing angle so the user looks down onto the object
	private Point3f initialViewPoint = new Point3f(0, 0, initialZ);
	int viewingAngle = 0;
	
	// the bounds for the scene
	BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), boundsSize);
	
	// this is the root of the object part of the scene
	private BranchGroup objRoot = null;
	
	BranchGroup allSpheresBG;
	
	// this holds all the objects
	private TransformGroup wholeObj = null;
	
	// the dataset with the data to plot
	DataSet dataSet;
	
	// the length of the coordinate system axes
	float axisLength = 0;
	
	// the size of the spheres to be used
	float sphereSize = 0;
	
	// a hashmap containing category names as keys and colors as values
	HashMap<String, Color3f> colourMap;
	
	// the background for the canvas
	Background background;
	
	// arrays that hold the sphere (point) objects and the corresponding category strings
	Shape3D[] allSpheres;
	String[] categories;
	
	// ==================================c'tor=============================
	
	public GraphViewer3DCanvas(DataSet dataSet)
	{
		super(getGraphicsConfig());
		this.dataSet = dataSet;
		calculateSizes();
		su = new SimpleUniverse(this);
		su.addBranchGraph(createSceneGraph());
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	public void colourSpheres(Vector<String> updatableCategories)
	{
		if (updatableCategories != null)
		{
			System.out.println("categories for update:");
			for (String string : updatableCategories)
			{
				System.out.println(string);
			}
		}
		
		Color3f colour = new Color3f(Color.black);
		
		// for all spheres
		for (int i = 0; i < allSpheres.length; i++)
		{
			Material mat = new Material();
			String category = categories[i];
			
			// if there are selected categories
			if (null != updatableCategories)
			{
				// check whether this sphere belongs to any of the selected categories
				for (String string : updatableCategories)
				{
					// if the current category does not match any of the selected ones
					if (!category.equals(string))
					{
						// white out the sphere
						colour = (new Color3f(Color.WHITE));
					}
					else
					{
						// colour it in
						System.out.println("colouring sphere in");
						System.out.println("compared category " + category + ", updatable category  is " + string);
						colour = colourMap.get(category);
						System.out.println("colour for this category = " + colour.toString());
					}
				}
			}
			// no categories selected
			else
			{
				// colour it in
				colour = colourMap.get(category);				
			}
			
			Appearance app = new Appearance();
			mat.setDiffuseColor(colour);
			app.setMaterial(mat);
			app.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
			app.setCapability(AlternateAppearance.ALLOW_SCOPE_READ);
			allSpheres[i].setAppearance(app);
			
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	private void calculateSizes()
	{
		// work out size of corrdinate system needed
		// this depends on the values of the data
		// we want the max value the data can take in any dimension
		// this becomes the length of the axes (with a bit to spare)
		if (dataSet.absoluteMax > Math.abs(dataSet.absoluteMin))
			axisLength = dataSet.absoluteMax;
		else
			axisLength = dataSet.absoluteMin;
		
		// work out sphere size for the plot symbols
		sphereSize = axisLength / 100;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
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
	private BranchGroup createSceneGraph()
	{
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
			addBackground();
			
			// this creates a marker at the system's origin for testing
			// makeOriginMarker();
			
			// this creates an ambient plus a directional light source to provide some shading
			setUpLights();
			
			// draw the coordinate system
			drawCoordinateSystem();
			
			// position the central cylinder and the peripheral ones
			makeSpheres();
			
			// colour them in
			colourSpheres(null);
			
			// add the whole Object to the root
			objRoot.addChild(wholeObj);
			
			// this allows us to set the initial camera view point
			// su.getViewingPlatform().setNominalViewingTransform();
			Vector3f translate = new Vector3f();
			Transform3D T3D = new Transform3D();
			TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
			translate.set(initialViewPoint);
			T3D.rotX(Math.toRadians(viewingAngle));
			T3D.setTranslation(translate);
			vpTrans.setTransform(T3D);
			
			// now add the various behaviours
			
			// rotation
			PickRotateBehavior rotateBehaviour = new PickRotateBehavior(objRoot, this, bounds);
			rotateBehaviour.setTolerance(100);
			objRoot.addChild(rotateBehaviour);
			
			// zooming
			PickZoomBehavior zoomBehaviour = new PickZoomBehavior(objRoot, this, bounds);
			zoomBehaviour.setTolerance(100);
			objRoot.addChild(zoomBehaviour);
			
			// sideways translation
//			PickTranslateBehavior translateBehaviour = new PickTranslateBehavior(objRoot, this, bounds);
//			objRoot.addChild(translateBehaviour);
			
			// Let Java 3D perform optimizations on this scene graph.
			objRoot.compile();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return objRoot;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	/**
	 * Creates the central and peripheral cylinders
	 */
	public void makeSpheres()
	{
		if(allSpheresBG != null)
			allSpheresBG.detach();
		allSpheresBG = new BranchGroup();
		
		// make up the spheres that represent the data points
		Vector3f vec = new Vector3f();
		Transform3D translate = new Transform3D();
		allSpheres = new Shape3D[dataSet.numEntries];
		categories = new String[dataSet.numEntries];
		
		// this maps each category to a colour
		makeColourMappings();
		
		// for each entry in the dataset
		for (int i = 0; i < dataSet.numEntries; i++)
		{
			// get x, y and z coords for this data point
			float x, y, z;
			float[] xData = dataSet.data.get(dataSet.currentXIndex);
			float[] yData = dataSet.data.get(dataSet.currentYIndex);
			float[] zData = dataSet.data.get(dataSet.currentZIndex);
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
			wholeObj.addChild(sphereTG);
		}
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	private void makeColourMappings()
	{
		// get a vector with all discrete categories in the dataset
		Vector<String> categories = dataSet.getCategories();
		
		// get a colour array with as many colours as we have categories
		Color3f[] colours = GUIUtils.generateColours(categories.size());
		System.out.println("num colours = " + colours.length);
		System.out.println("num categories = " + categories.size());
		
		// now add things into a map
		colourMap = new HashMap<String, Color3f>();
		int i = 0;
		for (String category : categories)
		{
			colourMap.put(category, colours[i]);
			i++;
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	private void drawCoordinateSystem()
	{
		LineArray coordinateAxes = new LineArray(12, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		
		Color3f blue = new Color3f(0, 0, 255);
		Color3f white = new Color3f(255, 255, 255);
		Color3f red = new Color3f(255, 0, 0);
		
		// x positive
		// line start point
		coordinateAxes.setCoordinate(0, new Point3f(0, 0, 0));
		coordinateAxes.setColor(0, blue);
		// line end point
		coordinateAxes.setCoordinate(1, new Point3f(axisLength, 0, 0));
		coordinateAxes.setColor(1, blue);
		
		// y positive
		// line start point
		coordinateAxes.setCoordinate(2, new Point3f(0, 0, 0));
		coordinateAxes.setColor(2, blue);
		// line end point
		coordinateAxes.setCoordinate(3, new Point3f(0, axisLength, 0));
		coordinateAxes.setColor(3, blue);
		
		// z positive
		// line start point
		coordinateAxes.setCoordinate(4, new Point3f(0, 0, 0));
		coordinateAxes.setColor(4, blue);
		// line end point
		coordinateAxes.setCoordinate(5, new Point3f(0, 0, axisLength));
		coordinateAxes.setColor(5, blue);
		
		// x negative
		// line start point
		coordinateAxes.setCoordinate(6, new Point3f(0, 0, 0));
		coordinateAxes.setColor(6, red);
		// line end point
		coordinateAxes.setCoordinate(7, new Point3f(-0.5f, 0, 0));
		coordinateAxes.setColor(7, red);
		
		// y negative
		// line start point
		coordinateAxes.setCoordinate(8, new Point3f(0, 0, 0));
		coordinateAxes.setColor(8, red);
		// line end point
		coordinateAxes.setCoordinate(9, new Point3f(0, -0.5f, 0));
		coordinateAxes.setColor(9, red);
		
		// z negative
		// line start point
		coordinateAxes.setCoordinate(10, new Point3f(0, 0, 0));
		coordinateAxes.setColor(10, red);
		// line end point
		coordinateAxes.setCoordinate(11, new Point3f(0, 0, -0.5f));
		coordinateAxes.setColor(11, red);
		
		Shape3D s3d = new Shape3D(coordinateAxes);
		s3d.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
		wholeObj.addChild(s3d);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	private void makeOriginMarker()
	{
		ColorCube cube = new ColorCube(sphereSize);
		wholeObj.addChild(cube);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	public void addBackground()
	{
		background = new Background(new Color3f(Color.LIGHT_GRAY));
		background.setCapability(Background.ALLOW_COLOR_WRITE);
		background.setApplicationBounds(bounds);
		objRoot.addChild(background);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	public void setBackgroundColour(int bgColour)
	{
		Color3f colour = null;
		switch (bgColour)
		{
			case 0:
				colour = new Color3f(Color.LIGHT_GRAY);
				break;
			case 1:
				colour = new Color3f(Color.DARK_GRAY);
				break;
			case 2:
				colour = new Color3f(Color.BLACK);
				break;
			case 3:
				colour = new Color3f(Color.WHITE);
				break;
		}
		background.setColor(colour);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
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

