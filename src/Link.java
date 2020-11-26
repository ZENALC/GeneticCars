/*
YOU DO NOT NEED TO MODIFY THIS CLASS

Link models a fixed length connection between two balls.  By default, two balls that are linked must stay a constant distance apart.
*/

import java.awt.Color;
import java.awt.Graphics;

public class Link
{
	Color color=null;
	Ball ball1,ball2;
	boolean isRope;
	double len;

	public Link(Ball b1, Ball b2, boolean isRope)
	{
		ball1=b1; ball2=b2; this.isRope=isRope;
		color=new Color(0,0,0);
		len=b1.position.sub(b2.position).length();
	}

	public double length()
	{
		return len;
	}

	public void paint(Graphics g)
	{
		g.setColor(color);
		g.drawLine((int)ball1.position.x,(int)ball1.position.y,(int)ball2.position.x,(int)ball2.position.y);
	}
}
