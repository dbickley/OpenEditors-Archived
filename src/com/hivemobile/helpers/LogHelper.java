package com.hivemobile.helpers;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.hivemobile.Activator;

public class LogHelper {
	private ILog log;
	private Class clazz;

	public LogHelper(Class clazz) {
		log = Activator.getDefault().getLog();
		this.clazz = clazz;
	}

	public void info(Exception e) {
		info(e, e.getMessage());
	}

	public void info(String message) {
		info(null, message);
	}

	public void info(Exception e, String message) {
		Status status = new Status(IStatus.INFO, Activator.PLUGIN_ID, clazz.getName() + ":" + message, e);
		log.log(status);
	}

	public void warn(Exception e) {
		warn(e, e.getMessage());
	}

	public void warn(String message) {
		warn(null, message);
	}

	public void warn(Exception e, String message) {
		Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, clazz.getName() + ":" + message, e);
		log.log(status);
	}

	public void error(Exception e) {
		error(e, e.getMessage());
	}

	public void error(String message) {
		error(null, message);
	}

	public void error(Exception e, String message) {
		Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, clazz.getName() + ":" + message, e);
		log.log(status);
	}

}
