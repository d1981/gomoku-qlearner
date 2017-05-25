Group 7: Joshua Kapple

3/21/2017

I. Acknowledgment
II. How to run the agent
III. What I did

---------------------------------------------------------------------------------
I. Acknowledgment

	My implementation of reinforced learning uses the q-learning algorithm from the book with the basic exploratory f-function. I used a sqlite database to store the two tables required of q-learning, frequencies and q-values. 
	I thought this would be a great way to store its learning and I could use sqlite to handle some of the algorithm since it would be good at running queries against the data. This did make implementation of finding the f-function value relatively easy.  
	
	Unfortunetly, the constant seemingly random look ups and state propogation using a database slows the algorithm to a crawl. I did not realize this until it was too late. Each game iteration takes about 7 seconds :(
	After a few days my trainer created a database of 3.5GB! Although, I did witness it eventually learn to defeat moves made by playing against Alpha-Beta (because its moves were predictable), 
	playing against itself or random did not seem to lead to any results mostly because it takes forever. 
	
	I believe this is because the f-function I utilized checked every state 5 times before using its actual value. Combined with the enourmous state-space of a 9x9 grid and the slow performance caused by tieing the tables to sqlite, I'm not sure
	if I have enough time or disk space for convergence. I was unable to implement a rollback feature where it would update back through the chain of played states from the reward state, so it's really depending on hitting the exact same sequence of states again to properly propogate the values backwards. 

---------------------------------------------------------------------------------
II. How to run the program. 
The program will create a new database file if qlearner.db is not present in the same directory as the src code. You can download what I was able to achieve so far from https://www.joshkapple.com/wp-content/uploads/2017/03/21/qlearner.db. 

	1) Copy qlearner.db to src/
	1) Open GomokuServer.rkt in Dr.racket and run.
	2) To play against it run SinglePlayer.java 
	   a) Open either RandomPlayer.rkt or ManualPlayer.rkt and run whichever you opened.
	3) To continue training, run Trainer.java

---------------------------------------------------------------------------------
III. What I did.
	1. Created DatabaseInterface.java to handle creating tables, querying, and updating rows in a sqlite database file. 
	2. Created a Trainer.java class to handle playing the q-learner against another version of itself.
	3. Re-used GomokuDriver to handle creating two player instances that will communicate with GomokuServer.
	3. Stored all states in the database as perspective from X. 
	4. Implemented perspective swaps to store states that O saw as if it was X. 
	5. Implemented q-learning. 
	6. Created SinglePlayer.java to play against the q-learner.
	6. Lots of debugging.

---------------------------------------------------------------------------------    
IV. To Do. 
    1) Identify features of the gomoku board to improve generalization
    2) Add multi-threading to maximize processor usage during search