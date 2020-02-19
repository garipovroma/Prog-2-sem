package search;

public class BinarySearchSpan {
    public static boolean predicate(int val, int x) {
        return (val <= x);
    }

    // pred : arr[-1] = INF, arr[n] = -INF, arr[i] >= arr[i + 1] for all i in [1;n - 1]
    public static int iterativeBinarySearch(int x, int arr[]) {
        int n = arr.length;
        int l = -1, r = n; // l, r in [-1;n]
        // Inv : predicate(arr[l], x) = false, predicate(arr[r], x) = true
        // Inv : arr[l] > x && arr[m] <= x
        while (r - l > 1) {
            // l < r - 1
            int m = (l + r) / 2;
            // m in [l;r]
            int val = arr[m];
            if (predicate(val, x)) {
                // arr[m] <= x
                r = m;
                // r' = m -> arr[r'] <= x
            } else {
                // arr[m] > x
                l = m;
                // l' = m -> arr[l'] > x
            }
            // r' = r && l = m || l = l' && r = m, m = (l + r) / 2 -> r' - l' < r - l, len > len'
        }
        // r - l <= 1
        // r <= l + 1
        // a[r] <= x
        // a[l] > x
        // it works!
        return l + 1;
    }
    //post : res in [0; n], a[res] <= x && a[l] > x

    // Inv : predicate(arr[l], x) = false, predicate(arr[r], x) = true
    public static int get(int l, int r, int x, int arr[]) {
        if (r - l == 1) {
            // l + 1 = r
            // arr[l] > x && arr[l + 1] <= x -> res = l + 1 - correct
            return l + 1;
        } else {
            // l + 1 != r -> l < r
            int m = (l + r) / 2;
            int val = arr[m];
            if (predicate(val, x)) {
                // arr[m] <= x
                return get(l, m, x, arr); // r' = m -> arr[r'] <= x, INV - OK
                // r' - l' < r - l -> len' < len
            } else {
                // arr[m] > x
                return get(m, r, x, arr); // l' = m -> arr[l'] > x, INV - OK
                // r' - l' < r - l -> len' < len
            }
        }
    }

    // pred : arr[-1] = INF, arr[n] = -INF, arr[i] >= arr[i + 1] for all i in [1;n - 1]
    public static int recursiveBinarySearch(int x, int arr[]) {
        return get(-1, arr.length, x, arr);
    }
    //post : res in [0; n], a[res] <= x && a[l] > x

    public static void main(String[] args) {
        int n = args.length - 1;
        int x = Integer.parseInt(args[0]);
        int arr[] = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(args[i + 1]);
        }
        int l = BinarySearch.iterativeBinarySearch(x, arr);
        int r = BinarySearch.recursiveBinarySearch(x - 1, arr);
        r--;
        int len = r - l + 1;
        if (r == -1) {
            len = 0;
        }
        System.out.println(l + " " + len);
    }
}
