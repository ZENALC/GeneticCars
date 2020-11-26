import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Knapsack470
{
	static final int BOXNUM=50;				//number of boxes
	static final int XSIZE=890,YSIZE=700;
	static final int SACKWIDTH=8*BOXNUM+32;
	static final int MAX_WEIGHT=100;		//heaviest possible box
	static final int SACK_WEIGHT=1000;		//sack breaks above 1000
	static final int MAX_VALUE=100;			//most valuable possible box
	static final int MAX_SACK=2500;
	static JFrame f;
	static Comp comp;
	static Box[] box;		//the 50 boxes
	static Sack[] sack;		//the population of sacks
	static int sacknum=20;	//current size of population
	static JButton pButton;
	static JTextField iField;

	public static void main(String[] args)
	{
		char letter='A';
		box=new Box[BOXNUM];
		for (int i=0; i<BOXNUM; i++)
		{
			box[i]=new Box(letter++);
			if (letter-1=='Z')
				letter='a';
		}

		//initial population
		sack=new Sack[MAX_SACK];

		//make 20 random sacks
		for (int i=0; i<sacknum; i++)
		{
			//if I create an invalid (too heavy) sack, redo it
			do
			{
				sack[i]=new Sack();
			} while (sack[i].getWeight()>SACK_WEIGHT);
		}
		//I have 20 valid sacks now.

		setupWindow();
	}

	public static void setupWindow()
	{
		f = new JFrame();
		f.setLayout(null);
		setFrameTitle();
		f.setSize(XSIZE,YSIZE);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		comp = new Comp();
		comp.setBounds(0,50,XSIZE,YSIZE-50);
		f.add(comp);
		JPanel panel = new JPanel();
		panel.setBounds(0,0,XSIZE,50);
		pButton = new JButton("Breed");
		pButton.addActionListener(new ButtonListener());
		panel.add(pButton);
		JButton goButton = new JButton("Run");
		goButton.addActionListener(new ButtonListener());
		panel.add(goButton);
		iField = new JTextField(4);
		iField.setText("100");
		panel.add(iField);

		f.add(panel);
		f.setVisible(true);
	}

	public static void setFrameTitle()
	{
		f.setTitle("Knapsack: $"+bestValue()+" ( $"+maxValue()+" ), "+bestValueWeight()+" lbs ( "+SACK_WEIGHT+" lbs )");
	}

	//breed: go through every possible of pair of sacks, roll the dice, 
	//   and potentially mate them to make a new sack
	public static final double MATE_PROBABILITY=.45;
	public static void breed()
	{
		//go through every possible pair
			//sacknum sacks in our sack array
		int parentpopulation=sacknum;
		for(int m=0; m<parentpopulation; m++)
		{
			for(int d=0; d<parentpopulation; d++)
			{
				if(Math.random()<MATE_PROBABILITY)
				{
					Sack baby = crossover(sack[m],sack[d]);
					//add baby to population  --  BUT ONLY IF IT'S VALID
					if(baby.getWeight()<=SACK_WEIGHT)
					{
						sack[sacknum]=baby;
						sacknum++;
					}
				}
			}
		}
	}
	//mating happens here and make a new baby sack
	public static Sack crossover(Sack d, Sack m)
	{
		Sack baby;
		//make it random - doesn't matter
		baby=new Sack();
			//for reference: baby has boolean[] insack = new boolean[BOXNUM];
		int dividingpoint = randomint(0,BOXNUM);
		//copy every gene up to divide from one parent
		for(int i=0; i<dividingpoint; i++)
			baby.insack[i]=d.insack[i];
		//rest from other
		for(int i=dividingpoint; i<BOXNUM; i++)
			baby.insack[i]=m.insack[i];
		return baby;
	}
	
	//gives a value between a and b
	public static int randomint(int a, int b)
	{
		return (int)(Math.random()*(b-a)+a);
	}


	public static final int SURVIVORS=20;		//only 20 are left
	public static void kill()
	{
		//pick a number of Survivors
		//at end of stage, only that number survive
		
		//use a fitness to choose survivors
			//how do I compare two sacks?  use the higher getValue
		
		//step 1: sort the sacks
		//step 2: cull: limit population to survivors
		
		//selection sort the sacks
		for(int i=0; i<sacknum; i++)
		{
			for(int j=i+1; j<sacknum; j++)
			{
				//if sack[i] and sack[j] are out of order, swap them
				if(sack[i].getValue()<sack[j].getValue())
				{
					Sack tmp=sack[i];
					sack[i]=sack[j];
					sack[j]=tmp;
				}
			}
		}
		//cap the population at SURVIVORS
		if(SURVIVORS<sacknum)
			sacknum = SURVIVORS;
	}

	//the odds that any sack will be chosen to make a mutant
	public static final double MUTATION_SELECTION_RATE=0.1;
	//the odds that any particular gene will flip
	public static final double MUTATE_RATE=0.1;
	public static void mutate()
	{
		//going to go through population
		// randomly (MUTATION_SELECTION_RATE) choose to make a mutant
		
		// to make a mutant:
		//    copy everything from parent, except
		//    randomly (MUTATE_RATE) flip a gene

		int parentpopulation=sacknum;
		for(int i=0; i<parentpopulation; i++)
		{
			//random: 0..1
			if(Math.random()<MUTATION_SELECTION_RATE)
			{
				//this code will run with MUTATION_SELECTION_RATE probability
				
				//you are chosen!
				Sack mutant=new Sack();
				
				//go through and copy from parent
				for(int g=0; g<BOXNUM; g++)
				{
					//copy the gene from parent
					mutant.insack[g]=sack[i].insack[g];
					//every now and then flip false to true and true to false
					if(Math.random()<MUTATE_RATE)
						mutant.insack[g]=!mutant.insack[g];
				}
				
				//only add to population if valid
				if(mutant.getWeight()<=SACK_WEIGHT)
					sack[sacknum++]=mutant;
			}
		}
	}

	public static int bestValue()
	{
		int v=0;
		for (int i=0; i<sacknum; i++)
			v=(sack[i].getValue()>v)? sack[i].getValue() : v;
		return v; 
	}

	public static int bestValueWeight()
	{
		int v=0;
		for (int i=0; i<sacknum; i++)
			v=(sack[i].getValue()>sack[v].getValue())? i : v;
		return sack[v].getWeight(); 
	}

	public static int maxValue()
	{
		int v=0;
		for (int i=0; i<BOXNUM; i++)
			v=v+box[i].value;
		return v;
	}

	public static class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();
			if (command.equals("Run"))
			{
				for (int i=0; i<Integer.parseInt(iField.getText()); i++)
				{
					breed();
					kill();
					mutate();
					setFrameTitle();
					f.setTitle(f.getTitle()+" -- "+i+" iteration");
				}
			}
			else if (command.equals("Breed"))
			{
				breed();
				pButton.setText("Kill");
			}
			else if (command.equals("Kill"))
			{
				kill();
				pButton.setText("Mutate");
			}
			else if (command.equals("Mutate"))
			{
				mutate();
				pButton.setText("Breed");
			}
			comp.repaint();
			setFrameTitle();

		}
	}

	public static class Comp extends JComponent
	{
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.WHITE);
			g.fillRect(0,0,XSIZE,YSIZE);
			for (int i=0; i<BOXNUM; i++)
			{
				box[i].drawBox(g,40*(i%(XSIZE/40)),30+40*(i/(XSIZE/40)));
			}
			int y=70+40*(BOXNUM/(XSIZE/40));
			for (int i=0; i<sacknum; i++)
			{
				sack[i].drawSack(g,(SACKWIDTH+10)*(i%(XSIZE/(SACKWIDTH+10))),y+15*(i/(XSIZE/(SACKWIDTH+10))));
			}
		}
	}
	public static class Box
	{
		int weight;
		int value;
		char name;

		public Box(char name)
		{
			weight=(int)(Math.random()*MAX_WEIGHT+1);
			value=(int)(Math.random()*MAX_VALUE+1);
			this.name=name;
		}

		public void drawBox(Graphics g, int x, int y)
		{
			int shade = (int)((double)weight/(double)MAX_WEIGHT*255);
			g.setColor(new Color(255-shade,0,0));
			g.fillRect(x,y,30,30);
			g.setColor(new Color(0,255,0));
			g.drawString(""+value,x+5,y+25);
			g.setColor(Color.WHITE);
			g.drawString(""+name,x+5,y+10);
		}
	}

	public static class Sack
	{
		//this array holds 50 true/false, one for each box
			//if true, box in sack
		boolean[] insack = new boolean[BOXNUM];

		//new Sack, this makes a random sack
		public Sack()
		{
			//make random sack
			boolean[] boxadded=new boolean[BOXNUM];

			//"flip a coin", heads it goes in
			for (int i=0; i<BOXNUM; i++)
				insack[i]=(Math.random()<0.5);
		}

		public Sack(Sack s)
		{
			for (int i=0; i<BOXNUM; i++)
				insack[i]=s.insack[i];
		}

		//total weight in sack
		public int getWeight()
		{
			int w=0;
			for (int i=0; i<BOXNUM; i++)
				if (insack[i])
					w+=box[i].weight;
			return w;
		}

		//total value of the sack
		public int getValue()
		{
			int v=0;
			for (int i=0; i<BOXNUM; i++)
				if (insack[i])
					v+=box[i].value;
			return v;
		}

		public String getword()
		{
			String s="";
			for (int i=0; i<BOXNUM; i++)
				if (insack[i])
					s+=box[i].name;
				else
					s+='-';
			return s;
		}

		public void drawSack(Graphics g, int x, int y)
		{
			g.setColor(Color.BLUE);
			g.drawLine(x,y+10,x+SACKWIDTH,y+10);
			g.drawLine(x,y,x,y+10);
			g.drawLine(x+SACKWIDTH,y,x+SACKWIDTH,y+10);
			g.setColor(new Color(100,100,0));
			g.drawString(""+getValue(),x,y+9);
			g.setColor(Color.BLACK);
			g.drawString(getword(),x+32,y+9);
		}		
	}
}
