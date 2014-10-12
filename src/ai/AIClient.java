package ai;

import java.io.*;
import java.net.*;

import javax.swing.*;

import java.awt.*;

import kalaha.*;

/**
 * This is the main class for your Kalaha AI bot. Currently it only makes a
 * random, valid move each turn.
 *
 * @author Johan Hagelbäck
 */
public class AIClient implements Runnable {

    private int player;
    private JTextArea text;

    private PrintWriter out;
    private BufferedReader in;
    private Thread thr;
    private Socket socket;
    private boolean running;
    private boolean connected;
    private int depthMax = 0;
    //private int depthLimit;
    

    /**
     * Creates a new client.
     */
    public AIClient() {
        player = -1;
        connected = false;

        // This is some necessary client stuff. You don't need
        // to change anything here.
        initGUI();

        try {
            addText("Connecting to localhost:" + KalahaMain.port);
            socket = new Socket("localhost", KalahaMain.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            addText("Done");
            connected = true;
        } catch (Exception ex) {
            addText("Unable to connect to server");
            return;
        }
    }

    /**
     * Starts the client thread.
     */
    public void start() {
        // Don't change this
        if (connected) {
            thr = new Thread(this);
            thr.start();
        }
    }

    /**
     * Creates the GUI.
     */
    private void initGUI() {
        // Client GUI stuff. You don't need to change this.
        JFrame frame = new JFrame("My AI Client");
        frame.setLocation(Global.getClientXpos(), 445);
        frame.setSize(new Dimension(420, 250));
        frame.getContentPane().setLayout(new FlowLayout());

        text = new JTextArea();
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(400, 210));

        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    /**
     * Adds a text string to the GUI textarea.
     *
     * @param txt The text to add
     */
    public void addText(String txt) {
        // Don't change this
        text.append(txt + "\n");
        text.setCaretPosition(text.getDocument().getLength());
    }

    /**
     * Thread for server communication. Checks when it is this client's turn to
     * make a move.
     */
    public void run() {
        String reply;
        running = true;

        try {
            while (running) {
                // Checks which player you are. No need to change this.
                if (player == -1) {
                    out.println(Commands.HELLO);
                    reply = in.readLine();

                    String tokens[] = reply.split(" ");
                    player = Integer.parseInt(tokens[1]);

                    addText("I am player " + player);
                }

                // Check if game has ended. No need to change this.
                out.println(Commands.WINNER);
                reply = in.readLine();
                if (reply.equals("1") || reply.equals("2")) {
                    int w = Integer.parseInt(reply);
                    if (w == player) {
                        addText("I won!");
                    } else {
                        addText("I lost...");
                    }
                    running = false;
                }
                if (reply.equals("0")) {
                    addText("Even game!");
                    running = false;
                }

                // Check if it is my turn. If so, do a move
                out.println(Commands.NEXT_PLAYER);
                reply = in.readLine();
                if (!reply.equals(Errors.GAME_NOT_FULL) && running) {
                    int nextPlayer = Integer.parseInt(reply);

                    if (nextPlayer == player) {
                        out.println(Commands.BOARD);
                        String currentBoardStr = in.readLine();
                        boolean validMove = false;
                        while (!validMove) {
                            long startT = System.currentTimeMillis();
                            // This is the call to the function for making a
                            // move.
                            // You only need to change the contents in the
                            // getMove()
                            // function.
                            GameState currentBoard = new GameState(
                                    currentBoardStr);
                            int cMove = getMove(currentBoard);

                            // Timer stuff
                            long tot = System.currentTimeMillis() - startT;
                            double e = (double) tot / (double) 1000;

                            out.println(Commands.MOVE + " " + cMove + " "
                                    + player);
                            reply = in.readLine();
                            if (!reply.startsWith("ERROR")) {
                                validMove = true;
                                addText("Made move " + cMove + " in " + e
                                        + " secs");
                            }
                        }
                    }
                }

                // Wait
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            running = false;
        }

        try {
            socket.close();
            addText("Disconnected from server");
        } catch (Exception ex) {
            addText("Error closing connection: " + ex.getMessage());
        }
    }

    /**
     * This is the method that makes a move each time it is your turn. Here you
     * need to change the call to the random method to your Minimax search.
     *
     * @param currentBoard The current board state
     * @return Move to make (1-6)
     */
    public int getMove(GameState currentBoard) {
        int myMove = makeDecision(currentBoard); //Chooses the best move
        return myMove;
    }

    /**
     * Returns a random ambo number (1-6) used when making a random move.
     *
     * @return Random ambo number
     */
    public int getRandom() {
        return 1 + (int) (Math.random() * 6);
    }

    /**
     * This the method that say to the AI what is the best move in each case.
     *
     * @param gs The current board state
     * @return Best move to make (1-6)
     */
    public int makeDecision(GameState gs) {
        depthMax = 0;  //depthMax starts with 0
       // depthLimit = 0; //For Debug
        int movimiento = 0; //Declare and initialize movimiento
        long startTime = System.currentTimeMillis();//Declare and initialize startTime
        int value = 0;// Puntuacion
        int points = 0; //DEBUG
        long timeElapsed = 0;//Declare and initialize timeElapsed
        boolean exit = false;//Declare and initialize exit

        do { //Do while the time is less than 5 seconds or the game is over
            depthMax++;//Increment in one the depthMax
            int newResultValue = 0;
            for (int ambo = 1; ambo <= 6; ambo++) { //Tries the six ambos
                if (gs.moveIsPossible(ambo)) { //If it's a possible move
                    GameState game = gs.clone(); //We clone the actual GameState
                    game.makeMove(ambo); //We make the move
                    value = minValue(game, 1, game.getNextPlayer(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);//As we are in a max we call min function
                    //We call the function that will return to us the minimun value.
                    if (value > newResultValue) { //Compare the two results
                        newResultValue = value;   //
                        movimiento = ambo;        //Save the new values
                        points = newResultValue;  //
                    }
                }
            }
            //depthLimit++; //DEBUG
            timeElapsed = System.currentTimeMillis() - startTime;
            //addText("TIEMPO ==> "+ timeElapsed + "PROFUNDIDAD ==> " +  depthLimit);
            if (timeElapsed > 3200) {
                exit = true;
            }

        } while (timeElapsed < 5000 && !exit && !gs.gameEnded());
        addText("=====INFO=====");//Tracking
        addText("Player ===> " + player + " ||| Move ===> " + movimiento);//Tracking
        addText("Score ==> " + points);//Tracking
        addText("=============");//Tracking
        return movimiento;

    }

    /**
     * The function for the min, part of the IDS
     *
     * @param game Game state
     * @param depth Actual depth
     * @param jugador The player is going to move now.
     * @param alpha For the pruning
     * @param beta For the pruning
     * @return The min of the utility values.
     */
    public int minValue(GameState game, int depth, int jugador, double alpha, double beta) {
        double newValue;
        if (maxDepthReached(depth, depthMax) == true || game.gameEnded()) { //If we are in the maximun depth
            return game.getScore(swapPlayer(jugador));

        } else { //We are not in the maximun depth
            double value = Double.POSITIVE_INFINITY;
            for (int ambo = 1; ambo <= 6; ambo++) { //Index that mark the movement of the ambos
                if (game.moveIsPossible(ambo)) { //The movement is posible (ambo!=0)
                    GameState gs = game.clone(); //Clone the state of the game 
                    gs.makeMove(ambo);//Make the movement                    
                    newValue = maxValue(gs, depth + 1,
                            gs.getNextPlayer(), alpha, beta);
                    if (newValue < value) { //Compare the values
                        value = newValue;
                    }
                    if (value <= alpha) { //Pruning
                        return (int) value;
                    }
                    beta = Math.min(beta, value);

                }
                /*
                 LLamamos al recursivo para ir al siguiente nivel,
                 en caso de que ese nivel sobrepase a la maxDepth
                 daremos el valor de ese estado.
                 */

            }
            /*System.out.println("#######MIN#######");
             System.out.println("");
             System.out.println("Jugador ==> " + jugador);
             System.out.println(game.toString());
             System.out.println("");
             System.out.println("Puntos ==> " + value);
             System.out.println("#################");*/
            return (int) value;
        }

    }

    public int maxValue(GameState game, int depth, int jugador, double alpha, double beta) {
        double newValue;
        if (maxDepthReached(depth, depthMax) == true || game.gameEnded()) { //We are in the maximun 
            return game.getScore(swapPlayer(jugador));
            
        } else {
            double value = Double.NEGATIVE_INFINITY;
            for (int casa = 1; casa <= 6; casa++) { //Index that mark the movement of the ambos
                if (game.moveIsPossible(casa)) { //The movement is posible (ambo!=0)
                    GameState gs = game.clone(); //Clone the state of the game 
                    gs.makeMove(casa);//Make the movement
                    newValue = minValue(gs, depth + 1,
                            gs.getNextPlayer(), alpha, beta);
                    if (newValue > value) {//Compare the values
                        value = newValue;
                    }
                    if (value >= beta) { //Pruning
                        return (int) value;
                    }
                    alpha = Math.max(alpha, value);

                }
                /*
                 LLamamos al recursivo para ir al siguiente nivel,
                 en caso de que ese nivel sobrepase a la maxDepth
                 daremos el valor de ese estado.
                 */

            }
            /*System.out.println("#######MAX#######");
             System.out.println("");
             System.out.println("Jugador ==> " + jugador);
             System.out.println(game.toString());
             System.out.println("");
             System.out.println("Puntos ==> " + value);
             System.out.println("#################");*/
            return (int) value;
        }

    }

    /**
     * Nos dirá si se ha llegado a la profundidad maxima.
     *
     * @param depth profundidad actual
     * @param maxDepth profundidad maxima
     * @return devolverá true si se ha llegado a la maxima profundidad y false
     * en caso contrario.
     */
    public boolean maxDepthReached(int depth, int maxDepth) {
        return depth > maxDepth;
    }

    /**
     * Cambia el jugador actual
     *
     * @param player jugador actual
     * @return devuelve el jugador contrario al actual o un -1 en caso de error.
     */
    public int swapPlayer(int player) {
        if (player == 1) {
            player = 2;
        } else if (player == 2) {
            player = 1;
        } else {
            player = -1;
        }
        return player;

    }
}
