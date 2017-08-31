// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import javax.vecmath.*;

public class Rotation
{
	// Rotation variables to allow compound rotations
	private final Matrix4f lastRotation;
	private final Matrix4f currRotation;
	private final Matrix4f autoRotation;

	private final Matrix4f combinedRotation;

	// Variables related automatic rotation of the model
	private boolean autoSpin;
	private float rotationAngle;

	Rotation()
	{
		lastRotation = new Matrix4f();
		currRotation = new Matrix4f();
		autoRotation = new Matrix4f();
		combinedRotation = new Matrix4f();

		autoSpin = false;
		rotationAngle = -0.1f;
	}

	void setIdentity()
	{
		lastRotation.setIdentity();
        currRotation.setIdentity();
		autoRotation.setIdentity();
		combinedRotation.setIdentity();
	}

	void automaticallyRotate()
	{
		if (autoSpin)
            rotateSceneBy(rotationAngle);
	}

    void rotateSceneBy(float angle)
    {
		// Rotate around the Y-axis by angle
		angle = (float) ((angle * Math.PI) / 180);
		autoRotation.set(new AxisAngle4f(0, 1, 0, -angle));
        // When carrying out automatic rotation we need to multiply the
        // automatic rotation by our combined rotation and set our
        // combined rotation to the result of this.
        combinedRotation.mul(autoRotation, combinedRotation);
    }

	// Updates the value to the last user drag rotation.
	public void updateLastRotation()
	{
		lastRotation.set(currRotation);
	}

	// Updates the value of the current user rotation and multiplies it by the
	// previous rotation to attain the difference between these two values. This
	// is the value used to rotate the model.
	public void updateCurrentRotation(Quat4f rotQuat)
	{
		// Convert Quaternion Into Matrix3f
		currRotation.setRotation(rotQuat);
		// Accumulate Last Rotation Into This One
		currRotation.mul(currRotation, lastRotation);
	}

	public void updateCombinedRotation()
	{
		// Multiply the current mouse rotation matrix by the combined matrix
		// and store as the new combined matrix (multiply in this order as
		// our mouse rotations were being applied first while dragging).
		combinedRotation.mul(currRotation, combinedRotation);
		// Reset currRot to prevent mouse clicks rotating the model
		currRotation.setIdentity();
	}

	public float getRotationSpeed()
	{
		return rotationAngle;
	}

	// Sets the speed at which the model automatically rotates
	public void setRotationSpeed(float speed)
	{
		this.rotationAngle = speed;
	}

	public void toggleSpin()
	{
		autoSpin = !autoSpin;
	}

	public boolean isSpinning()
	{
		return autoSpin;
	}

	// Gets the components of the supplied Matrix4f and returns them as a float
	// array.
	private float[] matrix4fToArray(Matrix4f matrix)
	{
		float[] dest = new float[16];

		dest[0] = matrix.m00;
        dest[1] = matrix.m10;
        dest[2] = matrix.m20;
        dest[3] = matrix.m30;
        dest[4] = matrix.m01;
        dest[5] = matrix.m11;
        dest[6] = matrix.m21;
        dest[7] = matrix.m31;
        dest[8] = matrix.m02;
        dest[9] = matrix.m12;
        dest[10] = matrix.m22;
        dest[11] = matrix.m32;
        dest[12] = matrix.m03;
        dest[13] = matrix.m13;
        dest[14] = matrix.m23;
        dest[15] = matrix.m33;

		return dest;
	}

	float[] getDragArray()
	{
		return matrix4fToArray(currRotation);
	}

	float[] getCumulativeRotationArray()
	{
		return matrix4fToArray(combinedRotation);
	}

	// Returns the inverse of the drag rotation array. Allows us to billboard
	// text by apply the opposite rotations to the text that we do to the model.
	float[] getInverseDragArray()
	{
		Matrix4f invCurr = (Matrix4f) currRotation.clone();
		invCurr.invert();

		return matrix4fToArray(invCurr);
	}

	// Returns the inverse of the combined rotation array. Allows us to
	// billboard text by applying the opposite rotations to the text that we do
	// to the model.
	float[] getInverseCumulativeArray()
	{
		Matrix4f invComb = (Matrix4f) combinedRotation.clone();
		invComb.invert();

		return matrix4fToArray(invComb);
	}
}