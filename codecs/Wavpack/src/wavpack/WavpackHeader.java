/*
** WavpackHeader.java
**
** Copyright (c) 2007 - 2008 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package wavpack;
public class WavpackHeader
{

    static char ckID[] = new char[4];
    long ckSize;	// was uint32_t in C
    short version;
    short track_no, index_no;	// was uchar in C
    long total_samples, block_index, block_samples, flags, crc;	// was uint32_t in C
    int status = 0;	// 1 means error
}