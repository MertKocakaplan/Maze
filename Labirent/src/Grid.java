import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;

public class Grid extends JFrame { // Labirentin çerçevesini oluşturacak sınıf

    final int rows = 30;      // Satır sayısı
    final int cols = 30;      // Sütun sayısı
    private final Stack<Block> blockStack = new Stack<>();   // A stack of blocks to track the current path
    Block[][] blocks;          // Griddeki blokların dizisi
    private JPanel gridPanel;  // Gridin UI da görüntüsü için
    private Block currentBlock;

    /**
     * Gridin ekran çıktısını gösterir, metodları çağırır.
     */

    public Grid() {
        setTitle("Labirent");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeGrid();
        setVisible(true);       // pencereyi görünür yapar
        generateMaze();


    }

    /**
     * The main method for the Grid class.
     * Creates a new instance of the Grid class.
     * @param args The command line arguments (not used)
     */
    public static void main(String[] args) {
        new Grid();
    }

    /**
     * Initializes the grid of blocks.
     * Creates a 2D array of Block objects, adds them to the gridPanel, and adds their neighbors.
     */
    /**
     * Initializes the grid of blocks.
     * Creates a 2D array of Block objects, adds them to the gridPanel, and adds their neighbors.
     */
    private void initializeGrid() {
        blocks = new Block[rows][cols];   // Create a new 2D array of Block objects

        gridPanel = new JPanel(new GridLayout(rows, cols));   // Create a new JPanel with a GridLayout for the blocks
        gridPanel.setPreferredSize(new Dimension(900, 700));   // Set the preferred size of the panel for the window

        // Create a new Block object for each element in the 2D array, add it to the gridPanel, and add its neighbors
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Block block = new Block(i, j, this);
                blocks[i][j] = block;
                gridPanel.add(block);

                // Remove the top border of the start block
                if (i == 0 && j == 0) {
                    block.setTopWall(false);
                    block.setBackground(Color.GREEN);
                }

                // Remove the bottom border of the end block
                if (i == rows - 1 && j == cols - 1) {
                    block.setBottomWall(false);
                    block.setBackground(Color.RED);
                }
            }
        }

        // Add the neighbors for each block in the grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                blocks[i][j].addNeighbors();
            }
        }

        add(gridPanel);   // Add the gridPanel to the JFrame
    }


    /**
     * recursive backtracking algorithm kullanarak labirenti oluşturur
     */
    private void generateMaze() {
        currentBlock = blocks[0][0];   // Start at the top left block
        currentBlock.setVisited(true);   // Mark it as visited
        blockStack.push(currentBlock);   // Push it onto the stack

        while (!blockStack.isEmpty()) {   // While there are still blocks on the stack
            Block nextCurrent = currentBlock.pickRandomNeighbor();   // Pick a random unvisited neighbor

            if (nextCurrent != null) {   // If a neighbor is found
                removeWall(currentBlock, nextCurrent);   // Remove the wall between the current block and the neighbor
                blockStack.push(currentBlock);   // Push the current block onto the stack
                currentBlock = nextCurrent;   // Set the neighbor as the current block
                currentBlock.setVisited(true);   // Mark it as visited
            } else {   // If no unvisited neighbors are found
                currentBlock = blockStack.pop();   // Pop the top block from the stack and set it as the current block
            }
        }
    }

    private void removeWall(Block a, Block b) {
        if (a.thisRow == b.thisRow) {   // If the two blocks are in the same row
            if (a.thisCol < b.thisCol) {   // If block a is to the left of block b
                a.setRightWall(false);   // Remove the right wall of block a
                b.setLeftWall(false);   // Remove the left wall of block b
            } else {   // If block b is to the left of block a
                a.setLeftWall(false);   // Remove the left wall of block a
                b.setRightWall(false);   // Remove the right wall of block b
            }
        } else {   // If the two blocks are in the same column
            if (a.thisRow < b.thisRow) {   // If block a is above block b
                a.setBottomWall(false);   // Remove the bottom wall of block a
                b.setTopWall(false);   // Remove the top wall of block b
            } else {   // If block b is above block a
                a.setTopWall(false);   // Remove the top wall of block a
                b.setBottomWall(false);   // Remove the bottom wall of block b
            }
        }
    }
}

/**
 * tek bir bloğu tanımlayan sınıf
 */
class Block extends JPanel {
    final Grid grid;
    final int thisRow;
    final int thisCol;
    private final ArrayList<Block> neighbors;
    private boolean visited;
    private boolean topWall, rightWall, bottomWall, leftWall;

    public Block(int i, int j, Grid grid) {
        thisRow = i;
        thisCol = j;
        setPreferredSize(new Dimension(20, 20));  // Set the preferred size of the block for painting
        this.grid = grid;
        visited = false;
        topWall = rightWall = bottomWall = leftWall = true;  // Initialize all walls to be present
        neighbors = new ArrayList<>();   // Initialize the list of neighbors to be empty
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    public void setTopWall(boolean topWall) {
        this.topWall = topWall;
    }
    public void setRightWall(boolean rightWall) {
        this.rightWall = rightWall;
    }
    public void setBottomWall(boolean bottomWall) {
        this.bottomWall = bottomWall;
    }
    public void setLeftWall(boolean leftWall) {
        this.leftWall = leftWall;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        if (topWall) {
            g.drawLine(0, 0, getWidth(), 0);
        }
        if (rightWall) {
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        }
        if (bottomWall) {
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        }
        if (leftWall) {
            g.drawLine(0, 0, 0, getHeight());
        }
    }


    public void addNeighbors() {
        if (thisRow > 0) {  // If the block is not in the first row of the grid
            neighbors.add(grid.blocks[thisRow - 1][thisCol]);  // Add the block above it to the list of neighbors
        }
        if (thisCol < grid.cols - 1) {  // If the block is not in the last column of the grid
            neighbors.add(grid.blocks[thisRow][thisCol + 1]);  // Add the block to the right of it to the list of neighbors
        }
        if (thisRow < grid.rows - 1) {  // If the block is not in the last row of the grid
            neighbors.add(grid.blocks[thisRow + 1][thisCol]);  // Add the block below it to the list of neighbors
        }
        if (thisCol > 0) {  // If the block is not in the first column of the grid
            neighbors.add(grid.blocks[thisRow][thisCol - 1]);  // Add the block to the left of it to the list of neighbors
        }
    }


    public Block pickRandomNeighbor() {
        ArrayList<Block> unvisitedNeighbors = new ArrayList<>();

        for (Block neighbor : neighbors) {
            if (!neighbor.visited) {   // If the neighboring block has not been visited
                unvisitedNeighbors.add(neighbor);   // Add it to the list of unvisited neighbors
            }
        }

        if (unvisitedNeighbors.isEmpty()) {  // If there are no unvisited neighbors
            return null;  // Return null
        }

        int randomIndex = new Random().nextInt(unvisitedNeighbors.size());  // Choose a random index from the list of unvisited neighbors
        return unvisitedNeighbors.get(randomIndex);   // Return the block at that index
    }


}
