import java.sql.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

class DatabaseInterface{
   public final int GRIDSIZE = 9;
   
   Connection c = null;
  
   public String convertGridtoString(char[][] grid){
      String gridString = "";
      for (int i=0; i < grid.length; i++){
         for (int j=0; j < grid[i].length; j++){
            gridString = gridString + grid[i][j];
         }
         if (i != grid.length - 1){
           gridString = gridString + "n";
         }
      }
      return gridString;
   }
   
   public char[][] convertStringtoGrid(String gridString){
     // check how big the grid is
     char[][] grid; 
     
     int lineSize = 0;
      
     grid = new char[GRIDSIZE][GRIDSIZE];
     
     int j = 0;
     int k = 0;
     for (int i=0; i < gridString.length() - 1; i++){
         char nextChar = gridString.charAt(i);
         if (nextChar == 'n'){
            j=j + 1;
            k=0;
         }
         else{
            grid[j][k]=nextChar;
            k++;
         }
         
      }
      return grid;
   }
   
   public void mutateGrid(char[][] grid, int action_x, int action_y, char player){
      grid[action_x][action_y] = player;
   }
   
   public char[][] swapPlayersInGridArray(char[][] grid){
      for (int i=0; i<grid.length - 1; i++){
         for (int j=0; j<grid[i].length; j++){
            if (grid[i][j] == 'x'){
               grid[i][j] = 'o';
            }
            else if (grid[i][j] == 'o'){
               grid[i][j] = 'x';
            }
         }
      }
      return grid;
   }
   
   public String swapPlayersInGrid(String inputString){
      String i = inputString.replace('x','i');
      String outputString = i.replace('o', 'x');
      outputString = outputString.replace('i','o');
      return outputString;
   }
   
   public float learningRate(int freq){
      if (freq == 1000){
         return 1000;
      }
      return 1000/(1000-freq);
   }
   
   public int retrieveStatesFromQtable(String gridstring, boolean playerIsX){
      if (playerIsX){}
      else
      {
         gridstring = swapPlayersInGrid(gridstring);
      }
      
      String sql  = "SELECT count(*) FROM qtable WHERE state_string = ? \n";
      int count = 0;
      
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setString(1, gridstring);
         ResultSet rs = pstmt.executeQuery();
     
         while (rs.next()) {
           count = rs.getInt("count(*)");
        }
      }
      
      catch (SQLException e){
         System.out.println(String.format("Error: %s", e.getMessage()));
      }
      
