package com.softwarearchitecture;

abstract public class FilterTemplate extends FilterFramework {

    private byte dataByte;
    int bytesRead = 0;
    int bytesWritten = 0;

    long measurement;
    int id;
    private int i;

    public void readNextPairValues(int inputPipeIndex) throws EndOfStreamException {
        id = 0;

        for (i = 0; i < Utils.ID_LENGTH; i++) {
            dataByte = ReadFilterInputPort(inputPipeIndex);
            id = id | (dataByte & 0xFF);

            if (i != Utils.ID_LENGTH - 1) {
                id = id << 8;
            } // if

            bytesRead++;
        } // for

        measurement = 0;

        for (i = 0; i < Utils.MEASUREMENT_LENGTH; i++) {
            dataByte = ReadFilterInputPort(inputPipeIndex);
            measurement = measurement | (dataByte & 0xFF);    // We append the byte on to measurement...

            if (i != Utils.MEASUREMENT_LENGTH - 1)                    // If this is not the last byte, then slide the
            {                                                // previously appended byte to the left by one byte
                measurement = measurement << 8;                // to make room for the next byte we append to the
                // measurement
            } // if

            bytesRead++;                                    // Increment the byte count

        } // if
    }

    public void writePairValuesToStream() {
        for (byte b : Utils.intToBytes(id)) {
            WriteFilterOutputPort(b);
            bytesWritten++;
        }

        for (byte b : Utils.longToBytes(measurement)) {
            WriteFilterOutputPort(b);
            bytesWritten++;
        }
    }

    public void writePairValuesToStream(int id, long measurement) {
        for (byte b : Utils.intToBytes(id)) {
            WriteFilterOutputPort(b);
            bytesWritten++;
        }

        for (byte b : Utils.longToBytes(measurement)) {
            WriteFilterOutputPort(b);
            bytesWritten++;
        }
    }

    public abstract void run();
}
