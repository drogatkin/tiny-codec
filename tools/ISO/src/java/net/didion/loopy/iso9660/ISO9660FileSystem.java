package net.didion.loopy.iso9660;

import net.didion.loopy.AccessStream;
import net.didion.loopy.FileEntry;
import net.didion.loopy.LoopyException;
import net.didion.loopy.impl.AbstractBlockFileSystem;
import net.didion.loopy.impl.VolumeDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;

public class ISO9660FileSystem extends AbstractBlockFileSystem implements Constants {
    
	public ISO9660FileSystem(File file, boolean readOnly) throws LoopyException {
        super(file, readOnly, BLOCK_SIZE, RESERVED_BYTES);
    }
	
	public ISO9660FileSystem(AccessStream as) throws LoopyException {
        super(as, BLOCK_SIZE, RESERVED_BYTES);
    } 

    public InputStream getInputStream(FileEntry entry) {
        ensureOpen();
        return new EntryInputStream((ISO9660FileEntry) entry, this);
    }

    protected Enumeration enumerate(FileEntry rootEntry) {
        return new EntryEnumeration(this, (ISO9660FileEntry) rootEntry);
    }

    protected VolumeDescriptor createVolumeDescriptor() {
        return new ISO9660VolumeDescriptor(this);
    }

    byte[] readData(ISO9660FileEntry entry) throws IOException {
        long size = entry.getSize();
        if (size > Integer.MAX_VALUE)
        	throw new IOException("Size of entryy "+size+" exceeds system limitation for this operation");
        int bufSize = (int)size;
        byte[] buf = new byte[bufSize];
        readData(entry, 0, buf, 0, bufSize);
        return buf;
    }

    int readData(
            ISO9660FileEntry entry,
            long entryOffset,
            byte[] buffer,
            int bufferOffset,
            int len)
            throws IOException {
        long startPos = (entry.getStartBlock() * BLOCK_SIZE) + entryOffset;
        return readData(startPos, buffer, bufferOffset, len);
    }
}