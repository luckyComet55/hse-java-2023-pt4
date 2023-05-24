import java.util.Comparator;
import java.util.PriorityQueue;

public class ElevatorController implements Runnable {
  
  private final Elevator elevator;
  private final IOHelper io = new IOHelper();
  private final PriorityQueue<Task> onExecution;
  private final PriorityQueue<Task> onTarget;
  public ElevatorController(Elevator elevator) {
    this.elevator = elevator;
    onExecution = new PriorityQueue<>(Comparator.comparingInt(Task::getTargetFloor));
    onTarget = new PriorityQueue<>(Comparator.comparingInt(Task::getCalledFromFloor));
  }
  
  @Override
  public void run() {
    while (true) {
      synchronized (onTarget) {
        while (onTarget.isEmpty() && onExecution.isEmpty()) {
          try {
            System.out.println("Task queue is empty, awaiting");
            onTarget.wait();
            elevator.setDirection(elevator.getCurrentFloor() <= onTarget.peek().getCalledFromFloor());
          } catch (InterruptedException e) {
            System.out.println("Exception at elevator " + elevator.getName() + ", message: " + e.getMessage());
          }
        }
      }
      synchronized (onTarget) {
        while (!onTarget.isEmpty() && elevator.getCurrentFloor() == Math.abs(onTarget.peek().getCalledFromFloor())) {
          Task t = onTarget.poll();
          elevator.setDirection(elevator.getCurrentFloor() <= t.getTargetFloor());
          io.printElevatorTaskAcquire(elevator, t);
          addOnExecution(t);
        }
      }
      moveThroughBuilding();
      synchronized (onExecution) {
        while (!onExecution.isEmpty() && elevator.getCurrentFloor() == Math.abs(onExecution.peek().getTargetFloor())) {
          Task t = onExecution.poll();
          io.printElevatorTaskRelease(elevator, t);
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
  
  private void addOnExecution(Task newTask) {
    synchronized (onExecution) {
      if (newTask.getTargetFloor() < elevator.getCurrentFloor()) {
        newTask.setTargetOpposite();
      }
      onExecution.add(newTask);
    }
  }
  
  public void addTask(Task newTask) {
    synchronized (onTarget) {
      if (newTask.getCalledFromFloor() < elevator.getCurrentFloor()) {
        newTask.setCalledOpposite();
      }
      onTarget.add(newTask);
      if (onTarget.size() == 1) {
        onTarget.notifyAll();
      }
    }
  }
  
  public Elevator getElevator() {
    return elevator;
  }
  
  public boolean ableToPickTask(Task t) {
    synchronized (onExecution) {
      if (onExecution.isEmpty()) return true;
    }
    if (t.isDirectionUp() && elevator.getDirection() && t.getCalledFromFloor() >= elevator.getCurrentFloor()) return true;
    if (!t.isDirectionUp() && !elevator.getDirection() && t.getCalledFromFloor() <= elevator.getCurrentFloor()) return true;
    return false;
  }
}
