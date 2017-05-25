import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

class DatabaseTester{
   public static void main(String[] args){
      // try and get a database instance 
      DatabaseInterface db = new DatabaseInterface();
      
      /* 
      // get some results from the database
      db.selectStateFromQTable("      x\n", 1, 1);
      
      // insert state into qtable with a value of 100
      db.updateQStateValue("1111\n", 1, 1, 100);
      */
      // use the same command to update the existing state with a new value;
      //db.insertStateAndActionsQTable("1111\n", 1, 1, 200);
      //db.insertStateAndFreq("1111\n", 1, 1, 1);
      
      // retrieve the qstate and value from the database
      //ArrayList result = db.selectStateFromQTable("1111\n", 1, 1);   
      
      
      
      // Test converting the char array to a string state
      char[][] grid = new char[3][3];
         
      grid[0] = new char[]{'x', ' ', ' ',};
      grid[1] = new char[]{' ', 'o', ' ',};
      grid[2] = new char[]{'x', 'x', 'x',};
    
      char[][] grid2 = db.convertStringtoGrid(new String("xxxx  o  no        n x   o   n         n         n         n     o   n o       n         "));
      db.mutateGrid(grid2, 0, 4, 'x');
       
      System.out.println("complete");
      //String result = db.convertGridtoString(grid);
      //System.out.println(result);
      //db.insertStateAndActionsQTable(result, 1, 1, 200);
      //db.insertStateAndFreq(result, 1, 1, 1);
      //ArrayList test = db.fFunction(result);
      // Test swapping players in state
      //System.out.println(db.swapPlayersInGrid(result));
      
      //db.incrementStateFreq(result, 1, 1);
   }// end main
}