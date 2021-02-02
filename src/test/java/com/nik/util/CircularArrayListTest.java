package com.nik.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CircularArrayListTest {

    public static final Class<?> CIRCULAR_ARRAY_LIST_CLASS = CircularArrayList.class;
    private final static Field elementsField;
    private final static Field sizeField;
    private final static Field headField;

    static {
        try {
            elementsField = CIRCULAR_ARRAY_LIST_CLASS.getDeclaredField("elements");
            elementsField.setAccessible(true);
            sizeField = CIRCULAR_ARRAY_LIST_CLASS.getDeclaredField("size");
            sizeField.setAccessible(true);
            headField = CIRCULAR_ARRAY_LIST_CLASS.getDeclaredField("head");
            headField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void setup(List<Integer> list,
                              int head, int capacity, Integer... values) throws IllegalAccessException {
        Object[] elements = new Object[capacity];
        for (int i = 0; i < values.length; i++) {
            int realIndex = (i + head) % capacity;
            elements[realIndex] = values[i];
        }
        elementsField.set(list, elements);
        sizeField.set(list, values.length);
        headField.set(list, head);
    }

    @Test
    public void testAddThoroughly() throws ReflectiveOperationException {
        List<Integer> list = (List<Integer>) CIRCULAR_ARRAY_LIST_CLASS.newInstance();
        int start_capacity = 1, start_numberOfElements = 0, start_head = 0;
        for (int capacity = start_capacity; capacity <= 20; capacity++) {
            for (int numberOfElements = 0; numberOfElements < capacity; numberOfElements++) {
                for (int index = start_numberOfElements; index <= numberOfElements; index++) {
                    Integer[] valuesInCollection = new Integer[numberOfElements];
                    for (int i = 0; i < valuesInCollection.length; i++) {
                        valuesInCollection[i] = i + 1;
                    }
                    List<Integer> expected = new ArrayList<>();
                    Collections.addAll(expected, valuesInCollection);
                    expected.add(index, 7);
                    for (int head = start_head; head < capacity; head++) {
                        setup(list, head, capacity, valuesInCollection);
                        System.out.printf("Testing add capacity=%d,numberOfElements=%d,head=%d,index=%d%n", capacity, numberOfElements, head, index);
                        list.add(index, 7);
                        assertEquals(String.format("numberOfElements=%d,head=%d,index=%d", numberOfElements, head, index), expected, list);
                    }
                }
            }
        }
    }

    @Test
    public void testRemoveThoroughly() throws ReflectiveOperationException {
        List<Integer> list = (List<Integer>) CIRCULAR_ARRAY_LIST_CLASS.newInstance();
        for (int capacity = 1; capacity <= 20; capacity++) {
            for (int numberOfElements = 0; numberOfElements < capacity; numberOfElements++) {
                for (int index = 0; index < numberOfElements; index++) {
                    Integer[] valuesInCollection = new Integer[numberOfElements];
                    for (int i = 0; i < valuesInCollection.length; i++) {
                        valuesInCollection[i] = i + 1;
                    }
                    List<Integer> expected = new ArrayList<>();
                    Collections.addAll(expected, valuesInCollection);
                    expected.remove(index);
                    for (int head = 0; head < capacity; head++) {
                        setup(list, head, capacity, valuesInCollection);
                        list.remove(index);
                        System.out.printf("Testing remove capacity=%d,numberOfElements=%d,head=%d,index=%d%n", capacity, numberOfElements, head, index);
                        assertEquals(String.format("remove numberOfElements=%d,head=%d,index=%d", numberOfElements, head, index), expected, list);
                    }
                }
            }
        }
    }
}