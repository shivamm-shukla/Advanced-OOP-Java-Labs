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

        // Task 1: Build Segement Tree
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


        // Task 2: Range find Querry

        public int findSum(int start, int end) {
            if (start > end) return 0;

            return queryHelper(start, end, 0, paddedSize - 1, 1);
        }

        private int queryHelper(int l, int r, int start, int end, int node) {

            // Case 1: Complete overlap
            if (l <= start && r >= end) {
                return STList.get(node);
            }

            // Case 2: No overlap
            if (r < start || l > end) {
                return 0;
            }

            // Case 3: Partial overlap
            int mid = start + (end - start) / 2;

            int leftSum = queryHelper(l, r, start, mid, 2 * node);
            int rightSum = queryHelper(l, r, mid + 1, end, 2 * node + 1);

            return leftSum + rightSum;
        }

        // Task 2: Update Querry
        public void update(int idx, int val) {

            // Update input array
            inputData.set(idx, val);

            // Go to leaf
            int STidx = paddedSize + idx;

            // Update leaf
            STList.set(STidx, val);

            // Update ancestors
            while (STidx > 1) {
                STidx = STidx / 2;

                STList.set(STidx,
                        STList.get(2 * STidx) + STList.get(2 * STidx + 1));
            }
        }


    }

    public static void main(String[] args) {
        List<Integer> inputData = new ArrayList<>(Arrays.asList(2, 4, 10, -7,8));

        RangeSumST s = new RangeSumST(inputData);
        System.out.println(s.STList);

        s.update(1,5);
        System.out.println(s.STList);
        System.out.println(inputData);
    }
}

