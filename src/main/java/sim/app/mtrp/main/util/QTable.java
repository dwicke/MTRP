package sim.app.mtrp.main.util;

import ec.util.MersenneTwisterFast;

/**
 * Created by drew on 3/1/17.
 */
public class QTable implements java.io.Serializable {

    private static final long serialVersionUID = 1;

    private int numStates, numActions;
    private double qtable[][];
    private double V[];
    private double alpha, oneMinusAlpha;
    private double beta;
    public QTable(int numStates, int numActions, double learningRate, double discountBeta, MersenneTwisterFast rand) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = rand.nextDouble(false, true);
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }
    public QTable(int numStates, int numActions, double learningRate, double discountBeta) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = 1;
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }


    public QTable(int numStates, int numActions, double learningRate, double discountBeta, double initValue) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = initValue;
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }


    public QTable(int numStates, int numActions, double learningRate, double discountBeta, MersenneTwisterFast rand, double max, double min) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = rand.nextDouble(true, true) * (max - min) + min;
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }




    public void setAlpha(double alphaT) {
        alpha = alphaT;
        oneMinusAlpha = 1 - alpha;
    }

    public void updateQ(int state, int action, double reward, int nextState) {
        // if(reward ==1 )
        qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * ( reward + beta * V[nextState]);

        // printTable();
//else
        //   qtable[state][action] = .3 * qtable[state][action];

        // find max q-value for the state
        double max = qtable[state][action];
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i])
                max = qtable[state][i];
        }
        V[state] = max;
    }

    public void update(int state, int action, double reward) {
        qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * (double)reward;
        ///printTable();
    }
    public void lesserUpdate(int state, int action, double reward) {
        double tempAlpha = alpha;
        qtable[state][action] = (1-tempAlpha) * qtable[state][action] + tempAlpha * (double)reward;
        //printTable();
    }
    public void meanUpdate(double gamma) {

        // average q-table

        double sum = 0.0;
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                sum += qtable[i][j];
        }

        // muliply the average by gamma
        double avg = (sum / (double)(qtable.length * qtable[0].length)) * gamma;

        // Q[i][j] = Q[i][j]*(1-gamma) + gamma*avg
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                qtable[i][j] = qtable[i][j] * (1 - gamma) + avg;
        }

    }
    public void oneUpdate(double gamma) {

        // average q-table

       /* double sum = 0.0;
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                sum += qtable[i][j];
        }

        // muliply the average by gamma
        double avg = (sum / (double)(qtable.length * qtable[0].length)) * gamma;*/

        // Q[i][j] = Q[i][j]*(1-gamma) + gamma*avg
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                qtable[i][j] = qtable[i][j] * (1 - gamma) + gamma;
        }

    }
    public void meanUpdateRow(double gamma) {

        // average q-table

        double avg[] = new double[qtable.length];
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                avg[i] += qtable[i][j];
            avg[i] =  (avg[i] / (double)(qtable.length * qtable[0].length)) * gamma;
        }

        // muliply the average by gamma


        // Q[i][j] = Q[i][j]*(1-gamma) + gamma*avg
        for (int i = 0; i < qtable.length; i++) {
            for (int j = 0; j < qtable[i].length; j++)
                qtable[i][j] = qtable[i][j] * (1 - gamma) + avg[i];
        }

    }
    public void meanUpdateColumn(double gamma) {

        // average q-table

        double avg[] = new double[qtable.length];
        for (int i = 0; i < qtable[0].length; i++) {
            for (int j = 0; j < qtable.length; j++)
                avg[i] += qtable[j][i];
            avg[i] =  (avg[i] / (double)(qtable.length * qtable[0].length)) * gamma;
        }

        // muliply the average by gamma


        // Q[i][j] = Q[i][j]*(1-gamma) + gamma*avg
        for (int i = 0; i < qtable[0].length; i++) {
            for (int j = 0; j < qtable.length; j++)
                qtable[j][i] = qtable[j][i] * (1 - gamma) + avg[i];
        }

    }


    public void update(int state, int action, double reward, int nextState) {

        //    System.err.println("BEFORE reward: " + reward + " qvalue: " + qtable[state][action]);
        //    System.err.println("alpha: " + alpha + " one minus alpha: " + oneMinusAlpha);

        //   for (int i = 0; i < numActions; i++) {
        //      if(qtable[state][i]>.93)
        //       qtable[state][i] *= 1-(1/numActions);
        //   }
        //if(reward ==1 )
        qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * ( (double)reward /*+ beta * V[nextState]*/);
        // printTable();


// else
        //  qtable[state][action] = alpha * qtable[state][action];
        //   System.err.println("AFTER reward: " + reward + " qvalue: " + qtable[state][action]);
        //  printTable();
        // find max q-value for the state
     /*   double max = qtable[state][action];
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i])
                max = qtable[state][i];
        }
        V[state] = max;*/
    }



    public double getQValue(int state, int action) {
        // System.err.println("Q_" + state + " = " + qtable[state][action]);
        return qtable[state][action];
    }

    public void setQValue(int state, int action, double value) {
        qtable[state][action] = value;
    }

    public int getBestAction(int state) {
        double max = qtable[state][0];
        int best = 0;
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i]) {
                max = qtable[state][i];
                best = i;
            }
        }
        return best;
    }
    public double getNormalQValue(int state, int action) {
        //return qtable[state][action];
        double sum = 0;
        for (int i = 0; i < qtable.length; i++) {
            sum+=qtable[i][action];
            System.err.println("Q_" + i + " = " + qtable[i][action]);

        }
        if(sum==0)
            return 1;
        if(qtable[state][action]/sum <0){
            System.err.println("you fail");
            System.exit(0);
        }
        return qtable[state][action]/sum;
    }

    public void printTable() {

        for (int i = 0; i < qtable.length; i++) {
            StringBuilder build = new StringBuilder();
            build.append("state ").append(i).append(" vals: ");
            for (int j = 0; j < qtable[i].length; j++) {
                build.append(qtable[i][j]).append(" ");
            }
            System.err.println(build.toString());
        }
    }

    public String getQTableAsString() {
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < qtable.length; i++) {

            build.append("state ").append(i).append(" vals: ");
            for (int j = 0; j < qtable[i].length; j++) {
                build.append(qtable[i][j]).append(" ");
            }
            build.append("\n");
        }
        return build.toString();
    }
}

