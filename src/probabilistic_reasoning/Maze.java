package probabilistic_reasoning;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Maze {
	final static Charset ENCODING = StandardCharsets.UTF_8;

	// A few useful constants to describe actions
	public static int[] NORTH = {0, 1};
	public static int[] EAST = {1, 0};
	public static int[] SOUTH = {0, -1};
	public static int[] WEST = {-1, 0};
	public static int[] PASS = {0, 0};
	public static int[][] moves = new int[][]{NORTH,EAST,SOUTH,WEST};
	public int width;
	public int height;
	public int numOfOb;
	private char[][] grid;

	public static Maze readFromFile(String filename) {
		Maze m = new Maze();

		try {
			List<String> lines = readFile(filename);
			m.height = lines.size();

			int y = 0;
			int obCtr = 0;
			m.grid = new char[m.height][];
			for (String line : lines) {
				m.width = line.length();
				m.grid[m.height - y - 1] = new char[m.width];
				for (int x = 0; x < line.length(); x++) {
					// (0, 0) should be bottom left, so flip y as 
					//  we read from file into array:
					m.grid[m.height - y - 1][x] = line.charAt(x);
					if(m.grid[m.height - y - 1][x]=='#'){
						obCtr++;
					}
				}
				y++;

				// System.out.println(line.length());
			}
			m.numOfOb = obCtr;
			return m;
		} catch (IOException E) {
			return null;
		}
	}

	public int[] charToDirection(char dir){
		switch(Character.toLowerCase(dir)){
			case 'e':
				return EAST;
			case 'w':
				return WEST;
			case 'n':
				return NORTH;
			case 's':
				return SOUTH;
		}
		return PASS;
	}
	
	private static List<String> readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllLines(path, ENCODING);
	}

	public char getChar(int x, int y) {
		return grid[y][x];
	}
	

	
	// is the location x, y on the map, and also a legal floor tile (not a wall)?
	public boolean isLegal(int x, int y) {
		// on the map
		if(x >= 0 && x < width && y >= 0 && y < height) {
			// and it's a floor tile, not a wall tile:
			return getChar(y, x) != '#';
		}
		return false;
	}
	
	public char[][] getGrid(){
		return this.grid;
	}
	
	public char getOnColor(int[] loc){
		return getOnColor(loc[0],loc[1]);
	}
	
	public char getOnColor(int x, int y){
		if(isLegal(x,y)){
			return this.getChar(x, y);
		}
		return '#';
	}
	
	public int[] makeMove(int[] from, int[] dir){
		return makeMove(from[0],from[1],dir);
	}
	
	public int[] makeMove(int x, int y, int[] dir){
		int nX = x+dir[0];
		int nY = y+dir[1];
		if(isLegal(nX,nY)){
			return new int[]{nX,nY};
		}
		return new int[]{x,y};
	}
	
	public String toString() {
		String s = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				s += grid[x][y];
			}
			s += "\n";
		}
		return s;
	}

	public static void main(String args[]) {
		Maze m = Maze.readFromFile("simple.maz");
		System.out.println(m);
	}

}
