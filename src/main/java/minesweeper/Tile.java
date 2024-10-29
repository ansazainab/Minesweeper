package minesweeper;

import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private boolean revealed;
    private boolean flagged;
    private int x;
    private int y;

    public boolean mine = false;
    private boolean exploded;
    private int explosionFrame;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.revealed = false;
        this.flagged = false;
        this.exploded = false;
        this.explosionFrame = -1;
    }

    public void draw(App app) {
        if (exploded) {
            drawExplosion(app);
            return;
        }
        PImage tile = app.getSprite("tile1");
        if (this.revealed) {
            tile = app.getSprite("tile");
            app.image(tile, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
        else if (app.mouseX >= x*App.CELLSIZE && app.mouseX < (x+1)*App.CELLSIZE && 
        app.mouseY >= y*App.CELLSIZE+App.TOPBAR && app.mouseY < (y+1)*App.CELLSIZE+App.TOPBAR && !app.gameEnded) {
            tile = app.getSprite("tile2");
        }
        app.image(tile, x*App.CELLSIZE, y*App.CELLSIZE+App.TOPBAR);
        
        if (this.flagged) {
            tile = app.getSprite("flag");
            app.image(tile, x*App.CELLSIZE, y*App.CELLSIZE+App.TOPBAR);
        }

        if (this.revealed && !this.mine) {
            int adjacentMines = this.countAdjacentMines(app);
            if (adjacentMines > 0) {
                app.textSize(20);
                app.fill(App.mineCountColour[adjacentMines][0], App.mineCountColour[adjacentMines][1], App.mineCountColour[adjacentMines][2]);
                app.text(adjacentMines, x*App.CELLSIZE + App.CELLSIZE/2 - 7, y*App.CELLSIZE+App.TOPBAR + App.CELLSIZE/2 + 8);
            }
        }

        if (this.hasAdjacentEmptyTile(app) && !this.mine && !this.exploded) {
            this.revealed = true;
        }
    }

    private void drawExplosion(App app) {
        if (explosionFrame != -1) {
            int mineNum = (app.frameCount - explosionFrame) / 3;
            if (mineNum < 10 && mineNum >= 0) {
                PImage tile = app.getSprite("mine"+ String.valueOf(mineNum));
                app.image(tile, x*App.CELLSIZE, y*App.CELLSIZE+App.TOPBAR);
            }
            else if (mineNum < 0) {
                PImage tile = app.getSprite("tile1");
                app.image(tile, x*App.CELLSIZE, y*App.CELLSIZE+App.TOPBAR);
            }
            else if (mineNum >= 10) {
                PImage tile = app.getSprite("mine9");
                app.image(tile, x*App.CELLSIZE, y*App.CELLSIZE+App.TOPBAR);
            }
        }
    }

    public void startExplosion(int currentFrame) {
        this.exploded = true;
        this.explosionFrame = currentFrame;
    }

    public boolean hasMine() {
        return this.mine;
    }

    public void reveal(App app) {
        if (this.revealed || this.flagged) {
            return;
        }
        this.revealed = true;
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    public boolean isFlagged() {
        return this.flagged;
    }

    public void toggleFlag() {
        this.flagged = !this.flagged;
    }

    public List<Tile> getAdjacentTiles(App app) {
        ArrayList<Tile> result = new ArrayList<>();
        if (x+1 < app.getBoard()[y].length) {
            result.add(app.getBoard()[y][x+1]);
        }
        if (y+1 < app.getBoard().length && x+1 < app.getBoard()[y].length) {
            result.add(app.getBoard()[y+1][x+1]);
        }
        if (y-1 >= 0 && x+1 < app.getBoard()[y].length) {
            result.add(app.getBoard()[y-1][x+1]);
        }
        if (y+1 < app.getBoard().length) {
            result.add(app.getBoard()[y+1][x]);
        }
        if (y-1 >= 0) {
            result.add(app.getBoard()[y-1][x]);
        }
        if (x-1 >= 0) {
            result.add(app.getBoard()[y][x-1]);
        }
        if (x-1 >= 0 && y+1 < app.getBoard().length) {
            result.add(app.getBoard()[y+1][x-1]);
        }
        if (x-1 >= 0 && y-1 >= 0) {
            result.add(app.getBoard()[y-1][x-1]);
        }
        return result;
    }

    public boolean hasAdjacentEmptyTile(App app) {
        for (Tile t : getAdjacentTiles(app)) {
            if (t.revealed && t.countAdjacentMines(app) == 0) {
                return true;
            }
        }
        return false;
    }

    public int countAdjacentMines(App app) {
        int count = 0;
        for (Tile t : getAdjacentTiles(app)) {
            if (t.hasMine()) {
                count++;
            }
        }
        return count;
    }
}