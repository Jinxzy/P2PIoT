package milestone4;

import milestone2.chord.Key;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class KeyUnitTest {
	Long key1;
	int key2;

	String basekey = "localhost:50001";
	
	@Before
	public void setup() {

        key1 = Key.generate64BitsKey(basekey);
        key2 = Key.generate32BitsKey(basekey);
    }
	
	@After
	public void teardown() {

	}
	
	@Test
	public void keyComparisson() {

        System.out.println("Basekey: ");
        System.out.println(basekey);
        System.out.println("To 64 bits key: " + key1);
        System.out.println("To 32 bits key: " + key2);

		Assert.assertNotEquals(key1, new Long(key2));
	}


}
