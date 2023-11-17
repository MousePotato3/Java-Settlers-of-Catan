package catan2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JFrame;

public class Simulation extends JFrame
{
	private static final long serialVersionUID = 1L;
	public static final int NUM_PLAYOUTS=1;
	public static final int MAX_RESOURCES=7;
	private Board board;
	private Player[] players=new Player[4];
	private Hexagon[] neighbors=new Hexagon[6];
	private int[][] resources=new int[4][5];
	private int[] numPoints=new int[4];
	private int numPlayers;
	private int playerToMove;
	private int turn=0;
	
	public Simulation(Board b,int[][] playerResources,int[] playerNumPoints, int tempPlayerToMove) {
		super();
		board=b.deepCopy();
		Container c=getContentPane();	
		c.add(board);
		c.setLayout(new BorderLayout());
		c.add(board, BorderLayout.CENTER);
		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		setSize(dim);
		
		//TODO: Set these to false once everything is working
		this.setVisible(false);
		board.setVisible(false);
		for(int i=0; i<resources.length; i++)
			for(int j=0; j<resources[i].length; j++)
				resources[i][j]=playerResources[i][j];
		for(int i=0; i<numPoints.length; i++)
			numPoints[i]=playerNumPoints[i];
		playerToMove=tempPlayerToMove;
	}

	public int runPlayouts(int montePlayer) {
		Board startBoard = board.deepCopy();
		int value=0;
		players[0]=new RandComp(board, Color.red, 1);
		players[1]=new RandComp(board, Color.blue, 2);
		players[2]=new RandComp(board, Color.white, 3);
		players[3]=new RandComp(board, Color.orange, 4);
		numPlayers=players.length;
		
		//TODO: Remove this once everything is working
		if(board.isVisible()){
			for(int j=0; j<numPlayers; j++){
				playerToMove=j+1;
				Point infoPoint;
				if(playerToMove==1)
					infoPoint=new Point(100,183);
				else if(playerToMove==2)
					infoPoint=new Point(1266,183);
				else if(playerToMove==3)
					infoPoint=new Point(100,549);
				else
					infoPoint=new Point(1266,549);
				//board.addPlayerInfo(infoPoint, players[i].getColor(),playerToMove);
				players[j].setTextCenter(infoPoint);
			}
			board.setPlayers(players);
		}		
//		System.out.println(board.getNumSettlements()+" "+board.getNumCities()+" "+board.getNumRoads());
//		initPlacement();
		
		// This is the code I would like to run in parallel
		for(int i=0;i<NUM_PLAYOUTS;i++){
			if(i<5) System.out.println("Running simulation "+(i+1)+" for player "+montePlayer);
			int winner=-1;
			board=startBoard.deepCopy();
			board.setPlayers(players);
			for(int j=0;j<numPlayers;j++){				
				players[j].resetData();
				for(int k=0;k<5;k++){
					if(i<5)
						System.out.println("Player "+(j+1)+" resource "+k+" is being set to "+resources[j][k]);
				}
				players[j].setResources(resources[j]);
				if(i<5) System.out.println("Setting player "+(j+1)+"'s points to "+numPoints[j]);
				players[j].setPoints(numPoints[j]);
			}
			Settlement[] newSettlements=board.getSettlements();
			for(int j=0; j<newSettlements.length; j++){
				if(newSettlements[j]==null)
					break;
				int settlePlayer=newSettlements[j].getPlayerNum();
				players[settlePlayer].addSettlement(newSettlements[j].getLocation(), players[settlePlayer].getColor(), settlePlayer);
			}
			City[] newCities=board.getCities();
			for(int j=0; j<newCities.length; j++){
				if(newCities[j]==null)
					break;
				int cityPlayer=newCities[j].getPlayerNum();
				players[cityPlayer].addCity(newCities[j].getLocation(), players[cityPlayer].getColor(), cityPlayer);
			}
			Road[] newRoads=board.getRoads();
			for(int j=0; j<newRoads.length; j++){
				if(newRoads[j]==null)
					break;
				int roadPlayer=newRoads[j].getPlayerNum();
				players[roadPlayer].addRoad(newRoads[j].getLocation1(), newRoads[j].getLocation2(), players[roadPlayer].getColor(), roadPlayer);
			}			
			while(winner==-1)
				winner=takeTurn();
			if(winner==montePlayer)
				value++;
		}
		System.out.println("The number of wins by player "+montePlayer+" was "+value);
		return value;
	}

