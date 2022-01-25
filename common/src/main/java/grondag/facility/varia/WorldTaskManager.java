/*
 * This file is part of Facility and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.facility.varia;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BooleanSupplier;

// PERF: throttle performance based on elapsed time instead of number of operations
/**
 * Maintains a queue of tasks that require world access and executes them in
 * FIFO order.
 *
 * <p>Ensures that all world access is synchronized, in-order, and does not exceed
 * the configured threshold for points per tick.
 *
 * <p>Tasks are not serialized or persisted - queue must be rebuilt by task
 * providers on world reload.
 */
public class WorldTaskManager {
	private static int tickCounter = 0;

	/** Monotonic increasing integer - incremented each tick.  May be more convenient than world time. */
	public static int tickCounter() {
		return tickCounter;
	}

	/**
	 * Metered tasks - run based on budget consumption until drained.
	 */
	private static ConcurrentLinkedQueue<BooleanSupplier> tasks = new ConcurrentLinkedQueue<>();

	/**
	 * Immediate tasks - queue is fully drained each tick.
	 */
	private static ConcurrentLinkedQueue<Runnable> immediateTasks = new ConcurrentLinkedQueue<>();

	public static void clear() {
		tasks.clear();
	}

	public static void doServerTick() {
		++tickCounter;

		while (!immediateTasks.isEmpty()) {
			final Runnable r = immediateTasks.poll();
			r.run();
		}

		if (tasks.isEmpty()) {
			return;
		}

		//TODO: remove or make configurable
		int operations = 64; //XmConfig.EXECUTION.maxQueuedWorldOperationsPerTick;

		BooleanSupplier task = tasks.peek();

		while (operations > 0 && task != null) {
			// check for canceled tasks and move to next if checked
			if (task.getAsBoolean()) {
				operations--;
			} else {
				tasks.poll();
				task = tasks.peek();
			}
		}
	}

	public static void enqueue(BooleanSupplier task) {
		tasks.offer(task);
	}

	/**
	 * Use for short-running operations that should run on next tick.
	 */
	public static void enqueueImmediate(Runnable task) {
		immediateTasks.offer(task);
	}
}
