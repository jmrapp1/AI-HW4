import com.sun.org.apache.bcel.internal.generic.POP;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import java.util.BitSet;

public class Main {

    private static final double A = 0.43;
    private static final double B = 1 - A;
    private static final double CROSSOVER_RATE = 0.75;
    private static final double MUTATOR_RATE = 0.001;

    private static final int CHROMOSOME_LENGTH = 71;
    private static final int EVOLUTIONS = 100;
    private static final int POPULATION = 50;

    private static int evolutionCounter = 0;
    private static double[] evolutionBestValues = new double[100];

    private static int eval(Genotype<BitGene> gt) {
        return eval(gt.getChromosome().as(BitChromosome.class));
    }

    private static int eval(Chromosome<BitGene> chromosome) {
        BitChromosome bitChromosome = chromosome.as(BitChromosome.class);
        long x1 = getX1(bitChromosome);
        long x2 = getX2(bitChromosome);
        long x3 = getX3(bitChromosome);
        long x4 = getX4(bitChromosome);

        long func1 = getfunc1(x1, x2, x3, x4);
        double func2 = getfunc2(x1, x2, x3, x4);

        double solution = (A * func1 + B * func2);
        solution /= 10 * 10 * 10 * 10; // Passing INT.MAX_VALUE so divide by 10 a few times to bring it down
        return (int) solution;
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<BitGene>> gtf =
                Genotype.of(BitChromosome.of(CHROMOSOME_LENGTH, 0.5));

        // 3.) Create the execution environment.
        Engine<BitGene, Integer> engine = Engine
                .builder(Main::eval, gtf)
                .populationSize(POPULATION)
                .alterers(
                        new SinglePointCrossover<>(CROSSOVER_RATE),
                        new Mutator<>(MUTATOR_RATE)
                )
                .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<BitGene> result = engine.stream()
                .peek(Main::handleEvolution)
                .limit(EVOLUTIONS)
                .collect(EvolutionResult.toBestGenotype());

        new Plot(EVOLUTIONS, evolutionBestValues);
    }

    private static void handleEvolution(EvolutionResult<BitGene, Integer> result) {
        int best = result.getBestFitness();
        evolutionBestValues[evolutionCounter] = best;
        evolutionCounter++;
    }

    /**
     * Gets the X1 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X1
     */
    public static long getX1(BitChromosome chromosome) {
        return getXValue(chromosome, 0, 20);
    }

    /**
     * Gets the X2 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X2
     */
    public static long getX2(BitChromosome chromosome) {
        return getXValue(chromosome, 20, 39);
    }

    /**
     * Gets the X3 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X3
     */
    public static long getX3(BitChromosome chromosome) {
        return getXValue(chromosome, 39, 55);
    }

    /**
     * Gets the X4 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X4
     */
    public static long getX4(BitChromosome chromosome) {
        return getXValue(chromosome, 55, 71);
    }

    /**
     * Get the value of some set of bits
     *
     * @param chromosome The chromosome
     * @param startIndex The starting bit index
     * @param endIndex   The ending bit index
     * @return The value of the set of bits
     */
    public static long getXValue(BitChromosome chromosome, int startIndex, int endIndex) {
        BitSet bitSet = chromosome.toBitSet().get(startIndex, endIndex);
        return bitSet.length() > 0 ? bitSet.toLongArray()[0] : 0;
    }

    public static long getfunc1(long x1, long x2, long x3, long x4) {
        return 2 * x2 * x4 + x3 * (x1 - (2 * x4));
    }

    public static double getfunc2(long x1, long x2, long x3, long x4) {
        return 60000 / (x3 * Math.pow((double) x1 - (2 * x4), 3) + (2 * x2 * x4 * (4 * Math.pow((double) x4, 2) + 3 * x1 * (x1 - (2 * x4)))));
    }
}
