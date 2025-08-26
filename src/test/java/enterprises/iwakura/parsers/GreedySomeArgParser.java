package enterprises.iwakura.parsers;

import enterprises.iwakura.arguments.GreedySomeArg;
import enterprises.iwakura.arguments.SomeArg;
import enterprises.iwakura.ganyu.ArgumentParser;
import enterprises.iwakura.ganyu.exception.CommandParseException;

public class GreedySomeArgParser extends ArgumentParser<GreedySomeArg> {

    public GreedySomeArgParser() {
        super(GreedySomeArg.class);
    }

    @Override
    public GreedySomeArg parse(String argument) throws CommandParseException {
        return new GreedySomeArg(argument);
    }
}
