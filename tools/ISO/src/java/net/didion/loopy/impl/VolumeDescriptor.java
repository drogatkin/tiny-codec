/* 
 * Copyright (c) 2004-2006, Loudeye Corp. All Rights Reserved.
 * Last changed by: $Author: jdidion $
 * Last changed at: $DateTime$
 * Revision: $Revision: 1.1.1.1 $
 */
package net.didion.loopy.impl;

import net.didion.loopy.FileEntry;

import java.io.IOException;

public interface VolumeDescriptor {
    boolean read(byte[] buffer) throws IOException;
    FileEntry getRootEntry();
}