	public void initPlacement() {		
		for(int i=0; i<numPlayers; i++){
			playerToMove=i+1;
			board.setPlayers(players);
			Point p1=players[i].placeFirstSettlement();
			players[i].addSettlement(p1,players[i].getColor(),i);
			board.addSettlement(p1, players[i].getColor(),i);
			String newPortType=board.getPortType(p1);
			if(newPortType!="")
				players[i].gainPortPower(newPortType);
			Point p2=players[i].placeFirstRoad(p1);
			players[i].addRoad(p1, p2, players[i].getColor(),i);
			board.addRoad(p1, p2, players[i].getColor(),i);
		}
		
		for(int i=0; i<numPlayers; i++){
			playerToMove=numPlayers-i;
			Point p1=players[numPlayers-i-1].placeSecondSettlement();
			players[numPlayers-i-1].addSettlement(p1,players[numPlayers-i-1].getColor(),numPlayers-i-1);
			board.addSettlement(p1, players[numPlayers-i-1].getColor(),numPlayers-i-1);
			String newPortType=board.getPortType(p1);
			if(newPortType!="")
				players[numPlayers-i-1].gainPortPower(newPortType);
			Point p2=players[numPlayers-i-1].placeSecondRoad(p1);
			neighbors=board.getAdjacentHexes(p1);
			for(int j=0; j<neighbors.length; j++)
				if(neighbors[j]!=null)
					players[numPlayers-i-1].addResource(neighbors[j].getType());
			players[i].addRoad(p1, p2, players[numPlayers-i-1].getColor(),numPlayers-i-1);
			board.addRoad(p1, p2, players[numPlayers-i-1].getColor(),numPlayers-i-1);
		}
	}
	
	public void collectResources(){
		// Eventually players should have an option to play soldier cards before rolling dice
		int diceroll=board.rollDice();

		if(diceroll==7){
			// Discard
			for(int i=0; i<players.length; i++)
				if(players[i].getTotalResources()>MAX_RESOURCES)
					players[i].discard(turn);
			
			// Move robber
			int maxPoints=0;
			int playerToRob=-1;
			for(int i=0; i<players.length; i++){
				if(i!=(playerToMove-1) && players[i].getPoints()>maxPoints){
					maxPoints=players[i].getPoints();
					playerToRob=i;
				}
			}	
			Point robberLoc=board.getPointToBlock(playerToRob, playerToMove-1);
			board.setRobberLocation(robberLoc);
			
			int resourceNum=players[playerToRob].getResourceToSteal();
			if(resourceNum!=-1){
				players[playerToMove-1].gainResource(resourceNum);
				players[playerToRob].loseResource(resourceNum);
			}
		}
		else{
			for(int i=0; i<numPlayers; i++){
				for(int j=0; j<players[i].getSettlements().length; j++){
					if(players[i].getSettlements()[j]==null)
						break;
					neighbors=board.getAdjacentHexes(players[i].getSettlements()[j].getLocation());
					for(int k=0; k<neighbors.length; k++){
						if(neighbors[k]!=null && neighbors[k].getNumber()==diceroll)
							players[i].addResource(neighbors[k].getType());
					}
				}
				for(int j=0; j<players[i].getCities().length; j++){
					if(players[i].getCities()[j]==null)
						break;
					neighbors=board.getAdjacentHexes(players[i].getCities()[j].getLocation());
					for(int k=0; k<neighbors.length; k++){
						if(neighbors[k]!=null && neighbors[k].getNumber()==diceroll
							&& !(neighbors[k].getLocation().getX()==board.getRobberLocation().getX()
							&& neighbors[k].getLocation().getY()==board.getRobberLocation().getY())){						
							players[i].addResource(neighbors[k].getType());
							players[i].addResource(neighbors[k].getType());
						}						
					}
				}
			}
		}
	}
	
	public int takeTurn(){
		Vector<Point> cityPoints=new Vector<Point>();
		Vector<DoublePoint> possibleRoadPoints=new Vector<DoublePoint>();
		Vector<DoublePoint> roadPoints=new Vector<DoublePoint>();
		Vector<Point> possibleSettlePoints=new Vector<Point>();
		Vector<Point> settlePoints=new Vector<Point>();

		collectResources();
		possibleRoadPoints=board.getRoadLocations(players[playerToMove-1].getColor());
		possibleSettlePoints=board.getSettleLocations(players[playerToMove-1].getColor());
		int winner=players[playerToMove-1].takeTurn(possibleRoadPoints,possibleSettlePoints,resources,numPoints,turn);
		
		cityPoints=players[playerToMove-1].getCityPoints();
		for(int i=0; i<cityPoints.size(); i++)
			board.addCity(cityPoints.get(i), players[playerToMove-1].getColor(), playerToMove-1);

		settlePoints=players[playerToMove-1].getSettlePoints();
		for(int i=0; i<settlePoints.size(); i++){
			board.addSettlement(settlePoints.get(i), players[playerToMove-1].getColor(), playerToMove-1);
			String newPortType=board.getPortType(settlePoints.get(i));
			if(newPortType!="")
				players[playerToMove-1].gainPortPower(newPortType);
		}

		roadPoints=players[playerToMove-1].getRoadPoints();
		for(int i=0; i<roadPoints.size(); i++)
			board.addRoad(roadPoints.get(i).getP1(),roadPoints.get(i).getP2(), players[playerToMove-1].getColor(), playerToMove-1);
			
		if(playerToMove==numPlayers){
			playerToMove=1;
			turn++;
		}
		else
			playerToMove++;
		return winner;
	}
}
