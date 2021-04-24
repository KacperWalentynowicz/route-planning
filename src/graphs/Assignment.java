package graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.sqrt;

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

    public Assignment(Graph g, int N_CORES) { //assigns graph g based on geocoded data
        nodesMappedToCore = new ArrayList<>();
        for (int i=0; i<N_CORES; ++i) {
            nodesMappedToCore.add(new ArrayList<>());
        }

        mapping = new ArrayList<Integer>(g.N);
        ArrayList<Float> coordsX = g.getXCoords();
        ArrayList<Float> coordsY = g.getYCoords();

        ArrayList<Float> sortedX = new ArrayList<>();
        Collections.addAll(coordsX);

        ArrayList<Float> sortedY = new ArrayList<>();
        Collections.addAll(coordsY);

        Collections.sort(sortedX);
        Collections.sort(sortedY);

        int N_ROW = (int)sqrt(N_CORES);
        int BARRIER = g.N / N_ROW;
        if (BARRIER * N_ROW < g.N) ++BARRIER;

        for (int i=0; i<g.N; ++i) {
            int pos_x = Collections.binarySearch(sortedX, coordsX.get(i));
            int pos_y = Collections.binarySearch(sortedY, coordsY.get(i));

            int range_x = pos_x / BARRIER;
            int range_y = pos_y / BARRIER;
            // this should be mapped to core with coordinates (range_x, range_y)

            int core_assigned = range_x * N_ROW + range_y;
            assert(0 <= core_assigned);
            assert(core_assigned < N_CORES);

            mapping.add(core_assigned);
            nodesMappedToCore.get(core_assigned).add(i);
        }
    }

    public int getCore(int pos) {
        return mapping.get(pos);
    }

    public ArrayList<Integer> getAssigned(int core) {
        return nodesMappedToCore.get(core);
    }
}