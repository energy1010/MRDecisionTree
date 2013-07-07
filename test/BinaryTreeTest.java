/**
 * 
 */


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.younger.struct.BinaryNode;
import com.younger.struct.BinaryNodeInterface;
import com.younger.struct.BinaryTree;

/**
 * @author Administrator
 *
 */
public class BinaryTreeTest {

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
		
		BinaryNodeInterface root = new BinaryNode(0);
		BinaryNodeInterface l1 = new BinaryNode(1);
		BinaryNodeInterface r1 = new BinaryNode(2);
		BinaryNodeInterface l11 = new BinaryNode(3);
		BinaryNodeInterface r12 = new BinaryNode(4);
		BinaryNodeInterface l21 = new BinaryNode(5);
		BinaryNodeInterface r22 = new BinaryNode(6);
		root.setLeftChild(l1);
		root.setRightChild(r1);
		l1.setLeftChild(l11);
		l1.setRightChild(r12);
		r1.setLeftChild(l21);
		r1.setRightChild(r22);
		
		BinaryTree tree = new BinaryTree(root);
		tree.getHeight();
		tree.getNumberOfNodes();
		tree.inorderTraverse(root);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
