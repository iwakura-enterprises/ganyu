package enterprises.iwakura.ganyu;

import enterprises.iwakura.ganyu.annotation.GreedyArgument;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines a command argument.
 */
@Getter
@Setter
public class CommandArgumentDefinition {

    private String name;
    private String longName;
    private String parameterName;
    private String description;
    private boolean injectable;
    private boolean mandatory;
    private Class<?> type;
    private int index;

    /**
     * Whether this argument is mandatory. Primitive types are always considered mandatory.
     *
     * @return true if the argument is mandatory, false otherwise.
     */
    public boolean isMandatory() {
        return mandatory || type.isPrimitive();
    }

    /**
     * Whether this argument is greedy (is annotated with {@link GreedyArgument}). Greedy arguments consume all remaining input. String types are always considered greedy.
     *
     * @return true if the argument is greedy, false otherwise.
     */
    public boolean isGreedy() {
        return type == String.class || type.isAnnotationPresent(GreedyArgument.class);
    }
}
