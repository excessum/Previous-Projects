package sokoban;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import sokoban.GameState.Position;



public class QueueNode implements Comparable<QueueNode> {
	
	private static final boolean SIMPLE_MANHATTAN = false;
	private static final boolean CHECK_FOR_DEADSTATES = true;	
	
	private int hValue = Integer.MAX_VALUE;
	private int gValue = 0;
	private int fValue;
	private QueueNode cameFrom = null;
	private GameState state;
	
	public QueueNode(GameState state, QueueNode previousNode){
		this.state = state;
		gValue = previousNode.getGValue() + 1;
		if(SIMPLE_MANHATTAN){
			hValue = manhattanDistance(state.getBlockPositions(), state.getGoalPositions());
		}else{
			hValue = matchedManhattanDistance(state.getBlockPositions(), state.getGoalPositions());
		}
		fValue = gValue + hValue;
		this.cameFrom = previousNode;
	}
	
	
	public QueueNode(GameState state){
		this.state = state;
		if(SIMPLE_MANHATTAN){
			hValue = manhattanDistance(state.getBlockPositions(), state.getGoalPositions());
		}else{
			hValue = matchedManhattanDistance(state.getBlockPositions(), state.getGoalPositions());
		}
		fValue = gValue + hValue;
	}
	
	private int matchedManhattanDistance(List<Position> blockPositions, List<Position> goalPositions) {
		int sum = 0;
		List<Position> chosenGoals = new ArrayList<Position>();
		Position takenPosition = null;
		for(Position block: blockPositions){
			int lowestCost = Integer.MAX_VALUE;
			for(Position goal: goalPositions){
				if(chosenGoals.contains(goal)){
					continue;
				}
				if(block.distance(goal) < lowestCost){
					lowestCost = block.distance(goal);
				}
			}
			chosenGoals.add(takenPosition);
			sum += lowestCost;
		}
		return sum;
	}
	
	private int manhattanDistance(List<Position> blockPositions, List<Position> goalPositions) {
		int sum = 0;
		for(Position block: blockPositions){
			int lowestCost = Integer.MAX_VALUE;
			for(Position goal: goalPositions){
				if(block.distance(goal) < lowestCost){
					lowestCost = block.distance(goal);
				}
			}
			sum += lowestCost;
		}
		return sum;
	}
	
	@Override
	public int compareTo(QueueNode arg0) {
		return this.fValue - arg0.fValue;
	}
	
	public GameState getGameState(){
		return this.state;
	}

	public int getGValue(){
		return this.gValue;
	}
	
	public ArrayList<QueueNode> getNeighbors(HashSet<GameState> closedSet, boolean multiplePathPruning) {
		ArrayList<QueueNode> toReturn = new ArrayList<QueueNode>();
		GameState toAdd = null;
		if(this.state.moveUpLegal()){
			toAdd = this.state.moveUp();
			if(!closedSet.contains(toAdd) || !multiplePathPruning){
				toReturn.add(new QueueNode(toAdd, this));
			}
		}
		if(this.state.moveLeftLegal()){
			toAdd = this.state.moveLeft();
			if(!closedSet.contains(toAdd) || !multiplePathPruning){
				toReturn.add(new QueueNode(toAdd, this));
			}
		}
		if(this.state.moveDownLegal()){
			toAdd = this.state.moveDown();
			if(!closedSet.contains(toAdd) || !multiplePathPruning){
				toReturn.add(new QueueNode(toAdd, this));
			}
		}
		if(this.state.moveRightLegal()){
			toAdd = this.state.moveRight();
			if(!closedSet.contains(toAdd) || !multiplePathPruning){
				toReturn.add(new QueueNode(toAdd, this));
			}
		}
		
		if(CHECK_FOR_DEADSTATES){
			//Checking for deadstates
			ArrayList<QueueNode> copy = (ArrayList<QueueNode>) toReturn.clone();
			for(QueueNode nodes: copy){
				GameState toCheck = nodes.state;
				
				//Get block which has been moved
				List<Position> originalBlockPositions = this.state.getBlockPositions();
				List<Position> newBlockPositions = toCheck.getBlockPositions();
				Position movedBlock = null;
				for(int i = 0; i < originalBlockPositions.size(); i++){
					if(!newBlockPositions.get(i).equals(originalBlockPositions.get(i))){
						movedBlock = newBlockPositions.get(i);
						break;
					}
				}
				
				//No block moved, cannot be deadstate
				if(movedBlock == null){
					continue;
				}else{
					if(CheckForDeadState(toCheck, movedBlock, false, closedSet, multiplePathPruning)){
						if(multiplePathPruning){ 
							closedSet.add(nodes.getGameState());
						}
						toReturn.remove(nodes);	
						continue;
					}
				}
			}
		}
		return toReturn;
	}
	
