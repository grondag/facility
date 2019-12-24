/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.contained;

/**
 * TODO: fix semantic key config in mod keys
 * TODO: rename again
 * TODO: wood tar process
 * TODO: implement rollback for flexible storage
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import grondag.contained.packet.BinActionC2S;
import grondag.fermion.registrar.Registrar;

public class Contained implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("Contained");
	public static final String MODID = "contained";
	public static Registrar REG  = new Registrar(MODID, "contained");

	@Override
	public void onInitialize() {
		Registrations.values();

		ServerSidePacketRegistry.INSTANCE.register(BinActionC2S.ID, BinActionC2S::accept);
	}
}
