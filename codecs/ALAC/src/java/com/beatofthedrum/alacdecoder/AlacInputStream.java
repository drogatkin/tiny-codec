package com.beatofthedrum.alacdecoder;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;

/**
 * Author: Denis Tulskiy Date: 4/7/11
 */
public interface AlacInputStream extends DataInput, Closeable {

	public void seek(long pos) throws IOException;

	public int read(byte[] b, int off, int len) throws IOException;

}
