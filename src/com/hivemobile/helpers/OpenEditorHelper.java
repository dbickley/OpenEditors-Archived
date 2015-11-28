package com.hivemobile.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.hivemobile.models.Editor;

public class OpenEditorHelper {
	private static LogHelper log = new LogHelper(OpenEditorHelper.class);

	private static final String SETS_FILE_NAME = "settings.hivemobile";
	private static final String SETS_ROOT = "settings";
	private DialogSettings settings;

	private Random random = new Random();
	private Map<String, Editor> pinnedEditors = new HashMap<String, Editor>();
	private Map<String, Long> accessMap = new HashMap<String, Long>();

	public void openPage(Editor editor, IWorkbenchPartSite site) {

		try {
			site.getWorkbenchWindow().getActivePage()
					.openEditor(editor.getReference().getEditorInput(), editor.getReference().getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	public void openPage(String editorPath, IWorkbenchPartSite site) {
		try {
			IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
			IPath location = new Path(editorPath);
			IFile file = ws.getFile(location);

			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(editorPath);
			site.getWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file),
					desc.getId());
		} catch (Exception e) {
			log.warn(e);
		}

	}

	public void closePage(TableItem[] selections, IWorkbenchPartSite site) {
		//Create a separate map due to selections collection being odd
		ArrayList<Editor> editorsToClose = new ArrayList<Editor>();

		for (TableItem selection : selections) {
			try {
				Editor editor = (Editor) selection.getData();
				if (editor != null) {
					editorsToClose.add(editor);
				}
			} catch (Exception e) {
				log.warn(e);
			}

		}

		for (Editor editor : editorsToClose) {
			try {
				closePage(editor, site);
			} catch (Exception e) {
				log.warn(e);
			}
		}
	}

	public void closePage(Editor editor, IWorkbenchPartSite site) {
		IEditorPart iEditorPart = editor.getReference().getEditor(true);
		if (iEditorPart != null) {
			site.getWorkbenchWindow().getActivePage()
					.closeEditor(iEditorPart, true);
		}

	}

	public Editor[] getOpenWindows() {
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IEditorReference[] ref = activePage.getEditorReferences();

			INavigationHistory navigationHistories = activePage.getNavigationHistory();

			Map<String, Editor> javaEditors = new HashMap<String, Editor>();

			javaEditors.putAll(pinnedEditors);
			for (int i = 0; i < ref.length; i++) {
				IEditorReference reference = ref[i];
				Integer naturalPosition = i;
				//Integer accessPosition = getPosition(reference.getName(), navigationHistories.getLocations());
				Integer modifiedPosition = getModifiedPosition(ref.length);
				Editor editor = new Editor(reference, naturalPosition, modifiedPosition);
				if (pinnedEditors.containsKey(editor.getFilePath())) {
					editor.isPinned(true);
				}
				Long accessDate = accessMap.get(editor.getReference().getTitleToolTip());
				if (accessDate == null) {
					accessDate = getAccessDate();
					putAccess(editor.getReference().getTitleToolTip(), accessDate);
				}

				editor.setAccessDate(accessDate);
				javaEditors.put(editor.getFilePath(), editor);
			}
			return javaEditors.values().toArray(new Editor[javaEditors.size()]);
		} catch (Exception e) {
			log.warn(e);
		}
		return null;
	}

	private Integer getModifiedPosition(int size) {
		return random.nextInt(size);
	}

	private Integer getPosition(String name, INavigationLocation[] iNavigationLocations) {
		try {
			if (iNavigationLocations != null) {
				for (int i = 0; i < iNavigationLocations.length; i++) {
					if (iNavigationLocations[i].getText().contentEquals(name)) {
						return i;
					}
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
		return null;
	}

	public void pin(TableItem[] selections) {
		if (selections != null) {
			for (TableItem selection : selections) {
				try {
					Editor editor = (Editor) selection.getData();
					if (editor != null) {
						editor.isPinned(true);
						pinnedEditors.put(editor.getFilePath(), editor);
					}

				} catch (Exception e) {
					log.warn(e);
				}
			}
		}

	}

	public void unpin(TableItem[] selections) {
		if (selections != null) {
			for (TableItem selection : selections) {
				try {
					Editor editor = (Editor) selection.getData();
					if (editor != null) {
						pinnedEditors.remove(editor.getFilePath());
					}
				} catch (Exception e) {
					log.warn(e);
				}
			}

		}
	}

	public void putAccess(String partName) {
		accessMap.put(partName, getAccessDate());
	}

	public void putAccess(String partName, Long accessDate) {
		accessMap.put(partName, accessDate);
	}

	private Long getAccessDate() {
		return Calendar.getInstance().getTimeInMillis();
	}
}
