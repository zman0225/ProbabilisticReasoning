package probabilistic_reasoning;


public class driver {
	public static void main(String[] args){
		Maze maze = Maze.readFromFile("simple.maz");
//		System.out.println(maze+" "+maze.getChar(0, 1));
		PRProblem prob = new PRProblem(maze,0,0,new char[]{'e','e','e','e'});
	}
}
