CPU Scheduler
=============

What is it
----------

A Java simulation of the Multi-Level Feedback Queue CPU scheduling algorithm.

Pseudo code
-----------
```python
Loop:
    IF process exists in blocking queue:
        Work on removing each process from blocking queue in FCFS basis
        When a process is removed, add it back to the highest priority cpu queue
    
    Move from top priority cpu queue to lowest cpu priority queue until we find a process
    IF a process is found:
        work on process until end of timeslice:
            do work
            IF the process becomes blocked:
                remove it from the cpu queue
                place it on the blocked queue
                restart the timeslice with a new process

        IF the end of the timeslice has been reached:
            remove the process from the top of the queue
            IF the process is not finished:
                IF process is already in lowest priority queue:
                    add process to back of queue
                ELSE:
                    add the process to the back of the next lower priority queue
```