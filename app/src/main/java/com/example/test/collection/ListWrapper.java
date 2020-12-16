package com.example.test.collection;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * date 2019/1/9
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class ListWrapper<T> implements List<T> {
    private @NonNull
    List<T> base;

    public ListWrapper(@NonNull List<T> base) {
        this.base = base;
    }

    public ListWrapper<T> filter(@NonNull IFilter<T> filter) {
        LinkedList<T> fList = new LinkedList<>();
        for (T t:base) {
            if (filter.filter(t)) {
                fList.add(t);
            }
        }
        return new ListWrapper<>(fList);
    }

    public <V> ListWrapper<V> map(@NonNull IMap<T, V> map) {
        ArrayList<V> vList = new ArrayList<>(base.size());
        for (T t:base) {
            vList.add(map.transform(t));
        }
        return new ListWrapper<>(vList);
    }

    public ListWrapper removeIf(@NonNull IFilter<T> filter) {
        Iterator<T> ite = base.iterator();
        while (ite.hasNext()) {
            if (filter.filter(ite.next())) {
                ite.remove();
            };
        }
        return this;
    }


    @NonNull
    public List<T> getBase() {
        return base;
    }

    public void setBase(@NonNull List<T> base) {
        this.base = base;
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return base.contains(o);
    }

    @Override
    @NonNull
    public Iterator<T> iterator() {
        return base.iterator();
    }

    @Override
    @NonNull
    public Object[] toArray() {
        return base.toArray();
    }

    @Override
    @NonNull
    public <T1> T1[] toArray(T1[] a) {
        return base.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return base.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return base.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return base.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return base.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return base.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return base.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return base.retainAll(c);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        return base.equals(o);
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T set(int index, T element) {
        return base.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        base.add(index, element);
    }

    @Override
    public T remove(int index) {
        return base.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return base.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return base.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return base.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return base.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return base.subList(fromIndex, toIndex);
    }
}
