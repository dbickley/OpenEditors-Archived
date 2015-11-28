package com.hivemobile.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.hivemobile.models.Editor;

public class EditorSorter extends ViewerSorter {
	public enum SortType {
		ACCESS, MODIFIED, NATURAL, NAME, PATH
	}

	private SortType sortBy = SortType.PATH;

	public EditorSorter(SortType sortBy) {
		this.sortBy = sortBy;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Editor editor1 = (Editor) e1;
		Editor editor2 = (Editor) e2;

		int compare = Boolean.compare(editor2.isPinned(), editor1.isPinned());
		if (compare == 0) {
			switch (sortBy) {
				case ACCESS:
					compare = compare(editor2.getAccessDate(), editor1.getAccessDate());
					break;
				case MODIFIED:
					compare = compare(editor2.getModifiedPosition(), editor1.getModifiedPosition());
					break;
				case NATURAL:
					compare = compare(editor1.getNaturalPosition(), editor2.getNaturalPosition());
					break;
				case NAME:
					compare = compare(editor1.getName().toLowerCase(), editor2.getName().toLowerCase());
					break;
				case PATH:
					compare = compare(editor1.getFilePath().toLowerCase(), editor2.getFilePath().toLowerCase());
					break;
			}
		}
		return compare;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(final Comparable one, final Comparable two) {
		if (one == null ^ two == null) {
			return (one == null) ? -1 : 1;
		}

		if (one == null && two == null) {
			return 0;
		}

		return one.compareTo(two);
	}

	public SortType getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortType sortBy) {
		this.sortBy = sortBy;
	}
}
