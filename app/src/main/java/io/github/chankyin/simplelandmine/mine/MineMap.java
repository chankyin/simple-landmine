package io.github.chankyin.simplelandmine.mine;

import android.support.annotation.Size;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

public class MineMap{
    public final static boolean MINE = true, SAFE = false;

    // immutable!!!
    private final boolean[][] secret;
    private final int width, height;
    private final int totalMines;

    // mutable
    private final TileState[][] states;
    // mutable
    private final int[][] surroundCache;

    private boolean exploded = false;

    public MineMap(boolean[][] secret){
        this.secret = secret;
        width = secret.length;
        height = secret[0].length;
        int minesCount = 0;
        for(boolean[] row : secret){
            for(boolean tile : row){
                if(tile){
                    minesCount++;
                }
            }
        }
        totalMines = minesCount;

        states = new TileState[width][];
        for(int x = 0; x < width; x++){
            states[x] = new TileState[height];
            Arrays.fill(states[x], TileState.EMPTY);
        }
        surroundCache = new int[width][];
        for(int x = 0; x < surroundCache.length; x++){
            surroundCache[x] = new int[height];
            Arrays.fill(surroundCache[x], -1);
        }

        exploded = false;
    }

    public MineMap(MineMap copy){
        secret = copy.secret;
        width = copy.width;
        height = copy.height;
        totalMines = copy.totalMines;
        states = new TileState[width][];
        for(int x = 0; x < width; x++){
            states[x] = Arrays.copyOf(copy.states[x], height);
        }
        surroundCache = new int[width][];
        for(int x = 0; x < width; x++){
            surroundCache[x] = Arrays.copyOf(copy.surroundCache[x], height);
        }

        exploded = copy.exploded;
    }

    public int doOpen(Coordinate coord) throws IllegalStateException, Explosion{
        return doOpen(coord.x, coord.y);
    }

    public int doOpen(int x, int y) throws IllegalStateException, Explosion{
        validateInMap(x, y);
        if(states[x][y] != TileState.EMPTY){
            throw new IllegalStateException("Cannot open non-empty tile");
        }
        states[x][y] = TileState.OPENED;
        if(secret[x][y]){
            explode(x, y);
            throw new Explosion(new Coordinate(x, y));
        }
        return countSurroundingMines(x, y);
    }

    public int countSurroundingMines(Coordinate coord){
        return countSurroundingMines(coord.x, coord.y);
    }

    public int countSurroundingMines(int x, int y){
        validateInMap(x, y);

        if(surroundCache[x][y] != -1){
            return surroundCache[x][y];
        }

        int[] around = around(secret, x, y);
        int cnt = 0;
        for(int i = 0; i < 9; i++){
            if(i == 4 || around[i] == -1){
                continue;
            }
            if(around[i] == 1){
                cnt++;
            }
        }
        return surroundCache[x][y] = cnt;
    }

    private void explode(int x, int y){
        // TODO
        states[x][y] = TileState.EXPLODED_MINE;
        exploded = true;
    }

    public boolean toggleFlag(Coordinate coord) throws IllegalStateException{
        return toggleFlag(coord.x, coord.y);
    }

