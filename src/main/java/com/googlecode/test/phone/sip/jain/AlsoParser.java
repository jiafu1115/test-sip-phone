
package com.googlecode.test.phone.sip.jain;

import java.lang.reflect.Method;
import java.text.ParseException;

import gov.nist.core.LexerCore;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.parser.AddressParametersParser;
import gov.nist.javax.sip.parser.Lexer;
import gov.nist.javax.sip.parser.TokenTypes;

 
public class AlsoParser extends AddressParametersParser {
  
    private static final int TOK = TokenTypes.START + 99;
 
	public AlsoParser(String also) {
        super(also);
    }

    protected AlsoParser(Lexer lexer) {
        super(lexer);
    }
    
    public SIPHeader parse() throws ParseException {
    	Class<? extends LexerCore> lexerClazz = LexerCore.class;
		try {
	    	Method declaredMethod = lexerClazz.getDeclaredMethod("addKeyword", String.class, int.class);
	    	declaredMethod.setAccessible(true);
	    	declaredMethod.invoke(this.lexer, Also.NAME.toUpperCase() , TOK);
		}  catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
     	 
        headerName(TOK);
        Also also = new Also();
        super.parse(also);
        this.lexer.match('\n');
        return also;
    }
    
 
}
