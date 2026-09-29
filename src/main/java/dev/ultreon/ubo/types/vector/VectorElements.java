package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.types.DataType;

/**
 * The element types shared by the numeric vector types.
 */
final class VectorElements {
    private VectorElements() {
    }

    /**
     * Checks that the given element can be held by a vector of the given type.
     *
     * @param index the index of the element, used for the error message.
     * @param element the element to check.
     * @param type the type of the vector's elements.
     * @param <T> the type of the vector's elements.
     * @return the checked element.
     */
    static <T extends Number> T check(int index, T element, Class<T> type) {
        if (element == null)
            throw new IllegalArgumentException("Element at index " + index + " can't be null");

        if (!type.isInstance(element))
            throw new IllegalArgumentException("Element at index " + index + " has invalid type: "
                    + element.getClass().getSimpleName() + " (expected " + type.getSimpleName() + ")");

        return element;
    }

    /**
     * The character used in USO to mark the given element type, eg. {@code i} in {@code vi2[1,2]}.
     *
     * @param type the type of the vector's elements.
     * @return the element type marker.
     */
    static char usoType(Class<? extends Number> type) {
        if (type == Integer.class) return 'i';
        if (type == Long.class) return 'l';
        if (type == Float.class) return 'f';
        if (type == Double.class) return 'd';
        throw new IllegalArgumentException("Unsupported vector element type: " + type);
    }

    /**
     * Reads the number held by a data type, eg. to convert a vector of {@link dev.ultreon.ubo.types.IntType}
     * into a vector of {@code int} values.
     *
     * @param type the data type to read.
     * @param elementType the expected type of the number.
     * @param <T> the expected type of the number.
     * @return the number held by the data type.
     */
    @SuppressWarnings("unchecked")
    static <T extends Number> T number(DataType<?> type, Class<T> elementType) {
        Object value = type.getValue();
        if (!elementType.isInstance(value))
            throw new IllegalArgumentException("Expected an element of type " + elementType.getSimpleName() + " but got: " + type);
        return (T) value;
    }
}
