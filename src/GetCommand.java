public class GetCommand implements Command {
    private final String id;

    public GetCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return "GET";
    }
}
