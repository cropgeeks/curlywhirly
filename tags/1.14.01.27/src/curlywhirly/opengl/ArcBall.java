package curlywhirly.opengl;

import java.awt.*;
import javax.vecmath.*;

public class ArcBall
{
	private static final float EPSILON = 1.0e-5f;

	private float width;
	private float height;

	Vector3f clickVec;
	Vector3f dragVec;

	public ArcBall(float width, float height)
	{
		clickVec = new Vector3f();
		dragVec = new Vector3f();
		setBounds(width, height);
	}

	public void mapToSphere(Point point, Vector3f vector)
	{
		// Make method copy of point
		Point2f tempP = new Point2f(point.x, point.y);

		// Map points into the range [-1,1]
		tempP.x = (tempP.x * width) - 1f;
		tempP.y = 1f - (tempP.y * height);

		// Calculate the square of the length of the vector to the point from the centre
		float length = (tempP.x * tempP.x) + (tempP.y * tempP.y);

		// If the point is mapped outside of the sphere... (length > radius squared)
		if (length > 1f)
		{
			// Compute a normalizing factor (radius / sqrt(length))
			float norm = (float) (1.0 / Math.sqrt(length));

			// Return "normalized" vector, a point on the sphere
			vector.x = tempP.x * norm;
			vector.y = tempP.y * norm;
			vector.z = 0;
		}
		// The mouse is on the sphere
		else
		{
			// Return a vector to a point mapped inside the sphere sqrt(radius square - length)
			vector.x = tempP.x;
			vector.y = tempP.y;
			vector.z = (float) Math.sqrt(1f - length);
		}
	}

	public void setBounds(float width, float height)
	{
		this.width = 1f / ((width - 1f) * 0.5f);
		this.height = 1f / ((height - 1f) * 0.5f);
	}

	// Mouse clicked
    public void click(Point point)
	{
        mapToSphere(point, clickVec);
    }

    // Mouse drag, calculate rotation
    public void drag(Point point, Quat4f newRot)
	{
        // Map the point to the sphere
        this.mapToSphere(point, dragVec);

        // Return the quaternion equivalent to the rotation
        if (newRot != null)
		{
			// Compute the vector perpendicular to the begin and end vectors
            Vector3f perp = new Vector3f();
			perp.cross(clickVec, dragVec);

            // Compute the length of the perpendicular vector
            if (perp.length() > EPSILON)
            {
                // We're ok, so return the perpendicular vector as the transform after all
                newRot.x = perp.x;
                newRot.y = perp.y;
                newRot.z = perp.z;
                // In the quaternion values, w is cosine (theta / 2), where theta is rotation angle
                newRot.w = clickVec.dot(dragVec);
            }
			// Perp.length came out as our zero equivalent (epsilon)
			else
            {
                // The begin and end vectors coincide, so return an identity transform
                newRot.x = newRot.y = newRot.z = newRot.w = 0.0f;
            }
        }
    }
}
