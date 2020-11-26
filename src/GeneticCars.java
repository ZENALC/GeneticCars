/*
	MOST OF YOUR CODE WILL GO IN THIS CLASS

	This is the main class for your project.  It does the following:
		- constructs KILLTOPOPULATION random cars
		- runs GENERATIONS breed/race/kill/mutate generations to evolve a car that completes the racetrack
		- shows the resulting car

	A generation consists of the following:
		- breed: mate pairs of cars, with probability BREED_RATE, adding the resulting cars to the population
		- race every car.  for each car, make a simulated world, run the car for ITERATIONS frames, then score it
			- cars are scored first by distance traveled.  Further is better.
			- cars that make the end of the track (position of 500) are scored second by iterations, or time taken to reach the end.  Smaller is better.
		- kill: sort the cars by score, and keep only the top KILLTOPOPULATION
		- mutate: each car, with probability MUTATE_SELECTION_RATE, has the chance to produce a new mutant car that is added to the population

	YOU SHOULD WRITE, AT A MINIMUM, FUNCTIONS BREED() KILL() and MUTATE().  Find the TODO lines.
*/

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class GeneticCars implements MouseListener
{
	//GENETIC PARAMETERS

	//how many frames the car is raced for
	public static final int ITERATIONS=2000;

	//number of breed/race/kill/mutate rounds
	public static final int GENERATIONS=100;

	//after each kill round, this many cars are left
	public static final int KILLTOPOPULATION=10;

	//the probability that any two cars will mate in a breed round
	public static final double BREED_RATE=0.5;

	//the probability that any car will produce a baby mutant in a mutate round
	public static final double MUTATE_SELECTION_RATE=0.5;

	//if the mutant is made, the probability that any single ball position or link is altered
	public static final double MUTATE_RATE=0.1;


	//This arraylist holds the population of cars
	public ArrayList<Car> population;
	private int previousWorld = 0;

	//Program starts here:
	// creates an initial population
	// does the genetic simulation
	// shows the winning car

	public GeneticCars()
	{
		population=new ArrayList<>();

		generateInitialPopulation(KILLTOPOPULATION);
		doGenetic(GENERATIONS);
		bubble_sort();  // to not embarrass myself, only show a car that works on track
		show(population.get(0));
	}

	// does the genetic simulation
	public void doGenetic(int generations)
	{
		// runs for generations cycles
		for(int g=0; g<generations; g++)
		{
			//calls the breed, race, kill, mutate functions and prints the winner
//			System.out.println("\nBefore breeding: " + population.size());
			breed();
//			System.out.println("After breeding: " + population.size());
			raceAll();
			kill();
//			System.out.println("After killing: " + population.size());
			mutate();
//			System.out.println("After mutating: " + population.size());
			System.out.println("Generation "+(g+1)+": best car has distance "+population.get(0).score_position +
					"/500, Iterations "+population.get(0).score_iterations+"/2000, "+"Track " + previousWorld);
//			if (population.get(0).score_position == 500) {System.out.println("We made it out!"); break;}
		}
	}

	public void bubble_sort() {
		// I only care about the best of the best
		Car bestCar = population.get(0);
		int swapIndex = 0;
		for (int x = 1; x < population.size(); ++x) {
			if (compare(bestCar, population.get(x))) {
				swapIndex = x;
				bestCar = population.get(x);
			}
		}
		population.set(swapIndex, population.get(0));
		population.set(0, bestCar);
	}


	//creates n new cars, each with 10 balls and random links, puts them in the population arraylist
	public void generateInitialPopulation(int n)
	{
		for(int i=0; i<n; i++)
			population.add(new Car(10));
	}

	//TODO
	//YOU WRITE THIS
	public void breed()
	{
		ArrayList<Car> newCars = new ArrayList<>();

		for (int m = 0; m < population.size(); ++m) {
			for (int d = m+1; d < population.size(); ++d) {  // set initial d = 0 for more permutations
				if (Math.random() < BREED_RATE) {
					Car newCar = population.get(m).breed(population.get(d));
					newCars.add(newCar);
				}
			}
		}
		population.addAll(newCars);
		//Make an arraylist of new cars
		// Go through every pair of cars in population
		//  with probability BREED_RATE, mate them by calling the "breed" method in class Car, and add the child to the new car arraylist
		//finally copy the cars in new car over to the population
	}

	//TODO
	//YOU WRITE THIS
	public void mutate()
	{
		ArrayList<Car> newCars = new ArrayList<>();
		for (Car car : population) {
			if (Math.random() < MUTATE_SELECTION_RATE) {
				Car newCar = car.mutate(MUTATE_RATE);
				newCars.add(newCar);
			}
		}
		population.addAll(newCars);
		//Make an arraylist of new cars
		// Go through every car in the population
		//   with probability MUTATE_SELECTION_RATE, call the "mutate" method in class Car and add the child to the new car arraylist
		//finally copy the cars in new car over to the population
	}

	//TODO
	//YOU WRITE THIS
	public void kill()
	{
		//make a "keep" arraylist of cars
		//Do this KILLTOPOPULATION times:
			// go through your population and find the best car.  Use the compare function (below).
			// remove the best car from population and put it in the keep list
		//set population=keep to make the keep list your population
		ArrayList<Car> keep = new ArrayList<>();

		for (int a = 0; a < KILLTOPOPULATION; ++a) {
			Car bestCar = population.get(0);
			for (int b = 1; b < population.size(); ++b) {
				if (compare(bestCar, population.get(b))) {
					bestCar = population.get(b);
				}
			}
			keep.add(bestCar);
		}
		population = keep;
	}

	//false if a is better, true if b is better
	//Use this in your kill function to select the best cars
	private boolean compare(Car a, Car b)
	{
		if(a.score_position>=500 && b.score_position>=500)
			return b.score_iterations<a.score_iterations;
		else
			return b.score_position>a.score_position;
	}

	//go through every car and race it
	public void raceAll()
	{
		for(Car car: population)
			race(car);
	}

	//make a World object containing a racetrack of walls
	// if you do the optional step, you should make several of these and return one of them at random
	public World makeRaceCourse()
	{
		World[] worlds = getRaceCourses();
		previousWorld = (int) (Math.random() * (worlds.length - 1));
		return worlds[previousWorld];
	}

	public World makeRaceCourse(int index)
	{
		World[] worlds = getRaceCourses();
		return worlds[index];
	}

	public World[] getRaceCourses() {
		World[] worlds = new World[4];

		worlds[0] = new World();
		worlds[0].WIDTH=500;
		worlds[0].HEIGHT=500;
		worlds[0].makeWall(1,500,499,500);
		worlds[0].makeWall(-20,132,123,285);
		worlds[0].makeWall(104,285,203,277);
		worlds[0].makeWall(202,275,271,344);
		worlds[0].makeWall(271,344,320,344);
		worlds[0].makeWall(321,345,354,318);
		worlds[0].makeWall(354,318,394,324);
		worlds[0].makeWall(394,324,429,390);
		worlds[0].makeWall(429,391,498,401);

		worlds[1]=new World();
		worlds[1].WIDTH=500;
		worlds[1].HEIGHT=500;
		worlds[1].makeWall(1,82, 499,362);
		worlds[1].makeWall(498,363, 506,412);
		worlds[1].makeWall(508,412, 983,647);

		worlds[2]=new World();
		worlds[2].WIDTH=500;
		worlds[2].HEIGHT=500;
		worlds[2].makeWall(1,64, 284,313);
		worlds[2].makeWall(284,313, 428,293);
		worlds[2].makeWall(426,291, 744,504);
		worlds[2].makeWall(745,505, 837,477);
		worlds[2].makeWall(836,476, 986,566);

		worlds[3]=new World();
		worlds[3].WIDTH=500;
		worlds[3].HEIGHT=500;
		worlds[3].makeWall(2,0, 978,647);

		return worlds;
	}

	//take an individual car, make a racetrack for it and simulate it
		//at the end of the function the car will have a score
	public void race(Car car)
	{
		World w=makeRaceCourse();
		car.constructCar(w);
		int i;
		for(i=0; i<ITERATIONS; i++)
		{
			w.doFrame();
			if(car.getPosition()>=500)
				break;
		}
		car.setScore(i);
	}

	//show every car in population racing, one at a time
	public void showAll()
	{
		for(Car car: population)
		{
			World w=makeRaceCourse();
			car.constructCar(w);
			show(w);
		}
	}

	//show a single car racing
	public void show(Car car)
	{
		World w= makeRaceCourse(previousWorld);
		System.out.println(car.getPosition());
		car.constructCar(w);
		show(w);
	}

	//pop up a window and show a car falling down its track
	private void show(World world)
	{
		JFrame window=new JFrame("World");
		window.setSize(600,600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(world);
		world.addMouseListener(this);
		world.graphics=true;

		window.setVisible(true);

		for(int i=0; i<ITERATIONS; i++)
		{
			world.doFrame();
			try{ Thread.sleep((int)(world.DT*1000/30)); } catch(InterruptedException ignored){}
		}
	}

	//these methods don't do anything currently
	// they're here only if you want to make the "show" window interactive
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
	}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}


	//main just calls GeneticCars
	public static void main(String[] args)
	{
		new GeneticCars();
	}
}
