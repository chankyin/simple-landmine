package io.github.chankyin.simplelandmine.mine;

public class SolveStep{
    private final Coordinate target;
    private final SolveStrategy strategy;
    private final Coordinate ref;

    public SolveStep(Coordinate target, SolveStrategy strategy, Coordinate ref){
        this.strategy = strategy;
        this.ref = ref;
        this.target = target;
    }

    public SolveStrategy getStrategy(){
        return strategy;
    }

    public Coordinate getRef(){
        return ref;
    }

    public Coordinate getTarget(){
        return target;
    }
}
