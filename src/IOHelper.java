import java.util.InputMismatchException;
import java.util.Scanner;

public class IOHelper {
  
  private Scanner in;
  
  public IOHelper() {
    in = new Scanner(System.in);
  }

  public Task inputTask() {
    System.out.println("Введите этаж вызова и целевой этаж заявки");
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
}
