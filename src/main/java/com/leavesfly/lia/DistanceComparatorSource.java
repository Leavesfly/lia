package com.leavesfly.lia;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

public class DistanceComparatorSource extends FieldComparatorSource {

    private static final long serialVersionUID = 1L;
    private int x;
    private int y;

    public DistanceComparatorSource(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public FieldComparator newComparator(String arg0, int arg1, int arg2, boolean arg3)
            throws IOException {
        // TODO Auto-generated method stub
        return new DistanceSourceLookupComparator(arg0, arg1);
    }

    private class DistanceSourceLookupComparator extends FieldComparator {
        private int[] xDoc, yDoc;
        private float[] values;
        private float bottom;
        private String fieldName;

        public DistanceSourceLookupComparator(String fieldName, int numHits) {
            values = new float[numHits];
            this.fieldName = fieldName;
        }

        @Override
        public int compare(int arg0, int arg1) {

            if (values[arg0] > values[arg1])
                return 1;
            if (values[arg0] < values[arg1])
                return -1;
            return 0;
        }

        private float getDistance(int doc) {
            int deltax = Math.abs(xDoc[doc] - x);
            int deltay = Math.abs(yDoc[doc] - y);
            return (float) Math.sqrt(deltax * deltax + deltay * deltay);
        }

        @Override
        public int compareBottom(int arg0) throws IOException {

            float distance = getDistance(arg0);
            if (bottom < distance)
                return -1;
            if (bottom > distance)
                return 1;
            return 0;
        }

        @Override
        public void copy(int arg0, int arg1) throws IOException {

            values[arg0] = getDistance(arg1);
        }

        @Override
        public void setBottom(int arg0) {

            bottom = values[arg0];
        }

        @Override
        public void setNextReader(IndexReader arg0, int arg1) throws IOException {
            String[] temp = FieldCache.DEFAULT.getStrings(arg0, "location");
            xDoc = new int[temp.length];
            yDoc = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                String[] str = temp[i].split(",");
                xDoc[i] = Integer.parseInt(str[0]);
                yDoc[i] = Integer.parseInt(str[1]);
            }
        }

        @Override
        public Comparable<?> value(int arg0) {
            return new Float(values[arg0]);
        }

        public String toString() {
            return "Distance from (" + x + "," + y + ")";
        }
    }

}
