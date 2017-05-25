import java.util.ArrayList;
import java.util.List;

/**
 * class Gomoku Driver
 * 
 * Main program for playing the Gomoku server game
 *
 *@author:  Josh Kapple
 *@date:    2-17-17
 *@version: Beta 0.1
 */
public class GomokuDriver {
   private static final int RACKETPORT = 17033;         // uses port  on localhost  
   private static int gridWidth;                        // Width of the Gomoku Board
   private static int gridHeight;                       // Height of the Gomoku Board
   protected RacketClient rc;
     
   public int getGridWidth(){
       return gridWidth;
   }
   
   public int getGridHeight(){
       return gridHeight;
   }
   
   public GomokuDriver(String h, int p){
      rc = new RacketClient(h, p);
   }
   
   /*
   public static void main(String[] args){
      int turn = 0;

      GomokuDriver client = new GomokuDriver("localhost", RACKETPORT);
      
      String result = "";
      
    	while(true){
         result = client.think(turn);   
         if (result.equals("continuing") == false){
             System.out.println(result);
             break;
         }
         turn++; 
      }
   }
   */
   
   /**
   * Parses the strings from the server into 
   * 2d char array for analyzing, String for gamestatus and String for player identification
   * 
   * Adapts to different grid sizes from the server
   *
   * Returns an arraylist of generic objects of the above 3 items
   */   
   public ArrayList<Object> getStatus(){ 
      char[][] gridArray;
                  
      ArrayList objectList = new ArrayList<Object>();
      List<String> inputStrings = new ArrayList<String>();
      
      try {
         String nextLine;
         
         while ((nextLine = this.rc.gridIn.readLine()) != null){
            inputStrings.add(nextLine.toLowerCase());
            if (nextLine.equals("o") || nextLine.equals("x")){
               break;
            } 
         }
      }
      catch(Exception e){
         //e.printStackTrace();
         return null;
      }
      
      gridHeight = inputStrings.size() -2;
      gridWidth  = inputStrings.get(1).length();

      gridArray = new char[gridHeight][gridWidth];
       
      for (int i=0; i < inputStrings.size() - 1; i++){
         if (i > 0 && i < inputStrings.size()){
            for (int j = 0; j < inputStrings.get(i).length(); j++){
               gridArray[i-1][j] = inputStrings.get(i).charAt(j); // gridArray[i-1] because the grid lines are actually coming from the second rc.gridLn.readLine(); call
            }
         }
      }
      
      objectList.add(gridArray);
      objectList.add(inputStrings.get(0));
      objectList.add(inputStrings.get(inputStrings.size() -1 ) );
      
      return objectList;
   }
   
   /**
   *  think is basically the loop body for getting information from the 
   *  server and acting upon it. 
   */
   public String think(int turn, ArrayList data){
      //ArrayList  data;
      String status;
      int depthlimit = 0;
      
      //data = this.getStatus();
      status = (String)data.get(1);
      
      if (status == null){
         System.out.println("Still waiting");
         return "";
      }
      if (status == "win" || status == "lose" || status == "forfeit"){
        return status;
      }
      
      
      // Setting our character from the server
      char player = 'x';
      char opponent = 'o';   
      if (String.valueOf(data.get(2)).equals("o")){
         player   = 'o';
         opponent = 'x';
      }
      
      if (turn < 3){
         depthlimit = 2;
      }
      else if (turn > 3 ){
         depthlimit = 4;
      }
                
      // Propogate moves / analyze board state / use Alpha Beta to determine best move before timer runs out
      AlphaBeta ab = new AlphaBeta((char[][])data.get(0), player, opponent);
      BoardState result = ab.AlphaBetaDecide(depthlimit);   
      //System.out.println(String.format("%d %d", result.getFirstMove()[0], result.getFirstMove()[1]));
      //System.out.println(result.getScore());
      
      System.out.println("\n");   
      if (result.getFirstMove()[0] == 0 && result.getFirstMove()[1] == 3){
    	System.out.println("what");      
      }
      // Send move
      rc.gridOut.println(String.format("%d %d", result.getFirstMove()[0], result.getFirstMove()[1]));
      return status;
  }
}
