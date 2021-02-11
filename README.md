# CPU-Scheduling-with-IO
First Come First Serve(FCFS) CPU scheduling with IO

Implement the First ComeFirst Served(FCFS) scheduling Algorithm and print:

      Average turnaround time: The average of the turnaround times of all process
      Average waiting time: The average of the total waiting time for all processes

Provided file contains a set of processes and a set of associated CPU and I/O bursts. For example:
      
      1:(45,15);(16,20);(80,10);(40,-1)
      2:(15,10);(60,15);(90,10);(85,20);(20,-1)
      3:(30,15);(40,20);(5,15);(10,15);(15,-1)

Format of a line is as follows:

      <process-id>:(<cpu-burst1, io-burst1>);(<cpu-burst2, io-burst2>);...(<cpu-bursti, io-bursti>)

If the last io-burst is -1, then it means that the process terminates without making an I/O.

All the jobs arrive at  the same time (t=0), the  order  of arrival is  the  same as  the  order  of process-ids (i.e., smallerids arrive earlier).
The process never waits at the device queues and I/O starts immediately.

works with this command:

      javac *.java
      java Main jobs.txt
