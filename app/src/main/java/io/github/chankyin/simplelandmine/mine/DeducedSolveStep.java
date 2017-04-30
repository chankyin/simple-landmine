package io.github.chankyin.simplelandmine.mine;

public class DeducedSolveStep extends SolveStep{
    private final Coordinate deductionRef;

    public DeducedSolveStep(Coordinate target, SolveStrategy strategy, Coordinate ref, Coordinate deductionRef){
        super(target, strategy, ref);
        this.deductionRef = deductionRef;
    }

    public Coordinate getDeductionRef(){
        return deductionRef;
    }
}
