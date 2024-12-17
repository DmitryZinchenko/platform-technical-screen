package ai.thoughtful;

public class Main {
    public static void main(String[] args) {
        System.out.println("Platform Technical Screen");

        RoboticArm arm = new RoboticArm();
        String result = arm.sort(10, 10, 10, 10);
        System.out.println(result);
    }
}