package fr.communaywen.core.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InverseStack<T> {

    private final List<T> stack;

    public InverseStack() {
        stack = new ArrayList<>();
    }

    public void push(T item) {
        stack.add(item);
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        }
        return stack.removeFirst();
    }

    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return stack.getFirst();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

}
