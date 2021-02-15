package uk.ac.cam.kpw29;

import java.util.ArrayList;
import java.util.Random;

public class Assignment {
    private ArrayList<Integer> mapping;
    private ArrayList<ArrayList<Integer> > nodesMappedToCore;

    public Assignment(int N, int N_CORES) {
        nodesMappedToCore = new ArrayList<>();
        for (int i=0; i<N_CORES; ++i) {
            nodesMappedToCore.add(new ArrayList<>());
        }

        Random r = new Random(2137);
        mapping = new ArrayList<Integer>(N);
        for (int i=0; i<N; ++i) {
            // assign randomly to one of the cores
            int core = r.nextInt(N_CORES);
            mapping.add(core);
            nodesMappedToCore.get(core).add(i);
        }
    }

    public int getCore(int pos) {
        return mapping.get(pos);
    }

    public ArrayList<Integer> getAssigned(int core) {
        return nodesMappedToCore.get(core);
    }
}