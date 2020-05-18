package DS.Basic;

public interface ListInterface<T> {
    /**
     * Adds a new entry to the end of this list. Entries currently in the list are
     * unaffected. The list's size is increased by 1.
     * 
     * @param newEntry
     *                     The object to be added as a new entry.
     */
    public void add(T newEntry);

    /**
     * Adds a new entry at a specified position within this list. Entries originally
     * at and above the specified position are at the next higher position within
     * the list. The list's size is increased by 1.
     * 
     * @param newPosition
     *                        An integer that specifies the desired position of the
     *                        new entry.
     * @param newEntry
     *                        The object to be added as a new entry.
     * @throws IndexOutOfBoundsException
     *                                       if either newPosition < 1 or
     *                                       newPosition > getLength() + 1.
     */
    public T add(int newPosition, T newEntry);

    /**
     * Replace the indexth element of this List with target.
     * 
     * @param index
     *                   index of the element to replace
     * @param target
     *                   element to be stored at the specified position
     * @throws NullPointerException
     *                                       if the specified element is null and
     *                                       this list does not permit null elements
     * @throws IndexOutOfBoundsException
     *                                       if the index is out of range (index < 0
     *                                       || index >= size()).
     */
    public void set(int index, T target);

    /**
     * Removes the entry at a given position from this list. Entries originally at
     * positions higher than the given position are at the next lower position
     * within the list, and the list's size is decreased by 1.
     * 
     * @param givenPosition
     *                          An integer that indicates the position of the entry
     *                          to be removed.
     * @return A reference to the removed entry.
     * @throws IndexOutOfBoundsException
     *                                       if either givenPosition < 1 or
     *                                       givenPosition > getLength().
     */
    public T remove(int givenPosition);

    /**
     * Removes the first occurrence of target from this List, shifting all later
     * items one place left. Return true if this List contained the specified
     * element.
     * 
     * @param target
     *                   element to be removed from this list, if present
     * @return true if this list contained the specified element
     * @throws NullPointerException
     *                                  - if the specified element is null and this
     *                                  list does not permit null elements
     */
    public boolean remove(T target);

    /** Removes all entries from this list. */
    public void clear();

    /**
     * Replaces the entry at a given position in this list.
     * 
     * @param givenPosition
     *                          An integer that indicates the position of the entry
     *                          to be replaced.
     * @param newEntry
     *                          The object that will replace the entry at the
     *                          position givenPosition.
     * @return The original entry that was replaced.
     * @throws IndexOutOfBoundsException
     *                                       if either givenPosition < 1 or
     *                                       givenPosition > getLength().
     */
    public T replace(int givenPosition, T newEntry);

    /**
     * Retrieves the entry at a given position in this list.
     * 
     * @param givenPosition
     *                          An integer that indicates the position of the
     *                          desired entry.
     * @return A reference to the indicated entry.
     * @throws IndexOutOfBoundsException
     *                                       if either givenPosition < 1 or
     *                                       givenPosition > getLength().
     */
    public T get(int givenPosition);

    /**
     * Retrieves all entries that are in this list in the order in which they occur
     * in the list.
     * 
     * @return A newly allocated array of all the entries in the list. If the list
     *         is empty, the returned array is empty.
     */
    public T[] toArray();

    /**
     * Sees whether this list contains a given entry.
     * 
     * @param anEntry
     *                    The object that is the desired entry.
     * @return True if the list contains anEntry, or false if not.
     */
    public boolean contains(T anEntry);

    /**
     * Gets the size (length) of this list.
     * 
     * @return The integer number of entries currently in the list.
     */
    public int size();

    /**
     * Sees whether this list is empty.
     * 
     * @return True if the list is empty, or false if not.
     */
    public boolean isEmpty();
} // end ListInterface