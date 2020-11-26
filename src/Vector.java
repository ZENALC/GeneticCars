/*
YOU DO NOT NEED TO MODIFY THIS CLASS

Vector stores a 2D vector and provides basic vector math operations
*/

public class Vector
{
	double x, y;

	public Vector(double x, double y)
	{
		this.x=x; this.y=y;
	}

	public Vector add(Vector v)
	{
		Vector vnew=new Vector(x,y);
		vnew.x+=v.x;
		vnew.y+=v.y;
		return vnew;
	}

	public Vector sub(Vector v)
	{
		Vector vnew=new Vector(x,y);
		vnew.x-=v.x;
		vnew.y-=v.y;
		return vnew;
	}

	public Vector mul(double f)
	{
		Vector vnew=new Vector(x,y);
		vnew.x*=f;
		vnew.y*=f;
		return vnew;
	}

	public double dot(Vector v)
	{
		return x*v.x + y*v.y;
	}

	public Vector normalize()
	{
		Vector vnew=new Vector(x,y);
		vnew.x=x/Math.sqrt(x*x+y*y);
		vnew.y=y/Math.sqrt(x*x+y*y);
		return vnew;
	}

	public double length()
	{
		return Math.sqrt(x*x+y*y);
	}

	public double lengthsquared()
	{
		return x*x+y*y;
	}

	public double angle()
	{
		return Math.atan2(y,x);
	}
}
