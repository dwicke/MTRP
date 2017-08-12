package sim.app.mtrp.main.agents.comparisonagents;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivities;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.learningagents.LearningAgent;
import sim.util.Bag;
import sim.util.Double2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by drew on 8/10/17.
 * going to go based on
 * https://github.com/graphhopper/jsprit/blob/master/jsprit-examples/src/main/java/com/graphhopper/jsprit/examples/SimpleExampleOpenRoutes.java
 */
public class TSPSolver extends LearningAgent {

    TourActivities activities;

    public TSPSolver(MTRP state, int id) {
        super(state, id);
        activities = new TourActivities();
    }


    @Override
    public Task getBestTask(Bag bagOfTasks) {

        // make new bag with tasks in my area



        if (activities.isEmpty()) {
            Bag myTasks = new Bag();
            for (int i = 0; i < bagOfTasks.size(); i++) {
                Task t = (Task) bagOfTasks.get(i);

                double cellHalf =  (state.getTaskLocLength() / Math.sqrt(state.numAgents)) / 2.0;
                if (Math.abs(startDepo.getLocation().getX() - t.getLocation().getX() ) <= cellHalf && Math.abs(startDepo.getLocation().getY() - t.getLocation().getY() ) <= cellHalf)
                {
                    myTasks.add(t);
                }
            }
            if (!myTasks.isEmpty() ) {
                //state.printlnSynchronized("the size of myTasks = " + myTasks.size());
                solveTSP(myTasks);
            } else {
                return null;
            }
        }

        return getNextTask();

    }

    public void solveTSP(Bag bagOfTasks) {
        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
		 */

        VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, Integer.MAX_VALUE).build();


		/*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
		 */
        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(this.curLocation.getX(), this.curLocation.getY()));
        vehicleBuilder.setType(vehicleType);
        vehicleBuilder.setReturnToDepot(false);

        VehicleImpl vehicle = vehicleBuilder.build();

		/*
         * build services at the required locations, each with a capacity-demand of 1.
		 */
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
		for (int i = 0; i < bagOfTasks.size(); i++) {
		    Task t = (Task) bagOfTasks.get(i);
            Service service1 = Service.Builder.newInstance(t.getId() + "").setLocation(Location.Builder.newInstance().setCoordinate(new Coordinate(t.getLocation().getX(), t.getLocation().getY())).setId(t.getId() + "").build()).build();
            vrpBuilder.addJob(service1);
		}

        VehicleRoutingProblem problem = vrpBuilder.build();


		/*
         * get the algorithm out-of-the-box.
		 */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

		/*
         * and search a solution
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

		/*
         * get the best
		 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
        ArrayList<VehicleRoute> a = new ArrayList<VehicleRoute>(bestSolution.getRoutes());
        activities = a.get(0).getTourActivities();

    }

    public Task getNextTask() {

        TourActivity a = activities.getActivities().get(0);
        Double2D loc = new Double2D(a.getLocation().getCoordinate().getX(), a.getLocation().getCoordinate().getY());
        Bag obj = state.taskPlane.getObjectsAtLocation(loc);
        if (obj.size() == 0) {
            state.printlnSynchronized("OH NO!! no task at location " + loc.toCoordinates() + "  with id " + a.getName());
        }else {
            for (int i = 0; i < obj.size(); i++) {
                Task t = (Task) obj.get(i);
                //state.printlnSynchronized("Task t = " + Integer.parseInt(a.getLocation().getId()));
                if (t.getId() == Integer.parseInt(a.getLocation().getId())) {
                    // then we've got it!
                    activities.removeActivity(a);
                    return t;
                }
            }
        }
        state.printlnSynchronized("yikes we have a problem in the tsp solver no activities i guess??");
        return null;// we have a problem

    }



}
