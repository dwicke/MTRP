package sim.app.mtrp.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
    private static double[][] stats = new double[(int)SimState.totalNumJobs][];
    private static Object mutex = new Object();

    public StatsPublisher(MTRP a, long maxNumSteps, String dir) {
        this.board = a;
        this.maxNumSteps = maxNumSteps;
        directoryName = dir;
        stats[(int)a.job()] = new double[(int)maxNumSteps];
    }

    public void finish() {
        synchronized (mutex) {
            numWritten++;
            if (numWritten == SimState.totalNumJobs) {
                // then I'll be the one to take care of the writting out to the file
                File file = new File(directoryName + "/" + System.currentTimeMillis() + "_" + board.getAgentType() + ".bounties");
                file.getParentFile().mkdirs();
                try {
                    PrintWriter writer = new PrintWriter(file, "UTF-8");
                    for (int i = 0; i < maxNumSteps; i++) {
                        double sum = 0.0;
                        for (int j = 0; j < stats.length; j++) {
                            sum += stats[j][i];
                            //writer.print(stats[j][i] + " ");
                        }
                        //writer.println("");
                        writer.println(sum/(double) stats.length);
                    }
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
            if (stats[(int)board.job()] == null) {
                System.out.println("OHHH Nooo " + (int)board.job());
                stats[(int)board.job()] = new double[(int)maxNumSteps];
            }
            stats[(int)board.job()][(int)state.schedule.getSteps()] = board.getTotalOutstandingBounty();
        }

    }



}
