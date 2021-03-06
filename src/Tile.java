/*************************************
 * Tile-Sliding Puzzle
 *************************************
 * contact:  zhengyi.le@dartmouth.edu
 * Date: 1/22/2004
 * Modified by Richard Jensen, 2015
 *************************************
 */


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;





public class Tile extends JApplet implements ActionListener, ChangeListener {
	//Some constants to be used elsewhere
	private final static int WINDOW_WIDTH = 560;
	private final static int WINDOW_HEIGHT = 780;
	
	final static int MIN_DELAY = 0;
	final static int MAX_DELAY = 3000;
	final static int INI_DELAY = 1500;
	
	final static int MIN_DIFFICULTY = 0;
	final static int MAX_DIFFICULTY = 100;
	final static int INI_DIFFICULTY = 20; //initial difficulty setting
	
	//determines the state of the GUI
	final static int STOP = 0;
	final static int RANDOMIZE = 1;
	final static int IDLE = 2;
	final static int START = 3;
	final static int PLAY = 4;
	
	JButton stop = new JButton("Stop Search");
	JButton start = new JButton("Start Search");
	JButton play = new JButton("Play Solution");
	JButton randomize = new JButton("Mix it!");
	JSlider sliderDisplay = new JSlider(JSlider.HORIZONTAL, MIN_DELAY,
			MAX_DELAY, INI_DELAY);
	JSlider sliderRandomize = new JSlider(JSlider.HORIZONTAL, MIN_DIFFICULTY,
			MAX_DIFFICULTY, INI_DIFFICULTY);
	JLabel stepCounterLabel = new JLabel("<html>Nodes expanded: <br>" + "0" +
			"</html>");
	JLabel soluLabel = new JLabel(" ");

	//Checkboxes for selecting the algorithms
	CheckboxGroup cbg = new CheckboxGroup();
	Checkbox cbBfs = new Checkbox("Breadth First", cbg, true);
	Checkbox cbDfs = new Checkbox("Depth First", cbg, false);
	Checkbox cbIt = new Checkbox("Iterative Deepening", cbg, false);
	Checkbox cbAStarTiles = new Checkbox("A*-Tiles", cbg, false);
	Checkbox cbAStar = new Checkbox("A*-2", cbg, false);

	Checkbox cbDisplay = new Checkbox("Display Search");
	MyBoard b = new MyBoard(); // this MyBoard b is used to display the graphic of the current borad.

	public void init() {
		getContentPane().setLayout(new BorderLayout());

		// Add the north Panel.
		Panel sliderDisplayPanel = new Panel();
		sliderDisplayPanel.setLayout(new GridLayout(3, 1));

		JLabel sliderDisplayLabel = new JLabel("Display Interval : ( ms )",
				JLabel.CENTER);
		sliderDisplayPanel.add(sliderDisplayLabel);
		sliderDisplayPanel.add(sliderDisplay);
		sliderDisplayPanel.add(new JLabel("  "));

		sliderDisplay.setMajorTickSpacing(MAX_DELAY / 3);
		sliderDisplay.setMinorTickSpacing(MAX_DELAY / 15);
		sliderDisplay.setPaintTicks(true);
		sliderDisplay.setPaintLabels(true);

		getContentPane().add("North", sliderDisplayPanel);

		// Add the board onto the center of the frame.
		getContentPane().add("Center", b);

		// Add the  east Panel.
		Panel eastPanel = new Panel();

		eastPanel.setLayout(new BorderLayout());
		eastPanel.add("North", new JLabel("  "));
		eastPanel.add("East", new JLabel("  "));
		eastPanel.add("West", new JLabel("  "));
		eastPanel.add("South", new JLabel("  "));

		Panel eastCenterPanel = new Panel();
		eastCenterPanel.setLayout(new GridLayout(4, 1));

		Panel checkboxPanel = new Panel();
		checkboxPanel.setLayout(new GridLayout(4, 1));
		checkboxPanel.add(cbBfs);
		checkboxPanel.add(cbDfs);
		checkboxPanel.add(cbIt);
		checkboxPanel.add(cbAStarTiles);
		checkboxPanel.add(cbAStar);

		eastCenterPanel.add(checkboxPanel);
		eastCenterPanel.add(cbDisplay);
		eastCenterPanel.add(stepCounterLabel);
		eastCenterPanel.add(soluLabel);

		eastPanel.add("Center", eastCenterPanel);

		getContentPane().add("East", eastPanel);

		// Add a blank west Panel.
		Panel westPanel = new Panel();
		westPanel.setLayout(new GridLayout(1, 1));
		westPanel.add(new JLabel("     "));
		getContentPane().add("West", westPanel);

		// Add the south Panel.
		Panel southPanel = new Panel();
		southPanel.setLayout(new BorderLayout());

		Panel buttonPanel = new Panel();

		buttonPanel.setLayout(new GridLayout(5, 3));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(randomize);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(stop);
		buttonPanel.add(start);
		buttonPanel.add(play);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel(" "));
		southPanel.add("South", buttonPanel);

