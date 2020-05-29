package Utils;

import java.util.Random;

public class JUtil {

    public static void main(String[] args) {
	int A[] = JUtil.rand_arr_Real(12, 20);
	JUtil.print(A);
	JUtil.print(min(A));
    }

    public static String n_times_char(int n, char ch) {
	String retval = "";
	for (int i = 0; i < n; i++)
	    retval += ch;
	return retval;
    }

    public static int[] range(int from, int to) {
	int[] retval = new int[to - from];

	for (int i = from; i < to; i++) {
	    retval[i - from] = i;
	}
	return retval;
    }

    /* A utility function to print array of size n */
    public static void printArray(Object arr[]) {
	System.out.print("[ ");
	for (int i = 0; i < arr.length; ++i)
	    System.out.print(arr[i] + " ");
	System.out.println("]");
    }

    public static void print(int arr[]) {
	System.out.print("[ ");
	for (int i = 0; i < arr.length; ++i)
	    System.out.print(arr[i] + " ");
	System.out.println("]");
    }

    public static int[] rand_arr(int size, int range) {
	int A[] = new int[size];
	for (int i = 0; i < size; i++)
	    A[i] = new Random().nextInt(range);
	return A;
    }

    public static int[] rand_arr_PosInt(int size, int range) {
	int A[] = new int[size];
	for (int i = 0; i < size; i++)
	    A[i] = new Random().nextInt(range) + 1;
	return A;
    }

    public static int[] rand_arr_Real(int size, int range) {
	int A[] = new int[size];
	for (int i = 0; i < size; i++)
	    A[i] = new Random().nextInt(range * 2) - range;
	return A;
    }

    public static void print(Object a) {
	System.out.println(a);
    }

    public static void printBigArray(int[] a) {
	System.out.print("[ ");
	for (int i = 0; i < 10; i++) {
	    System.out.print(a[i] + " ");
	}
	if (a.length > 10)
	    System.out.print("... " + a[a.length - 1] + " ");
	System.out.println("]");

    }

    public static int max(int[] A) {
	int max = A[0];

	for (int i = 1; i < A.length; i++) {
	    max = (max < A[i]) ? A[i] : max;
	}

	return max;
    }

    public static int min(int[] A) {
	int min = A[0];

	for (int i = 1; i < A.length; i++) {
	    min = (min > A[i]) ? A[i] : min;
	}

	return min;
    }

    public static int nth_digit(int a, int b) {
	String str = "" + a;
	if (str.length() < b)
	    return 0;

	return Integer.parseInt(str.charAt(str.length() - b) + "");

    }

    public static void swap(int[] A, int i, int j) {
	int temp = A[i];
	A[i] = A[j];
	A[j] = temp;
    }
}