	private boolean CheckForDeadState(GameState toCheck, Position movedBlock, boolean ignoreBlockDS, HashSet<GameState> closedSet, boolean multiplePathPruning) {
		char[][] map = toCheck.getMap();
		int row = movedBlock.row;
		int col = movedBlock.col;
		
		/*
		 * Check if goal
		 */
		if(map[row][col] == 'B'){
			return false;
		}
		
		char[] surroundingTiles = {map[row-1][col], map[row][col-1], map[row+1][col], map[row][col+1]} ;		
		int wallCount = 0;
		
		
		/*
		 * Check for corner and 2 blocks against wall
		 */
		char previousChar = ' ';
		if(surroundingTiles[0] == 'w' && (surroundingTiles[3] =='w' || surroundingTiles[3] =='b')){
			if(multiplePathPruning) closedSet.add(toCheck);
			return true;
		}
		for(char character: surroundingTiles){
			if(character == 'w'){
				if((previousChar == 'w') || (previousChar == 'b' && !ignoreBlockDS)){
					if(multiplePathPruning) closedSet.add(toCheck);
					return true;
				}
				wallCount++;
			}else if(character == 'b' && !ignoreBlockDS){
				if(previousChar == 'w'){
					if(multiplePathPruning) closedSet.add(toCheck);
					return true;
				}
			}
			previousChar = character;
		}
		
		//Zero or one blocking objects
		if(wallCount == 0){
			return false;
		//Need to detect if there is a block against a wall with no escape
		}else{
			int wallDirection = 0;
			//Get direction of wall
			//System.err.println(surroundingTiles.length);
			for (wallDirection = 0; wallDirection < surroundingTiles.length; wallDirection++){
				if(surroundingTiles[wallDirection] == 'w'){
					break;
				}
			}
			
			switch(wallDirection){
				case 0:
				case 2:
					if(check(toCheck, movedBlock, false)){
						if(multiplePathPruning){
							closedSet.add(toCheck);							
						}
						return true;
					}
					return false;
				case 1:
				case 3:
					if(check(toCheck, movedBlock, true)){
						if(multiplePathPruning){
							closedSet.add(toCheck);
						}
						return true;
					}
					return false;
			}			
		}
		return false;
	}
	
	public boolean equals(Object o){
		
		if (!(o instanceof QueueNode)) {
            return false;
        }
		QueueNode toCompare = (QueueNode) o;
		if(this.state.equals(toCompare.getGameState())){
			return true;
		}
		return false;
	}
	
	private boolean check(GameState toCheck, Position movedBlock, boolean vertical) {
		
		int row = movedBlock.row;
		int col = movedBlock.col;
		char[][] map = toCheck.getMap();
		
		while(map[row][col] != 'w'){
			if(map[row][col] == 'B') return false;
			if(vertical){
				if(map[row][col+1] != 'w' && map[row][col-1] != 'w' ){
					return false;
				}
				row++;
			}else{
				if(map[row+1][col] != 'w' && map[row-1][col] != 'w' ){				
					return false;
				}
				col++;
			}
		}
		
		row = movedBlock.row;
		col = movedBlock.col;
		
		while(map[row][col] != 'w'){
			if(map[row][col] == 'B') return false;
			if(vertical){
				if(map[row][col+1] != 'w' && map[row][col-1] != 'w' ){
					return false;
				}
				row--;
			}else{
				if(map[row+1][col] != 'w' && map[row-1][col] != 'w' ){
					return false;
				}
				col--;
			}
		}		
		return true;
	}


	public QueueNode getParent(){
		return this.cameFrom;
	}
}
