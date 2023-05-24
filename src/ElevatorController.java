import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Контроллер лифта.
 * Тут происходит основная логика по выполнению задач.
 * Если задача определена на исполнение для лифта, она добавляется
 * в очередь onTarget. Когда лифт достигает этажа, в котором имеется
 * задача из очереди onTarget, он перемещает её в очередь onExecution.
 * Когда задача исполнена, она удаляется из очереди onExecution.
 */
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
  
  /**
   * Основной метод, в нём делается
   * много всего.
   * Если очередь onExecution пуста, ожидается, когда
   * очередь onTarget станет непустой,
   * после чего для каждого этажа выполняется следующее:
   * проверятеся, есть ли на этаже задачи из onTarget,
   * если есть, они переносятся из onTarget в onExecution.
   * Если на этаже есть задачи из onExecution, они выбрасываются.
   * Лифт перемещается на следующий этаж или ожидает заполнение очереди onTarget.
   */
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
  
  /**
   * Определает движение лифта по зданию
   * Использует Thread.sleep для имитации передвижения
   */
  public void moveThroughBuilding() {
    try {
      Thread.sleep(3000);
      elevator.updateFloor();
      io.printElevatorStatus(elevator);
    } catch (InterruptedException e) {
      System.out.println("Exception at elevator " + elevator.getName() + ", message: " + e.getMessage());
    }
  }
  
  /**
   * Добавляет новую задачу в очередь onExecution
   * @param newTask новая задача, которую надо исполнить и сбросить на этаже
   */
  private void addOnExecution(Task newTask) {
    synchronized (onExecution) {
      if (newTask.getTargetFloor() < elevator.getCurrentFloor()) {
        newTask.setTargetOpposite();
      }
      onExecution.add(newTask);
    }
  }
  
  /**
   * Добавляет новую задачу в очередь onTarget
   * @param newTask новая задача, которую надо получить на этаже
   */
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
  
  /**
   * Метод проверки того, может ли лифт взять
   * задачу в параметре
   * @param t задача, объект класса Task
   * @return если задача может быть исполнена, возвращается true, иначе false
   */
  public boolean ableToPickTask(Task t) {
    synchronized (onExecution) {
      if (onExecution.isEmpty()) return true;
    }
    if (t.isDirectionUp() && elevator.getDirection() && t.getCalledFromFloor() >= elevator.getCurrentFloor()) return true;
    if (!t.isDirectionUp() && !elevator.getDirection() && t.getCalledFromFloor() <= elevator.getCurrentFloor()) return true;
    return false;
  }
}
