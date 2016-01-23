package net.didion.loopy;

import java.io.Closeable;
import java.io.IOException;

public interface AccessStream extends Closeable {
	long length() throws IOException;

	void seek(long pointer) throws IOException;

	int read(byte[] b) throws IOException;

	int read(byte[] b, int off, int len) throws IOException;

	int read() throws IOException;
}
