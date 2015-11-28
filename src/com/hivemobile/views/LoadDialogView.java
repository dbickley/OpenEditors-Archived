package com.hivemobile.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.hivemobile.helpers.LogHelper;
import com.hivemobile.helpers.SettingsHelper;

public class LoadDialogView extends TitleAreaDialog {
	private static final int DELETE = 3;
	private static LogHelper log = new LogHelper(LoadDialogView.class);
	private SettingsHelper settingsHelper = new SettingsHelper();
	private String fileName;
	private SetsTreeView treeViewer;

	public LoadDialogView() {
		super(new Shell());

	}

	@Override
	public void create() {
		super.create();
		setTitle("Open");
		setMessage("Please pick an item from the list.");
		setDialogHelpAvailable(false);
		setHelpAvailable(false);
		setTitleImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(2, true);
		parent.setLayout(layout);
		createTreeViewer(parent);
		return parent;
	}

	private void createTreeViewer(Composite parent) {
		treeViewer = new SetsTreeView(parent, settingsHelper.getSets());
		treeViewer.expandAll();
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		createOkButton(parent, OK, "Open", true);

		Button deleteButton = createButton(parent, DELETE, "Delete", false);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					settingsHelper.deleteWindowSet(treeViewer.getSelectedFileName());
					close();
				}
			}
		});

		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);

		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(Composite parent, int id,
			String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		boolean valid = true;
		return valid;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		fileName = treeViewer.getSelectedFileName();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFileName() {
		return fileName;
	}

}