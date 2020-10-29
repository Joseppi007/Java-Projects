/*
 * An AsyncFunc is used in a Hex, or any SelfType, for click events, as well as other events.
 * I did this so that I can make functions act like varubles.
 */

/**
 *
 * @author joeyn
 * @param <ArgType> This is the data that is passed into the function, besides self. Ex: MouseEvent for a click event
 * @param <SelfType> The type of self
 * @param <ReturnType> What to return
 */
public interface AsyncFunc <ArgType, SelfType, ReturnType> {
    public ReturnType func(ArgType e, SelfType self);
}
