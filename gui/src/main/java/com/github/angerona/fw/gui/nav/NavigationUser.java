package com.github.angerona.fw.gui.nav;
import com.github.angerona.fw.internal.Entity;
import com.github.angerona.fw.report.ReportEntry;

/**
 * The NavigationUser interface is responsible for the communication
 * between a NavigationPanel and its parent component.
 * @author Tim Janus
 *
 */
public interface NavigationUser {
	/** @return a reference to the report-attachment which is observed by the parent component */
	Entity getAttachment();
	
	/** @return the entry which is watch currently */
	ReportEntry getCurrentEntry();
	
	/** sets the current watched report entry to the given reference */
	void setCurrentEntry(ReportEntry entry);
}
