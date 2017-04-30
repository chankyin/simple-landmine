package io.github.chankyin.simplelandmine.mine;

public class Explosion extends Exception{
    private final Coordinate position;

    public Explosion(Coordinate position){
        this.position = position;
    }

    public Coordinate getPosition(){
        return position;
    }
}
