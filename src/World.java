/*
YOU DO NOT NEED TO MODIFY THIS CLASS

This class makes a 2d physics simulation consisting of balls, links, walls, friction, and gravity.  Graphics are optional.
*/

import javax.swing.JComponent;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

public class World extends JComponent
{
	public int WIDTH=500,HEIGHT=500;

	public final double Restitution=0.9;
	public final double WallRestitution=0.2;
	public final double gravity=0.5;
	public final double DT=0.1;
	public final double FRICTION=40.0;
	public final double timeunit=1000.0/660.0;

	int BallCount=0;
	int WallCount=1;
	int LinkCount=0;
	int Bound=50;

	int Radius=5;

	long iterations=0;
	boolean graphics=false;

	ArrayList<Ball> balls;
	ArrayList<Wall> walls;
	ArrayList<Link> links;

	public World()
	{
		balls=new ArrayList<Ball>();
		walls=new ArrayList<Wall>();
		links=new ArrayList<Link>();

		iterations=0;
		graphics=false;
	}

	public Ball getBall(int i)
	{
		return balls.get(i);
	}

	public void handleWallCollision(Ball ball, Wall wall)
	{
		double A=wall.y1-wall.y2;
		double B=wall.x2-wall.x1;
		double C=wall.x1*wall.y2-wall.x2*wall.y1;

		double dist=(A*ball.position.x + B*ball.position.y + C) / Math.sqrt(A*A+B*B);

		if(Math.abs(dist)>=ball.radius) return;

		double xi=(B*(B*ball.position.x - A*ball.position.y)-A*C)/(A*A+B*B);
//		double yi=(A*(-B*ball.position.x - A*ball.position.y)-A*C)/(A*A+B*B);

		if(xi<wall.x1 || xi>wall.x2) return;

		double dista=ball.radius-Math.abs(dist);

		Vector wallnormal=new Vector(wall.y2-wall.y1,wall.x1-wall.x2);
		Vector wallnormalnormalized=wallnormal.normalize();
		Vector adjust=wallnormalnormalized.mul(dista);
		Vector newposition;
		if(dist<0)
			newposition=ball.position.add(adjust);
		else
			newposition=ball.position.sub(adjust);

		double vchange=ball.velocity.dot(wallnormalnormalized);
		double vchange1=(1+WallRestitution)*vchange;

		Vector velchange1=wallnormalnormalized.mul(vchange1);
		Vector newvelocity=ball.velocity.sub(velchange1);

		Vector wallvect= new Vector(wall.x2-wall.x1,wall.y2-wall.y1);
		Vector walln=wallvect.normalize();
		double vfriction=FRICTION*DT;
		double vforward=ball.velocity.dot(walln);

		Vector frictvect;
		if(vfriction>=Math.abs(vforward))
			frictvect=walln.mul(vforward);
		else
			frictvect=walln.mul(vfriction);
		Vector newvelocitywithfriction=newvelocity.sub(frictvect);

		ball.velocity=newvelocitywithfriction;
		ball.position=newposition; 

	}

	public void handleCollision(Ball ball1, Ball ball2)
	{
		//collision vector is difference of positions
		Vector collision=ball1.position.sub(ball2.position);
		double distance = collision.length();

		//the balls overlap.  need to move them apart.
		Vector adjust=collision.mul((ball1.radius+ball2.radius-distance)/distance);

		double im1=1.0/ball1.mass;
		double im2=1.0/ball2.mass;

		//adjust positions according to mass
		Vector adjust1=adjust.mul(im1/(im1+im2));
		Vector adjust2=adjust.mul(im2/(im1+im2));

		//get new positions
		Vector newposition1=ball1.position.add(adjust1);
		Vector newposition2=ball2.position.sub(adjust2);

		//now all we need is the collision angle
		Vector collision_n=collision.normalize();
		//velocity difference vector
		Vector vdiff=ball1.velocity.sub(ball2.velocity);
		//change is based on how much velocity lies along the collision angle
		double vchange=vdiff.dot(collision_n);
		//adjust for mass and restitution
		double vchange1=(1+Restitution)*vchange*im1/(im1+im2);
		double vchange2=(1+Restitution)*vchange*im2/(im1+im2);
		//adjust velocity by adjustment magnitude along the collision vector
		Vector velchange1=collision_n.mul(vchange1);
		Vector velchange2=collision_n.mul(vchange2);
		Vector newvelocity1=ball1.velocity.sub(velchange1);
		Vector newvelocity2=ball2.velocity.add(velchange2);

		ball1.position=newposition1;
		ball2.position=newposition2;
		ball1.velocity=newvelocity1;
		ball2.velocity=newvelocity2;
	}

