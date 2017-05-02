package sim.app.mtrp.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.commons.math3.stat.inference.TTest;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * originally had this so it would write out to file at the end via the scheduler.
 * found it much more reliable to call on finish.
 */
public class StatsPublisher implements Steppable {
    MTRP board = null;
    Bag bagOfTotal = new Bag();

    String directoryName;
    private long maxNumSteps;


    private static int numWritten = 0;
    private static double[][] bountyStats = new double[(int)SimState.totalNumJobs][];
    private static double[][] timeStats = new double[(int)SimState.totalNumJobs][];
    private static long[] seeds = new long[(int)SimState.totalNumJobs];

    private static Object mutex = new Object();

    public StatsPublisher(MTRP a, long maxNumSteps, String dir) {
        this.board = a;
        this.maxNumSteps = maxNumSteps;
        directoryName = dir;
        bountyStats[(int)a.job()] = new double[(int)maxNumSteps];
        timeStats[(int)a.job()] = new double[(int)maxNumSteps];
    }

    public void finish() {
        synchronized (mutex) {
            numWritten++;
            if (numWritten == SimState.totalNumJobs) {
                // then I'll be the one to take care of the writting out to the file

                File file = null;
                if (board.isShouldDie()) {
                    file = new File(directoryName + "/" + board + "_" + board.getAgentType()  + ".death.bounties");
                } else if (board.isHasUnexpectedlyHardJobs()) {
                    file = new File(directoryName + "/" + board + "_" + board.getAgentType() + ".hardjob.bounties");
                } else if (board.isHasEmergentJob()) {
                    file = new File(directoryName + "/" + board + "_" + board.getAgentType() + ".emergentjob.bounties");
                } else {
                    file = new File(directoryName + "/" + board + "_" + board.getAgentType() + ".regular.bounties");
                }
                file.getParentFile().mkdirs();
                try {
                    PrintWriter writer = new PrintWriter(file, "UTF-8");

                    for (int job = 0; job < bountyStats.length; job++) {
                        writer.println(bountyStats[job][((int)maxNumSteps - 1)] + " " + timeStats[job][((int)maxNumSteps - 1)]  + " " + seeds[job]);
                    }

                    /*
                    for (int i = ((int)maxNumSteps - 1); i < maxNumSteps; i++) {
                        double sum = 0.0;
                        for (int j = 0; j < stats.length; j++) {
                            sum += stats[j][i];
                            writer.println(stats[j][i] + " " + board.seed());
                        }
                        //writer.println("");

                        double average = sum / (double) stats.length;
                        double sd = 0;
                        for (int k = 0; k < stats.length; k++)
                        {
                            sd += Math.pow(stats[k][i] - average, 2) / stats.length;
                        }
                        double standardDeviation = Math.sqrt(sd);
                        writer.println(average + " " + standardDeviation);

                    }
                    */
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }

            }
        }
    }

    public void step(SimState state) {

        if (state.schedule.getSteps() < maxNumSteps) {
            if (bountyStats[(int)board.job()] == null) {
                System.out.println("OHHH Nooo " + (int)board.job());
                bountyStats[(int)board.job()] = new double[(int)maxNumSteps];

            }
            if (timeStats[(int)board.job()] == null) {
                System.out.println("OHHH Nooo " + (int)board.job());
                timeStats[(int)board.job()] = new double[(int)maxNumSteps];

            }
            timeStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getTotalTime();//board.getTotalOutstandingBounty();
            bountyStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getTotalOutstandingBounty();

            seeds[(int)board.job()] = board.seed();
        }

    }



}
