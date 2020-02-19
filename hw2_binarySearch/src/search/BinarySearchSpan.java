package search;

public class BinarySearchSpan {
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
