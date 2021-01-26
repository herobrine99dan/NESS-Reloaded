package com.github.ness.violation;

import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionTrigger;

class CheckSpecificInfractionTrigger implements InfractionTrigger {

	private final InfractionTrigger delegate;
	private final String checkName;

	CheckSpecificInfractionTrigger(InfractionTrigger delegate, String checkName) {
		this.delegate = delegate;
		this.checkName = checkName;
	}

	@Override
	public SynchronisationContext context() {
		return delegate.context();
	}

	@Override
	public void trigger(Infraction infraction) {
		if (infraction.getCheck().getCheckName().equals(checkName)) {
			delegate.trigger(infraction);
		}
	}
}
