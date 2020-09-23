/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.org.tspb.architect.form.validator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author telman
 */
public class FmsFormValidatorTest {

    public FmsFormValidatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of givenInvalidInput_whenValidating_thenInvalid method, of class
     * FmsFormValidator.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");
        FmsFormValidator instance = new FmsFormValidator();
        //assertNull(instance.validate());
        // fail("The test case is a prototype.");
    }

}
