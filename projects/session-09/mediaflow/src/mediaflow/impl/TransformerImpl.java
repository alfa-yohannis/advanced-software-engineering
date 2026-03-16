/**
 */
package mediaflow.impl;

import mediaflow.Backend;
import mediaflow.MediaflowPackage;
import mediaflow.Transformer;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Transformer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link mediaflow.impl.TransformerImpl#getBackend <em>Backend</em>}</li>
 *   <li>{@link mediaflow.impl.TransformerImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link mediaflow.impl.TransformerImpl#getReplicas <em>Replicas</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class TransformerImpl extends NodeImpl implements Transformer {
	/**
	 * The default value of the '{@link #getBackend() <em>Backend</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackend()
	 * @generated
	 * @ordered
	 */
	protected static final Backend BACKEND_EDEFAULT = Backend.FFMPEG;

	/**
	 * The cached value of the '{@link #getBackend() <em>Backend</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBackend()
	 * @generated
	 * @ordered
	 */
	protected Backend backend = BACKEND_EDEFAULT;

	/**
	 * The default value of the '{@link #getCommand() <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommand()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMAND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCommand() <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommand()
	 * @generated
	 * @ordered
	 */
	protected String command = COMMAND_EDEFAULT;

	/**
	 * The default value of the '{@link #getReplicas() <em>Replicas</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReplicas()
	 * @generated
	 * @ordered
	 */
	protected static final int REPLICAS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getReplicas() <em>Replicas</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReplicas()
	 * @generated
	 * @ordered
	 */
	protected int replicas = REPLICAS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TransformerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MediaflowPackage.Literals.TRANSFORMER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Backend getBackend() {
		return backend;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBackend(Backend newBackend) {
		Backend oldBackend = backend;
		backend = newBackend == null ? BACKEND_EDEFAULT : newBackend;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSFORMER__BACKEND, oldBackend, backend));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCommand(String newCommand) {
		String oldCommand = command;
		command = newCommand;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSFORMER__COMMAND, oldCommand, command));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getReplicas() {
		return replicas;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setReplicas(int newReplicas) {
		int oldReplicas = replicas;
		replicas = newReplicas;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MediaflowPackage.TRANSFORMER__REPLICAS, oldReplicas, replicas));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MediaflowPackage.TRANSFORMER__BACKEND:
				return getBackend();
			case MediaflowPackage.TRANSFORMER__COMMAND:
				return getCommand();
			case MediaflowPackage.TRANSFORMER__REPLICAS:
				return getReplicas();
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
			case MediaflowPackage.TRANSFORMER__BACKEND:
				setBackend((Backend)newValue);
				return;
			case MediaflowPackage.TRANSFORMER__COMMAND:
				setCommand((String)newValue);
				return;
			case MediaflowPackage.TRANSFORMER__REPLICAS:
				setReplicas((Integer)newValue);
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
			case MediaflowPackage.TRANSFORMER__BACKEND:
				setBackend(BACKEND_EDEFAULT);
				return;
			case MediaflowPackage.TRANSFORMER__COMMAND:
				setCommand(COMMAND_EDEFAULT);
				return;
			case MediaflowPackage.TRANSFORMER__REPLICAS:
				setReplicas(REPLICAS_EDEFAULT);
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
			case MediaflowPackage.TRANSFORMER__BACKEND:
				return backend != BACKEND_EDEFAULT;
			case MediaflowPackage.TRANSFORMER__COMMAND:
				return COMMAND_EDEFAULT == null ? command != null : !COMMAND_EDEFAULT.equals(command);
			case MediaflowPackage.TRANSFORMER__REPLICAS:
				return replicas != REPLICAS_EDEFAULT;
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
		result.append(" (backend: ");
		result.append(backend);
		result.append(", command: ");
		result.append(command);
		result.append(", replicas: ");
		result.append(replicas);
		result.append(')');
		return result.toString();
	}

} //TransformerImpl
