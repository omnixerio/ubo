package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.types.VectorType;

/**
 * A vector of numbers of which every element has the same type, eg. a vector of two {@code int} values.
 * <p>
 * Unlike the generic vector types, which hold an arbitrary data type per element, these vectors hold plain
 * numbers. They are a data type of their own, so their id already describes their element type and no
 * element id is written next to their elements. In USO they are written as {@code ivec2[1,2]} instead of the
 * generic {@code v2:i[1,2]}.
 *
 * @param <T> the type of the vector's elements.
 */
public interface NumericVector<T extends Number> extends VectorType<T> {
    /**
     * The type of the vector's elements, eg. {@code Integer.class} for a vector of {@code int} values.
     *
     * @return the element type of this vector.
     */
    Class<T> elementType();

    /**
     * The id of this vector, eg. {@code DataTypes#INT_VECTOR_2} for a vector of two {@code int} values.
     *
     * @return the id of this vector.
     */
    int id();
}
