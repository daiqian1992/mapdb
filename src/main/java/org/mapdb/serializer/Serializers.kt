package org.mapdb.serializer

import org.mapdb.io.DataInput2
import org.mapdb.io.DataOutput2
import org.mapdb.io.DataOutput2ByteArray

object Serializers{


    /** Serializer for [java.lang.Integer] */
    @JvmStatic val INTEGER = object:Serializer<Int>{
        override fun serialize(out: DataOutput2, k: Int) {
            out.writeInt(k)
        }

        override fun deserialize(input: DataInput2): Int {
            return input.readInt()
        }
    }

    /** Serializer for [java.lang.Long] */
    @JvmStatic val LONG = object:Serializer<Long>{
        override fun serialize(out: DataOutput2, k: Long) {
            out.writeLong(k)
        }

        override fun deserialize(input: DataInput2): Long {
            return input.readLong()
        }
    }


    /** Serializer for [java.lang.String] */
    @JvmStatic val STRING = object:Serializer<String>{
        override fun serialize(out: DataOutput2, k: String) {
            out.writeUTF(k)
        }

        override fun deserialize(input: DataInput2): String {
            return input.readUTF()
        }
    }

    /** Serializer for `byte[]`, adds extra few bytes for array size */
    @JvmStatic val BYTE_ARRAY = object:Serializer<ByteArray>{
        override fun deserialize(input: DataInput2): ByteArray {
            val size = input.readPackedInt()
            val b = ByteArray(size)
            input.readFully(b)
            return b
        }

        override fun serialize(out: DataOutput2, k: ByteArray) {
            out.writePackedInt(k.size)
            out.sizeHint(k.size)
            out.write(k)
        }

    }


    /**
     * Serializer for `byte[]`, but does not add extra bytes for array size.
     *
     * Uses [org.mapdb.io.DataInput2.available] to determine array size on deserialization.
     */
    @JvmStatic val BYTE_ARRAY_NOSIZE = object:Serializer<ByteArray>{
        override fun deserialize(input: DataInput2): ByteArray {
            val size = input.available()
            val b = ByteArray(size)
            input.readFully(b)
            return b
        }

        override fun serialize(out: DataOutput2, k: ByteArray) {
            out.sizeHint(k.size)
            out.write(k)
        }

    }

    /** serialize record into ByteArray using given serializer */
    fun <K> serializeToByteArray(record: K, serializer: Serializer<K>): ByteArray {
        val out = DataOutput2ByteArray()
        serializer.serialize(out, record)
        return out.copyBytes()
    }


    /** serialize record into ByteArray using given serializer */
    fun <K> serializeToByteArrayNullable(record: K?, serializer: Serializer<K>): ByteArray? {
        return if(record==null) null
            else serializeToByteArray(record, serializer)
    }
}