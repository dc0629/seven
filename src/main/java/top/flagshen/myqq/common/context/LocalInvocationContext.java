package top.flagshen.myqq.common.context;


/**
 *
 * @author dengchao
 */
public class LocalInvocationContext {

    private LocalInvocationContext() {
    }

    private static final ThreadLocal<InvocationContext> T_CONTEXTS = new ThreadLocal<>();

    /**
     * Gets invocation.
     *
     * @param <T> the type parameter
     * @return the invocation
     */
    @SuppressWarnings("unchecked")
    public static <T extends InvocationContext> T getContext() {
        return (T) T_CONTEXTS.get();
    }

    /**
     * Bind invocation.
     *
     * @param context the invocation
     */
    public static void bindContext(InvocationContext context) {
        T_CONTEXTS.set(context);
    }

    /**
     * Unbind invocation.
     */
    public static void unbindContext() {
        T_CONTEXTS.remove();
    }
}
