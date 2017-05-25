import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 * class SinglePlayer 
 * 
 * Plays a single player  
 *
 * All states are stored as the perspective of player 1 being X
 *
 *@author:  Josh Kapple
 *@date:    3-07-17
 *@version: Beta 0.1
 */
public class SinglePlayer {
   static DatabaseInterface db = null;
   
	private static final int RACKETPORT = 17033;         // uses port 1237 on localhost  
	   
   public static void run(){
      int turn = 0;
           
      // Connect to the database 
      db = new DatabaseInterface();      
   	
      // Start gomoku player
		GomokuDriver player1 = new GomokuDriver("localhost", RACKETPORT);      
		
      
		String result1 = "";
		String result2 = "";
		
		ArrayList data1;
      
      ArrayList player1moveHistory = new ArrayList<ArrayList<Object>>();
            
      boolean playerisX;
            
      // Loop and play both players moves
		while(true){
         // record the states and moves players made
         data1 = player1.getStatus();
         char[][] grid1 = (char[][])data1.get(0);
         //System.out.println(data1.get(2));
         // check if player1 is X
         if (data1.get(2).equals("x")){
            playerisX = true;
         }
         else{
            playerisX = false;
         }
         
         // If this is a new state, build out the QTable and FreqTable
         // TODO add clause that checks if this state is in the database, if so we can assume the possibilities have been propagated already
         db.propogateStateLegalActions((char[][])data1.get(0), playerisX);
                  
         // Other player must have made an illegal move for us to be seeing a win state on our turn
         if (data1.get(1).equals("win") == true){     
             
            db.updateQStateValue(db.convertGridtoString(grid1), playerisX, -1, -1, (float)200.0);
                                    
            ArrayList lastmove = (ArrayList)player1moveHistory.get(player1moveHistory.size()-1);
            String previousgridstring = db.convertGridtoString((char[][])lastmove.get(0));
            db.incrementStateFreq(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2));
           
            // update Qtable
            // Q[s, a] = Q[s, a] + aplha(Nsa [s, a])(r + discount maxa Q[s', a'] - Q[s, a])
            // Q value of last state and action = Qvalue[last state, action] + learningrate*(freq[last state, action)(previous reward + discountfactor 

            // get the previous state action pair q value 
            ArrayList prevQstate = (ArrayList)db.selectStateFromQTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
           
            // get the previous state action pair freq
            ArrayList prevFstate = (ArrayList)db.selectStateFromFreqTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
            // mutate current state with the best action to get the 
           
            float r = (float)prevQstate.get(4);
            float gamma = (float)0.8;
            int Nsa =  (int)prevFstate.get(4);
           
            float updateValue = r + db.learningRate(Nsa)*(r + gamma*(200 - r));
            db.updateQStateValue(previousgridstring, playerisX,  (int)lastmove.get(1), (int)lastmove.get(2), updateValue);
            
            break;
	      }
         
         else if (data1.get(1).equals("lose") == true || data1.get(1).equals("draw") == true){
             // player two won
             //System.out.println(String.format("Player 2: win"));
             db.updateQStateValue(db.convertGridtoString(grid1), playerisX, -1, -1, (float)-200.0);

             //System.out.println(String.format("Player 1: wins"));
                        
             ArrayList lastmove = (ArrayList)player1moveHistory.get(player1moveHistory.size()-1);
             String previousgridstring = db.convertGridtoString((char[][])lastmove.get(0));
             db.incrementStateFreq(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2));
           
             // update Qtable
             // Q[s, a] = Q[s, a] + aplha(Nsa [s, a])(r + discount maxa Q[s', a'] - Q[s, a])
             // Q value of last state and action = Qvalue[last state, action] + learningrate*(freq[last state, action)(previous reward + discountfactor 

             // get the previous state action pair q value 
             ArrayList prevQstate = (ArrayList)db.selectStateFromQTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
           
             // get the previous state action pair freq
             ArrayList prevFstate = (ArrayList)db.selectStateFromFreqTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
            // mutate current state with the best action to get the 
           
            float r = (float)prevQstate.get(4);
            float gamma = (float)0.8;
            int Nsa =  (int)prevFstate.get(4);
           
            float updateValue = r + db.learningRate(Nsa)*(r + gamma*(-200 - r));
            db.updateQStateValue(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2), updateValue);
            break;
         }  
         
        // Didn't win or lose, game is still playable        
		  // Player 1 normal turn
        // 
        
        ArrayList instancep1 = new ArrayList<Object>();           
        ArrayList fFunctionResult = db.fFunction(db.convertGridtoString(grid1), playerisX);
        
        // if previous state is not null, increment frequency table of previous state and previous action
        if (player1moveHistory.size() > 0){
           ArrayList lastmove = (ArrayList)player1moveHistory.get(player1moveHistory.size()-1);
           String previousgridstring = db.convertGridtoString((char[][])lastmove.get(0));
           db.incrementStateFreq(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2));
           
           // update Qtable
           // Q[s, a] = Q[s, a] + aplha(Nsa [s, a])(r + discount maxa Q[s', a'] - Q[s, a])
           // Q value of last state and action = Qvalue[last state, action] + learningrate*(freq[last state, action)(previous reward + discountfactor 

           // get the previous state action pair q value 
           ArrayList prevQstate = (ArrayList)db.selectStateFromQTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
           
           // get the previous state action pair freq
           ArrayList prevFstate = (ArrayList)db.selectStateFromFreqTable(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2)).get(0);
           // mutate current state with the best action to get the 
           
           float r = (float)prevQstate.get(4);
           float gamma = (float)0.8;
           int Nsa =  (int)prevFstate.get(4);
           
           float updateValue = r + db.learningRate(Nsa)*(r + gamma*((int)fFunctionResult.get(2) - r));
           db.updateQStateValue(previousgridstring, playerisX, (int)lastmove.get(1), (int)lastmove.get(2), updateValue);
        }    
        
        // play the move we retrieved
        // and add it to the move history                
        player1.rc.gridOut.println(String.format("%d %d", fFunctionResult.get(4), fFunctionResult.get(5)));
        instancep1.add(data1.get(0));
        instancep1.add((int)fFunctionResult.get(4));
        instancep1.add((int)fFunctionResult.get(5));
        player1moveHistory.add(instancep1);        
	     turn++;
		}
   }

	public static void main(String[] args){
      run();
	}
}
