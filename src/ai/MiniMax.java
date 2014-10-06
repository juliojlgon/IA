package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kalaha.GameState;

public class MiniMax {

    protected double utilMax;
    protected double utilMin;
    protected int currDepthLimit;
    private GameState Tablero;
    private int expandedNodes;
    //private int maxDepth;
    private int player;
    long tiempo = System.currentTimeMillis();

    final static int TIMELIMIT = 5000; //ms

    public void expandir(GameState tablero, int depth, int player) {
        //Función recursiva que irá llamando para expandir el arbol.
        this.Tablero = tablero.clone();
        int profundidad = 0;

        long timeRunning = (System.currentTimeMillis() - tiempo);

        player = tablero.getNextPlayer();
        if (player == 1) {  //Estamos en el jugador 1
            if (!Tablero.gameEnded()) { //Si el juego aun no ha terminado.
                for (int j = 0; j <= depth; j++) { //Numero de veces que ejecutamos el bucle.
                    for (int i = GameState.START_S; i <= GameState.END_S; i++) {
                        if (Tablero.moveIsPossible(i)) { //Comprobamos si se puede hacer un movimiento en ese Ambo
                            Tablero.makeMove(i); //Hacemos el movimiento con las semillas de ese arbol
                        }
                        if (!esFinal(Tablero, player) && (j == depth)) {
                            expandir(Tablero, depth + 1, Tablero.getNextPlayer());
                        } else {
                            //Devolver el valor de utilidad del nodo.
                        }
                    }
                }
            }

        } else { //Jugador 2
            if (!Tablero.gameEnded()) { //Si el juego aun no ha terminado.
                for (int j = 0; j <= depth || timeRunning < TIMELIMIT; j++) { //Numero de veces que ejecutamos el bucle.
                    for (int i = GameState.START_N; i <= GameState.END_N; i++) {
                        if (Tablero.moveIsPossible(i)) { //Comprobamos si se puede hacer un movimiento en ese Ambo
                            Tablero.makeMove(i); //Hacemos el movimiento con las semillas de ese arbol
                        }
                        if (!esFinal(Tablero, player) && (profundidad == depth)) {
                            expandir(Tablero, depth + 1, Tablero.getNextPlayer());
                        } else {
                            //Devolver el valor de utilidad del nodo.
                        }
                    }
                }
            }
        }
    }

    public int utilidad(GameState tablero) {
        int valor = -100;

        return valor;
    }

    public boolean esFinal(GameState tablero, int player) {
        boolean result = false;
        if (tablero.getNoValidMoves(player) == 0) {
            result = true;
        }
        return result;
    }

    public int maxPLayer(int node1, int node2) {
        int maximo = 0;

        return maximo;
    }

    public int minPlayer(int node1, int node2) {
        int minimo = 0;

        return minimo;
    }

    public int makeDecision(GameState state) {
        List<int> results;
        results = null;
        double resultValue = Double.NEGATIVE_INFINITY;
        int player = state.getNextPlayer();
        expandedNodes = 0;
        //maxDepth = 0;
        currDepthLimit = 0;
        long startTime = System.currentTimeMillis();
        boolean exit = false;
        do {
            currDepthLimit++;
            //maxDepthReached = false;
            List<ACTION> newResults = new ArrayList<ACTION>();
            double newResultValue = Double.NEGATIVE_INFINITY;
            double secondBestValue = Double.NEGATIVE_INFINITY;

            for (ACTION action : orderActions(state, game.getActions(state),
                    player, 0)) {
                if (results != null
                        && System.currentTimeMillis() > startTime + maxTime) {
                    exit = true;
                    break;
                }
                double value = minValue(game.getResult(state, action), player,
                        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);

                if (value >= newResultValue) {
                    if (value > newResultValue) {
                        secondBestValue = newResultValue;
                        newResultValue = value;
                        newResults.clear();
                    }
                    newResults.add(action);
                } else if (value > secondBestValue) {
                    secondBestValue = value;
                }
            }
            if (!exit) {
                results = newResults;
                resultValue = newResultValue;
            }
            if (!exit && results.size() == 1) {
                break;
            }
        } while (!exit);
        return results.get(0);
    }

    public double maxValue(STATE state, PLAYER player, double alpha,
            double beta, int depth) { // returns an utility value
        expandedNodes++;
        if (game.isTerminal(state) || depth >= currDepthLimit) {
            return eval(state, player);
        } else {
            double value = Double.NEGATIVE_INFINITY;
            for (ACTION action : orderActions(state, game.getActions(state),
                    player, depth)) {
                value = Math.max(value, minValue(game.getResult(state, action), //
                        player, alpha, beta, depth + 1));
                if (value >= beta) {
                    return value;
                }
                alpha = Math.max(alpha, value);
            }
            return value;
        }
    }

    public double minValue(STATE state, PLAYER player, double alpha,
            double beta, int depth) { // returns an utility
        expandedNodes++;
        if (game.isTerminal(state) || depth >= currDepthLimit) {
            return eval(state, player);
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (ACTION action : orderActions(state, game.getActions(state),
                    player, depth)) {
                value = Math.min(value, maxValue(game.getResult(state, action), //
                        player, alpha, beta, depth + 1));
                if (value <= alpha) {
                    return value;
                }
                beta = Math.min(beta, value);
            }
            return value;
        }
    }

}
