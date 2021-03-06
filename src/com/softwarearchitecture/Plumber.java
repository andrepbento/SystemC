package com.softwarearchitecture;

import java.util.Arrays;

public class Plumber {

    public static void main(String argv[]) {

        SourceFilter dataSourceA = new SourceFilter("SubSetA.dat");
        SourceFilter dataSourceB = new SourceFilter("SubSetB.dat");

        OrderFilter orderFilter = new OrderFilter();

        SinkFilter sinkFilter = new SinkFilter("Output.dat");

        orderFilter.Connect(dataSourceA);
        orderFilter.Connect(dataSourceB);
        sinkFilter.Connect(orderFilter);

        dataSourceA.start();
        dataSourceB.start();
        orderFilter.start();
        sinkFilter.start();
    }
}
