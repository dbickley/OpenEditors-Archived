package com.hivemobile.views;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class SetsTreeView extends TreeViewer {

	private Vector<Node> filesInput = new Vector<Node>();
	private HashMap<String, String> filesInputHash;

	public SetsTreeView(Composite parent, HashMap<String, String> filesInputHash) {
		super(parent);
		this.filesInputHash = filesInputHash;
		setContentProvider(new FilesContentProvider());
		setLabelProvider(new FilesLabelProvider());

		setInput(getNodes());
	}

	public String getSelectedFileName() {
		TreeSelection treeSelection = (TreeSelection) getSelection();
		if (treeSelection != null) {
			return filesInputHash.get(((Node) treeSelection.getFirstElement()).getName());
		} else {
			return null;
		}

	}

	private Vector<Node> getNodes() {
		Node parent = new Node("Local", null);
		for (String key : filesInputHash.keySet()) {
			Node node = new Node(key, parent);
			filesInput.add(node);
		}
		return filesInput;
	}

	class Node {
		private String name;

		private Vector<Node> subCategories;

		private Node parent;

		public Node(String name, Node parent) {
			this.name = name;
			this.parent = parent;
			if (parent != null)
				parent.addSubCategory(this);
		}

		public Vector<Node> getSubCategories() {
			return subCategories;
		}

		private void addSubCategory(Node subcategory) {
			if (subCategories == null)
				subCategories = new Vector<Node>();
			if (!subCategories.contains(subcategory))
				subCategories.add(subcategory);
		}

		public String getName() {
			return name;
		}

		public Node getParent() {
			return parent;
		}
	}

	class FilesLabelProvider implements ILabelProvider {
		@Override
		public String getText(Object element) {
			return ((Node) element).getName();
		}

		@Override
		public Image getImage(Object arg0) {
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener arg0) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
		}
	}

	class FilesContentProvider implements ITreeContentProvider {
		@Override
		public Object[] getChildren(Object parentElement) {
			Vector<Node> subcats = ((Node) parentElement).getSubCategories();
			return subcats == null ? new Object[0] : subcats.toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((Node) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((Node) element).getSubCategories() != null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement != null && inputElement instanceof Vector) {
				return ((Vector<Node>) inputElement).toArray();
			}
			return new Object[0];
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
