package dev.ultreon.ubo;

import dev.ultreon.ubo.types.*;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
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
            case 'f':
                this.unread();
                return readBoolean();
            case 'v':
                return readVector();
            default:
                if (Character.isDigit(read)) return readNumber(read);
                throw new IOException("Invalid USO: " + (char) read);
        }
    }

    private DataType<?> readVector() throws IOException {
        int size = read();
        if (size == -1) throw new EOFException("Invalid vector: EOF, expected size");

        if (read() != ':') throw new IOException("Invalid vector: expected ':' after size " + (char) size);

        int elementType = read();
        if (elementType == -1) throw new EOFException("Invalid vector: EOF, expected element type");

        switch ((char) size) {
            case '2':
                return readVector(2, (char) elementType);
            case '3':
                return readVector(3, (char) elementType);
            case '4':
                return readVector(4, (char) elementType);
            default:
                throw new IOException("Invalid vector count: " + (char) size);
        }
    }

    private DataType<?> readVector(int size, char elementType) throws IOException {
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

    private DataType<?> readVectorElement(char type) throws IOException {
        switch (type) {
            case 'b':
                return new ByteType(Byte.parseByte(readNumberText()));
            case 'z':
                return readBoolean();
            case 's':
                return new ShortType(Short.parseShort(readNumberText()));
            case 'i':
                return new IntType(Integer.parseInt(readNumberText()));
            case 'l':
                return new LongType(Long.parseLong(readNumberText()));
            case 'f':
                return new FloatType(Float.parseFloat(readNumberText()));
            case 'd':
                return new DoubleType(Double.parseDouble(readNumberText()));
            case 'I':
                return new BigIntType(new BigInteger(readNumberText()));
            case 'D':
                return new BigDecType(new BigDecimal(readNumberText()));
            case 'c':
                return readCharElement();
            case 't':
                return readText();
            default:
                throw new IOException("Invalid vector element type: " + type);
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
            default:
                throw new IOException("Invalid array");
        }
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
                    read();
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
