// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StackAnalyzer.java

package com.github.janssk1.threadanalyzer;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;


public class ThreadAnalyzer {
    private static class FilesComboBoxModel extends DefaultComboBoxModel<File> {

        protected void fireIntervalAdded(Object source, int index0, int index1) {
            super.fireIntervalAdded(source, index0, index1);
            StringBuilder files = new StringBuilder();
            for (int i = 0; i < getSize(); i++) {
                if (files.length() > 0)
                    files.append(",");
                files.append((getElementAt(i)).getPath());
            }
            ThreadAnalyzer.preferences.put("files", files.toString());
        }

        protected void fireContentsChanged(Object source, int index0, int index1) {
            super.fireContentsChanged(source, index0, index1);
            ThreadAnalyzer.preferences.put("selected", ((File) getSelectedItem()).getPath());
        }

        public FilesComboBoxModel(File files[]) {
            super(files);
        }
    }

    private static class MyTreeModel
            implements TreeModel {

        public Object getRoot() {
            return root;
        }

        public Object getChild(Object parent, int index) {
            return ((StackAggregateNode) parent).childList.get(index);
        }

        public int getChildCount(Object parent) {
            return ((StackAggregateNode) parent).childList.size();
        }

        public boolean isLeaf(Object node) {
            return ((StackAggregateNode) node).childList.size() == 0;
        }

        public void valueForPathChanged(TreePath treepath, Object obj) {
        }

        public int getIndexOfChild(Object parent, Object child) {
            return 0;
        }

        public void addTreeModelListener(TreeModelListener treemodellistener) {
        }

        public void removeTreeModelListener(TreeModelListener treemodellistener) {
        }

        private final StackAggregateNode root;

        private MyTreeModel(StackAggregateNode root) {
            this.root = root;
        }

    }


    public ThreadAnalyzer() {
    }

    private static FilesComboBoxModel load() {
        FilesComboBoxModel filesComboBoxModel;
        java.util.List<File> files = new LinkedList<>();
        String separatedFiles = preferences.get("files", null);
        if (separatedFiles != null) {
            String split[] = separatedFiles.split(",");
            for (String s : split) {
                files.add(new File(s));
            }
        }
        filesComboBoxModel = new FilesComboBoxModel(files.toArray(new File[files.size()]));
        String selected = preferences.get("selected", null);
        if (selected != null)
            filesComboBoxModel.setSelectedItem(new File(selected));
        return filesComboBoxModel;
    }

    public static void main(String args[])
            throws IOException {
        JButton chooseFile = new JButton("...");
        final FilesComboBoxModel comboBoxModel = load();
        JComboBox<File> files = new JComboBox<>(comboBoxModel);
        final JFileChooser chooser = new JFileChooser();
        final JFrame frame = new JFrame("Stack Analyzer");
        chooseFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                chooser.setSelectedFile((File) comboBoxModel.getSelectedItem());
                int state = chooser.showOpenDialog(frame.getContentPane());
                File file = chooser.getSelectedFile();
                if (file != null && state == JFileChooser.APPROVE_OPTION) {
                    if (comboBoxModel.getIndexOf(file) == -1) {
                        comboBoxModel.addElement(file);
                    }
                    comboBoxModel.setSelectedItem(file);
                }
            }

        });
        final JTree tree = new JTree();
        final JTextArea comp = new JTextArea();
        comp.setEditable(false);
        files.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                File item = (File) e.getItem();
                if (e.getStateChange() == ItemEvent.SELECTED)
                    ThreadAnalyzer.showFileContents(item, tree, comp);
            }


        });
        showFileContents((File) comboBoxModel.getSelectedItem(), tree, comp);
        JSplitPane splitPane = new JSplitPane();
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
                StringBuilder res = new StringBuilder();
                int maxAmountOfStacksRendered = 500;
                if (newLeadSelectionPath != null) {
                    StackAggregateNode lastPathComponent = (StackAggregateNode) newLeadSelectionPath.getLastPathComponent();
                    java.util.List<String> allStacks = lastPathComponent.getAllStacks(maxAmountOfStacksRendered);
                    for (String allStack : allStacks) {
                        if (res.length() > 0) {
                            res.append("\n\n");
                        }
                        res.append(allStack);
                    }
                    final int stackCount = lastPathComponent.getStackCount();
                    if (maxAmountOfStacksRendered < stackCount) {
                        res.append("\n\n");
                        res.append("SHOWING ONLY ").append(maxAmountOfStacksRendered).append(" OF ").append(stackCount).append(" STACKS");
                    }

                }
                comp.setText(res.toString());
            }


        });
        splitPane.add(new JScrollPane(tree), "left");
        splitPane.add(new JScrollPane(comp), "right");
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLUE);
        panel.setLayout(new BorderLayout());
        panel.add(files, "Center");
        panel.add(chooseFile, "East");
        frame.getContentPane().add(panel, "North");
        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void showFileContents(final File item, final JTree tree, final JTextArea comp) {
        tree.setModel(new MyTreeModel(new StackAggregateNode("")));
        comp.setText("");
        if (item != null) {
            SwingWorker<StackAggregateNode, Object> w = new SwingWorker<StackAggregateNode, Object>() {

                @Override
                protected StackAggregateNode doInBackground() throws Exception {
                    //return new VisualVmStackParser().readStackTrace(item);
                    //return new StackParser().readStackTrace(item);
                    //comp.getRootPane().setGlassPane();

                    return readStackFile(item);
                }


                @Override
                protected void done() {
                    try {
                        StackAggregateNode result = get();
                        tree.setModel(new MyTreeModel(result));
                    } catch (InterruptedException e) {
                        handleUnexpectedException(e);
                    } catch (ExecutionException e) {
                        handleUnexpectedException(e.getCause());
                    }
                }

                private void handleUnexpectedException(Throwable e) {
                    JOptionPane.showMessageDialog(comp, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();//todo
                }
            };
            w.execute();
        }
    }

    private static StackAggregateNode readStackFile(File item) throws IOException {
        final FileResourceDescriptor resource = new FileResourceDescriptor(item);
        return StackAggregateNode.create(resource);
    }


    private static final Preferences preferences = Preferences.userNodeForPackage(ThreadAnalyzer.class);


}
