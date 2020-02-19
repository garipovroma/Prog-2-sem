package search;

public class BinarySearch {
    public static boolean predicate(int x, int val) {
        return (val <= x);
    }
    public static int iterativeBinarySearch(int x, int arr[]) {
        int n = arr.length;
        int l = -1, r = n; // predicate(x, arr[l]) = false, predicate(x, arr[r]) = true
        while (r - l > 1) {
            int m = (l + r) / 2;
            int val = arr[m];
            if (predicate(x, val)) {
                r = m;
            } else {
                l = m;
            }
        }
        return l + 1;
    }
    public static void main(String[] args) {
        int n = args.length - 1;
	    int x = Integer.parseInt(args[0]);
	    int arr[] = new int[n];
	    for (int i = 0; i < n; i++) {
	        arr[i] = Integer.parseInt(args[i + 1]);
        }
	    System.out.println(iterativeBinarySearch(x, arr));
    }
}
