/**
 */
package mediaflow.tests;

import junit.textui.TestRunner;

import mediaflow.MediaflowFactory;
import mediaflow.Transcoder;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Transcoder</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class TranscoderTest extends TransformerTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TranscoderTest.class);
	}

	/**
	 * Constructs a new Transcoder test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TranscoderTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Transcoder test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Transcoder getFixture() {
		return (Transcoder)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(MediaflowFactory.eINSTANCE.createTranscoder());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //TranscoderTest
