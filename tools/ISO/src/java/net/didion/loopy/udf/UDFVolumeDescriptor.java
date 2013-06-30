/* 
 * Copyright (c) 2004-2006, Loudeye Corp. All Rights Reserved.
 * Last changed by: $Author: jdidion $
 * Last changed at: $DateTime$
 * Revision: $Revision: 1.1.1.1 $
 */
package net.didion.loopy.udf;

import net.didion.loopy.impl.VolumeDescriptor;
import net.didion.loopy.FileEntry;

import java.io.IOException;

public class UDFVolumeDescriptor implements VolumeDescriptor {
    private UDFFileSystem fs;

    public UDFVolumeDescriptor(UDFFileSystem fs) {
        this.fs = fs;    
    }

    public boolean read(byte[] buffer) throws IOException {
        return false;
    }

    public FileEntry getRootEntry() {
        return null;
    }
}