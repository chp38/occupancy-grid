package AI;

import robot.*;
/**
 * Interface for A* search.
 * Created by charles on 21/03/17.
 */
public interface IAStar {

    public void start(Robot r);

    public void finalise();

    public void playSolution();

    public int calculateCost(Grid grid);
}
