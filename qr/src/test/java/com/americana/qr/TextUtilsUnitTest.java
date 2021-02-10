package com.americana.qr;


import com.americana.qr.utils.TextUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TextUtilsUnitTest local unit test, which used to test possible cases.
 */

public class TextUtilsUnitTest {

    /**
     * isEmpty checks, if we pass blank string then we will get false or not.
     */
    @Test
    public void isEmpty() {
        String str="UAE";
        assertFalse("", TextUtils.isEmpty(str.toString()));
    }

    /**
     * isNotEmpty checks, if we pass any string then we will get true or not.
     */
    @Test
    public void isNotEmpty() {
        String str="";
        assertTrue(TextUtils.isEmpty(str.toString()));
    }

    /**
     * checkWithNullValue checks, if we pass null then we will get true or not.
     */
    @Test
    public void checkWithNullValue(){
        String str=null;
        assertTrue(TextUtils.isEmpty(str));
    }
}