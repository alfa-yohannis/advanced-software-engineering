/**
 */
package mediaflow;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.Resource#getMediaType <em>Media Type</em>}</li>
 *   <li>{@link mediaflow.Resource#getUri <em>Uri</em>}</li>
 *   <li>{@link mediaflow.Resource#isExternal <em>External</em>}</li>
 * </ul>
 *
 * @see mediaflow.MediaflowPackage#getResource()
 * @model
 * @generated
 */
public interface Resource extends Node {
	/**
	 * Returns the value of the '<em><b>Media Type</b></em>' attribute.
	 * The literals are from the enumeration {@link mediaflow.MediaType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Media Type</em>' attribute.
	 * @see mediaflow.MediaType
	 * @see #setMediaType(MediaType)
	 * @see mediaflow.MediaflowPackage#getResource_MediaType()
	 * @model
	 * @generated
	 */
	MediaType getMediaType();

	/**
	 * Sets the value of the '{@link mediaflow.Resource#getMediaType <em>Media Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Media Type</em>' attribute.
	 * @see mediaflow.MediaType
	 * @see #getMediaType()
	 * @generated
	 */
	void setMediaType(MediaType value);

	/**
	 * Returns the value of the '<em><b>Uri</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uri</em>' attribute.
	 * @see #setUri(String)
	 * @see mediaflow.MediaflowPackage#getResource_Uri()
	 * @model
	 * @generated
	 */
	String getUri();

	/**
	 * Sets the value of the '{@link mediaflow.Resource#getUri <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uri</em>' attribute.
	 * @see #getUri()
	 * @generated
	 */
	void setUri(String value);

	/**
	 * Returns the value of the '<em><b>External</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External</em>' attribute.
	 * @see #setExternal(boolean)
	 * @see mediaflow.MediaflowPackage#getResource_External()
	 * @model
	 * @generated
	 */
	boolean isExternal();

	/**
	 * Sets the value of the '{@link mediaflow.Resource#isExternal <em>External</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External</em>' attribute.
	 * @see #isExternal()
	 * @generated
	 */
	void setExternal(boolean value);

} // Resource
