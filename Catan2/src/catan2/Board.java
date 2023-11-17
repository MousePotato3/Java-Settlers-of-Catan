package catan2;

import java.awt.Color;
//import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.Image;
import java.awt.Point;
import java.awt.RadialGradientPaint;
//import java.awt.Toolkit;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int SLEEP_TIME=100;
	public static final int MAX_SETTLEMENTS=5;
	public static final int MAX_CITIES=4;
	public static final int MAX_ROADS=15;
	public static final int NUM_PLAYERS=4;
	public static final int NUM_TILES=19;
	public static final int NUM_PORTS=9;
	
	public static final int CENTERX=683;
	public static final int CENTERY=364;
	public static final int WIDTH=75;
	public static final int HEIGHT=65;
	public static final int SMALLWIDTH=37;
	private ImageIcon desert,forest,hill,mountain,pasture,plain,robber;
	
	private Hexagon[] myTiles=new Hexagon[19];
	private Integer[] hexNumbers = {5,2,6,3,8,10,9,12,11,4,8,10,9,4,5,6,3,11};
	private Port[] myPorts= new Port[9];
	private Settlement[] mySettlements = new Settlement[MAX_SETTLEMENTS*NUM_PLAYERS];
	private City[] myCities = new City[MAX_CITIES*NUM_PLAYERS];
	private Road[] myRoads = new Road[MAX_ROADS*NUM_PLAYERS];
	private Player[] myPlayers = new Player[NUM_PLAYERS];
	private Point[] hexCenters=new Point[NUM_TILES];
	private Point[] portTextLocations=new Point[NUM_PORTS];
	private Point[] portPoint1Locations=new Point[NUM_PORTS];
	private Point[] portPoint2Locations=new Point[NUM_PORTS];
	private Vector<Point> hexIntersections=new Vector<Point>();

	private Point robberLocation;
	private Dice d=new Dice();
	//private int centerX,centerY,width,height,smallWidth;
	private boolean isVisible=true;
	
	public Board() {
		super();
		desert=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\desert.png");
		forest=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\forest.png");
		hill=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\hill.png");
		mountain=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\mountain.png");
		pasture=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\pasture.png");
		plain=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\plain.png");
		robber=new ImageIcon("C:\\Users\\adhub\\Pictures\\Settlers Tiles\\robber.png");
		
/*		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		centerX=dim.width/2;
		centerY=dim.height/2-20;
		Image tile = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Andrew\\Pictures\\Settlers Tiles\\desert.png");
		width = (int)(tile.getWidth(null)/2);
		height = (int)(tile.getHeight(null)/2);
		smallWidth = (int)(Math.sqrt(Math.pow(WIDTH*2, 2)-Math.pow(HEIGHT*2,2))/2);	
		System.out.println("Screen Info: "+centerX+" "+centerY+" "+width+" "+height+" "+smallWidth);
*/	}
	
    public Board deepCopy() {
    	return new Board((Board)this);
    }

	public Board(Board b) {
		super();
		// Try using for loops on each array and vector
		for(int i=0; i<b.myTiles.length; i++)
			if(b.myTiles[i]!=null)
				this.myTiles[i]=new Hexagon(b.myTiles[i]);
		this.hexNumbers=b.hexNumbers;
		for(int i=0; i<b.myPorts.length; i++)
			if(b.myPorts[i]!=null)
				this.myPorts[i]=new Port(b.myPorts[i]);
		for(int i=0; i<b.mySettlements.length; i++)
			if(b.mySettlements[i]!=null)
				this.mySettlements[i]=new Settlement(b.mySettlements[i]);
		for(int i=0; i<b.myCities.length; i++)
			if(b.myCities[i]!=null)
				this.myCities[i]=new City(b.myCities[i]);
		for(int i=0; i<b.myCities.length; i++)
			if(b.myRoads[i]!=null)
				this.myRoads[i]=new Road(b.myRoads[i]);
		this.hexCenters=b.hexCenters;
		this.portTextLocations=b.portTextLocations;
		this.portPoint1Locations=b.portPoint1Locations;
		this.portPoint2Locations=b.portPoint2Locations;
		for(int i=0; i<b.hexIntersections.size(); i++)
			this.hexIntersections.add(b.hexIntersections.get(i));
		this.robberLocation=b.robberLocation;
		this.d=b.d;
		this.desert=b.desert;
		this.forest=b.forest;
		this.hill=b.hill;
		this.mountain=b.mountain;
		this.pasture=b.pasture;
		this.plain=b.plain;
		this.robber=b.robber;
	}
