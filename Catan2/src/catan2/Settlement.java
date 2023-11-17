package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class Settlement extends Component {
	private static final long serialVersionUID = 1L;
	private Point location;
	private Color color;
	private int playerNum;
	
	public Settlement(Point p, Color c,int n){
		location=p;
		color=c;
		playerNum=n;
	}
	
	public Settlement(Settlement s){
		this.location=s.location;
		this.color=s.color;
		this.playerNum=s.playerNum;
	}

	public Point getLocation(){
		return location;
	}
	
	public Color getColor(){
		return color;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
	public void draw(Graphics g) {
		int x=(int)getLocation().getX();
		int y=(int)getLocation().getY();
		
		Polygon settlement=new Polygon();
		settlement.addPoint(x-10,y+10);
		settlement.addPoint(x-10,y-8);
		settlement.addPoint(x,y-15);
		settlement.addPoint(x+10,y-8);
		settlement.addPoint(x+10,y+10);
		
		g.setColor(color);
		g.fillPolygon(settlement);
		g.setColor(Color.black);
		g.drawPolygon(settlement);
	}
}
