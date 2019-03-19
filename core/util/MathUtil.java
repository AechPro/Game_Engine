package core.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class MathUtil 
{
	public static int[] lerp(double[] point1, double[] point2, double alpha)
	{
		int[] interperand = new int[2];
		interperand[0] = (int)(((1-alpha)*point1[0] + alpha*point2[0]) + 0.001);
		interperand[1] = (int)(((1-alpha)*point1[1] + alpha*point2[1]) + 0.001);
		return interperand;
	}
	public static int[] lerp(int[] point1, double[] point2, double alpha)
	{
		int[] interperand = new int[2];
		interperand[0] = (int)(((1-alpha)*point1[0] + alpha*point2[0]) + 0.001);
		interperand[1] = (int)(((1-alpha)*point1[1] + alpha*point2[1]) + 0.001);
		return interperand;
	}
	
	public static int[] transform(int[] pt, double[] transformData)
	{
		double[] point = new double[pt.length];
		for(int i=0;i<pt.length;i++)
		{
			point[i] = pt[i];
		}
		double[] transformed = transform(point,transformData);
		int[] ans = new int[pt.length];
		for(int i=0;i<ans.length;i++)
		{
			ans[i] = (int)Math.round(transformed[i]);
		}
		return ans;
	}
	
	/**
	 * 
	 * @param pt point to be transformed
	 * @param transformData transformation data. [0,1] = scaleFrom, [2,3] = translateFrom, [4,5] = scaleTo
	 * [6,7] = transelateTo
	 * @return
	 */
	public static double[] transform(double[] pt, double[] transformData)
	{
		double[] scaleFrom = {transformData[0],transformData[1]};
		double[] translateFrom = {transformData[2],transformData[3]};
		double[] scaleTo = {transformData[4],transformData[5]};
		double[] translateTo = {transformData[6],transformData[7]};
		double[] ans = {pt[0],pt[1]};
		
		vecMulElem(ans,scaleFrom);
		vecAdd(ans,translateFrom);
		vecSub(ans,translateTo);
		vecDiv(ans,scaleTo);
		return ans;
	}
	public static BufferedImage convertRGBAToIndexed(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
		Graphics g = dest.getGraphics();
		g.setColor(new Color(231, 20, 189));

		// fill with a hideous color and make it transparent
		g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
		dest = makeTransparent(dest, 0, 0);

		dest.createGraphics().drawImage(src, 0, 0, null);
		return toCompatibleImage(dest);
	}
	public static BufferedImage toCompatibleImage(BufferedImage image)
	{
	    // obtain the current system graphical settings
	    GraphicsConfiguration gfxConfig = GraphicsEnvironment.
	        getLocalGraphicsEnvironment().getDefaultScreenDevice().
	        getDefaultConfiguration();

	    /*
	     * if image is already compatible and optimized for current system 
	     * settings, simply return it
	     */
	    if (image.getColorModel().equals(gfxConfig.getColorModel()))
	        return image;

	    // image is not optimized, so create a new image that is
	    BufferedImage newImage = gfxConfig.createCompatibleImage(
	            image.getWidth(), image.getHeight(), image.getTransparency());

	    // get the graphics context of the new image to draw the old image on
	    Graphics2D g2d = newImage.createGraphics();

	    // actually draw the image and dispose of context no longer needed
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();

	    // return the new optimized image
	    return newImage; 
	}
	public static BufferedImage makeTransparent(BufferedImage image, int x, int y) {
		ColorModel cm = image.getColorModel();
		if (!(cm instanceof IndexColorModel))
		{
			System.out.println("RETURNING IMAGE "+cm);
			return image; // sorry...
		}
		IndexColorModel icm = (IndexColorModel) cm;
		WritableRaster raster = image.getRaster();
		int pixel = raster.getSample(x, y, 0); // pixel is offset in ICM's palette
		int size = icm.getMapSize();
		byte[] reds = new byte[size];
		byte[] greens = new byte[size];
		byte[] blues = new byte[size];
		icm.getReds(reds);
		icm.getGreens(greens);
		icm.getBlues(blues);
		IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
		return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
	}

	public static void vecSub(double[] a, double[] b)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i] -= b[i];
		}
	}
	public static void vecAdd(double[] a, double[] b)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i] += b[i];
		}
	}
	public static void vecDiv(double[] a, double[] b)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i]/=b[i];
		}
	}
	public static void vecMulElem(double[] a, double[] b)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i]*=b[i];
		}
	}
	public static double vecDot(double[] a, double[] b)
	{
		double ans = 0;
		for(int i=0;i<a.length;i++)
		{
			ans += a[i]*b[i];
		}
		return ans;
	}
	public static double[] vecCross(double[] a, double[] b)
	{
		double[] out = new double[3];
		out[0] = a[1]*b[2] - a[2]*b[1];
		out[1] = a[2]*b[0] - a[0]*b[2];
		out[2] = a[0]*b[1] - a[1]*b[0];
		return out;
	}
	public static void vecMul(double[] a, double b)
	{
		for(int i=0;i<a.length;i++)
		{
			a[i]*=b;
		}
	}
}
