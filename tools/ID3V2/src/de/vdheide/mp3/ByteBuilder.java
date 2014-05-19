// ByteBuilder.java
//
// $Id: ByteBuilder.java,v 1.6 2008/01/03 04:35:51 dmitriy Exp $
//
// de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
// Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the
// Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA  02111-1307, USA.

package de.vdheide.mp3;

import java.io.ByteArrayOutputStream;


/**
 * This does the opposite of Parser, i.e. it takes Strings or byte array,
 * parses them and adds them to a byte array.
 * <p>
 * Text encoding is set with one of the constants:
 * NONE: ISO-8859-1 and no encoding byte
 * ISO: ISO-8859-1 and encoding byte
 * UNICODE: Unicode and encoding byte
 */
public class ByteBuilder
{   
    /**
     * Creates a new instance
     *
     * @param encoding Encoding to use (see above)
     */
    public ByteBuilder(byte encoding)
    {
	arr = new ByteArrayOutputStream();
	this.encoding = encoding;
	checkEncoding();
    }


    /**
     * Creates a new instance with an estimation of the size needed.
     * It is most efficient when this estimation is the real size, but
     * it creates no error if it is not. 
     * <p>
     * Text encoding is set with one of the constants defined in TextFrame:
     * <ol>
     * <li>NONE: ISO-8859-1 and no encoding byte
     * <li>ISO: ISO-8859-1 and encoding byte
     * <li>UNICODE: Unicode and encoding byte
     * <li>UTF16BE:
     * <li>UTF8:
     * </ol> 
     *
     * @param encoding Encoding to use (see above)
     * @param size Size estimate
     */
    public ByteBuilder(byte encoding, int size)
    {
	arr = new ByteArrayOutputStream(size);
	this.encoding = encoding;
	checkEncoding();
    }
    
    /**
     * Write a single byte to the end of the so far saved contents.
     *
     * @param put Byte to insert
     */
    public void put(byte put)
    {
	arr.write(put);
    }
    
    
    /**
     * Write a byte array to the end of the contents saved so far.
     *
     * @param put Byte array to insert
     */
    public void put(byte []put)
    {
	arr.write(put, 0, put.length);
    }
    

    /**
     * Write a text according to the selected encoding
     *
     * @param put Text to write
     */
	public void put(String put) { 
		// encode string
		byte []encoded = null;
		
		switch (encoding) { 
		case TextFrame.NONE:
		case TextFrame.ISO:
			try {
				encoded = put.getBytes(MP3File.DEF_ASCII_ENCODING);
			} catch (java.io.UnsupportedEncodingException e) {
				// cannot happen
			}
			break;
		case TextFrame.UNICODE:
			try {
				// BOM 2 bytes are written at checkEncoding()
				encoded = put.getBytes("UTF-16LE"/*MP3File.UNICODE_ENCODING*/);
				//System.err.println("UCOde for "+put+" is: 0x"+rogatkin.BaseController.bytesToHex(encoded));
	    		arr.write(255); // FF
	    		arr.write(254); // FE
			} catch (java.io.UnsupportedEncodingException e) {
				// cannot happen
			}
			break;
		case TextFrame.UTF16BE:
			try {
				encoded = put.getBytes("UTF-16BE");
			} catch (java.io.UnsupportedEncodingException e) {
				// cannot happen
			}
			break;
		case TextFrame.UTF8:
			try {
				encoded = put.getBytes("UTF-8");
			} catch (java.io.UnsupportedEncodingException e) {
				// cannot happen
			}
		}
		
		try {
			arr.write(encoded);
		} catch (java.io.IOException e) {
			// How can this possibly happen?
		}
	}
    

    /**
     * Read contents as byte array
     */
	public byte []getBytes() { 
		   //System.err.println("array is: 0x"+rogatkin.BaseController.bytesToHex(arr.toByteArray()));
	return arr.toByteArray();
    }


    protected ByteArrayOutputStream arr;
    protected byte encoding;
    

    /**
     * Check encoding set and write encoding byte if appropriate
     */
    protected void checkEncoding()
    {
    	if (encoding != TextFrame.NONE)
    		arr.write(encoding);
    }
}
