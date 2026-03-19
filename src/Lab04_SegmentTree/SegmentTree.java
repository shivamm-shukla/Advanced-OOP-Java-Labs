package Lab04_SegmentTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SegmentTree {

    static class RangeSumST {

        List<Integer> inputData;
        List<Integer> STList;
        int paddedSize;

        RangeSumST(List<Integer> inputData) {
            this.inputData = inputData;
            this.paddedSize = getNextPower(inputData.size());

            STList = new ArrayList<>(Collections.nCopies(2 * paddedSize, 0));

            BuildST();
        }

        public int getNextPower(int n) {
            int k = 1;
            while (k < n) {
                k = k * 2;
            }
            return k;
        }

        void BuildST() {
            buildHelper(1);
        }

        void buildHelper(int index) {

            // Only real data leaves
            if (index >= paddedSize && index < paddedSize + inputData.size()) {
                STList.set(index, inputData.get(index - paddedSize));
                return;
            }

            // Skip padded leaves (already 0)
            if (index >= paddedSize) {
                return;
            }

            // Internal nodes
            buildHelper(2 * index);
            buildHelper(2 * index + 1);

            STList.set(index,
                    STList.get(2 * index) + STList.get(2 * index + 1));
        }


    }

    public static void main(String[] args) {
        List<Integer> inputData = new ArrayList<>(Arrays.asList(2, 4, 10, -7,8));

        RangeSumST s = new RangeSumST(inputData);
        System.out.println(s.STList);
    }
}

