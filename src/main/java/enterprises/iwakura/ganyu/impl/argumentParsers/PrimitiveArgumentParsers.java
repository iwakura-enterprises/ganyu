package enterprises.iwakura.ganyu.impl.argumentParsers;

import enterprises.iwakura.ganyu.ArgumentParser;
import enterprises.iwakura.ganyu.Ganyu;
import enterprises.iwakura.ganyu.exception.CommandParseException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * Utility class to register primitive argument parsers for Ganyu instance. This covers the majority
 * of common types used in command methods including all primitive types, their boxed counterparts,
 * String, UUID, BigDecimal, BigInteger, URL, URI, and various Java Time classes.
 */
@UtilityClass
public class PrimitiveArgumentParsers {

    public static void register(Ganyu ganyu) {
        ganyu.registerArgumentParser(new IntegerArgumentParser());
        ganyu.registerArgumentParser(new DoubleArgumentParser());
        ganyu.registerArgumentParser(new BooleanArgumentParser());
        ganyu.registerArgumentParser(new StringArgumentParser());
        ganyu.registerArgumentParser(new LongArgumentParser());
        ganyu.registerArgumentParser(new UUIDArgumentParser());
        ganyu.registerArgumentParser(new FloatArgumentParser());
        ganyu.registerArgumentParser(new ShortArgumentParser());
        ganyu.registerArgumentParser(new ByteArgumentParser());
        ganyu.registerArgumentParser(new CharacterArgumentParser());
        ganyu.registerArgumentParser(new LocalDateParser());
        ganyu.registerArgumentParser(new LocalDateTimeParser());
        ganyu.registerArgumentParser(new LocalTimeParser());
        ganyu.registerArgumentParser(new InstantParser());
        ganyu.registerArgumentParser(new OffsetDateTimeParser());
        ganyu.registerArgumentParser(new ZoneIdParser());
        ganyu.registerArgumentParser(new ZoneOffsetParser());
        ganyu.registerArgumentParser(new DurationParser());
        ganyu.registerArgumentParser(new PeriodParser());
        ganyu.registerArgumentParser(new BigDecimalParser());
        ganyu.registerArgumentParser(new BigIntegerParser());
        ganyu.registerArgumentParser(new URLParser());
        ganyu.registerArgumentParser(new URIParser());
    }

    public static class IntegerArgumentParser extends ArgumentParser<Integer> {

        public IntegerArgumentParser() {
            super(Integer.class);
        }

        @Override
        public Integer parse(String argument) throws CommandParseException {
            return Integer.valueOf(argument);
        }
    }

    public static class DoubleArgumentParser extends ArgumentParser<Double> {

        public DoubleArgumentParser() {
            super(Double.class);
        }

        @Override
        public Double parse(String argument) throws CommandParseException {
            return Double.valueOf(argument);
        }
    }

    public static class BooleanArgumentParser extends ArgumentParser<Boolean> {

        public BooleanArgumentParser() {
            super(Boolean.class);
        }

        @Override
        public Boolean parse(String argument) throws CommandParseException {
            if ("true".equalsIgnoreCase(argument) || "1".equals(argument)) {
                return true;
            } else if ("false".equalsIgnoreCase(argument) || "0".equals(argument)) {
                return false;
            } else {
                throw new CommandParseException("Invalid boolean value: " + argument);
            }
        }
    }

    public static class StringArgumentParser extends ArgumentParser<String> {

        public StringArgumentParser() {
            super(String.class);
        }

        @Override
        public String parse(String argument) throws CommandParseException {
            return argument;
        }
    }

    public static class LongArgumentParser extends ArgumentParser<Long> {

        public LongArgumentParser() {
            super(Long.class);
        }

        @Override
        public Long parse(String argument) throws CommandParseException {
            return Long.valueOf(argument);
        }
    }

    public static class UUIDArgumentParser extends ArgumentParser<UUID> {

        public UUIDArgumentParser() {
            super(UUID.class);
        }

        @Override
        public UUID parse(String argument) throws CommandParseException {
            try {
                return UUID.fromString(argument);
            } catch (IllegalArgumentException e) {
                throw new CommandParseException("Invalid UUID format: " + argument);
            }
        }
    }

    public static class FloatArgumentParser extends ArgumentParser<Float> {

        public FloatArgumentParser() {
            super(Float.class);
        }

        @Override
        public Float parse(String argument) throws CommandParseException {
            return Float.valueOf(argument);
        }
    }

    public static class ShortArgumentParser extends ArgumentParser<Short> {

        public ShortArgumentParser() {
            super(Short.class);
        }

        @Override
        public Short parse(String argument) throws CommandParseException {
            return Short.valueOf(argument);
        }
    }

    public static class ByteArgumentParser extends ArgumentParser<Byte> {

        public ByteArgumentParser() {
            super(Byte.class);
        }

        @Override
        public Byte parse(String argument) throws CommandParseException {
            return Byte.valueOf(argument);
        }
    }

    public static class CharacterArgumentParser extends ArgumentParser<Character> {

        public CharacterArgumentParser() {
            super(Character.class);
        }

        @Override
        public Character parse(String argument) throws CommandParseException {
            if (argument.length() != 1) {
                throw new CommandParseException("Invalid character value: " + argument);
            }
            return argument.charAt(0);
        }
    }

    public static class EnumArgumentParser<T extends Enum<T>> extends ArgumentParser<T> {

