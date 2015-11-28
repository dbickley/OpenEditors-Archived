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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class SaveDialogView extends TitleAreaDialog {

	private Text fileNameText;
	private String fileName;
	private boolean saveInProject = false;

	public SaveDialogView(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Save");
		setMessage("Please choose a name to save your file.");
		setDialogHelpAvailable(false);
		setHelpAvailable(false);
		this.setTitleImage(PlatformUI.getWorkbench().getSharedImages().
				getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(2, false);

		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Name");

		fileNameText = new Text(parent, SWT.BORDER);
		fileNameText.setLayoutData(gridData);

		//saveInProjectCheck = new Button(getShell(), SWT.CHECK);
		//saveInProjectCheck.setText("Save in project");

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "Add", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton =
				createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
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
		Button btnSave = new Button(parent, SWT.PUSH);
		btnSave.setText("Save");
		btnSave.setFont(JFaceResources.getDialogFont());
		btnSave.setData(new Integer(id));
		btnSave.addSelectionListener(new SelectionAdapter() {
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
				shell.setDefaultButton(btnSave);
			}
		}
		setButtonLayoutData(btnSave);
		return btnSave;
	}

	private boolean isValidInput() {
		boolean valid = true;
		if (fileNameText.getText().length() == 0) {
			setErrorMessage("Name can not be empty");
			valid = false;
		}
		return valid;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		fileName = fileNameText.getText();
		//saveInProject = saveInProjectCheck.getSelection() ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isSaveInProject() {
		return saveInProject;
	}

}