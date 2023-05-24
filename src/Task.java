/**
 * Класс задачи
 * Задача имеет этаж получения
 * и этаж назначения, а также направление движения.
 */
public class Task {
  private final boolean directionUp;
  private int targetFloor;
  private int calledFromFloor;
  
  public Task (int calledFromFloor, int targetFloor) {
    this.directionUp = calledFromFloor < targetFloor;
    this.calledFromFloor = calledFromFloor;
    this.targetFloor = targetFloor;
  }
  
  public boolean isDirectionUp() {
    return directionUp;
  }
  
  public int getTargetFloor() {
    return targetFloor;
  }
  
  public int getCalledFromFloor() {
    return calledFromFloor;
  }
  
  public void setTargetOpposite() {
    targetFloor = -targetFloor;
  }
  
  public void setCalledOpposite() {
    calledFromFloor = -calledFromFloor;
  }
}
