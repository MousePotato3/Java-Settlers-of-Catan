package catan2;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

public class MonteComp extends Player {
	private static final long serialVersionUID = 1L;
	private Vector<Point> hexIntersections=new Vector<Point>();
	
	public MonteComp(Board startBoard, Color c, int n){
		board=startBoard;
		color=c;
		playerNum=n;
	}

	public Point placeFirstSettlement(){
		Hexagon[] adjacentHexes=new Hexagon[6];
		int hexValue;
		int maxValue=-1;
		int maxIndex=-1;

		hexIntersections=board.getHexIntersections();
		
		for(int i=0; i<hexIntersections.size(); i++){
			hexValue=0;
			adjacentHexes=board.getAdjacentHexes(hexIntersections.get(i));
			//System.out.println("Number of adjacent hexes: "+ adjacentHexes.length);
			for(int j=0; j<adjacentHexes.length; j++)
				if(adjacentHexes[j]!=null)
					hexValue+=adjacentHexes[j].getValue();
			if(hexValue>maxValue)
				if(board.hasNoNeighbors(hexIntersections.get(i))){
					maxValue=hexValue;
					maxIndex=i;
				}
		}
		points++;
		return hexIntersections.get(maxIndex);
	}
	public Point placeFirstRoad(Point p){
		Vector<Point> possibleRoadPoints=board.getAdjacentIntersections(p);
		int roadNum=generator.nextInt(possibleRoadPoints.size());
		return possibleRoadPoints.get(roadNum);
	}
	
	// Currently second placement is no different from first placement; to be improved later.
	public Point placeSecondSettlement(){
		return placeFirstSettlement();
	}
	public Point placeSecondRoad(Point p){
		return placeFirstRoad(p);
	}
	
	public void discard(int turn){
		int numberToDiscard=getTotalResources()/2;
		System.out.println("Player "+playerNum+" discarded "+numberToDiscard+" resources on turn "+turn);
		for(int i=0; i<numberToDiscard; i++){
			int maxIndex=0;
			int maxResources=resources[0];
			for(int j=1; j<resources.length;j++){
				if(resources[j]>=maxResources){
					maxIndex=j;
					maxResources=resources[j];
				}
			}
			resources[maxIndex]--;
		}
	}
	
	public Vector<Integer> canPlaceCity(){
		Vector<Integer> resourcesToTrade=new Vector<Integer>();
		if(settlements[0]==null||cities[MAX_CITIES-1]!=null)
			return null;
		int[] tempResources = (int[])resources.clone();
		int maxRemainingResources;
		do{
			if(tempResources[0]>=3 && tempResources[1]>=2)
				return resourcesToTrade;
			int maxIndex=0;
			maxRemainingResources=tempResources[0]-3-tradeRates[0];
			if(tempResources[1]-2-tradeRates[1] >= maxRemainingResources){
				maxIndex=1;
				maxRemainingResources=tempResources[1]-2-tradeRates[1];
			}
			for(int i=2; i<tempResources.length; i++){
				if(tempResources[i]-tradeRates[i] >= maxRemainingResources){
					maxIndex=i;
					maxRemainingResources=tempResources[i]-tradeRates[i];
				}
			}
			if(maxRemainingResources>=0){
				if(tempResources[0]<3)
					tempResources[0]++;
				else
					tempResources[1]++;
				resourcesToTrade.add(maxIndex);
				tempResources[maxIndex]-=tradeRates[maxIndex];
			}
		} while(maxRemainingResources>=0);
		return null;
	}

	public Vector<Integer> canPlaceSettlement(){
		Vector<Integer> resourcesToTrade=new Vector<Integer>();
		if(settlements[MAX_SETTLEMENTS-1]!=null)
			return null;
		int[] tempResources = (int[])resources.clone();
		int maxRemainingResources;
		do{
			if(tempResources[1]>0 && tempResources[2]>0 && tempResources[3]>0 && tempResources[4]>0)
				return resourcesToTrade;
			int maxIndex=0;
			maxRemainingResources=tempResources[0]-tradeRates[0];
			for(int i=1; i<tempResources.length; i++){
				if(tempResources[i]-1-tradeRates[i]>=maxRemainingResources){
					maxIndex=i;
					maxRemainingResources=tempResources[i]-1-tradeRates[i];
				}
			}
			if(maxRemainingResources>=0){
				if(tempResources[1]==0)
					tempResources[1]++;
				else if(tempResources[2]==0)
					tempResources[2]++;
				else if(tempResources[3]==0)
					tempResources[3]++;
				else if(tempResources[4]==0)
					tempResources[4]++;
				resourcesToTrade.add(maxIndex);
				tempResources[maxIndex]-=tradeRates[maxIndex];
			}
		} while(maxRemainingResources>=0);
		return null;
	}	
	
