package dev.ultreon.ubo;

import dev.ultreon.ubo.types.*;
import dev.ultreon.ubo.types.vector.*;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

public class UsoParser {
    private final char[] chars;
    private int pos;

    public UsoParser(String input) {
        this.chars = input.toCharArray();
    }

    private DataType<?> readUso() throws IOException {
        int read = read();
        switch (read) {
            case '[':
                return readList();
            case '{':
                return readMap();
            case '(':
                return readArray();
            case '<':
                return readUUID();
            case '"':
                return readString();
            case '\'':
                return new CharType(readChar());
            case 'x':
                return readBitSet();
            case 't':
                this.unread();
                return readBoolean();
            case 'f':
                // f is ambiguous, since it's the start of both "false" and "fvec<count>".
                if (isNumericVectorStart()) return readNumericVector();
                this.unread();
                return readBoolean();
            case 'v':
                return readVector();
            case 'i':
            case 'l':
            case 'd':
                if (isNumericVectorStart()) return readNumericVector();
                throw new IOException("Invalid USO: " + (char) read);
            default:
                if (Character.isDigit(read)) return readNumber(read);
                throw new IOException("Invalid USO: " + (char) read);
        }
    }

    /**
     * Checks whether the position right after the read element type marker is followed by {@code vec<count>},
     * eg. {@code vec2[1,2]}.
     */
    private boolean isNumericVectorStart() {
        if (this.pos + 4 > this.chars.length) return false;
        if (!"vec".contentEquals(String.valueOf(this.chars, this.pos, 3))) return false;

        char size = this.chars[this.pos + 3];
        return size == '2' || size == '3' || size == '4';
    }

    private DataType<?> readVector() throws IOException {
        int size = read();
        if (size == -1) throw new EOFException("Invalid vector: EOF, expected size");

        if (read() != ':') throw new IOException("Invalid vector: expected ':' after size " + (char) size);

        String elementType = readVectorElementType();

        switch ((char) size) {
            case '2':
                return readGenericVector(2, elementType);
            case '3':
                return readGenericVector(3, elementType);
            case '4':
                return readGenericVector(4, elementType);
            default:
                throw new IOException("Invalid vector count: " + (char) size);
        }
    }

