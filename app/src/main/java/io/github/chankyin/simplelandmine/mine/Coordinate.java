package io.github.chankyin.simplelandmine.mine;

import android.support.annotation.IntRange;

import java.util.ArrayList;
import java.util.List;

public class Coordinate{
    public final static int SIDE_UP_LEFT = 0;
    public final static int SIDE_UP = 1;
    public final static int SIDE_UP_RIGHT = 2;
    public final static int SIDE_LEFT = 3;
    public final static int SIDE_RIGHT = 5;
    public final static int SIDE_DOWN_LEFT = 6;
    public final static int SIDE_DOWN = 7;
    public final static int SIDE_DOWN_RIGHT = 8;

    public final int x;
    public final int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinate[] around(int width, int height){
        List<Coordinate> list = new ArrayList<>(8);
        if(y > 0){
            if(x > 0){
                list.add(new Coordinate(x - 1, y - 1));
            }
            list.add(new Coordinate(x, y - 1));
            if(x + 1 < width){
                list.add(new Coordinate(x + 1, y - 1));
            }
        }

        if(x > 0){
            list.add(new Coordinate(x - 1, y));
        }
        if(x + 1 < width){
            list.add(new Coordinate(x + 1, y));
        }

        if(y + 1 < height){
            if(x > 0){
                list.add(new Coordinate(x - 1, y + 1));
            }
            list.add(new Coordinate(x, y + 1));
            if(x + 1 < width){
                list.add(new Coordinate(x + 1, y + 1));
            }
        }

        return list.toArray(new Coordinate[list.size()]);
    }

    public Coordinate index(@IntRange(from = 0, to = 8) int index){
        return index(index, 1);
    }

