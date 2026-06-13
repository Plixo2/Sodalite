package io.github.plixo2.sodalite.memory;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class IntList implements Iterable<Integer> {
    private int[] data;
    private int length;
    private int size;

    public IntList() {
        this.length = 16;
        this.data = new int[this.length];
        this.size = 0;
    }

    /// Removes all elements from the list
    public void clear() {
        this.size = 0;
    }

    /// @return if the list is empty
    public boolean isEmpty() {
        return this.size == 0;
    }

    /// @return the number of elements in the list
    public int size() {
        return this.size;
    }

    /// @return the index of the first occurrence of the value, or -1 if not found
    public int indexOf(int value) {
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /// @return the index of the last occurrence of the value, or -1 if not found
    public int lastIndexOf(int value) {
        for (int i = this.size - 1; i >= 0; i--) {
            if (this.data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /// @return if the value is in the list
    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    /// Set the element at the specified index to the given value
    /// @return the old value at the index
    /// @throws IndexOutOfBoundsException if the index is out of bounds
    public int set(int index, int value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        int old = this.data[index];
        this.data[index] = value;
        return old;
    }

    /// Gets the element at the specified index
    /// @throws IndexOutOfBoundsException if the index is out of bounds
    public int get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        return this.data[index];
    }

    /// Add a value to the end of the list
    public boolean add(int value) {
        if (this.size >= this.length) {
            resize();
        }
        this.data[this.size++] = value;
        return true;
    }

    /// Add multiple values to the end of the list
    public void add(int[] values) {
        if (this.size + values.length >= this.length) {
            resize(this.size + values.length);
        }
        System.arraycopy(values, 0, this.data, this.size, values.length);
        this.size += values.length;
    }

    /// Add multiple values to the end of the list
    public void add(int[] values, int from, int size) {
        if (from < 0 || size < 0 || from + size > values.length) {
            throw new IndexOutOfBoundsException("From: " + from + ", Size: " + size + ", Length: " + values.length);
        }
        if (this.size + size >= this.length) {
            resize(this.size + size);
        }
        System.arraycopy(values, from, this.data, this.size, size);
        this.size += size;
    }

    /// Add all values from another IntList to the end of the list
    public void add(IntList values) {
        if (this.size + values.size >= this.length) {
            resize(this.size + values.size);
        }
        System.arraycopy(values.data, 0, this.data, this.size, values.size);
        this.size += values.size;
    }

    /// @return the added value
    public int push(int value) {
        add(value);
        return value;
    }

    /// Removes the last element
    /// @return the removed element
    /// @throws NoSuchElementException if the list is empty
    public int removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return this.data[--this.size];
    }
    /// Removes the last element
    /// @return the removed element
    /// @throws NoSuchElementException if the list is empty
    public int pop() {
        return removeLast();
    }
    public int peek() {
        if (this.size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return this.data[this.size - 1];
    }


    public void forEach(IntConsumer consumer) {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.data[i]);
        }
    }

    @Override
    public void forEach(Consumer<? super Integer> consumer) {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.data[i]);
        }
    }


    private void resize() {
        int newSize = this.length * 2;
        int[] newData = new int[newSize];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
        this.length = newSize;
    }
    private void resize(int min) {
        int newSize = this.length * 2;
        while (newSize < min) {
            newSize *= 2;
        }
        int[] newData = new int[newSize];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
        this.length = newSize;
    }


    @Override
    public @NotNull Iterator<Integer> iterator() {
        return new IntListIterator();
    }


    @RequiredArgsConstructor
    private final class IntListIterator implements Iterator<Integer> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return this.index < IntList.this.size;
        }

        @Override
        public Integer next() {
            return IntList.this.get(this.index++);
        }
    }

    /*
    @Override
    public @NotNull Object @NotNull [] toArray() {
        Object[] result = new Object[this.size];
        for (int i = 0; i < this.size; i++) result[i] = this.data[i];
        return result;
    }

    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        if (a.length < this.size) {
            @SuppressWarnings("unchecked")
            T[] newArray = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size);
            a = newArray;
        }
        for (int i = 0; i < this.size; i++) {
            @SuppressWarnings("unchecked")
            T value = (T) Integer.valueOf(this.data[i]);
            a[i] = value;
        }
        if (a.length > this.size) {
            //noinspection DataFlowIssue
            a[this.size] = null;
        }
        return a;
    }

    /// @throws NullPointerException if `integer` is null
    @Override
    public boolean add(Integer integer) {
        return add(Objects.requireNonNull(integer).intValue());
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Integer in)) return false;
        int val = in;
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] == val) {
                System.arraycopy(this.data, i + 1, this.data, i, this.size - i - 1);
                this.size--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Integer i)) {
            return false;
        }
        return contains(i.intValue());
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!(o instanceof Integer i) || !contains(i.intValue())) {
                return false;
            }
        }
        return true;
    }

    /// @throws NullPointerException if the collection contains null elements
    @Override
    public boolean addAll(@NotNull Collection<? extends Integer> c) {
        var collectionSize = c.size();
        if (this.size + collectionSize >= this.length) {
            resize(this.size + collectionSize);
        }
        for (Integer i : c) {
            this.data[this.size++] = i;
        }
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (Object o : c) modified |= remove(o);
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (int i = this.size - 1; i >= 0; i--) {
            if (!c.contains(this.data[i])) {
                System.arraycopy(this.data, i + 1, this.data, i, this.size - i - 1);
                this.size--;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean offer(Integer integer) {
        return add(integer);
    }

    @Override
    public Integer remove() {
        if (this.size == 0) throw new NoSuchElementException();
        int val = this.data[0];
        System.arraycopy(this.data, 1, this.data, 0, --this.size);
        return val;
    }

    @Override
    public @Nullable Integer poll() {
        if (this.size == 0) return null;
        int val = this.data[0];
        System.arraycopy(this.data, 1, this.data, 0, --this.size);
        return val;
    }

    @Override
    public Integer element() {
        if (this.size == 0) throw new NoSuchElementException();
        return this.data[0];
    }

    @Override
    public Integer peek() {
        if (this.size == 0) return null;
        return this.data[0];
    }
     */
}
