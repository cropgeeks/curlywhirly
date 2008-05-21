package graphviewer3d.gui;

import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import javax.media.j3d.*;
import javax.vecmath.*;


import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.pickfast.behaviors.*;
import com.sun.j3d.utils.universe.*;

/**
 * @author Micha Bayer, Scottish Crop Research Institute
 * 
 * A 3D graph viewing Canvas
 */
public class GraphViewer3DCanvas extends Canvas3D
{
	
	// ==================================vars=============================
	
	//variables to adjust manually for now
	int boundsSize = 100;
	int initialZ = 2;
	static boolean antiAlias = true;
	
	// infrastructure
	private SimpleUniverse su = null;
	
	// this is the initial position of the camera/viewer's eye
	//a rotation will be applied to this position to create the correct viewing angle so the user looks down onto the object
	private Point3f initialViewPoint = new Point3f(0,0,initialZ);
	int viewingAngle = 0;
	
	//the bounds for the scene
	BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), boundsSize);
	
	// this is the root of the object part of the scene
	private BranchGroup objRoot = null;
	// this holds all the objects
	private TransformGroup wholeObj = null;
	
	//the dataset with the data to plot
	DataSet dataSet;
	
	//the length of the coordinate system axes
	float axisLength = 0;
	
	//the size of the spheres to be used
	float sphereSize = 0;
	
	//a hashmap containing category names as keys and colors as values
	HashMap<String,Color3f> colourMap;
	
	// ==================================c'tor=============================
	
	public GraphViewer3DCanvas(GraphViewerFrame frame)
	{
		super(getGraphicsConfig());	
		
		//load data
		//TODO : remove hard coding of file path
		String filePath = "E:\\SVNSandbox\\graphViewer3D\\pco_data.txt";		
		DataLoader loader = new DataLoader();		
		dataSet = loader.getDataFromFile(filePath);
		
		calculateSizes();
		
		su = new SimpleUniverse(this);
		su.addBranchGraph(createSceneGraph());
	}
	
	// ---------------------------------------------------------------------------------------------------------------------	
	
	private void calculateSizes()
	{
		//work out size of corrdinate system needed
		//this depends on the values of the data
		//we want the max value the data can take in any dimension
		//this becomes the length of the axes (with a bit to spare)
		if(dataSet.absoluteMax > Math.abs(dataSet.absoluteMin))
			axisLength = dataSet.absoluteMax;
		else
			axisLength = dataSet.absoluteMin;
		
		//work out sphere size for the plot symbols
		sphereSize = axisLength/100;
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
		Vector3f lDir1 = new Vector3f(0.0f,0.0f, -20.0f);
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
			//addWhiteBackground();
			
			//this creates a marker at the system's origin for testing
			//makeOriginMarker();
			
			// this creates an ambient plus a directional light source to provide some shading
			setUpLights();
			
			//draw the coordinate system
			drawCoordinateSystem();
			
			// position the central cylinder and the peripheral ones
			makeSpheres();
			
			// add the whole Object to the root
			objRoot.addChild(wholeObj);
			
			// this allows us to set the initial camera view point
			//su.getViewingPlatform().setNominalViewingTransform();
			Vector3f translate = new Vector3f();
			Transform3D T3D = new Transform3D();
			TransformGroup vpTrans = su.getViewingPlatform().getViewPlatformTransform();
			translate.set(initialViewPoint);
			T3D.rotX(Math.toRadians(viewingAngle));
			T3D.setTranslation(translate);				
			vpTrans.setTransform(T3D);			
			
			// now add the various behaviours
			
			//rotation
			PickRotateBehavior rotateBehaviour = new PickRotateBehavior(objRoot, this, bounds);
			rotateBehaviour.setTolerance(100);
			objRoot.addChild(rotateBehaviour);
			
			// zooming
			PickZoomBehavior zoomBehaviour = new PickZoomBehavior(objRoot, this, bounds);
			zoomBehaviour.setTolerance(100);
			objRoot.addChild(zoomBehaviour);
			
			// sideways translation
			PickTranslateBehavior translateBehaviour = new PickTranslateBehavior(objRoot, this, bounds);
			objRoot.addChild(translateBehaviour);
			
			
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
	private void makeSpheres()
	{

		Color3f colour;

		//make up the spheres that represent the data points
		Vector3f vec = new Vector3f();
		Transform3D translate = new Transform3D();	
		
		//this maps each category to a colour
		makeColourMappings();
		
		//for each entry in the dataset
		for (int i = 0; i < dataSet.numEntries; i++)
		{
			//get x, y and z coords for this data point
			float x, y, z;			
			float [] xData = dataSet.data.get(dataSet.currentXIndex);
			float [] yData = dataSet.data.get(dataSet.currentYIndex);
			float [] zData = dataSet.data.get(dataSet.currentZIndex);
			x = xData[i];
			y = yData[i];
			z = zData[i];
			
			//coloring
			//get new colour
			String category = dataSet.groupIds[i];
			colour = colourMap.get(category);
			Material mat = new Material();
			mat.setDiffuseColor(colour);
			Appearance app = new Appearance();
			app.setMaterial(mat);
			
			//apply this and make the sphere
			vec.set(x,y,z);
			translate.setTranslation(vec);
			TransformGroup sphereTG = new TransformGroup(translate);
			sphereTG.addChild(new Sphere(sphereSize,app));
			wholeObj.addChild(sphereTG);
		}

	}
	
	// ---------------------------------------------------------------------------------------------------------------------	
	
	private void makeColourMappings()
	{
		//get a vector with all discrete categories in the dataset
		Vector<String> categories = dataSet.getCategories();
		
		//get a colour array with as many colours as we have categories
		Color3f [] colours = GUIUtils.generateColours(categories.size());
		System.out.println("num colours = " + colours.length);
		System.out.println("num categories = " + categories.size());
		
		//now add things into a map
		colourMap = new HashMap<String,Color3f>();
		int i = 0;
		for (String category : categories)
		{
			colourMap.put(category, colours[i]);
			i++;
		}
		
//		System.out.println("colour mappings:");
//		for(String category : colourMap.keySet())
//		{
//		System.out.println("category " + category + " = " + colourMap.get(category).toString());
//		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	private void drawCoordinateSystem()
	{
		LineArray coordinateAxes = new LineArray(6, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		
		//x
		//line start point
		coordinateAxes.setCoordinate(0, new Point3f(0,0,0));
		coordinateAxes.setColor(0, new float [] {255,255,255});
		//line end point
		coordinateAxes.setCoordinate(1, new Point3f(axisLength,0,0));
		coordinateAxes.setColor(1, new float [] {255,255,255});
		
		//y
		//line start point
		coordinateAxes.setCoordinate(2, new Point3f(0,0,0));
		coordinateAxes.setColor(2, new float [] {255,255,255});
		//line end point
		coordinateAxes.setCoordinate(3, new Point3f(0,axisLength,0));
		coordinateAxes.setColor(3, new float [] {255,255,255});
		
		//z
		//line start point
		coordinateAxes.setCoordinate(4, new Point3f(0,0,0));
		coordinateAxes.setColor(4, new float [] {255,255,255});
		//line end point
		coordinateAxes.setCoordinate(5, new Point3f(0,0,axisLength));
		coordinateAxes.setColor(5, new float [] {255,255,255});
		
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
	
	public void addWhiteBackground()
	{
		BoundingSphere boundingSphere = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		Background background = new Background(new Color3f(Color.white));
		background.setApplicationBounds(boundingSphere);
		objRoot.addChild(background);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
	// get a nice graphics config
	private static GraphicsConfiguration getGraphicsConfig()
	{
		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		if(antiAlias)
		{
			template.setSceneAntialiasing(GraphicsConfigTemplate3D.PREFERRED);
		}
		GraphicsConfiguration gcfg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
						template);
		return gcfg;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	
} // end of class

