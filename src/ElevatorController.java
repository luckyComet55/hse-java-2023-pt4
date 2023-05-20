import java.util.Comparator;
import java.util.PriorityQueue;

public class ElevatorController implements Runnable {
  
  private final Elevator elevator;
  private final PriorityQueue<Task> onExecution;
  public ElevatorController(Elevator elevator) {
    this.elevator = elevator;
    onExecution = new PriorityQueue<>(Comparator.comparingInt(Task::getTargetFloor));
  }
  
  @Override
  public void run() {
    while (true) {
      synchronized (onExecution) {
        while (onExecution.isEmpty()) {
          try {
            System.out.println("Task queue is empty, awaiting");
            onExecution.wait();
            elevator.setDirection(elevator.getCurrentFloor() <= onExecution.peek().getTargetFloor());
          } catch (InterruptedException e) {
            System.out.println("Exception at elevator " + elevator.getName() + ", message: " + e.getMessage());
          }
        }
      }
      moveThroughBuilding();
      synchronized (onExecution) {
        if (onExecution.peek() == null) {
          System.out.println("Undefined behavior from elevator " + elevator.getName());
          return;
        }
        if (elevator.getCurrentFloor() == Math.abs(onExecution.peek().getTargetFloor())) {
          Task t = onExecution.peek();
          System.out.println("Elevator " + elevator.getName() + " released task from floor " + t.getCalledFromFloor() + " at floor " + t.getTargetFloor());
          onExecution.poll();
        }
      }
    }
  }
  
  public void moveThroughBuilding() {
    try {
      Thread.sleep(3000);
      elevator.updateFloor();
    } catch (InterruptedException e) {
      System.out.println("Exception at elevator " + elevator.getName() + ", message: " + e.getMessage());
    }
  }
  
  public void addTask(Task newTask) {
    synchronized (onExecution) {
      onExecution.add(newTask);
      if(onExecution.size() == 1) {
        onExecution.notifyAll();
      }
    }
  }
  
  public Elevator getElevator() {
    return elevator;
  }
}
