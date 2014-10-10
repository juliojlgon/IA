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
    int contador = 0;
    private int depthLimit;
    private int Movimiento;

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
        /*depthMax = 0;
         long tiempoInicial = System.currentTimeMillis();
         int myMove = 0;
         int[] datos = new int[2];

         do {
         // System.out.println("Entre");
         depthMax++;
         datos = miniMax(currentBoard, 0, player, true);
         myMove = datos[1];
         } while (Math.pow((System.currentTimeMillis() - tiempoInicial), 1.3) < 5000 && !currentBoard.gameEnded());

         int puntuacion = datos[0];

         addText("=====INFO=====");
         addText("Jugador ===> " + player + " ||| movimiento ===> " + myMove);
         addText("Puntuacion ===> " + puntuacion);
         addText("Se ha ejecutado el bucle ==> " + contador);
         addText("==============");
         contador++;*/
        int myMove = makeDecision(currentBoard);
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

    public int[] miniMax(GameState gs, int profundidad, int jugador) {

        int[] resultado = new int[2];
        int[] recursivo = new int[2];
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        if (maxDepthReached(profundidad, depthMax) == false) { //LLegamos al maximo
            //if (jugador == player) {
            //resultado[0] = -Integer.MAX_VALUE;
            max = Double.NEGATIVE_INFINITY;
            min = Double.POSITIVE_INFINITY;
            for (int casa = 1; casa <= 6; casa++) { //Indice que nos marca el movimiento de los ambos
                if (gs.moveIsPossible(casa)) { //Si el movimiento se peude realizar (Ambo!=0)
                    GameState game = gs.clone(); //Clonamos el estado del juego
                    game.makeMove(casa);//Hacemos el movimiento
                    recursivo = miniMax(game, profundidad + 1,
                            game.getNextPlayer());
                    /*
                     LLamamos al recursivo para ir al siguiente nivel,
                     en caso de que ese nivel sobrepase a la maxDepth
                     daremos el valor de ese estado.
                     */

                    if (recursivo[0] > max) { //Si el resultado obtenido es mayor que el que teniamos guardado 
                        // addText("Comprobamos -> " + recursivo[0] + ">" + max + " ==> " + (primero) + " move => " + casa + " Player => " + jugador);
                        max = recursivo[0]; // Actualizamos el valor al MAX
                        //resultado[0] = (int) max;
                        if (profundidad == 0) {
                            //addText("ENTRO EN EL MAX-> " + jugador + " PUNTUACION = " + max + " TURN => " + whosTurn(gs));
                            resultado[1] = casa; //En caso de que la profundidad sea 0  actualizamos el valor de resultado[1]
                        }
                    }
                    if (min > recursivo[0]) {
                        min = recursivo[0]; // MIN
                        //resultado[0] = (int) min;
                    }

                    if (whosTurn(gs) == true) {
                        resultado[0] = (int) max;
                    } else {
                        resultado[0] = (int) min;
                    }
                }
                // }
            }

            /*if (jugador == swapPlayer(player)) {
             resultado[0] = Integer.MAX_VALUE;
             for (int i = 1; i <= 6; i++) {
             GameState game = gs.clone();
             if (game.moveIsPossible(i)) {
             game.makeMove(i);
             recursivo = miniMax(game, profundidad + 1,
             game.getNextPlayer());
             if (resultado[0] > recursivo[0]) {
             resultado[0] = recursivo[0]; // MIN
             }
             }
             }
             }*/
        } else {
            //resultado[0] = evaluacion(gs, jugador);
            resultado[0] = gs.getScore(jugador);
        }
        return resultado;
    }

    public int makeDecision(GameState gs) {
        depthMax = 0;
        depthLimit = 0;
        int movimiento = 0;
        long tiempoInicial = System.currentTimeMillis();
        int value = 0;// Puntuacion
        int puntos = 0; //DEBUG
        long timeElapsed = 0;
        boolean exit = false;

        do {
            depthMax++;
            int newResultValue = 0;
            for (int ambo = 1; ambo <= 6; ambo++) {

                //datos = miniMax(gs, 0, player, true);
                if (gs.moveIsPossible(ambo)) {
                    GameState game = gs.clone();
                    game.makeMove(ambo);
                    value = minValue(game, 1, game.getNextPlayer(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    //Funcion que devuelve el valor minimo.
                    if (value > newResultValue) {
                        newResultValue = value;
                        movimiento = ambo;
                        puntos = newResultValue;
                    }
                }
            }
            depthLimit++;
            timeElapsed = System.currentTimeMillis() - tiempoInicial;
            //addText("TIEMPO ==> "+ timeElapsed + "PROFUNDIDAD ==> " +  depthLimit);
            if (timeElapsed > 3200)
                exit = true;

        } while (timeElapsed < 5000 && !exit && !gs.gameEnded());
        addText("=====INFO=====");
        addText("Jugador ===> " + player + " ||| movimiento ===> " + movimiento);
        addText("Puntuacion ==> " + puntos);
       // addText("PROFUNDIDAD ==> " +  depthLimit);
        addText("=============");
        return movimiento;

    }

    public int minValue(GameState game, int depth, int jugador, double alpha, double beta) {
        /*depthMax = Math.max(depth, depthMax);
         if ( maxDepthReached(depthLimit, depthMax)) {
         return game.getScore(jugador);
         } else {
         double value = Double.POSITIVE_INFINITY;
         for (int ambo = 1; ambo <= 6; ambo++) {
         if (game.moveIsPossible(ambo)) {
         GameState gs = game.clone();
         gs.makeMove(ambo);
         value = Math.min(value, maxValue(gs, depth + 1, gs.getNextPlayer(), alpha, beta));
                    
         if (value <= alpha) {
         return (int) value;
         }
         beta = Math.min(beta, value);
         }
         }*/
        double valor;
        if (maxDepthReached(depth, depthMax) == true || game.gameEnded()) { //LLegamos al maximo
            //return evaluacion(game, jugador, false);
            //return game.getScore(jugador);
            return game.getScore(swapPlayer(jugador));

        } else {
            double value = Double.POSITIVE_INFINITY;
            for (int casa = 1; casa <= 6; casa++) { //Indice que nos marca el movimiento de los ambos
                if (game.moveIsPossible(casa)) { //Si el movimiento se peude realizar (Ambo!=0)
                    GameState gs = game.clone(); //Clonamos el estado del juego
                    gs.makeMove(casa);//Hacemos el movimiento                    
                    valor = maxValue(gs, depth + 1,
                            gs.getNextPlayer(), alpha, beta);
                    if (valor < value) {
                        // System.out.println(valor + "<" + value);
                        //System.out.println("");
                        value = valor;
                    }
                    if (value <= alpha) {
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
        /* depthMax = Math.max(depth, depthMax);
         if (maxDepthReached(depthLimit, depthMax)) {
         return game.getScore(jugador);
         } else {
         double value = Double.NEGATIVE_INFINITY;
         for (int ambo = 1; ambo <= 6; ambo++) {
         if (game.moveIsPossible(ambo)) {
         GameState gs = game.clone();
         gs.makeMove(ambo);
         value = Math.max(value, minValue(gs, depth + 1, gs.getNextPlayer(), alpha, beta));
         if (value >= beta) {
         return (int) value;
         }
         alpha = Math.min(alpha, value);
         }
         }*/
        double valor;
        if (maxDepthReached(depth, depthMax) == true || game.gameEnded()) { //LLegamos al maximo
            //return evaluacion(game, jugador, true);
            //int score = game.getScore(swapPlayer(jugador));
            return game.getScore(swapPlayer(jugador));
            //return game.getScore(jugador);
        } else {
            double value = Double.NEGATIVE_INFINITY;
            for (int casa = 1; casa <= 6; casa++) { //Indice que nos marca el movimiento de los ambos
                if (game.moveIsPossible(casa)) { //Si el movimiento se peude realizar (Ambo!=0)
                    GameState gs = game.clone(); //Clonamos el estado del juego
                    gs.makeMove(casa);//Hacemos el movimiento
                    valor = minValue(gs, depth + 1,
                            gs.getNextPlayer(), alpha, beta);
                    if (valor > value) {
                        value = valor;
                    }
                    if (value >= beta) {
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
     * Saber quien es el jugador actual
     *
     * @param gs el tablero con el jugador actual
     * @return true si el juegador es max
     */
    public boolean whosTurn(GameState gs) {
        return player == gs.getNextPlayer();
    }

    public int evaluacion(GameState gs, int play, boolean max) {
        int jugador1 = gs.getScore(play);
        int jugador2 = gs.getScore(swapPlayer(play));
        int score = 0;
        if (max) {
            if (jugador1 > jugador2) {
                score = 1000;
            } else {
                score = -1000;
            }
        } else {
            if (jugador1 > jugador2) {
                score = -1000;
            } else {
                score = 1000;
            }
        }
        return score;
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
