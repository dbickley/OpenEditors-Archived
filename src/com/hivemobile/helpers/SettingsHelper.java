package com.hivemobile.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;

import com.hivemobile.Activator;
import com.hivemobile.models.Editor;
import com.hivemobile.views.EditorSorter;
import com.hivemobile.views.EditorSorter.SortType;

public class SettingsHelper {
	private static final String IS_PINNED = "isPinned";
	private static LogHelper log = new LogHelper(SettingsHelper.class);
	private static final String SETS_FILE_NAME = "settings.hivemobile";
	private static final String SETS_ROOT = "settings";
	private static final String SORT_BY = "sortBy";
	private DialogSettings settings;

	public ArrayList<String> openWindowsSet(String fileName) {
		ArrayList<String> editors = new ArrayList<String>();
		try {
			DialogSettings settings = getSettings();
			for (IDialogSettings setting : settings.getSection(fileName).getSections()) {

				if (setting.getBoolean(IS_PINNED)) {
					//Editor editor = new Editor(setting.getName());
				}
				editors.add(setting.getName());
			}
		} catch (Exception e) {
			log.warn(e);
		}
		return editors;
	}

	public void saveWindowSet(String fileName, boolean includeInProject, Editor[] openWindows) {

		IDialogSettings settings = getSettings().addNewSection(fileName);

		for (Editor editor : openWindows) {

			IDialogSettings section = settings.addNewSection(editor.getFilePath());
			section.put(IS_PINNED, editor.isPinned());
		}

		saveSettings();
		System.out.println("saved");

	}

	public void deleteWindowSet(String fileName) {
		try {
			getSettings().removeSection(fileName);
			saveSettings();
			System.out.println("saved");
		} catch (Exception e) {
			log.warn(e);
		}
	}

	public HashMap<String, String> getSets() {
		IDialogSettings[] setsChildren = getSettings().getSections();
		HashMap<String, String> sets = new HashMap<String, String>();
		for (IDialogSettings s : setsChildren) {

			sets.put(s.getName(), s.getName());
		}
		return sets;
	}

	private DialogSettings getSettings() {
		//if (settings == null) {
		IPath path = Activator.getDefault().getStateLocation();
		String settingsFileName = path.append(SETS_FILE_NAME).toOSString();
		settings = new DialogSettings(SETS_ROOT);
		try {
			settings.load(settingsFileName);
		} catch (IOException e) {
			saveSettings();
		}
		//}
		return settings;
	}

	private void saveSettings() {
		try {
			IPath path = Activator.getDefault().getStateLocation();
			String settingsFileName = path.append(SETS_FILE_NAME).toOSString();
			settings.save(settingsFileName);
		} catch (IOException e) {
			log.warn(e);
		}

	}

	public SortType loadSortBy() {
		try {
			String sortByString = getSettings().get(SORT_BY);
			if (sortByString != null) {
				return EditorSorter.SortType.valueOf(sortByString);
			}
		} catch (Exception e) {
			log.warn(e);
		}
		return EditorSorter.SortType.ACCESS;
	}

	public void saveSortBy(SortType access) {
		getSettings().put(SORT_BY, access.toString());
		saveSettings();

	}
}
