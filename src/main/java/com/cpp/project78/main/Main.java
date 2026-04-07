package com.cpp.project78.main;

import com.cpp.project78.codeWriter.CodeWriter;
import com.cpp.project78.codeWriter.CodeWriterImpl;
import com.cpp.project78.parser.Command;
import com.cpp.project78.parser.CommandType;
import com.cpp.project78.parser.Parser;
import com.cpp.project78.parser.ParserImpl;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

public class Main {
    private final CodeWriter codeWriter;
    private final String[] fileNames;
    private String sourcePath;

    public Main(String name) throws IOException {
        File file = new File(name);
        String targetFileName;

        if (file.isFile()) {
            targetFileName = FilenameUtils.getPath(name) + FilenameUtils.getBaseName(name) + ".asm";
            fileNames = new String[]{FilenameUtils.getName(name)};
            sourcePath = FilenameUtils.getPath(name);
        } else if (file.isDirectory()) {
            Path path = Paths.get(name);
            targetFileName = path.resolve(path.getName(path.getNameCount() - 1) + ".asm").toString();

            fileNames = file.list();
            sourcePath = file.getPath();
        } else {
            throw new InvalidParameterException("Unable to read path: " + name);
        }

        if (!sourcePath.endsWith("/")) {
            sourcePath += "/";
        }
        codeWriter = new CodeWriterImpl(targetFileName);
    }

    public void compile() throws IOException {
        for (String fileName : this.fileNames) {
            Parser parser = new ParserImpl(sourcePath + fileName);
            codeWriter.setFileName(fileName);

            while (parser.hasMoreCommands()) {
                CommandType cmd = CommandType.valueOf(parser.commandType());

                switch (cmd) {
                    case CommandType.C_ARITHMETIC -> codeWriter.writeArithmetic(parser.arg1());
                    case CommandType.C_PUSH ->
                            codeWriter.writePushPop(Command.push.toString(), parser.arg1(), parser.arg2());
                    case CommandType.C_POP ->
                            codeWriter.writePushPop(Command.pop.toString(), parser.arg1(), parser.arg2());
                    default -> throw new IllegalStateException("Unexpected value: " + cmd);
                }

                parser.advance();
            }
        }

        codeWriter.close();
    }
}
