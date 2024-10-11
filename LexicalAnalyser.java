import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyser {

    private enum State {
        START, ZERO, NONZERONUMBER, STARTDECIMAL, ENDDECIMAL, WHITESPACE, STARTOPERATOR,
    }

    public static List<Token> analyse(String input) throws NumberException, ExpressionException {
        ArrayList<Token> output = new ArrayList<Token>(); // stores the tokens for a valid output
        StringBuilder buffer = new StringBuilder(); // used for flexible storage
        State state = State.START; // set the entry point of the program
        int length = input.length(); // length of the input
        String nonzero = "123456789";

        // iterates through characters in the input string
        for (int i = 0; i < length; i++) {
            // variable to represent current chat
            char currentChar = input.charAt(i);
            // performs different actions based on the state variable
            switch (state) {
                case START:
                    if (nonzero.contains(String.valueOf(currentChar))) {
                        buffer.append(currentChar);
                        state = State.NONZERONUMBER;
                    } else if (currentChar == '0') {
                        buffer.append(currentChar);
                        state = State.ZERO;
                    } else if ("+-/*".contains(String.valueOf(currentChar))) {
                        // example exception
                        throw new ExpressionException("Cannot start with operator: " + currentChar);
                    } else if (Character.isWhitespace(currentChar)) {
                        throw new ExpressionException("Cannot start with whitespace");
                    } else if (currentChar == '.') {
                        throw new NumberException("Cannot start with decimal");
                    }
                    break;

                case NONZERONUMBER:
                    if ("+-/*".contains(String.valueOf(currentChar))) {
                        output.add(new Token(Double.parseDouble(buffer.toString()))); // add the buffer
                        output.add(new Token(Token.typeOf(currentChar))); // add the operator
                        buffer = new StringBuilder(); // reset buffer
                        state = State.STARTOPERATOR;
                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.WHITESPACE;
                    } else if (currentChar == '.') {
                        throw new NumberException("Only zero can have a decimal");
                    } else if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                    }
                    break;

                case STARTOPERATOR:
                    if (nonzero.contains(String.valueOf(currentChar))) {
                        buffer.append(currentChar);
                        state = State.NONZERONUMBER; // go back to previous state
                    } else if (".+-/*".contains(String.valueOf(currentChar))) {
                        throw new ExpressionException("Cannot have consecutive operators");
                    } else if (currentChar == '0') {
                        buffer.append(currentChar);
                        state = State.ZERO;
                    }
                    // do nothing if its whitespace
                    break;

                case WHITESPACE:
                    if (".+-/*".contains(String.valueOf(currentChar))) {
                        output.add(new Token(Double.parseDouble(buffer.toString()))); // add the buffer
                        output.add(new Token(Token.typeOf(currentChar))); // add the operator
                        buffer = new StringBuilder(); // reset buffer
                        state = State.STARTOPERATOR;
                    } else if (Character.isDigit(currentChar)) {
                        throw new ExpressionException("Cannot have whitespace between numbers");
                    } else if (currentChar == '.') {
                        throw new NumberException("No leading 0 with decimal");

                    }
                    // do nothing with whitespace
                    break;

                case ZERO:
                    if (Character.isDigit(currentChar)) {
                        throw new NumberException("Leading zero must be followed by decimal");
                    } else if (currentChar == '.') {
                        buffer.append(currentChar);
                        state = State.STARTDECIMAL;
                    } else if ("+-/*".contains(String.valueOf(currentChar))) {
                        output.add(new Token(Double.parseDouble(buffer.toString()))); // add the buffer
                        output.add(new Token(Token.typeOf(currentChar))); // add the operator
                        buffer = new StringBuilder();
                        state = State.STARTOPERATOR;
                    }
                    break;
                case STARTDECIMAL:
                    if ("+-/*".contains(String.valueOf(currentChar)) || Character.isWhitespace(currentChar)) {
                        throw new ExpressionException("No digits following leading 0.");
                    } else if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                        state = State.ENDDECIMAL;
                    } else if (currentChar == '.') {
                        throw new NumberException("Cannot repeat decimal");
                    }
                    break;

                case ENDDECIMAL:
                    if (currentChar == '.') {
                        throw new NumberException("Cannont have more than one decimal in same number");
                    } else if ("+-/*".contains(String.valueOf(currentChar))) {
                        output.add(new Token(Double.parseDouble(buffer.toString()))); // add the buffer
                        output.add(new Token(Token.typeOf(currentChar))); // add the operator
                        state = State.STARTOPERATOR;
                    } else if (Character.isWhitespace(currentChar)) {
                        state = State.WHITESPACE;
                    } else if (Character.isDigit(currentChar)) {
                        buffer.append(currentChar);
                    }
                    break;
                default:
                    break;
            }
        }

        switch (state) {
            case NONZERONUMBER:
            case ENDDECIMAL:
            case ZERO:
                if (buffer.length() != 0) {
                    output.add(new Token(Double.parseDouble(buffer.toString()))); // if the buffer still had stuff in it
                    return output;
                }
            case STARTDECIMAL:
                throw new NumberException("Cannot end in a decimal");
            case STARTOPERATOR:
                throw new ExpressionException("Cannot end in operator");
            case WHITESPACE:
                throw new ExpressionException("Cannot end in whitespace");
            case START:
                throw new ExpressionException("Cannot be empty");
            default:
                throw new ExpressionException("Dont know how you made it here");

        }

    }
}

