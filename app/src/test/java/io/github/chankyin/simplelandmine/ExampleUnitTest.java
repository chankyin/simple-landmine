package io.github.chankyin.simplelandmine;

import io.github.chankyin.simplelandmine.mine.MineMap;
import io.github.chankyin.simplelandmine.mine.MineSecretFactory;
import io.github.chankyin.simplelandmine.mine.MineSweeper;
import org.junit.Test;

import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest{
    private MineMap map;
    private Random random;

    @Test
    public void testGenerate() throws Exception{
        random = new Random(0);
        System.out.println("== TEST GENERATE ==");
        long start;
        start = System.nanoTime();
        map = new MineMap(new MineSecretFactory().setProbability(0.2f).setWidth(128).setHeight(45).generate(random));
        map.debugPrint(System.out);
        System.out.println("Spent " + (System.nanoTime() - start) / 1000000 + " ms");
        start = System.nanoTime();
        testInit();
        System.out.println("Spent " + (System.nanoTime() - start) / 1000000 + " ms");
        start = System.nanoTime();
        testSolve();
        System.out.println("Spent " + (System.nanoTime() - start) / 1000000 + " ms");
    }

    public void testInit() throws Exception{
        System.out.println("== TEST INIT ==");
        map.init(random);
        map.debugPrint(System.out);
    }

    public void testSolve() throws Exception{
        System.out.println("== TEST SOLVE ==");
        MineSweeper sweeper = new MineSweeper(map);
        sweeper.operate();
        map.debugPrint(System.out);
        sweeper.explain(new OutputStreamWriter(System.out));
    }
}
