/*
YOU DO NOT NEED TO MODIFY THIS CLASS

This class models a simulated wall, or fixed barrier that balls cannot pass through
*/

import java.awt.Color;
import java.awt.Graphics;

public class Wall
{
	double mass,radius;
	Vector velocity,position;
	Color color=null;
	int x1,x2,y1,y2;

	public Wall(int x1, int y1, int x2, int y2)
	{
		this.x1=x1; this.y1=y1; this.x2=x2; this.y2=y2;
		color=new Color(0,0,0);
	}

	public void paint(Graphics g)
	{
		g.setColor(color);
		g.drawLine(x1,y1,x2,y2);
	}
}
