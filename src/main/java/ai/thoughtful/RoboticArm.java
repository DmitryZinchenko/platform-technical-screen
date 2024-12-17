package ai.thoughtful;

public class RoboticArm {
    private final static int MAX_DIMENSION = 150; // in cm
    private final static int MAX_VOLUME = 1000000; // in cm^3
    private final static int MAX_WEIGHT = 20; // in kg
    private final static String VALIDATE_FORMAT = "%s must be greater than 0";

    public String sort(int width, int height, int length, int mass) {
        // Validate input arguments
        validate("width", width);
        validate("height", height);
        validate("length", length);
        validate("mass", mass);

        boolean isBulky = isBulky(width, height, length);
        boolean isHeavy = isHeavy(mass);

        String result = "STANDARD";
        if (isBulky && isHeavy) result = "REJECTED";
        if (isBulky && !isHeavy || isHeavy && !isBulky) result = "SPECIAL";

        return result;
    }

    // If input parameter is invalid - we have to terminate program execution
    private static void validate(String argumentName, Integer argument) throws IllegalArgumentException {
        if (argument < 0) {
            String message = String.format(VALIDATE_FORMAT, argumentName);
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean isBulky(Integer width, Integer height, Integer length) {
        return width >= MAX_DIMENSION
                || height >= MAX_DIMENSION
                || length >= MAX_DIMENSION
                || width * height * length >= MAX_VOLUME;
    }

    private static boolean isHeavy(Integer mass) {
        return mass >= MAX_WEIGHT;
    }
}
