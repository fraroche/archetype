package fr.si2m.csnt.socle;

import fr.si2m.socle.context.filter.AbsInitContextFilter;

/**
 * Servlet Filter implementation class AbsInitContextFilter
 */
public class InitContextFilter extends AbsInitContextFilter {

	@Override
	protected Class getRealizingClass() {
		return this.getClass();
	}

}
