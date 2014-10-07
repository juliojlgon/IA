package ai;

import java.util.ArrayList;
import java.util.HashMap;
import kalaha.GameState;

public class MiniMax {

    private static final int maxWin = 1000;
    private static final int maxLose = -1000;
    private static final int draw = 0;
    private int player;
    private int depth;
    private int originalPlayer;
    private int realDepth;
    private GameState tablero;

    /**
     *
     * @param board(GameState) it will contain the GameState
     * @param depth(int) It will have the depth  
     * @param realDepth(int) The Real depth or the max level of the tree
     * @param player(int) The current Player
     * @param originalPlayer(int) The player that was playing in the first iteration
     * @return Int with the move we are going to do.
     */
    public int decideMovement(GameState board, int depth, int realDepth, int player, int originalPlayer) {
        this.player = player;
        this.depth = depth;
        this.realDepth = realDepth;
        this.originalPlayer = originalPlayer;
        this.tablero = board.clone();
        return expand();
    }

    private int emptySpaceMovement() {
        if (!emptySearch().isEmpty()) {
            return reachEmptyNodes(emptySearch());
        } else {
            return -1;
        }
    }

    /**
     * Busqueda Vacia. No se que hace.
     *
     * @return
     */
    private ArrayList<Integer> emptySearch() {
        ArrayList<Integer> position = new ArrayList<Integer>();
        for (int i = 1; i < 7; i++) {
            if (tablero.getSeeds(i, player) == 0) {
                if (tablero.getSeeds(i, tablero.getNextPlayer()) > 0) {
                    position.add(i);
                }
            }
        }
        if (position.size() > 1) {
            return orderPositions(position);
        } else {
            return position;
        }
    }

    /**
     * LLegar a nodos vacios
     *
     * @param emptySearch
     * @return
     */
    private int reachEmptyNodes(ArrayList<Integer> emptySearch) {
        for (int i = 0; i < emptySearch.size(); i++) {
            for (int j = 1; j < 7; j++) {
                if (emptySearch.get(i) > j) {
                    if (j + tablero.getSeeds(emptySearch.get(i), player) == emptySearch.get(i)) {
                        return j;
                    }
                } else {
                    break;
                }
            }
        }
        return -1;
    }

    /**
     * Trata de ordenar las posiciones de un array. Entiendo...
     *
     * @param position
     * @return
     */
    private ArrayList<Integer> orderPositions(ArrayList<Integer> position) {
        ArrayList<Integer> fList = new ArrayList<Integer>();
        fList.add(position.get(0));
        for (int i = 1; i < position.size(); i++) {
            for (int j = 0; j < fList.size(); j++) {
                if (tablero.getSeeds(position.get(i), tablero.getNextPlayer()) >= tablero.getSeeds(fList.get(j), tablero.getNextPlayer())) {
                    fList.add(j, position.get(i));
                    break;
                }
            }
        }
        return fList;

    }

