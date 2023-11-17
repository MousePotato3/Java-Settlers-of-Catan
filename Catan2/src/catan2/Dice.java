package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

public class Dice extends Component {
	private static final long serialVersionUID = 1L;
	public static final Random generator=new Random();

	private int d1;
	private int d2;
	private int sum;
	
	public int rollDice(){
		d1=generator.nextInt(6)+1;
		d2=generator.nextInt(6)+1;
		sum=d1+d2;
		return sum;
	}
	
	public int getRoll(){
		return sum;
	}
	
	public void draw(Graphics g,int s) {
		sum=s;
		int x,y;
		x=240; y=364;
		g.setColor(Color.white);
 		g.fillRect(x-25,y-25,50,50);
 		g.setColor(Color.black);
 		Font font=new Font("Veranda", Font.BOLD,32);
	 	g.setFont(font);
 		if(sum<10)
 			g.drawString(Integer.toString(sum), x-9, y+12);
 		else
 			g.drawString(Integer.toString(sum), x-18, y+12);
	}
}
