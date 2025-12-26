package enterprises.iwakura;

import enterprises.iwakura.ganyu.Ganyu;
import enterprises.iwakura.ganyu.impl.*;
import enterprises.iwakura.ganyu.test.TestCommand;
import enterprises.iwakura.parsers.GreedySomeArgParser;
import enterprises.iwakura.parsers.SomeArgParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class GanyuTest {

    public static final String STRING_EXPECTED = "wooo here's some value!";

    private Ganyu ganyu;
    private WritableInput input;

    @BeforeEach
    public void setup() {
        input = new WritableInput();
        ganyu = Ganyu.standard(input, new ConsoleOutput());

        ganyu.registerCommands(new TestCommand());
        ganyu.registerArgumentParser(new SomeArgParser());
        ganyu.registerArgumentParser(new GreedySomeArgParser());

        ganyu.run();
    }

    public static void main(String[] args) {
        Ganyu ganyu = Ganyu.console();

        ganyu.registerCommands(new TestCommand());
        ganyu.registerArgumentParser(new SomeArgParser());
        ganyu.registerArgumentParser(new GreedySomeArgParser());

        ganyu.run();
    }

    @SneakyThrows
    private <T> T waitForReference(AtomicReference<T> reference) {
        synchronized (reference) {
            reference.wait(100);
            return reference.get();
        }
    }

    @Test
    public void testDefaultCommand() {
        input.write("test " + STRING_EXPECTED);
        assertEquals("Default command executed with text: " + STRING_EXPECTED, waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testTest2Command() {
        input.write("test-2 " + STRING_EXPECTED);
        assertEquals("Test 2 executed with text: " + STRING_EXPECTED, waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoArgInjectCommand() {
        String from = "sender";
        input.write("test echo-arg-inject " + from + " " + STRING_EXPECTED);
        assertEquals(String.format("[%s] %s: %s", ganyu.toString(), from, STRING_EXPECTED), waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoAllCommand() {
        int simpleNumber = 42;
        double decimalNumber = 3.14;
        long longNumber = 1000000L;
        boolean bool = true;
        UUID uuid = UUID.randomUUID();

        input.write(String.format(Locale.US, "test echo-all %d %.2f %d %b %s", simpleNumber, decimalNumber, longNumber, bool, uuid));

        assertEquals(String.format(Locale.US, "Simple: %d, Decimal: %.2f, Long: %d, Boolean: %b, UUID: %s",
                                   simpleNumber, decimalNumber, longNumber, bool, uuid), waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoNamedCommand_short() {
        UUID uuid = UUID.randomUUID();
        input.write(String.format(Locale.US, "test echo-named -t \"%s\" -n %d -d %.2f -b %b -u %s",
                                  STRING_EXPECTED, 42, 3.14, true, uuid));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: 42, Decimal: 3.14, Bool: true, UUID: " + uuid));
    }

    @Test
    public void testEchoNamedCommand_long() {
        UUID uuid = UUID.randomUUID();
        input.write(String.format(Locale.US, "test echo-named --text \"%s\" --number %d --decimal %.2f --bool %b --uuid %s",
                                  STRING_EXPECTED, 42, 3.14, true, uuid));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: 42, Decimal: 3.14, Bool: true, UUID: " + uuid));
    }

    @Test
    public void testEchoNamedCommand_combined() {
        UUID uuid = UUID.randomUUID();
        input.write(String.format(Locale.US, "test echo-named -t \"%s\" --number %d -d %.2f --bool %b -u %s",
                                  STRING_EXPECTED, 42, 3.14, true, uuid));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: 42, Decimal: 3.14, Bool: true, UUID: " + uuid));
    }

    @Test
    public void testEchoNamedOptionalCommand_noArgs() {
        input.write("test echo-optional-named");
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: null, Number: null, Decimal: null"));
    }

    @Test
    public void testEchoNamedOptionalCommand_onlyText() {
        input.write(String.format(Locale.US, "test echo-optional-named -t \"%s\"", STRING_EXPECTED));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: null, Decimal: null"));
    }

    @Test
    public void testEchoNamedOptionalCommand_textAndNumber() {
        input.write(String.format(Locale.US, "test echo-optional-named -t \"%s\" -n %d", STRING_EXPECTED, 42));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: 42, Decimal: null"));
    }

    @Test
    public void testEchoNamedOptionalCommand_allArgs() {
        input.write(String.format(Locale.US, "test echo-optional-named -t \"%s\" -n %d -d %.2f", STRING_EXPECTED, 42, 3.14));
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Text: " + STRING_EXPECTED + ", Number: 42, Decimal: 3.14"));
    }

    @Test
    public void testEchoOptionalCommand_all() {
        input.write("test echo-optional this-is-string 42 3.14 ");
        assertEquals("Optional Arguments: Text: this-is-string, Number: 42, Decimal: 3.14", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoOptionalCommand_all_spacedOut() {
        input.write("test echo-optional this-is-string    42               3.14 ");
        assertEquals("Optional Arguments: Text: this-is-string, Number: 42, Decimal: 3.14", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoOptionalCommand_decimalOmitted() {
        input.write("test echo-optional this-is-string 42");
        assertEquals("Optional Arguments: Text: this-is-string, Number: 42, Decimal: null", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoOptionalCommand_integerDecimalOmitted() {
        input.write("test echo-optional this-is-string");
        assertEquals("Optional Arguments: Text: this-is-string, Number: null, Decimal: null", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoOptionalCommand_allOmitted() {
        input.write("test echo-optional");
        assertEquals("Optional Arguments: Text: null, Number: null, Decimal: null", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testStopCommand() {
        input.write("test stop");
        assertEquals("Stopping!", waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testEchoTwoLevelDeepCommand() {
        input.write("test echo two-level-deep " + STRING_EXPECTED);
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Two-level-deep command executed with text: " + STRING_EXPECTED));
    }

    @Test
    public void testTwoLevelDeepCommand() {
        input.write("test two-level-deep " + STRING_EXPECTED);
        assertTrue(waitForReference(TestCommand.lastOutputReference).contains("Test two-level-deep command executed with text: " + STRING_EXPECTED));
    }

    @Test
    public void testAsyncExecutionCommand() {
        input.write("test async-execution " + STRING_EXPECTED);
        assertEquals("Asynchronous execution with text: " + STRING_EXPECTED, waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testPreCommand() {
        input.write("test echo-optional this-is-string 42");
        assertEquals("Pre command executed for: this-is-string 42", waitForReference(TestCommand.preCommandOutputReference));
    }

    @Test
    public void testPostCommand() {
        input.write("test echo-optional this-is-string 42");
        assertEquals("Post command executed for: this-is-string 42", waitForReference(TestCommand.postCommandOutputReference));
    }

    @Test
    public void testExceptionHandler() {
        input.write("test throw-exception");
        assertNotNull(waitForReference(TestCommand.lastException));
    }

    @Test
    public void testSomeArgCommand() {
        String value = "custom-arg-value";
        input.write("test some-arg " + value);
        assertEquals("Received SomeArg with value: " + value, waitForReference(TestCommand.lastOutputReference));
    }

    @Test
    public void testGreedySomeArgCommand() {
        String value = "custom greedy arg value with spaces";
        input.write("test greedy-some-arg " + value);
        assertEquals("Received GreedySomeArg with value: " + value, waitForReference(TestCommand.lastOutputReference));
    }
}