    /**
     * Expandimos todas las posibles combinaciones Usando DFS
     *
     * @return
     */
    private int expand() {
        if (originalPlayer == 1) {
            if (depth > 0 && !tablero.gameEnded()) {
                int value = 0;
                int depth = new Integer(this.depth - 1);
                if (player == 1) {
                    if (emptySpaceMovement() != -1) {
                        GameState tableroCopia = tablero.clone();
                        tableroCopia.makeMove(emptySpaceMovement());
                        return new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                    } else {
                        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
                        for (int i = 1; i <= 6; i++) {
                            if (tablero.moveIsPossible(i)) {
                                if (tablero.getSeeds(i, player) + i == 7) {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax()
                                            .decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                                    values.put(i, value);
                                } else {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                                    values.put(i, value);
                                }
                            }
                        }
                        if (depth != realDepth - 1) {
                            return selectMaxValue(values);
                        } else {
                            return searchValue(values, selectMaxValue(values));
                        }
                    }
                } else if (player == 2) {
                    if (emptySpaceMovement() != -1) {
                        GameState tableroCopia = tablero.clone();
                        tableroCopia.makeMove(emptySpaceMovement());
                        return new MiniMax().decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                    } else {
                        HashMap<Integer, Integer> values = new HashMap<>();
                        for (int i = 1; i <= 6; i++) {
                            if (tablero.moveIsPossible(i)) {
                                if (tablero.getSeeds(i, player) + i == 7) {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                                    values.put(i, value);
                                } else {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                                    values.put(i, value);
                                }
                            }
                        }
                        return selectMinValue(values);
                    }
                } else {
                    return -1002;
                }

            } else {
                int scrOne = tablero.getScore(1);
                int scrTwo = tablero.getScore(2);
                if (scrOne == scrTwo) {
                    return draw;
                } else if (scrOne > scrTwo) {
                    return maxWin;
                } else if (scrOne < scrTwo) {
                    return maxLose;
                } else {
                    return tablero.getSeeds(0, 1) - tablero.getSeeds(0, 2);
                }
            }

        } else {

            if (depth > 0 && !tablero.gameEnded()) {
                int value = 0;
                int depth = new Integer(this.depth - 1);
                HashMap<Integer, Integer> values = new HashMap<>();
                if (player == 1) {
                    if (emptySpaceMovement() != -1) {
                        GameState tableroCopia = tablero.clone();
                        tableroCopia.makeMove(emptySpaceMovement());
                        return new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                    } else {
                        for (int i = 1; i < 7; i++) {
                            if (tablero.moveIsPossible(i)) {
                                if (tablero.getSeeds(i, player) + i == 7) {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                                    values.put(i, value);
                                } else {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                                    values.put(i, value);
                                }
                            }
                        }
                    }
                    return selectMinValue(values);

                } else if (player == 2) {
                    if (emptySpaceMovement() != -1) {
                        GameState tableroCopia = tablero.clone();

                        tableroCopia.makeMove(emptySpaceMovement());
                        return new MiniMax().decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                    } else {
                        for (int i = 1; i <= 6; i++) {
                            if (tablero.moveIsPossible(i)) {
                                if (tablero.getSeeds(i, player) + i == 7) {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 2, originalPlayer);
                                    values.put(i, value);
                                } else {
                                    GameState tableroCopia = tablero.clone();
                                    tableroCopia.makeMove(i);
                                    value = new MiniMax().decideMovement(tableroCopia, depth, realDepth, 1, originalPlayer);
                                    values.put(i, value);
                                }
                            }
                        }
                    }
                    if (depth != realDepth - 1) {
                        return selectMaxValue(values);
                    } else {
                        return searchValue(values, selectMaxValue(values));
                    }
                }
            } else {
                if (tablero.getWinner() == 0) {
                    return draw;
                } else if (tablero.getWinner() == 1) {
                    return maxLose;
                } else if (tablero.getWinner() == 2) {
                    return maxWin;
                } else {
                    return tablero.getSeeds(0, 2) - tablero.getSeeds(1, 0);
                }
            }
        }
        return -1010;
    }

    private int selectMaxValue(HashMap<Integer, Integer> list) {
        ArrayList<Integer> nList = new ArrayList<Integer>(list.values());
        int maxValue = nList.get(0);
        for (int i = 1; i < nList.size(); i++) {
            if (maxValue < nList.get(i)) {
                maxValue = nList.get(i);
            }
        }
        return maxValue;
    }

    private int selectMinValue(HashMap<Integer, Integer> list) {
        ArrayList<Integer> nList = new ArrayList<Integer>(list.values());
        int minValue = nList.get(0);
        for (int i = 1; i < nList.size(); i++) {
            if (minValue > nList.get(i)) {
                minValue = nList.get(i);
            }
        }
        return minValue;
    }

    private int searchValue(HashMap<Integer, Integer> list, int value) {
        for (int i = 1; i < 7; i++) {
            if (list.get(i) != null) {
                if (list.get(i) == value) {
                    return i;
                }
            }
        }
        return -1;
    }
}
