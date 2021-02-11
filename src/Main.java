import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Hakkı Can Akut, 20170808010
 */
public class Main {
    public static void main(String[] args) {
        // if there isn't any argument then will try to get jobs.txt initially
        String inputFile = "jobs.txt";
        if (args.length > 0) {
            inputFile = args[0];
        }
        Scanner input = null;
        try {
            input = new Scanner(new File(inputFile));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        // Parsing processes
        LinkedList<Process> processes = new LinkedList<>();
        while (input.hasNextLine()) {
            LinkedList<Tuple> tuples = new LinkedList<>();
            String s = input.nextLine();
            s = s.replace(" ", "");
            if (s.equals("")) {
                continue;
            }
            int pid = Integer.parseInt(s.split(":")[0]);
            s = s.split(":")[1];
            s = s.replace("(", "");
            s = s.replace(")", "");
            String[] array = s.split(";");
            for (String t : array) {
                tuples.add(new Tuple(Integer.parseInt(t.split(",")[0]), Integer.parseInt(t.split(",")[1])));
            }
            // Checks uniqueness of pid so there will be no duplicates.
            if (checkPIDUniqueness(processes, pid)) {
                processes.add(new Process(pid, tuples));
            }

        }

        firstComeFirstServe(processes);
    }

    /**
     * finds average turnaround time and average waiting time prints it.
     * Firstly sorts processes by process id(pid).
     * Then starts while loop, while loop checks is all processes terminated with isEnded() method.
     * If it's ended breaks while loop. Otherwise continues to work.
     * Firstly checks there a process which can be start(not in io-burst) with returnTimeCheck() method.
     * if there is not process available then makes time to minimum return time of processes with minReturnTime() method.
     * Also changes pc because makes sure next running process is not last ran process,
     * if there is more than 1 process available at that time.
     * Then checks is current pc is terminated or return time of process is greater than current time.
     * If it's then will change current pc.
     * Then changes current time(adds cpu burst) and changes current tuple as completed.
     * And update current process return time.
     * Lastly checks is every tuple completed, if it's then changes current process as terminated.
     * After all processes terminated(loop is ended) will find average turnaround time and average waiting time with
     * getAverageTurnaroundTime() and getAverageWaitingTime() method and print it.
     *
     * @param processes list of processes
     */
    public static void firstComeFirstServe(LinkedList<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.pid));
        int time = 0;
        int pc = 0;
        int n = processes.size();
        while (!isEnded(processes)) {

            if (!returnTimeCheck(time, processes)) {
                if (pc + 1 == n) {
                    pc = 0;
                } else {
                    pc++;
                }
                time = minReturnTime(processes);
                continue;
            }
            while (processes.get(pc).isTerminated || processes.get(pc).returnTime > time) {
                if (pc + 1 < n) {
                    pc++;
                } else {
                    pc = 0;
                }
            }
            int ct = getCurrentTuple(processes.get(pc).tuples);
            time = time + processes.get(pc).tuples.get(ct).cpuBurst;
            processes.get(pc).tuples.get(ct).isCompleted = true;
            if (processes.get(pc).tuples.get(ct).ioBurst == -1) {
                processes.get(pc).returnTime = time;
            } else {
                processes.get(pc).returnTime = time + processes.get(pc).tuples.get(ct).ioBurst;
            }
            if (isTerminated(processes.get(pc).tuples)) {
                processes.get(pc).isTerminated = true;
            }

        }

        System.out.println("Average turnaround time: " + getAverageTurnaroundTime(processes));
        System.out.println("Average waiting time: " + getAverageWaitingTime(processes));

    }

    /**
     * finds average turnaround time
     *
     * @param processes list of processes
     * @return average turnaround time
     */
    public static double getAverageTurnaroundTime(LinkedList<Process> processes) {
        int ttt = 0;
        for (Process p : processes) {
            ttt += p.returnTime;
        }
        return (double) ttt / processes.size();
    }

    /**
     * finds average waiting time
     *
     * @param processes list of processes
     * @return average waiting time
     */
    public static double getAverageWaitingTime(LinkedList<Process> processes) {
        int twt = 0;
        for (Process p : processes) {
            int pwt = p.returnTime;
            for (Tuple t : p.tuples) {
                pwt -= t.cpuBurst;
                if (t.ioBurst != -1) {
                    pwt -= t.ioBurst;
                }
            }
            twt += pwt;
        }
        return (double) twt / processes.size();
    }

    /**
     * Checks is every tuple completed
     *
     * @param tuples list of tuples
     * @return true if every tuple is completed
     */
    public static boolean isTerminated(LinkedList<Tuple> tuples) {
        for (Tuple t : tuples) {
            if (!t.isCompleted) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks every process
     *
     * @param processes list of processes
     * @return true if every process is terminated
     */
    public static boolean isEnded(LinkedList<Process> processes) {
        for (Process process : processes) {
            if (!process.isTerminated) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks is there a not terminated and not working process
     *
     * @param time      current time
     * @param processes list of processes
     * @return true if there is a process can work
     */
    public static boolean returnTimeCheck(int time, LinkedList<Process> processes) {
        for (Process process : processes) {
            if (!process.isTerminated && process.returnTime <= time) {
                return true;
            }
        }
        return false;
    }

    /**
     * finds minimum return time from unterminated processes
     *
     * @param processes list of processes
     * @return minimum return time
     */
    public static int minReturnTime(LinkedList<Process> processes) {
        int min = Integer.MAX_VALUE;
        for (int i = 1; i < processes.size(); i++) {
            if (min > processes.get(i).returnTime && !processes.get(i).isTerminated) {
                min = processes.get(i).returnTime;
            }
        }
        return min;
    }

    /**
     * finds current tuple(first tuple which is not completed)
     *
     * @param tuples list of tuples of a processes
     * @return current tuple
     */
    public static int getCurrentTuple(LinkedList<Tuple> tuples) {
        for (int i = 0; i < tuples.size(); i++) {
            if (!tuples.get(i).isCompleted) {
                return i;
            }
        }
        return -1;
    }

    /**
     * checks uniqueness of pid so there will be no duplicate
     *
     * @param processes list of processes
     * @param pid       process id will be checked
     * @return true if pid is unique
     */
    public static boolean checkPIDUniqueness(LinkedList<Process> processes, int pid) {
        for (Process process : processes) {
            if (process.pid == pid) {
                return false;
            }
        }
        return true;
    }
}


class Tuple {
    int cpuBurst;
    int ioBurst;
    boolean isCompleted;

    public Tuple(int cpuBurst, int ioBurst) {
        this.cpuBurst = cpuBurst;
        this.ioBurst = ioBurst;
        this.isCompleted = false;
    }
}

class Process {
    int pid;
    LinkedList<Tuple> tuples;
    boolean isTerminated;
    int returnTime;

    public Process(int pid, LinkedList<Tuple> tuples) {
        this.pid = pid;
        this.tuples = tuples;
        this.isTerminated = false;
        returnTime = 0;
    }
}