    /**
     * Reads the element type of a vector, eg. {@code i} in {@code v2:i[2,4]}. Array elements carry the array's own
     * element type as well, eg. {@code Ai} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    private String readVectorElementType() throws IOException {
        int read = read();
        if (read == -1) throw new EOFException("Invalid vector: EOF, expected element type");

        if (read != VectorType.USO_ELEMENT_ARRAY.charAt(0)) return String.valueOf((char) read);

        int arrayType = read();
        if (arrayType == -1) throw new EOFException("Invalid vector: EOF, expected array element type");

        return VectorType.USO_ELEMENT_ARRAY + (char) arrayType;
    }

    /**
     * Reads a vector of numbers of a single type, written as {@code <type>vec<size>[...]}, eg. {@code ivec2[2,4]}.
     * <p>
     * Such a vector is a data type of its own, so its element type doesn't need a type marker next to it. The
     * element type marker is expected to have been read already.
     */
    private DataType<?> readNumericVector() throws IOException {
        char elementType = this.chars[this.pos - 1];
        this.pos += 3; // The 'vec' marker, already checked by isNumericVectorStart().

        int size = read();
        String vector = elementType + "vec" + (char) size;

        if (read() != '[') throw new IOException("Invalid " + vector + ": expected '['");

        String[] elements = new String[size - '0'];
        for (int i = 0; i < elements.length; i++) {
            readWhitespace();
            elements[i] = readNumberText();
            readWhitespace();
            if (i + 1 < elements.length && read() != ',') throw new IOException("Invalid " + vector + ": expected ',' after element " + i);
        }

        if (read() != ']') throw new IOException("Invalid " + vector + ": expected ']'");

        switch (elementType) {
            case 'i': {
                int x = Integer.parseInt(elements[0]);
                int y = Integer.parseInt(elements[1]);
                switch (size) {
                    case '2':
                        return new IntVector2Type(x, y);
                    case '3':
                        return new IntVector3Type(x, y, Integer.parseInt(elements[2]));
                    default:
                        return new IntVector4Type(x, y, Integer.parseInt(elements[2]), Integer.parseInt(elements[3]));
                }
            }
            case 'l': {
                long x = Long.parseLong(elements[0]);
                long y = Long.parseLong(elements[1]);
                switch (size) {
                    case '2':
                        return new LongVector2Type(x, y);
                    case '3':
                        return new LongVector3Type(x, y, Long.parseLong(elements[2]));
                    default:
                        return new LongVector4Type(x, y, Long.parseLong(elements[2]), Long.parseLong(elements[3]));
                }
            }
            case 'f': {
                float x = Float.parseFloat(elements[0]);
                float y = Float.parseFloat(elements[1]);
                switch (size) {
                    case '2':
                        return new FloatVector2Type(x, y);
                    case '3':
                        return new FloatVector3Type(x, y, Float.parseFloat(elements[2]));
                    default:
                        return new FloatVector4Type(x, y, Float.parseFloat(elements[2]), Float.parseFloat(elements[3]));
                }
            }
            default: {
                double x = Double.parseDouble(elements[0]);
                double y = Double.parseDouble(elements[1]);
                switch (size) {
                    case '2':
                        return new DoubleVector2Type(x, y);
                    case '3':
                        return new DoubleVector3Type(x, y, Double.parseDouble(elements[2]));
                    default:
                        return new DoubleVector4Type(x, y, Double.parseDouble(elements[2]), Double.parseDouble(elements[3]));
                }
            }
        }
    }

    /**
     * Reads a vector of arbitrary data types, written as {@code v<size>:<type>[...]}, eg. {@code v2:i[2,4]}.
     */
    private DataType<?> readGenericVector(int size, String elementType) throws IOException {
        String vector = "v" + size;
        if (read() != '[') throw new IOException("Invalid " + vector + ": expected '['");

        DataType<?>[] elements = new DataType<?>[size];
        for (int i = 0; i < size; i++) {
            readWhitespace();
            elements[i] = readVectorElement(elementType);
            readWhitespace();
            if (i + 1 < size && read() != ',') throw new IOException("Invalid " + vector + ": expected ',' after element " + i);
        }

        if (read() != ']') throw new IOException("Invalid " + vector + ": expected ']'");

        switch (size) {
            case 2:
                return new Vector2Type<>(elements[0], elements[1]);
            case 3:
                return new Vector3Type<>(elements[0], elements[1], elements[2]);
            default:
                return new Vector4Type<>(elements[0], elements[1], elements[2], elements[3]);
        }
    }

    private DataType<?> readVectorElement(String elementType) throws IOException {
        switch (elementType) {
            case "b":
                return new ByteType(Byte.parseByte(readNumberText()));
            case "z":
                return readBoolean();
            case "s":
                return new ShortType(Short.parseShort(readNumberText()));
            case "i":
                return new IntType(Integer.parseInt(readNumberText()));
            case "l":
                return new LongType(Long.parseLong(readNumberText()));
            case "f":
                return new FloatType(Float.parseFloat(readNumberText()));
            case "d":
                return new DoubleType(Double.parseDouble(readNumberText()));
            case "I":
                return new BigIntType(new BigInteger(readNumberText()));
            case "D":
                return new BigDecType(new BigDecimal(readNumberText()));
            case "c":
                return readCharElement();
            case "t":
                return readText();
            case VectorType.USO_ELEMENT_MAP:
                return readCompositeElement(MapType.class);
            case VectorType.USO_ELEMENT_LIST:
                return readCompositeElement(ListType.class);
            case VectorType.USO_ELEMENT_VECTOR:
                return readCompositeElement(VectorType.class);
            default:
                if (elementType.startsWith(VectorType.USO_ELEMENT_ARRAY)) {
                    return readArrayElement(elementType.charAt(1));
                }
                throw new IOException("Invalid vector element type: " + elementType);
        }
    }

