package enterprises.iwakura.ganyu.test;

import enterprises.iwakura.arguments.GreedySomeArg;
import enterprises.iwakura.arguments.SomeArg;
import enterprises.iwakura.ganyu.CommandInvocationContext;
import enterprises.iwakura.ganyu.CommandResult;
import enterprises.iwakura.ganyu.GanyuCommand;
import enterprises.iwakura.ganyu.annotation.*;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Command("test")
@Syntax("<long ahh syntax text here>")
@Description("A test command!")
public class TestCommand implements GanyuCommand {

    public static final AtomicReference<String> lastOutputReference = new AtomicReference<>("");
    public static final AtomicReference<String> preCommandOutputReference = new AtomicReference<>("");
    public static final AtomicReference<String> postCommandOutputReference = new AtomicReference<>("");
    public static final AtomicReference<Exception> lastException = new AtomicReference<>(null);

    private <T> void updateReference(CommandInvocationContext ctx, AtomicReference<T> reference, T message) {
        synchronized (reference) {
            ctx.getGanyu().getOutput().info("Updating reference to: " + message);
            reference.set(message);
            reference.notifyAll();
        }
    }
    
    @PreCommand
    public void preCommand(CommandInvocationContext ctx) {
        updateReference(ctx, preCommandOutputReference, "Pre command executed for: " + ctx.getUnprocessedArguments());
    }

    @PostCommand
    public void postCommand(CommandInvocationContext ctx) {
        updateReference(ctx, postCommandOutputReference, "Post command executed for: " + ctx.getUnprocessedArguments());
    }

    @DefaultCommand
    public void defaultTest(CommandInvocationContext ctx, String text) {
        updateReference(ctx, lastOutputReference, "Default command executed with text: " + text);
    }

    @Command("test-2")
    @Syntax("<text>")
    @Description("A second test command!")
    public void test2(CommandInvocationContext ctx, String text) {
        updateReference(ctx, lastOutputReference, "Test 2 executed with text: " + text);
    }

    @SubCommand("echo")
    @Syntax("<text>")
    @Description("Echoes a message.")
    public CommandResult echo(CommandInvocationContext ctx, String text) {
        updateReference(ctx, lastOutputReference, text);
        return CommandResult.success();
    }

    @SubCommand("echo-arg-inject")
    @Syntax("<from> <text>")
    @Description("Echoes a message with a specified sender.")
    public CommandResult echoComplex(CommandInvocationContext ctx, String from, String text) {
        updateReference(ctx, lastOutputReference, String.format("[%s] %s: %s", ctx.getGanyu(), from, text));
        return CommandResult.success();
    }

    @SubCommand("echo-all")
    @Description("Echoes everything.")
    public CommandResult echoNumber(CommandInvocationContext ctx, @NamedArg("simple-number") int simpleNumber, double decimalNumber, long longNumber, boolean bool, UUID uuid) {
        updateReference(ctx, lastOutputReference, String.format(Locale.US, "Simple: %d, Decimal: %.2f, Long: %d, Boolean: %b, UUID: %s", simpleNumber, decimalNumber, longNumber, bool, uuid));
        return CommandResult.success();
    }

    @SubCommand("echo-named")
    @NamedArgumentHandler
    public void echoNamed(
            CommandInvocationContext ctx,
            @NamedArg(value = "t", longForm = "text") String text,
            @NamedArg(value = "n", longForm = "number") int number,
            @NamedArg(value = "d", longForm = "decimal") double decimal,
            @NamedArg(value = "b", longForm = "bool") boolean bool,
            @NamedArg(value = "u", longForm = "uuid") UUID uuid
    ) {
        updateReference(ctx, lastOutputReference, String.format(Locale.US, "Text: %s, Number: %d, Decimal: %.2f, Bool: %b, UUID: %s", text, number, decimal, bool, uuid));
    }

    @SubCommand("echo-optional-named")
    @NamedArgumentHandler
    @Syntax("[text] [number] [decimal]")
    @Description("Echoes an optional message with named arguments.")
    public void echoOptionalNamed(
            CommandInvocationContext ctx,
            @OptionalArg @NamedArg(value = "t", longForm = "text") String text,
            @OptionalArg @NamedArg(value = "n", longForm = "number") Integer number,
            @OptionalArg @NamedArg(value = "d", longForm = "decimal") Double decimal
    ) {
        updateReference(ctx, lastOutputReference, String.format("Optional Arguments: Text: %s, Number: %s, Decimal: %s",
                                                      text != null ? text : "null",
                                                      number != null ? number : "null",
                                                      decimal != null ? decimal : "null"));
    }

    @SubCommand("echo-optional")
    @Syntax("[text] [number] [decimal]")
    @Description("Echoes an optional message with optional arguments.")
    public CommandResult echoOptional(CommandInvocationContext ctx, @OptionalArg String text, @OptionalArg Integer number, @OptionalArg Double decimal) {
        updateReference(ctx, lastOutputReference, String.format("Optional Arguments: Text: %s, Number: %s, Decimal: %s",
                text != null ? text : "null",
                number != null ? number : "null",
                decimal != null ? decimal : "null"));
        return CommandResult.success();
    }

    @SubCommand("stop")
    @Description("Stops the application.")
    public void stop(CommandInvocationContext ctx) {
        updateReference(ctx, lastOutputReference, "Stopping!");
    }

    @SubCommand("some-arg")
    @Description("Command with custom argument type.")
    public void someArg(CommandInvocationContext ctx, SomeArg someArg) {
        updateReference(ctx, lastOutputReference, "Received SomeArg with value: " + someArg.getStringValue());
    }

    @SubCommand("greedy-some-arg")
    @Description("Command with custom greedy argument type.")
    public void someArg(CommandInvocationContext ctx, GreedySomeArg someArg) {
        updateReference(ctx, lastOutputReference, "Received GreedySomeArg with value: " + someArg.getStringValue());
    }

    @SubCommand("echo two-level-deep")
    @Syntax("<text>")
    @Description("Echoes a message as well.")
    public CommandResult echoTwoLevelDeep(CommandInvocationContext ctx, String text) {
        updateReference(ctx, lastOutputReference, String.format("Two-level-deep command executed with text: %s in context %s", text, ctx));
        return CommandResult.success();
    }

    @Command("test two-level-deep")
    @Syntax("<text>")
    @Description("Echoes a message as well in a two-level-deep command.")
    public CommandResult testTwoLevelDeep(CommandInvocationContext ctx, String text) {
        updateReference(ctx, lastOutputReference, String.format("Test two-level-deep command executed with text: %s in context %s", text, ctx));
        return CommandResult.success();
    }

    @SubCommand("async-execution")
    @Syntax("<text>")
    @Description("Returns a command result asynchronously.")
    public CompletableFuture<CommandResult> asyncExecution(CommandInvocationContext ctx, String text) {
        return CompletableFuture.supplyAsync(() -> {
            updateReference(ctx, lastOutputReference, "Asynchronous execution with text: " + text);
            return CommandResult.success();
        });
    }

    @SubCommand("throw-exception")
    @Description("A command that always throws an exception.")
    public void throwException(CommandInvocationContext ctx) {
        throw new RuntimeException("This is a test exception from command: " + ctx);
    }

    @ExceptionHandler
    public void handleException(CommandInvocationContext ctx, Exception exception) {
        updateReference(ctx, lastOutputReference, "An error occurred while executing command: " + ctx);
        updateReference(ctx, lastException, exception);
    }
}