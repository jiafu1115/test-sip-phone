package com.googlecode.test.phone.sip.jain;

import gov.nist.javax.sip.address.*;
import gov.nist.javax.sip.header.AddressParametersHeader;

 
public final class Also extends AddressParametersHeader implements AlsoHeader {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Also() {
        super(NAME);
    }

    /**
     * Encode the header content into a String.
     * @return String
     */
    public StringBuilder encodeBody(StringBuilder retval) {
        if (address == null)
            return null;        
        if (address.getAddressType() == AddressImpl.ADDRESS_SPEC) {
            retval.append(LESS_THAN);
        }
        address.encode(retval);
        if (address.getAddressType() == AddressImpl.ADDRESS_SPEC) {
            retval.append(GREATER_THAN);
        }

        if (!parameters.isEmpty()) {
            retval.append(SEMICOLON); 
            parameters.encode(retval);
        }
        return retval;
    }
}
 