package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class City extends Component {
	private static final long serialVersionUID = 1L;
	private Point location;
	private Color color;
	private int playerNum;
	
	public City(Point p, Color c, int n){
		location=p;
		color=c;
		playerNum=n;
	}
	
	public City(City c){
		this.location=c.location;
		this.color=c.color;
		this.playerNum=c.playerNum;
	}
	
	public Color getColor(){
		return color;
	}
	
	public Point getLocation(){
		return location;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
	public void draw(Graphics g) {
		int x=(int)location.getX();
		int y=(int)location.getY();
		
		Polygon settlement=new Polygon();
		settlement.addPoint(x-10,y+10);
		settlement.addPoint(x-10,y-8);
		settlement.addPoint(x,y-8);
		settlement.addPoint(x+8, y-15);
		settlement.addPoint(x+15,y-8);
		settlement.addPoint(x+15,y+10);
		
		g.setColor(color);
		g.fillPolygon(settlement);
		g.setColor(Color.black);
		g.drawPolygon(settlement);
	}
}
