package ai.thoughtful;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RoboticArmTest {
    @Test
    public void validate_successfully_validates_argument() throws NoSuchMethodException {
        // arrange
        Method method = getValidateMethod();
        // act and assert
        assertDoesNotThrow(() -> {
            method.invoke(null,"height", 10);
        });
    }

    @Test
    public void validate_throws_exception_on_invalid_argument() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        // arrange
        Method validate = getValidateMethod();
        String validateFormat = getValidateFormatString();
        final String argumentName = "height";
        final Integer argumentValue = -6;
        final String expectedMessage = String.format(validateFormat, argumentName);

        // act
        // Since we are using Reflection - the InvocationTargetException will have IllegalArgumentException
        // as its cause.
        Exception exception = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
            validate.invoke(null, argumentName, argumentValue);
        });

        // assert
        assertEquals(expectedMessage, exception.getCause().getMessage());
    }

    @Test
    public void isBulky_returns_false() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method isBulky = getIsBulkyMethod();
        // act
        boolean bulky = (boolean) isBulky.invoke(null, 10, 10, 10);
        // assert
        assertFalse(bulky);
    }

    @ParameterizedTest
    @ArgumentsSource(IsBulkyArgumentsProvider.class)
    public void isBulky_returns_true(Integer width, Integer height, Integer length) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // arrange
        Method isBulky = getIsBulkyMethod();
        // act
        boolean bulky = (boolean) isBulky.invoke(null, width, height, length);
        // assert
        assertTrue(bulky);
    }

    @Test
    public void isHeavy_returns_false() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // NOTE: I could retrieve the value of MAX_WEIGHT the same as VALIDATE_FORMAT
        // but for the sake of time I'm just hardcoding it
        final int MAX_WEIGHT = 20;

        // arrange
        Method isHeavy = getIsHeavyMethod();
        boolean heavy = (boolean) isHeavy.invoke(null, MAX_WEIGHT - 1);
        assertFalse(heavy);
    }

    @Test
    public void isHeavy_returns_true() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // NOTE: I could retrieve the value of MAX_WEIGHT the same as VALIDATE_FORMAT
        // but for the sake of time I'm just hardcoding it
        final int MAX_WEIGHT = 20;

        // arrange
        Method isHeavy = getIsHeavyMethod();
        boolean heavy = (boolean) isHeavy.invoke(null, MAX_WEIGHT);
        assertTrue(heavy);
    }

    @ParameterizedTest
    @ArgumentsSource(SortArgumentsProvider.class)
    public void sort_returns_appropriate_string(Integer width, Integer height, Integer length, Integer mass, String expected) {
        RoboticArm arm = new RoboticArm();
        String actual = arm.sort(width, height, length, mass);
        assertEquals(expected, actual);
    }

    // since "validate" method is declared as private - we need to use Reflection
    private Method getValidateMethod() throws NoSuchMethodException {
        Method method = RoboticArm.class.getDeclaredMethod("validate", String.class, Integer.class);
        method.setAccessible(true);
        return method;
    }

    // since "VALIDATE_FORMAT" field is declared as private - we need to use Reflection
    private String getValidateFormatString() throws NoSuchFieldException, IllegalAccessException {
        Field field = RoboticArm.class.getDeclaredField("VALIDATE_FORMAT");
        field.setAccessible(true);
        return (String)field.get(null);
    }

    // since "isBulky" method is declared as private - we need to use Reflection
    private Method getIsBulkyMethod() throws NoSuchMethodException {
        Method method = RoboticArm.class.getDeclaredMethod("isBulky", Integer.class, Integer.class, Integer.class);
        method.setAccessible(true);
        return method;
    }

    // since "isHeavy" method is declared as private - we need to use Reflection
    private Method getIsHeavyMethod() throws NoSuchMethodException {
        Method method = RoboticArm.class.getDeclaredMethod("isHeavy", Integer.class);
        method.setAccessible(true);
        return method;
    }

    private static class IsBulkyArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // NOTE: I could retrieve the value of MAX_DIMENSION the same as VALIDATE_FORMAT
            // but for the sake of time I'm just hardcoding it
            final int MAX_DIMENSION = 150;

            return Stream.of(
                    Arguments.of(MAX_DIMENSION, 10, 10),
                    Arguments.of(10, MAX_DIMENSION, 10),
                    Arguments.of(10, 10, MAX_DIMENSION),
                    Arguments.of(100, 100, 100)
            );
        }
    }

    private static class SortArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // NOTE: I could retrieve the value of MAX_DIMENSION and MAX_WEIGHT the same as VALIDATE_FORMAT
            // but for the sake of time I'm just hardcoding it
            final int MAX_DIMENSION = 150;
            final int MAX_WEIGHT = 20;

            return Stream.of(
                    Arguments.of(10, 10, 10, 10, "STANDARD"),
                    Arguments.of(MAX_DIMENSION, MAX_DIMENSION, MAX_DIMENSION, MAX_WEIGHT, "REJECTED"),
                    Arguments.of(MAX_DIMENSION, 10, 10, 10, "SPECIAL"),
                    Arguments.of(10, MAX_DIMENSION, 10, 10, "SPECIAL"),
                    Arguments.of(10, 10, MAX_DIMENSION, 10, "SPECIAL"),
                    Arguments.of(10, 10, 10, MAX_WEIGHT, "SPECIAL")
            );
        }
    }
}
