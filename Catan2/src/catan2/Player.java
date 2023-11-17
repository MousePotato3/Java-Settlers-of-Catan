package catan2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;
import java.util.Vector;

abstract class Player extends Component {	
	private static final long serialVersionUID = 1L;
	public static final Random generator=new Random();
	public static final int POINTS_TO_WIN=10;
	public static final int MAX_SETTLEMENTS=5;
	public static final int MAX_CITIES=4;
	public static final int MAX_ROADS=15;
	public static final int NUM_PLAYERS=4;
	
	protected Board board;
	protected Settlement[] settlements = new Settlement[MAX_SETTLEMENTS]; 
    protected City[] cities = new City[MAX_CITIES];
	protected Road[] roads = new Road[MAX_ROADS];
	protected Vector<Point> newCityPoints = new Vector<Point>();
	protected Vector<Point> newSettlePoints = new Vector<Point>();
	protected Vector<DoublePoint> newRoadPoints = new Vector<DoublePoint>();
	protected int[] resources = {0,0,0,0,0};
	protected int[] tradeRates = {4,4,4,4,4};
//  protected Vector<DevCard> devCards = new Vector<DevCard>();
	protected Color color;
	protected int playerNum;
	protected Point textCenter;
	protected int points=0;
	
	public Player(){}
	// Just copy the data needed to draw the player
	public Player(Player p){
		this.textCenter=p.textCenter;
		this.color=p.color;
		this.resources=p.resources.clone();
	}
	
	public int getPoints(){
		return points;
	}
	
	public Color getColor(){
		return color;
	}
	
	public City[] getCities(){
		return cities;
	}
	
	public Settlement[] getSettlements(){
		return settlements;
	}
	
	public Road[] getRoads(){
		return roads;
	}
	
	public Vector<Point> getCityPoints(){
		return newCityPoints;
	}
	
	public Vector<Point> getSettlePoints(){
		return newSettlePoints;
	}
	
	public Vector<DoublePoint> getRoadPoints(){
		return newRoadPoints;
	}
	// These might be removed later in favor of random generation of resources
	public int[] getResources(){
		return resources;
	}
	
	public void setPoints(int newPoints){
		points=newPoints;
	}
	
	public void setResources(int newResources[]){
		for(int i=0;i<newResources.length;i++)
			resources[i]=newResources[i];
	}
	
	public Point getTextCenter(){
		return textCenter;
	}
	
	public void setTextCenter(Point tc){
		textCenter=tc;
	}
	
	//This function will be needed for Monte Carlo; I am surprised it isn't needed sooner.
/*	public void resetBoard(Board b){
		board=b;
	}*/
	public void resetData(){
		for(int i=0;i<settlements.length;i++)
			settlements[i]=null;
		for(int i=0;i<cities.length;i++)
			cities[i]=null;
		for(int i=0;i<roads.length;i++)
			roads[i]=null;
	}
	
	public void gainResource(int i){
		resources[i]++;
	}
	public void loseResource(int i){
		resources[i]--;
	}
	
	public String getResourceType(int i){
		switch(i){
			case 0: return "ore";
			case 1: return "wheat";
			case 2: return "sheep";
			case 3: return "clay";
			case 4: return "wood";
			default: return null;
		}
	}
	
	public void addSettlement(Point p, Color c, int k){
//		System.out.println("New settlement location: "+p.getX()+" "+p.getY());
		//mySettlements.add(new Settlement(p,c));
		int index=-1;
		for(int i=0; i<settlements.length; i++) {
			if(settlements[i]==null){
				index=i;
				break;
			}
		}
		
		if(index==-1){
			System.err.println("Error adding settlement to array");
			return;
		}
		settlements[index]=new Settlement(p,c,k);
	}
/*	
	public void addSettlement(Point p, Color c){
		settlements.add(new Settlement(p,c));
	}
*/
	//For simulations only
	public void addCity(Point p,Color c,int k){
		int index1=-1;
		for(int i=0; i<cities.length; i++) {
			if(cities[i]==null){
				index1=i;
				break;
			}
		}
		if(index1==-1){
			System.err.println("Error adding city to array");
			return;
		}
		cities[index1]=new City(p,c,k);
	}
	
	public void addCity(Point p,Color c,int settlementIndex,int k){
		int index1=-1;
		for(int i=0; i<cities.length; i++) {
			if(cities[i]==null){
				index1=i;
				break;
			}
		}
		int index2=settlements.length;
		for(int i=0; i<settlements.length; i++) {
			if(settlements[i]==null){
				index2=i;
				break;
			}
		}
//		System.out.println("New city location: "+p.getX()+" "+p.getY());
		//myCities.add(new City(p,c));
		if(index1==-1){
			System.err.println("Error adding city to array");
			return;
		}
		cities[index1]=new City(p,c,k);
		//mySettlements.remove(findSettlementIndex(p));
		settlements[settlementIndex]=settlements[index2-1];
		settlements[index2-1]=null;
	}
	
