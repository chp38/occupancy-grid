package AI;

import PriorityQueue.DEPQ;
import robot.Grid;
import robot.Robot;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Class to handle A* searching.
 * Created by charles on 22/03/17.
 */
public class AStar extends ISearcher{

    private int goalX, goalY;

    @Override
    public Node start(Robot r) {
        Node temp;

        this.goalY = r.getGoalY();
        this.goalX = r.getGoalX();
        this.board = r.getGrid();

        PriorityQueue frontier = new PriorityQueue();
        HashSet explored = new HashSet<String>();

        temp = new Node(r.getRobotX(), r.getRobotY());

        while((temp!=null)){
            if(isGoal(temp)){
                System.out.println("Goal Found!");
                System.out.println(temp.getDepth());
                //temp.print();
                break;
            }
            if(explored.contains(temp)){
                continue;
            }
            expandAll(temp, frontier, temp.getDepth()+1);
            explored.add(temp);
            temp = (Node) frontier.poll();
        }
        return temp;
    }

    public boolean isGoal(Node curr){
        if(curr.getXpos() == this.goalX && curr.getYpos() == this.goalY){
            return true;
        } else {
            return false;
        }
    }

    public void createNode(PriorityQueue open, Node parent, int depth, int x, int y){
        Node temp;
        temp = new Node(x, y, depth);
        temp.setParent(parent);

        int h = this.calculateCost(temp);

        //f(n) = g(n) + h(n)
        temp.setF(depth + h);
        open.add(temp);
    }

    public int calculateCost(Node grid) {
        int y = Math.abs(this.goalY - grid.getYpos());
        int x = Math.abs(this.goalX - grid.getXpos());

        return Math.abs(x + y);
    }
}
