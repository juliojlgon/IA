
package ai;

import kalaha.GameState;


public class MiniMax {
    
    protected double utilMax;
    protected double utilMin;
    protected int currDepthLimit;
    private boolean maxDepthReached;
    private long maxTime;
    private GameState Tablero;
    private int expandedNodes;
    private int maxDepth;
    private int player;
    
    public void expandir(GameState tablero, int depth, int time, int player){
        //Función recursiva que irá llamando para expandir el arbol.
        this.Tablero=tablero.clone();
        
        player = tablero.getNextPlayer();
        if (player == 2){  //Estamos en el jugador 1
            
        }else{ //Jugador 2
            
        }
        
        
    }
    
    public int utilidad(GameState tablero){
        int valor = -100;
        
        
        
        return valor;
    }
    
    public boolean esFinal(GameState tablero, int player){
        boolean result = false;
            if (tablero.getNoValidMoves(player) == 0){
                result = true;
            }
        return result;
    }
    
    public int maxPLayer(int node1, int node2){
        int maximo=0;
        
        return maximo;
    }
    
    public int minPlayer(int node1, int node2){
        int minimo = 0;
        
        return minimo;
    }
}