	public Vector<Integer> canPlaceRoad(){
		Vector<Integer> resourcesToTrade=new Vector<Integer>();
		if(roads[MAX_ROADS-1]!=null)
			return null;
		int[] tempResources = (int[])resources.clone();
		int maxRemainingResources;
		do{
			if(tempResources[3]>0 && tempResources[4]>0)
				return resourcesToTrade;
			int maxIndex=0;
			maxRemainingResources=tempResources[0]-tradeRates[0];
			for(int i=1; i<tempResources.length; i++){
				if(tempResources[i]-1-tradeRates[i]>=maxRemainingResources){
					maxIndex=i;
					maxRemainingResources=tempResources[i]-1-tradeRates[i];
				}
			}
			if(maxRemainingResources>=0){
				if(tempResources[3]==0)
					tempResources[3]++;
				else if(tempResources[4]==0)
					tempResources[4]++;
				resourcesToTrade.add(maxIndex);
				tempResources[maxIndex]-=tradeRates[maxIndex];
			}
		} while(maxRemainingResources>=0);
		return null;
	}	
	
	public int placeCity(int[][] playerResources, int[] playerNumPoints, int turn){		
		Vector<Integer> resourcesToTrade=canPlaceCity();
		if(resourcesToTrade==null)
			return -1;
		
		System.out.println("About to run city simulations on turn"+turn);
		System.out.print("Resources before first simulation: ");
		for(int i=0; i<resources.length; i++){
			System.out.print(resources[i]+" of type "+i+", ");
		}
		System.out.println();
		// Default option is not to place a city
		int maxValue=runSimulation(board,playerResources,playerNumPoints);
		int maxIndex=-1;
		System.out.print("Resources after first simulation: ");
		for(int i=0; i<resources.length; i++){
			System.out.print(resources[i]+" of type "+i+", ");
		}
		System.out.println();
		
		// First make the necessary port trades to place the city and adjust temporary resources
		for(int i=0; i<resourcesToTrade.size(); i++){
			int resourceReceived;
			if(playerResources[playerNum-1][0]<3&&resourcesToTrade.get(i)!=0)
				resourceReceived=0;
			else
				resourceReceived=1;
			playerResources[playerNum-1][resourceReceived]++;
			playerResources[playerNum-1][resourcesToTrade.get(i)]-=tradeRates[resourcesToTrade.get(i)];
		}
		playerResources[playerNum-1][0]-=3;
		playerResources[playerNum-1][1]-=2;
		playerNumPoints[playerNum-1]++;
		
		// Now place the city and run the simulations
		for(int i=0;i<settlements.length;i++){
			if(settlements[i]==null)
				break;
			Board newBoard=board.deepCopy();
			System.out.println("About to add possible city "+i);
			newBoard.addCity(settlements[i].getLocation(),color,playerNum-1);
			System.out.println("PlaceCityData: "+board.getNumCities()+" "+newBoard.getNumCities());
			int value=runSimulation(newBoard,playerResources,playerNumPoints);
			if(value>=maxValue){
				maxValue=value;
				maxIndex=i;
			}
		}
		if(maxIndex==-1)
			return -1;
		
		for(int i=0; i<resourcesToTrade.size(); i++){
			int resourceReceived;
			if(resources[0]<3&&resourcesToTrade.get(i)!=0)
				resourceReceived=0;
			else
				resourceReceived=1;
			resources[resourceReceived]++;
			resources[resourcesToTrade.get(i)]-=tradeRates[resourcesToTrade.get(i)];
		}
	
		if(resources[0]>=3 && resources[1]>=2){
			resources[0]-=3;
			resources[1]-=2;
			Point cityLocation=settlements[maxIndex].getLocation();
			addCity(cityLocation,getColor(),maxIndex,playerNum-1);
//			cities.add(new City(cityLocation,getColor()));
			newCityPoints.add(cityLocation);
//			settlements.remove(settleNum);
			points++;
			return maxIndex;
		}
		else{
			System.out.println("Player "+playerNum+" messed up its city placement on turn "+turn);
		}
		return -1;
	}
	
