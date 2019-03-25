// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

/**
 * Stores the current position in 3D space in float variables for x, y and z
 * components. Can query for x, y and z individually, or for all 3 together as
 * a float array.
 */
public class Position3D
{
	private float x;
	private float y;
	private float z;

	public Position3D()
	{
		x = 0;
		y = 0;
		z = 0;
	}

	public float[] getPosition()
	{
		return new float[]{x, y, z};
	}

	// Getters and setters

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getZ()
	{
		return z;
	}

	public void setZ(float z)
	{
		this.z = z;
	}
}