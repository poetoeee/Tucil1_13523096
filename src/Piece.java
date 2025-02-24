import java.util.*;
import java.util.stream.Collectors;

class Piece {
    private char[][] shape; 
    private char label;

    public Piece(char[][] shape, char label) {
        this.shape = deepCopy(shape);
        this.label = label;
    }

    public Piece(List<String> shapeLines, char label) {
        this.label = label;
        int height = shapeLines.size();
        int width = shapeLines.stream().mapToInt(String::length).max().orElse(0);

        shape = new char[height][width];

        for (int i = 0; i < height; i++) {
            String line = shapeLines.get(i);
            Arrays.fill(shape[i], ' '); 
            for (int j = 0; j < line.length(); j++) {
                shape[i][j] = line.charAt(j);
            }
        }

        System.out.println("Debug: Bentuk yang tersimpan untuk label " + label);
        for (char[] row : shape) {
            System.out.println(new String(row)); 
        }
        System.out.println();
    }

    public char[][] getShape() {
        return shape;
    }

    public int getHeight() {
        return shape.length;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public char getLabel() {
        return label;
    }

    public List<Piece> getAllTransformations() {
        Set<String> seenShapes = new HashSet<>();
        List<Piece> transformations = new ArrayList<>();

        char[][] currentShape = shape;
        for (int i = 0; i < 4; i++) { 
            currentShape = rotate90(currentShape);
            addUniqueTransformation(transformations, seenShapes, new Piece(currentShape, label));
        }

        currentShape = mirror(shape);
        for (int i = 0; i < 4; i++) {
            currentShape = rotate90(currentShape);
            addUniqueTransformation(transformations, seenShapes, new Piece(currentShape, label));
        }

        return transformations;
    }

    public void printShape() {
        for (char[] row : shape) {
            for (char cell : row) {
                System.out.print((cell == 'X') ? ' ' : cell);
            }
            System.out.println();
        }
    }

    private void addUniqueTransformation(List<Piece> transformations, Set<String> seenShapes, Piece newPiece) {
        String shapeString = Arrays.stream(newPiece.getShape())
                                   .map(row -> new String(row))
                                   .collect(Collectors.joining("\n"));

        if (seenShapes.add(shapeString)) { 
            transformations.add(newPiece);
        }
    }

    private char[][] rotate90(char[][] shape) {
        int h = shape.length;
        int w = shape[0].length;
        char[][] rotated = new char[w][h];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                rotated[j][h - 1 - i] = shape[i][j];
            }
        }
        return rotated;
    }

    private char[][] mirror(char[][] shape) {
        int h = shape.length;
        int w = shape[0].length;
        char[][] mirrored = new char[h][w];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                mirrored[i][w - 1 - j] = shape[i][j];
            }
        }
        return mirrored;
    }

    private char[][] deepCopy(char[][] original) {
        return Arrays.stream(original)
                     .map(char[]::clone)
                     .toArray(char[][]::new);
    }
}