/*	public Object clone() throws CloneNotSupportedException{
		Board b=(Board)super.clone();
		return b;
	}
*/
	public void paintComponent(Graphics g) {		
		if(isVisible==false)
			return;
		super.paintComponent(g);
		
		// This paints the background as a square, gradually fading from blue to white
		Graphics2D g2=(Graphics2D)g;
		
//		System.out.println(CENTERX+" "+CENTERY);
		Point2D center = new Point2D.Float(CENTERX, CENTERY);
		float radius = 1000;
		float[] dist = {0.0f, 1.0f};
		Color[] colors = {Color.WHITE, Color.BLUE};
		g2.setPaint(new RadialGradientPaint(center, radius, dist, colors, CycleMethod.NO_CYCLE));
		g.fillRect((int)(center.getX()-radius/2),(int)(center.getY()-radius/2),(int)radius,(int)radius);

		for(int i=0;i<myTiles.length;i++) {
			if(myTiles[i]==null)
				break;
			Hexagon myHex=myTiles[i];
			Icon myIcon;
			int x=(int)myHex.getLocation().getX();
			int y=(int)myHex.getLocation().getY();
			if(myHex.getType()=="wood")
				myIcon=forest;
			else if(myHex.getType()=="clay")
				myIcon=hill;
			else if(myHex.getType()=="ore")
				myIcon=mountain;
			else if(myHex.getType()=="sheep")
				myIcon=pasture;
			else if(myHex.getType()=="wheat")
				myIcon=plain;
			else
				myIcon=desert;
			myIcon.paintIcon(this,g,(int)x-75, (int)y-131/2);
			myHex.draw(g);
		}
		if(robberLocation!=null){
			Icon rIcon=robber;
			rIcon.paintIcon(this,g,(int)robberLocation.getX()-15,(int)robberLocation.getY()-33);
		}
		
		for(int i=0;i<myPorts.length;i++) {
			if(myPorts[i]==null)
				break;
			Port port=myPorts[i];
			port.draw(g);
		}
		
		for(int i=0;i<myRoads.length;i++){
			if(myRoads[i]==null)
				break;
			Road road=myRoads[i];
			road.draw(g);
		}
		
		for(int i=0;i<mySettlements.length;i++){
			if(mySettlements[i]==null)
				break;
			Settlement settlement=mySettlements[i];
			settlement.draw(g);
		}
		
		for(int i=0;i<myCities.length;i++){
			if(myCities[i]==null)
				break;
			City city=myCities[i];
			city.draw(g);
		}
		
		for(int i=0;i<myPlayers.length;i++){
			if(myPlayers[i]==null || myPlayers[i].getTextCenter()==null)
				break;
			myPlayers[i].draw(g);
		}
		int roll=d.getRoll();
		d.draw(g,roll);
	}
	
	public Settlement[] getSettlements(){
		return mySettlements;
	}
	
	public City[] getCities(){
		return myCities;
	}
	
	public Road[] getRoads(){
		return myRoads;
	}
	
	public Hexagon getHexagon(int i){
		return myTiles[i];
	}
	
	public Port getPort(int i){
		return myPorts[i];
	}
	
	public String getPortType(Point p){
		for(int i=0; i<myPorts.length; i++)
			if(p.equals(myPorts[i].getPortLocations().getP1()) 
			|| p.equals(myPorts[i].getPortLocations().getP2()))
				return myPorts[i].getType();
		return "";
	}
	
	public Point[] getHexCenters(){
		return hexCenters;
	}
	
	public Vector<Point> getHexIntersections(){
		return hexIntersections;
	}
	
	public Point getRobberLocation(){
		return robberLocation;
	}
	
	public void setVisible(boolean b){
		isVisible=b;
	}
	
	public void setPlayers(Player[] p){
		for(int i=0; i<myPlayers.length; i++)
			myPlayers[i]=p[i];
	}
	
	public void setRobberLocation(Point p){
		robberLocation=p;
	}
	
	public int rollDice(){
		int roll=d.rollDice();
		if(isVisible==false)
			return roll;
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
		return roll;
	}

	public void addSettlement(Point p, Color c, int n){		
		//mySettlements.add(new Settlement(p,c));
		int index=-1;
		for(int i=0; i<mySettlements.length; i++) {
			if(mySettlements[i]==null){
				index=i;
				break;
			}
		}
		
		if(index==-1){
			System.err.println("Error adding settlement to array");
			return;
		}
		mySettlements[index]=new Settlement(p,c,n);
		
		if(isVisible==false)
			return;
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	public void addCity(Point p, Color c, int n){
		int index1=-1;
		for(int i=0; i<myCities.length; i++) {
			if(myCities[i]==null){
				index1=i;
				break;
			}
		}
		int index2=mySettlements.length;
		for(int i=0; i<mySettlements.length; i++) {
			if(mySettlements[i]==null){
				index2=i;
				break;
			}
		}
//		System.out.println("New city location: "+p.getX()+" "+p.getY());
		//myCities.add(new City(p,c));
		if(index1==-1){
			System.err.println("Error adding city settlement to array");
			return;
		}
		myCities[index1]=new City(p,c,n);
		//mySettlements.remove(findSettlementIndex(p));
		//System.out.println("Board addCity: "+findSettlementIndex(p)+" "+(index2-1));
		mySettlements[findSettlementIndex(p)]=mySettlements[index2-1];
		mySettlements[index2-1]=null;

		if(isVisible==false)
			return;
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	public void addRoad(Point p1, Point p2, Color c, int n){
		int index=-1;
		for(int i=0; i<myRoads.length; i++) {
			if(myRoads[i]==null){
				index=i;
				break;
			}
		}
//		System.out.println("New road location: "+p2.getX()+" "+p2.getY());
		if(index==-1){
			System.err.println("Error adding road to array");
			return;
		}
		if((int)p1.getX()>(int)p2.getX())
			myRoads[index]=new Road(p2,p1,c,n);
		else
			myRoads[index]=new Road(p1,p2,c,n);

		if(isVisible==false)
			return;
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	public void initTiles() {
		int i;
		int counter=0;
	
		myTiles[0]=new Hexagon("desert");
		for(i=1; i<4; i++)
			myTiles[i]=new Hexagon("ore");
		for(i=4; i<7; i++)
			myTiles[i]=new Hexagon("clay");
		for(i=7; i<11; i++)
			myTiles[i]=new Hexagon("sheep");
		for(i=11; i<15; i++)
			myTiles[i]=new Hexagon("wheat");
		for(i=15; i<19; i++)
			myTiles[i]=new Hexagon("wood");

		Collections.shuffle(Arrays.asList(myTiles));
	
		// The points are added in a spiral starting at the top 
		// and moving counter-clockwise toward the center
		hexCenters[0]=new Point(CENTERX,CENTERY-2*HEIGHT*2);
		hexCenters[1]=new Point(CENTERX-(WIDTH+SMALLWIDTH),CENTERY-3*HEIGHT);
		hexCenters[2]=new Point(CENTERX-(WIDTH*2+SMALLWIDTH*2),CENTERY-HEIGHT*2);
		hexCenters[3]=new Point(CENTERX-(WIDTH*2+SMALLWIDTH*2),CENTERY);
		hexCenters[4]=new Point(CENTERX-(WIDTH*2+SMALLWIDTH*2),CENTERY+HEIGHT*2);
		hexCenters[5]=new Point(CENTERX-(WIDTH+SMALLWIDTH),CENTERY+3*HEIGHT);
		hexCenters[6]=new Point(CENTERX,CENTERY+2*HEIGHT*2);
		hexCenters[7]=new Point(CENTERX+(WIDTH+SMALLWIDTH),CENTERY+3*HEIGHT);
		hexCenters[8]=new Point(CENTERX+(WIDTH*2+SMALLWIDTH*2),CENTERY+HEIGHT*2);
		hexCenters[9]=new Point(CENTERX+(WIDTH*2+SMALLWIDTH*2),CENTERY);
		hexCenters[10]=new Point(CENTERX+(WIDTH*2+SMALLWIDTH*2),CENTERY-HEIGHT*2);
		hexCenters[11]=new Point(CENTERX+(WIDTH+SMALLWIDTH),CENTERY-3*HEIGHT);
		hexCenters[12]=new Point(CENTERX,CENTERY-HEIGHT*2);
		hexCenters[13]=new Point(CENTERX-(WIDTH+SMALLWIDTH),CENTERY-HEIGHT);
		hexCenters[14]=new Point(CENTERX-(WIDTH+SMALLWIDTH),CENTERY+HEIGHT);
		hexCenters[15]=new Point(CENTERX,CENTERY+HEIGHT*2);
		hexCenters[16]=new Point(CENTERX+(WIDTH+SMALLWIDTH),CENTERY+HEIGHT);
		hexCenters[17]=new Point(CENTERX+(WIDTH+SMALLWIDTH),CENTERY-HEIGHT);
		hexCenters[18]=new Point(CENTERX,CENTERY);
		
		for(i=0; i<19; i++){
			myTiles[i].setLocation(hexCenters[i]);
			if(myTiles[i].getType()!="desert") {
				myTiles[i].setNumber(hexNumbers[counter]);
				counter++;
			}
			else
				robberLocation=myTiles[i].getLocation();
		}
		
		Vector<Point> tempIntersections=new Vector<Point>();
		for(i=0; i<hexCenters.length; i++){
			int x=(int)hexCenters[i].getX();
			int y=(int)hexCenters[i].getY();
			
			tempIntersections.add(new Point(x+WIDTH,y));
			tempIntersections.add(new Point(x-WIDTH,y));
			tempIntersections.add(new Point(x+SMALLWIDTH,y+HEIGHT));
			tempIntersections.add(new Point(x-SMALLWIDTH,y+HEIGHT));
			tempIntersections.add(new Point(x+SMALLWIDTH,y-HEIGHT));
			tempIntersections.add(new Point(x-SMALLWIDTH,y-HEIGHT));
		}
		hexIntersections = removeDuplicates(tempIntersections);
		tempIntersections.clear();
	}
	
	public void initPorts() {
		for(int i=0; i<4; i++)
			myPorts[i]=new Port("general", Color.gray.darker().darker());
		myPorts[4]=new Port("clay", Color.red.darker());
		myPorts[5]=new Port("ore", Color.gray);
		myPorts[6]=new Port("sheep", Color.green);
		myPorts[7]=new Port("wheat", Color.yellow.darker());
		myPorts[8]=new Port("wood", Color.green.darker().darker());
		Collections.shuffle(Arrays.asList(myPorts));
		
		portTextLocations[0]=new Point(CENTERX-SMALLWIDTH+15, CENTERY+5*HEIGHT+30);
		portPoint1Locations[0]=new Point(CENTERX-SMALLWIDTH,CENTERY+5*HEIGHT);
		portPoint2Locations[0]=new Point(CENTERX+SMALLWIDTH,CENTERY+5*HEIGHT);		
		
		portTextLocations[1]=new Point(CENTERX-(WIDTH*2+SMALLWIDTH*2)+10, CENTERY+7*HEIGHT/2+15);
		portPoint1Locations[1]=new Point(CENTERX-(WIDTH+SMALLWIDTH*2),CENTERY+2*HEIGHT*2);
		portPoint2Locations[1]=new Point(CENTERX-(WIDTH*2+SMALLWIDTH),CENTERY+3*HEIGHT);
		
		portTextLocations[2]=new Point(CENTERX-3*(WIDTH+SMALLWIDTH)+10, CENTERY+HEIGHT/2+15);
		portPoint1Locations[2]=new Point(CENTERX-(3*WIDTH+SMALLWIDTH*2),CENTERY);
		portPoint2Locations[2]=new Point(CENTERX-(WIDTH*2+3*SMALLWIDTH),CENTERY+HEIGHT);
	
		portTextLocations[3]=new Point(CENTERX-3*(WIDTH+SMALLWIDTH)+10, CENTERY-5*HEIGHT/2+5);
		portPoint1Locations[3]=new Point(CENTERX-(3*WIDTH+SMALLWIDTH*2),CENTERY-HEIGHT*2);
		portPoint2Locations[3]=new Point(CENTERX-(WIDTH*2+3*SMALLWIDTH),CENTERY-3*HEIGHT);
	
		portTextLocations[4]=new Point(CENTERX-(WIDTH+SMALLWIDTH*2)+15, CENTERY-2*HEIGHT*2-5);
		portPoint1Locations[4]=new Point(CENTERX-(WIDTH+SMALLWIDTH*2),CENTERY-2*HEIGHT*2);
		portPoint2Locations[4]=new Point(CENTERX-WIDTH,CENTERY-2*HEIGHT*2);
		
		portTextLocations[5]=new Point(CENTERX+WIDTH+15, CENTERY-2*HEIGHT*2-5);
		portPoint1Locations[5]=new Point(CENTERX+WIDTH+SMALLWIDTH*2,CENTERY-2*HEIGHT*2);
		portPoint2Locations[5]=new Point(CENTERX+WIDTH,CENTERY-2*HEIGHT*2);
		
		portTextLocations[6]=new Point(CENTERX+(WIDTH*2+3*SMALLWIDTH)+23, CENTERY-5*HEIGHT/2+5);
		portPoint1Locations[6]=new Point(CENTERX+(3*WIDTH+SMALLWIDTH*2),CENTERY-HEIGHT*2);
		portPoint2Locations[6]=new Point(CENTERX+(WIDTH*2+3*SMALLWIDTH),CENTERY-3*HEIGHT);
		
		portTextLocations[7]=new Point(CENTERX+(WIDTH*2+3*SMALLWIDTH)+23, CENTERY+HEIGHT/2+15);
		portPoint1Locations[7]=new Point(CENTERX+(3*WIDTH+SMALLWIDTH*2),CENTERY);
		portPoint2Locations[7]=new Point(CENTERX+(WIDTH*2+3*SMALLWIDTH),CENTERY+HEIGHT);
		
		portTextLocations[8]=new Point(CENTERX+(WIDTH+SMALLWIDTH*2)+23, CENTERY+7*HEIGHT/2+15);
		portPoint1Locations[8]=new Point(CENTERX+(WIDTH+SMALLWIDTH*2),CENTERY+2*HEIGHT*2);
		portPoint2Locations[8]=new Point(CENTERX+(WIDTH*2+SMALLWIDTH),CENTERY+3*HEIGHT);
		
		for(int i=0; i<myPorts.length; i++){
			myPorts[i].setLocation(portTextLocations[i]);
			myPorts[i].setPortLocations(new DoublePoint(portPoint1Locations[i],portPoint2Locations[i]));
		}
	}

	// For some reason findHexIndex breaks if points are compared directly.
	public int findHexIndex(Point p){
		for(int i=0; i<myTiles.length; i++){
			if((int)myTiles[i].getLocation().getX()==p.getX()
					&&(int)myTiles[i].getLocation().getY()==p.getY())
				return i;
		}
		return -1;
	}
	
	public int findSettlementIndex(Point p){
		for(int i=0; i<mySettlements.length; i++)
			if(mySettlements[i]!=null && mySettlements[i].getLocation()==p)
				return i;
		return -1;
	}
	
	public int findCityIndex(Point p){
		for(int i=0; i<myCities.length; i++)
			if(myCities[i]!=null && myCities[i].getLocation()==p)
				return i;
		return -1;
	}
	
	public int findRoadIndex(Point p1, Point p2){
		if((int)p1.getX()>(int)p2.getX()){
			Point temp=p2;
			p2=p1;
			p1=temp;
		}
		for(int i=0; i<myRoads.length; i++){
			if(myRoads[i]==null)
				break;
			if(myRoads[i].getLocation1()==p1 && myRoads[i].getLocation2()==p2)
				return i;
		}
		return -1;
	}

	public boolean isAdjacent(Point p1, Point p2){
		if(Math.abs(p1.distance(p2)-SMALLWIDTH*2)<2)
			return true;
		else
			return false;
	}
	
	public Vector<Point> getAdjacentIntersections(Point p){
		Vector<Point> myPoints = new Vector<Point>();
		
		for(int i=0; i<hexIntersections.size(); i++)
			if(isAdjacent(p,hexIntersections.get(i)))
				myPoints.add(hexIntersections.get(i));
		return myPoints;
	}
	
	public Hexagon[] getAdjacentHexes(Point p){
		int x=(int)p.getX();
		int y=(int)p.getY();
		Hexagon[] myHexes = new Hexagon[6];
		Integer[] myInts = new Integer[6];
//		if(!isVisible)
//			System.out.println("Inside getAdjHex");
		
//		System.out.println("Point in getAdjHex: " + x+" "+y);
		myInts[0]=findHexIndex(new Point(x+WIDTH,y));
		myInts[1]=findHexIndex(new Point(x-WIDTH,y));
		myInts[2]=findHexIndex(new Point(x+SMALLWIDTH,y+HEIGHT));
		myInts[3]=findHexIndex(new Point(x-SMALLWIDTH,y+HEIGHT));
		myInts[4]=findHexIndex(new Point(x+SMALLWIDTH,y-HEIGHT));
		myInts[5]=findHexIndex(new Point(x-SMALLWIDTH,y-HEIGHT));
		
		for(int i=0; i<myInts.length; i++){
			if(myInts[i]!=-1)
				myHexes[i]=myTiles[myInts[i]];
			else
				myHexes[i]=null;
		}
		return myHexes;
	}
	
	public int getHexValue(Point p){
		Hexagon[] adjacentHexes=new Hexagon[6];
		int hexValue=0;
		adjacentHexes=getAdjacentHexes(p);
		for(int i=0; i<adjacentHexes.length; i++){
			if(adjacentHexes[i]!=null)
				hexValue+=adjacentHexes[i].getValue();
		}
		return hexValue;
	}
	
	// This function checks if either an intersection contains a settlement 
	// or city, or one of its neighbors contains a settlement or city.
	public boolean hasNoNeighbors(Point p){
		boolean isHexLegal=true;
		if(findSettlementIndex(p)!=-1 || findCityIndex(p)!=-1)
			isHexLegal=false;
		Vector<Point> checkPoints=getAdjacentIntersections(p);
		for(int j=0;j<checkPoints.size();j++)
			if(findSettlementIndex(checkPoints.get(j))!=-1 || findCityIndex(checkPoints.get(j))!=-1)
				isHexLegal=false;
		return isHexLegal;
	}
	
	public Vector<Point> getSettleLocations(Color c){
		Vector<Point> playerRoadPoints=new Vector<Point>();
		Vector<Point> settlePoints=new Vector<Point>();
		
		for(int i=0; i<myRoads.length; i++){
			if(myRoads[i]==null)
				break;
			if(myRoads[i].getColor()==c){
				playerRoadPoints.add(myRoads[i].getLocation1());
				playerRoadPoints.add(myRoads[i].getLocation2());
			}
		}
		removeDuplicates(playerRoadPoints);		
		
		for(int i=0; i<playerRoadPoints.size(); i++)
			if(hasNoNeighbors(playerRoadPoints.get(i)))
				settlePoints.add(playerRoadPoints.get(i));
		
		return settlePoints;
	}
	
	// 1. Determine all vertices of roads of a specified color (removing duplicates)
	// 2. For each vertex above, determine all adjacent vertices, and check if there is
	// a road with that pair of vertices.  If not, add the pair of points to a vector.
	// 3. Somehow delete matches with start and end locations swapped.
	// 4. Return the vector of road start and end locations.
	public Vector<DoublePoint> getRoadLocations(Color c){
		Vector<Point> playerRoadPoints=new Vector<Point>();
		Vector<Point> neighbors=new Vector<Point>();
		Vector<DoublePoint> roadLocations=new Vector<DoublePoint>();
		Vector<DoublePoint> bestLocations=new Vector<DoublePoint>();
		Vector<DoublePoint> okayLocations=new Vector<DoublePoint>();
		
		for(int i=0; i<myRoads.length; i++){
			if(myRoads[i]==null)
				break;
			if(myRoads[i].getColor()==c){
				playerRoadPoints.add(myRoads[i].getLocation1());
				playerRoadPoints.add(myRoads[i].getLocation2());
			}
		}
		removeDuplicates(playerRoadPoints);
		
		for(int i=0; i<playerRoadPoints.size(); i++){
			// Ignore if there is a different player's settlement or city on the start location of the road
			int index1=findSettlementIndex(playerRoadPoints.get(i));
			int index2=findCityIndex(playerRoadPoints.get(i));
			if((index1!=-1 && mySettlements[index1].getColor()!=c)
					||(index2!=-1 && myCities[index2].getColor()!=c))
				break;
			
			neighbors=getAdjacentIntersections(playerRoadPoints.get(i));
			for(int j=0; j<neighbors.size(); j++){
				if(findRoadIndex(playerRoadPoints.get(i),neighbors.get(j))==-1){
					roadLocations.add(new DoublePoint(playerRoadPoints.get(i),neighbors.get(j)));
				}
			}
		}
		
		removeDuplicates2(roadLocations);
		for(int i=0; i<roadLocations.size(); i++){
			if(hasNoNeighbors(roadLocations.get(i).getP2())){
				bestLocations.add(roadLocations.get(i));
			}
		}
		if(bestLocations.size()>0)
			return bestLocations;
		else{
			for(int i=0; i<roadLocations.size(); i++){
				Vector<Point> checkPoints=getAdjacentIntersections(roadLocations.get(i).getP2());
				for(int j=0; j<checkPoints.size(); j++){
					if(hasNoNeighbors(checkPoints.get(j))){
						okayLocations.add(roadLocations.get(i));
						break;
					}
				}
			}
			if(okayLocations.size()>0)
				return okayLocations;
		}
		return roadLocations;
	}
	
	// Get all hex locations adjacent to playerToRob's settlements and cities
	public Point getPointToBlock(int playerToRob, int robbingPlayer){
//		if(!isVisible)
//			System.out.println("GPTB: "+playerToRob+" "+robbingPlayer);
		Hexagon[] neighbors=new Hexagon[6];
		Vector<Point> checkPoints;
		Vector<Hexagon> tempHexes=new Vector<Hexagon>();
		Vector<Hexagon> possibleBlockedHexes;
		int owner,value,maxValue,maxIndex,index1,index2;
		for(int i=0; i<mySettlements.length; i++){
			if(mySettlements[i]==null)
				break;
//			if(!isVisible)
//				System.out.println("Examining a possible settlement to block");
			if(mySettlements[i].getPlayerNum()!=playerToRob)
				continue;
//			if(!isVisible)
//				System.out.println("Examining a possible settlement owned by the correct player");
			neighbors=getAdjacentHexes(mySettlements[i].getLocation());
			for(int j=0; j<neighbors.length; j++)
				if(neighbors[j]!=null){
					tempHexes.add(neighbors[j]);
//					if(!isVisible)
//						System.out.println("Found a neighbor of a settlement");
				}
		}
		for(int i=0; i<myCities.length; i++){
			if(myCities[i]==null)
				break;
//			if(!isVisible)
//				System.out.println("Examining a possible city to block");
			if(myCities[i].getPlayerNum()!=playerToRob)
				continue;
			neighbors=getAdjacentHexes(myCities[i].getLocation());
			for(int j=0; j<neighbors.length; j++)
				if(neighbors[j]!=null){
					tempHexes.add(neighbors[j]);
//					if(!isVisible)
//						System.out.println("Found a neighbor of a city");
				}
		}
		
		possibleBlockedHexes=removeDuplicates3(tempHexes);
		maxIndex=-1;
		maxValue=-10;
//		if(isVisible==false)
//			System.out.println("Number of possible blocked hexes:"+possibleBlockedHexes.size());
		for(int i=0; i<possibleBlockedHexes.size(); i++){
			value=0;
			checkPoints=getAdjacentIntersections(possibleBlockedHexes.get(i).getLocation());
			for(int j=0; j<checkPoints.size(); j++){
				index1=findSettlementIndex(checkPoints.get(j));
				index2=findCityIndex(checkPoints.get(j));
				if(index1!=-1){
					owner=mySettlements[index1].getPlayerNum();
					if(owner!=robbingPlayer)
						value+=1;
					else
						value-=1;
				}
				if(index2!=-1){
					owner=myCities[index2].getPlayerNum();
					if(owner!=robbingPlayer)
						value+=2;
					else
						value-=2;
				}
			}
			value*=possibleBlockedHexes.get(i).getValue();
			if(value>maxValue){
				maxIndex=i;
				maxValue=value;
			}
		}

		return possibleBlockedHexes.get(maxIndex).getLocation();
	}
	
	public static Vector<Point> removeDuplicates(Vector<Point> s) {
		boolean duplicates = false;
		Vector<Point> v = new Vector<Point>();
		for (int i=0; i<s.size(); i++) {
			duplicates = false;
			for (int j = (i+1); j < s.size(); j++)
				if(s.get(i).equals(s.get(j)))
					duplicates = true;
			if (duplicates == false)
				v.add(s.get(i));
		}	    
		return v;
	}
	public static Vector<DoublePoint> removeDuplicates2(Vector<DoublePoint> s) {
		boolean duplicates = false;
		Vector<DoublePoint> v = new Vector<DoublePoint>();
		for (int i=0; i<s.size(); i++) {
			duplicates = false;
			for (int j = (i+1); j < s.size(); j++)
				if(s.get(i).equals(s.get(j)))
					duplicates = true;
			if (duplicates == false)
				v.add(s.get(i));
		}	    
		return v;
	}
	public static Vector<Hexagon> removeDuplicates3(Vector<Hexagon> s) {
		boolean duplicates = false;
		Vector<Hexagon> v = new Vector<Hexagon>();
		for (int i=0; i<s.size(); i++) {
			duplicates = false;
			for (int j = (i+1); j < s.size(); j++)
				if(s.get(i).equals(s.get(j)))
					duplicates = true;
			if (duplicates == false)
				v.add(s.get(i));
		}	    
		return v;
	}
	
	//TODO: These are only temporary
	public int getNumSettlements(){
		for(int i=0;i<mySettlements.length;i++)
			if(mySettlements[i]==null)
				return i;
		return mySettlements.length;
	}
	public int getNumCities(){
		for(int i=0;i<myCities.length;i++)
			if(myCities[i]==null)
				return i;
		return myCities.length;
	}
	public int getNumRoads(){
		for(int i=0;i<myRoads.length;i++)
			if(myRoads[i]==null)
				return i;
		return myRoads.length;
	}
}
