// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.viewer;

import java.nio.*;
import javax.media.opengl.*;

import com.jogamp.common.nio.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLPointerFunc.*;

public class GLIcoSphere implements Sphere
{
	// An custom made sphere object (faster than gluSphere)
	private IcoSphere sphere;

	private final int sphereDetailLevel;

	// GL 1.5+, VBO ID for vertex data
	private int vertexBufferId;
	// GL 1.5+, VBO ID for face data
	private int indexBufferId;
	// Buffer to hold icosphere vertex data.
	private FloatBuffer vertexBuffer;
	// Buffer to hold faces, represented by 3 indices
	private IntBuffer indexBuffer;

	/**
	 * Constructs a GLIcoSphere using the given opengl context and level of
	 * of detail.
	 *
	 * @param gl					Opengl context that allows opengl operations
	 *								to be carried out
	 * @param sphereDetailLevel		Level of detail for IcoSphere creation.
	 *								Larger number = more detail.
	 */
	public GLIcoSphere(GL gl, int sphereDetailLevel)
	{
		this.sphereDetailLevel = sphereDetailLevel;

		createSphereVertexBuffer(gl);
	}

	private void createSphereVertexBuffer(GL gl)
	{
		// Vertex buffers for our spheres
		int[] bufferID = new int[2];
		gl.glGenBuffers(2, bufferID, 0);
		vertexBufferId = bufferID[0];
		indexBufferId = bufferID[1];

		// A single sphere object to be copied from
		sphere = new IcoSphere(sphereDetailLevel);

		createVertexBuffer();
		createIndexBuffer();
	}

	private void createIndexBuffer()
	{
		indexBuffer = Buffers.newDirectIntBuffer(sphere.faceNormalCount());
		sphere.getFaceNormals().stream().forEachOrdered(n -> indexBuffer.put(n));
		indexBuffer.rewind();
	}

	private void createVertexBuffer()
	{
		vertexBuffer = Buffers.newDirectFloatBuffer(sphere.vertexCount());
		sphere.getVertices().stream().forEachOrdered(v -> vertexBuffer.put(v));
		vertexBuffer.rewind();
	}

	@Override
	public void preRender(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
		gl.glBufferData(GL_ARRAY_BUFFER, getVertexBufferSize(), vertexBuffer, GL_STATIC_DRAW);
		gl.glVertexPointer(3, GL_FLOAT, 0, 0);
		gl.glNormalPointer(GL_FLOAT, 0, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndexBufferSize(), indexBuffer, GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
	}

	@Override
	public void render(GL2 gl)
	{
		// Draw the triangles using the isosphereIndexBuffer VBO for the
		// element data (as well as the isosphereVertexBuffer).
		gl.glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public void postRender(GL2 gl)
	{
		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
	}

	public int getVertexBufferSize()
		{ return sphere.vertexCount() * 4; }

	public int getIndexBufferSize()
		{ return sphere.faceNormalCount() * 4; }

	public int getIndexCount()
		{ return sphere.faceNormalCount(); }
}