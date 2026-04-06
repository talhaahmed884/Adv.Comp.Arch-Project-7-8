package com.cpp.project78.codeWriter;

import java.io.IOException;

public interface CodeWriter {
    void setFileName(String fileName);

    void writeArithmetic(String command) throws IOException;

    void writePushPop(String command, String segment, int index) throws IOException;

    void close() throws IOException;
}
