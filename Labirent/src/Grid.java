import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;

public class Grid extends JFrame { // Labirentin çerçevesini oluşturacak sınıf

    final int rows = 30;      // Satır sayısı
    final int cols = 30;      // Sütun sayısı
    private final Stack<Block> blockStack = new Stack<>();
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
        solveMaze();


    }


    public static void main(String[] args) {
        new Grid();
    }

    private void initializeGrid() {
        blocks = new Block[rows][cols];

        gridPanel = new JPanel(new GridLayout(rows, cols));
        gridPanel.setPreferredSize(new Dimension(900, 700));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Block block = new Block(i, j, this);
                blocks[i][j] = block;
                gridPanel.add(block);


                if (i == 0 && j == 0) {
                    block.setTopWall(false);
                    block.setBackground(Color.GREEN);
                }


                if (i == rows - 1 && j == cols - 1) {
                    block.setBottomWall(false);
                    block.setBackground(Color.RED);
                }
            }
        }


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                blocks[i][j].addNeighbors();
            }
        }

        add(gridPanel);
    }

    /**
     * recursive backtracking algorithm kullanarak labirenti oluşturur
     */
    private void generateMaze() {
        currentBlock = blocks[0][0];
        currentBlock.setVisited(true);
        blockStack.push(currentBlock);

        while (!blockStack.isEmpty()) {
            Block nextCurrent = currentBlock.pickRandomNeighbor();

            if (nextCurrent != null) {
                removeWall(currentBlock, nextCurrent);
                blockStack.push(currentBlock);
                currentBlock = nextCurrent;
                currentBlock.setVisited(true);
            } else {
                currentBlock = blockStack.pop();
            }
        }


    }


    private void removeWall(Block a, Block b) {
        if (a.thisRow == b.thisRow) {
            if (a.thisCol < b.thisCol) {
                a.setRightWall(false);
                b.setLeftWall(false);
            } else {
                a.setLeftWall(false);
                b.setRightWall(false);
            }
        } else {
            if (a.thisRow < b.thisRow) {
                a.setBottomWall(false);
                b.setTopWall(false);
            } else {
                a.setTopWall(false);
                b.setBottomWall(false);
            }
        }
    }

    public void solveMazeDFS() {
        Block startBlock = blocks[0][0];
        Block endBlock = blocks[rows-1][cols-1];
        Stack<Block> stack = new Stack<>();
        Set<Block> visited = new HashSet<>();
        Map<Block, Block> cameFrom = new HashMap<>();

        stack.push(startBlock);
        visited.add(startBlock);

        while (!stack.isEmpty()) {
            Block currentBlock = stack.pop();
            currentBlock.mazeVisited(true);

            if (currentBlock == endBlock) {
                System.out.println("Maze solved!");
                // reconstruct path
                List<Block> path = new ArrayList<>();
                path.add(endBlock);
                while (cameFrom.containsKey(currentBlock)) {
                    currentBlock = cameFrom.get(currentBlock);
                    path.add(currentBlock);
                }
                Collections.reverse(path);
                // highlight path
                for (Block block : path) {
                    block.setBackground(Color.YELLOW);
                }
                return;
            }

            for (Block neighbor : currentBlock.getNeighbors()) {
                if (!visited.contains(neighbor) && !neighbor.hasAnyWalls()) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, currentBlock);
                    stack.push(neighbor);
                }
            }
        }

        System.out.println("Maze cannot be solved!");
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

        private boolean solveVisited;
        private boolean topWall, rightWall, bottomWall, leftWall;

        public Block(int i, int j, Grid grid) {
            thisRow = i;
            thisCol = j;
            setPreferredSize(new Dimension(20, 20));
            this.grid = grid;
            visited = false;
            solveVisited = false;
            topWall = rightWall = bottomWall = leftWall = true;
            neighbors = new ArrayList<>();
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public void mazeVisited(boolean visited) {
            this.solveVisited = solveVisited;
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
            if (thisRow > 0) {
                neighbors.add(grid.blocks[thisRow - 1][thisCol]);
            }
            if (thisCol < grid.cols - 1) {
                neighbors.add(grid.blocks[thisRow][thisCol + 1]);
            }
            if (thisRow < grid.rows - 1) {
                neighbors.add(grid.blocks[thisRow + 1][thisCol]);
            }
            if (thisCol > 0) {
                neighbors.add(grid.blocks[thisRow][thisCol - 1]);
            }
        }


        public Block pickRandomNeighbor() {
            ArrayList<Block> unvisitedNeighbors = new ArrayList<>();

            for (Block neighbor : neighbors) {
                if (!neighbor.visited) {
                    unvisitedNeighbors.add(neighbor);
                }
            }

            if (unvisitedNeighbors.isEmpty()) {
                return null;
            }

            int randomIndex = new Random().nextInt(unvisitedNeighbors.size());
            return unvisitedNeighbors.get(randomIndex);
        }



    }
}
