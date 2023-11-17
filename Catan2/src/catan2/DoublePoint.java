package catan2;

import java.awt.Point;

// This class is just a structure to hold two Point variables at once, for Road objects.
public class DoublePoint {
	private Point p1;
	private Point p2;
	
	public DoublePoint(Point a, Point b){
		p1=a;
		p2=b;
	}
	public Point getP1() {return p1;}
	public Point getP2() {return p2;}
	
	public boolean equals(DoublePoint k){
		if((p1.equals(k.getP1()) && p2.equals(k.getP2())) || (p1.equals(k.getP2()) && p2.equals(k.getP1())))
			return true;
		return false;
	}
}
