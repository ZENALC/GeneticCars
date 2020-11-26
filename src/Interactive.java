/*
TO TRY OUT THE PHYSICS SIMULATOR, RUN THIS CLASS.
BUT DON'T USE IT IN YOUR PROJECT.

YOU DO NOT NEED TO MODIFY THIS CLASS.

Interactive is a main class that creates an empty physics simulator world.

Left click to add balls.  Drag between balls to link them.  If you drag and there are no balls, you create a wall.
Right click to start the simulation.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Interactive implements MouseListener, Runnable
{
	public static final int WIDTH=1000,HEIGHT=700;

	World world;
	JFrame window;
	boolean running=false;
	Thread thread;

	public Interactive()
	{
		world=new World();
		world.WIDTH=WIDTH; world.HEIGHT=HEIGHT;

		JFrame window=new JFrame("World");
		window.setSize(WIDTH,HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(world);
		world.addMouseListener(this);
		world.graphics=true;

		world.makeWall(0,HEIGHT-50,WIDTH,HEIGHT-50);

		thread=new Thread(this);

		window.setVisible(true);

		thread.start();
	}

	public void mouseClicked(MouseEvent e)
	{
		int px=e.getX();
		int py=e.getY();
	}
	int px,py;
	public void mousePressed(MouseEvent e)
	{
		px=e.getX();
		py=e.getY();
	}
	int rx,ry;
	public void mouseReleased(MouseEvent e)
	{
		rx=e.getX();
		ry=e.getY();

		if(e.getButton()==MouseEvent.BUTTON1)
		{
			int ball1=-1,ball2=-1;
			for(int i=0; i<world.balls.size(); i++)
				if(world.balls.get(i).contains(px,py))
					ball1=i;
			for(int i=0; i<world.balls.size(); i++)
				if(world.balls.get(i).contains(rx,ry))
					ball2=i;
			if(ball1!=-1 && ball2!=-1)
			{
				if(ball1!=ball2)
				{
					world.makeLink(ball1,ball2);
					System.out.println("Making a link between the balls at "+px+","+py+" and "+rx+","+ry);
				}
				else
				{
					Ball b=world.balls.get(ball1);
					world.balls.remove(ball1);
					ArrayList<Link> linksToGo=new ArrayList<Link>();
					for(int l=0; l<world.links.size(); l++)
						if(world.links.get(l).ball1==b || world.links.get(l).ball2==b)
							linksToGo.add(world.links.get(l));
					for(Link l:linksToGo)
						world.links.remove(l);
					System.out.println("Removing the ball at position "+px+","+py);
				}
			}
			else if(px==rx && py==ry)
			{
				world.makeBall(px,py);
				System.out.println("Making a ball at position "+px+","+py);
			}
			else if ((rx-px)*(rx-px) + (ry-py)*(ry-py) >= 5*5)
			{
 				world.makeWall(px,py,rx,ry);
				System.out.println("Making a wall from "+px+","+py+" to "+rx+","+ry);
			}
		}
		else
		{
			running=!running;
		}

		world.repaint();
	}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}

	public void run()
	{
		while(true)
		{
			if(running)
				world.doFrame();
			try{ Thread.sleep((int)(world.DT*1000/30)); } catch(InterruptedException e){};
		}
	}

	public static void main(String[] args)
	{
		new Interactive();
	}
}