	public void handleLink(int l)
	{
		Link link=links.get(l);

		Ball ball1=link.ball1;
		Ball ball2=link.ball2;

		//collision vector is difference of positions
		Vector collision=ball1.position.sub(ball2.position);
		double distance = collision.length();

		if(link.isRope && link.length()>distance)
		{
			return;
		}

		//the balls need to stay a certain distance apart
		Vector adjust=collision.mul((link.length()-distance)/distance);

		double im1=1.0/ball1.mass;
		double im2=1.0/ball2.mass;

		//adjust positions according to mass
		Vector adjust1=adjust.mul(im1/(im1+im2));
		Vector adjust2=adjust.mul(im2/(im1+im2));

		//get new positions
		Vector newposition1;
		Vector newposition2;

		newposition1=ball1.position.add(adjust1);
		newposition2=ball2.position.sub(adjust2);

		//now all we need is the collision angle
		Vector collision_n=collision.normalize();
		//velocity difference vector
		Vector vdiff=ball1.velocity.sub(ball2.velocity);
		//change is based on how much velocity lies along the collision angle
		double vchange=vdiff.dot(collision_n);
		//adjust for mass and restitution
		double vchange1=(1+1)*vchange*im1/(im1+im2);
		double vchange2=(1+1)*vchange*im2/(im1+im2);
		//adjust velocity by adjustment magnitude along the collision vector
		Vector velchange1=collision_n.mul(vchange1);
		Vector velchange2=collision_n.mul(vchange2);
		Vector newvelocity1=ball1.velocity.sub(velchange1);
		Vector newvelocity2=ball2.velocity.add(velchange2);

		ball1.position=newposition1;
		ball2.position=newposition2;
		ball1.velocity=newvelocity1;
		ball2.velocity=newvelocity2;
	}

	public void doFrame()
	{
		int i,j;
		//move each ball
		for(i=0; i<balls.size(); i++)
			balls.get(i).move();

		//apply gravity
		for(i=0; i<balls.size(); i++)
			balls.get(i).velocity.y+=gravity*DT;

		//detect collision between balls
		for(i=0; i<balls.size(); i++)
		{
			for(j=i+1; j<balls.size(); j++)
			{
				//if dist(balli,ballj)<=radii^2
				if((balls.get(j).position.x-balls.get(i).position.x)*(balls.get(j).position.x-balls.get(i).position.x)+(balls.get(j).position.y-balls.get(i).position.y)*(balls.get(j).position.y-balls.get(i).position.y) <= (balls.get(j).radius+balls.get(i).radius)*(balls.get(j).radius+balls.get(i).radius))
				{
					handleCollision(balls.get(j),balls.get(i));
				}
			}
		}

		//detect collision with a wall
		for(i=0; i<balls.size(); i++)
		{
			for(j=0; j<walls.size(); j++)
			{
				handleWallCollision(balls.get(i),walls.get(j));
			}
		}

		for(i=0; i<links.size(); i++)
			handleLink(i);

		if(graphics)
			repaint();
	}

	public void makeBall(int x, int y)
	{
		Ball ball=new Ball(this);
		ball.position.x=x;
		ball.position.y=y;
		ball.velocity.x=ball.velocity.y=0;
		ball.mass=1;
		balls.add(ball);
	}

	public void makeLink(int ball1, int ball2)
	{
		if(ball1==ball2) return;
		Link link=new Link(balls.get(ball1),balls.get(ball2),false);
		links.add(link);
	}

	public void makeWall(int x1,int y1,int x2,int y2)
	{
		Wall wall=new Wall(x1,y1,x2,y2);
		walls.add(wall);
	}

	public void paintComponent(Graphics g)
	{
		if(!graphics) return;

		g.setColor(Color.WHITE);
		g.fillRect(0,0,WIDTH,HEIGHT);

		for(Ball ball:balls)
			ball.paint(g);
		for(Link link:links)
			link.paint(g);
		for(Wall wall:walls)
			wall.paint(g);
	}
}
