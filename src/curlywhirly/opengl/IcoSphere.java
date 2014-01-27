package curlywhirly.opengl;

import java.nio.*;
import java.util.*;

import com.jogamp.common.nio.*;

public class IcoSphere
{
	private float[][] icoshedronVertices;
	// represented as 3 indices in the vertex array.
	private int[][] icoshedronFaces;

	// Buffer to hold isosphere vertex data.
	private FloatBuffer vertexBuffer;
	// Buffer to hold faces, represented by 3 indices
	private IntBuffer indexBuffer;
	// Number of float values stored in the buffers.
	private int vertexCount;
	private int indexCount;

	public IcoSphere(int level)
	{
		icoshedronVertices = setupVertices();
		icoshedronFaces = setupFaces();
		makeIcosphere(level);
	}

	private float[][] setupVertices()
	{
		// Initialize the data for the icosahedron vertices.
		float t = (float)((Math.sqrt(5) - 1)/2);
		float[][] vertices = new float[][]
		{
			new float[] { -1, -t, 0 },
			new float[] { 0, 1, t },
			new float[] { 0, 1, -t },
			new float[] { 1, t, 0 },
			new float[] { 1, -t, 0 },
			new float[] { 0, -1, -t },
			new float[] { 0, -1, t },
			new float[] { t, 0, 1 },
			new float[] { -t, 0, 1 },
			new float[] { t, 0, -1 },
			new float[] { -t, 0, -1 },
			new float[] { -1, t, 0 },
		};

		// Normalize the vertices to have unit length.
		for (float[] v : vertices)
		{
			float length = (float)Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
			v[0] /= length;
			v[1] /= length;
			v[2] /= length;
		}

		return vertices;
	}

	private int[][] setupFaces()
	{
		int[][] faces = new int[][]
		{
			{ 3, 7, 1 },
			{ 4, 7, 3 },
			{ 6, 7, 4 },
			{ 8, 7, 6 },
			{ 7, 8, 1 },
			{ 9, 4, 3 },
			{ 2, 9, 3 },
			{ 2, 3, 1 },
			{ 11, 2, 1 },
			{ 10, 2, 11 },
			{ 10, 9, 2 },
			{ 9, 5, 4 },
			{ 6, 4, 5 },
			{ 0, 6, 5 },
			{ 0, 11, 8 },
			{ 11, 1, 8 },
			{ 10, 0, 5 },
			{ 10, 5, 9 },
			{ 0, 8, 6 },
			{ 0, 10, 11 },
		};

		return faces;
	}

	private void makeIcosphere(int level)
	{
		ArrayList<Float> vertices = new ArrayList<Float>();
		ArrayList<Integer> faceIndices = new ArrayList<Integer>();

		// Create a linear arraylist of our (normalized) vertices
		for (float[] v : icoshedronVertices)
		{
			vertices.add(v[0]);
			vertices.add(v[1]);
			vertices.add(v[2]);
		}

		// Subdivide each face of the sphere in turn, to the required level of detail
		for (int[] face : icoshedronFaces)
			subdivide(face[0], face[1], face[2], vertices, faceIndices, level);

		vertexCount = vertices.size();
		indexCount = faceIndices.size();

		// Setup vertex and index buffers ready for the OpenGL code
		vertexBuffer = Buffers.newDirectFloatBuffer(vertices.size());
		for (float x : vertices)
			vertexBuffer.put(x);
		vertexBuffer.rewind();

		indexBuffer = Buffers.newDirectIntBuffer(faceIndices.size());
		for (int i : faceIndices)
			indexBuffer.put(i);
		indexBuffer.rewind();
	}

	/**
	* Subdivides a triangular face on the unit sphere and stores the
	* data for all the (sub-)faces that are generated into the list of
	* vertex coordinates and the list of vertex indices for faces.
	* (Note that a given vertex will actually be generated twice, and that
	* no attempt is made to eliminate this redundancy.)
	* @param v1  Index in vertex list of the first vertex of the face.
	* @param v2  Index in vertex list of the second vertex of the face.
	* @param v3  Index in vertex list of the third vertex of the face.
	* @param vertices  The vertex list.
	* @param faces  The list of vertex indices for each face that is generated.
	* @param level  The number of times the face is to be subdivided.
	*/
	private void subdivide(int v1, int v2, int v3, ArrayList<Float> vertices,
						 ArrayList<Integer> faces, int level)
	{
		if (level == 0)
		{
			// For level 0, add the vertex indices for this face to the vertex data.
			faces.add(v1);
			faces.add(v2);
			faces.add(v3);
		}
		else
		{
			// Subdivide the face into 4 triangles, and then subdivide
			// each of those triangles (level-1) times. The new vertices
			// that are generated are placed in the vertex list.  There
			// is a new vertex half-way between each pair of vertices
			// of the original face.
			float a1 = (vertices.get(3*v1) + vertices.get(3*v2));
			float a2 = (vertices.get(3*v1+1) + vertices.get(3*v2+1));
			float a3 = (vertices.get(3*v1+2) + vertices.get(3*v2+2));
			float length = (float)Math.sqrt(a1*a1+a2*a2+a3*a3);
			a1 /= length;
			a2 /= length;
			a3 /= length;
			int indexA = vertices.size()/3;
			vertices.add(a1);
			vertices.add(a2);
			vertices.add(a3);

			float b1 = (vertices.get(3*v3) + vertices.get(3*v2));
			float b2 = (vertices.get(3*v3+1) + vertices.get(3*v2+1));
			float b3 = (vertices.get(3*v3+2) + vertices.get(3*v2+2));
			length = (float)Math.sqrt(b1*b1+b2*b2+b3*b3);
			b1 /= length;
			b2 /= length;
			b3 /= length;
			int indexB = vertices.size()/3;
			vertices.add(b1);
			vertices.add(b2);
			vertices.add(b3);

			float c1 = (vertices.get(3*v1) + vertices.get(3*v3));
			float c2 = (vertices.get(3*v1+1) + vertices.get(3*v3+1));
			float c3 = (vertices.get(3*v1+2) + vertices.get(3*v3+2));
			length = (float)Math.sqrt(c1*c1+c2*c2+c3*c3);
			c1 /= length;
			c2 /= length;
			c3 /= length;
			int indexC = vertices.size()/3;
			vertices.add(c1);
			vertices.add(c2);
			vertices.add(c3);

			subdivide(v1,indexA,indexC,vertices,faces,level-1);
			subdivide(indexA,v2,indexB,vertices,faces,level-1);
			subdivide(indexC,indexB,v3,vertices,faces,level-1);
			subdivide(indexA,indexB,indexC,vertices,faces,level-1);
		}
	}

	public int vertexCount()
		{ return vertexCount; }

	public int indexCount()
		{ return indexCount; }

	public FloatBuffer vertexBuffer()
		{ return vertexBuffer; }

	public IntBuffer indexBuffer()
		{ return indexBuffer; }
}