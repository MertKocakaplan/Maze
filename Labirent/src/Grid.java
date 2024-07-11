import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Grid extends JFrame {

    int rows = 30;
    int cols = 30;
    private final Stack<Block> blockStack = new Stack<>();
    Block[][] blocks;
    private JPanel gridPanel;
    private Block currentBlock;
    private JPanel controlPanel;
    private JButton solveButton;
    private boolean stopSolving = false;
    private Timer timer;
    private int delay = 10;

    public Grid() {
        setTitle("Maze");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeGrid();
        initializeControlPanel();
        pack();
        setVisible(true);
        generateMaze();
    }

    public static void main(String[] args) {
        new Grid();
    }

    private void initializeControlPanel() {
        controlPanel = new JPanel();

        JComboBox<Integer> rowsComboBox = new JComboBox<>(new Integer[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100});
        rowsComboBox.setSelectedItem(rows);
        controlPanel.add(new JLabel("Rows:"));
        controlPanel.add(rowsComboBox);

        JComboBox<Integer> colsComboBox = new JComboBox<>(new Integer[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100});
        colsComboBox.setSelectedItem(cols);
        controlPanel.add(new JLabel("Columns:"));
        controlPanel.add(colsComboBox);

        JButton regenerateButton = new JButton("Regenerate Maze");
        regenerateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSolving = true;
                if (timer != null) {
                    timer.stop();
                    timer = null;
                }
                gridPanel.removeAll();
                initializeGrid();
                rows = (Integer) rowsComboBox.getSelectedItem();
                cols = (Integer) colsComboBox.getSelectedItem();
                generateMaze();
                gridPanel.revalidate();
                gridPanel.repaint();
                stopSolving = false;
            }
        });
        controlPanel.add(regenerateButton);

        solveButton = new JButton("Solve Maze");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (solveButton.getText().equals("Solve Maze")) {
                    solveButton.setText("Stop Solving");
                    stopSolving = false;
                    mazeSolver();
                } else {
                    solveButton.setText("Solve Maze");
                    stopSolving = true;
                }
            }
        });
        controlPanel.add(solveButton);

        // Delay slider ekle
        JSlider delaySlider = new JSlider(0, 100, delay);
        delaySlider.setMajorTickSpacing(10);
        delaySlider.setMinorTickSpacing(1);
        delaySlider.setPaintTicks(true);
        delaySlider.setPaintLabels(true);
        delaySlider.addChangeListener(e -> delay = delaySlider.getValue());
        controlPanel.add(new JLabel("Delay:"));
        controlPanel.add(delaySlider);

        add(controlPanel, BorderLayout.SOUTH);
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

    List<Block> getNeighbors(Block block) {
        List<Block> neighbors = new ArrayList<>();

        if (block.thisRow > 0 && !block.topWall) {
            neighbors.add(blocks[block.thisRow - 1][block.thisCol]);
        }
        if (block.thisCol < cols - 1 && !block.rightWall) {
            neighbors.add(blocks[block.thisRow][block.thisCol + 1]);
        }
        if (block.thisRow < rows - 1 && !block.bottomWall) {
            neighbors.add(blocks[block.thisRow + 1][block.thisCol]);
        }
        if (block.thisCol > 0 && !block.leftWall) {
            neighbors.add(blocks[block.thisRow][block.thisCol - 1]);
        }

        return neighbors;
    }

    public void mazeSolver() {
        Block startBlock = blocks[0][0];
        Block endBlock = blocks[rows - 1][cols - 1];
        Stack<Block> stack = new Stack<>();
        Set<Block> visited = new HashSet<>();
        Map<Block, Block> cameFrom = new HashMap<>();

        stack.push(startBlock);
        visited.add(startBlock);

        timer = new Timer(delay, null); // Delay slider'dan gelen deðeri kullan
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (stopSolving) {
                    solveButton.setText("Solve Maze"); // Stop sýrasýnda buton metnini deðiþtir
                    timer.stop();
                    return;
                }

                Block currentBlock = stack.pop();

                if (currentBlock == endBlock) {
                    System.out.println("Maze solved!");
                    solveButton.setText("Solve Maze"); // Çözüm tamamlandýðýnda buton metnini deðiþtir
                    timer.stop();

                    List<Block> path = new ArrayList<>();
                    Block pathBlock = currentBlock;
                    while (pathBlock != null) {
                        path.add(pathBlock);
                        pathBlock = cameFrom.get(pathBlock);
                    }
                    Collections.reverse(path);

                    for (Block block : visited) {
                        if (!path.contains(block)) {
                            block.setBackground(null);
                        }
                    }

                    for (Block block : path) {
                        block.setBackground(Color.GREEN);
                    }
                    return;
                }

                List<Block> neighbors = getNeighbors(currentBlock);
                for (Block neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        cameFrom.put(neighbor, currentBlock);
                        stack.push(neighbor);
                    }
                }

                currentBlock.setBackground(Color.darkGray);
            }
        });

        timer.start();
    }

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
            topWall = rightWall = bottomWall = leftWall = true;
            neighbors = new ArrayList<>();
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
