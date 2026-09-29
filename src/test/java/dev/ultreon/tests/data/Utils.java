package dev.ultreon.tests.data;

import dev.ultreon.ubo.types.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.UUID;

public class Utils {
    static MapType createExampleMap() {
        BitSet bitSet = new BitSet(16);
        bitSet.set(0);
        bitSet.set(2);
        bitSet.set(5);
        bitSet.set(8);
        bitSet.set(12);
        bitSet.set(14);
        bitSet.set(15);

        MapType type = new MapType();
        type.putInt("integer", 123456789);
        type.putString("string", "Hello World");
        type.putByte("byte", 64);
        type.putShort("short", 1024);
        type.putUUID("uuid", UUID.nameUUIDFromBytes("Hello World".getBytes()));
        type.putBitSet("bitSet", bitSet);
        type.putDouble("double", 123456.123456789);
        type.putFloat("float", 123.456f);
        type.putLong("long", 7342041283402173783L);
        type.putLongArray("longArray", new long[]{393784924237483L, 485934279453232L, 78523942493L, 763828943274932L, 7534792734329L, 578432640347329432L});
        type.putIntArray("integerArray", new int[]{970423324, 324354353, 546345436, 345436345, 56, 654354354, 353454354, 54352543, 364335, 545});
        type.putShortArray("shortArray", new short[]{9234, 433, 5453, 20495, 94, 5385, 38, 9843, 572, 34, 729, 4, 485, 3986, 2964, 64, 5843, 20275, 3821, 14573, 3584, 995});
        type.putByteArray("byteArray", new byte[]{-32, 109, -4, 65, -74, 111, 35, 95, -10, -104, 59, 21, -73, 87, -49, 79, 63, -90, 121, 103, 1});
        type.putCharArray("characterArray", new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'});
        type.putChar("character", '#');
        type.putChar("unicodeSymbol", '\u263A');
        type.putBoolean("boolean", false);
        type.putBoolean("booleanTrue", true);
        type.putBigInt("bigInteger", new BigInteger("9342759832409326583274320943265943209407326940327842163498"));
        type.putBigDec("bigDecimal", new BigDecimal("2480750435679032974329809463294032649281037216482019372198.547323843264398412730293619401264392837214982713626981326149213"));
        type.put("vec2", new Vector2Type<>(new IntType(2), new IntType(4)));
        type.put("vec3", new Vector3Type<>(new IntType(2), new IntType(4), new IntType(6)));
        type.put("vec4", new Vector4Type<>(new IntType(2), new IntType(4), new IntType(6), new IntType(8)));

        MapType inner = new MapType();
        inner.putString("message", "Hello, Glitch.");
        inner.putString("name", "Glitch");
        inner.putInt("highScore", 7638);
        inner.putLong("fileSize", 7_323_358_494L);
        type.put("Map", inner);

        ListType<StringType> list = new ListType<>();
        list.add(new StringType("Glitch"));
        list.add(new StringType("Qboi"));
        list.add(new StringType("QTechCommunity"));
        list.add(new StringType("UltreonTeam"));
        type.put("List", list);
        return type;
    }

    static File file(String name) {
        File file = new File("build/test-tmp", name);
        File parentFile = file.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdirs();
        return file;
    }
}
