package com.cpp.project78.parser;

public class CommandCleanserImpl implements CommandCleanser {
    public String CleanseCommand(String command) {
        command = this.removeComments(command);
        return this.trimCommand(command);
    }

    private String removeComments(String command) {
        command = this.trimCommand(command);

        if (this.hasComments(command)) {
            if (command.charAt(0) == '/') {
                return "";
            }

            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 0; i < command.length(); i++) {
                if (command.charAt(i) == '/') {
                    break;
                }
                commandBuilder.append(command.charAt(i));
            }
            return commandBuilder.toString();
        }

        return command;
    }

    private boolean hasComments(String command) {
        return command.contains("//");
    }

    private String trimCommand(String command) {
        String trimmedCommand = command.trim();
        // removing extra spaces in a single line command
        String[] splitCommand = trimmedCommand.split("\\s+");

        return String.join(" ", splitCommand);
    }
}
