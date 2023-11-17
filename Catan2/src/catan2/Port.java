package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

public class Port extends Component {
	private static final long serialVersionUID = 1L;
	private String type;
	private Color color;
	private DoublePoint portLocation;

	public Port(String t,Color c){
		type=t;
		color=c;
	}
	
	public Port(Port p){
		this.portLocation=p.portLocation;
		this.setLocation(p.getLocation());
		this.type=p.type;
		this.color=p.color;
	}
	
	public String getType(){
		return type;
	}
	public DoublePoint getPortLocations(){
		return portLocation;
	}
	
	public void setPortLocations(DoublePoint dp){
		portLocation=dp;
	}
	
	public void draw(Graphics g) { //Displays the port information
		int x=(int)getLocation().getX();
		int y=(int)getLocation().getY();
		Font font=new Font("Veranda", Font.BOLD,32);
 		g.setFont(font);
		g.setColor(Color.black);
		if(type=="general")
			g.drawString("3:1", x, y);
		else
			g.drawString("2:1", x, y);
		
		 g.setColor(color);
		 x=(int)portLocation.getP1().getX();
		 y=(int)portLocation.getP1().getY();
 		 g.fillOval(x-15/2,y-15/2,15,15);
		 x=(int)portLocation.getP2().getX();
		 y=(int)portLocation.getP2().getY();
 		 g.fillOval(x-15/2,y-15/2,15,15);
	}
}
