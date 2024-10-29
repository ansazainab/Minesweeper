package minesweeper;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 864;
    public static int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int FPS = 30;

    private boolean timerStarted = false;
    private long startTime;
    private long elapsedTime = 0;
    public boolean gameEnded = false;
    private boolean mineClicked = false;
    private Tile clickedMine = null;

    public static int numOfMines = 100;

    public static Random random = new Random();
	
	public static int[][] mineCountColour = new int[][] {
            {0,0,0},
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };

    public App() {
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    private Tile[][] board;
    private HashMap<String, PImage> sprites = new HashMap<>();

    public PImage getSprite(String s) {
        PImage result = sprites.get(s);
        if (result == null) {
            result = loadImage(this.getClass().getResource(s+".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            sprites.put(s, result);
        }
        return result;
    }

    public Tile[][] getBoard() {
        return this.board;
    }

	@Override
    public void setup() {
        frameRate(FPS);
        String[] sprites = new String[] {
                "tile1",
                "tile2",
                "flag",
                "tile"
        };
        for (int i = 0; i < sprites.length; i++) {
            getSprite(sprites[i]);
        }
        for (int i = 0; i < 10; i++) {
            getSprite("mine"+String.valueOf(i));
        }

        resetGame();
    }

    public void resetGame() {
        this.board = new Tile[(HEIGHT-TOPBAR)/CELLSIZE][WIDTH/CELLSIZE];

        for (int i = 0; i < this.board.length; i++) {
            for (int i2 = 0; i2 < this.board[i].length; i2++) {
                this.board[i][i2] = new Tile(i2, i);
            }
        }

        int minesPlaced = 0;
        Random rand = new Random();
        while (minesPlaced < numOfMines) {
            int randX = rand.nextInt(WIDTH / CELLSIZE);
            int randY = rand.nextInt((HEIGHT - TOPBAR) / CELLSIZE);
            if (!this.board[randY][randX].hasMine()) {
                this.board[randY][randX].mine = true;
                minesPlaced++;
            }
        }

        timerStarted = false;
        startTime = 0;
        elapsedTime = 0;
        gameEnded = false;
        mineClicked = false;
    }

	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKey() == 'R' || event.getKey() == 'r') {
            resetGame();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY()-App.TOPBAR;
        if (!gameEnded) {
            if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
                if (!timerStarted && !gameEnded) {
                    timerStarted = true;
                    startTime = millis();
                }

                Tile t = board[mouseY/App.CELLSIZE][mouseX/App.CELLSIZE];
                
                if (e.getButton() == PConstants.LEFT) {
                    if (!t.isFlagged() && !t.hasMine()) {
                        t.reveal(this);
                    }

                    if (t.hasMine() && !mineClicked) {
                        t.reveal(this);
                        gameEnded = true;
                        mineClicked = true;
                        clickedMine = t;
                        revealAllMines();
                    }
                }
                else if (e.getButton() == PConstants.RIGHT) {
                    if (!t.isRevealed()) {
                        t.toggleFlag();
                    }
                }
            }
        }
    }

    private void revealAllMines() {
        int delay = 0;
        if (clickedMine != null) {
            clickedMine.startExplosion(this.frameCount);
            delay += 3;
        }

        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                Tile t = board[i][i2];
                if (t.hasMine() && t != clickedMine) {
                    t.startExplosion(this.frameCount + delay);
                    delay += 3;
                }
            }
        }
    }

	@Override
    public void draw() {
        background(200,200,200);
        int nonRevealedNonMines = 0;
        for (int i = 0; i < this.board.length; i++) {
            for (int i2 = 0; i2 < this.board[i].length; i2++) {
                this.board[i][i2].draw(this);
                if (!this.board[i][i2].isRevealed() && !this.board[i][i2].hasMine()) {
                    nonRevealedNonMines += 1;
                }
            }
        }

        if (timerStarted && !gameEnded) {
            elapsedTime = (millis() - startTime) / 1000;
        }

        textSize(30);
        fill(0);
        text("Time: " + elapsedTime + "s", WIDTH-180, App.TOPBAR - 10);

        if (nonRevealedNonMines == 0) {
            textSize(30);
            fill(0);
            text("You win!", 150, App.TOPBAR-10);
            gameEnded = true;
        }
        else if (gameEnded) {
            textSize(30);
            fill(0);
            text("You lost!", 150, App.TOPBAR-10);
        }
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                int num = Integer.parseInt(args[0]);
                if (num > 0) {
                    numOfMines = num;
                }
            } catch (NumberFormatException e) {
                numOfMines = 100;
            }
        }
        PApplet.main("minesweeper.App");
    }

}