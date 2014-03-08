package probabilistic_reasoning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PRProblem {
	private Maze maze;
	private int startX, startY;
	
	public PRProblem(Maze m, int sx, int sy, char[] startInstr){
		this.maze = m;
		startX = sx;
		startY = sy;
		run(startInstr);
	}
	
	public void run(char[] startInstr){
		Robo r = new Robo(this.maze);
		
		char c = r.getSensorInput(maze.getOnColor(startX,startY));
		System.out.println(c);
		System.out.println(r.matToString(r.calculate(c)));

		for (char dir:startInstr){
			int[] d = maze.charToDirection(dir);
			System.out.println("next move "+Arrays.toString(d));
			
			int[] newLoc = maze.makeMove(startX, startY,d);
			System.out.println("new loc "+Arrays.toString(newLoc));
			startX = newLoc[0];
			startY = newLoc[1];
			r.move++;
			
			c = r.getSensorInput(maze.getOnColor(startX,startY));
			System.out.println(c);
			System.out.println(r.matToString(r.calculate(c)));
			
		}
	}
	
	class Robo{
		final Character[] tiles = {'r','g','b','y'};
		Double[][] lastP;
		ArrayList<Character> evidence;
		Maze maze;
		int move; 
		
		public Robo(Maze m){
			maze = m;
			evidence = new ArrayList<Character>();
		}
		
		public Double[][] generateProbability(){
			int width = maze.width;
			int height = maze.height;
			Double[][] retval = new Double[width][height];
			
			int ob = maze.numOfOb;
			double legalSize = (maze.height*maze.width)-ob;
				
			for (int i = 0; i < maze.width; i++) {
				for (int j = 0; j < maze.height; j++) {
					if(maze.getChar(i,j)=='#'){
						retval[i][j]=(double) 0;
					}else{
						retval[i][j]=1.0/legalSize;
					}
				}
			}
			return retval;
		}
		
		public Double[][] calculate(char c){
			if(this.move==0){
				lastP = generateProbability();
				return lastP;
			}else{
				Double[][] nP = predict(c);
				System.out.println("NP\n"+matToString(nP));
				this.lastP = nP;
				Double[][] withTransition = multiTransition(nP);
				System.out.println("trans\n"+matToString(withTransition));
				Double[][] normalized = normalize(withTransition);
				return normalized;
			}
		}
		
		public Double[][] normalize(Double[][] A){
			double sum = 0;
			for (int i = 0; i < A.length; i++) {
	            for (int j = 0; j < A[0].length; j++) {
	                sum += A[i][j];
	            }
	        }
			double a = 1/sum;
			Double[][] retval = new Double[maze.width][maze.height];
			for(int i = 0; i < retval.length; i++){
				for(int j = 0; j < retval[0].length; j++){
					retval[i][j]=a*A[i][j];
				}
			}
			return retval;
		}
		
		public Double[][] predict(char c){
			Double[][] retval = new Double[maze.width][maze.height];
			for(int i = 0; i < retval.length; i++){
				for(int j = 0; j < retval[0].length; j++){
					retval[i][j]=(maze.getOnColor(i, j)==c?0.88:0.04)*lastP[i][j];
				}
			}
			return retval;
		}
		
		public Double[][] multiTransition(Double[][] A){
			Double[][] retval = new Double[maze.width][maze.height];
			for(int i = 0; i < retval.length; i++){
				for(int j = 0; j < retval[0].length; j++){
					retval[i][j]=A[i][j]*transition(i,j);
				}
			}
			return retval;
		}
		
		public Double[][] multiElem(Double[][] A, Double[][] B){
			Double[][] C = new Double[A.length][A[0].length];
	        for (int i = 0; i < A.length; i++) {
	            for (int j = 0; j < A[0].length; j++) {
	                C[i][j] = A[i][j]*B[i][j];
	            }
	        }
	        return C;
		}
		
		public double transition(int x, int y){
			int ctr = 0;
			for(int[] move: maze.moves){
				int nx = x+move[0];
				int ny = y+move[1];
				if(maze.isLegal(nx, ny)){
					ctr++;
				}
			}
			return ctr==0?0:0.25*ctr;
		}
		
		public String matToString(Double[][] mat){
			String s = "";
			for (int y = 0; y < maze.height; y++) {
				for (int x = 0; x < maze.width; x++) {
					s += maze.getChar(x, y)+": "+(mat[x][y]<0.001?0.0:mat[x][y])+" ";
				}
				s += "\n";
			}
			return s;
		}
		
		// this is effectively the sensor model
		public char getSensorInput(char color){
			System.out.println("color is "+color);
			Random generator = new Random();
			double number = generator.nextDouble();
			return sortByRandom(number,color);
		}
		
		private char sortByRandom(double num, char sq){
			if (num<=0.88){
				return sq;
			}else{
				char[] temp = new char[tiles.length-1];
				int ctr = 0;
				for(char tile:tiles){
					if(tile!=sq){
						temp[ctr]=tile;
						ctr++;
					}
				}
				Random generator = new Random();
				char retval = temp[generator.nextInt(tiles.length-1)];
				return retval;
			}
		}
	}
}
