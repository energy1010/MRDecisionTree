/**
 * 
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.younger.struct.Alist;
import com.younger.struct.ListInterface;

/**
 * @author Administrator
 *
 */
public class AlistTest {

	private ListInterface alist ;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
			alist	= new Alist(10);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#getEntry()}.
	 */
	@Test
	public void testGetEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#setEntry(T[])}.
	 */
	@Test
	public void testSetEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#setLength(int)}.
	 */
	@Test
	public void testSetLength() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#Alist()}.
	 */
	@Test
	public void testAlist() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#Alist(int)}.
	 */
	@Test
	public void testAlistInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#add(java.lang.Object)}.
	 */
	@Test
	public void testAddT() {
		alist.add(2);
		alist.add(4);
		alist.add(5);
		alist.add(1);
		alist.add(3);
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#add(int, java.lang.Object)}.
	 */
	@Test
	public void testAddIntT() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#clear()}.
	 */
	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#contains(java.lang.Object)}.
	 */
	@Test
	public void testContains() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#display()}.
	 */
	@Test
	public void testDisplay() {
		alist.display();
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#getEntry(int)}.
	 */
	@Test
	public void testGetEntryInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#getLength()}.
	 */
	@Test
	public void testGetLength() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#isFull()}.
	 */
	@Test
	public void testIsFull() {
		alist.isFull();
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#remove(int)}.
	 */
	@Test
	public void testRemove() {
		alist.remove(2);
	}

	/**
	 * Test method for {@link com.younger.struct.Alist#replace(int, java.lang.Object)}.
	 */
	@Test
	public void testReplace() {
		fail("Not yet implemented");
	}

}
