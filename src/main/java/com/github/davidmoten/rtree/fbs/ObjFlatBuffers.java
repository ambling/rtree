package com.github.davidmoten.rtree.fbs;

/**
 *
 * For each generated class, allocate just one object and share by all wrappers,
 * the wrappers record the underlying ByteBuffer offset as the real data.
 * When need to use, the wrapper need to set the data.
 *
 * Therefore, there would be no more allocation than the algorithms that operate
 * on the in-memory java objects.
 */
public interface ObjFlatBuffers {

    /**
     * get the offest of internal data in the ByteBuffer
     * @return
     */
    int offest();

    /**
     * set of offset of internal generated class object
     */
    void set();
}
