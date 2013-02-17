/**
 * @author Mark Dunne (111379601)
 */

public class Main {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.add(new Process());
        scheduler.run();
    }
}
