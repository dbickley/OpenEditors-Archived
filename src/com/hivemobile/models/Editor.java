package com.hivemobile.models;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.hivemobile.helpers.LogHelper;

public class Editor {
	private static LogHelper log = new LogHelper(Editor.class);

	private IEditorReference reference;
	private Integer naturalPosition;
	private Long accessDate;
	private Integer modifiedPosition;

	private boolean isPinned;

	private String filePath;

	private String name;

	private String id;

	public Editor(IEditorReference reference, Integer naturalPosition, Integer modifiedPosition) {
		this.reference = reference;
		this.naturalPosition = naturalPosition;
		this.modifiedPosition = modifiedPosition;
		try {
			filePath = reference.getEditorInput().getToolTipText();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		name = reference.getName();

	}

	public Editor(String name, boolean isPinned) {
		this.name = name;
		this.isPinned = isPinned;
	}

	public IEditorReference getReference() {
		return reference;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	private String getDisplayName() {
		StringBuffer name = new StringBuffer();

		name.append(getTitle());

		return name.toString();

	}

	public String getFilePath() {
		return filePath;
	}

	public String getContentDescription() {
		return reference.getContentDescription();
	}

	public String getName() {
		return reference.getName();
	}

	public IWorkbenchPage getPage() {
		return reference.getPage();
	}

	public String getTitle() {
		return reference.getTitle();
	}

	public Image getTitleImage() {
		return reference.getTitleImage();
	}

	public boolean isDirty() {
		return reference.isDirty();
	}

	public Integer getNaturalPosition() {
		return naturalPosition;
	}

	public void setNaturalPosition(Integer naturalPosition) {
		this.naturalPosition = naturalPosition;
	}

	public Integer getModifiedPosition() {
		return modifiedPosition;
	}

	public void setModifiedPosition(Integer modifiedPosition) {
		this.modifiedPosition = modifiedPosition;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void isPinned(boolean isPinned) {
		this.isPinned = isPinned;

	}

	public Long getAccessDate() {
		return accessDate;
	}

	public void setAccessDate(Long accessDate) {
		this.accessDate = accessDate;
	}

}
