package operations;

/**
 * Abstract decorator, wraps an existing op and adds behavior to it
 */
public abstract class OperationDecorator implements ComplexOperation {

    protected final ComplexOperation wrapped;

    protected OperationDecorator(ComplexOperation wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getSymbol() {
        return wrapped.getSymbol();
    }
}
