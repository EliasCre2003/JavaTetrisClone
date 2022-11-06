package eliascregard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;


public class GamePanel extends JPanel implements Runnable {
    final Dimension SCREEN_SIZE = new Dimension(1920, 1080);
    final Dimension DEFAULT_SCREEN_SIZE = new Dimension(1920, 1080);
    final double SCREEN_SCALE = (double) SCREEN_SIZE.width / DEFAULT_SCREEN_SIZE.width;
    int MAX_FRAME_RATE = 0;
    int MAX_TICKSPEED = 0;
    long timeAtMovement = System.nanoTime();
    public long timeSinceMovement() {
        return System.nanoTime() - timeAtMovement;
    }
    public Thread gameThread;
    GameTime time = new GameTime();
    KeyHandler keyH = new KeyHandler();
    Vector2D mousePos = new Vector2D(0, 0);
    double deltaT;
    public int tickSpeed;
    double renderDeltaT = 0;
    public int fps;

    final Line[] SCREEN_EDGES = new Line[] {
            new Line(new Vector2D(0, 0), new Vector2D(DEFAULT_SCREEN_SIZE.width, 0)),
            new Line(new Vector2D(DEFAULT_SCREEN_SIZE.width, 0), new Vector2D(DEFAULT_SCREEN_SIZE.width, DEFAULT_SCREEN_SIZE.height)),
            new Line(new Vector2D(DEFAULT_SCREEN_SIZE.width, DEFAULT_SCREEN_SIZE.height), new Vector2D(0, DEFAULT_SCREEN_SIZE.height)),
            new Line(new Vector2D(0, DEFAULT_SCREEN_SIZE.height), new Vector2D(0, 0))
    };

    final int DEFAULT_SQUARE_SIZE = 53;
    final int SQUARE_SIZE = (int) (DEFAULT_SQUARE_SIZE * SCREEN_SCALE);
    final Dimension GRID_SIZE = new Dimension(10, 24);
    TetrominoSquare[][] squareGrid = new TetrominoSquare[GRID_SIZE.height][GRID_SIZE.width];
    final Dimension GRID_AREA_SIZE = new Dimension(GRID_SIZE.width * SQUARE_SIZE, GRID_SIZE.height * SQUARE_SIZE);
    final Vector2D GRID_AREA_POSITION = new Vector2D((double) SCREEN_SIZE.width / 2 - (double) GRID_AREA_SIZE.width / 2,
                                               (double) SCREEN_SIZE.height / 2 - (double) GRID_AREA_SIZE.height / 2 - 2 * SQUARE_SIZE);
    final Vector2D NEXT_TETROMINO_AREA_POSITION = new Vector2D(GRID_AREA_POSITION.x + GRID_AREA_SIZE.width + 2 * SQUARE_SIZE,
                                                        GRID_AREA_POSITION.y + 5  * SQUARE_SIZE);
    final Dimension NEXT_TETROMINO_AREA_SIZE = new Dimension(6 * SQUARE_SIZE, 6 * SQUARE_SIZE);
    Tetromino currentTetromino;
    Tetromino nextTetromino;
    Tetromino shadow;
    Vector2D spawnPosition = new Vector2D(4, 4);
    double tetrominoSpeed = 1;
    int points = 0;
    boolean gameOver = false;

    public double[] sortArray(double[] array) {
        double[] sortedArray = Arrays.copyOf(array, array.length);
        for (int i = 0; i < sortedArray.length; i++) {
            for (int j = 0; j < sortedArray.length - 1; j++) {
                if (sortedArray[j] > sortedArray[j + 1]) {
                    double temp = sortedArray[j];
                    sortedArray[j] = sortedArray[j + 1];
                    sortedArray[j + 1] = temp;
                }
            }
        }
        return sortedArray;
    }

    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addToGrid(Tetromino tetromino) {
        for (TetrominoSquare square : tetromino.squares) {
            int y = (int) (square.position.y + tetromino.position.y);
            int x = (int) (square.position.x + tetromino.position.x);
            while (y >= squareGrid.length) {
                y--;
            }
            while (x >= squareGrid[0].length) {
                x--;
            }
            while (y < 0) {
                y++;
            }
            while (x < 0) {
                x++;
            }
            squareGrid[y][x] = square;
        }
    }

