/* ******************************************************************
************
Author’s name(s): Emily DeMarco, Nick Boyd, Jon Rapp
Course Title: Artificial Intelligence
Semester: Fall 2017
Assignment Number: HW 4
Submission Date: 11/27/17
Purpose: This program uses a Java GA Library to perform enginerring design optimization for an I-beam.
Input: none
Output: The 100 generation's bests & 4 graphs.
Help: Acknowledge any help you might have received or simply indicate that you
worked alone.
*********************************************************************
********* */
import com.sun.org.apache.bcel.internal.generic.POP;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

import java.util.BitSet;

public class Main {

    private static final double A = 0; // The A weight for func 1
    private static final double B = 1; // The B weight for func 2
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

    //******************************************************
//*** Purpose: This method is used to evaluate a chromosome.
//*** Input: a Genotype
//*** Output: an integer
//******************************************************
    private static int eval(Genotype<BitGene> gt) {
        return eval(gt.getChromosome().as(BitChromosome.class));
    }
    //******************************************************
//*** Purpose: This method is used to evaluate a chromosome.
//*** Input: a chromosome
//*** Output: an integer
//******************************************************
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
        double solution = (A * func1 + B * func2) *1000; //multiple by 1000 because we cast the double to an int
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

        new Plot(EVOLUTIONS, evolutionBestValuesF1, "Cross Section Area");
        new Plot(EVOLUTIONS, evolutionBestValuesF2, "Static Deflection");
        new Plot(EVOLUTIONS, evolutionAverageValuesF1, "Cross Section Area");
        new Plot(EVOLUTIONS, evolutionAverageValuesF2, "Static Deflection");
    }
    //******************************************************
//*** Purpose: This method is used to run the evolution and find the fitness values.
//*** Input: an evolutionResult
//*** Output: none.
//******************************************************
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


//*** Purpose: This method is used to get the x1 value from the chromosome.
//*** Input: chromosome
//*** Output: double containing the value of the bits of X1
//******************************************************
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


    //******************************************************
//*** Purpose: This method gets the X3 value from the chromosome.
//*** Input: chromosome
//*** Output: a double containing the value of the bits of x3
//******************************************************
    public static double getX3(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 39, 55);
        return calculateXValue(chromValue, 16, 0.9, 5.0);
    }


//*** Purpose: This method gets the X4 value from the chromosome.
//*** Input: chromosome
//*** Output: a double containing the value of the bits of x4
//******************************************************
    public static double getX4(BitChromosome chromosome) {
        long chromValue = getChromosomeValue(chromosome, 55, 71);
        return calculateXValue(chromValue, 16, 0.9, 5.0);
    }


    //******************************************************
//*** Purpose: This method gets the value of some set of bits.
//*** Input: chromosome, startindex integer, endindex intger
//*** Output: the value of the set of bits
//******************************************************
    public static long getChromosomeValue(BitChromosome chromosome, int startIndex, int endIndex) {
        BitSet bitSet = chromosome.toBitSet().get(startIndex, endIndex);
        return bitSet.length() > 0 ? bitSet.toLongArray()[0] : 0;
    }


    //******************************************************
//*** Purpose: This method gets the value for some x variable and clamp between min and max.
//*** Input: long chromvalue, int chrombinlength which contains the binary length of the variable
    //*** double min and double max
//*** Output: a double containing the value
//******************************************************
    private static double calculateXValue(long chromValue, int chromBinLength, double min, double max) {
        return min + (chromValue * ((max - min) / (Math.pow(2, chromBinLength) - 1)));
    }

    //******************************************************
//*** Purpose: This method gets the fitness value function 1.
//*** Input: the 4 double x values
//*** Output: a double containing the value of function 1 fitness
//******************************************************
    public static double getfunc1(double x1, double x2, double x3, double x4) {
        return (2 * x2 * x4) + (x3 * (x1 - (2 * x4)));
    }


    //******************************************************
//*** Purpose: This method gets the fitness value function 2.
//*** Input: the 4 double x values
//*** Output: a double containing the value of function 2 fitness
//******************************************************
    public static double getfunc2(double x1, double x2, double x3, double x4) {
        return (60000 / ((x3 * Math.pow(x1 - (2 * x4), 3)) + (2 * x2 * x4 * (4 * Math.pow(x4, 2) + (3 * x1 * (x1 - (2 * x4)))))));

    }

}