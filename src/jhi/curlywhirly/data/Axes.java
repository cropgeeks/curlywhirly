// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

public class Axes
{
	private final String[] axisLabels;
	private int x;
	private int y;
	private int z;

	public Axes(String[] axisLabels)
	{
		this.axisLabels = axisLabels;

		setDefaultAxes(axisLabels.length);
	}

	// Adjust the default axes displayed based on the number of axes in the
	// dataset. If there are 3 or more, display the first 3, otherwise display
	// as many different axes as is possible. Assumption is made that the first
	// axis is most important as this is where most of the variation should be
	// covered by PCO/PCA data.
	private void setDefaultAxes(int numAxes)
	{
		switch (numAxes)
		{
			case 1:		x = 0;
						y = 0;
						z = 0;
						break;
			case 2:		x = 0;
						y = 0;
						z = 1;
						break;

			default:	x = 0;
						y = 1;
						z = 2;
		}
	}

	public int[] getXYZ()
	{
		return new int[] { x, y, z };
	}

	public String[] getXYZLabels()
	{
		return new String[] { axisLabels[x], axisLabels[y], axisLabels[z] };
	}

	public String[] getAxisLabels()
		{ return axisLabels; }

	public int getX()
		{ return x; }

	public void setX(int x)
		{ this.x = x; }

	public int getY()
		{ return y; }

	public void setY(int y)
		{ this.y = y; }

	public int getZ()
		{ return z; }

	public void setZ(int z)
		{ this.z = z; }
}