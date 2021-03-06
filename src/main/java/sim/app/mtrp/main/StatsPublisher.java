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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private static double[][] fairStats = new double[(int)SimState.totalNumJobs][];
    private static double[][] varStats = new double[(int)SimState.totalNumJobs][];
    //private static double[][] areaStats = new double[(int)SimState.totalNumJobs][];

    private static long[] seeds = new long[(int)SimState.totalNumJobs];

    private static Object mutex = new Object();

    public StatsPublisher(MTRP a, long maxNumSteps, String dir) {
        this.board = a;
        this.maxNumSteps = maxNumSteps;
        directoryName = dir;
        bountyStats[(int)a.job()] = new double[(int)maxNumSteps];
        timeStats[(int)a.job()] = new double[(int)maxNumSteps];
        fairStats[(int)a.job()] = new double[(int)maxNumSteps];
        varStats[(int)a.job()] = new double[(int)maxNumSteps];
       // areaStats[(int)a.job()] = new double[(int)maxNumSteps];

    }

    public void finish() {
        synchronized (mutex) {
            numWritten++;
            if (numWritten == SimState.totalNumJobs) {
                // then I'll be the one to take care of the writting out to the file

                String filepath = null;
                if (board.isShouldDie()) {
                    filepath = directoryName + "/death/" + board.jobLength;
                } else if (board.isHasUnexpectedlyHardJobs()) {
                    filepath = directoryName + "/hardjobs/" + board.jobLength;
                } else if (board.isHasEmergentJob()) {
                    filepath = directoryName + "/emergentjobs/" + board.jobLength;
                } else if (board.isHasSuddenTaskIncrease()) {
                    filepath = directoryName + "/suddentasks/" + board.jobLength;
                } else {
                    filepath = directoryName + "/regular/" + board.jobLength;
                }

                String withRate = (board.hasBountyRate == true) ? "r" : "n";

                File file = new File(filepath + "/" + board.groupLabel+ "_" + board.increment + "_"  +  "BountyResults.txt");
                file.getParentFile().mkdirs();

                PrintWriter out = getWriter(file);

                if (out != null) {
                    for (int job = 0; job < bountyStats.length; job++) {
                        out.println(bountyStats[job][((int)maxNumSteps - 1)]  );
                    }
                    out.close();
                }


                file = new File(filepath + "/" + board.groupLabel + "_" + board.increment + "_"  + "allTimeResults.txt");
                file.getParentFile().mkdirs();

                out = getWriter(file);

                if (out != null) {
                    for (int job = 0; job < timeStats.length; job++) {
                        out.println(timeStats[job][((int)maxNumSteps - 1)] );
                    }
                    out.close();
                }

                file = new File(filepath + "/" + board.groupLabel + "_" + board.increment + "_" + "allFairResults.txt");
                file.getParentFile().mkdirs();

                out = getWriter(file);

                if (out != null) {
                    for (int job = 0; job < fairStats.length; job++) {
                        out.println(fairStats[job][((int)maxNumSteps - 1)] );
                    }
                    out.close();
                }
                file = new File(filepath + "/" + board.groupLabel + "_" + board.increment + "_" + "allVarResults.txt");
                file.getParentFile().mkdirs();

                out = getWriter(file);

                if (out != null) {
                    for (int job = 0; job < varStats.length; job++) {
                        out.println(varStats[job][((int)maxNumSteps - 1)] );
                    }
                    out.close();
                }

//                file = new File(filepath + "/" + board.agentType + "_" + board.increment + "_" + "allAreaResults.txt");
//                file.getParentFile().mkdirs();
//
//                out = getWriter(file);
//
//                if (out != null) {
//                    for (int job = 0; job < areaStats.length; job++) {
//                        for (int step = 0; step < areaStats[job].length; step++) {
//                            out.print(areaStats[job][step] + " " );
//                        }
//                        out.println();
//                    }
//                    out.close();
//                }


//                file = new File(filepath + "/graphBountyResults.txt");
//                file.getParentFile().mkdirs();
//
//                out = getWriter(file);
//
//                if (out != null) {
//                    for (int i = 0; i < maxNumSteps; i++) {
//                        double avg = 0.0;
//                        for (int job = 0; job < bountyStats.length; job++) {
//                            avg += bountyStats[job][i];
//                        }
//                        out.println(i + " " + (avg / (bountyStats.length))  + " " + board.groupLabel);
//                    }
//                    out.close();
//                }
//
//                file = new File(filepath + "/graphTimeResults.txt");
//                file.getParentFile().mkdirs();
//
//                out = getWriter(file);
//
//                if (out != null) {
//                    for (int i = 0; i < maxNumSteps; i++) {
//                        double avg = 0.0;
//                        for (int job = 0; job < timeStats.length; job++) {
//                            avg += timeStats[job][i];
//                        }
//                        out.println(i + " " + (avg / (timeStats.length))  + " " + board.groupLabel);
//                    }
//                    out.close();
//                }

            }
        }
    }

    public PrintWriter getWriter(File file) {
        PrintWriter out = null;
        if ( file.exists() && !file.isDirectory() ) {
            try {
                out = new PrintWriter(new FileOutputStream(file, true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else {
            try {
                out = new PrintWriter(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return out;
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
            if (fairStats[(int)board.job()] == null) {
                System.out.println("OHHH Nooo " + (int)board.job());
                fairStats[(int)board.job()] = new double[(int)maxNumSteps];
            }
            if (varStats[(int)board.job()] == null) {
                System.out.println("OHHH Nooo " + (int)board.job());
                varStats[(int)board.job()] = new double[(int)maxNumSteps];
            }
//            if (areaStats[(int)board.job()] == null) {
//                System.out.println("OHHH Nooo " + (int)board.job());
//                areaStats[(int)board.job()] = new double[(int)maxNumSteps];
//            }
            timeStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getTotalTime();//board.getTotalOutstandingBounty();
            bountyStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getTotalOutstandingBounty();
            fairStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getFairness();
            varStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getVarianceTime();
            //areaStats[(int)board.job()][(int)state.schedule.getSteps()] = board.getAreaStats();


            seeds[(int)board.job()] = board.seed();
        }

    }



}
