import java.util.List;
import java.util.Timer;

/**
 * class AlphaBeta
 * 
 * The Alpha Beta Algorithm 
 *
 * Implemented from psuedo-code found in Artificial Intelligence: A Modern Approach, ch 5. 
 * Works with the BoardState class
 *
 *@date:    2-17-17
 *@version: Beta 0.1
 */
class AlphaBeta{
   static BoardState ab_bs;
   static char player;
   static char opponent;
   static long startTime;
   static BoardState negative_board;
   static BoardState positive_board;
   

   public AlphaBeta(char[][] gridArray, char myplayer, char myopponent){
      ab_bs = new BoardState(gridArray, null, 0, 0);
      player = myplayer;
      opponent = myopponent; 
      negative_board = new BoardState(-Integer.MAX_VALUE);
      positive_board = new BoardState(Integer.MAX_VALUE);
      startTime = System.currentTimeMillis();
   }   
   
   public static BoardState AlphaBetaDecide(int depthlimit){
      BoardState b = maxValue(ab_bs, negative_board, positive_board, 0, depthlimit);
      //System.out.println(String.format("Move took: %d ms", System.currentTimeMillis() - startTime));
      System.out.println(String.format("Move-depth: %d\n", b.getMaxDepth()/2));
      return b;
   }
   
   /** Alpha Beta maximizer
   *
   */
   public static BoardState maxValue(BoardState boardstate, BoardState alpha, BoardState beta, int depth, int depthlimit){
       BoardState v;
       BoardState w;
       boardstate.increaseMaxDepth();
       
       if (depth > depthlimit || (System.currentTimeMillis() - startTime) > 1900){ // terminal test and timeout
         return boardstate;
       }
          
       v = negative_board;; // Negative infinity
       
       for (int i=0; i < boardstate.getGrid().length; i++){
          for (int j=0; j < boardstate.getGrid()[i].length; j++){
             
             if(boardstate.getGrid()[i][j] == ' '){     
                BoardState mutatedBoard;                            
                int[] thefirstmove;
                thefirstmove = boardstate.getFirstMove();
                if (thefirstmove == null){
                   thefirstmove = new int[]{i,j};
                }
                                
                mutatedBoard = new BoardState(boardstate.getGrid(), thefirstmove, boardstate.getScore(), boardstate.getMaxDepth());
                mutatedBoard.mutateBoard(i,j, player);
                mutatedBoard.evaluate(player, opponent);
                w = minValue(mutatedBoard, alpha, beta, depth+1, depthlimit);
                
                
                // get the min between v and w
                // This was a huge pain in the ass to catch. Because I wanted a REAL boardstate, had to implement this
                // with the secondary check. Otherwise, it would just modify v all the way down the board
                // or worse, return the infinite board
                if (w.getScore() > v.getScore() || (w.getScore() >= v.getScore() && v.getGrid() == null)){
                   v = w;
                }
               
                if (v.getScore() >= beta.getScore()){ 
                  return v;  
                }
                  
                // get the max between alpha and v
                if (v.getScore() > alpha.getScore()){
                   alpha = v;
                }
             }
          }
       }                         
       return v;       
   }
   
   /** Alpha Beta minimizer
   *
   */
   public static BoardState minValue(BoardState boardstate, BoardState alpha, BoardState beta, int depth, int depthlimit){
       BoardState v;
       BoardState w;
       boardstate.increaseMaxDepth();
       
       if (depth > depthlimit || (System.currentTimeMillis() - startTime) > 1900){ // terminal test
          return boardstate;
       }
            
       v = positive_board; // Positive infinity       
              
       for (int i=0; i < boardstate.getGrid().length; i++){
          for (int j=0; j < boardstate.getGrid()[i].length; j++){
     
             if(boardstate.getGrid()[i][j] == ' '){  
                BoardState mutatedBoard;
                
                mutatedBoard = new BoardState(boardstate.getGrid(), boardstate.getFirstMove(), boardstate.getScore(), boardstate.getMaxDepth());
                mutatedBoard.mutateBoard(i,j, opponent);
                mutatedBoard.evaluate(player,opponent);               
                w = maxValue(mutatedBoard, alpha, beta, depth+1, depthlimit);
                
               
                // get the min between v and w
                // This was a huge pain in the ass to catch. Because I wanted a REAL boardstate, had to implement this
                // with the secondary check. Otherwise, it would just modify v all the way down the board
                // or worse, return the infinite board
                if (w.getScore() < v.getScore() || (w.getScore() <= v.getScore() && v.getGrid() == null)){  
                   v = w;
                }
                
                if (v.getScore() <= alpha.getScore()){
                   return v;
                }
                
                // get the min between beta and v
                if (v.getScore() < beta.getScore()){
                   beta = v;
                }   
             }
          }
       }
       return v;   
   }
}