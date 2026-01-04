public class SetCommand implements Command {
    private final String id;
    private final String message;

    public SetCommand(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getType() {
        return "SET";
    }
}
