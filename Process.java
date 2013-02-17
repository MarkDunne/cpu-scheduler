/**
 * @author Mark Dunne (111379601)
 */

import java.util.Random;

public class Process {

    private Queue queue;
    private Random random;
    private long cpuTimeNeeded;
    private long blockingTimeNeeded;
    private boolean stateChanged;

    public Process() {
        random = new Random();
        cpuTimeNeeded = random.nextInt(10000);
        System.out.println(cpuTimeNeeded);
    }

    /**
     * Set the queue that the Process is current in
     *
     * @param queue The queue that it is in
     */
    public void setParentQueue(Queue queue) {
        this.queue = queue;
    }

    /**
     * Determine if the Process is finished execution
     *
     * @return True if it is finished execution
     */
    public boolean isFinished() {
        return cpuTimeNeeded == 0;
    }

    /**
     * Do some computation
     *
     * @param time The amount of time given for the computation
     */
    public void doCPUWork(long time) {
        stateChanged = false;
        if (blockingTimeNeeded == 0) {
            cpuTimeNeeded -= time;
            cpuTimeNeeded = cpuTimeNeeded > 0 ? cpuTimeNeeded : 0;

            if (!isFinished()) {
                //25% chance to enter blocked state
                if (Math.random() < 0.25) {
                    System.out.println("Process Blocked!");
                    blockingTimeNeeded = random.nextInt(1000);
                    //process entered blocked state
                    queue.event(this, Scheduler.Interrupt.PROCESS_BLOCKED);
                    stateChanged = true;
                }
            }
        }
    }

    /**
     * Simulate working through a blocked process
     *
     * @param time The amount of time given to work through the block
     */
    public void doBlockedWork(long time) {
        stateChanged = false;
        blockingTimeNeeded -= time;
        blockingTimeNeeded = blockingTimeNeeded > 0 ? blockingTimeNeeded : 0;
        //process entered running state
        if (blockingTimeNeeded == 0) {
            queue.event(this, Scheduler.Interrupt.PROCESS_READY);
            stateChanged = true;
        }
    }

    /**
     * Determines if the Process has changed queues recently
     *
     * @return True if it has changed queues recently
     */
    public boolean isStateChanged() {
        return stateChanged;
    }

    @Override
    public String toString() {
        return "[Proc " + hashCode() + "time: " + cpuTimeNeeded + ":" + blockingTimeNeeded + "]";
    }
}