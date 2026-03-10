/**
 */
package mediaflow;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Transcoder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.Transcoder#getVideoCodec <em>Video Codec</em>}</li>
 *   <li>{@link mediaflow.Transcoder#getAudioCodec <em>Audio Codec</em>}</li>
 *   <li>{@link mediaflow.Transcoder#getContainer <em>Container</em>}</li>
 *   <li>{@link mediaflow.Transcoder#getBitrate <em>Bitrate</em>}</li>
 * </ul>
 *
 * @see mediaflow.MediaflowPackage#getTranscoder()
 * @model
 * @generated
 */
public interface Transcoder extends Transformer {
	/**
	 * Returns the value of the '<em><b>Video Codec</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Video Codec</em>' attribute.
	 * @see #setVideoCodec(String)
	 * @see mediaflow.MediaflowPackage#getTranscoder_VideoCodec()
	 * @model
	 * @generated
	 */
	String getVideoCodec();

	/**
	 * Sets the value of the '{@link mediaflow.Transcoder#getVideoCodec <em>Video Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Video Codec</em>' attribute.
	 * @see #getVideoCodec()
	 * @generated
	 */
	void setVideoCodec(String value);

	/**
	 * Returns the value of the '<em><b>Audio Codec</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Audio Codec</em>' attribute.
	 * @see #setAudioCodec(String)
	 * @see mediaflow.MediaflowPackage#getTranscoder_AudioCodec()
	 * @model
	 * @generated
	 */
	String getAudioCodec();

	/**
	 * Sets the value of the '{@link mediaflow.Transcoder#getAudioCodec <em>Audio Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Audio Codec</em>' attribute.
	 * @see #getAudioCodec()
	 * @generated
	 */
	void setAudioCodec(String value);

	/**
	 * Returns the value of the '<em><b>Container</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Container</em>' attribute.
	 * @see #setContainer(String)
	 * @see mediaflow.MediaflowPackage#getTranscoder_Container()
	 * @model
	 * @generated
	 */
	String getContainer();

	/**
	 * Sets the value of the '{@link mediaflow.Transcoder#getContainer <em>Container</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Container</em>' attribute.
	 * @see #getContainer()
	 * @generated
	 */
	void setContainer(String value);

	/**
	 * Returns the value of the '<em><b>Bitrate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bitrate</em>' attribute.
	 * @see #setBitrate(int)
	 * @see mediaflow.MediaflowPackage#getTranscoder_Bitrate()
	 * @model
	 * @generated
	 */
	int getBitrate();

	/**
	 * Sets the value of the '{@link mediaflow.Transcoder#getBitrate <em>Bitrate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bitrate</em>' attribute.
	 * @see #getBitrate()
	 * @generated
	 */
	void setBitrate(int value);

} // Transcoder
