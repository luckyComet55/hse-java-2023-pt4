import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Класс для ввода-вывода информации по лифту и задачам
 */
public class IOHelper {
  
  private Scanner in;
  
  public IOHelper() {
    in = new Scanner(System.in);
  }

  public Task inputTask() {
    System.out.println("Введите этаж заявки и этаж назначения:");
    int a = 1, b = 1;
    boolean isCorrect = false;
    while (!isCorrect) {
      try {
        a = in.nextInt();
        b = in.nextInt();
        if (a < 1 || b < 1) {
          throw new InputMismatchException("Bad");
        }
        isCorrect = true;
      } catch (InputMismatchException e) {
        System.out.println("Неверный формат ввода");
      }
    }
    return new Task(a, b);
  }
  
  public void printElevatorStatus(Elevator elevator) {
    System.out.format("%s at %d, %s\n", elevator.getName(), elevator.getCurrentFloor(), elevator.getDirection() ? "up" : "down");
  }
  
  public void printElevatorTaskRelease(Elevator elevator, Task task) {
    System.out.format("%s dropped %d-%d\n", elevator.getName(), Math.abs(task.getCalledFromFloor()), Math.abs(task.getTargetFloor()));
  }
  
  public void printElevatorTaskAcquire(Elevator elevator, Task task) {
    System.out.format("%s picked %d-%d\n", elevator.getName(), Math.abs(task.getCalledFromFloor()), task.getTargetFloor());
  }
  
  public void printElevatorTaskTarget(Elevator elevator, Task task) {
    System.out.format("%s locked on %d-%d", elevator.getName(), task.getCalledFromFloor(), task.getTargetFloor());
  }
  
  public void printTaskQueued(Task t) {
    System.out.format("Task %d-%d queued\n", t.getCalledFromFloor(), t.getTargetFloor());
  }
}