    /**
     * Reads a map, list or vector element of a vector, which are written as complete USO values, including their own
     * type marker, eg. {@code {"a":1i}} in {@code v2:M[{"a":1i},{"a":2i}]}.
     */
    private DataType<?> readCompositeElement(Class<?> expected) throws IOException {
        DataType<?> element = readUso();
        if (!expected.isInstance(element)) {
            throw new IOException("Invalid vector element: expected " + expected.getSimpleName() + " but got " + element);
        }

        return element;
    }

    /**
     * Reads an array element of a vector, which only carries its values, since the array's element type is part of
     * the vector's element type, eg. {@code (1,2)} in {@code v2:Ai[(1,2),(3,4)]}.
     */
    private DataType<?> readArrayElement(char valueType) throws IOException {
        if (read() != '(') throw new IOException("Invalid array element: expected '('");

        List<DataType<?>> values = new ArrayList<>();
        readWhitespace();
        int next = read();
        if (next == ')') {
            return createArrayElement(valueType, values);
        }
        unread();

        while (true) {
            values.add(readVectorElement(String.valueOf(valueType)));

            readWhitespace();
            int read = read();
            if (read == ',') {
                readWhitespace();
                continue;
            }
            if (read == ')') break;
            throw new IOException("Invalid array element: expected ',' or ')' but got " + (char) read);
        }

        return createArrayElement(valueType, values);
    }

    private DataType<?> createArrayElement(char valueType, List<DataType<?>> values) throws IOException {
        int size = values.size();
        switch (valueType) {
            case 'b': {
                byte[] array = new byte[size];
                for (int i = 0; i < size; i++) array[i] = ((ByteType) values.get(i)).getByteValue();
                return new ByteArrayType(array);
            }
            case 's': {
                short[] array = new short[size];
                for (int i = 0; i < size; i++) array[i] = ((ShortType) values.get(i)).getShortValue();
                return new ShortArrayType(array);
            }
            case 'i': {
                int[] array = new int[size];
                for (int i = 0; i < size; i++) array[i] = ((IntType) values.get(i)).getIntValue();
                return new IntArrayType(array);
            }
            case 'l': {
                long[] array = new long[size];
                for (int i = 0; i < size; i++) array[i] = ((LongType) values.get(i)).getLongValue();
                return new LongArrayType(array);
            }
            case 'f': {
                float[] array = new float[size];
                for (int i = 0; i < size; i++) array[i] = ((FloatType) values.get(i)).getFloatValue();
                return new FloatArrayType(array);
            }
            case 'd': {
                double[] array = new double[size];
                for (int i = 0; i < size; i++) array[i] = ((DoubleType) values.get(i)).getDoubleValue();
                return new DoubleArrayType(array);
            }
            case 'c': {
                char[] array = new char[size];
                for (int i = 0; i < size; i++) array[i] = ((CharType) values.get(i)).getCharValue();
                return new CharArrayType(array);
            }
            case 'z': {
                boolean[] array = new boolean[size];
                for (int i = 0; i < size; i++) array[i] = ((BooleanType) values.get(i)).getBooleanValue();
                return new BooleanArrayType(array);
            }
            default:
                throw new IOException("Invalid array element type: " + valueType);
        }
    }

    private CharType readCharElement() throws IOException {
        int read = read();
        if (read != '\'') throw new IOException("Invalid char: expected ' but got " + (char) read);
        return new CharType(readChar());
    }

    private StringType readText() throws IOException {
        if (read() != '"') throw new IOException("Invalid text: expected '\"'");
        return readString();
    }