    public Coordinate index(@IntRange(from = 0, to = 8) int index, int steps){
        switch(index){
            case 0:
                return new Coordinate(x - steps, y - steps);
            case 1:
                return new Coordinate(x, y - steps);
            case 2:
                return new Coordinate(x + steps, y - steps);
            case 3:
                return new Coordinate(x - steps, y);
            case 4:
                return new Coordinate(x, y);
            case 5:
                return new Coordinate(x + steps, y);
            case 6:
                return new Coordinate(x - steps, y + steps);
            case 7:
                return new Coordinate(x, y + steps);
            case 8:
                return new Coordinate(x + steps, y + steps);
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean outOfBounds(MineMap map){
        return outOfBounds(map.getWidth(), map.getHeight());
    }

    public boolean outOfBounds(int width, int height){
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    public static boolean areIndicesAdjacent(int i, int j){
        if(i > j){
            return areIndicesAdjacent(j, i);
        }
        return i == 0 && j == 1 ||
                i == 1 && j == 2 ||
                i == 2 && j == 5 ||
                i == 5 && j == 8 ||
                i == 7 && j == 8 ||
                i == 6 && j == 7 ||
                i == 3 && j == 6 ||
                i == 0 && j == 3;
    }

    public AdjacentInfo[] furtherAdjacentCoords(MineMap map, int i, int j){
        if(i > j){
            return furtherAdjacentCoords(map, j, i);
        }
        if(i == SIDE_UP_LEFT && j == SIDE_UP){
            Coordinate up = index(SIDE_UP, 2);
            return up.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_LEFT), SIDE_UP, SIDE_UP_RIGHT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_LEFT), SIDE_UP, SIDE_UP_RIGHT),
                    new AdjacentInfo(up, SIDE_DOWN_LEFT, SIDE_DOWN),
                    new AdjacentInfo(up.index(SIDE_LEFT), SIDE_DOWN, SIDE_DOWN_RIGHT)
            };
        }
        if(i == SIDE_DOWN_LEFT && j == SIDE_DOWN){
            Coordinate down = index(SIDE_DOWN, 2);
            return down.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_LEFT), SIDE_DOWN, SIDE_DOWN_RIGHT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_LEFT), SIDE_DOWN, SIDE_DOWN_RIGHT),
                    new AdjacentInfo(down, SIDE_UP_LEFT, SIDE_UP),
                    new AdjacentInfo(down.index(SIDE_LEFT), SIDE_UP, SIDE_UP_RIGHT)
            };
        }
        if(i == SIDE_UP && j == SIDE_UP_RIGHT){
            Coordinate up = index(SIDE_UP, 2);
            return up.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_RIGHT), SIDE_UP_LEFT, SIDE_UP)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_RIGHT), SIDE_UP_LEFT, SIDE_UP),
                    new AdjacentInfo(up, SIDE_DOWN, SIDE_DOWN_RIGHT),
                    new AdjacentInfo(up.index(SIDE_RIGHT), SIDE_DOWN_LEFT, SIDE_DOWN)
            };
        }
        if(i == SIDE_DOWN && j == SIDE_DOWN_RIGHT){
            Coordinate down = index(SIDE_DOWN, 2);
            return down.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_RIGHT), SIDE_DOWN_LEFT, SIDE_DOWN)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_RIGHT), SIDE_DOWN_LEFT, SIDE_DOWN),
                    new AdjacentInfo(down, SIDE_UP, SIDE_UP_RIGHT),
                    new AdjacentInfo(down.index(SIDE_RIGHT), SIDE_UP_LEFT, SIDE_UP)
            };
        }

        if(i == SIDE_UP_LEFT && j == SIDE_LEFT){
            Coordinate left = index(SIDE_LEFT, 2);
            return left.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_UP), SIDE_LEFT, SIDE_DOWN_LEFT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_UP), SIDE_LEFT, SIDE_DOWN_LEFT),
                    new AdjacentInfo(left, SIDE_UP_RIGHT, SIDE_RIGHT),
                    new AdjacentInfo(left.index(SIDE_UP), SIDE_RIGHT, SIDE_DOWN_RIGHT)
            };
        }
        if(i == SIDE_LEFT && j == SIDE_DOWN_LEFT){
            Coordinate left = index(SIDE_LEFT, 2);
            AdjacentInfo[] ret = left.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_DOWN), SIDE_UP_LEFT, SIDE_LEFT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_DOWN), SIDE_UP_LEFT, SIDE_LEFT),
                    new AdjacentInfo(left, SIDE_RIGHT, SIDE_DOWN_RIGHT),
                    new AdjacentInfo(left.index(SIDE_DOWN), SIDE_UP_RIGHT, SIDE_RIGHT)
            };
            return ret;
        }
        if(i == SIDE_UP_RIGHT && j == SIDE_RIGHT){
            Coordinate right = index(SIDE_RIGHT, 2);
            return right.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_UP), SIDE_RIGHT, SIDE_DOWN_RIGHT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_UP), SIDE_RIGHT, SIDE_DOWN_RIGHT),
                    new AdjacentInfo(right, SIDE_UP_LEFT, SIDE_LEFT),
                    new AdjacentInfo(right.index(SIDE_UP), SIDE_LEFT, SIDE_DOWN_LEFT)
            };
        }
        if(i == SIDE_RIGHT && j == SIDE_DOWN_RIGHT){
            Coordinate right = index(SIDE_RIGHT, 2);
            return right.outOfBounds(map) ? new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_DOWN), SIDE_UP_RIGHT, SIDE_RIGHT)
            } : new AdjacentInfo[]{
                    new AdjacentInfo(index(SIDE_DOWN), SIDE_UP_RIGHT, SIDE_RIGHT),
                    new AdjacentInfo(right, SIDE_LEFT, SIDE_DOWN_LEFT),
                    new AdjacentInfo(right.index(SIDE_DOWN), SIDE_UP_LEFT, SIDE_LEFT)
            };
        }
        throw new AssertionError("Attempt to pass non-adjacent indices");
    }

    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }

    public static class AdjacentInfo{
        public final Coordinate coord;
        public final int i2;
        public final int j2;

        public AdjacentInfo(Coordinate coord, int i2, int j2){
            this.coord = coord;
            this.i2 = i2;
            this.j2 = j2;
        }

        @Override
        public String toString(){
            return "coord=" + coord + ",i2=" + i2 + ",j2=" + j2;
        }
    }
}
