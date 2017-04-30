package io.github.chankyin.simplelandmine.mine;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static io.github.chankyin.simplelandmine.mine.TileState.*;

public class MineSweeper{
    private final MineMap map;

    private List<SolveStep> steps;

    private int changes;

    public MineSweeper(MineMap map){
        this.map = map;
        steps = new ArrayList<>(map.getWidth() * map.getHeight());
    }

    public int operate(){
        int total = 0;
        try{
            while(true){
                int mines = scanMap();
                total += mines;
                if(mines == 0){
                    return total;
                }
            }
        }catch(RuntimeException | Error t){
            try{
                map.debugPrint(System.err);
            }catch(IOException e){
                e.printStackTrace();
            }
            throw t;
        }
    }

    private int scanMap(){
        return scanMapFrom(0, 0);
    }

    private int scanMapFrom(int x, int y){
        changes = 0;
        for(; x < map.getWidth(); x++, y = 0){
            for(; y < map.getHeight(); y++){
                operateTile(new Coordinate(x, y));
            }
        }
        int finalChanges = changes;
        changes = 0;
        return finalChanges;
    }

    private void operateTile(Coordinate coord){
        int x = coord.x, y = coord.y;

        if(map.getState(coord) == OPENED){
            int mines = map.countSurroundingMines(x, y);
            int empty = 0;
            int flags = 0;
            int opened = 0;
            for(int i = 0; i < 9; i++){
                Coordinate current = coord.index(i);
                if(i == 4 || current.outOfBounds(map.getWidth(), map.getHeight())){
                    continue;
                }
                TileState state = map.getState(current);
                if(state == EMPTY){
                    empty++;
                }else if(state == FLAGGED){
                    flags++;
                }else if(state == OPENED){
                    opened++;
                }else{
                    throw new AssertionError(state.name());
                }
            }

            try{
                if(mines == 0 && empty > 0){
                    tryAutoAroundOpened(coord);
                }else if(mines == flags && empty > 0){
                    trySafetyAroundOpened(coord);
                }else if(empty + flags == mines){
                    tryRemFlagAroundOpened(coord);
                }else{
                    tryDeductionForOpened(coord);
                }
            }catch(Explosion explosion){
                throw new AssertionError("Explosion at " + explosion.getPosition().x + "," + explosion.getPosition().y + " by safety at " + coord.x + "," + coord.y);
            }
        }
        // TODO other strategies
    }

    private void tryAutoAroundOpened(Coordinate coord) throws Explosion{
        for(int i = 0; i < 9; i++){
            Coordinate subject = coord.index(i);
            if(i == 4 || subject.outOfBounds(map)){
                continue;
            }
            TileState state = map.getState(subject);
            if(state == EMPTY){
                try{
                    map.doOpen(subject);
                }catch(Explosion explosion){
                    throw new AssertionError(explosion);
                }
                steps.add(new SolveStep(subject, SolveStrategy.AUTO, coord));
                changes++;
                operateTile(subject);
            }
        }
    }

    private void trySafetyAroundOpened(Coordinate coord) throws Explosion{
        for(int i = 0; i < 9; i++){
            Coordinate subject = coord.index(i);
            if(i == 4 || subject.outOfBounds(map)){
                continue;
            }
            TileState state = map.getState(subject);
            if(state == EMPTY){
                map.doOpen(subject);
                steps.add(new SolveStep(subject, SolveStrategy.SAFETY, coord));
                changes++;
            }
        }
    }

    private void tryRemFlagAroundOpened(Coordinate coord) throws Explosion{
        for(int i = 0; i < 9; i++){
            Coordinate subject = coord.index(i);
            if(i == 4 || subject.outOfBounds(map)){
                continue;
            }
            TileState state = map.getState(subject);
            if(state == EMPTY){
                if(!map.toggleFlag(subject)){
                    throw new AssertionError();
                }
                steps.add(new SolveStep(subject, SolveStrategy.REMAINING_FLAGS, coord));
                changes++;
            }
        }
    }

    private void tryDeductionForOpened(Coordinate coord) throws Explosion{
        int expectedMines = map.countSurroundingMines(coord);
        List<Integer> emptyIndices = new ArrayList<>(8);
        List<Integer> flaggedIndices = new ArrayList<>(8);
        for(int i = 0; i < 9; i++){
            Coordinate adj = coord.index(i);
            if(i == 4 || adj.outOfBounds(map)){
                continue;
            }
            TileState state = map.getState(adj);
            if(state == EMPTY){
                emptyIndices.add(i);
            }else if(state == FLAGGED){
                flaggedIndices.add(i);
            }
        }
        if(emptyIndices.size() == 2 && flaggedIndices.size() == expectedMines - 1){
            int i = emptyIndices.get(0), j = emptyIndices.get(1);
            if(Coordinate.areIndicesAdjacent(i, j)){
                for(Coordinate.AdjacentInfo info : coord.furtherAdjacentCoords(map, i, j)){
                    if(map.getState(info.coord) == OPENED){
                        tryDeductionPaired(coord, info);
                    }
                }
            }
        }
    }

    private void tryDeductionPaired(Coordinate coord, Coordinate.AdjacentInfo info) throws Explosion{
        int expectedMines = map.countSurroundingMines(info.coord);
        List<Integer> emptyIndices = new ArrayList<>(8);
        List<Integer> flaggedIndices = new ArrayList<>(8);
        for(int i = 0; i < 9; i++){
            Coordinate adj = info.coord.index(i);
            if(i == 4 || adj.outOfBounds(map)){
                continue;
            }
            TileState state = map.getState(adj);
            if(state == EMPTY){
                emptyIndices.add(i);
            }else if(state == FLAGGED){
                flaggedIndices.add(i);
            }
        }
        if(flaggedIndices.size() + 1 == expectedMines && emptyIndices.size() > 2){ // deduced safety
            for(Integer index : emptyIndices){
                int i = index;
                if(i != info.i2 && i != info.j2){
                    Coordinate subject = info.coord.index(i);
                    TileState state = map.getState(subject);
                    if(state == EMPTY){
                        map.doOpen(subject);
                        steps.add(new DeducedSolveStep(subject, SolveStrategy.DEDUCED_SAFETY, info.coord, coord));
                        changes++;
                    }
                }
            }
        }else if(flaggedIndices.size() + emptyIndices.size() - 1 == expectedMines){
            for(Integer index : emptyIndices){
                int i = index;
                if(i != info.i2 && i != info.j2){
                    Coordinate subject = info.coord.index(i);
                    TileState state = map.getState(subject);
                    if(state == EMPTY){
                        if(!map.toggleFlag(subject)){
                            throw new AssertionError();
                        }
                        steps.add(new DeducedSolveStep(subject, SolveStrategy.DEDUCED_REMAINING_FLAGS, info.coord, coord));
                        operateTile(coord);
                    }
                }
            }
        }
    }

    public void explain(Writer writer) throws IOException{
        int i = 0;
        writer.append("== START MINE SWEEPING EXPLAINATION ==\n");
        for(SolveStep step : steps){
            if(step.getStrategy() == SolveStrategy.AUTO){
                continue;
            }
            ++i;
            writer.append(Integer.toString(i)).append(". ");
            writer.append(step.getStrategy().isOpen() ? "Opened" : "Flagged").append(' ').append(step.getTarget().toString()).append(": ");
            step.getStrategy().explain(writer, step);
            writer.append('\n');
        }
        writer.append("== END MINE SWEEPING EXPLANATION ==\n");
    }
}
