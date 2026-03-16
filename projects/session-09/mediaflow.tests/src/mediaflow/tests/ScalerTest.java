/**
 */
package mediaflow.tests;

import junit.textui.TestRunner;

import mediaflow.MediaflowFactory;
import mediaflow.Scaler;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Scaler</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ScalerTest extends TransformerTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ScalerTest.class);
	}

	/**
	 * Constructs a new Scaler test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScalerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Scaler test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Scaler getFixture() {
		return (Scaler)fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(MediaflowFactory.eINSTANCE.createScaler());
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

} //ScalerTest
