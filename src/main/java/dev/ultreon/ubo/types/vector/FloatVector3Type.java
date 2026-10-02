package dev.ultreon.ubo.types.vector;

import dev.ultreon.ubo.DataTypes;
import dev.ultreon.ubo.types.FloatType;
import dev.ultreon.ubo.types.Vector3Type;
import dev.ultreon.ubo.types.util.Vector3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A vector of three {@code float} values, written as {@code fvec3[1.5,2.5,3.5]} in USO.
 */
public class FloatVector3Type extends NumericVector3Type<Float> {
    public FloatVector3Type(float x, float y, float z) {
        super(x, y, z);
    }

    public FloatVector3Type(Vector3<Float> value) {
        super(value);
    }

    @Override
    public int id() {
        return DataTypes.FLOAT_VECTOR_3;
    }

    @Override
    public Class<Float> elementType() {
        return Float.class;
    }

    @Override
    protected void writeElement(DataOutput output, Float element) throws IOException {
        output.writeFloat(element);
    }

    public static FloatVector3Type read(DataInput input) throws IOException {
        return new FloatVector3Type(input.readFloat(), input.readFloat(), input.readFloat());
    }

    @Override
    public FloatVector3Type copy() {
        return new FloatVector3Type(getValue());
    }

    public static FloatVector3Type from(Vector3Type<?> vector) {
        return new FloatVector3Type(VectorElements.number(vector.getX(), Float.class),
                VectorElements.number(vector.getY(), Float.class),
                VectorElements.number(vector.getZ(), Float.class));
    }

    public Vector3Type<FloatType> toVector3Type() {
        return new Vector3Type<>(new FloatType(getX()), new FloatType(getY()), new FloatType(getZ()));
    }
}
