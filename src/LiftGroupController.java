import java.util.ArrayList;

/**
 * Класс контроллер группы из двух лифтов
 * Обрабатывает запросы на этажи, распределяет между лифтами
 * Запросы, которые не являются попутными (а по этому критерию распределяются задачи),
 * сохраняются в массив
 */
public class LiftGroupController implements Runnable {
  
  private final ElevatorController l1;
  private final ElevatorController l2;
  private final int maxFloor = 18;
  private final IOHelper io = new IOHelper();
  private final ArrayList<Task> onAwait = new ArrayList<>();
  
  public LiftGroupController() {
    l1 = new ElevatorController(new Elevator("L1", maxFloor));
    l2 = new ElevatorController(new Elevator("L2", maxFloor));
  }
  
  /**
   * Поток обработки запросов по путешествию меж этажами
   * Проверяет корректность введённых значений, пытается добавить
   * задачи по лифтам. Если не выходит, сохраняет к себе
   */
  @Override
  public void run() {
    Thread t1 = new Thread(l1);
    Thread t2 = new Thread(l2);
    t1.setDaemon(true);
    t2.setDaemon(true);
    t1.start();
    t2.start();
    while (true) {
      Task t = io.inputTask();
      if (t.getCalledFromFloor() < 1 || t.getCalledFromFloor() > maxFloor || t.getTargetFloor() < 1 || t.getTargetFloor() > maxFloor) {
        System.out.println("Неверные номера этажей");
        continue;
      }
      if (t.getTargetFloor() == t.getCalledFromFloor()) {
        System.out.println("Так можно и пешком добраться");
        continue;
      }
      boolean res = tryAddTask(t);
      onAwait.removeIf(this::tryAddTask);
      if (!res) {
        io.printTaskQueued(t);
        onAwait.add(t);
      }
    }
    
  }
  
  /**
   * Проверка того, возможно ли текущую задачу добавить какому-либо
   * из лифтов
   * @param t задача, объект класса Task
   * @return если задача может быть отдана лифту на исполнение, она отдаётся и возвращается true
   */
  private boolean tryAddTask(Task t) {
    if (l1.ableToPickTask(t) && l2.ableToPickTask(t)) {
      if (Math.abs(l1.getElevator().getCurrentFloor() - t.getCalledFromFloor()) <= Math.abs(l2.getElevator().getCurrentFloor() - t.getCalledFromFloor())) {
        l1.addTask(t);
        io.printElevatorTaskTarget(l1.getElevator(), t);
      } else {
        l2.addTask(t);
        io.printElevatorTaskTarget(l2.getElevator(), t);
      }
      return true;
    } else if (l1.ableToPickTask(t)) {
      l1.addTask(t);
      io.printElevatorTaskTarget(l1.getElevator(), t);
      return true;
    } else if (l2.ableToPickTask(t)) {
      l2.addTask(t);
      io.printElevatorTaskTarget(l2.getElevator(), t);
      return true;
    }
    return false;
  }
}
