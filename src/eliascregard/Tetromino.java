package eliascregard;

import java.awt.*;

public class Tetromino {

    public Vector2D position;
    int type;
    public TetrominoSquare[] squares;
    public Color color;
    public int moves;

    public static final TetrominoSquare[][] SQUARE_TYPES = {
        new TetrominoSquare[] {                                         // Square - piece
            new TetrominoSquare(new Vector2D(0, 0)),            // X , X , 0 , 0 //
            new TetrominoSquare(new Vector2D(1, 0)),            // X , X , 0 , 0 //
            new TetrominoSquare(new Vector2D(0, 1)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(1, 1))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // Line - piece
            new TetrominoSquare(new Vector2D(0, 0)),            // X , X , X , X //
            new TetrominoSquare(new Vector2D(1, 0)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 0)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(3, 0))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // T - piece
            new TetrominoSquare(new Vector2D(0, 0)),            // X , X , X , 0 //
            new TetrominoSquare(new Vector2D(1, 0)),            // 0 , X , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 0)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(1, 1))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // L - piece
            new TetrominoSquare(new Vector2D(0, 0)),            // X , X , X , 0 //
            new TetrominoSquare(new Vector2D(1, 0)),            // 0 , 0 , X , 0 //
            new TetrominoSquare(new Vector2D(2, 0)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 1))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // J - piece
            new TetrominoSquare(new Vector2D(0, 1)),            // 0 , 0 , X , 0 //
            new TetrominoSquare(new Vector2D(1, 1)),            // X , X , X , 0 //
            new TetrominoSquare(new Vector2D(2, 1)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 0))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // S - piece
            new TetrominoSquare(new Vector2D(0, 0)),            // X , X , 0 , 0 //
            new TetrominoSquare(new Vector2D(1, 0)),            // 0 , X , X , 0 //
            new TetrominoSquare(new Vector2D(1, 1)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 1))             // 0 , 0 , 0 , 0 //
        },
        new TetrominoSquare[] {                                         // Z - piece
            new TetrominoSquare(new Vector2D(0, 1)),            // 0 , X , X , 0 //
            new TetrominoSquare(new Vector2D(1, 1)),            // X , X , 0 , 0 //
            new TetrominoSquare(new Vector2D(1, 0)),            // 0 , 0 , 0 , 0 //
            new TetrominoSquare(new Vector2D(2, 0))             // 0 , 0 , 0 , 0 //
        }
    };

    public static final Color[] COLOR_TYPES = {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW
    };

    public TetrominoSquare[] makeCopy(TetrominoSquare[] squares) {
        TetrominoSquare[] newSquares = new TetrominoSquare[squares.length];
        for (int i = 0; i < squares.length; i++) {
            newSquares[i] = squares[i].makeCopy();
        }
        return newSquares;
    }

    public Tetromino(Vector2D position, int type, Color color) {
        this.position = position;
        this.type = type;
        this.squares = makeCopy(SQUARE_TYPES[type]);
        this.color = color;
        this.moves = 0;
    }

    public static Tetromino randomTetromino(Vector2D position) {
        int type = (int) (Math.random() * SQUARE_TYPES.length);
        int colorIndex = (int) (Math.random() * COLOR_TYPES.length);
        Color color = COLOR_TYPES[colorIndex];
        for (int i = 0; i < SQUARE_TYPES[type].length; i++) {
            SQUARE_TYPES[type][i].color = color;
        }
        return new Tetromino(position, type, color);
    }

    public Tetromino makeCopy() {
        Tetromino newTetromino = new Tetromino(this.position.makeCopy(), this.type, this.color);
        newTetromino.squares = makeCopy(this.squares);
        return newTetromino;
    }

    public void rotateLeft() {
        if (this.type == 0) {
            return;
        }
        for (TetrominoSquare square : this.squares) {
            //noinspection SuspiciousNameCombination
            square.position.set(square.position.y, -square.position.x);
        }
    }

    public void rotateRight() {
        if (this.type == 0) {
            return;
        }
        for (TetrominoSquare square : this.squares) {
            //noinspection SuspiciousNameCombination
            square.position.set(-square.position.y, square.position.x);
        }
    }

    public Tetromino returnRotated(String direction) {
        Tetromino newTetromino = this.makeCopy();
        if (direction.equals("left")) {
            newTetromino.rotateLeft();
        } else if (direction.equals("right")) {
            newTetromino.rotateRight();
        }
        return newTetromino;
    }
    public Tetromino returnDoubleRotated() {
        Tetromino newTetromino = this.makeCopy();
        newTetromino.rotateLeft();
        newTetromino.rotateLeft();
        return newTetromino;
    }

    public void draw(Graphics2D g2, Vector2D position, int gridSize) {
        for (TetrominoSquare square : this.squares) {
            square.draw(g2, this, position, gridSize);
        }
    }
}