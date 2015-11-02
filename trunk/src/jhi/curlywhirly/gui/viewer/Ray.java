// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.vecmath.*;

public class Ray
{
	private final Vector3f direction;
	private final Vector3f origin;

	Ray(Vector3f direction, Vector3f origin)
	{
		this.direction = direction;
		this.origin = origin;

		direction.normalize();
	}

	Vector3f direction()
	{
		return direction;
	}

	Vector3f origin()
	{
		return origin;
	}
}