		Panel sliderRandomizePanel = new Panel();
		sliderRandomizePanel.setLayout(new GridLayout(3, 1));
		sliderRandomizePanel.add(new JLabel(" "));

		JLabel sliderRandomizeLabel = new JLabel("Randomize the initial board:  (easy --> difficult)",
				JLabel.CENTER);
		sliderRandomizePanel.add(sliderRandomizeLabel);
		sliderRandomizePanel.add(sliderRandomize);

		sliderRandomize.setMajorTickSpacing(MAX_DIFFICULTY / 4);
		sliderRandomize.setMinorTickSpacing(MAX_DIFFICULTY / 20);
		sliderRandomize.setPaintTicks(true);
		sliderRandomize.setPaintLabels(true);

		southPanel.add("North", sliderRandomizePanel);

		getContentPane().add("South", southPanel);
		sliderDisplay.addChangeListener(this);
		sliderRandomize.addChangeListener(this);

		randomize.addActionListener(this);
		stop.addActionListener(this);
		start.addActionListener(this);
		play.addActionListener(this);

		b.start();
	}

	//-------------------------------------------------------------
	// Listen to the actions of the buttons and the slider.
	//-------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			b.stopAlgorithm = true;
			b.setStatus(STOP);
		} else if (e.getSource() == randomize) {
			b.setStatus(RANDOMIZE);
		} else if (e.getSource() == start) {
			b.setStatus(START);
			stepCounterLabel.setText("<html>Nodes expanded: <br>" + "0" +
					"</html>");
			soluLabel.setText(" ");
		} else if (e.getSource() == play) {
			b.setStatus(PLAY);
		}
	}

	//-------------------------------------------------------
	// Reset the delay value of display from the slider
	//-------------------------------------------------------
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == sliderDisplay) {
			int value = sliderDisplay.getValue();
			b.setDelay(value);
		}
	}

	//--------------------------------------------------------
	// Create and run the application.
	//--------------------------------------------------------
	public static void main(String[] args) {
		JFrame applicationFrame = new JFrame(
				"8 Puzzle");

		// kill application when window closes
		applicationFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Tile appletObject = new Tile();
		appletObject.init();

		applicationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		applicationFrame.getContentPane().add(appletObject);
		applicationFrame.pack();

		applicationFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		applicationFrame.setVisible(true);
	}

	// CS26110 Assignment
	//----------------------------------------------------------------
	// Define the board and the operations on it.
	// The grid is stored in a 2D array in columns, a 0 is used to indicate the blank
	//----------------------------------------------------------------
	private class MyBoard extends Canvas implements MouseListener, Runnable, Comparable<MyBoard> {
		final static int BOARD_SIZE = 3; //the size of the puzzle, 3x3
		int delay = INI_DELAY;
		int status = IDLE;
		int stepCounter = -1; //used to keep track of the number of expanded nodes
		private Thread animationThread;
		int[][] grid = new int[BOARD_SIZE][BOARD_SIZE]; //the 3x3 grid of 'tiles' = just integers
		MyBoard next;
		MyBoard parent; //the parent of this board - used to trace back the path to the solution from the goal node/board
		Graphics gr = this.getGraphics();
		int gWidth;
		int gHeight;
		
		// the user can decide to stop the algorithm if it's taking too long
		// Note that a new board will need to be created if another algorithm is to be run after stopping
		boolean stopAlgorithm; 
		
		MyBoard temp;
		int depth; //the depth of the node in the search, corresponds to g(n)
		
		// CS26110 Assignment
		/** These are currently unused but might be useful for your A* and heuristic implementation... **/
		int heuristic; //h(n)
		int f;  //f(n)
		
		//What the goal state looks like in this representation
		int[][] goalState = {{0, 3, 6}, {1, 4, 7}, {2, 5, 8}};
		
		// These map from the tile to its coordinates in the goalState array
		int[] xcoord = {0 , 1 , 2 , 0 , 1 , 2 , 0 , 1 , 2};
		int[] ycoord = {0 , 0 , 0 , 1 , 1 , 1 , 2 , 2 , 2};  


		// Constructor of the class of MyBoard
		//---------------------------------------------------------
		public MyBoard() {
			addMouseListener(this);
			depth = 0;
		}

		// Constructor of the class of MyBoard which also sets the depth of the node
		//---------------------------------------------------------
		public MyBoard(int d) {
			addMouseListener(this);
			depth = d;
		}

		// Reset the status of the board.
		//--------------------------------------------------------
		public void setStatus(int newStatus) {
			status = newStatus;
		}

		//print the grid in a one dimensional format
		//In this format, the goal would be printed: { 0 1 2 3 4 5 6 7 8 }
		public String print() {
			String ret = "{ ";

			for (int i=0;i<BOARD_SIZE;i++) {
				for (int j=0;j<BOARD_SIZE;j++) {
					ret += grid[j][i]+" ";
				}
			}

			return ret +"}";
		}

		// CS26110 Assignment (though not too important)
		//Used for the explored list. Converts the grid into a String and this is used to determine if the grid has been seen before. 
		//The String is used to obtain a unique integer identifier for the String (and hence the grid) and this can be used to see if
		//a particular grid is identical to another one (the integer values will be the same in this case). You could also use String.equals.
		public String hash() {
			String ret="";

			for (int i=0;i<3;i++) {
				for (int j=0;j<3;j++) {
					ret+=grid[i][j];
				}
			}

			return ret;
		}

		// Start a new Thread to listen to the status change.
		//---------------------------------------------------------
		public void start() {
			if ((animationThread == null) || (!animationThread.isAlive())) {
				animationThread = new Thread(this);
			}

			animationThread.start();
		}

		// Stop the Thread.
		//------------------------------------------------------------
		public void stop() {
			if ((animationThread != null) && (animationThread.isAlive())) {
				// look at the Java Tutorial or
				// http://java.sun.com/products/jdk/1.2/docs/guide/misc/threadPrimitiveDeprecation.html
				animationThread = null;
			}
		}

		// Run the thread. This is required when you implement the Runnable interface.
		// Namely, repaint the display board according to the board status.
		//-----------------------------------------------------------
		public void run() {
			System.out.println("Tile Puzzle is running ... ...");

			while (true) {
				switch (status) {
				case RANDOMIZE:
					temp = null;
					initBoard();
					paintSlow(b.getGraphics());

					break;

				case START:

					if (cbg.getSelectedCheckbox() == cbBfs) {
						temp = bfs(this);
					} else if (cbg.getSelectedCheckbox() == cbDfs) {
						temp = dfs(this);

					} else if (cbg.getSelectedCheckbox() == cbIt) {
						temp = iterativeDeepening(this);
					} else if (cbg.getSelectedCheckbox() == cbAStar) {
						//temp = aStar2(this);
					} else if (cbg.getSelectedCheckbox() == cbAStarTiles) {
						temp = aStarTiles(this);
					}


					break;

				case PLAY:
					play(temp);

					break;

				default:
					break;
				}
			}
		}

		// Initialises the configuration of this board according to the difficulty slider.
		// The difficulty slider sets a number of (random) moves to make to the initial grid to
		// scramble it. The higher the difficulty setting, the more scrambling will result
		//
		// As an aside, this guarantees that a solvable puzzle will be produced. It's possible to 
		// generate unsolvable 8 puzzles if you're not careful (i.e. just randomly generating grids).
		//-----------------------------------------------------------
		public void initBoard() {
			int difficulty;
			int counter;
			int rand;
			MyBoard auxBoard = new MyBoard();
			MyBoard temp;

			stopAlgorithm = false;
			sliderDisplay.setValue(INI_DELAY);
			stepCounterLabel.setText("<html>Nodes expanded: <br>0</html>");
			soluLabel.setText("Initial Board");

			//Initialise the grid
			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++) 
					grid[i][j] = (j * BOARD_SIZE) + i;


			//get the value from the GUI
			difficulty = sliderRandomize.getValue();

			//For each difficulty 'step', make a random move
			//This can actually go backwards by undoing the previously made move
			while (difficulty > 0) {
				auxBoard.next = expandAll(this);
				temp = auxBoard.next;
				counter = 0;

				while (temp != null) {
					temp = temp.next;
					counter++;
				}

				// Select a random successor from all the successors
				rand = (int) (counter * Math.random()) + 1;

				while (rand > 0) {
					auxBoard = auxBoard.next;
					rand--;
				}

				copyBoard(this, auxBoard);

				difficulty--;
			}
			
			repaint();
			sliderDisplay.setValue(INI_DELAY);
			status = IDLE;
		}


		// Set the delay value of paint
		//---------------------------------------------------------
		public int setDelay(int newDelay) {
			delay = newDelay;

			return delay;
		}

		//Paint the entire board.
		//--------------------------------------------------------
		public void paint(Graphics g) {
			gr = g;
			gWidth = getBounds().width / BOARD_SIZE;
			gHeight = getBounds().height / BOARD_SIZE;

			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++)
					drawCell(i, j);
		}

		// Paint the entire board with the delay controlled by the slider.
		//---------------------------------------------------------------
		public void paintSlow(Graphics g) {
			repaint();

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}

		// Draw a specific cell on the board.
		//---------------------------------------------------------
		public void drawCell(int x, int y) {
			// Draw the outline of the cell.
			gr.setColor(Color.black);
			gr.drawRect(x * gWidth, y * gHeight, gWidth, gHeight);

			// Draw the background of the cell.
			gr.setColor(Color.black);
			gr.setFont(new Font("SansSerif", Font.BOLD, 14));
			gr.drawRect((x * gWidth) + 4, (y * gHeight) + 4, gWidth - 8,
					gHeight - 8);

			// Draw the tile in the cell.
			switch (grid[x][y]) {
			case 0:
				break;

			default:
				gr.setColor(Color.white);
				gr.fillRect((x * gWidth) + 15, (y * gHeight) + 15, gWidth - 30,
						gHeight - 30);
				gr.setColor(Color.black);
				gr.drawChars(Integer.toString(grid[x][y]).toCharArray(), 0,
						Integer.toString(grid[x][y]).length(),
						(x * gWidth) + (gWidth / 2), (y * gHeight) + (gHeight / 2));

				break;
			}
		}

		// Handle the mouse actions.
		// The five methods are required when you implement the MouseListener interface.
		//--------------------------------------------------------------------------
		public void mousePressed(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		// CS26110 Assignment
		// See whether current configuration is a goal.
		// The relationship between the graph and coordinates is below:
		// (0,0) (1,0) (2,0)
		// (0,1) (1,1) (2,1)
		// (0,2) (1,2) (2,2)
		//-----------------------------------------------------------------------
		public boolean isGoal(MyBoard mb) {
			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++)
					if (mb.grid[i][j] != ((j * BOARD_SIZE) + i)) {
						return false;
					}

			return true;
		}

		// Returns true if the coordinates are a legal board position.
		//-----------------------------------------------------------------------
		public boolean legal(int x, int y) {
			return ((x >= 0) && (x < BOARD_SIZE) && (y >= 0) &&
					(y < BOARD_SIZE));
		}

		// Copy the grid from an old board to a new board.
		//-----------------------------------------------------------------------
		public void copyBoard(MyBoard mbNew, MyBoard mbOld) {
			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++)
					mbNew.grid[i][j] = mbOld.grid[i][j];
		}


		// find the ancestor link of the solution board.
		//------------------------------------------------------------------------
		public MyBoard findAncestors(MyBoard mb) {
			MyBoard boardList = null;
			MyBoard temp;
			MyBoard tempNew;

			temp = mb;

			while (temp != null) {
				tempNew = new MyBoard();
				copyBoard(tempNew, temp);
				tempNew.next = boardList;
				boardList = tempNew;
				temp = temp.parent;
			} 

			return boardList;
		}

		// display a board List for a solution.
		//------------------------------------------------------------------------
		public void displaySolution(MyBoard mb) {
			MyBoard temp = mb;
			stepCounter = -1;

			while (temp != null) {
				stepCounter++;
				stepCounterLabel.setText("<html>Solution step: <br>" +
						Integer.toString(stepCounter) + "</html>");
				copyBoard(this, temp);
				paintSlow(this.getGraphics());
				temp = temp.next;
			}
		}

		// After search, play the solution.
		//------------------------------------------------------------------------
		public void play(MyBoard mb) {
			sliderDisplay.setValue(INI_DELAY);
			displaySolution(findAncestors(mb));
			status = IDLE;
		}

		// CS26110 Assignment
		// Expand all the possible succeeding configurations. These are the actions.
		// The current board has 2, 3, or 4 succeeding boards.
		// You might need to alter this method depending on how you implement the heuristic calculations
		//------------------------------------------------------------------------
		public void expandAll(MyBoard mb, Collection<MyBoard> list, int depth)  {
			int p = 0;
			int q = 0;

			// locate the "0" tile.
			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++)
					if (mb.grid[i][j] == 0) {
						p = i;
						q = j;
					}

			if (legal(p, q - 1)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p][q - 1] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p][q - 1];
				child.parent = mb;

				list.add(child);
			}

			if (legal(p, q + 1)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p][q + 1] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p][q + 1];
				child.parent = mb;

				list.add(child);
			}

			if (legal(p - 1, q)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p - 1][q] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p - 1][q];
				child.parent = mb;

				list.add(child);
			}

			if (legal(p + 1, q)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p + 1][q] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p + 1][q];
				child.parent = mb;

				list.add(child);
			}
		}
		
		//This is the edited expand all method that is used to expand
		//all of the children for the A* algorithms
		public void expandAllA(MyBoard mb, HashSet<String> explored2, PriorityQueue open, int depth, String checkHeuristic){
			int p = -1;
			int q = -1;
			// locate the "0" tile.
			for (int i = 0; i < BOARD_SIZE; i++){
				for (int j = 0; j < BOARD_SIZE; j++){
					if (mb.grid[i][j] == 0) {
						p = i;
						q = j;
					}
				}
			}
			if (legal(p, q - 1)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p][q - 1] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p][q - 1];
				child.parent = mb;
				if (depth < child.depth) {
					if (open.contains(child)) {
						open.remove(child);
					}
						if (explored2.contains(child)) {
							explored2.remove(child);
						}
					}
						
					if (!open.contains(child) && (!alreadyVisited(child))) {
						if(checkHeuristic == "heuristic"){
							child.heuristic = heuristic(child, p, q-1);
						}
						else if(checkHeuristic == "manhattanHeuristic"){
							child.heuristic = manhattanHeuristic(child, p, q-1);
						}
						child.f = child.heuristic + child.depth;
						open.add(child);
					}
					else if(explored2.contains(child)){
						System.out.println("true");
					}
			}

			if (legal(p, q + 1)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p][q + 1] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p][q + 1];
				child.parent = mb;
				if (depth < child.depth) {
					if (open.contains(child)) {
						open.remove(child);
					}
						if (explored2.contains(child)) {
							explored2.remove(child);
						}
					}
						
					if (!open.contains(child) && !(alreadyVisited(child))){
						if(checkHeuristic == "heuristic"){
							child.heuristic = heuristic(child, p, q-1);
						}
						else if(checkHeuristic == "manhattanHeuristic"){
							child.heuristic = manhattanHeuristic(child, p, q-1);
						}
						child.f = child.heuristic + child.depth;
						open.add(child);
					}
			}

			if (legal(p - 1, q)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p-1][q] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p-1][q];
				child.parent = mb;
				if (depth < child.depth) {
					if (open.contains(child)) {
						open.remove(child);
					}
						if (explored2.contains(child)) {
							explored2.remove(child);
						}
					}
						
					if (!open.contains(child) && !(alreadyVisited(child))) {
						int a= child.grid[p-1][q];
						if(checkHeuristic == "heuristic"){
							child.heuristic = heuristic(child, p, q-1);
						}
						else if(checkHeuristic == "manhattanHeuristic"){
							child.heuristic = manhattanHeuristic(child, p, q-1);
						}
						child.f = child.heuristic + child.depth;
						open.add(child);
					}
			}

			if (legal(p + 1, q)) {
				MyBoard child = new MyBoard(depth);
				copyBoard(child, mb);

				child.grid[p+1][q] = mb.grid[p][q];
				child.grid[p][q] = mb.grid[p+1][q];
				child.parent = mb;
				if (depth < child.depth) {
					if (open.contains(child)) {
						open.remove(child);
					}
						if (explored2.contains(child)) {
							explored2.remove(child);
						}
					}
						
					if (!open.contains(child) && !(alreadyVisited(child))) {
						int a = child.grid[p+1][q];
						if(checkHeuristic == "heuristic"){
							child.heuristic = heuristic(child, p, q-1);
						}
						else if(checkHeuristic == "manhattanHeuristic"){
							child.heuristic = manhattanHeuristic(child, p, q-1);
						}
						child.f = child.heuristic + child.depth;
						open.add(child);
					}
			}
		}


		// Depth first search (DFS)
		// Uses a stack. (last-in-first-out)
		// As you will see, DFS is not good for solving 8 puzzles... 
		//--------------------------------------------------------------------
		public MyBoard dfs(MyBoard mb)  {
			MyBoard board;
			Stack<MyBoard> frontier = new Stack<MyBoard>();
			frontier.add(mb);

			explored = new HashSet<String>();
			stepCounter = -1;

			soluLabel.setText("Searching ...");
			boolean displaySearch = cbDisplay.getState();

			board = frontier.pop();

			while ((!stopAlgorithm) && (!isGoal(board)) && (board!=null)) {	
				if (!alreadyVisited(board)) {
					// Display the step counter
					stepCounter++;
					stepCounterLabel.setText("<html>Nodes explored: <br>" + Integer.toString(stepCounter) + "</html>");

					// Display the inner node
					if (displaySearch) {
						copyBoard(this, board);
					}

					// Add it to the searched board list.
					addToExplored(board);

					// Attach the expanded succeeding nodes onto the top of the stack.
					expandAll(board, frontier, board.depth+1);
				}

				board = frontier.pop();
			}

			return finalise(board,displaySearch);
		}



		//A HashMap has to be used for IDS as you also need to keep track of the depth of nodes:
		//http://stackoverflow.com/questions/12598932/how-to-store-visited-states-in-iterative-deepening-depth-limited-search
		HashMap<String, Integer> exploredIDS;
		
		public boolean alreadyVisitedIDS(MyBoard board) {
			String hash = board.hash();
			if (exploredIDS.containsKey(hash)) {
				int depth = exploredIDS.get(hash);
				//If the previously encountered node was deeper than the current node 'board' then pretend that we haven't seen it before
				//This is done as the higher up node could lead to a shallower goal node ultimately.
				if (depth>board.depth) {
					return false; //pretend we haven't seen this before  (the current board is higher up the tree)
				}
				else return true; //say that we have seen this before (the current node is at least at the depth of the previously stored node)
			}
			else return false; //we haven't seen this node before
		}

		//Add to the explored list. If this state has not been encountered before, add it to the list
		public void addToExploredIDS(MyBoard board) {
			String hash = board.hash(); //get the unique identifier for this board
			if (!exploredIDS.containsKey(hash)) exploredIDS.put(hash,board.depth); //if it doesn't exist already then add it
			else { //replace the depth indicator for this existing board to the smallest value seen
				int depth = exploredIDS.get(hash);
				exploredIDS.put(hash, Math.min(depth,board.depth));
			}
		}
		
		//Iterative deepening search - an incremental depth limit until a solution is reached
		public MyBoard iterativeDeepening(MyBoard mb) {
			MyBoard board = null;
			stepCounter = -1;

			soluLabel.setText("Searching ...");
			boolean displaySearch = cbDisplay.getState();
		
			for (int depth=0;depth<1000000000;depth++) {
				Stack<MyBoard> frontier = new Stack<MyBoard>();
				mb.depth=0;
				frontier.push(mb);
				exploredIDS = new HashMap<String, Integer>();
				
				while ((!stopAlgorithm)&&(frontier.size()>0)) {	
					board = frontier.pop();
					if (isGoal(board)) return finalise(board,displaySearch); 
					
					if (!alreadyVisitedIDS(board)&&board.depth<depth) {
						// Display the step counter
						stepCounter++;
						stepCounterLabel.setText("<html>Nodes expanded: <br>" + Integer.toString(stepCounter) + "<br>Depth limit: "+depth+"</html>");

						// Display the inner node
						if (displaySearch) {
							copyBoard(this, board);
							paintSlow(this.getGraphics());
						}

						// Add it to the searched board list.
						addToExploredIDS(board);

						// Attach the expanded succeeding nodes onto the top of the stack.
						expandAll(board, frontier, board.depth+1);
					}

				}
				if (stopAlgorithm || isGoal(board)) break;
				if (stopAlgorithm) break;
			}
			return finalise(board,displaySearch);
		}

		// CS26110 Assignment
		// You need to write the code for this method
		/**
		public MyBoard aStar2(MyBoard mb)  {
			stepCounter = -1;
			MyBoard board = null;
			explored = new HashSet<String>();
			priorityQueue open = new priorityQueue();
			open.add(mb);
			soluLabel.setText("Searching ...");
			boolean displaySearch = cbDisplay.getState();
			board = open.first();
			while ((!stopAlgorithm) && (board!=null) && !isGoal(board)){
				if (displaySearch) {
					copyBoard(this, board);
					paintSlow(this.getGraphics());
				}
				open.remove(board);
				
				stepCounter++;
				stepCounterLabel.setText("<html>Nodes expanded: <br>" + Integer.toString(stepCounter) + "</html>");
				expandAllA(board, explored, open, board.depth+1, "heuristic");
				
				addToExplored(board);
				board = open.first();
			}
			return finalise(board,displaySearch);
		}*/
		//Heuristic calculation for aStar2 which uses the manhattan heuristic
		//to calculate the h value for the node. Does this by getting the x 
		//and y current location of each tile, then the tx and ty target coordinates
		//then calculates tx minus x and ty minus y, if they are both 0, then the tile
		//is in its target location, so do nothing. If they are not 0, then add
		//the value of x-tx and y-ty, add them together, giving the amount of 
		//moves until it is in its target location.
		public int manhattanHeuristic(MyBoard mb, int bx, int by){
			int h = 0;
			int minimumCost = 1;
			for (int x=0;x<BOARD_SIZE;x++) {
				for (int y=0;y<BOARD_SIZE;y++) {
					int check = mb.grid[x][y];
					int tx = xcoord[check];
					int ty = ycoord[check];
					if(check == 0){
						
					}
					else if(check != 0){
						h = h + minimumCost*(Math.abs(x-tx) + Math.abs(y-ty));
					}
				
				}
			}
			return h;
		}
		//Heuristic for aStar, which uses the tiles out of place heuristic
		//done by going through every tile on the board and checking if
		//it is out of place, if its out of place, add one to the h value
		//as the tile is out of place.
		public int heuristic(MyBoard mb, int bx, int by){
			int h = 0;
			for(int x=0;x<BOARD_SIZE;x++){
				for(int y=0;y<BOARD_SIZE;y++){
					int check = mb.grid[x][y];
					int tx = xcoord[check];
					int ty = ycoord[check];
					int rx = Math.abs(tx - x);
					int ry = Math.abs(ty - y);
					if (check == 0){
						
					}
					else if(check != 0){
					if(ry == 0 && rx == 0){
						continue;
					}
					else{
						h++;
					}
					}
				}
			}
			return h;
		}
		//Data structure for the priority queue used for both of the 
		//A* algorithms
		/**
		private class priorityQueue {
			private ArrayList<MyBoard> list = new ArrayList<MyBoard>();
			public void getQty(){

				//System.out.println(list.size());
			}
			public MyBoard first() {
				MyBoard mb = (MyBoard) list.get(0);
				System.out.println(this.contains(mb));
				return mb;
			}
			public void add(MyBoard o) {
				list.add(o);
				Collections.sort(list);
			}
			public void remove(MyBoard o) {
				list.remove(o);
			}
			public boolean contains(MyBoard o) {
				return list.contains(o);
			}
		}*/
		// CS26110 Assignment
		// You need to write the code for this method
		public MyBoard aStarTiles(MyBoard mb)  {
			stepCounter = -1;
			MyBoard board = null;
			explored = new HashSet<String>();
			//priorityQueue open = new priorityQueue();
			PriorityQueue open = new PriorityQueue();
			open.add(mb);
			soluLabel.setText("Searching ...");
			boolean displaySearch = cbDisplay.getState();
			board = (MyBoard) open.poll();
			while ((!stopAlgorithm) && (board!=null) && !isGoal(board)){
				if (displaySearch) {
					copyBoard(this, board);
					paintSlow(this.getGraphics());
				}
				open.remove(board);
				stepCounter++;
				stepCounterLabel.setText("<html>Nodes expanded: <br>" + Integer.toString(stepCounter) + "</html>");
				expandAllA(board, explored, open, board.depth+1, "heuristic");
				
				addToExplored(board);
				board = (MyBoard) open.poll();
				System.out.println(open.size());
				System.out.println(open.contains(board));
			}
			return finalise(board,displaySearch);
		}
		

		// CS26110 Assignment - use the structure of this algorithm as a basis for your A* implementation
		// Breadth first search (BFS) uses a queue to store unexpanded nodes
		// (queue: first-in-first-out)
		//-------------------------------------------------------------------------
		public MyBoard bfs(MyBoard mb)  {
			MyBoard board = null;//new MyBoard();
			LinkedList<MyBoard> frontier = new LinkedList<MyBoard>();
			frontier.add(mb);
			explored = new HashSet<String>();
			stepCounter = -1;
			soluLabel.setText("Searching ...");
			boolean displaySearch = cbDisplay.getState();
			board = frontier.poll();

			while ((!stopAlgorithm) && (!isGoal(board)) && (board!=null)) {	
				if (!alreadyVisited(board)) {
					// Display the step counter
					stepCounter++;
					stepCounterLabel.setText("<html>Nodes expanded: <br>" + Integer.toString(stepCounter) + "</html>");

					// Display the inner node
					if (displaySearch) {
						copyBoard(this, board);
						paintSlow(this.getGraphics());
					}

					// Add it to the explored list.
					addToExplored(board);
					// Attach the expanded succeeding nodes onto the tail of the queue.
					expandAll(board, frontier, board.depth+1);
				}

				board = frontier.poll();
			}

			return finalise(board,displaySearch);

		}

		//A HashSet is used for the explored list as we only need to check if a state has been seen before
		//and we don't need to retain any more information about it
		HashSet<String> explored = null;

		//check if the state has been visited already
		public boolean alreadyVisited(MyBoard board) {
			return explored.contains(board.hash());
		}

		//Add to the explored list. If this state has not been encountered before, add it to the list
		public void addToExplored(MyBoard board) {
			String hash = board.hash();
			if (!explored.contains(hash)) explored.add(hash);
		}

		// Used elsewhere - ignore this
		// Expand all the possible succeeding configurations.
		// The current board has 2, 3, or 4 succeeding boards, return the list.
		//------------------------------------------------------------------------
		public MyBoard expandAll(MyBoard mb) {
			int p = -1;
			int q = -1;
			MyBoard nextBoardHead = new MyBoard();
			MyBoard tempBoard = nextBoardHead;

			// locate the "0" tile.
			for (int i = 0; i < BOARD_SIZE; i++)
				for (int j = 0; j < BOARD_SIZE; j++)
					if (mb.grid[i][j] == 0) {
						p = i;
						q = j;
					}

			if (legal(p, q - 1)) {
				tempBoard.next = new MyBoard();
				tempBoard = tempBoard.next;

				copyBoard(tempBoard, mb);

				tempBoard.grid[p][q - 1] = mb.grid[p][q];
				tempBoard.grid[p][q] = mb.grid[p][q - 1];
				tempBoard.parent = mb;
			}

			if (legal(p, q + 1)) {
				tempBoard.next = new MyBoard();
				tempBoard = tempBoard.next;

				copyBoard(tempBoard, mb);

				tempBoard.grid[p][q + 1] = mb.grid[p][q];
				tempBoard.grid[p][q] = mb.grid[p][q + 1];
				tempBoard.parent = mb;
			}

			if (legal(p - 1, q)) {
				tempBoard.next = new MyBoard();
				tempBoard = tempBoard.next;

				copyBoard(tempBoard, mb);

				tempBoard.grid[p - 1][q] = mb.grid[p][q];
				tempBoard.grid[p][q] = mb.grid[p - 1][q];
				tempBoard.parent = mb;
			}

			if (legal(p + 1, q)) {
				tempBoard.next = new MyBoard();
				tempBoard = tempBoard.next;

				copyBoard(tempBoard, mb);

				tempBoard.grid[p + 1][q] = mb.grid[p][q];
				tempBoard.grid[p][q] = mb.grid[p + 1][q];
				tempBoard.parent = mb;
			}

			return nextBoardHead.next;
		}

		//Update the GUI, output statistics
		public MyBoard finalise(MyBoard finalNode, boolean displaySearch) {
			// Paint the solution node.
			if (!stopAlgorithm) {
				stepCounter++;
				stepCounterLabel.setText("<html>Nodes expanded: <br>" +
						Integer.toString(stepCounter) + "</html>");

				if (displaySearch) {
					copyBoard(this, finalNode);
					paintSlow(this.getGraphics());
				}

				soluLabel.setText("<html>Solution Found!<br>" + "<html>");
				status = IDLE;

				//calculate the solution length
				int solutionLength=-1;

				MyBoard boardList = null;
				MyBoard temp;
				MyBoard tempNew;
				temp = finalNode;

				//work back from the final node reached to see the solution path (and calculate its length)
				while (temp != null) {
					solutionLength++;
					tempNew = new MyBoard();
					copyBoard(tempNew, temp);
					tempNew.next = boardList;
					boardList = tempNew;
					temp = temp.parent;
				}

				//Print out some stats
				System.out.println(" ---------------- ");
				System.out.println("Nodes expanded: "+stepCounter);
				System.out.println("Solution length: "+solutionLength);
				return finalNode;
			} else {
				return null;
			}
		}

		// CS26110 Assignment
		public int compareTo(MyBoard mb) {
			return f-mb.f;
		}
	}


}//end of the class Tile
