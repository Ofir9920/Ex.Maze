
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;

    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {         //Use only bfs algorithm
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    break;
                case Definitions.ALGORITHM_BFS:
                    result = bfs();
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }


    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bfs () {
        Queue <Vertex> vertices = new LinkedList<>();
        vertices.add(new Vertex(this.startRow,this.startColumn));
        while (!vertices.isEmpty()) {
        Vertex vertex = vertices.remove();
        if (!isVisited(vertex)) {
            this.visited[vertex.getX()][vertex.getY()] = true;
            setSquareAsVisited(vertex.getX(), vertex.getY(), true);
            if (vertex.getX() == values.length-1 && vertex.getY() == values.length-1) {
                return true;
            }
            List <Vertex> vertexNeighbor = neighbor(vertex);
            for (Vertex neighbor : vertexNeighbor) {
                if (!isVisited(neighbor)) {
                    vertices.add(neighbor);
                    }
                }
            }
        }
        return false;
    }


    public List<Vertex> neighbor (Vertex vertex) {
        List<Vertex> neighbors = new LinkedList<>();
        List<Vertex> copyList = new LinkedList<>();

        neighbors.add(new Vertex(vertex.getX() + 1, vertex.getY()));
        neighbors.add(new Vertex(vertex.getX() - 1, vertex.getY()));
        neighbors.add(new Vertex(vertex.getX(), vertex.getY() + 1));
        neighbors.add(new Vertex(vertex.getX(), vertex.getY() - 1));

        for (Vertex vertices : neighbors){                                          //Adding to new list and delete them
            if (vertices.getX() < 0 || vertices.getY() < 0 || vertices.getX() >= values.length ||
                    vertices.getY() >= values.length || values[vertices.getX()][vertices.getY()] == Definitions.OBSTACLE){
                copyList.add(vertices);
            }
        }
        neighbors.removeAll(copyList);

            return neighbors;

    }

               public boolean isVisited (Vertex vertex) {
        boolean beenVisited = false;
        if (visited[vertex.getX()][vertex.getY()]) {
            beenVisited = true;
        } else {
            beenVisited = false;
        }
        return beenVisited;
    }


    

}