      return count;
   }
   
   public int retrieveStatesFromFreq(String gridstring, boolean playerIsX){
      if (playerIsX){}
      else
      {
         gridstring = swapPlayersInGrid(gridstring);
      }
      
      String sql  = "SELECT count(*) FROM freq WHERE state_string = ? \n";
      int count = 0;
      
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setString(1, gridstring);
         ResultSet rs = pstmt.executeQuery();
     
         while (rs.next()) {
           count = rs.getInt("count(*)");
        }
      }
      
      catch (SQLException e){
         System.out.println(String.format("Error: %s", e.getMessage()));
      }
      
      return count;
   }
   
   public String propogateStateLegalActions(char[][] grid, boolean playerIsX){            
      String gridstring;
      if (playerIsX){
         gridstring = convertGridtoString(grid);
      }
      else {
         gridstring = swapPlayersInGrid(convertGridtoString(grid));
      }
      
      // check if states exist already
      if (!(retrieveStatesFromQtable(gridstring, playerIsX) >= 81 && retrieveStatesFromFreq(gridstring, playerIsX) >=81)){

         for (int i=0; i < grid.length; i++){
             for (int j=0; j < grid[i].length; j++){
             if (grid[i][j] == ' '){
                String sql  = "INSERT INTO qtable(state_string, action_x, action_y, value) VALUES(?,?,?,?);\n";
                String sql2 = "INSERT INTO freq(state_string, action_x, action_y, count) VALUES(?,?,?,?);\n";     
                
                try{
                   PreparedStatement pstmt = c.prepareStatement(sql);
                   PreparedStatement pstmt2 = c.prepareStatement(sql2);
                   pstmt.setString(1, gridstring);
                   pstmt.setInt(2, i);
                   pstmt.setInt(3, j);
                   pstmt.setFloat(4, (float)0.0);
                   
                   pstmt2.setString(1, gridstring);
                   pstmt2.setInt(2, i);
                   pstmt2.setInt(3, j);
                   pstmt2.setInt(4, 0);
                   pstmt.executeUpdate();
                   pstmt2.executeUpdate();
                }
                   catch (SQLException e){
                System.out.println(String.format("Error: %s", e.getMessage()));
                }
              }
           }
         }     
      }     
      return new String("Propogation Insert Successful");
   }
   
   public ArrayList fFunction(String state_string, boolean playerIsX){
   // f function
   // argmax a'  f(Q[s',a'], Nsa[s',a']) = / R^+ if n < Nsubscript e 
   //                 u          n         < actual utility   otherwise
   //                                      \
   //
   // Where R^+ = 2 and Ne = 5           = / 2 if n < 5
   //                                      < actual utility  otherwise
   //                                      \

   // Letting sqlite do the heavy lifting here
   // SELECT *, CASE WHEN count < 5 THEN 2 ELSE value END AS f_value FROM(
   // SELECT * from qtable INNER JOIN freq ON qtable.state_string = freq.state_string AND qtable.action_x = freq.action_x AND qtable.action_y = freq.action_y
   // )
   // ORDER BY f_value DESC LIMIT 1
   // )
   
   if (playerIsX){}
   else {
     state_string = swapPlayersInGrid(state_string);
   }
      
   String sql = "SELECT *, CASE WHEN count < 2 THEN 5 ELSE value END AS f_value FROM(" +
                "SELECT * from qtable INNER JOIN freq ON qtable.state_string = freq.state_string AND qtable.action_x = freq.action_x AND qtable.action_y = freq.action_y" +
                ")" + 
                "WHERE state_string = ? ORDER BY f_value DESC LIMIT 1";
   
   ArrayList instance = new ArrayList<Object>();
     
   try{
     PreparedStatement pstmt = c.prepareStatement(sql);
     pstmt.setString(1, state_string);
     
     ResultSet rs = pstmt.executeQuery();
     
     while (rs.next()) {
        instance.add(rs.getString("state_string")); 
        instance.add(rs.getInt("count"));
        instance.add(rs.getInt("value"));
        instance.add(rs.getFloat("f_value"));
        instance.add(rs.getInt("action_x"));
        instance.add(rs.getInt("action_y"));
        //result.add(instance);
     }
     
   }
   catch (SQLException e) {
      System.out.println(e.getMessage());
   }
   return instance;
   }

   
   //public char[][] convertStringtoGrid(String gridString){   
   //}
  
   /* Contructor for database interface
   *  Creates a sqlite interface for use by its 
   *  other methods */
   public DatabaseInterface(){
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:qlearner.db");
     
         // create Q table if they dont exist         
         // need to add unique constraints for action_x, action_y and state string

         Statement stmt = null;
         Statement stmt2 = null;
      
         stmt = c.createStatement();
         String sql = "CREATE TABLE if not exists qtable" +
                   "(id INTEGER PRIMARY KEY NOT NULL ," +
                   "state_string CHAR(128),"+
                   "action_x INT NOT NULL," +
                   "action_y INT NOT NULL," +
                   "value FLOAT NOT NULL," +
                   "UNIQUE(state_string,action_x,action_y) ON CONFLICT IGNORE)";
         stmt.executeUpdate(sql);
         stmt.close();
      
         // create frequency table
         // need to add unique constraints for action_x, action_y and state string
         stmt2 = c.createStatement();
         String sql2 = "CREATE TABLE if not exists freq" +
                   "(id INTEGER PRIMARY KEY NOT NULL ," +
                   "state_string CHAR(128),"+
                   "action_x INT NOT NULL," +
                   "action_y INT NOT NULL," +
                   "COUNT INT NOT NULL," +
                   "UNIQUE(state_string,action_x,action_y) ON CONFLICT IGNORE)";
         stmt2.executeUpdate(sql2);
         stmt2.close();

         } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
         }
         System.out.println("Opened database successfully");
      } // end Database constructor
      
   /*
   *  Retrieve the state from the Qtable
   */
   public ArrayList selectStateFromQTable(String state, boolean playerIsX, int x, int y){
      ArrayList result = new ArrayList<Object>();
      if (playerIsX){}
      else{
         state = swapPlayersInGrid(state);
      }

      
      String sql = String.format("SELECT * FROM qtable WHERE state_string = ? AND action_x = ? AND action_y = ?");
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setString(1, state);
         pstmt.setInt(2, x);
         pstmt.setInt(3, y);
         
         ResultSet rs = pstmt.executeQuery();
         while (rs.next()) {
            ArrayList instance = new ArrayList<Object>();
            instance.add(rs.getInt("id"));
            instance.add(rs.getString("state_string")); 
            instance.add(rs.getInt("action_x"));
            instance.add(rs.getInt("action_y"));
            instance.add(rs.getFloat("value"));
            result.add(instance);
         }   
         
      }
      catch (SQLException e){
         System.out.println(e.getMessage());
      }
   return result;
   } // end SelectStateFromQTable
   
   /*
   *  Retrieve the states from the freq table
   */ 
   public ArrayList selectStateFromFreqTable(String state, boolean playerIsX, int x, int y){
      ArrayList result = new ArrayList<Object>();
      
      if (playerIsX){}
      else{
         state = swapPlayersInGrid(state);
      }
      
      String sql = String.format("SELECT * FROM freq WHERE state_string = ? AND action_x = ? AND action_y = ?");
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setString(1, state);
         pstmt.setInt(2, x);
         pstmt.setInt(3, y);
         
         ResultSet rs = pstmt.executeQuery();
         while (rs.next()) {
            ArrayList instance = new ArrayList<Object>();
            instance.add(rs.getInt("id"));
            instance.add(rs.getString("state_string"));
            instance.add(rs.getInt("action_x"));
            instance.add(rs.getInt("action_y")); 
            instance.add(rs.getInt("count"));
            result.add(instance);
         }   
         
      }
      catch (SQLException e){
         System.out.println(e.getMessage());
      }
   return result;
   } // end selectStateFromFreqTable
    
     
   /* Increments the frequency table given the unqiue
   *  combination of state and action (x,y)
   *  If row doesn't exist yet, it creates it
   */
   public void incrementStateFreq(String state, boolean playerIsX, int x, int y){
      // Check if state action pair exists
      if (playerIsX){}
      else
      {
         state = swapPlayersInGrid(state);
      }

      
      String sql = "SELECT id, state_string, action_x, action_y, count FROM freq WHERE state_string = ? AND action_x = ? AND action_y = ?";
      ArrayList instance = new ArrayList<Object>();
      
      try{
        PreparedStatement pstmt = c.prepareStatement(sql);
        pstmt.setString(1, state);
        pstmt.setInt(2, x);
        pstmt.setInt(3, y);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
           instance.add(rs.getString("state_string")); 
           instance.add(rs.getInt("count"));
           instance.add(rs.getInt("action_x"));
           instance.add(rs.getInt("action_y"));
        }
        
      }
      catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      
      // If not, create it and set it to 1
      if (instance.size() == 0){
         incrementStateFreq((String)instance.get(1), playerIsX, (int)instance.get(3), (int)instance.get(4));
      }
      // Else, increment the value and update the row
      try{
         String sqlupdate = "UPDATE freq SET count = ? " +
                            "WHERE state_string = ? AND action_x = ? AND action_y = ?";  
                   
         PreparedStatement pstmtupdate = c.prepareStatement(sqlupdate);
         pstmtupdate.setInt(1, (int)instance.get(1)+1);
         pstmtupdate.setString(2, state);
         pstmtupdate.setInt(3, x);
         pstmtupdate.setInt(4, y);
         pstmtupdate.executeUpdate();
      }
      catch (SQLException e){
         System.out.println(e.getMessage());
      }
   }
   
    
   public String insertStateAndActionsFreq(String state, boolean playerIsX, int action_x, int action_y, int count){
      String sql = "INSERT INTO freq(state_string, action_x, action_y, count) VALUES(?,?,?,?)";
      if (playerIsX){}
      else
      {
         state = swapPlayersInGrid(state);
      }

      // Check if it already exists first
      ArrayList result = selectStateFromFreqTable(state, playerIsX, action_x, action_y);
      if (result.size() == 0){
         try{
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, state);
            pstmt.setInt(2, action_x);
            pstmt.setInt(3, action_y);
            pstmt.setInt(4, count);
            pstmt.executeUpdate();
         }
         catch (SQLException e){
            System.out.println(String.format("Error: %s", e.getMessage()));
         }      
      }
      return new String("Freq table Insert Successful");
   }
   
   public String insertStateAndActionsQTable(String state, boolean playerIsX, int action_x, int action_y, float value){
      String sql = "INSERT INTO qtable(state_string, action_x, action_y, value) VALUES(?,?,?,?)";
      if (playerIsX){}
      else
      {
         state = swapPlayersInGrid(state);
      }

      // Check if it already exists first
      ArrayList result = selectStateFromQTable(state, playerIsX, action_x, action_y);
      if (result.size() == 0){
         try{
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, state);
            pstmt.setInt(2, action_x);
            pstmt.setInt(3, action_y);
            pstmt.setFloat(4, value);
            pstmt.executeUpdate();
         }
         catch (SQLException e){
            System.out.println(String.format("Error: %s", e.getMessage()));
         }      
      }
      return new String("Qtable Insert Successful");
   }
   
   public String insertStateAndFreq(String state, boolean playerIsX, int action_x, int action_y, int count){
      if (playerIsX){}
      else
      {
         state = swapPlayersInGrid(state);
      }
   
      String sql = "INSERT INTO freq(state_string, action_x, action_y, count) VALUES(?,?,?,?)";
      System.out.println(String.format("%d %d %d \n%s", action_x,action_y,count,state));
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setString(1, state);
         pstmt.setInt(2, action_x);
         pstmt.setInt(3, action_y);
         pstmt.setInt(4, count);
         pstmt.executeUpdate();
      }
      catch (SQLException e){
         System.out.println(String.format("Error: %s", e.getMessage()));
      }
      
      return new String("Update successful");
   }
   
   public String updateQStateValue(String state, boolean playerIsX, int action_x, int action_y, float value){
      // try and get the unique state and actions first...
      if (playerIsX){}
      else
      {
         state = swapPlayersInGrid(state);
      }

      ArrayList result = selectStateFromQTable(state, playerIsX, action_x, action_y);
      String sql = "";
      
      if (result.size() > 0){
        // Exists, use update
        sql = "UPDATE qtable SET value = ? WHERE state_string = ? AND action_x = ? AND action_y = ?";
      }
      
      else {
        // Doesn't exist, so create it
        sql = "INSERT INTO qtable(value, state_string, action_x, action_y) VALUES(?,?,?,?)";
      }
      
      try{
         PreparedStatement pstmt = c.prepareStatement(sql);
         pstmt.setFloat(1, value);
         pstmt.setString(2, state); 
         pstmt.setInt(3, action_x); 
         pstmt.setInt(4, action_y); 
         pstmt.executeUpdate();
      }
      catch (SQLException e){
         System.out.println(String.format("Error: %s", e.getMessage()));
      }
   return String.format("Updated");
   }
}