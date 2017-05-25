import java.util.ArrayList;
import java.util.List;
/* Manual class for testing Boardstate / AlphaBeta evalutions
*  
*/
class AlphaBetaDriver{
      
   public static void setGrid(char[][] grid){
      grid = grid;
   }

   public static String gridToString(char[][] grid){
      String arraystring = new String();
      for (int i=0; i < grid.length; i++){
         for(int j=0; j < grid[i].length; j++){
            arraystring = arraystring + grid[i][j];
         }
         arraystring = arraystring + "\n";
      }
      return arraystring;
   }

   public static void main(String[] args){
       
      ArrayList player1history = new ArrayList<ArrayList<Object>>();
      ArrayList player2history = new ArrayList<ArrayList<Object>>();
      
      ArrayList instance = new ArrayList<Object>();
      
      char[][] grid = new char[9][9];
      
      //grid[0] = new char[]{'x', ' ', ' ',};
      //grid[1] = new char[]{' ', 'o', ' ',};
      //grid[2] = new char[]{'x', 'x', 'x',};
      //                    0   1   2   3   4   5   6   7   8   9   10
      grid[0] = new char[]{'x','x','x','x',' ',' ',' ',' ',' '};//,' ',' '};
      grid[1] = new char[]{' ',' ',' ',' ',' ','x','x',' ',' '};//,' ',' '};
      grid[2] = new char[]{'o','o',' ',' ',' ','x','x',' ',' '};//,' ',' '};
      grid[3] = new char[]{'o','o',' ',' ',' ','x','x',' ',' '};//,' ',' '};
      grid[4] = new char[]{'o','o',' ',' ',' ',' ','x',' ',' '};//,' ',' '};
      grid[5] = new char[]{'o',' ',' ',' ',' ',' ',' ',' ',' '};//,' ',' '};
      grid[6] = new char[]{' ',' ',' ',' ','x','x','x','x',' '};//,' ',' '};
      grid[7] = new char[]{' ','o','o','o','o',' ',' ',' ',' '};//,' ',' '};
      grid[8] = new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '};//,' ',' '};
      //grid[9] = new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};
      //grid[10] = new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '};

      char player = 'x';
      char opponent = 'o';
      
      instance.add(grid);
      instance.add(new int[]{8,7});
      player1history.add(instance);
      
      
      
      String gridstring = gridToString(grid);
      System.out.println(gridstring);
      
      
      // Propogate moves / analyze board state / use Alpha Beta to determine best move before timer runs out
      //AlphaBeta ab = new AlphaBeta((char[][])status.get(0), player, opponent);
      //AlphaBeta ab = new AlphaBeta(grid, player, opponent);
      //BoardState result = ab.AlphaBetaDecide(2);
      BoardState bs = new BoardState(grid, null, 0, 0);
      bs.evaluate('x','o');
      System.out.println(bs.getScore());
      
      //System.out.println(String.format("%d %d", result.getFirstMove()[0], result.getFirstMove()[1]));
   }
}