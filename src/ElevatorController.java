import java.util.Comparator;
import java.util.PriorityQueue;

public class ElevatorController implements Runnable {
  
  private final Elevator elevator;
  private final IOHelper io = new IOHelper();
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
          io.printElevatorTaskRelease(elevator, t);
          onExecution.poll();
        }
      }
    }
  }
  
  public void moveThroughBuilding() {
    try {
      Thread.sleep(3000);
      elevator.updateFloor();
      io.printElevatorStatus(elevator);
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
  
  public int getTasksSize() {
    return onExecution.size();
  }
  
  public boolean ableToPickTask(Task t) {
    synchronized (onExecution) {
      if (onExecution.isEmpty()) return true;
    }
    if (t.isDirectionUp() && elevator.getDirection() && t.getCalledFromFloor() <= elevator.getCurrentFloor()) return true;
    if (!t.isDirectionUp() && !elevator.getDirection() && t.getCalledFromFloor() >= elevator.getCurrentFloor()) return true;
    return false;
  }
}
