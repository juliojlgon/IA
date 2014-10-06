
package ai;

import java.util.ArrayList;
import java.util.HashMap;
import kalaha.GameState;


public class MiniMax {
    
    /*
    protected double utilMax;
    protected double utilMin;
    protected int currDepthLimit;
    private boolean maxDepthReached;
    private long maxTime;
    private GameState Tablero;
    private int expandedNodes;
    private int maxDepth;
    private int player;
    
    final static int TIEMPO = 5000; //ms
    
    public void expandir(GameState tablero, int depth, int time, int player){
        //Función recursiva que irá llamando para expandir el arbol.
        this.Tablero=tablero.clone();
        int profundidad = 0;
        
        player = tablero.getNextPlayer();
        if (player == 2){  //Estamos en el jugador 1
            if (!esFinal(tablero, player)){
                
                
            }
            
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
    */



	
	private static final int maxWin = 1000;
	private static final int maxLose = -1000;
	private static final int draw = 0;
	private int player;
	private int depth;
	private int originalPlayer;
	private int realDepth;
	private GameState kalaha;
		
	public int decideMovement(GameState kalaha,int depth,int realDepth,int player,int originalPlayer){
		this.player = new Integer (player);
		this.depth = new Integer(depth);
		this.realDepth = new Integer(realDepth);
		this.originalPlayer = originalPlayer;
		this.kalaha = kalaha.clone();
		return expand();
	}

	private int emptySpaceMovement(){
		if(!emptySearch().isEmpty() )
			return reachEmptyNodes(emptySearch());
		else
			return -1;
	}
	
	private ArrayList<Integer> emptySearch() {
		ArrayList<Integer> position = new ArrayList<Integer>();
		for(int i = 1;i<7;i++){
			if(kalaha.getSeeds(i, player)==0){
				if(kalaha.getSeeds(i, kalaha.getNextPlayer())>0)
					position.add(i);
			}
		}
		if(position.size()>1)
			return orderPositions(position);
		else
			return position;
	}

	private int reachEmptyNodes(ArrayList<Integer> emptySearch){
		for(int i =0;i<emptySearch.size();i++){
			for(int j=1;j<7;j++){
				if(emptySearch.get(i)>j){
					if(j+kalaha.getSeeds(emptySearch.get(i), player)==emptySearch.get(i))
						return j;
				}else
					break;
			}
		}
		return -1;
	}

	private ArrayList<Integer> orderPositions(ArrayList<Integer> position) {
		ArrayList<Integer> fList = new ArrayList<Integer>();
		fList.add(position.get(0));
		for(int i = 1; i<position.size();i++){
			for(int j = 0;j<fList.size();j++){
				if(kalaha.getSeeds(position.get(i),kalaha.getNextPlayer())>=kalaha.getSeeds(fList.get(j), kalaha.getNextPlayer())){
					fList.add(j, position.get(i));
					break;
				}
			}
		}
		return fList;
		
	}
	