	public void addRoad(Point p1, Point p2, Color c, int n){
		int index=-1;
		for(int i=0; i<roads.length; i++) {
			if(roads[i]==null){
				index=i;
				break;
			}
		}
//		System.out.println("New road location: "+p2.getX()+" "+p2.getY());
		if(index==-1){
			System.err.println("Error adding road to array");
			return;
		}
		if((int)p1.getX()<(int)p2.getX())
			roads[index]=new Road(p1,p2,c,n);
		else
			roads[index]=new Road(p2,p1,c,n);
	}

/*	public void addRoad(Point p1, Point p2, Color c){
		if((int)p1.getX()<(int)p2.getX())
			roads.add(new Road(p1,p2,c));
		else
			roads.add(new Road(p2,p1,c));
	}
*/
	
	public void draw(Graphics g) {
		int x=(int)textCenter.getX()-100;
		int y=(int)textCenter.getY()-183;
		
		g.setColor(color.brighter());
		g.fillRect(x,y,200,366);
		
		g.setColor(Color.black);
		Font header=new Font("Veranda", Font.BOLD,32);
 		g.setFont(header);
        g.drawString("Player "+Integer.toString(playerNum),x+36,y+30);
        
        Font normal=new Font("Veranda", Font.PLAIN,20);
        g.setFont(normal);
        g.drawString("Points: "+Integer.toString(points),x+36,y+60);
        g.drawString("Clay: "+Integer.toString(resources[3]),x+36,y+85);
        g.drawString("Ore: "+Integer.toString(resources[0]),x+36,y+110);
        g.drawString("Sheep: "+Integer.toString(resources[2]),x+36,y+135);
        g.drawString("Wheat: "+Integer.toString(resources[1]),x+36,y+160);
        g.drawString("Wood: "+Integer.toString(resources[4]),x+36,y+185);
	}

	public void addResource(String t){
//		System.out.println("Player "+ playerNum + " got resource: " + t);
		if(t=="ore")
			resources[0]++;
		else if(t=="wheat")
			resources[1]++;
		else if(t=="sheep")
			resources[2]++;
		else if(t=="clay")
			resources[3]++;
		else if(t=="wood")
			resources[4]++;
	}
	
	public int getTotalResources(){
		int totalResources=0;
		for(int i=0; i<resources.length; i++)
			totalResources+=resources[i];
		return totalResources;
	}
	
	public int getTotalSettlements(){
		for(int i=0; i<settlements.length; i++){
			if(settlements[i]==null)
				return i;
		}
		return settlements.length;
	}
	
	public void gainPortPower(String t){
		if(t=="general"){
			for(int i=0; i<tradeRates.length; i++)
				if(tradeRates[i]==4)
					tradeRates[i]=3;
		}
		else if(t=="ore")
			tradeRates[0]=2;
		else if(t=="wheat")
			tradeRates[1]=2;
		else if(t=="sheep")
			tradeRates[2]=2;
		else if(t=="clay")
			tradeRates[3]=2;
		else if(t=="wood")
			tradeRates[4]=2;		
	}

	public int getResourceToSteal(){
		int total=getTotalResources();
		// TODO: Total should never be negative, but perhaps it is on rare occasions?
		if(total<0)
			System.out.println("ERROR: Player "+playerNum+" had a negative resource total of "+total);
		if(total==0)
			return -1;
		int resourceNum=generator.nextInt(total);
		if(0<=resourceNum && resourceNum<resources[0])
			return 0;
		else if(resources[0]<=resourceNum && resourceNum<resources[0]+resources[1])
			return 1;
		else if(resources[0]+resources[1]<=resourceNum && resourceNum<resources[0]+resources[1]+resources[2])
			return 2;
		else if(total-resources[4]<=resourceNum && resourceNum<total)
			return 4;
		else
			return 3;
	}
	
	//To be overridden by Human or Computer class
	public abstract Point placeFirstSettlement();
	public abstract Point placeFirstRoad(Point p);
	public abstract Point placeSecondSettlement();
	public abstract Point placeSecondRoad(Point p);	
	public abstract void discard(int turn);
	public abstract int takeTurn(Vector<DoublePoint> roadPoints, Vector<Point> settlePoints, int[][] playerResources, int[] playerNumPoints, int turn);
}
