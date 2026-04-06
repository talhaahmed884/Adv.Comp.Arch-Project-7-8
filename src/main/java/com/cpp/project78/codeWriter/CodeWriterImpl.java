package com.cpp.project78.codeWriter;

import com.cpp.project78.parser.Command;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriterImpl implements CodeWriter {
    private final BufferedWriter writer;
    private String currentFileName;
    private int comparisonLabelCounter;


    public CodeWriterImpl(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName));
        currentFileName = "";
        comparisonLabelCounter = 0;
    }

    @Override
    public void setFileName(String fileName) {
        this.currentFileName = fileName;
    }

    @Override
    public void writeArithmetic(String command) throws IOException {
        writer.write("@SP\n");

        Command cmd = Command.valueOf(command.toLowerCase());
        switch (cmd) {
            case Command.add, Command.sub, Command.and, Command.or, Command.eq, Command.gt, Command.lt -> {
                writer.write("AM=M-1\n");
                writer.write("D=M\n");
                writer.write("A=A-1\n");
            }
            case Command.neg, Command.not -> writer.write("A=A-1\n");
        }

        switch (cmd) {
            case Command.add -> writer.write("M=D+M\n");
            case Command.sub -> writer.write("M=M-D\n");
            case Command.and -> writer.write("M=D&M\n");
            case Command.or -> writer.write("M=D|M\n");
            case Command.neg -> writer.write("M=-M\n");
            case Command.not -> writer.write("M=!M\n");
            case Command.eq -> this.writeComparisonOperators("JEQ");
            case Command.gt -> this.writeComparisonOperators("JGT");
            case Command.lt -> this.writeComparisonOperators("JLT");
            default -> throw new IllegalStateException("Unexpected value: " + cmd);
        }
    }

    @Override
    public void writePushPop(String command, String segment, int index) throws IOException {
        Command cmd = Command.valueOf(command.toLowerCase());
        Segment seg = Segment.fromValue(segment.toLowerCase());

        switch (cmd) {
            case Command.push -> this.writePush(seg, index);
            case Command.pop -> this.writePop(seg, index);
            default -> throw new IllegalStateException("Unexpected value: " + cmd);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            writer.close();
        } catch (Exception e) {
            throw new IOException("Unable to complete writing to file", e);
        }
    }

    private void writeComparisonOperators(String command) throws IOException {
        String comparisonLabelStart = "COMPARISON_LABEL_START_" + comparisonLabelCounter;
        String comparisonLabelEnd = "COMPARISON_LABEL_END_" + comparisonLabelCounter;
        comparisonLabelCounter++;

        writer.write("D=M-D\n");

        writer.write("@" + comparisonLabelStart + "\n");
        writer.write("D;" + command + "\n");

        writer.write("@SP\n");
        writer.write("A=M-1\n");
        writer.write("M=0\n");
        writer.write("@" + comparisonLabelEnd + "\n");
        writer.write("0;JMP\n");

        writer.write("(" + comparisonLabelStart + ")\n");
        writer.write("@SP\n");
        writer.write("A=M-1\n");
        writer.write("M=-1\n");

        writer.write("(" + comparisonLabelEnd + ")\n");
    }

    private void writePush(Segment seg, int index) throws IOException {
        switch (seg) {
            case Segment.local, Segment.argument, Segment._this, Segment.that ->
                    this.writeSegmentPush(seg.getCode(), index);
            case Segment.constant -> {
                writer.write("@" + index + "\n");
                writer.write("D=A\n");
            }
            case Segment.temp -> {
                writer.write("@" + (5 + index) + "\n");
                writer.write("D=M\n");
            }
            case Segment.pointer -> {
                if (index == 0) {
                    writer.write("@THIS\n");
                } else if (index == 1) {
                    writer.write("@THAT\n");
                } else {
                    throw new IllegalStateException("Unexpected value of Segment: " + seg + ", Value: " + index);
                }
                writer.write("D=M\n");
            }
            case Segment._static -> {
                writer.write("@" + currentFileName + "." + index + "\n");
                writer.write("D=M\n");
            }
            default -> throw new IllegalStateException("Unexpected value: " + seg);
        }

        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");
    }

    private void writeSegmentPush(String segmentCode, int index) throws IOException {
        writer.write("@" + segmentCode + "\n");
        writer.write("D=M\n");
        writer.write("@" + index + "\n");
        writer.write("A=D+A\n");
        writer.write("D=M\n");
    }

    private void writePop(Segment seg, int index) throws IOException {
        switch (seg) {
            case Segment.local, Segment.argument, Segment._this, Segment.that ->
                    this.writeSegmentPop(seg.getCode(), index);
            case Segment.temp -> {
                this.writeSegmentPopToDReg();
                writer.write("@" + (5 + index) + "\n");
                writer.write("M=D\n");
            }
            case Segment.pointer -> {
                if (index == 0) {
                    writer.write("@THIS\n");
                } else if (index == 1) {
                    writer.write("@THAT\n");
                } else {
                    throw new IllegalStateException("Unexpected value of Segment: " + seg + ", Value: " + index);
                }
                this.writeSegmentPopToDReg();
                writer.write("M=D\n");
            }
            case Segment._static -> {
                this.writeSegmentPopToDReg();
                writer.write("@" + currentFileName + "." + index + "\n");
                writer.write("M=D\n");
            }
            default -> throw new IllegalStateException("Unexpected value: " + seg);
        }
    }

    private void writeSegmentPop(String segmentCode, int index) throws IOException {
        writer.write("@" + segmentCode + "\n");
        writer.write("D=M\n");
        writer.write("@" + index + "\n");
        writer.write("D=D+A\n");
        writer.write("@R13\n");
        writer.write("M=D\n");
        this.writeSegmentPopToDReg();
        writer.write("@R13\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
    }

    private void writeSegmentPopToDReg() throws IOException {
        writer.write("@SP\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
    }
}
