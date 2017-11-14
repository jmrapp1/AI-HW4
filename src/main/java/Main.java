import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import jahuwaldt.plot.*;

import java.math.BigInteger;
import java.util.BitSet;

public class Main {

    private static int eval(Genotype<BitGene> gt) {
        return gt.getChromosome()
                .as(BitChromosome.class)
                .bitCount();
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<BitGene>> gtf =
                Genotype.of(BitChromosome.of(71, 0.001));

        // 3.) Create the execution environment.
        Engine<BitGene, Integer> engine = Engine
                .builder(Main::eval, gtf)
                .populationSize(50)
                .alterers(
                    // new Crossover<>(0.1),
                    new Mutator<>(0.001)
                )
                .build();

        // System.out.println(gtf);
        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<BitGene> result = engine.stream()
                .limit(100)
                .collect(EvolutionResult.toBestGenotype());
        System.out.println(result);
        System.out.println(getXValue(result.getChromosome().as(BitChromosome.class), 0, 20));

    }

    public static long getXValue(BitChromosome chromosome, int startIndex, int endIndex) {
        BitSet value = chromosome.toBitSet().get(startIndex, endIndex);
        return value.toLongArray()[0];
    }

}
