package com.cpp.project78.codeWriter;

import java.io.IOException;

public interface CodeWriter {
    void setFileName(String fileName);

    void writeArithmetic(String command) throws IOException;

    void writePushPop(String command, String segment, int index) throws IOException;

    void close() throws IOException;

    void writeInit() throws IOException;

    void writeLabel(String label) throws IOException;

    void writeGoto(String label) throws IOException;

    void writeIf(String label) throws IOException;

    void writeCall(String functionName, int numArgs) throws IOException;

    void writeReturn() throws IOException;

    void writeFunction(String functionName, int numLocals) throws IOException;
}
