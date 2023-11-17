package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class Road extends Component {
	private static final long serialVersionUID = 1L;
	private Point location1;
	private Point location2;
	private Color color;
	private int playerNum;
	
	public Road(Point p1, Point p2, Color c, int n){
		location1=p1;
		location2=p2;
		color=c;
		playerNum=n;
	}
	
	public Road(Road r){
		this.location1=r.location1;
		this.location2=r.location2;
		this.color=r.color;
		this.playerNum=r.playerNum;
	}
	
	public Point getLocation1(){
		return location1;
	}
	
	public Point getLocation2(){
		return location2;
	}

	public Color getColor(){
		return color;
	}
	public int getPlayerNum(){
		return playerNum;
	}
	
	public void draw(Graphics g) {
		int angle;
		int x1=(int)location1.getX(); //This is always the leftmost point
		int y1=(int)location1.getY();
		int y2=(int)location2.getY();

		Graphics2D g2=(Graphics2D)g;
		g2.setColor(color);
		Rectangle rect = new Rectangle(x1+7, y1-4, 60, 8);
		
		if(y1<y2)
			angle=60;
		else if(y1>y2)
			angle=300;
		else
			angle=0;
		
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(angle),x1,y1);

		Shape rotatedRect = at.createTransformedShape(rect);
		g2.fill(rotatedRect);
		g2.setColor(Color.black);
		g2.draw(rotatedRect);
	}
}
