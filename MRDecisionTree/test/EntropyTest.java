import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.decisionTree.Entropy;


public class EntropyTest {
	
	private static final Logger log = LoggerFactory.getLogger(EntropyTest.class);
	
	private Entropy entropy = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		entropy = Entropy.getInstance();
		Assert.assertNotNull(entropy);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEntropy() {
	}

	@Test
	public void testGetSplitEntropy() {
	}

	@Test
	public void testEntropyLn() {
	}

	@Test
	public void testEntropyOfDictionary() {
	}

	@Test
	public void testEntropyOfattributeClassDictionary() {
	}

	@Test
	public void testEntropyDictionaryOfQInteger() {
	}

	@Test
	public void testEntropyHashtableOfQListOfInteger() {
	}

	@Test
	public void testEntropyIntArray() {
		double value= entropy.entropy(9,7,8);
//		log.debug(" value: {}",value);
		System.out.println(value);
	}

	@Test
	public void testEntropyListOfInteger() {
//		entropy.entropy(groupList);
	}

	@Test
	public void testInfoIntArray() {
	}

	@Test
	public void testEntropyContinousHistogram() {
	}

	@Test
	public void testInfoVectorOfVectorOfInteger() {
	}

}
