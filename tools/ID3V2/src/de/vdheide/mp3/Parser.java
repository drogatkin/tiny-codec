// Parser.java
// $Id: Parser.java,v 1.10 2008/01/03 04:35:51 dmitriy Exp $
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

//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class used to parse a byte array and extract text or binary information
 * It maintained a pointer so that parse operations are always performed
 * on the first byte after end of the last operation.
 * All parsing is done until a 0x00 (or 0x00 0x00 for Unicode) is encountered.
 * Byte position is then placed on first byte after this terminator.
 */
class Parser
{
    /**
     * Create new instance, use complete array
     *
     * @param in Array to parse
     * @param encoding True: First byte of input contains encoding.
     *                 False: Encoding is set to ISO-8859-1
     */
    public Parser(byte []in, boolean encoding, String targetEncoding)
    {
	this(in, encoding, 0, in.length - 1);	//System.err.println("Created for "+targetEncoding);
	if (targetEncoding != null)		this.targetEncoding = targetEncoding;
    }
    	public Parser(byte []in, boolean encoding) {
		this(in, encoding, 0, in.length - 1);	}
    
    /**
     * Create new instance, use part of array
     *
     * @param in Array to parse
     * @param encoding True: First byte of input contains encoding.
     *                 False: Encoding is set to ISO-8859-1
     * @param start Offset of first byte to parse
     * @param stop Offset of last byte to parse
     */
    public Parser(byte []in, boolean encoding, int start, int stop)
    {
	this.in = in;
	this.pos = start;
	this.stop = stop;
	
	if (encoding == true) {
	    parseEncoding();
	} else {
	    this.encoding = TextFrame.NONE;
	}
    }
    
    
    /**
     * Set byte position
     *
     * @param pos New byte position
     */
    public void setPosition(int pos)
    {
	this.pos = pos;
    }


    /**
     * @return Byte position
     */
    public int getPosition()
    {
	return pos;
    }

    
    /**
     * Parse encoding byte. This is automatically done
     * in the constructor if encoding is set to true.
     */
	public void parseEncoding()
	{
		encoding = in[pos];
		pos++;
	}


    /**
     * Parse next bytes as text according to set encoding
     */
    public String parseText() throws ParseException
    {
	return parseText(this.encoding);
    } 


    /**
     * Parse next bytes as text according to given encoding
     */ 
	public String parseText(byte encoding) throws ParseException
	{
		try {
			// find termination
			int term = pos;
			boolean doubleByte = encoding == TextFrame.UNICODE || encoding == TextFrame.UTF16BE;
			
			if (doubleByte == false) {
				while (in[term]!=0 && term<stop) {
					term++;
				}
			} else {
				while (term<stop-1 && (in[term]!=0 || in[term+1]!=0)) {
					term+=2;
				}
				
			}

			if (in[term]!=0 && encoding != TextFrame.NONE)
				term = stop + 1;
			
			// convert
			String ret = null;
			String charSet = targetEncoding;
			switch(encoding) {
			case TextFrame.UTF8:
				charSet = "UTF-8";
				break;
			case  TextFrame.UTF16BE:
				charSet = "UTF-16BE";
				break;
			case  TextFrame.UNICODE:
				charSet = "UTF-16LE";
				//System.err.printf("!!!!!!pos0 %d, pos1 %d%n", in [pos], in [pos+1]);
				// check for BOM (FF FE or FE FF)
				if (in [pos] == -1 && in[pos+1] == -2)
					pos+=2;
				break;
			}
			try {
				ret = new String(in, pos, term - pos, charSet);
			} catch (java.io.UnsupportedEncodingException e) {
				// cannot happen
			}
			
			// advance position marker
			pos = term + (doubleByte ? 2 : 1);
			
			return ret;
		} catch (Exception e) {
			throw (ParseException)new ParseException().initCause(e);
		}
	} 

    /**
     * Read next bytes to end (no real parsing, just copying)
     */
    public byte[] parseBinary() throws ParseException
    {
	return parseBinary(stop - pos + 1);
    }
    

    /**
     * Read next <code>number</code> bytes (no real parsing, just copying)
     *
     * @param number Number of bytes to read
     */
    public byte[] parseBinary(int number) throws ParseException
    {
	try {
	    byte []ret = new byte[number];
	    System.arraycopy(in, pos, ret, 0, number);
	
	    pos += number; // no more reading possible...
	    
	    return ret;
	} catch (Exception e) {
	    throw new ParseException();
	}
    }
      
    public Date parseDate() throws ParseException {
    	// 
    	String dateString = parseText();
    	if (dateString != null && dateString.length() > 1)
			try {
				return new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss".substring(0, dateString.length())).parse(dateString);
			} catch (java.text.ParseException e) {
				throw new ParseException();
			}
			return null;
    }

    private byte []in;
    private int stop;
    private int pos; // Next byte to parse
    private byte encoding; // Encoding used for text	private String targetEncoding = MP3File.DEF_ASCII_ENCODING;
   
}
