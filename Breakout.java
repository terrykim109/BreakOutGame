import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends JPanel implements KeyListener, ActionListener {

    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of turns */
    private static final int NTURNS = 3;

    // Ball position
    private int ballX = WIDTH / 2;
    private int ballY = HEIGHT / 2;

    // Ball speed
    private int ballDX = 2;
    private int ballDY = 3;

    // Paddle position
    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;

    // Bricks set up 
    private boolean[][] bricks = new boolean[NBRICK_ROWS][NBRICKS_PER_ROW];

    // Keeping score
    private int score = 0;

    private Timer gameTimer;

    public Breakout() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.gray);
        this.addKeyListener(this);
        this.setFocusable(true);

        // For the bricks
        for (int row = 0; row < NBRICK_ROWS; row++) {
            for (int col = 0; col < NBRICKS_PER_ROW; col++) {
                bricks[row][col] = true;
            }
        }

        // timer update
        gameTimer = new Timer(5, this);
        gameTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            paddleX -= 15;
        } else if (key == KeyEvent.VK_RIGHT) {
            paddleX += 15;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Maybe not needed for this game.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this game
    }

    @Override
    public void actionPerformed(ActionEvent e) {
           
        // Ball position change
        ballX += ballDX;
        ballY += ballDY;
        
        // Paddle height
        int paddleY = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

        // Paddle collision with the ball
        if (ballY + BALL_RADIUS * 2 >= paddleY && ballX + BALL_RADIUS >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballDY = -ballDY;
            ballY = paddleY - BALL_RADIUS * 2;
        }

        // Collision with walls
        // Side collision
        if (ballX <= 0 || ballX + BALL_RADIUS * 2 >= WIDTH) {
            ballDX = -ballDX;
        }

        // Top collision
        if (ballY <= 0) {
            ballDY = -ballDY;
        }

        // Bottom collision
        if (ballY + BALL_RADIUS * 2 >= HEIGHT) {
            ballX = WIDTH / 2;
            ballY = HEIGHT / 2;
            ballDY = -ballDY;
        }
        
        // Brick collision
        for (int row = 0; row < NBRICK_ROWS; row++) {
            for (int col = 0; col < NBRICKS_PER_ROW; col++) {
                if (bricks[row][col]) {

                    // Position of the brick
                    int brickX = col * ( BRICK_WIDTH + BRICK_SEP)+ (WIDTH - ( NBRICKS_PER_ROW * BRICK_WIDTH + (NBRICKS_PER_ROW - 1) * BRICK_SEP)) / 2;
                    int brickY = BRICK_Y_OFFSET + row * ( BRICK_HEIGHT + BRICK_SEP);
                    
                    // Using rectangles for a ball as well for intersect methods down below for collision detection
                    Rectangle ball = new Rectangle(ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);
                    Rectangle brick = new Rectangle(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
        
                    if (ball.intersects(brick)) {
                        bricks[row][col] = false;  
                        ballDY = -ballDY;  
                        
                        // Increment the score per brick hit
                        score += 1; 
                 

                        break;
                                       
                    }
                }
            }
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Setting the bricks
        setupBricks(g);

        // For the ball
        g.setColor(Color.blue);
        g.fillOval(ballX, ballY, BALL_RADIUS * 2, BALL_RADIUS * 2);

        // For the paddle
        g.setColor(Color.pink);
        g.fillRect(paddleX, HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
   
        // For the score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
    }

    /** Sets up the bricks in the initial configuration */
    private void setupBricks(Graphics g) {

         for (int row = 0; row < NBRICK_ROWS; row++) {
            for (int col = 0; col < NBRICKS_PER_ROW; col++) {
                int brickX = col * (BRICK_WIDTH + BRICK_SEP)
                        + (WIDTH - (NBRICKS_PER_ROW * BRICK_WIDTH + (NBRICKS_PER_ROW - 1) * BRICK_SEP)) / 2;
                int brickY = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
                if (bricks[row][col]) {
                    g.setColor(getColorForRow(row));
                    g.fillRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
                }
    
            }
        }
    }

    /** Returns the color to use for a given row based on rainbow scheme */
    private Color getColorForRow(int row) {
        if (row < 2)
            return Color.RED;
        else if (row < 4)
            return Color.ORANGE;
        else if (row < 6)
            return Color.YELLOW;
        else if (row < 8)
            return Color.GREEN;
        else
            return Color.CYAN;
    }

    // Main function
    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout");
        Breakout newGame = new Breakout();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(newGame);
        frame.pack();
        frame.setVisible(true);
    }
}