    public boolean toggleFlag(int x, int y) throws IllegalStateException{
        if(states[x][y] == TileState.OPENED){
            throw new IllegalStateException("Cannot flag already-revealed tile");
        }
        if(states[x][y] == TileState.EMPTY){
            states[x][y] = TileState.FLAGGED;
            return true;
        }
        if(states[x][y] == TileState.FLAGGED){
            states[x][y] = TileState.EMPTY;
            return false;
        }
        throw new AssertionError("Non-exploded game has tile of state " + states[x][y].name());
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    @Size(9)
    public TileState[] statesAround(Coordinate coord){
        return statesAround(coord.x, coord.y);
    }

    @Size(9)
    public TileState[] statesAround(int x, int y){
        int width = getWidth();
        int height = getHeight();

        TileState[] ret = new TileState[9];
        if(y > 0){
            ret[0] = x > 0 ? states[x - 1][y - 1] : null;
            ret[1] = states[x][y - 1];
            ret[2] = x + 1 < height ? states[x + 1][y - 1] : null;
        }
        ret[3] = x > 0 ? states[x - 1][y] : null;
        ret[4] = states[x][y];
        ret[5] = x + 1 < height ? states[x + 1][y] : null;
        if(y + 1 < height){
            ret[6] = x > 0 ? states[x - 1][y + 1] : null;
            ret[7] = states[x][y + 1];
            ret[8] = x + 1 < height ? states[x + 1][y + 1] : null;
        }
        return ret;
    }

    @Size(9)
    public static int[] around(boolean[][] secret, int x, int y){
        int width = secret.length;
        int height = secret[0].length;

        int[] ret = new int[9];
        // @formatter:off
        ret[0] = x > 0          && y > 0          ? secret[x - 1][y - 1] ? 1 : 0 : -1;
        ret[1] =                   y > 0          ? secret[x]    [y - 1] ? 1 : 0 : -1;
        ret[2] = x + 1 < width  && y > 0          ? secret[x + 1][y - 1] ? 1 : 0 : -1;

        ret[3] = x > 0                            ? secret[x - 1][y]     ? 1 : 0 : -1;
        ret[4] =                                    secret[x]    [y]     ? 1 : 0;
        ret[5] = x + 1 < width                    ? secret[x + 1][y]     ? 1 : 0 : -1;

        ret[6] = x > 0          && y + 1 < height ? secret[x - 1][y + 1] ? 1 : 0 : -1;
        ret[7] =                   y + 1 < height ? secret[x    ][y + 1] ? 1 : 0 : -1;
        ret[8] = x + 1 < width  && y + 1 < height ? secret[x + 1][y + 1] ? 1 : 0 : -1;
        // @formatter:on

        return ret;
    }

    private void validateInMap(int x, int y){
        if(x < 0 || x >= width || y < 0 || y >= height){
            throw new IllegalArgumentException();
        }
    }

    public void init(Random random){
        for(int i = 0; i < 10; ){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if(states[x][y] != TileState.EMPTY || secret[x][y] || countSurroundingMines(x, y) > 0){
                continue;
            }

            i++;
            try{
                doOpen(x, y);
            }catch(Explosion explosion){
                throw new AssertionError(explosion);
            }
        }

        // TODO attempt solve
    }

    public TileState getState(Coordinate coord){
        return getState(coord.x, coord.y);
    }

    public TileState getState(int x, int y){
        return states[x][y];
    }

    public OutputStream debugPrint(OutputStream stream) throws IOException{
        stream.write(debugPrint(new StringBuilder()).toString().getBytes());
        return stream;
    }

    public StringBuilder debugPrint(StringBuilder builder) throws IOException{
        builder
                .append("Legend: F flagged mine tile").append('\n')
                .append("        X flagged empty tile").append('\n')
                .append("      0-9 opened empty tile, showing number of adjacent mines (including diagonal)").append('\n')
                .append("        _ unopened empty tile").append('\n')
                .append("        . unopened tile with mine").append('\n')
                .append("        * opened mine tile (exploded)").append('\n')
        ;
        builder.append("== START MINE MAP ==\n");
        builder.append("     ");
        for(int i = 0; i < width; i++){
            builder.append(i % 10 == 0 ? Integer.toString(i / 10) + "0" : i % 10 == 1 ? "" : " ");
        }
        builder.append('\n');
        for(int y = 0; y < height; y++){
            builder.append(StringUtils.leftPad(Integer.toString(y), 4, '0')).append(':');
            for(int x = 0; x < width; x++){
                switch(states[x][y]){
                    case EMPTY:
                    case UNOPENED_MINE:
                        builder.append(secret[x][y] ? '.' : '_');
                        break;
                    case OPENED:
                        builder.append(Integer.toString(countSurroundingMines(x, y)));
                        break;
                    case FLAGGED:
                    case PROBLEMATIC_WRONG_FLAG:
                        builder.append(secret[x][y] ? 'F' : 'X');
                        break;
                    case EXPLODED_MINE:
                        builder.append('*');
                        break;
                }
            }
            builder.append('\n');
        }
        return builder.append("== END MINE MAP ==\n");
    }
}
