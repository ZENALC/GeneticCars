/*
YOU DO NOT NEED TO MODIFY THIS CLASS

Ball simulates a ball, or a 2D sphere with position, mass, nonzero radius, and velocity.
A ball is the basic movable unit in the physics model.
*/

import java.awt.Color;
import java.awt.Graphics;

public class Ball
{
	double mass,radius;
	Vector velocity,position;
	Color color=null;
	World world;

	public Ball(World w)
	{
		world=w;
		position=new Vector(0,0);
//		mass=(Math.random()*10.0);
		mass=1;
		switch((int)(Math.random()*3))
		{
			case 0:
				color=new Color(0,0,255-(int)(25*mass)); break;
			case 1:
				color=new Color(255-(int)(25*mass),0,0); break;
			default:
				color=new Color(0,255-(int)(25*mass),0); break;
		}
		double speed=Math.random()*5+1;
		double angle=Math.random()*360*2*3.1416/360.0;
		velocity=new Vector(speed*Math.cos(angle),speed*Math.sin(angle));

		radius=w.Radius;
	}

	public void paint(Graphics g)
	{
		g.setColor(color);
		double x=position.x;
		double y=position.y;
		g.fillOval((int)(x -radius), (int)(y-radius), (int)(radius*2),(int)(radius*2));
	}

	public void move()
	{
		position.x+=velocity.x*world.DT;
		position.y+=velocity.y*world.DT;

		double MARGIN=300;

		if(position.x<-MARGIN) position.x=-MARGIN;
		if(position.y<-MARGIN) position.y=-MARGIN;
		if(position.x>world.WIDTH+MARGIN) position.x=world.WIDTH+MARGIN;
		if(position.y>world.HEIGHT+MARGIN) position.y=world.HEIGHT+MARGIN;
	}

	public boolean contains(double px, double py)
	{
		return ((px-position.x)*(px-position.x)+(py-position.y)*(py-position.y)<=radius*radius);
	}
}
