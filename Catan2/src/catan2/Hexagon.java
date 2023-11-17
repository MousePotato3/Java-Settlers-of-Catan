package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

public class Hexagon extends Component {
	private static final long serialVersionUID = 1L;
	private String type;
	private int number;
	private int value;

	public Hexagon(String t){
		type=t;
	}
	
	public Hexagon(Hexagon h){
		this.setLocation(h.getLocation());
		this.type=h.type;
		this.number=h.number;
		this.value=h.value;
	}
	
	public String getType(){
		return type;
	}
	
	public int getNumber(){
		return number;
	}
	
	public int getValue(){
		return value;
	}
	
	public void setNumber(int n){
		number=n;
		if(number<7)
			value=number-1;
		else
			value=13-number;
	}
	
	public boolean equals(Hexagon h){
		if(getLocation()==h.getLocation())
			return true;
		return false;
	}

	public void draw(Graphics g) {
		int x=(int)getLocation().getX();
		int y=(int)getLocation().getY();

		if(type!="desert") {
		 	g.setColor(Color.white);
 		 	g.fillOval(x-25,y-25,50,50);
 		 	if(number==6||number==8)
 		 		g.setColor(Color.red);
 		 	else
 		 		g.setColor(Color.black);
	 		Font font=new Font("Veranda", Font.BOLD,32);
	 		g.setFont(font);
	 		if(number<10)
	 			g.drawString(Integer.toString(number), x-9, y+12);
	 		else
	 			g.drawString(Integer.toString(number), x-18, y+12);
		}
	}
}
