package enterprises.iwakura.parsers;

import enterprises.iwakura.arguments.SomeArg;
import enterprises.iwakura.ganyu.ArgumentParser;
import enterprises.iwakura.ganyu.exception.CommandParseException;

public class SomeArgParser extends ArgumentParser<SomeArg> {

    public SomeArgParser() {
        super(SomeArg.class);
    }

    @Override
    public SomeArg parse(String argument) throws CommandParseException {
        return new SomeArg(argument);
    }
}
