package com.hivemobile.views;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.hivemobile.helpers.LogHelper;
import com.hivemobile.helpers.OpenEditorHelper;
import com.hivemobile.helpers.SettingsHelper;
import com.hivemobile.models.Editor;
import com.hivemobile.views.EditorSorter.SortType;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class OpenEditorsView extends ViewPart implements IPartListener2 {
	private static LogHelper log = new LogHelper(OpenEditorsView.class);

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "bickley.views.SampleView";

	private TableViewer viewer;

	private Action saveSetAction;
	private Action loadSetAction;
	private Action sortByAccessAction;
	//private Action sortByModifiedAction;
	private Action sortByNameAction;
	private Action sortByPathAction;

	private OpenEditorHelper openEditorHelper = new OpenEditorHelper();
	private SettingsHelper settingsHelper = new SettingsHelper();

	private Color highlightColor = new Color(Display.getCurrent(), new RGB(219, 219, 219));
	private Color pinnedColor = new Color(Display.getCurrent(), new RGB(60, 15, 175));
	private Color dirtyColor = new Color(Display.getCurrent(), new RGB(204, 0, 0));

	protected SortType sortBy;
	private EditorSorter sorter;

	private String activePart;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content
	 * (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

			System.out.println("inputChanged");
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object parent) {
			return openEditorHelper.getOpenWindows();
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			if (obj instanceof Editor) {
				return ((Editor) obj).getReference().getTitleImage();
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		buildTableViewer(parent);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "bickley.viewer");
		buildActions();
		//hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		final IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this);

	}

	private void buildTableViewer(Composite parent) {
		sortBy = settingsHelper.loadSortBy();
		sorter = new EditorSorter(sortBy);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(sorter);
		viewer.setInput(getViewSite());

		final Table table = viewer.getTable();
		final Menu contextMenu = new Menu(table);
		table.setMenu(contextMenu);

		final MenuItem pinMenuItem = new MenuItem(contextMenu, SWT.None);
		pinMenuItem.setText("pin");
		pinMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				openEditorHelper.pin(table.getSelection());
				refresh();
			}
		});

		final MenuItem unpinMenuItem = new MenuItem(contextMenu, SWT.None);
		unpinMenuItem.setText("un-pin");
		unpinMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				openEditorHelper.unpin(table.getSelection());
				refresh();
			}
		});

		final MenuItem closeMenuItem = new MenuItem(contextMenu, SWT.None);
		closeMenuItem.setText("close");
		closeMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				openEditorHelper.closePage(table.getSelection(), getSite());
				refresh();
			}
		});

		table.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (table.getSelectionCount() <= 0) {
					event.doit = false;
				}
			}

		});

	}

	private void refresh() {
		try {
			if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
				viewer.refresh();
				formatRows();
			}
		} catch (Exception e) {
			log.warn(e);
		}
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(saveSetAction);
		manager.add(loadSetAction);
		manager.add(new Separator());
		manager.add(sortByNameAction);
		manager.add(sortByPathAction);
		manager.add(sortByAccessAction);
		//manager.add(sortByModifiedAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(saveSetAction);
		manager.add(loadSetAction);

	}

	private void buildActions() {
		saveSetAction = new Action() {
			@Override
			public void run() {
				SaveDialogView dialog = new SaveDialogView(new Shell());
				dialog.create();
				if (dialog.open() == Window.OK) {
					settingsHelper.saveWindowSet(dialog.getFileName(),
							dialog.isSaveInProject(),
							openEditorHelper.getOpenWindows());
				}

			}
		};
		saveSetAction.setText("Save Set");
		saveSetAction.setToolTipText("Save Set");
		saveSetAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		loadSetAction = new Action() {

			@Override
			public void run() {

				LoadDialogView dialog = new LoadDialogView();
				dialog.create();
				if (dialog.open() == Window.OK) {
					ArrayList<String> editors = settingsHelper.openWindowsSet(dialog.getFileName());
					for (String e : editors) {
						openEditorHelper.openPage(e, getSite());
					}
				}

			}
		};
		loadSetAction.setText("Load Set");
		loadSetAction.setToolTipText("Load Set");
		loadSetAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));

		sortByAccessAction = new Action() {

			@Override
			public void run() {
				setSortBy(EditorSorter.SortType.ACCESS);
			}
		};
		sortByAccessAction.setText("Sort by last access");
		sortByAccessAction.setToolTipText("Sort by last access");

		//		sortByModifiedAction = new Action() {
		//
		//			@Override
		//			public void run() {
		//				sorter.setSortBy(EditorSorter.SortType.MODIFIED);
		//				refresh();
		//			}
		//		};
		//		sortByModifiedAction.setText("Sort by last modified");
		//		sortByModifiedAction.setToolTipText("Sort by last modified");

		sortByNameAction = new Action() {

			@Override
			public void run() {
				setSortBy(EditorSorter.SortType.NAME);
			}
		};
		sortByNameAction.setText("Sort by name");
		sortByNameAction.setToolTipText("Sort by name");

		sortByPathAction = new Action() {

			@Override
			public void run() {
				setSortBy(EditorSorter.SortType.PATH);
			}

		};
		sortByPathAction.setText("Sort by path");
		sortByPathAction.setToolTipText("Sort by path");

	}

	private void setSortBy(EditorSorter.SortType sortBy) {
		this.sortBy = sortBy;
		sorter.setSortBy(sortBy);
		settingsHelper.saveSortBy(sortBy);
		refresh();
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = viewer.getSelection();
				Editor editor = (Editor) ((IStructuredSelection) selection).getFirstElement();

				openEditorHelper.openPage(editor, getSite());
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void partActivated(IWorkbenchPartReference workbenchPartReference) {
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			openEditorHelper.putAccess(workbenchPartReference.getTitleToolTip());
			activePart = workbenchPartReference.getTitleToolTip();
		}

		refresh();
	}

	private void formatRows() {
		try {
			TableItem[] items = viewer.getTable().getItems();
			for (TableItem item : items) {
				try {
					Editor editor = ((Editor) item.getData());

					if (editor.isDirty()) {
						item.setForeground(dirtyColor);
					} else if (editor.isPinned()) {
						item.setForeground(pinnedColor);
					} else {
						item.setForeground(viewer.getTable().getForeground());
					}
					if (editor.getReference().getTitleToolTip().equals(activePart)) {
						item.setBackground(highlightColor);
					}
				} catch (Exception e) {
					log.warn(e);
				}
			}

		} catch (Exception e) {
			log.warn(e);
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference arg0) {
		if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
			activePart = arg0.getPartName();
		}

	}

	@Override
	public void partClosed(IWorkbenchPartReference arg0) {
		refresh();
	}

	@Override
	public void partOpened(IWorkbenchPartReference arg0) {
		refresh();

	}

	@Override
	public void partDeactivated(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		//refresh();
	}

	@Override
	public void partHidden(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		//refresh();
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		//refresh();
	}

	@Override
	public void partVisible(IWorkbenchPartReference arg0) {
		// TODO Auto-generated method stub
		//refresh();
	}

	@Override
	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub
		super.saveState(memento);
	}
}