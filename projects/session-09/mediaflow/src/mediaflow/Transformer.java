/**
 */
package mediaflow;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transformer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.Transformer#getBackend <em>Backend</em>}</li>
 *   <li>{@link mediaflow.Transformer#getCommand <em>Command</em>}</li>
 *   <li>{@link mediaflow.Transformer#getReplicas <em>Replicas</em>}</li>
 * </ul>
 *
 * @see mediaflow.MediaflowPackage#getTransformer()
 * @model abstract="true"
 * @generated
 */
public interface Transformer extends Node {
	/**
	 * Returns the value of the '<em><b>Backend</b></em>' attribute.
	 * The literals are from the enumeration {@link mediaflow.Backend}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Backend</em>' attribute.
	 * @see mediaflow.Backend
	 * @see #setBackend(Backend)
	 * @see mediaflow.MediaflowPackage#getTransformer_Backend()
	 * @model
	 * @generated
	 */
	Backend getBackend();

	/**
	 * Sets the value of the '{@link mediaflow.Transformer#getBackend <em>Backend</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Backend</em>' attribute.
	 * @see mediaflow.Backend
	 * @see #getBackend()
	 * @generated
	 */
	void setBackend(Backend value);

	/**
	 * Returns the value of the '<em><b>Command</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command</em>' attribute.
	 * @see #setCommand(String)
	 * @see mediaflow.MediaflowPackage#getTransformer_Command()
	 * @model
	 * @generated
	 */
	String getCommand();

	/**
	 * Sets the value of the '{@link mediaflow.Transformer#getCommand <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Command</em>' attribute.
	 * @see #getCommand()
	 * @generated
	 */
	void setCommand(String value);

	/**
	 * Returns the value of the '<em><b>Replicas</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Replicas</em>' attribute.
	 * @see #setReplicas(int)
	 * @see mediaflow.MediaflowPackage#getTransformer_Replicas()
	 * @model
	 * @generated
	 */
	int getReplicas();

	/**
	 * Sets the value of the '{@link mediaflow.Transformer#getReplicas <em>Replicas</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Replicas</em>' attribute.
	 * @see #getReplicas()
	 * @generated
	 */
	void setReplicas(int value);

} // Transformer