    public void moveTetromino(Tetromino tetromino, Vector2D direction) {
        boolean canMove = true;
        boolean landed = false;

        for (TetrominoSquare square : tetromino.squares) {
            if (square.position.x + tetromino.position.x + direction.x >= GRID_SIZE.width || square.position.x + tetromino.position.x + direction.x < 0) {
                canMove = false;
                break;
            }
            if (square.position.y + tetromino.position.y + direction.y >= GRID_SIZE.height) {
                landed = true;
                break;
            }
            if (squareGrid[(int) (square.position.y + tetromino.position.y + direction.y)][(int) (square.position.x + tetromino.position.x + direction.x)] != null) {
                if (direction.y > 0) {
                    landed = true;
                } else {
                    canMove = false;
                }
                break;
            }
        }
        if (canMove && !landed) {
            tetromino.position.add(direction, 1);
        }
        if (landed) {
            addToGrid(tetromino);
            updateGrid();
            spawnNewTetromino();
        }
        if (direction.y > 0) {
            timeAtMovement = System.nanoTime();
        }
    }

    public boolean allowedPlacement(Tetromino rotatedTetromino) {
        for (TetrominoSquare square : rotatedTetromino.squares) {
            if (square.position.x + rotatedTetromino.position.x >= GRID_SIZE.width || square.position.x + rotatedTetromino.position.x < 0) {
                return false;
            }
            if (square.position.y + rotatedTetromino.position.y >= GRID_SIZE.height - 1) {
                return false;
            }
            int y = (int) (square.position.y + rotatedTetromino.position.y);
            int x = (int) (square.position.x + rotatedTetromino.position.x);
            if (x < 0 || y < 0 || x >= squareGrid[0].length || y >= squareGrid.length) {
                return false;
            }
            if (squareGrid[y][x] != null) {
                return false;
            }
        }
        return true;
    }

    public void updateShadow() {
        shadow = currentTetromino.makeCopy();
        shadow.color = new Color(16, 16, 16);
        while (true) {
            for (TetrominoSquare square : shadow.squares) {
                if (square.position.y + shadow.position.y >= GRID_SIZE.height - 1) {
                    return;
                }
                int y = (int) (square.position.y + shadow.position.y + 1);
                int x = (int) (square.position.x + shadow.position.x);
                if (squareGrid[y][x] != null) {
                    return;
                }
            }
            shadow.position.y++;
        }
    }
    public void spawnNewTetromino() {
        currentTetromino = nextTetromino.makeCopy();
        currentTetromino.position.set(spawnPosition);
        nextTetromino = Tetromino.randomTetromino(new Vector2D(1, 1));
        for (TetrominoSquare square : currentTetromino.squares) {
            int y = (int) (square.position.y + currentTetromino.position.y);
            int x = (int) (square.position.x + currentTetromino.position.x);
            if (squareGrid[y][x] != null) {
                gameOver = true;
                break;
            }
        }
        updateShadow();
    }

