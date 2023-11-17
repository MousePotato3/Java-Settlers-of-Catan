package catan2;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

public class RandComp extends Player {
	private static final long serialVersionUID = 1L;
	private Vector<Point> hexIntersections=new Vector<Point>();
	
	public RandComp(Board startBoard, Color c, int n){
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
		/*int maxIndex=-1;
		int maxValue=-1;
		int value;
		*/
		Vector<Point> possibleRoadPoints=board.getAdjacentIntersections(p);
		int roadNum=generator.nextInt(possibleRoadPoints.size());
		return possibleRoadPoints.get(roadNum);
		
		/*if(possibleRoadPoints.size()==0){
			System.err.println("ERROR: First settlement location had no neighbors");
			return new Point(0,0);
		}
		
		for(int i=0; i<possibleRoadPoints.size(); i++){
			boolean ignoreRoad=false;
			Vector<Point> futureSettlePoints=board.getAdjacentIntersections(possibleRoadPoints.get(i));
			for(int j=0; j<futureSettlePoints.size(); j++){
				if(board.findRoadIndex(possibleRoadPoints.get(i),futureSettlePoints.get(j))!=-1){
					ignoreRoad=true;
//					System.out.println("Road "+i+" ignored");
				}
				// Check if already settled or someone else built a road nearby
				else if(!ignoreRoad && board.findSettlementIndex(futureSettlePoints.get(j))==-1) { 
					value=board.getHexValue(futureSettlePoints.get(j));
					if(value>maxValue){
//						System.out.println("New maxValue: "+maxValue+" at index: "+maxIndex);
						maxValue=value;
						maxIndex=i;
					}
				}
				else{
//					System.out.println("Settlement "+j+" from road "+i+" ignored");
				}
			}
		}
		
		return possibleRoadPoints.get(maxIndex);
		*/
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
//		System.out.println("Player "+playerNum+" discarded "+numberToDiscard+" resources on turn "+turn);
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
		//int[] tempResources = (int[])resources.clone();
		int[] tempResources=new int[5];
		for(int i=0; i<resources.length; i++)
			tempResources[i]=resources[i];
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
		//int[] tempResources = (int[])resources.clone();
		int[] tempResources=new int[5];
		for(int i=0; i<resources.length; i++)
			tempResources[i]=resources[i];
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
		//int[] tempResources = (int[])resources.clone();
		int[] tempResources=new int[5];
		for(int i=0; i<resources.length; i++)
			tempResources[i]=resources[i];
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
	
	public int placeCity(int turn){
		Vector<Integer> resourcesToTrade=canPlaceCity();
		if(resourcesToTrade==null)
			return -1;
		for(int i=0; i<resourcesToTrade.size(); i++){
			int resourceReceived;
			if(resources[0]<3&&resourcesToTrade.get(i)!=0)
				resourceReceived=0;
			else
				resourceReceived=1;
			resources[resourceReceived]++;
			resources[resourcesToTrade.get(i)]-=tradeRates[resourcesToTrade.get(i)];
//			System.out.println("Player "+playerNum+" just traded "+tradeRates[resourcesToTrade.get(i)]
//			+" "+getResourceType(resourcesToTrade.get(i))+" for 1 "+getResourceType(resourceReceived)
//			+" from a port");
		}
	
		if(resources[0]>=3 && resources[1]>=2){
			resources[0]-=3;
			resources[1]-=2;
			int cityIndex=generator.nextInt(getTotalSettlements());
			Point cityLocation=settlements[cityIndex].getLocation();
			addCity(cityLocation,getColor(),cityIndex,playerNum-1);
//			cities.add(new City(cityLocation,getColor()));
			newCityPoints.add(cityLocation);
//			settlements.remove(settleNum);
			points++;
//			System.out.println("Player "+playerNum+" scored a point on turn "+turn+" by placing " +
//					"a city at "+cityLocation.getX()+" "+cityLocation.getY()+"!");
			return cityIndex;
		}
		else{
			System.out.println("Player "+playerNum+" messed up its city placement on turn "+turn);
		}
		return -1;
	}
	
	public int placeSettlement(Vector<Point> possibleSettlements, int turn){
		Vector<Integer> resourcesToTrade=canPlaceSettlement();
		if(possibleSettlements.size()==0||resourcesToTrade==null)
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
//			System.out.println("Player "+playerNum+" just traded "+tradeRates[resourcesToTrade.get(i)]
//			+" "+getResourceType(resourcesToTrade.get(i))+" for 1 "+getResourceType(resourceReceived)
//			+" from a port");
		}
		if(resources[1]>0 && resources[2]>0 && resources[3]>0 && resources[4]>0){
			resources[1]--;
			resources[2]--;
			resources[3]--;
			resources[4]--;
			int settleIndex=generator.nextInt(possibleSettlements.size());
			Point settleLocation=possibleSettlements.get(settleIndex);
			addSettlement(settleLocation,getColor(),playerNum-1);
			newSettlePoints.add(settleLocation);
			points++;
//			System.out.println("Player "+playerNum+" scored a point on turn "+turn+" by placing " +
//					"a settlement at "+ settleLocation.getX()+" "+settleLocation.getY()+"!");
			return settleIndex;
		}
		else{
			System.out.println("Player "+playerNum+" messed up its settlement placement on turn "+turn);
		}
		return -1;
	}

	public int placeRoad(Vector<DoublePoint> possibleRoads){
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
//			System.out.println("Player "+playerNum+" just traded "+tradeRates[resourcesToTrade.get(i)]
//			+" "+getResourceType(resourcesToTrade.get(i))+" for 1 "+getResourceType(resourceReceived)
//			+" from a port!");
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
			cityIndex=placeCity(turn);
			settleIndex=placeSettlement(settlePoints,turn);
			if(settleIndex!=-1)
				settlePoints.remove(settleIndex);
			roadIndex=placeRoad(roadPoints);
			if(roadIndex!=-1)
				roadPoints.remove(roadIndex);
		} while (cityIndex!=-1 && settleIndex!=-1 && roadIndex!=-1);
		if(points>=POINTS_TO_WIN)
			return playerNum;			
		return -1;
	}
}
