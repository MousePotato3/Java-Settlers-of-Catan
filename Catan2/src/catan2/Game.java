package catan2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JFrame;

public class Game extends JFrame
{
	private static final long serialVersionUID = 1L;
	public static final int MAX_RESOURCES=7;
	private Board board;
	private Player[] players=new Player[4];
	private Hexagon[] neighbors=new Hexagon[6];
	private int numPlayers;
	private int playerToMove;
	private int turn=0;
	
	public Game() {
		super();
		board=new Board();
		Container c=getContentPane();
		c.add(board);
		c.setLayout(new BorderLayout());
		c.add(board, BorderLayout.CENTER);
		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		setSize(dim);
		this.setVisible(true);
	}

	public void play() {
		int winner=-1;
		board.initTiles();
		board.initPorts();	
		players[0]=new RandComp(board, Color.red, 1);
		players[1]=new RandComp(board, Color.blue, 2);
		players[2]=new RandComp(board, Color.white, 3);
		players[3]=new RandComp(board, Color.orange, 4);
		numPlayers=players.length;
		initPlacement();
		while(winner==-1)
			winner=takeTurn();
		System.out.println("Player "+winner+" won with "+players[winner-1].getPoints()+" points at the end of turn "+turn+"!");
	}

	public void initPlacement() {
		for(int i=0; i<numPlayers; i++){
			playerToMove=i+1;
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
			players[i].setTextCenter(infoPoint);
			board.setPlayers(players);
			Point p1=players[i].placeFirstSettlement();
			players[i].addSettlement(p1,players[i].getColor(),i);
			board.addSettlement(p1, players[i].getColor(),i);
			System.out.println("Player " + playerToMove+ " placed its first settlement at "
					+ p1.getX()+ " "+p1.getY());
			String newPortType=board.getPortType(p1);
			if(newPortType!=""){
				players[i].gainPortPower(newPortType);
				System.out.println("Player "+playerToMove+" just acquired a "+newPortType+ " port!");
			}
			Point p2=players[i].placeFirstRoad(p1);
			players[i].addRoad(p1, p2, players[i].getColor(),i);
			board.addRoad(p1, p2, players[i].getColor(),i);
		}
		
		for(int i=0; i<numPlayers; i++){
			playerToMove=numPlayers-i;
			Point p1=players[numPlayers-i-1].placeSecondSettlement();
			players[numPlayers-i-1].addSettlement(p1,players[numPlayers-i-1].getColor(),numPlayers-i-1);
			board.addSettlement(p1, players[numPlayers-i-1].getColor(),numPlayers-i-1);
			System.out.println("Player " + playerToMove+ " placed its second settlement at "
					+ p1.getX()+ " "+p1.getY());
			String newPortType=board.getPortType(p1);
			if(newPortType!=""){
				players[numPlayers-i-1].gainPortPower(newPortType);
				System.out.println("Player "+playerToMove+" just acquired a "+newPortType+ " port!");
			}
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
		int[][] playerResources=new int[4][5];
		int[] playerNumPoints=new int[4];

		collectResources();
		for(int i=0;i<numPlayers;i++)
			playerResources[i]=players[i].getResources();
		for(int i=0;i<numPlayers;i++)
			playerNumPoints[i]=players[i].getPoints();
		
		possibleRoadPoints=board.getRoadLocations(players[playerToMove-1].getColor());
		possibleSettlePoints=board.getSettleLocations(players[playerToMove-1].getColor());
		int winner=players[playerToMove-1].takeTurn(possibleRoadPoints,possibleSettlePoints,playerResources,playerNumPoints,turn);
		
		cityPoints=players[playerToMove-1].getCityPoints();
		for(int i=0; i<cityPoints.size(); i++){
			System.out.println("Player "+playerToMove+" scored a point on turn "+turn+" by placing " +
					"a city at "+cityPoints.get(i).getX()+" "+cityPoints.get(i).getY()+"!");
			board.addCity(cityPoints.get(i), players[playerToMove-1].getColor(), playerToMove-1);
		}

		settlePoints=players[playerToMove-1].getSettlePoints();
		for(int i=0; i<settlePoints.size(); i++){
			System.out.println("Player "+playerToMove+" scored a point on turn "+turn+" by placing " +
					"a settlement at "+ settlePoints.get(i).getX()+" "+settlePoints.get(i).getY()+"!");
			board.addSettlement(settlePoints.get(i), players[playerToMove-1].getColor(), playerToMove-1);
			String newPortType=board.getPortType(settlePoints.get(i));
			if(newPortType!=""){
				players[playerToMove-1].gainPortPower(newPortType);
				System.out.println("Player "+playerToMove+" just acquired a "+newPortType+ " port on turn "+turn+"!");
			}
		}

		roadPoints=players[playerToMove-1].getRoadPoints();
		for(int i=0; i<roadPoints.size(); i++){
			board.addRoad(roadPoints.get(i).getP1(),roadPoints.get(i).getP2(), players[playerToMove-1].getColor(), playerToMove-1);
			System.out.println("Player "+playerToMove+" just placed a road between "+roadPoints.get(i).getP1().getX()
					+" "+roadPoints.get(i).getP1().getY()+" and "+roadPoints.get(i).getP2().getX()
					+" "+roadPoints.get(i).getP2().getY()+" on turn "+turn);
		}
			
		if(playerToMove==numPlayers){
			playerToMove=1;
			turn++;
		}
		else
			playerToMove++;
		return winner;
	}
}
