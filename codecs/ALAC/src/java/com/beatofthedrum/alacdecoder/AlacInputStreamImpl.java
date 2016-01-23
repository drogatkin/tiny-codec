package com.beatofthedrum.alacdecoder;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlacInputStreamImpl extends DataInputStream implements AlacInputStream {
	/**
	 * Creates a DataInputStream that uses the specified underlying InputStream.
	 *
	 * @param in
	 *            the specified input stream
	 */
	public AlacInputStreamImpl(InputStream in) {
		super(in);
	}

	public void seek(long pos) throws IOException {
		if (in instanceof FileInputStream) {
			((FileInputStream) in).getChannel().position(pos);
		}
	}
}
