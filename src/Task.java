public class Task {
  private final boolean directionUp;
  private final int targetFloor;
  private final int calledFromFloor;
  
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
}
