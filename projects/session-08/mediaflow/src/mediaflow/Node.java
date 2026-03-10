/**
 */
package mediaflow;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.Node#getPorts <em>Ports</em>}</li>
 * </ul>
 *
 * @see mediaflow.MediaflowPackage#getNode()
 * @model abstract="true"
 * @generated
 */
public interface Node extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Ports</b></em>' containment reference list.
	 * The list contents are of type {@link mediaflow.Port}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ports</em>' containment reference list.
	 * @see mediaflow.MediaflowPackage#getNode_Ports()
	 * @model containment="true"
	 * @generated
	 */
	EList<Port> getPorts();

} // Node
