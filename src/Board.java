class Board {
    private int rows, cols;
    private char[][] grid;

    public Board(int rows, int cols ) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols]; 

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 'X'; 
            }
        }
    }

    public void setCustomBoard(String[] customGrid) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = customGrid[i].charAt(j);
                if (c == 'X') {
                    grid[i][j] = 'X'; 
                } else {
                    grid[i][j] = '.'; 
                }
            }
        }
    }
    
    public char getCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return grid[row][col];
        }
        throw new IndexOutOfBoundsException("Invalid grid position: " + row + "," + col);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private static final String RESET = "\u001B[0m";
    private static final String[] COLORS = {
        "\u001B[31m",  // Red (A)
        "\u001B[32m",  // Green (B)
        "\u001B[33m",  // Yellow (C)
        "\u001B[34m",  // Blue (D)
        "\u001B[35m",  // Magenta (E)
        "\u001B[36m",  // Cyan (F)
        "\u001B[91m",  // Bright Red (G)
        "\u001B[92m",  // Bright Green (H)
        "\u001B[93m",  // Bright Yellow (I)
        "\u001B[94m",  // Bright Blue (J)
        "\u001B[95m",  // Bright Magenta (K)
        "\u001B[96m",  // Bright Cyan (L)
        "\u001B[97m",  // Bright White (M)
        "\u001B[90m",  // Dark Gray (N)
        "\u001B[37m",  // Light Gray (O)
        "\u001B[98m",  // Light Red (P)
        "\u001B[99m",  // Light Green (Q)
        "\u001B[100m", // Light Yellow (R)
        "\u001B[101m", // Light Blue (S)
        "\u001B[102m"  // Light Magenta (T)
    };

    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '.') {
                    System.out.print(c + " "); 
                } else {
                    int colorIndex = (c - 'A') % COLORS.length;
                    System.out.print(COLORS[colorIndex] + c + " " + RESET); 
                }
            }
            System.out.println();
        }
    }

    public boolean canPlace(Piece piece, int startX, int startY) {
        char[][] shape = piece.getShape();
        int height = piece.getHeight();
        int width = piece.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (shape[i][j] != 'X') {
                    int x = startX + i;
                    int y = startY + j;
                    if (x < 0 || x >= rows || y < 0 || y >= cols || grid[x][y] != 'X') {
                        return false; 
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(Piece piece, int startX, int startY) {
        char[][] shape = piece.getShape();
        int height = piece.getHeight();
        int width = piece.getWidth();
        char label = piece.getLabel(); 

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (shape[i][j] != 'X') {
                    grid[startX + i][startY + j] = label;
                }
            }
        }
    }

    public void removePiece(Piece piece, int startX, int startY) {
        char[][] shape = piece.getShape();
        int height = piece.getHeight();
        int width = piece.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (shape[i][j] != 'X') {
                    grid[startX + i][startY + j] = 'X'; 
                }
            }
        }
    }

    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) { 
            for (char cell : row) {
                sb.append(cell).append(" ");
            }sb.append("\n");
        }return sb.toString();
    }
}