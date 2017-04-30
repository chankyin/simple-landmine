package io.github.chankyin.simplelandmine.mine;

import java.util.Random;

public class MineSecretFactory{
    private int width;
    private int height;
    private float probability;

    public int getWidth(){
        return width;
    }

    public MineSecretFactory setWidth(int width){
        this.width = width;
        return this;
    }

    public int getHeight(){
        return height;
    }

    public MineSecretFactory setHeight(int height){
        this.height = height;
        return this;
    }

    public float getProbability(){
        return probability;
    }

    public MineSecretFactory setProbability(float probability){
        this.probability = probability;
        return this;
    }

    public boolean[][] generate(Random random){
        boolean[][] secret = new boolean[width][];
        for(int x = 0; x < width; x++){
            secret[x] = new boolean[height];
            for(int y = 0; y < height; y++){
                secret[x][y] = random.nextFloat() < probability;
            }
        }
        for(int x = 0; x < width; x++){
            tile_loop:
            for(int y = 0; y < height; y++){
                boolean hasEmpty = false;
                int[] around = MineMap.around(secret, x, y); // TODO bigger search range for sealed corners
                for(int i : around){
                    if(i == 0){
                        continue tile_loop;
                    }
                }

                secret[x][y] = false;
            }
        }
        return secret;
    }
}