        private final Class<T> enumClass;

        public EnumArgumentParser(Class<T> enumClass) {
            super(enumClass);
            this.enumClass = enumClass;
        }

        @Override
        public T parse(String argument) throws CommandParseException {
            try {
                return Enum.valueOf(enumClass, argument.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommandParseException("Invalid enum value: " + argument);
            }
        }
    }
    
    public static class LocalDateParser extends ArgumentParser<LocalDate> {

        public LocalDateParser() {
            super(LocalDate.class);
        }

        @Override
        public LocalDate parse(String argument) throws CommandParseException {
            try {
                return LocalDate.parse(argument);
            } catch (DateTimeParseException e) {
                throw new CommandParseException("Invalid date format: " + argument);
            }
        }
    }
    
    public static class LocalDateTimeParser extends ArgumentParser<LocalDateTime> {

        public LocalDateTimeParser() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime parse(String argument) throws CommandParseException {
            try {
                return LocalDateTime.parse(argument);
            } catch (DateTimeParseException e) {
                throw new CommandParseException("Invalid date-time format: " + argument);
            }
        }
    }
    
    public static class LocalTimeParser extends ArgumentParser<LocalTime> {

        public LocalTimeParser() {
            super(LocalTime.class);
        }

        @Override
        public LocalTime parse(String argument) throws CommandParseException {
            try {
                return LocalTime.parse(argument);
            } catch (DateTimeParseException e) {
                throw new CommandParseException("Invalid time format: " + argument);
            }
        }
    }
    
    public static class InstantParser extends ArgumentParser<Instant> {

        public InstantParser() {
            super(Instant.class);
        }

        @Override
        public Instant parse(String argument) throws CommandParseException {
            try {
                return Instant.parse(argument);
            } catch (DateTimeParseException e) {
                throw new CommandParseException("Invalid instant format: " + argument);
            }
        }
    }

    public static class OffsetDateTimeParser extends ArgumentParser<OffsetDateTime> {

        public OffsetDateTimeParser() {
            super(OffsetDateTime.class);
        }

        @Override
        public OffsetDateTime parse(String argument) throws CommandParseException {
            try {
                return OffsetDateTime.parse(argument);
            } catch (DateTimeParseException e) {
                throw new CommandParseException("Invalid offset date-time format: " + argument);
            }
        }
    }
    
    public static class ZoneIdParser extends ArgumentParser<ZoneId> {

        public ZoneIdParser() {
            super(ZoneId.class);
        }

        @Override
        public ZoneId parse(String argument) throws CommandParseException {
            try {
                return ZoneId.of(argument);
            } catch (java.time.DateTimeException e) {
                throw new CommandParseException("Invalid zone ID format: " + argument);
            }
        }
    }
    
    public static class ZoneOffsetParser extends ArgumentParser<ZoneOffset> {

        public ZoneOffsetParser() {
            super(ZoneOffset.class);
        }

        @Override
        public ZoneOffset parse(String argument) throws CommandParseException {
            try {
                return ZoneOffset.of(argument);
            } catch (java.time.DateTimeException e) {
                throw new CommandParseException("Invalid zone offset format: " + argument);
            }
        }
    }
    
    public static class DurationParser extends ArgumentParser<Duration> {

        public DurationParser() {
            super(Duration.class);
        }

        @Override
        public Duration parse(String argument) throws CommandParseException {
            try {
                return Duration.parse(argument);
            } catch (java.time.format.DateTimeParseException e) {
                throw new CommandParseException("Invalid duration format: " + argument);
            }
        }
    }
    
    public static class PeriodParser extends ArgumentParser<Period> {

        public PeriodParser() {
            super(Period.class);
        }

        @Override
        public Period parse(String argument) throws CommandParseException {
            try {
                return Period.parse(argument);
            } catch (java.time.format.DateTimeParseException e) {
                throw new CommandParseException("Invalid period format: " + argument);
            }
        }
    }
    
    public static class BigDecimalParser extends ArgumentParser<BigDecimal> {

        public BigDecimalParser() {
            super(BigDecimal.class);
        }

        @Override
        public BigDecimal parse(String argument) throws CommandParseException {
            try {
                return new BigDecimal(argument);
            } catch (NumberFormatException e) {
                throw new CommandParseException("Invalid decimal format: " + argument);
            }
        }
    }
    
    public static class BigIntegerParser extends ArgumentParser<BigInteger> {

        public BigIntegerParser() {
            super(BigInteger.class);
        }

        @Override
        public BigInteger parse(String argument) throws CommandParseException {
            try {
                return new BigInteger(argument);
            } catch (NumberFormatException e) {
                throw new CommandParseException("Invalid big integer format: " + argument);
            }
        }
    }
    
    public static class URLParser extends ArgumentParser<URL> {

        public URLParser() {
            super(URL.class);
        }

        @Override
        public URL parse(String argument) throws CommandParseException {
            try {
                return new URL(argument);
            } catch (java.net.MalformedURLException e) {
                throw new CommandParseException("Invalid URL format: " + argument);
            }
        }
    }
    
    public static class URIParser extends ArgumentParser<URI> {

        public URIParser() {
            super(URI.class);
        }

        @Override
        public URI parse(String argument) throws CommandParseException {
            try {
                return new URI(argument);
            } catch (URISyntaxException e) {
                throw new CommandParseException("Invalid URI format: " + argument);
            }
        }
    }
}