    public void updateGrid() {
        int rowsDeleted = 0;
        for (TetrominoSquare[] row : squareGrid) {
            boolean full = true;
            for (TetrominoSquare square : row) {
                if (square == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                Arrays.fill(row, null);
                rowsDeleted++;
            }
        }
        if (rowsDeleted == 0) {
            return;
        }
        points += rowsDeleted * rowsDeleted;
        boolean aRowWasMoved = true;
        while (aRowWasMoved) {
            aRowWasMoved = false;
            for (int row = squareGrid.length-1; row > 0; row--) {
                boolean empty = true;
                for (int col = 0; col < squareGrid[row].length; col++) {
                    if (squareGrid[row][col] != null) {
                        empty = false;
                        break;
                    }
                }
                if (empty) {
                    for (int col = 0; col < squareGrid[row-1].length; col++) {
                        if (squareGrid[row-1][col] != null) {
                            aRowWasMoved = true;
                            break;
                        }
                    }
                    squareGrid[row] = Arrays.copyOf(squareGrid[row-1], squareGrid[row].length);
                    Arrays.fill(squareGrid[row-1], null);

                }
            }
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public GamePanel() {
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(new Color(0, 0, 0));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
//        this.addMouseListener(mouseH);
        this.setFocusable(true);
    }

    @Override
    public void run() {

        nextTetromino = Tetromino.randomTetromino(new Vector2D(1, 1));
        spawnNewTetromino();
        timeAtMovement = System.nanoTime();

        while (gameThread != null) {
            deltaT = time.getDeltaTime();
            tickSpeed = time.getFPS(deltaT);
            renderDeltaT += deltaT;
            fps = tickSpeed;
            if (fps > MAX_FRAME_RATE && MAX_FRAME_RATE > 0) {
                fps = MAX_FRAME_RATE;
            }

            if (keyH.escapePressed) {
                System.exit(0);
            }

            wait(10);
            update();
            if (MAX_FRAME_RATE > 0) {
                if (renderDeltaT >= 1.0 / MAX_FRAME_RATE) {
                    repaint();
                    renderDeltaT -= 1.0 / MAX_FRAME_RATE;
                }
            }
            else {
                repaint();
            }
        }
    }

    public void update() {
        mousePos.set(
                (double) MouseInfo.getPointerInfo().getLocation().x / SCREEN_SCALE,
                (double) MouseInfo.getPointerInfo().getLocation().y / SCREEN_SCALE
        );

        if (gameOver) {
            return;
        }

        if ((double) timeSinceMovement() / GameTime.NANO_TIME_CONVERTER_CONSTANT >= 1.0 / tetrominoSpeed) {
            moveTetromino(currentTetromino, new Vector2D(0,1));
        }
        if (keyH.leftPressed) {
            keyH.leftPressed = false;
            moveTetromino(currentTetromino, new Vector2D(-1,0));
        }

        if (keyH.rightPressed) {
            keyH.rightPressed = false;
            moveTetromino(currentTetromino, new Vector2D(1,0));
        }
        if (keyH.downPressed) {
            keyH.downPressed = false;
            moveTetromino(currentTetromino, new Vector2D(0,1));
        }
        if (keyH.zPressed) {
            keyH.zPressed = false;
            Tetromino rotatedTetromino = currentTetromino.returnRotated("left");
            if (allowedPlacement(rotatedTetromino)) {
                currentTetromino = rotatedTetromino;
            }
        }
        if (keyH.cPressed) {
            keyH.cPressed = false;
            Tetromino rotatedTetromino = currentTetromino.returnRotated("right");
            if (allowedPlacement(rotatedTetromino)) {
                currentTetromino = rotatedTetromino;
            }
        }
        if (keyH.xPressed) {
            keyH.xPressed = false;
            Tetromino rotatedTetromino = currentTetromino.returnDoubleRotated();
            if (allowedPlacement(rotatedTetromino)) {
                currentTetromino = rotatedTetromino;
            }
        }
        if (keyH.upPressed) {
            keyH.upPressed = false;
            Tetromino nextCopy = nextTetromino.makeCopy();
            nextCopy.position.set(currentTetromino.position);
            if (allowedPlacement(nextCopy)) {
                nextTetromino = currentTetromino.makeCopy();
                nextTetromino.position.set(new Vector2D(1, 1));
                currentTetromino = nextCopy;
            }
        }
        if (keyH.spacePressed) {
            keyH.spacePressed = false;
            updateShadow();
            currentTetromino.position.set(shadow.position);
            addToGrid(currentTetromino);
            updateGrid();
            spawnNewTetromino();
        }
        if (keyH.enterPressed) {
            keyH.enterPressed = false;
        }
        updateShadow();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(new Color(54, 54, 54));
        g2.fillRect(0, 0, SCREEN_SIZE.width, SCREEN_SIZE.height);

        g2.setColor(new Color(255, 0, 0));
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("Points: " + points, 10, 20);

        g2.setColor(new Color(25, 25, 25));
        g2.fillRect(
                (int) GRID_AREA_POSITION.x, (int) GRID_AREA_POSITION.y,
                GRID_AREA_SIZE.width, GRID_AREA_SIZE.height
        );

        if (shadow != null) {
            shadow.draw(g2, GRID_AREA_POSITION, SQUARE_SIZE);
        }
        currentTetromino.draw(g2, GRID_AREA_POSITION, SQUARE_SIZE);

        for (int row = 0; row < squareGrid.length; row++) {
            for (int col = 0; col < squareGrid[row].length; col++) {
                if (squareGrid[row][col] != null) {
                    Vector2D position = new Vector2D(
                            GRID_AREA_POSITION.x + col * SQUARE_SIZE,
                            (GRID_AREA_POSITION.y) + row * SQUARE_SIZE
                    );
                    squareGrid[row][col].draw(g2, position, SQUARE_SIZE);
                }
            }
        }

        g2.setColor(new Color(25, 25, 25));
        g2.fillRect(
                (int) NEXT_TETROMINO_AREA_POSITION.x, (int) NEXT_TETROMINO_AREA_POSITION.y,
                NEXT_TETROMINO_AREA_SIZE.width, NEXT_TETROMINO_AREA_SIZE.height
        );

        boolean outside = true;
        while (outside) {
            outside = false;
            for (TetrominoSquare square : nextTetromino.squares) {
                if (square.position.y + nextTetromino.position.y < 1) {
                    outside = true;
                    nextTetromino.position.y++;
                }
                if (square.position.x + nextTetromino.position.x < 1) {
                    outside = true;
                    nextTetromino.position.x++;
                }
                if (outside) { break; }
            }
        }
        nextTetromino.draw(g2, NEXT_TETROMINO_AREA_POSITION, SQUARE_SIZE);

        if (gameOver) {
            g2.setColor(new Color(255, 0, 0));
            g2.setFont(new Font("Arial", Font.PLAIN, 56));
            g2.drawString("Game Over", 100, 300);
        }

        g2.dispose();
    }
}