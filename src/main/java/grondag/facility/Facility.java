/*******************************************************************************
 * Copyright 2019, 2020 grondag
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
package grondag.facility;

/**
 * TODO: Things and stuff
 * -- next release
 * fix creative bin persistence
 * placeholder recipes
 * implement transactions
 *
 * -- near releases
 * inserter
 * make a pump
 * make a tank
 * controller/access block
 * crafting block
 * signal station
 * config screen
 * api java docs
 * api wiki
 * implement multiblock limits
 *
 * -- production release
 * wood tar process
 * better crafting
 *
 * -- maybe ever
 * throttle tick times
 * explicit device disconnect handling to allow retaining storage instances without wrapping
 * article metadata loader
 *
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import grondag.facility.init.BinBlocks;
import grondag.facility.init.Containers;
import grondag.facility.init.CrateBlocks;
import grondag.facility.init.PipeBlocks;
import grondag.facility.init.Textures;
import grondag.facility.packet.BinActionC2S;
import grondag.fermion.registrar.Registrar;

public class Facility implements ModInitializer {
	public static final Logger LOG = LogManager.getLogger("Facility");
	public static final String MODID = "facility";
	public static Registrar REG  = new Registrar(MODID, "facility");

	@Override
	public void onInitialize() {
		Containers.values();
		Textures.values();
		CrateBlocks.values();
		BinBlocks.values();
		PipeBlocks.values();

		ServerSidePacketRegistry.INSTANCE.register(BinActionC2S.ID, BinActionC2S::accept);
	}
}
