package com.cpp.project78.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParserImpl implements Parser {
    private final CommandCleanser commandCleanser;
    private List<String> commandsList;
    private int commandsListCounter;

    public ParserImpl(String fileName) throws IOException {
        this.commandsListCounter = 0;
        this.commandCleanser = new CommandCleanserImpl();

        this.readFile(fileName);
    }

    @Override
    public boolean hasMoreCommands() {
        return this.commandsListCounter < this.commandsList.size();
    }

    @Override
    public void advance() {
        this.commandsListCounter++;
    }

    @Override
    public String commandType() {
        String[] splitCommand = this.splitCurrentCommand();
        if (splitCommand.length == 0) {
            throw new IllegalArgumentException("Invalid command");
        }

        try {
            Command command = Command.fromValue(splitCommand[0].toLowerCase());

            return switch (command) {
                case Command.add, Command.sub, Command.neg, Command.eq, Command.gt, Command.lt, Command.and, Command.or,
                     Command.not -> CommandType.C_ARITHMETIC.toString();
                case Command.push -> CommandType.C_PUSH.toString();
                case Command.pop -> CommandType.C_POP.toString();
                case Command.label -> CommandType.C_LABEL.toString();
                case Command._goto -> CommandType.C_GOTO.toString();
                case Command.if_goto -> CommandType.C_IF.toString();
                case Command.function -> CommandType.C_FUNCTION.toString();
                case Command.call -> CommandType.C_CALL.toString();
                case Command._return -> CommandType.C_RETURN.toString();
                default -> throw new IllegalStateException("Unexpected value: " + command);
            };
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid command", e);
        }
    }

    @Override
    public String arg1() {
        if (Objects.equals(this.commandType(), CommandType.C_RETURN.toString())) {
            throw new IllegalStateException("Can not be invoked when command type is C_RETURN");
        }

        String[] splitCommand = this.splitCurrentCommand();
        if (splitCommand.length == 0) {
            throw new IllegalArgumentException("Invalid command");
        }

        if (Objects.equals(this.commandType(), CommandType.C_ARITHMETIC.toString())) {
            return splitCommand[0];
        }

        if (splitCommand.length < 2) {
            throw new IllegalArgumentException("Invalid command");
        }

        return splitCommand[1];
    }

    @Override
    public int arg2() {
        if (!Objects.equals(this.commandType(), CommandType.C_PUSH.toString()) &&
                !Objects.equals(this.commandType(), CommandType.C_POP.toString()) &&
                !Objects.equals(this.commandType(), CommandType.C_FUNCTION.toString()) &&
                !Objects.equals(this.commandType(), CommandType.C_CALL.toString())) {
            throw new IllegalStateException("Can not be invoked when command type is other than C_PUSH, C_POP, C_FUNCTION, C_CALL");
        }

        String[] splitCommand = this.splitCurrentCommand();
        if (splitCommand.length < 3) {
            throw new IllegalArgumentException("Invalid command");
        }

        return Integer.parseInt(splitCommand[2]);
    }

    private void readFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new InvalidParameterException("Unable to read file: " + fileName);
        }

        try {
            commandsList = Files.readAllLines(file.toPath());
        } catch (Exception e) {
            throw new IOException("Unable to read file: " + fileName, e);
        }

        this.cleanseCommandsList();
    }

    private void cleanseCommandsList() {
        List<String> cleansedCommandsList = new ArrayList<>();

        for (String command : commandsList) {
            String currentCommand = this.commandCleanser.CleanseCommand(command);
            if (!currentCommand.isEmpty()) {
                cleansedCommandsList.add(currentCommand);
            }
        }

        this.commandsList = cleansedCommandsList;
    }

    private String getCurrentCommand() {
        return this.commandsList.get(this.commandsListCounter);
    }

    private String[] splitCurrentCommand() {
        // after cleansing, a command will only be spaced by a single space on a line
        return this.getCurrentCommand().split(" ");
    }
}
