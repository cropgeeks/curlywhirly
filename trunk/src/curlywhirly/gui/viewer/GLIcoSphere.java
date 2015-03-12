package curlywhirly.gui.viewer;

import java.nio.*;
import javax.media.opengl.*;

import com.jogamp.common.nio.*;

public class GLIcoSphere
{
	// An custom made sphere object (faster than gluSphere)
	private IcoSphere sphere;

	private int sphereDetailLevel;

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
		sphere.getFaceNormals().stream().forEach(n -> indexBuffer.put(n));
		indexBuffer.rewind();
	}

	private void createVertexBuffer()
	{
		vertexBuffer = Buffers.newDirectFloatBuffer(sphere.vertexCount());
		sphere.getVertices().stream().forEach(v -> vertexBuffer.put(v));
		vertexBuffer.rewind();
	}

	public int getVertexBufferId()
		{ return vertexBufferId; }

	public int getIndexBufferId()
		{ return indexBufferId; }

	public int getVertexBufferSize()
		{ return sphere.vertexCount() * 4; }

	public int getIndexBufferSize()
		{ return sphere.faceNormalCount() * 4; }

	public int getIndexCount()
		{ return sphere.faceNormalCount(); }

	public FloatBuffer getVertexBuffer()
		{ return vertexBuffer; }

	public IntBuffer getIndexBuffer()
		{ return indexBuffer; }
}
