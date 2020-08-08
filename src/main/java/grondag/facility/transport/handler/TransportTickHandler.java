package grondag.facility.transport.handler;

@FunctionalInterface
public interface TransportTickHandler {
	/** returns true if remains valid */
	boolean tick(TransportContext context);


	TransportTickHandler NOOP = c -> true;
}
