package eliascregard;

import java.awt.*;

public class TetrominoSquare {

    Color color;
    Vector2D position;

    public TetrominoSquare(Color color, Vector2D position) {
        this.color = color;
        this.position = position;
    }
    public TetrominoSquare(Vector2D position) {
        this(Color.WHITE, position);
    }

    public TetrominoSquare makeCopy() {
        return new TetrominoSquare(this.color, this.position.makeCopy());
    }

    public void draw(Graphics2D g2, int gridSize) {
        g2.setColor(this.color);
        g2.fillRect((int) (this.position.x) * gridSize,
                    (int) (this.position.y) * gridSize,
                    gridSize, gridSize);
    }

    public void draw(Graphics2D g2, Tetromino tetromino, Vector2D position, int gridSize) {
        g2.setColor(tetromino.color);
        g2.fillRect((int) (this.position.x + tetromino.position.x) * gridSize + (int) position.x,
                    (int) (this.position.y + tetromino.position.y) * gridSize + (int) position.y,
                    gridSize, gridSize);
        g2.setColor(new Color(0,0,0));
        g2.setStroke(new BasicStroke(1));
        g2.drawRect((int) (this.position.x + tetromino.position.x) * gridSize + (int) position.x,
                (int) (this.position.y + tetromino.position.y) * gridSize + (int) position.y,
                gridSize, gridSize);

    }

    public void draw(Graphics2D g2, Vector2D position, int gridSize) {
        g2.setColor(this.color);
        g2.fillRect((int) position.x, (int) position.y, gridSize, gridSize);
        g2.setColor(new Color(0,0,0));
        g2.setStroke(new BasicStroke(1));
        g2.drawRect((int) position.x, (int) position.y, gridSize, gridSize);
    }
}
