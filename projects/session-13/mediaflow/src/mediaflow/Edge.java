/**
 */
package mediaflow;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.Edge#getSource <em>Source</em>}</li>
 *   <li>{@link mediaflow.Edge#getTarget <em>Target</em>}</li>
 * </ul>
 *
 * @see mediaflow.MediaflowPackage#getEdge()
 * @model
 * @generated
 */
public interface Edge extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(Port)
	 * @see mediaflow.MediaflowPackage#getEdge_Source()
	 * @model
	 * @generated
	 */
	Port getSource();

	/**
	 * Sets the value of the '{@link mediaflow.Edge#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(Port value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(Port)
	 * @see mediaflow.MediaflowPackage#getEdge_Target()
	 * @model
	 * @generated
	 */
	Port getTarget();

	/**
	 * Sets the value of the '{@link mediaflow.Edge#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(Port value);

} // Edge
