package be.kwakeroni.evelyn.model;

public class RuntimeParseException extends RuntimeException {

    public RuntimeParseException(ParseException cause) {
        super(cause);
    }

    @Override
    public synchronized ParseException getCause() {
        return (ParseException) super.getCause();
    }
}
