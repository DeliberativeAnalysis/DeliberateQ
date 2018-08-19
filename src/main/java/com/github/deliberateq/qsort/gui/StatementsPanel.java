package com.github.deliberateq.qsort.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;

public class StatementsPanel extends JPanel {

    private static final long serialVersionUID = -5357822385951586019L;
    private Object[] statementCheckBoxes;
    private boolean updatingMultipleCheckBoxes;
    private JCheckBoxList statementList;

    public StatementsPanel(final Data data, JFrame frame) {
        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        LinkButton selectAll = new LinkButton("Select all");
        LinkButton selectNone = new LinkButton("Select none");
        LinkButton autoSelect = new LinkButton("Autoselect");
        add(selectAll);
        add(selectNone);
        add(autoSelect);

        Component list = createStatementList(data, selectAll, selectNone);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll);

        layout.putConstraint(SpringLayout.NORTH, selectAll, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, selectAll, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, selectNone, 5, SpringLayout.SOUTH, selectAll);
        layout.putConstraint(SpringLayout.WEST, selectNone, 0, SpringLayout.WEST, selectAll);
        layout.putConstraint(SpringLayout.NORTH, autoSelect, 5, SpringLayout.SOUTH, selectNone);
        layout.putConstraint(SpringLayout.WEST, autoSelect, 0, SpringLayout.WEST, selectAll);

        layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH, autoSelect);
        layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST, this);
        // layout
        setPreferredSize(new Dimension(scroll.getPreferredSize().width, frame.getHeight() * 2 / 3));

    }

    private Component createStatementList(final Data data, LinkButton selectAll,
            LinkButton selectNone) {
        Set<Integer> statementIds = data.getStatements().keySet();
        statementCheckBoxes = new Object[statementIds.size()];
        statementList = new JCheckBoxList();
        int i = 0;
        for (Integer statementId : statementIds) {
            final JCheckBox checkBox = new JCheckBox(
                    statementId + ". " + truncate(data.getStatements().get(statementId)));
            checkBox.setSelected(data.getStatementFilter().contains(statementId));
            statementCheckBoxes[i] = checkBox;
            checkBox.addChangeListener(createChangeListener(checkBox, data, statementId));
            i++;
        }
        statementList.setListData(statementCheckBoxes);
        // event listeners
        selectAll.addActionListener(createSelectAllActionListener(statementCheckBoxes));
        selectNone.addActionListener(createSelectNoneActionListener(statementCheckBoxes));
        return statementList;
    }

    private String truncate(String s) {
        int maxLength = 103;
        if (s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        } else {
            return s;
        }
    }

    private ChangeListener createChangeListener(final JCheckBox checkBox, final Data data,
            final Integer statementId) {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (checkBox.isSelected())
                    data.getStatementFilter().add(statementId);
                else
                    data.getStatementFilter().remove(statementId);
                if (!updatingMultipleCheckBoxes) {
                    EventManager.getInstance().notify(new Event(data, Events.DATA_CHANGED));
                }
            }
        };
    }

    private ActionListener createSelectNoneActionListener(final Object[] checkBoxes) {
        return createSetAllListener(checkBoxes, false);
    }

    private ActionListener createSelectAllActionListener(final Object[] checkBoxes) {
        return createSetAllListener(checkBoxes, true);
    }

    private ActionListener createSetAllListener(final Object[] checkBoxes, boolean value) {

        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatingMultipleCheckBoxes = true;
                try {
                    for (int i = 0; i < checkBoxes.length; i++) {
                        JCheckBox checkBox = (JCheckBox) checkBoxes[i];
                        if (i == checkBoxes.length - 1) {
                            updatingMultipleCheckBoxes = false;
                        }
                        checkBox.setSelected(value);
                    }
                    repaint();
                } finally {
                    updatingMultipleCheckBoxes = false;
                }
            }
        };
    }

}
