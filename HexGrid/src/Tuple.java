/*
 * Two classes in one!
 */

/**
 *
 * @author joeyn
 * @param <First> The first of the two classes
 * @param <Second> The second of the two classes
 */
public class Tuple <First, Second> {
    private final First first;
    private final Second second;
    public First getFirst () {
        return first;
    }
    public Second getSecond () {
        return second;
    }
    public Tuple (First f, Second s) {
        first = f;
        second = s;
    }
}
