// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.opengl;

import java.util.*;

public class IcoSphere
{
	private final ArrayList<FaceTriangle> icoFaces;
	private final ArrayList<Float[]> icoVertices;

	private int index;
	private final HashMap<String, Integer> middlePointCache;

	private final ArrayList<Float> vertexIndices;
	private final ArrayList<Integer> faceNormals;

	public IcoSphere(int level)
	{
		index = 0;
		icoVertices = new ArrayList<Float[]>();
		icoFaces = new ArrayList<FaceTriangle>();
		middlePointCache = new HashMap<String, Integer>();
		vertexIndices = new ArrayList<Float>();
		faceNormals = new ArrayList<Integer>();

		setupVertices();
		setupFaces();

		makeIcosphere(level);
	}

	private void setupVertices()
	{
		// Initialize the data for the icosahedron vertices.
		float t = (float)((1 + (Math.sqrt(5)))/2);

		addVertex(new Float[] { -1f, t, 0f });
		addVertex(new Float[] { 1f, t, 0f });
		addVertex(new Float[] { -1f, -t, 0f });
		addVertex(new Float[] { 1f, -t, 0f });

		addVertex(new Float[] { 0f, -1f, t });
		addVertex(new Float[] { 0f, 1f, t });
		addVertex(new Float[] { 0f, -1f, -t });
		addVertex(new Float[] { 0f, 1f, -t });

		addVertex(new Float[] { t, 0f, -1f });
		addVertex(new Float[] { t, 0f, 1f });
		addVertex(new Float[] { -t, 0f, -1f });
		addVertex(new Float[] { -t, 0f, 1f });
	}

	// Create the default 20 faces of the icosahedron
	private void setupFaces()
	{
		// 5 faces around point 0
		icoFaces.add(new FaceTriangle(0, 11, 5));
		icoFaces.add(new FaceTriangle(0, 5, 1));
		icoFaces.add(new FaceTriangle(0, 1, 7));
		icoFaces.add(new FaceTriangle(0, 7, 10));
		icoFaces.add(new FaceTriangle(0, 10, 11));

		// 5 adjacent faces
		icoFaces.add(new FaceTriangle(1, 5, 9));
		icoFaces.add(new FaceTriangle(5, 11, 4));
		icoFaces.add(new FaceTriangle(11, 10, 2));
		icoFaces.add(new FaceTriangle(10, 7, 6));
		icoFaces.add(new FaceTriangle(7, 1, 8));

		// 5 faces around point 3
		icoFaces.add(new FaceTriangle(3, 9, 4));
		icoFaces.add(new FaceTriangle(3, 4, 2));
		icoFaces.add(new FaceTriangle(3, 2, 6));
		icoFaces.add(new FaceTriangle(3, 6, 8));
		icoFaces.add(new FaceTriangle(3, 8, 9));

		// 5 adjacent faces
		icoFaces.add(new FaceTriangle(4, 9, 5));
		icoFaces.add(new FaceTriangle(2, 4, 11));
		icoFaces.add(new FaceTriangle(6, 2, 10));
		icoFaces.add(new FaceTriangle(8, 6, 7));
		icoFaces.add(new FaceTriangle(9, 8, 1));
	}

	private void makeIcosphere(int subdivision)
	{
		// Subdivide our faces by the number of times specified
		for (int i=0; i < subdivision; i++)
		{
			ArrayList<FaceTriangle> subdividedFaces = new ArrayList<>();
			for (FaceTriangle face : icoFaces)
			{
				// Subdivide triangle into 4 trianlges
				int a = getMiddlePoint(face.v1, face.v2);
				int b = getMiddlePoint(face.v2, face .v3);
				int c = getMiddlePoint(face.v3, face.v1);

				// Add our subdivided faces
				subdividedFaces.add(new FaceTriangle(face.v1, a, c));
				subdividedFaces.add(new FaceTriangle(face.v2, b, a));
				subdividedFaces.add(new FaceTriangle(face.v3, c, b));
				subdividedFaces.add(new FaceTriangle(a, b, c));
			}
			// Clear the collection and add the subdivided faces, preparing
			// us for potential further subdivision
			icoFaces.clear();
			icoFaces.addAll(subdividedFaces);
		}

		createVertexList();
		createFaceNormalList();
	}

	private void createFaceNormalList()
	{
		for (FaceTriangle face : icoFaces)
		{
			faceNormals.add(face.v1);
			faceNormals.add(face.v2);
			faceNormals.add(face.v3);
		}
	}

	private void createVertexList()
	{
		for (Float[] vertices : icoVertices)
			for (float vertex : vertices)
				vertexIndices.add(vertex);
	}

	private int getMiddlePoint(int p1, int p2)
	{
		int smallerIndex = Math.min(p1, p2);
		int greaterIndex = Math.max(p1, p2);
		String key = smallerIndex + "-" + greaterIndex;

		Integer value = middlePointCache.get(key);
		if (value != null)
			return value;

		Float[] point1 = icoVertices.get(p1);
		Float[] point2 = icoVertices.get(p2);
		// Generate a 3D point that is in the middle of point 1 and 2
		Float[] middle = new Float[] {
				(point1[0] + point2[0]) / 2f,
				(point1[1] + point2[1]) / 2f,
				(point1[2] + point2[2]) / 2f };

		int i = addVertex(middle);
		middlePointCache.put(key, i);
		return i;
	}

	private int addVertex(Float[] vertex)
	{
		normalizeVertex(vertex);
		icoVertices.add(vertex);
		return index++;
	}

	private void normalizeVertex(Float[] v)
	{
		float length = (float)Math.sqrt((v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
		v[0] /= length;
		v[1] /= length;
		v[2] /= length;
	}

	public ArrayList<Float> getVertices()
	{
		return vertexIndices;
	}

	public ArrayList<Integer> getFaceNormals()
	{
		return faceNormals;
	}

	public int vertexCount()
		{ return vertexIndices.size(); }

	public int faceNormalCount()
		{ return faceNormals.size(); }

	class FaceTriangle
	{
		final int v1;
		final int v2;
		final int v3;

		FaceTriangle(int v1, int v2, int v3)
		{
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
	}
}