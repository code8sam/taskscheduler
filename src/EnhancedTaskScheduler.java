import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnhancedTaskScheduler implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    // key : task time, value : task description
    private TreeMap<LocalDateTime, String> taskMap;
    // for real-time task execution
    //  Tasks are scheduled using ScheduledExecutorService, ensuring they execute at the right time.
    private transient ScheduledExecutorService scheduler;

    public EnhancedTaskScheduler(){
        this.taskMap = new TreeMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    // add a task to the scheduler
    public void addTask(LocalDateTime time, String task){
        if(taskMap.containsKey(time)){
            System.out.println("Conflict : A task is already scheduled at : " + formatDateTime(time));
        }else {
            taskMap.put(time, task);
            System.out.println("Task added : [" + formatDateTime(time) + "] ->" + task);
            scheduleTask(time, task);
        }
    }

    // add a recurring task
    public void addRecurringTask(LocalDateTime startTime, String task, long periodInSeconds){
        System.out.println("Recurring task added : [" + formatDateTime(startTime) + "] -> " + task);
        // Tasks can repeat periodically using scheduleAtFixedRate :
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Executing recurring task : " + task + " at " + formatDateTime(LocalDateTime.now()));
        }, calculateInitialDelay(startTime), periodInSeconds, TimeUnit.SECONDS);
    }

    // remove a task
    public void removeTask(LocalDateTime time){
        if(taskMap.containsKey(time)){
            String removedTask = taskMap.remove(time);
            System.out.println("Task removed : [" + formatDateTime(time) + "] -> " + removedTask);
        }else {
            System.out.println("Not task found at time : " + formatDateTime(time));
        }
    }

    // get the next task
    public void getNextTask(){
        if(!taskMap.isEmpty()){
            Map.Entry<LocalDateTime, String> nextTask = taskMap.firstEntry();
            System.out.println("Next task : [" + formatDateTime(nextTask.getKey()) + "] -> " + nextTask.getValue());
        }else {
            System.out.println("No tasks available.");
        }
    }

    // retrieve tasks in a specific range
    public void getTasksInRange(LocalDateTime start, LocalDateTime end){
        NavigableMap<LocalDateTime, String> rangeTasks = taskMap.subMap(start, true, end, false);
        if(rangeTasks.isEmpty()){
            System.out.println("No tasks found in the range [" + formatDateTime(start) + ", " + formatDateTime(end) + ").");
        }else {
            System.out.println("Tasks in range [" + formatDateTime(start) + ", " + formatDateTime(end) + ") : ");
            for(Map.Entry<LocalDateTime, String> entry : rangeTasks.entrySet()){
                System.out.println("[" + formatDateTime(entry.getKey()) + "] -> " +entry.getValue());
            }
        }
    }

    // display all tasks
    public void displayAllTasks(){
        if(taskMap.isEmpty()){
            System.out.println("No tasks scheduled.");
        }else {
            System.out.println("All scheduled tasks : ");
            for(Map.Entry<LocalDateTime, String> entry : taskMap.entrySet()){
                System.out.println("[" + formatDateTime(entry.getKey()) + "] -> " + entry.getValue());
            }
        }
    }

    // schedule a task for real time execution
    private void scheduleTask(LocalDateTime time, String task){
        long delay = calculateInitialDelay(time);
        if(delay>=0){
            scheduler.schedule(() -> {
                System.out.println("Executing task : " + task + " at " + formatDateTime(LocalDateTime.now()));
                taskMap.remove(time);
            }, delay, TimeUnit.SECONDS);
        }
    }

    // calculate initial delay for scheduling
    private long calculateInitialDelay(LocalDateTime time){
        long delay = java.time.Duration.between(LocalDateTime.now(), time).getSeconds();
        return Math.max(delay, 0);
    }

    // format LocalDateTime for display
    private String formatDateTime(LocalDateTime time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }

    // save tasks to a file (Persistence)
    // Saves and loads tasks using serialization, enabling the scheduler to persist across sessions.
    public void saveToFile(String fileName) throws IOException{
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
            System.out.println("Task scheduler saved to file : " + fileName);
        }
    }

    // load tasks from a file (Persistence)
    public static EnhancedTaskScheduler loadFromFile(String fileName) throws IOException, ClassNotFoundException{
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            EnhancedTaskScheduler scheduler = (EnhancedTaskScheduler) ois.readObject();
            // reinitialize transient scheduler
            scheduler.scheduler = Executors.newSingleThreadScheduledExecutor();
            System.out.println("Task scheduler loaded from file : " + fileName);
            return scheduler;
        }
    }

    // main class
    public static void main (String ... args) throws IOException, ClassNotFoundException {
        EnhancedTaskScheduler scheduler = new EnhancedTaskScheduler();
        // add tasks
        LocalDateTime now = LocalDateTime.now();
        scheduler.addTask(now.plusSeconds(10), "Morning Meeting");
        scheduler.addTask(now.plusSeconds(20), "Submit Report");
        // add a recurring task
        scheduler.addRecurringTask(now.plusSeconds(15), "Daily Reminder", 10);
        // display tasks
        scheduler.displayAllTasks();
        // save tasks to file
        scheduler.saveToFile("tasks.ser");
        // load tasks to file
        EnhancedTaskScheduler loadedScheduler = EnhancedTaskScheduler.loadFromFile("tasks.ser");
        // display loaded tasks
        loadedScheduler.displayAllTasks();
    }
}
