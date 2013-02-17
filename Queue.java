/**
 * @author Mark Dunne (111379601)
 */

import java.util.LinkedList;

public class Queue extends LinkedList<Process> {

    private long quantum;
    private long quantumClock;
    private Scheduler scheduler;
    private int priorityLevel;
    private QueueType queueType;


    /**
     * The types of queue that can exist
     */
    public static enum QueueType {
        CPU_QUEUE,
        BLOCKED_QUEUE
    }


    /**
     * Creates a new Queue object
     *
     * @param scheduler     The Scheduler instance the queue is tied to
     * @param quantum       The quantum time of the queue
     * @param priorityLevel The priority of the queue
     * @param queueType     The type of the queue
     */
    public Queue(Scheduler scheduler, long quantum, int priorityLevel, QueueType queueType) {
        this.priorityLevel = priorityLevel;
        this.scheduler = scheduler;
        this.quantum = quantum;
        this.quantumClock = 0;
        this.queueType = queueType;
    }

    /**
     * Manage changing between time slices
     *
     * @param currentProcess The process that is currently being worked on
     * @param time           The amount of time that has passed
     */
    public void manageTimeSlice(Process currentProcess, long time) {
        if (currentProcess.isStateChanged()) {
            quantumClock = 0;
            return;
        }

        quantumClock += time;
        if (quantumClock > quantum) {
            quantumClock = 0;
            Process process = remove();
            if (!process.isFinished()) {
                //move down to next queue if we can
                scheduler.event(this, process, Scheduler.Interrupt.LOWER_PRIORITY);
            } else {
                System.out.println("Process complete!");
            }
        }
    }

    /**
     * Simulate doing some computation work on the process
     *
     * @param time The amount of time for computation given to the process
     */
    public void doCPUWork(long time) {
        Process process = element();
        process.doCPUWork(time);
        manageTimeSlice(process, time);
    }

    /**
     * Simulate working through a blocked process
     *
     * @param time The amount of time given for working through the block
     */
    public void doBlockedWork(long time) {
        Process process = element();
        process.doBlockedWork(time);
        manageTimeSlice(process, time);
    }

    /**
     * Determine if the queue is empty
     *
     * @return True if the queue is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Notify the queue of any interrupts that happen on the process
     *
     * @param source    The source process
     * @param interrupt The interrupt type
     */
    public void event(Process source, Scheduler.Interrupt interrupt) {
        remove(source);
        switch (interrupt) {
            case PROCESS_BLOCKED:
                //process entered blocked state
                scheduler.event(this, source, Scheduler.Interrupt.PROCESS_BLOCKED);
                break;
            case PROCESS_READY:
                scheduler.event(this, source, Scheduler.Interrupt.PROCESS_READY);
                break;
        }
    }

    /**
     * Add a process to the queue and set its parent queue to this one
     *
     * @param process The queue to add
     * @return True as usual with collections
     */
    @Override
    public boolean add(Process process) {
        process.setParentQueue(this);
        return super.add(process);
    }

    /**
     * Get the priority level of the queue
     *
     * @return The priority level
     */
    public int getPriorityLevel() {
        return priorityLevel;
    }

    /**
     * Get the type of the queue
     *
     * @return The type of the queue
     */
    public QueueType getType() {
        return queueType;
    }
}
