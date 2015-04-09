/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author steven
 */
public class ComputerPlayer {
	
	public static final boolean MULTIPLE_PATH_PRUNING = true;
    GameDisplay display;
    GameState startingState;
    public ComputerPlayer(GameState state){
        display = new GameDisplay(state);
        this.startingState = state;
    }
    
    public List<GameState> getSolution(){
    	PriorityQueue<QueueNode> frontier = new PriorityQueue<QueueNode>();     
    	HashSet<GameState> closedSet = new HashSet<GameState>();        	
        frontier.add(new QueueNode(this.startingState));
        
        //Frontier should never be empty as long as a solution can be found
        while(!frontier.isEmpty()){
        	QueueNode currentNode = frontier.poll();
        	if(currentNode.getGameState().isGoalState()){
        		return reconstructPath(currentNode);
        	}if(MULTIPLE_PATH_PRUNING){
        		if(closedSet.contains(currentNode.getGameState())){
        			continue;
        		}
        		closedSet.add(currentNode.getGameState());
        	}
        	for(QueueNode neighbor: currentNode.getNeighbors(closedSet, MULTIPLE_PATH_PRUNING)){
        		frontier.add(neighbor);		
        	}
        }
        System.err.println("Path not found");
        System.exit(100);
        return null;
    }
    
    
	private List<GameState> reconstructPath(QueueNode currentNode) {
		List<GameState> sortedList = new ArrayList<GameState>();
		QueueNode node = currentNode;
		while(node != null){
			sortedList.add(node.getGameState());
			node = node.getParent();
		}
		Collections.reverse(sortedList);
		return sortedList;
	}

	public void showSolution(List<GameState> solution) {               
        for (GameState st : solution) {            
            display.updateState((GameState) st);
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws Exception{
        GameState state = args.length==0?new GameState("C:\\Users\\Excessum\\workspace\\CICoursework\\src\\levels\\level4.txt"):new GameState(args[0]);        
        long t1 = System.currentTimeMillis();
        ComputerPlayer player = new ComputerPlayer(state);  
        List<GameState> solution = player.getSolution();
        long t2 = System.currentTimeMillis();
        System.out.println("Time: " + (t2-t1));
        player.showSolution(solution);
    }
     
}