    /**
     * Reads a plain number, without the type marker used by the non-vector parts of USO. In addition to the digits,
     * this accepts a leading minus sign and an exponent, since vectors regularly hold negative and huge values.
     */
    private String readNumberText() throws IOException {
        StringBuilder builder = new StringBuilder();

        int read = read();
        if (read == '-') {
            builder.append('-');
            read = read();
        }

        boolean decimal = false;
        boolean exponent = false;
        while (read != -1) {
            if (Character.isDigit(read)) {
                builder.append((char) read);
            } else if (read == '.' && !decimal && !exponent) {
                decimal = true;
                builder.append('.');
            } else if ((read == 'e' || read == 'E') && !exponent) {
                exponent = true;
                builder.append((char) read);
                read = read();
                if (read == '-' || read == '+') {
                    builder.append((char) read);
                } else {
                    unread();
                }
            } else {
                unread();
                break;
            }
            read = read();
        }

        if (builder.length() == 0 || !Character.isDigit(builder.charAt(builder.length() - 1))) {
            throw new IOException("Invalid number");
        }

        return builder.toString();
    }

    private DataType<?> readBoolean() throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int r = read();
            if (r == -1) break;
            if (!Character.isAlphabetic(r)) break;
            builder.append((char) r);
        }

        unread();

        String string = builder.toString();
        if (string.equalsIgnoreCase("true")) return new BooleanType(true);
        if (string.equalsIgnoreCase("false")) return new BooleanType(false);
        throw new IOException("Invalid boolean: " + string);
    }

    private DataType<?> readArray() throws IOException {
        int read = read();
        switch ((char) read) {
            case 'b':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readByteArray();
            case 's':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readShortArray();
            case 'i':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readIntArray();
            case 'l':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readLongArray();
            case 'f':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readFloatArray();
            case 'd':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readDoubleArray();
            case 'B':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readBitSet();
            case 'c':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readCharArray();
            case 'z':
                if (read() != ';') throw new IOException("Invalid array: expected ';'");
                return readBooleanArray();
            default:
                throw new IOException("Invalid array");
        }
    }

    private BooleanArrayType readBooleanArray() throws IOException {
        boolean[] booleans = new boolean[0];
        while (true) {
            booleans = add(booleans, ((BooleanType) readBoolean()).getBooleanValue());

            int r = read();
            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new BooleanArrayType(booleans);
    }

    private CharArrayType readCharArray() throws IOException {
        char[] chars = new char[0];
        while (true) {
            int r;
            if ((r = read()) == -1) {
                throw new EOFException("Invalid character: EOF");
            }

            if (r != '\'') {
                throw new IOException("Invalid character: expected ' but got " + (char) r);
            }

            char c = readChar();

            r = read();

            chars = add(chars, c);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected , or ) but got " + (char) r);
        }

        return new CharArrayType(chars);
    }

    private ByteArrayType readByteArray() throws IOException {
        byte[] bytes = new byte[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r)) break;
                builder.append((char) r);
                first = false;
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }


            byte number = Byte.parseByte(builder.toString());
            bytes = add(bytes, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new ByteArrayType(bytes);
    }

    private ShortArrayType readShortArray() throws IOException {
        short[] shorts = new short[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r)) break;
                builder.append((char) r);
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }

            short number = Short.parseShort(builder.toString());
            shorts = add(shorts, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new ShortArrayType(shorts);
    }

    private IntArrayType readIntArray() throws IOException {
        int[] ints = new int[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r)) break;
                builder.append((char) r);
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }

            int number = Integer.parseInt(builder.toString());
            ints = add(ints, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new IntArrayType(ints);
    }

    private LongArrayType readLongArray() throws IOException {
        long[] longs = new long[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r)) break;
                builder.append((char) r);
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }

            long number = Long.parseLong(builder.toString());
            longs = add(longs, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new LongArrayType(longs);
    }

    private FloatArrayType readFloatArray() throws IOException {
        float[] floats = new float[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r) && r != '.') break;
                builder.append((char) r);
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }

            float number = Float.parseFloat(builder.toString());
            floats = add(floats, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new FloatArrayType(floats);
    }

    private DoubleArrayType readDoubleArray() throws IOException {
        double[] doubles = new double[0];
        while (true) {
            StringBuilder builder = new StringBuilder();
            int r;
            boolean first = true;
            while (true) {
                r = read();
                if (first && r == '-') {
                    builder.append((char) r);
                    first = false;
                    continue;
                }
                if (!Character.isDigit(r) && r != '.') break;
                builder.append((char) r);
            }

            if (r == -1) {
                throw new IOException("Invalid number");
            }

            double number = Double.parseDouble(builder.toString());
            doubles = add(doubles, number);

            if (r == ',') continue;
            if (r == ')') break;
            throw new IOException("Invalid array: expected ',' or ')' but got " + (char) r);
        }

        return new DoubleArrayType(doubles);
    }

    private char readChar() throws IOException {
        int read = read();
        if (read == '\\') {
            read = read();
            if (read == 't') {
                read = '\t';
            } else if (read == 'n') {
                read = '\n';
            } else if (read == 'r') {
                read = '\r';
            } else if (read == 'b') {
                read = '\b';
            } else if (read == 'f') {
                read = '\f';
            } else if (read == '0') {
                read = '\0';
            } else if (read == 'u') {
                read = read();
                read = (read << 4) + read();
                read = (read << 4) + read();
                read = (read << 4) + read();
            }
        } else if (read == -1) {
            throw new EOFException("Invalid char: reached end of stream");
        }

        if (read() != '\'') throw new IOException("Invalid char: expected ' but got " + (char) read);
        return (char) read;
    }

    private byte[] add(byte[] bytes, byte number) {
        byte[] newBytes = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private short[] add(short[] bytes, short number) {
        short[] newBytes = new short[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private int[] add(int[] bytes, int number) {
        int[] newBytes = new int[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private long[] add(long[] bytes, long number) {
        long[] newBytes = new long[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private float[] add(float[] bytes, float number) {
        float[] newBytes = new float[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private double[] add(double[] bytes, double number) {
        double[] newBytes = new double[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private char[] add(char[] bytes, char number) {
        char[] newBytes = new char[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private boolean[] add(boolean[] bytes, boolean number) {
        boolean[] newBytes = new boolean[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = number;
        return newBytes;
    }

    private BitSetType readBitSet() throws IOException {
        BitSet set = new BitSet();
        int i = 0;

        loop:
        while (true) {
            int read = read();
            switch (read) {
                case '0':
                    set.clear(i);
                    break;
                case '1':
                    set.set(i);
                    break;
                case ';':
                    break loop;
                case -1:
                    throw new EOFException("Invalid bitset: EOF");
                default:
                    throw new IOException("Invalid bitset: expected '0', '1' or ';', got " + (char) read);
            }
            i++;
        }

        return new BitSetType(set);
    }

    private DataType<?> readNumber(int read) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append((char) read);
        while (true) {
            read = read();
            if (!Character.isDigit(read) && read != '.') break;
            builder.append((char) read);
        }

        if (read == -1) {
            throw new IOException("Invalid number");
        }

        switch ((char) read) {
            case 'b':
                return new ByteType(Byte.parseByte(builder.toString()));
            case 's':
                return new ShortType(Short.parseShort(builder.toString()));
            case 'i':
                return new IntType(Integer.parseInt(builder.toString()));
            case 'l':
                return new LongType(Long.parseLong(builder.toString()));
            case 'f':
                return new FloatType(Float.parseFloat(builder.toString()));
            case 'd':
                return new DoubleType(Double.parseDouble(builder.toString()));
            case 'I':
                return new BigIntType(new BigInteger(builder.toString()));
            case 'D':
                return new BigDecType(new BigDecimal(builder.toString()));
            default:
                throw new IOException("Invalid number");
        }
    }

    private DataType<?> readList() throws IOException {
        DataType<?> dataType = readUso();
        if (dataType == null) {
            throw new IOException("Invalid list: expected at least one element");
        }

        int id = dataType.id();
        int read = read();
        if (read == ']') {
            ListType<DataType<?>> list = new ListType<>(id);
            list.add(dataType);
            return list;
        } else if (read != ',') {
            throw new IOException("Invalid list: expected ',' or ']'");
        }

        readWhitespace();

        ListType<DataType<?>> list = new ListType<>(id);
        list.add(dataType);
        while (true) {
            DataType<?> cur = readUso();
            if (cur == null) {
                throw new IOException("Invalid list: invalid element at index " + list.size());
            }

            if (cur.id() != id) {
                throw new IOException("Invalid list, ID mismatch: should be " + id + " but was " + cur.id());
            }

            list.add(cur);

            readWhitespace();
            read = read();
            if (read == ',') {
                readWhitespace();
                read = read();
                if (read == ']') {
                    return list;
                } else {
                    unread();
                }
            } else if (read == ']') {
                return list;
            } else {
                throw new IOException("Invalid list: expected ',' or ']' but got " + (char) read);
            }
        }
    }

    private DataType<?> readMap() throws IOException {
        MapType map = new MapType();
        int read = read();
        while (read != '}') {
            if (read != '"') throw new IOException("Invalid map: expected '\"' but got " + (char) read);
            StringType key = readString();

            readWhitespace();
            if (read() != ':') throw new IOException("Invalid map: expected ':' but got " + (char) read);
            read();
            readWhitespace();
            DataType<?> value = readUso();

            map.put(key.getValue(), value);

            readWhitespace();
            read = read();
            if (read == ',') {
                readWhitespace();
                read = read();
                if (read == '}') {
                    break;
                }
            } else if (read == '}') {
                break;
            } else {
                throw new IOException("Invalid map: expected ',' or '}' but got " + (char) read);
            }
        }

        return map;
    }

    private StringType readString() {
        StringBuilder builder = new StringBuilder();
        int read = read();
        while (read != '"') {
            builder.append((char) read);
            if (read == '\\') {
                read = read();
                if (read == 'n') {
                    builder.append('\n');
                } else if (read == 'r') {
                    builder.append('\r');
                } else if (read == 't') {
                    builder.append('\t');
                } else if (read == 'b') {
                    builder.append('\b');
                } else if (read == 'f') {
                    builder.append('\f');
                } else if (read == '0') {
                    builder.append('\0');
                } else if (read == 'u') {
                    builder.append((char) (read() << 12
                            | read() << 8
                            | read() << 4
                            | read()));
                } else {
                    builder.append((char) read);
                }
            }
            read = read();
        }
        return new StringType(builder.toString());
    }

    private void readWhitespace() {
        while (true) {
            int read = read();
            if (!Character.isWhitespace(read)) {
                unread();
                return;
            }
        }
    }

    private UUIDType readUUID() throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int read = read();
            if (read == '>') break;

            if (read == -1) throw new EOFException("Invalid UUID: EOF");

            if (Character.isWhitespace(read)) continue;

            builder.append((char) read);
        }

        try {
            return new UUIDType(UUID.fromString(builder.toString()));
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid UUID: " + builder, e);
        }
    }

    private int unread() {
        if (this.pos <= 0) {
            return -1;
        }

        return this.chars[--this.pos];
    }

    private int read() {
        if (this.pos >= this.chars.length) {
            return -1;
        }

        return this.chars[this.pos++];
    }

    public DataType<?> parse() throws IOException {
        try {
            return readUso();
        } catch (Exception e) {
            throw new IOException("Unable to parse USO at pos " + pos + ": " + e.getMessage(), e);
        }
    }
}
