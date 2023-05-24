/**
 * Класс лифта. Хранит в себе данные по тому,
 * куда лифт перемещается в данный момент, какой
 * этаж текущий и какой в здании - максимальный.
 * Осуществляет перемещение лифта
 */
public class Elevator {
  
  private int currentFloor = 1;
  private boolean directionUp = true;
  private final int maxFloor;
  private final String name;
  
  public Elevator(String name, int maxFloor) {
    this.name = name;
    this.maxFloor = maxFloor;
  }
  
  public int getCurrentFloor() {
    return currentFloor;
  }
  
  /**
   * Перемещает лифт вверх или вниз, если
   * это не выходит за границы здания
   */
  public void updateFloor() {
    if (directionUp && currentFloor < maxFloor) {
      currentFloor++;
    } else if (!directionUp && currentFloor > 1) {
      currentFloor--;
    }
  }
  
  public String getName() {
    return name;
  }
  
  public boolean getDirection() {
    return directionUp;
  }
  
  public void setDirection(boolean directionUp) {
    this.directionUp = directionUp;
  }
}
