public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.isEmpty()) {
            return new UnknownCommand();
        }

        // 3 parçaya kadar böl: cmd id mesaj
        String[] parts = line.split(" ", 3);

        // hiç parça yoksa
        if (parts.length == 0) {
            return new UnknownCommand();
        }

        String cmd = parts[0].trim().toUpperCase();

        if (cmd.equals("SET") && parts.length == 3) {
            return new SetCommand(parts[1], parts[2]);
        }

        if (cmd.equals("GET") && parts.length == 2) {
            return new GetCommand(parts[1]);
        }

        return new UnknownCommand();
    }
}

