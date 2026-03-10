/**
 */
package mediaflow.impl;

import mediaflow.MediaflowPackage;
import mediaflow.Transcoder;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transcoder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.impl.TranscoderImpl#getVideoCodec <em>Video Codec</em>}</li>
 *   <li>{@link mediaflow.impl.TranscoderImpl#getAudioCodec <em>Audio Codec</em>}</li>
 *   <li>{@link mediaflow.impl.TranscoderImpl#getContainer <em>Container</em>}</li>
 *   <li>{@link mediaflow.impl.TranscoderImpl#getBitrate <em>Bitrate</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TranscoderImpl extends TransformerImpl implements Transcoder {
	/**
	 * The default value of the '{@link #getVideoCodec() <em>Video Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVideoCodec()
	 * @generated
	 * @ordered
	 */
	protected static final String VIDEO_CODEC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVideoCodec() <em>Video Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVideoCodec()
	 * @generated
	 * @ordered
	 */
	protected String videoCodec = VIDEO_CODEC_EDEFAULT;

	/**
	 * The default value of the '{@link #getAudioCodec() <em>Audio Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAudioCodec()
	 * @generated
	 * @ordered
	 */
	protected static final String AUDIO_CODEC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAudioCodec() <em>Audio Codec</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAudioCodec()
	 * @generated
	 * @ordered
	 */
	protected String audioCodec = AUDIO_CODEC_EDEFAULT;

	/**
	 * The default value of the '{@link #getContainer() <em>Container</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainer()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTAINER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContainer() <em>Container</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainer()
	 * @generated
	 * @ordered
	 */
	protected String container = CONTAINER_EDEFAULT;

	/**
	 * The default value of the '{@link #getBitrate() <em>Bitrate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBitrate()
	 * @generated
	 * @ordered
	 */
	protected static final int BITRATE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBitrate() <em>Bitrate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBitrate()
	 * @generated
	 * @ordered
	 */
	protected int bitrate = BITRATE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TranscoderImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MediaflowPackage.Literals.TRANSCODER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getVideoCodec() {
		return videoCodec;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVideoCodec(String newVideoCodec) {
		String oldVideoCodec = videoCodec;
		videoCodec = newVideoCodec;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSCODER__VIDEO_CODEC, oldVideoCodec, videoCodec));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAudioCodec() {
		return audioCodec;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAudioCodec(String newAudioCodec) {
		String oldAudioCodec = audioCodec;
		audioCodec = newAudioCodec;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSCODER__AUDIO_CODEC, oldAudioCodec, audioCodec));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContainer() {
		return container;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContainer(String newContainer) {
		String oldContainer = container;
		container = newContainer;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSCODER__CONTAINER, oldContainer, container));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBitrate(int newBitrate) {
		int oldBitrate = bitrate;
		bitrate = newBitrate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSCODER__BITRATE, oldBitrate, bitrate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MediaflowPackage.TRANSCODER__VIDEO_CODEC:
				return getVideoCodec();
			case MediaflowPackage.TRANSCODER__AUDIO_CODEC:
				return getAudioCodec();
			case MediaflowPackage.TRANSCODER__CONTAINER:
				return getContainer();
			case MediaflowPackage.TRANSCODER__BITRATE:
				return getBitrate();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MediaflowPackage.TRANSCODER__VIDEO_CODEC:
				setVideoCodec((String)newValue);
				return;
			case MediaflowPackage.TRANSCODER__AUDIO_CODEC:
				setAudioCodec((String)newValue);
				return;
			case MediaflowPackage.TRANSCODER__CONTAINER:
				setContainer((String)newValue);
				return;
			case MediaflowPackage.TRANSCODER__BITRATE:
				setBitrate((Integer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MediaflowPackage.TRANSCODER__VIDEO_CODEC:
				setVideoCodec(VIDEO_CODEC_EDEFAULT);
				return;
			case MediaflowPackage.TRANSCODER__AUDIO_CODEC:
				setAudioCodec(AUDIO_CODEC_EDEFAULT);
				return;
			case MediaflowPackage.TRANSCODER__CONTAINER:
				setContainer(CONTAINER_EDEFAULT);
				return;
			case MediaflowPackage.TRANSCODER__BITRATE:
				setBitrate(BITRATE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MediaflowPackage.TRANSCODER__VIDEO_CODEC:
				return VIDEO_CODEC_EDEFAULT == null ? videoCodec != null : !VIDEO_CODEC_EDEFAULT.equals(videoCodec);
			case MediaflowPackage.TRANSCODER__AUDIO_CODEC:
				return AUDIO_CODEC_EDEFAULT == null ? audioCodec != null : !AUDIO_CODEC_EDEFAULT.equals(audioCodec);
			case MediaflowPackage.TRANSCODER__CONTAINER:
				return CONTAINER_EDEFAULT == null ? container != null : !CONTAINER_EDEFAULT.equals(container);
			case MediaflowPackage.TRANSCODER__BITRATE:
				return bitrate != BITRATE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (videoCodec: ");
		result.append(videoCodec);
		result.append(", audioCodec: ");
		result.append(audioCodec);
		result.append(", container: ");
		result.append(container);
		result.append(", bitrate: ");
		result.append(bitrate);
		result.append(')');
		return result.toString();
	}

} //TranscoderImpl
