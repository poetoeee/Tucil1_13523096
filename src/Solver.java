import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Solver {
    private Board board;
    private List<Piece> pieces;
    private int iterationCount = 0; 

    public Solver(Board board, List<Piece> pieces) {
        this.board = board;
        this.pieces = pieces;
    }

    private boolean isBoardFullyCovered() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                if (board.getCell(i, j) == 'X') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean solve(int pieceIndex) {
        iterationCount++; 
        if (pieceIndex == pieces.size()) {
            return isBoardFullyCovered(); 
        }

        Piece piece = pieces.get(pieceIndex);
        List<Piece> transformedPieces = piece.getAllTransformations();

        for (Piece transformed : transformedPieces) {
            for (int row = 0; row <= board.getRows() - transformed.getHeight(); row++) {
                for (int col = 0; col <= board.getCols() - transformed.getWidth(); col++) {
                    if (board.canPlace(transformed, row, col)) {
                        board.placePiece(transformed, row, col); 
                        if (solve(pieceIndex + 1)) { 
                            return true; 
                        }
                        board.removePiece(transformed, row, col); 
                    }
                }
            }
        }
        return false; 
    }

    public void startSolving() {
        iterationCount = 0; 

        if (solve(0)) {
            System.out.println("Solution found:");
            board.printBoard(); 
        } else {
            System.out.println("No solution found.");
        }
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public void saveSolutionToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(board.getBoardString()); 
            System.out.println("Solusi berhasil disimpan ke " + filename);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan solusi: " + e.getMessage());
        }
    }
}