public class Main {
  
  public static synchronized void main(String[] args) {
    IOHelper io = new IOHelper();
    int maxFloor = 15;
    Elevator elevator = new Elevator("L1", maxFloor);
    ElevatorController ec = new ElevatorController(elevator);
    Thread thread = new Thread(ec);
    thread.setDaemon(true);
    thread.start();
    while (true) {
      Task t = io.inputTask();
      ec.addTask(t);
    }
  }
}
