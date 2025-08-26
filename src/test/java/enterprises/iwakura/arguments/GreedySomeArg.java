package enterprises.iwakura.arguments;

import enterprises.iwakura.ganyu.annotation.GreedyArgument;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@GreedyArgument
@AllArgsConstructor
public class GreedySomeArg {

    private String stringValue;

}
