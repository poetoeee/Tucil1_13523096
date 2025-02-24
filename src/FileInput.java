import java.io.*;
import java.util.*;

public class FileInput {
    private Board board;
    private List<Piece> pieces;

    public FileInput(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String[] line = br.readLine().trim().split(" ");
            int n = Integer.parseInt(line[0]); 
            int m = Integer.parseInt(line[1]); 
            int p = Integer.parseInt(line[2]); 

            String type = br.readLine().trim();

            if (type.equals("DEFAULT")) {
                this.board = new Board(n, m);
            } 
            else if (type.equals("CUSTOM")) {
                this.board = new Board(n, m); 
                String[] customGrid = new String[n];
                for (int i = 0; i < n; i++) {
                    customGrid[i] = br.readLine().trim(); 
                }
                board.setCustomBoard(customGrid);
            }

            this.pieces = new ArrayList<>();

            String pieceLine;
            List<String> currentPiece = new ArrayList<>();
            Character currentLabel = null;
            int pieceCount = 0;

            while ((pieceLine = br.readLine()) != null) {
                if (pieceLine.isEmpty()) continue; 
                
                char firstChar = pieceLine.trim().charAt(0);

                if (currentLabel != null && firstChar != currentLabel && pieceCount < p) {
                    pieces.add(new Piece(convertToMatrix(currentPiece), currentLabel));
                    currentPiece.clear();
                    pieceCount++;

                    if (pieceCount == p) break;
                }

                if (currentLabel == null || currentPiece.isEmpty()) {
                    currentLabel = firstChar;
                }

                currentPiece.add(pieceLine);
            }

            if (!currentPiece.isEmpty() && pieceCount < p) {
                pieces.add(new Piece(convertToMatrix(currentPiece), currentLabel));
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    private char[][] convertToMatrix(List<String> pieceLines) {
        int h = pieceLines.size();
        int w = pieceLines.stream().mapToInt(String::length).max().orElse(0);
        char[][] matrix = new char[h][w];

        for (int i = 0; i < h; i++) {
            Arrays.fill(matrix[i], 'X'); 
            for (int j = 0; j < pieceLines.get(i).length(); j++) {
                if (pieceLines.get(i).charAt(j) == ' ') {
                    matrix[i][j] = 'X';
                    continue;
                }
                matrix[i][j] = pieceLines.get(i).charAt(j);
            }
        }

        return matrix;
    }
}