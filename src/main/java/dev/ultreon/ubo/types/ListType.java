package dev.ultreon.ubo.types;

import dev.ultreon.ubo.DataTypeRegistry;
import dev.ultreon.ubo.DataTypes;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListType<T extends DataType<?>> implements DataType<List<T>>, List<T> {
    private final int id;
    Class<T> componentType;
    private List<T> obj;

    public ListType(int id) {
        this(new ArrayList<>(), id);
    }

    @SafeVarargs
    public ListType(T... type) {
        this((Class<T>) type.getClass().getComponentType());

        this.setValue(Arrays.asList(type));
    }

    @SafeVarargs
    public ListType(List<T> obj, T... type) {
        this(obj, (Class<T>) type.getClass().getComponentType());
    }

    public ListType(Class<? extends DataType<?>> type) {
        this(new ArrayList<>(), DataTypeRegistry.getIdOrThrow(type));
    }

    public ListType(List<T> obj, Class<T> type) {
        this.obj = obj;
        this.id = DataTypeRegistry.getIdOrThrow(type);
        this.componentType = type;
    }

    private ListType(List<T> list, int id) {
        setValue(list);
        this.id = id;
        this.componentType = (Class<T>) DataTypeRegistry.getType(id);
    }

    @Override
    public List<T> getValue() {
        return obj;
    }

    @Override
    public void setValue(List<T> obj) {
        int id = -1;
        List<T> list = new ArrayList<>(obj.size());
        for (int i = 0, objSize = obj.size(); i < objSize; i++) {
            T iType = obj.get(i);
            if (id == -1) {
                id = iType.id();
            }
            if (id != iType.id()) {
                throw new IllegalArgumentException("Type at index " + i + " has invalid id: " + iType.id() + " (expected " + id + ")");
            }

            list.add(iType);
        }

        this.obj = list;
    }

    @Override
    public int id() {
        return DataTypes.LIST;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(id);
        output.writeInt(obj.size());
        for (DataType<?> l : obj) {
            l.write(output);
        }
    }

    public static ListType<?> read(DataInput input) throws IOException {
        int id = input.readUnsignedByte();
        int len = input.readInt();
        List<DataType<?>> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(DataTypeRegistry.read(id, input));
        }

        return new ListType<>(list, id);
    }

    @Override
    public boolean add(T type) {
        if (type.id() != id)
            throw new IllegalArgumentException("Type has invalid id: " + type.id() + " (expected " + id + ")");
        return obj.add(type);
    }

    @Override
    public boolean remove(Object o) {
        return obj.remove(o);
    }

    @Override
    @SuppressWarnings("SlowListContainsAll")
    public boolean containsAll(@NotNull Collection<?> c) {
        return obj.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return obj.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return obj.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return obj.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return obj.retainAll(c);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return obj.iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return obj.toArray();
    }

    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
        return obj.toArray(a);
    }

    public T @NotNull [] toTypeArray() {
        T[] a = (T[]) Array.newInstance(componentType, obj.size());
        for (int i = 0; i < obj.size(); i++) {
            a[i] = obj.get(i);
        }
        return a;
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return obj.listIterator();
    }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) {
        return obj.listIterator(index);
    }

    @Override
    public @NotNull List<T> subList(int fromIndex, int toIndex) {
        return obj.subList(fromIndex, toIndex);
    }

    public int type() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <C extends DataType<?>> ListType<C> listCast(C... type) {
        return this.listCast((Class<C>) type.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    public final <C extends DataType<?>> ListType<C> listCast(Class<C> type) {
        ListType<C> cs = new ListType<>(type);
        cs.setValue((List<C>) obj);
        return cs;
    }

    @Override
    public T get(int index) {
        return obj.get(index);
    }

    @Override
    public T remove(int index) {
        if (index >= obj.size())
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        if (index < 0)
            index = obj.size() + index;
        if (index < 0)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        obj.remove(index);
        return null;
    }

    @Override
    @Deprecated
    public int indexOf(Object o) {
        if (!(o instanceof DataType<?>))
            return -1;
        DataType<?> type = (DataType<?>) o;
        if (type.id() != id)
            throw new IllegalArgumentException("Type has invalid id: " + type.id() + " (expected " + id + ")");
        return obj.indexOf(o);
    }

    @Override
    @Deprecated
    public int lastIndexOf(Object o) {
        if (!(o instanceof DataType<?>))
            return -1;
        DataType<?> type = (DataType<?>) o;
        if (type.id() != id)
            throw new IllegalArgumentException("Type has invalid id: " + type.id() + " (expected " + id + ")");
        return obj.lastIndexOf(o);
    }

    public int indexOf(T type) {
        return obj.indexOf(type);
    }

    public int lastIndexOf(T type) {
        return obj.lastIndexOf(type);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ListType)) return false;
        ListType<?> listType = (ListType<?>) other;
        return id == listType.id && Objects.equals(obj, listType.obj);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + obj.hashCode();
        return result;
    }

    @Override
    public ListType<T> copy() {
        return new ListType<>(obj.stream().map(t -> (T) t.copy()).collect(Collectors.toList()));
    }

    @Override
    public String writeUso() {
        StringBuilder builder = new StringBuilder("[");
        for (T t : this)
            builder.append(t.writeUso()).append(", ");

        if (!this.obj.isEmpty())
            return builder.substring(0, builder.length() - 2) + "]";

        return builder + "]";
    }

    @Override
    public int size() {
        return obj.size();
    }

    @Override
    public void clear() {
        obj.clear();
    }

    @Override
    public T set(int index, T type) {
        if (type.id() != id)
            throw new IllegalArgumentException("Type at index " + index + " has invalid id: " + type.id() + " (expected " + id + ")");
        return obj.set(index, type);
    }

    @Override
    public void add(int index, T element) {
        if (element.id() != id)
            throw new IllegalArgumentException("Type at index " + index + " has invalid id: " + element.id() + " (expected " + id + ")");
        obj.add(index, element);
    }

    public T remove(T type) {
        obj.remove(type);
        return type;
    }

    public boolean contains(T type) {
        return obj.contains(type);
    }

    public <R> List<R> mapTo(Function<T, R> mapper) {
        return mapTo(mapper, new ArrayList<>());
    }

    public <R> List<R> mapTo(Function<T, R> mapper, List<R> list) {
        for (T t : obj) {
            R r = mapper.apply(t);
            list.add(r);
        }
        return list;
    }

    @Override
    public @NotNull Stream<T> stream() {
        return obj.stream();
    }

    @Override
    public @NotNull Stream<T> parallelStream() {
        return obj.parallelStream();
    }

    @Override
    public boolean isEmpty() {
        return obj.isEmpty();
    }

    @Override
    @Deprecated
    public boolean contains(Object o) {
        return obj.contains(o);
    }

    @Override
    public String toString() {
        return writeUso();
    }
}
