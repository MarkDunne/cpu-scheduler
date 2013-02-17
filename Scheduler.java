/**
 * @author Mark Dunne (111379601)
 */

public class Scheduler {

    public static final int PRIORITY_LEVELS = 3;
    private final Queue blockedQueue;
    private final Queue[] runningQueues;
    private long clock;

    /**
     * The types of interrupt that can happen
     */
    public static enum Interrupt {
        PROCESS_BLOCKED,
        PROCESS_READY,
        LOWER_PRIORITY
    }

    public Scheduler() {
        //create new blocked queue
        blockedQueue = new Queue(this, 50, 0, Queue.QueueType.BLOCKED_QUEUE);

        //create the cpu queues
        runningQueues = new Queue[PRIORITY_LEVELS];
        for (int i = 0; i < PRIORITY_LEVELS; i++) {
            runningQueues[i] = new Queue(this, 10 + i * 20, i, Queue.QueueType.CPU_QUEUE);
        }

        clock = System.currentTimeMillis();
    }

    /**
     * The main loop of the scheduler
     * Each process is worked on for a time based on
     * the time between loops to be more realistic
     */
    public void run() {
        while (true) {
            long time = System.currentTimeMillis();
            long workTime = time - clock;
            clock = time;

            //work through blocked and cpu processes in parallel

            //pass some time on the blocked processes
            if (!blockedQueue.isEmpty()) {
                blockedQueue.doBlockedWork(workTime);
            }

            //do cpu work
            for (int i = 0; i < PRIORITY_LEVELS; i++) {
                Queue queue = runningQueues[i];
                if (!queue.isEmpty()) {
                    queue.doCPUWork(workTime);
                    break;
                }
            }

            //if no processes left, simulate idle mode
            if (allEmpty()) {
                System.out.println("Idle mode");
                break;
            } else {
                System.out.println(this);
            }

            //slow the program down for output to be more readable
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determine if any processes left
     *
     * @return returns true if there isn't anything left to do
     */
    public boolean allEmpty() {
        for (Queue queue : runningQueues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }

        return blockedQueue.isEmpty();
    }

    /**
     * Add new process to be scheduled
     *
     * @param process The process to be added
     */
    public void add(Process process) {
        runningQueues[0].add(process);
    }

    /**
     * Notify the scheduler of an Interrupt that needs its attention
     * Used to move processes between different queses and priority levels
     *
     * @param queue     The source queue
     * @param process   The source process
     * @param interrupt The interrupt type that happened
     */
    public void event(Queue queue, Process process, Interrupt interrupt) {
        switch (interrupt) {
            case PROCESS_BLOCKED:
                blockedQueue.add(process);
                break;
            case PROCESS_READY:
                add(process);
                break;
            case LOWER_PRIORITY:
                if (queue.getType() == Queue.QueueType.CPU_QUEUE) {
                    //move process to back of next lowest priority queue
                    //if it is already in the lowest, just add it to the back
                    int priorityLevel = Math.min(PRIORITY_LEVELS - 1, queue.getPriorityLevel() + 1);
                    runningQueues[priorityLevel].add(process);
                } else {
                    //just add process to back of blocking queue
                    blockedQueue.add(process);
                }
                break;
        }
    }

    @Override
    public String toString() {
        String result = "[BlockedQueue Size:" + blockedQueue.size() + "]";
        for (Queue runningQueue : runningQueues) {
            result += "[CPUQueue Size: " + runningQueue.size() + "]";
        }
        return result;
    }
}