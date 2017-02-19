/*
 * CompositeChunk
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.expressions.Expression;

public class CompositeChunk extends Chunk {

    public final Chunk chunkOf;

    public CompositeChunk(ChunkType type, Expression start, Expression end, Chunk chunkOf) {
        super(type, start, end);
        this.chunkOf = chunkOf;
    }

    public ChunkType getMutatedChunkType () {
        return getMutatedChunkType(this);
    }

    public static ChunkType getMutatedChunkType (Chunk c) {
        if (c instanceof CompositeChunk) {
            return getMutatedChunkType(((CompositeChunk) c).chunkOf);
        } else {
            return c.type;
        }
    }
}
