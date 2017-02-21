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

/**
 * originally had this so it would write out to file at the end via the scheduler.
 * found it much more reliable to call on finish.
 */
public class StatsPublisher implements Steppable {
    MTRP board = null;
    Bag bagOfTotal = new Bag();

    String directoryName;
    private long maxNumSteps;

    public StatsPublisher(MTRP a, long maxNumSteps, String dir) {
        this.board = a;
        this.maxNumSteps = maxNumSteps;
        directoryName = dir;
    }


    public void step(SimState state) {

            try {
                File file = new File(directoryName + "/" + "maxTicks" + state.seed() + ".bounties");
                file.getParentFile().mkdirs();
                PrintWriter writer = new PrintWriter(file, "UTF-8");

                writer.print("Job = " + board.job() + " seed = " + board.seed() + " numAgents = " + board.numAgents);

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }

        /*bagOfTotal.add(board.getStats());


        if(state.schedule.getSteps() >= maxNumSteps-2){
            try{
                File file = new File(directoryName + "/" + "maxTicks" + state.seed() + ".bounties");
                file.getParentFile().mkdirs();
                PrintWriter writer = new PrintWriter(file, "UTF-8");

                for(int i = 0; i<bagOfTotal.numObjs; i++){
                    writer.print(((Double)bagOfTotal.objs[i]) + ",");
                }

                writer.close();
            }catch(Exception e){e.printStackTrace(); System.exit(0);}
        }*/
        }

    }
