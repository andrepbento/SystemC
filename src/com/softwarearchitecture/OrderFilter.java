package com.softwarearchitecture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderFilter extends FilterTemplate {

    private boolean isFirstRead = false;
    private int pipeIndexAux = 0;
    private boolean hasReadAllPipes = false;

    private ArrayList<Map<Integer, Long>> dataValuesArray = new ArrayList<>();

    @Override
    public void run() {
        for (int i = 0; i < getPipedInputStreamCount(); i++) {
            dataValuesArray.add(new HashMap<>());
        }

        while (true) {
            try {
                while (true) {
                    if (id == Utils.PITCH_ID) {
                        id = 0;
                        break;
                    }

                    readNextPairValues(pipeIndexAux);

                    dataValuesArray.get(pipeIndexAux).put(id, measurement);
                }
            } catch (EndOfStreamException e) {
                e.printStackTrace();
                dataValuesArray.remove(pipeIndexAux);
                return;
            }

            if (!hasReadAllPipes && pipeIndexAux < getPipedInputStreamCount() - 1) {
                pipeIndexAux++;
                continue;
            }

            hasReadAllPipes = true;

            // Obtain the smaller frame values
            Map<Integer, Long> minDataValuesArray = dataValuesArray.get(0);
            if (minDataValuesArray == null)
                return;

            for (int i = 1; i < dataValuesArray.size(); i++) {
                Map<Integer, Long> mapAux = dataValuesArray.get(i);
                Double firstValue = Double.longBitsToDouble(minDataValuesArray.get(Utils.TIME_ID));
                Double secondValue = Double.longBitsToDouble(mapAux.get(Utils.TIME_ID));
                if (secondValue < firstValue) {
                    minDataValuesArray = mapAux;
                }
                pipeIndexAux = dataValuesArray.indexOf(minDataValuesArray);
            }

            // Write the smaller frame to the output pipe stream
            for (Integer i : minDataValuesArray.keySet())
                writePairValuesToStream(i, minDataValuesArray.get(i));

            // Clear map data
            dataValuesArray.get(pipeIndexAux).clear();
        }
    }
}
