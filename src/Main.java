public class Main {
  
  public static synchronized void main(String[] args) {
    LiftGroupController lgc = new LiftGroupController();
    Thread thread = new Thread(lgc);
    thread.start();
  }
}
