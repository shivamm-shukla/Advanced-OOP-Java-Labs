package Lab04_SegmentTree;

import java.util.*;
import java.util.function.BinaryOperator;

public class SegmentTreeGen {

    static class GenericST<T> {

        List<T> inputData;
        List<T> STList;
        int paddedSize;
        T identity;
        BinaryOperator<T> mergeFn;

        GenericST(List<T> inputData, T identity, BinaryOperator<T> mergeFn) {
            this.inputData = new ArrayList<>(inputData);
            this.identity = identity;
            this.mergeFn = mergeFn;
            this.paddedSize = getNextPower(inputData.size());

            STList = new ArrayList<>(Collections.nCopies(2 * paddedSize, identity));

            buildST();
        }

        private int getNextPower(int n) {
            int k = 1;
            while (k < n) k *= 2;
            return k;
        }

        void buildST() {
            buildHelper(1);
        }

        void buildHelper(int index) {
            // Real data leaves
            if (index >= paddedSize && index < paddedSize + inputData.size()) {
                STList.set(index, inputData.get(index - paddedSize));
                return;
            }
            // Padded leaves — already identity, skip
            if (index >= paddedSize) return;

            // Internal nodes
            buildHelper(2 * index);
            buildHelper(2 * index + 1);
            STList.set(index, mergeFn.apply(STList.get(2 * index), STList.get(2 * index + 1)));
        }

        public T query(int start, int end) {
            if (start > end) return identity;
            return queryHelper(start, end, 0, paddedSize - 1, 1);
        }

        private T queryHelper(int l, int r, int start, int end, int node) {
            if (l <= start && r >= end) return STList.get(node);   // Complete overlap
            if (r < start || l > end)  return identity;            // No overlap

            int mid = start + (end - start) / 2;                   // Partial overlap
            T left  = queryHelper(l, r, start, mid, 2 * node);
            T right = queryHelper(l, r, mid + 1, end, 2 * node + 1);
            return mergeFn.apply(left, right);
        }

        public void update(int idx, T val) {
            inputData.set(idx, val);

            int STidx = paddedSize + idx;
            STList.set(STidx, val);

            while (STidx > 1) {
                STidx /= 2;
                STList.set(STidx, mergeFn.apply(STList.get(2 * STidx), STList.get(2 * STidx + 1)));
            }
        }
    }

    // GCD helper
    static int gcd(int a, int b) {
        while (b != 0) { int t = b; b = a % b; a = t; }
        return a;
    }

    public static void main(String[] args) {

        List<Integer> data = new ArrayList<>(Arrays.asList(2, 4, 10, -7, 8));

        // Sum
        GenericST<Integer> sumST = new GenericST<>(data, 0, Integer::sum);
        System.out.println("Sum tree:  " + sumST.STList);
        System.out.println("query(1,3) = " + sumST.query(1, 3));  // 4+10-7 = 7

        // Min
        GenericST<Integer> minST = new GenericST<>(data, Integer.MAX_VALUE, Integer::min);
        System.out.println("Min tree:  " + minST.STList);
        System.out.println("query(0,4) = " + minST.query(0, 4));  // -7

        // GCD
        GenericST<Integer> gcdST = new GenericST<>(data, 0, SegmentTreeGen::gcd);
        System.out.println("GCD tree:  " + gcdST.STList);
        System.out.println("query(0,2) = " + gcdST.query(0, 2));  // gcd(2,4,10) = 2
    }
}