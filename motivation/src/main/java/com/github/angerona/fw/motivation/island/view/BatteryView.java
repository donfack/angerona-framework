package com.github.angerona.fw.motivation.island.view;

import java.util.LinkedList;
import java.util.List;

import com.github.angerona.fw.gui.view.ListViewColored;
import com.github.angerona.fw.internal.Entity;
import com.github.angerona.fw.motivation.island.comp.Battery;

/**
 * 
 * @author Manuel Barbi
 * 
 */
public class BatteryView extends ListViewColored {

	private static final long serialVersionUID = -6063338442612426053L;

	@Override
	protected List<String> getStringRepresentation(Entity entity) {
		if (entity instanceof Battery) {
			List<String> reval = new LinkedList<String>();
			reval.add("Charge: " + ((Battery) entity).getCharge());
			return reval;
		}
		return null;
	}

	@Override
	public Class<? extends Entity> getObservedType() {
		return Battery.class;
	}

}
