// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.opengl;

import java.util.*;
import javax.vecmath.*;

import curlywhirly.data.*;

public class CollisionDetection
{
	// Keeps track of the translated locations of points in the display
	// used in ray tracing code to find points under the mouse.
	private final HashMap<DataPoint, float[]> translatedPoints;

	public CollisionDetection()
	{
		translatedPoints = new HashMap<DataPoint, float[]>();
	}

	// Keeps track of the translated positions of all of our DataPoints
	void updatePointLocation(float[] modelView, float[] axes, DataPoint point)
	{
		float [] location = getXYZForMatrix(axes, modelView);

		translatedPoints.put(point, location);
	}

	private float[] getXYZForMatrix(float[] axes, float[] modelView)
	{
		float x = (axes[0] * modelView[0]) +  (axes[1] * modelView[4]) + (axes[2] * modelView[8]) + modelView[12];
		float y = (axes[0] * modelView[1]) +  (axes[1] * modelView[5]) + (axes[2] * modelView[9]) + modelView[13];
		float z = (axes[0] * modelView[2]) +  (axes[1] * modelView[6]) + (axes[2] * modelView[10]) + modelView[14];

		return new float[] { x, y, z };
	}

	DataPoint findSphereRayIntersection(Ray ray, float pointSize)
	{
		float tmin = 1000000;

		DataPoint found = null;
		for (DataPoint point : translatedPoints.keySet())
		{
			float t = intersect(ray, translatedPoints.get(point), tmin, pointSize);
			if (t > 0 && t < tmin)
			{
				tmin = t;
				found = point;
			}
		}

		return found;
	}

	private float intersect(Ray ray, float[] sphere, float max_t, float pointSize)
	{
		float t0, t1;

		Vector3f origin = ray.origin();
		Vector3f direction = ray.direction();

		Vector3f l = new Vector3f(sphere[0]-origin.x, sphere[1]-origin.y, sphere[2]-origin.z);
		float tca = l.dot(direction);
		if (tca < 0)
			return -1;

		float d2 = l.dot(l) - tca * tca;
		// Radius can be adjusted to make detection more, or less stringent
		float radiusSquared = pointSize * pointSize;
		if (d2 > radiusSquared)
			return -1;
		float thc = (float) Math.sqrt(radiusSquared - d2);
		t0 = tca - thc;
		t1 = tca + thc;

		l = new Vector3f(origin.x - sphere[0], origin.y - sphere[1], origin.z - sphere[2]);
		float a = direction.dot(direction);
		float b = 2 * direction.dot(l);
		float c = l.dot(l) - radiusSquared;

		t0 = solveQuadratic(a, b, c, t0, t1);
		if (t0 == -1)
			return -1;

		if (t0 > max_t)
			return -1;
		else
			return t0;
	}

	private float solveQuadratic(float a, float b, float c, float t0, float t1)
	{
		float discr = b * b - 4 * a * c;
		if (discr < 0)
			return -1;
		else if (discr == 0)
			t0 = t1 = (float) (-0.5 * b / a);
		else
		{
			float q	= (float) (b > 0 ? -0.5  * (b + Math.sqrt(discr)) :  -0.5  * (b - Math.sqrt(discr)));
			t0 = q / a;
			t1 = c / q;
		}

		return Math.min(t0, t1);
	}
}