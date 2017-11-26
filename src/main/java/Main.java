import com.sun.org.apache.bcel.internal.generic.POP;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import java.util.BitSet;

public class Main {

    private static final double A = 0.43; // The A weight for func 1
    private static final double B = 1 - A; // The B weight for func 2
    private static final double CROSSOVER_RATE = 0.75; // Crossover weight
    private static final double MUTATOR_RATE = 0.001; // Mutator weight

    private static final int CHROMOSOME_LENGTH = 71; // Total length of chromosome to store vars
    private static final int EVOLUTIONS = 100; // The evolution number
    private static final int POPULATION = 50; // The population

    private static int evolutionCounter = 0;
    private static double[] evolutionBestValuesF1 = new double[100];
    private static double[] evolutionBestValuesF2 = new double[100];
    private static double[] evolutionAverageValuesF1 = new double[100];
    private static double[] evolutionAverageValuesF2 = new double[100];
    private static double averageF1 = 0;
    private static double averageF2 = 0;

    private static int eval(Genotype<BitGene> gt) {
        return eval(gt.getChromosome().as(BitChromosome.class));
    }

    private static int eval(Chromosome<BitGene> chromosome) {
        BitChromosome bitChromosome = chromosome.as(BitChromosome.class);
        // Get individual x values from chromosome
        double x1 = getX1(bitChromosome);
        double x2 = getX2(bitChromosome);
        double x3 = getX3(bitChromosome);
        double x4 = getX4(bitChromosome);

        // Get function values
        double func1 = getfunc1(x1, x2, x3, x4);
        double func2 = getfunc2(x1, x2, x3, x4);

        // Combine and add weight
        double solution = (A * func1 + B * func2);
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
                .populationSize(POPULATION) // Set population
                .minimizing() // Minimize
                .alterers(
                        new SinglePointCrossover<>(CROSSOVER_RATE), // Set crossover
                        new Mutator<>(MUTATOR_RATE) // Set mutator
                )
                .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        engine.stream()
                .peek(Main::handleEvolution)
                .limit(EVOLUTIONS)
                .collect(EvolutionResult.toBestGenotype());

        new Plot(EVOLUTIONS, evolutionBestValuesF1, "Best Func1");
        new Plot(EVOLUTIONS, evolutionBestValuesF2, "Best Func2");
        new Plot(EVOLUTIONS, evolutionAverageValuesF1, "Average Func1");
        new Plot(EVOLUTIONS, evolutionAverageValuesF2, "Average Func2");
    }

    private static void handleEvolution(EvolutionResult<BitGene, Integer> result) {
        BitChromosome bitChromosome = result.getBestPhenotype().getGenotype().getChromosome().as(BitChromosome.class);
        // Get individual x values from chromosome
        double x1 = getX1(bitChromosome);
        double x2 = getX2(bitChromosome);
        double x3 = getX3(bitChromosome);
        double x4 = getX4(bitChromosome);

        // Get function values
        double func1 = getfunc1(x1, x2, x3, x4);
        double func2 = getfunc2(x1, x2, x3, x4);

        // Combine and add weight
        double fitness = (A * func1 + B * func2);

        System.out.println("Generation " + evolutionCounter + "'s Best:");
        System.out.println("-------------------------------");
        System.out.println("X1: " + x1 + "\tX2: " + x2 + "\tX3: " + x3 + "\tX4: " + x4);
        System.out.println("Fitness: " + fitness + "\n\n");

        // Add to arrays for plot
        averageF1 += func1;
        averageF2 += func2;
        evolutionBestValuesF1[evolutionCounter] = func1;
        evolutionBestValuesF2[evolutionCounter] = func2;
        evolutionAverageValuesF1[evolutionCounter] = averageF1 / (evolutionCounter + 1);
        evolutionAverageValuesF2[evolutionCounter] = averageF2 / (evolutionCounter + 1);
        evolutionCounter++;
    }

    /**
     * Gets the X1 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X1
     */
    public static double getX1(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 0, 20);
        return calculateXValue(chromValue, 20, 10, 80);
    }

    /**
     * Gets the X2 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X2
     */
    public static double getX2(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 20, 39);
        return calculateXValue(chromValue, 19, 10, 50);
    }

    /**
     * Gets the X3 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X3
     */
    public static double getX3(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 39, 55);
        return calculateXValue(chromValue, 16, 0.9, 5.0);
    }

    /**
     * Gets the X4 value from the chromosome
     *
     * @param chromosome The chromosome
     * @return The value of the bits of X4
     */
    public static double getX4(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 55, 71);
        return calculateXValue(chromValue, 16, 0.9, 5.0);
    }

    /**
     * Get the value of some set of bits
     *
     * @param chromosome The chromosome
     * @param startIndex The starting bit index
     * @param endIndex   The ending bit index
     * @return The value of the set of bits
     */
    public static long getChromosomeValue(BitChromosome chromosome, int startIndex, int endIndex) {
        BitSet bitSet = chromosome.toBitSet().get(startIndex, endIndex);
        return bitSet.length() > 0 ? bitSet.toLongArray()[0] : 0;
    }

    /**
     * Calculate the value for some X variable and clamp between min and max
     *
     * @param chromValue The value from the chromosome
     * @param chromBinLength The chromosome binary length of the variable
     * @param min The min value
     * @param max The max value
     * @return The value
     */
    private static double calculateXValue(long chromValue, int chromBinLength, double min, double max) {
        return min + (chromValue * ((max - min) / (Math.pow(2, chromBinLength) - 1)));
    }

    /**
     * Calculate the fitness for function 1
     *
     * @param x1 The x1 value
     * @param x2 The x2 value
     * @param x3 The x3 value
     * @param x4 The x4 value
     * @return The fitness
     */
    public static double getfunc1(double x1, double x2, double x3, double x4) {
        return (2 * x2 * x4) + (x3 * (x1 - (2 * x4)));
    }

    /**
     * Calculate the fitness for function 2
     *
     * @param x1 The x1 value
     * @param x2 The x2 value
     * @param x3 The x3 value
     * @param x4 The x4 value
     * @return The fitness
     */
    public static double getfunc2(double x1, double x2, double x3, double x4) {
        return 60000 / ((x3 * Math.pow(x1 - (2 * x4), 3)) + (2 * x2 * x4 * (4 * Math.pow(x4, 2) + (3 * x1 * (x1 - (2 * x4))))));
    }

}