package curlywhirly.gui.viewer;

import javax.media.opengl.*;

import com.jogamp.opengl.util.gl2.*;

public class GlutSphere implements Sphere
{
	private GLUT glut;
	private int glutSlices;

	public GlutSphere(int sphereDetailLevel)
	{
		glut = new GLUT();

		switch (sphereDetailLevel)
		{
			case 0: glutSlices = 4;
				break;
			case 1: glutSlices = 6;
				break;
			case 2: glutSlices = 8;
				break;
			case 3: glutSlices = 10;
				break;
			case 4: glutSlices = 12;
		}
	}

	@Override
	public void preRender(GL2 gl)
	{
	}

	@Override
	public void render(GL2 gl)
	{
		glut.glutSolidSphere(1, glutSlices, glutSlices);
	}

	@Override
	public void postRender(GL2 gl)
	{
	}
}