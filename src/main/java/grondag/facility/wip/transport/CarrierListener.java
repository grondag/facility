package grondag.facility.wip.transport;

public interface CarrierListener {
	/**
	 * Called when carrier being listened to becomes unavailable.
	 * Carriers that are disconnecting due to error or unexpected conditions
	 * should, at a minimum, call with {@code didNotify = false} and
	 * {@code isValid = false} so aggregate listeners know to reconstruct their views.
	 *
	 * @param storage Carrier that was being monitored.
	 * @param didNotify True if carrier called {@code onAttach} before disconnecting. (Preferred)
	 * @param isValid True if carrier state is currently valid and could be used to update listener.
	 */
	void disconnect(Carrier carrier, boolean didNotify, boolean isValid);

	void onAttach(Carrier carrier, CarrierSession node);

	void onDetach(Carrier carrier, CarrierSession node);
}
