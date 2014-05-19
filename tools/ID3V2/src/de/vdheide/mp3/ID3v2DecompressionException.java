// ID3v2DecompressionException
//
// $Id: ID3v2DecompressionException.java,v 1.2 2007/12/27 05:23:29 dmitriy Exp $
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

/**
 * Thrown when decompression of an ID3v2Frame failed.
 */

package de.vdheide.mp3;

public class ID3v2DecompressionException extends ID3v2Exception {

	public ID3v2DecompressionException() {
		super();
	}

	public ID3v2DecompressionException(String message) {
		super(message);
	}
}
