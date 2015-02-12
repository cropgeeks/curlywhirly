// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.viewer;

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
	void updatePointLocation(float[] modelView, DataPoint point)
	{
		float [] location = new float[] { modelView[12], modelView[13], modelView[14] };

		translatedPoints.put(point, location);
	}

	// Loop over data points trying to find the collision closest to the mouse
	// click (smallest positive value of t returned from intersect
	DataPoint findSphereRayIntersection(Ray ray, float pointSize)
	{
		double tmin = 1000000;

		DataPoint found = null;
		for (DataPoint point : translatedPoints.keySet())
		{
			float t = intersect(ray, translatedPoints.get(point), pointSize);
			if (t > 0 && t < tmin)
			{
				tmin = t;
				found = point;
			}
		}

		return found;
	}

	// Equation to check for collision between a ray and spehre.
	private float intersect(Ray ray, float[] sphere, float pointSize)
	{
		Vector3f origin = ray.origin();
		Vector3f direction = ray.direction();

		Vector3f sphereVec = new Vector3f(sphere);
		Vector3f l = new Vector3f();
		l.sub(origin, sphereVec);

		// Radius can be adjusted to make detection more, or less stringent
		float radiusSquared = pointSize * pointSize;

		float a = direction.dot(direction);
		float b = 2 * direction.dot(l);
		float c = l.dot(l) - radiusSquared;

		return solveQuadratic(a, b, c);
	}

	private float solveQuadratic(float a, float b, float c)
	{
		float t0;
		float t1;

		float discr = (b * b) - (4 * a * c);
		if (discr < 0)
			return -1;
		else
		{
			t0 = (float) ((-b - Math.sqrt(discr)) / (2 * a));
			t1 = (float) ((-b + Math.sqrt(discr)) / (2 * a));
		}

		return Math.min(t0, t1);
	}
}