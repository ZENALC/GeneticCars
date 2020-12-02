/*
MODIFY THIS CLASS TO MATE TWO CARS AND MUTATE CARS

Your code will go in methods BREED() and MUTATE().  Find the TODO lines.
  you will call these methods from your code in GeneticCars

A "Car" is a collection of balls and links
*/

public class Car
{
	//how many balls in the car
	int nodes;
	//position of balls
	int[] balls_x;
	int[] balls_y;
	//for every ball i,j  true if there's a link between them
	boolean[][] linkmatrix;

	//these are set by the setScore function after a simulated race
	double score_position;		//how far did the car get
	double score_iterations;	//how long did it take the car to reach the end

	//the simulated world the car is running in.  null until the car is raced.
	World world;

	//construct a car with nodes balls and random links
	//every ball is placed between (5,5) and (50,50)

	public Car(int nodes)
	{
		this.world=null;
		this.nodes=nodes;

		balls_x=new int[nodes];
		balls_y=new int[nodes];
		linkmatrix=new boolean[nodes][nodes];

		//randomly place balls between (5,5 and 50,50)
		for(int i=0; i<nodes; i++)
		{
			balls_x[i]=randint(5,50);
			balls_y[i]=randint(5,50);
		}

		//assign a link between two balls with probability 1/3
		for(int i=0; i<nodes; i++)
		{
			for(int j=0; j<nodes; j++)
			{
				if(randint(1,3)==1)
					linkmatrix[i][j]=true;
			}
		}
	}

	//return the average x position of the nodes
	//this is called only after the car has been raced
	public double getPosition()
	{
		float sum=0;
		for(int i=0; i<nodes; i++)
			sum+=world.getBall(i).position.x;
		return sum / nodes;
	}

	//set the car's score
	//this is called once the race simulation is done
		//don't call it before then or you'll get a nullpointerexception
	public void setScore(int iterations)
	{
		score_position=getPosition();
		if(score_position>world.WIDTH)
			score_position=world.WIDTH;
		score_iterations=iterations;
	}

	//build the car into the world: create its balls and links
	//call this when you're ready to start racing
	public void constructCar(World world)
	{
		this.world=world;
		for(int i=0; i<nodes; i++)
		{
			world.makeBall(balls_x[i],balls_y[i]);
		}
		for(int i=0; i<nodes; i++) {
			for(int j=0; j<nodes; j++) {
				if(linkmatrix[i][j]) {
					world.makeLink(i,j);
				}
			}
		}
	}

	//returns a random integer between [a,b]
	private int randint(int a, int b)
	{
		return (int)(Math.random()*(b-a+1)+a);
	}

	//TODO
	//YOU WRITE THIS FUNCTION
	//It should return a "child" car that is the crossover between this car and parameter car c
	public Car breed(Car c)
	{
		//YOUR WORK HERE

		//Choose a random crossover point.  Also choose a car to go first

		// copy the balls from the first car's balls_x and balls_y to the child
		// after the crossover, copy the balls_x and balls_y from the second car to the child

		//pick a new crossover point, then do the same with the linkmatrix

		Car first, second;
		if (Math.random() == 0) {first = this; second = c;} else {first = c; second = this;}

		Car child=new Car(nodes);
		int randomCrossOver = randomint(0, nodes);

		for (int z = 0; z < randomCrossOver; ++z) {
			child.balls_x[z] = first.balls_x[z];
			child.balls_y[z] = first.balls_y[z];
		}

		for (int z = randomCrossOver; z < nodes; ++z) {
			child.balls_x[z] = second.balls_x[z];
			child.balls_y[z] = second.balls_y[z];
		}

		for (int i = 0; i < nodes; ++i) {
			for (int j = 0; j < nodes; ++j) {
				if (randomint(1, 3) == 3) {
					child.linkmatrix[i][j] = first.linkmatrix[i][j];
				} else {
					child.linkmatrix[i][j] = second.linkmatrix[i][j];
				}
			}
		}

		return child;
	}

	//gives a value between a and b
	public static int randomint(int a, int b)
	{
		return (int)(Math.random()*(b-a)+a);
	}

	//TODO
	//YOU WRITE THIS FUNCTION
	//It should return a car "newcar" that is identical to the current car, except with mutations
	public Car mutate(double probability)
	{
		//YOUR WORK HERE
		//  You should copy over the car's balls_x and balls_y to newcar
		//with probability "probability", change the balls_x and balls_y to a random number from 5 to 50
		//  Then copy over the links
		//	//with probability "probability", set the link to true/false (50/50 chance)

		Car newCar =new Car(nodes);

		for (int x = 0; x < nodes; ++x) {
			if (Math.random() < probability) {
				newCar.balls_x[x] = randomint(5, 50);
				newCar.balls_y[x] = randomint(5, 50);
			} else {
				newCar.balls_x[x] = this.balls_x[x];
				newCar.balls_y[x] = this.balls_y[x];
			}
		}

		for (int x = 0; x < nodes; ++x) {
			for (int y = 0; y < nodes; ++y) {
				if (Math.random() < probability) {
					boolean link = Math.random() > 0.5;
					newCar.linkmatrix[x][y] = link;
				} else {
					newCar.linkmatrix[x][y] = this.linkmatrix[x][y];
				}
			}
		}

		return newCar;
	}
}
