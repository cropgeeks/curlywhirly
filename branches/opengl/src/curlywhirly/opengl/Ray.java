package curlywhirly.opengl;

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