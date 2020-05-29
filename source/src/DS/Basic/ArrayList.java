package DS.Basic;

import java.util.Iterator;

public class ArrayList<T> implements ListInterface<T>, Iterable<T> {
    T data[];
    public int size;

    public static final int DEFUALT_CAPACITY = 16;

    public ArrayList() {
	this(DEFUALT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ArrayList(int def_cap) {
	data = ((T[]) new Object[def_cap]);
    }

    // public MyArrayList(T data[]) {
    // this.data = data;
    // size = data.length;
    // }

    @Override
    public void add(T newEntry) {
	if (++size == data.length)
	    expand();

	data[size - 1] = newEntry;
    }

    @Override
    public T add(int newPosition, T newEntry) {
	// Excluding invalid index
	if (newPosition < 0 || newPosition > size)
	    throw new IndexOutOfBoundsException("Index: " + newPosition + ", Size: " + size);

	T temp = data[newPosition];

	if (++size == data.length)
	    expand();

	for (int i = size - 1; i > newPosition - 1 && i != 0;)
	    data[i] = data[--i];

	data[newPosition] = newEntry;

	return temp;

    }

    @Override
    public void set(int index, T target) {
	// Excluding invalid index
	if (index < 0 || index >= size)
	    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

	data[index] = target;
    }

    @Override
    public T remove(int givenPosition) {
	// Excluding invalid index
	if (givenPosition < 0 || givenPosition >= size)
	    throw new IndexOutOfBoundsException("Index: " + givenPosition + ", Size: " + size);

	T temp = data[givenPosition];

	for (int i = givenPosition; i < size;)
	    data[i] = data[++i];

	size--;

	return temp;
    }

    @Override
    public boolean remove(T target) {
	try {
	    remove(contains2(target));
	    return true;
	} catch (Exception e) {
	    return false;
	}

    }

    @Override
    public void clear() {
	for (int i = 0; i < size; i++)
	    data[i] = null;

	size = 0;
    }

    @Override
    public T replace(int givenPosition, T newEntry) {
	T temp = data[givenPosition];
	set(givenPosition, newEntry);
	return temp;
    }

    @Override
    public T get(int givenPosition) {
	if (givenPosition < 0 || givenPosition > size - 1) {
	    throw new IndexOutOfBoundsException("Nope...");
	}
	return data[givenPosition];
    }

    @Override
    public T[] toArray() {
	return data;
    }

    @Override
    public boolean contains(T anEntry) {
	for (T t : data)
	    if (t == anEntry)
		return true;
	return false;
    }

    @Override
    public int size() {
	return size;
    }

    @Override
    public boolean isEmpty() {
	return size == 0;
    }

    /* My methods */
    private int contains2(T anEntry) {
	for (int i = 0; i < size; i++)
	    if (data[i] == anEntry)
		return i;
	return -1;
    }

    private void expand() {
	@SuppressWarnings("unchecked")
	T newD[] = ((T[]) new Object[data.length * 2]);

	int i = 0;
	for (T elem : data) {
	    newD[i++] = elem;
	}
	data = newD;
    }

    public int getSize() {
	return size;
    }

    public int getCapacity() {
	return data.length;
    }

    @Override
    public String toString() {
	if (size == 0)
	    return " cap: " + getCapacity() + " []";

	String out = "[";
	for (int i = 0; i < size; i++) {
	    // I wanted to make it t.toString but did not work out for primitive data types
	    out += data[i] + ", ";
	}
	return " cap: " + getCapacity() + " " + out.substring(0, out.length() - 2) + "]";

    }

    @Override
    public Iterator<T> iterator() {
	return new Iterator<T>() {
	    private int iterator = 0;

	    @Override
	    public boolean hasNext() {
		return iterator < size;
	    }

	    @Override
	    public T next() {
		return get(iterator++);
	    }
	};
    }

}