	public int placeSettlement(Vector<Point> possibleSettlements, int[][] playerResources, int[] playerNumPoints, int turn){
		Vector<Integer> resourcesToTrade=canPlaceSettlement();
		if(possibleSettlements.size()==0||resourcesToTrade==null)
			return -1;
		
		System.out.println("About to run settlement simulations on turn "+turn);
		System.out.print("Resources before first simulation: ");
		for(int i=0; i<resources.length; i++){
			System.out.print(resources[i]+" of type "+i+", ");
		}
		System.out.println();
		// Default option is not to place a settlement
		int maxValue=runSimulation(board,playerResources,playerNumPoints);
		int maxIndex=-1;
		System.out.print("Resources after first simulation: ");
		for(int i=0; i<resources.length; i++){
			System.out.print(resources[i]+" of type "+i+", ");
		}
		System.out.println();
		
		// First make the necessary port trades to place the settlement and adjust temporary resources
		for(int j=0; j<resourcesToTrade.size(); j++){
			int resourceReceived;
			if(playerResources[playerNum-1][1]==0&&resourcesToTrade.get(j)!=1)
				resourceReceived=1;
			else if(playerResources[playerNum-1][2]==0&&resourcesToTrade.get(j)!=2)
				resourceReceived=2;
			else if(playerResources[playerNum-1][3]==0&&resourcesToTrade.get(j)!=3)
				resourceReceived=3;
			else
				resourceReceived=4;
			playerResources[playerNum-1][resourceReceived]++;
			playerResources[playerNum-1][resourcesToTrade.get(j)]-=tradeRates[resourcesToTrade.get(j)];
		}
		playerResources[playerNum-1][1]--;
		playerResources[playerNum-1][2]--;
		playerResources[playerNum-1][3]--;
		playerResources[playerNum-1][4]--;
		playerNumPoints[playerNum-1]++;
		
		// Now place the settlement and run the simulations
		for(int i=0;i<possibleSettlements.size();i++){
			Board newBoard=board.deepCopy();
			System.out.println("About to add possible settlement "+i);
			newBoard.addSettlement(possibleSettlements.get(i),color,playerNum-1);
			System.out.println("PlaceSettlementData: "+board.getNumSettlements()+" "+newBoard.getNumSettlements());
			int value=runSimulation(newBoard,playerResources,playerNumPoints);
			if(value>=maxValue){
				maxValue=value;
				maxIndex=i;
			}
		}
		if(maxIndex==-1)
			return -1;
		
		for(int i=0; i<resourcesToTrade.size(); i++){
			int resourceReceived;
			if(resources[1]==0&&resourcesToTrade.get(i)!=1)
				resourceReceived=1;
			else if(resources[2]==0&&resourcesToTrade.get(i)!=2)
				resourceReceived=2;
			else if(resources[3]==0&&resourcesToTrade.get(i)!=3)
				resourceReceived=3;
			else
				resourceReceived=4;
			resources[resourceReceived]++;
			resources[resourcesToTrade.get(i)]-=tradeRates[resourcesToTrade.get(i)];
		}
		if(resources[1]>0 && resources[2]>0 && resources[3]>0 && resources[4]>0){
			resources[1]--;
			resources[2]--;
			resources[3]--;
			resources[4]--;
			Point settleLocation=possibleSettlements.get(maxIndex);
			addSettlement(settleLocation,getColor(),playerNum-1);
			newSettlePoints.add(settleLocation);
			points++;
//			System.out.println("Player "+playerNum+" scored a point on turn "+turn+" by placing " +
//					"a settlement at "+ settleLocation.getX()+" "+settleLocation.getY()+"!");
			return maxIndex;
		}
		else{
			System.out.println("Player "+playerNum+" messed up its settlement placement on turn "+turn);
		}
		return -1;
	}

	public int placeRoad(Vector<DoublePoint> possibleRoads, int[][] playerResources, int[] playerNumPoints){
		Vector<Integer> resourcesToTrade=canPlaceRoad();
		if(possibleRoads.size()==0||resourcesToTrade==null)
			return -1;		
		for(int i=0; i<resourcesToTrade.size(); i++){
			int resourceReceived;
			if(resources[3]==0&&resourcesToTrade.get(i)!=3)
				resourceReceived=3;
			else
				resourceReceived=4;
			resources[resourceReceived]++;
			resources[resourcesToTrade.get(i)]-=tradeRates[resourcesToTrade.get(i)];
		}
		if(resources[3]>0 && resources[4]>0 && possibleRoads.size()>0){
			resources[3]--;
			resources[4]--;
			int roadIndex=generator.nextInt(possibleRoads.size());
			addRoad(possibleRoads.get(roadIndex).getP1(),possibleRoads.get(roadIndex).getP2(),getColor(),playerNum-1);
			newRoadPoints.add(possibleRoads.get(roadIndex));
			return roadIndex;
		}
		return -1;
	}
	
	public int takeTurn(Vector<DoublePoint> roadPoints, Vector<Point> settlePoints, int[][] playerResources, int[] playerNumPoints, int turn){
		newRoadPoints.clear();
		newSettlePoints.clear();
		newCityPoints.clear();
		int cityIndex,settleIndex,roadIndex;
		do{
			cityIndex=placeCity(playerResources,playerNumPoints,turn);
			settleIndex=placeSettlement(settlePoints,playerResources,playerNumPoints,turn);
			if(settleIndex!=-1)
				settlePoints.remove(settleIndex);
			roadIndex=placeRoad(roadPoints,playerResources,playerNumPoints);
			if(roadIndex!=-1)
				roadPoints.remove(roadIndex);
		} while (cityIndex!=-1 && settleIndex!=-1 && roadIndex!=-1);
		if(points>=POINTS_TO_WIN)
			return playerNum;			
		return -1;
	}

	public int runSimulation(Board newBoard, int[][] playerResources,int[] playerNumPoints){
		int playerToMove;
		if(playerNum==NUM_PLAYERS)
			playerToMove=1;
		else
			playerToMove=playerNum+1;
		Simulation s=new Simulation(newBoard,playerResources,playerNumPoints,playerToMove);
		int value=s.runPlayouts(playerNum);
		return value;
	}
}
