import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class GUI extends JFrame {
    private JPanel boardPanel;
    private JPanel piecesPanel;
    private JLabel statusLabel;
    private JLabel iterationsLabel;
    private JLabel timeLabel;
    private Board board;
    private List<Piece> pieces;
    private static final int MAX_CELL_SIZE = 100; 
    private static final int MIN_CELL_SIZE = 10; 
    private int cellSize = MAX_CELL_SIZE;

    private static final Color[] PIECE_COLORS = {
        new Color(255, 0, 0),      // A - Red
        new Color(0, 255, 0),      // B - Green
        new Color(0, 0, 255),      // C - Blue
        new Color(255, 255, 0),    // D - Yellow
        new Color(255, 0, 255),    // E - Magenta
        new Color(0, 255, 255),    // F - Cyan
        new Color(128, 0, 0),      // G - Maroon
        new Color(0, 128, 0),      // H - Dark Green
        new Color(0, 0, 128),      // I - Navy
        new Color(128, 128, 0),    // J - Olive
        new Color(128, 0, 128),    // K - Purple
        new Color(0, 128, 128),    // L - Teal
        new Color(255, 128, 0),    // M - Orange
        new Color(255, 0, 128),    // N - Pink
        new Color(128, 255, 0),    // O - Lime
        new Color(0, 255, 128),    // P - Spring Green
        new Color(128, 0, 255),    // Q - Purple Blue
        new Color(0, 128, 255),    // R - Sky Blue
        new Color(255, 128, 128),  // S - Light Red
        new Color(128, 255, 128),  // T - Light Green
        new Color(128, 128, 255),  // U - Light Blue
        new Color(255, 255, 128),  // V - Light Yellow
        new Color(255, 128, 255),  // W - Light Magenta
        new Color(128, 255, 255),  // X - Light Cyan
        new Color(192, 192, 192),  // Y - Light Gray
        new Color(128, 128, 128)   // Z - Gray
    };
    
    public GUI() {
        setTitle("Puzzle Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadButton = new JButton("Load Puzzle");
        JButton solveButton = new JButton("Solve");
        JButton saveButton = new JButton("Save Solution");
        controlPanel.add(loadButton);
        controlPanel.add(solveButton);
        controlPanel.add(saveButton);
        add(controlPanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); 
        
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setBackground(Color.LIGHT_GRAY);
        
        JScrollPane boardScroll = new JScrollPane(boardPanel);
        boardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        boardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        piecesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieces(g);
            }
        };
        piecesPanel.setBackground(Color.WHITE);
        
        JScrollPane piecesScroll = new JScrollPane(piecesPanel);
        piecesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        piecesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        splitPane.setLeftComponent(boardScroll);
        splitPane.setRightComponent(piecesScroll);
        add(splitPane, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statusLabel = new JLabel("Status: Ready");
        iterationsLabel = new JLabel("Iterations: 0");
        timeLabel = new JLabel("Time: 0 ms");
        statusPanel.add(statusLabel);
        statusPanel.add(iterationsLabel);
        statusPanel.add(timeLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        loadButton.addActionListener(e -> loadPuzzle());
        solveButton.addActionListener(e -> solvePuzzle());
        saveButton.addActionListener(e -> saveSolution());
    }
    
    private void adjustCellSize() {
        if (board == null) return;
        
        Dimension viewSize = boardPanel.getParent().getSize();
        int maxWidth = viewSize.width - 50; 
        int maxHeight = viewSize.height - 50;
        
        int cellWidthByWidth = maxWidth / board.getCols();
        int cellHeightByHeight = maxHeight / board.getRows();
        
        cellSize = Math.min(Math.min(cellWidthByWidth, cellHeightByHeight), MAX_CELL_SIZE);
        cellSize = Math.max(cellSize, MIN_CELL_SIZE);
        
        int preferredWidth = cellSize * board.getCols() + 50;
        int preferredHeight = cellSize * board.getRows() + 50;
        boardPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        
        if (pieces != null) {
            int maxPieceHeight = pieces.stream()
                .mapToInt(p -> (p.getHeight() + 1) * (cellSize / 2))
                .sum();
            piecesPanel.setPreferredSize(new Dimension(300, maxPieceHeight + 50));
        }
    }
    
    private void loadPuzzle() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                FileInput fileInput = new FileInput(file.getPath());
                board = fileInput.getBoard();
                pieces = fileInput.getPieces();
                adjustCellSize();
                boardPanel.revalidate();
                boardPanel.repaint();
                piecesPanel.revalidate();
                piecesPanel.repaint();
                statusLabel.setText("Status: Puzzle loaded");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
            }
        }
    }
    
    private void solvePuzzle() {
        if (board == null || pieces == null) {
            JOptionPane.showMessageDialog(this, "Please load a puzzle first!");
            return;
        }
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                statusLabel.setText("Status: Solving...");
                long startTime = System.currentTimeMillis();
                
                Solver solver = new Solver(board, pieces);
                // solver.startSolving();
                
                if (solver.solve(0)){statusLabel.setText("Status: Solution found");}
                else {statusLabel.setText("Status: No Solution found");}
                long endTime = System.currentTimeMillis();
                timeLabel.setText("Time: " + (endTime - startTime) + " ms");
                iterationsLabel.setText("Iterations: " + solver.getIterationCount());
                
                
                boardPanel.repaint();
                return null;
            }
        }.execute();
    }
    
    private void saveSolution() {
        if (board == null) {
            JOptionPane.showMessageDialog(this, "No solution to save!");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getPath();
                Solver solver = new Solver(board, pieces);
                solver.saveSolutionToFile(filename);
                JOptionPane.showMessageDialog(this, "Solution saved successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving solution: " + e.getMessage());
            }
        }
    }
    
    private void drawBoard(Graphics g) {
        if (board == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int rows = board.getRows();
        int cols = board.getCols();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellSize + 25; 
                int y = i * cellSize + 25;
                
                // Draw cell background
                g.setColor(Color.WHITE);
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
                
                // Draw cell content
                char cell = board.getCell(i, j);
                if (cell != 'X' && cell != '.') {
                    g.setColor(getPieceColor(cell));
                    g.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                    g.setColor(Color.BLACK);
                    Font font = new Font("Arial", Font.BOLD, cellSize / 3);
                    g.setFont(font);
                    FontMetrics metrics = g.getFontMetrics(font);
                    String text = String.valueOf(cell);
                    int textX = x + (cellSize - metrics.stringWidth(text)) / 2;
                    int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
                    g.drawString(text, textX, textY);
                }
            }
        }
    }
    
    private void drawPieces(Graphics g) {
        if (pieces == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int pieceScale = cellSize / 2;
        int yOffset = 10;
        
        for (Piece piece : pieces) {
            char[][] shape = piece.getShape();
            int height = piece.getHeight();
            int width = piece.getWidth();
            
            g.setColor(Color.BLACK);
            Font font = new Font("Arial", Font.BOLD, 14);
            g.setFont(font);
            g.drawString(piece.getLabel() + ":", 10, yOffset + pieceScale);
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int x = j * pieceScale + 50; 
                    int y = yOffset + i * pieceScale;
                    
                    if (shape[i][j] != 'X') {
                        g.setColor(getPieceColor(piece.getLabel()));
                        g.fillRect(x, y, pieceScale, pieceScale);
                        g.setColor(Color.BLACK);
                        g.drawRect(x, y, pieceScale, pieceScale);
                    }
                }
            }
            
            yOffset += (height + 1) * pieceScale; 
        }
    }
    
    private Color getPieceColor(char label) {
        if (label >= 'A' && label <= 'Z') {
            return PIECE_COLORS[label - 'A'];
        }
        return Color.GRAY;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}