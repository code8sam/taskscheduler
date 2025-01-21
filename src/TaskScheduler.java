import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TaskScheduler {
    // key : time(timestamp), value : task description
    private TreeMap<Long, String> taskMap;

    public TaskScheduler(){
        this.taskMap = new TreeMap<>();
    }

    // add a task to the scheduler
    public void addTask(long timestamp, String task){
        if(taskMap.containsKey(timestamp)){
            System.out.println("Conflict : A task is already scheduled at : " + timestamp);
        } else {
            taskMap.put(timestamp, task);
            System.out.println("Task added : [" +timestamp+ "] -> " + task);
        }
    }

    // remove a task from the scheduler
    public void removeTask(long timestamp){
        if(taskMap.containsKey(timestamp)){
            String removedTask = taskMap.remove(timestamp);
            System.out.println("Task removed : [" +timestamp+"] -> " + removedTask);
        } else {
            System.out.println("No task found at timestamp : " + timestamp);
        }
    }

    // get the next scheduled task
    public String getNextTask(){
        if(!taskMap.isEmpty()){
            Map.Entry<Long, String> nextTask = taskMap.firstEntry();
            return "Next task : [" +nextTask.getKey() + "] -> " + nextTask.getValue();
        }
        return "No tasks available !";
    }

    // retrieve all tasks in a specified range
    public void getTasksInRange(long start, long end){
        NavigableMap<Long, String> rangeTasks = taskMap.subMap(start, true, end, true);
        if(rangeTasks.isEmpty()){
            System.out.println("No tasks found in the range : [" + start + ", " + end + "].");
        } else{
            System.out.println("Tasks in range [" + start + ", " + end + "] : ");
            for(Map.Entry<Long, String> entry : rangeTasks.entrySet()){
                System.out.println("[" + entry.getKey() + "] -> " + entry.getValue());
            }
        }
    }

    // display all tasks
    public void displayAllTasks(){
        if(taskMap.isEmpty()){
            System.out.println("No tasks scheduled.");
        } else{
            System.out.println("All scheduled tasks :");
            for(Map.Entry<Long, String> entry : taskMap.entrySet()){
                System.out.println("[" + entry.getKey() + "] -> " + entry.getValue());
            }
        }
    }

    // main class
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler();
        // Add tasks
        scheduler.addTask(1672503000000L, "Meeting with Team A");
        scheduler.addTask(1672506600000L, "Submit Project Report");
        scheduler.addTask(1672504800000L, "Prepare Presentation");
        // Display all tasks
        scheduler.displayAllTasks();
        // Get the next task
        System.out.println(scheduler.getNextTask());
        // Retrieve tasks in a specific range
        // Start of range
        long startTime = 1672503000000L;
        // End of range
        long endTime = 1672506600000L;
        scheduler.getTasksInRange(startTime, endTime);
        // Remove a task
        scheduler.removeTask(1672504800000L);
        // Display all tasks after removal
        scheduler.displayAllTasks();
    }
}
