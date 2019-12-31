package grondag.facility.wip.transport.impl;

import java.util.concurrent.atomic.AtomicLong;

public class AssignedNumbersAuthority {
	private static final AtomicLong NEXT = new AtomicLong(1024);

	public static long assign() {
		return NEXT.getAndIncrement();
	}
}
