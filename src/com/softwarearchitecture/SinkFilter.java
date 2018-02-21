package com.softwarearchitecture;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SinkFilter extends FilterTemplate {

    private Calendar timeStamp = Calendar.getInstance();
    private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy:dd:hh:mm:ss");

    private String fileName = null;
    private String stringFormat = "%-20s %-20s %-20s %-20s %-20s %-20s %n";

    private int nFrame = 0;
    private String speed, temperature, altitude, pressure, pitch;

    public SinkFilter(String fileName) {
        this.fileName = fileName;
    }

    public void run() {
        try {
            File file = new File(fileName);

            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(String.format(stringFormat,
                    "Time:", "Speed(Knots):", "Altitude(Feet):", "Pressure(psi):", "Temperature(F):", "Pitch():"));
            printWriter.flush();

            System.out.print("\n" + this.getName() + "::Sink Reading");

            while (true) {
                try {
                    readNextPairValues(0);

                    if (id == Utils.TIME_ID) {
                        timeStamp.setTimeInMillis(measurement);
                        nFrame++;
                    } else if (id == Utils.TEMPERATURE_ID) {
                        temperature = String.valueOf(Double.longBitsToDouble(measurement));
                    } else if (id == Utils.ALTITUDE_ID) {
                        altitude = String.valueOf(Double.longBitsToDouble(measurement));
                    } else if (id == Utils.PRESSURE_ID) {
                        pressure = String.valueOf(Double.longBitsToDouble(measurement));
                    } else if (id == Utils.PITCH_ID) {
                        pitch = String.valueOf(Double.longBitsToDouble(measurement));
                    } else if (id == Utils.SPEED_ID) {
                        speed = String.valueOf(Double.longBitsToDouble(measurement));
                    }

                    if (id == Utils.TEMPERATURE_ID) {
                        System.out.println("\n" + this.getName() + "::Sink Writing" + "\n");
                        printWriter.write(String.format(stringFormat, timeStampFormat.format(timeStamp.getTime()),
                                speed, altitude, pressure, temperature, pitch));
                        printWriter.flush();
                    }
                } catch (EndOfStreamException e) {
                    ClosePorts();
                    System.out.print("\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesRead
                            + " bytes written: " + bytesWritten);
                    break;
                }
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