	private int expand(){
		if(originalPlayer==1){
			if(depth>0 && !kalaha.gameEnded()){
				int value = 0;
				int depth = new Integer(this.depth-1);
				if(player==1){
					if(emptySpaceMovement()!=-1){
						GameState newKalaha = kalaha.clone();
						newKalaha.makeMove(emptySpaceMovement());
						return new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
					}else{
						HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
						for(int i = 1;i<7;i++){
							if(kalaha.getSeeds(i, player)>0){
								if(kalaha.getSeeds(i, player)+i==7){
									GameState newKalaha = kalaha.clone();
									
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
									values.put(i, value);
								}else{
									GameState newKalaha = kalaha.clone();
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
									values.put(i, value);
								}
							}
						}
					if(depth!=realDepth-1){
						return selectMaxValue(values);
					}else{
						return searchValue(values, selectMaxValue(values));
					}
					}
				}else if (player==2){
					if(emptySpaceMovement()!=-1){
						GameState newKalaha = kalaha.clone();
						newKalaha.makeMove(emptySpaceMovement());
						return new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
					}
					else{
						HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
						for(int i = 1;i<=6;i++){
							if(kalaha.getSeeds(i, player)>0){
								if(kalaha.getSeeds(i, player)+i==7){
									GameState newKalaha = kalaha.clone();
									
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
									values.put(i, value);
								}else{
									GameState newKalaha = kalaha.clone();
									
									newKalaha.makeMove(i);
									value =new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
									values.put(i, value);
								}
							}
						}
						return selectMinValue(values);
						}
				}else{
					return -1002;
				}
			}else{
				if(kalaha.getWinner()==0){
					return draw;
				}else if(kalaha.getWinner()==1){
					return maxWin;
				}else if(kalaha.getWinner()==2){
					return maxLose;
				}else{
					return kalaha.getSeeds(0, 1)-kalaha.getSeeds(0, 2);
				}
			}
		}else{
			if(depth>0 && !kalaha.gameEnded()){
				int value = 0;
				int depth = new Integer(this.depth-1);
				HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
				if(player==1){
					if(emptySpaceMovement()!=-1){
						GameState newKalaha = kalaha.clone();
						
						newKalaha.makeMove(emptySpaceMovement());
						return new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
					}else{
						for(int i = 1;i<7;i++){
							if(kalaha.getSeeds(i, player)>0){
								if(kalaha.getSeeds(i, player)+i==7){
									GameState newKalaha = kalaha.clone();
	
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
									values.put(i, value);
								}else{
									GameState newKalaha = kalaha.clone();
									
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
									values.put(i, value);
								}
							}
							}
						}
						return selectMinValue(values);
					}else if (player==2){
					if(emptySpaceMovement()!=-1){
						GameState newKalaha = kalaha.clone();
						
						newKalaha.makeMove(emptySpaceMovement());
						return new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
					}
					else{
						for(int i = 1;i<=6;i++){
							if(kalaha.getSeeds(i, player)>0){
								if(kalaha.getSeeds(i, player)+i==7){
									GameState newKalaha = kalaha.clone();
									
									newKalaha.makeMove(i);
									value = new MiniMax().decideMovement(newKalaha,depth,realDepth,2,originalPlayer);
									values.put(i, value);
								}else{
									GameState newKalaha = kalaha.clone();
	
									newKalaha.makeMove(i);
									value =new MiniMax().decideMovement(newKalaha,depth,realDepth,1,originalPlayer);
									values.put(i, value);
								}
							}
						}
					}
						if(depth!=realDepth-1){
							return selectMaxValue(values);
						}else{
							return searchValue(values, selectMaxValue(values));
						}
						}
			}else{
				if(kalaha.getWinner()==0){
					return draw;
				}else if(kalaha.getWinner()==1){
					return maxLose;
				}else if(kalaha.getWinner()==2){
					return maxWin;
				}else{
					return kalaha.getSeeds(0, 2)-kalaha.getSeeds(1, 0);
				}
			}
		}
		return -1010;
	}
	
	private int selectMaxValue(HashMap<Integer,Integer> list){
		ArrayList<Integer> nList = new ArrayList<Integer>(list.values());
		int maxValue = nList.get(0);
		for(int i=1;i<nList.size();i++){
			if(maxValue<nList.get(i))
				maxValue=nList.get(i);
		} 
		return maxValue;
	}
	
	private int selectMinValue(HashMap<Integer,Integer> list){
		ArrayList<Integer> nList = new ArrayList<Integer>(list.values());
		int minValue = nList.get(0);
		for(int i=1;i<nList.size();i++){
			if(minValue>nList.get(i))
				minValue=nList.get(i);
		}
		return minValue;
	}
	
	private int searchValue(HashMap<Integer,Integer>list, int value){
		for(int i = 1 ; i<7; i++){
			if(list.get(i)!=null){
				if(list.get(i)==value){
					return i;
				}
			}
		}
		return -1;
	}
}

