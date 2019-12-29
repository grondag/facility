package grondag.facility.wip.transport;

/**
 * Negotiates the connection of two devices via eligible ports.
 * Should be called *after* determined compound device is not the outcome.
 *
 * Three scenarios are possible:
 *
 * 1) Non-Carrier Device to Non-Carrier Device
 * A virtual p2p carrier will be created for port types that support it.
 *
 * 2) Carrier to Non-Carrier
 * Will try to assign non-carrier device one or more ports on the carrier(s)
 * available depending on port configuration.
 *
 * 3) Carrier to Carrier
 * A virtual p2p carrier will be created for port types that support it.
 *
 */
public interface DeviceConnector {

}
