package io.github.chankyin.simplelandmine.mine;

import java.io.IOException;
import java.io.Writer;

public enum SolveStrategy{
    AUTO(true){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("Automatic expansion of 0-tile " + step.getRef());
        }
    },
    SAFETY(true){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("Safe expansion of tile " + step.getRef() + " because all surrounding mines are flagged");
        }
    }, // Count(surrounding flags) = this.number, can open all surrounding
    REMAINING_FLAGS(false){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("All unopened tiles surrounding " + step.getRef() + " must have mines");
        }
    }, // Count(unopened tiles) + Count(surrounding flags) = this.number, can flag all surrounding
    DEDUCED_SAFETY(true){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("A pair of empty tiles near " + ((DeducedSolveStep) step).getDeductionRef() + " are complementary, hence all other unopened tiles surrounding " + step.getRef() + " must be safe");
        }
    }, // n of the surrounding unopened tiles must have exactly m mines, m + Count(surrounding flags) = this.number, can open others
    DEDUCED_REMAINING_FLAGS(false){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("A pair of empty tiles near " + ((DeducedSolveStep) step).getDeductionRef() + " are complementary, hence all other unopened tiles surrounding " + step.getRef() + " must have mines");
        }
    }, // n of the surrounding unopened tiles must have exactly m mines, m + Count(other unopened tiles) = this.number, can open other unopened tiles
    GLOBAL_SAFETY(true){
        @Override
        public void explain(Writer writer, SolveStep step) throws IOException{
            writer.write("This is the only combination of mines according to the number of mines displayed!");
        }
    }; // number of remaining mines is shown as 0, can open all unopened tiles

    private final boolean isOpen;

    SolveStrategy(boolean isOpen){
        this.isOpen = isOpen;
    }

    public boolean isOpen(){
        return isOpen;
    }

    public abstract void explain(Writer writer, SolveStep step) throws IOException;
}
