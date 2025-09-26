package br.estudantesuam.ead.nexusacademico.view.componentes;

import br.estudantesuam.ead.nexusacademico.view.DesignTelasUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * Editor para permitir a ação dos botões em uma célula da JTable.
 * @param <T> O tipo de objeto (Modelo) contido na célula.
 */
public class BotaoEditor<T> extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 1L;
    private final JPanel painel;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private T valorAtual;

    public BotaoEditor(Consumer<T> acaoEditar, Consumer<T> acaoExcluir) {
        // Layout para garantir botões lado a lado
        painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); 
        painel.setOpaque(true);
        // Força a altura mínima para acomodar os botões lado a lado
        painel.setPreferredSize(new Dimension(150, 30)); 

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(DesignTelasUI.COR_PRIMARIA); 
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnExcluir = new JButton("Excluir");
        btnExcluir.setBackground(new Color(220, 20, 60)); 
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setFocusPainted(false);
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEditar.addActionListener(_ -> {
            fireEditingStopped();
            acaoEditar.accept(valorAtual);
        });

        btnExcluir.addActionListener(_ -> {
            fireEditingStopped();
            acaoExcluir.accept(valorAtual);
        });

        painel.add(btnEditar);
        painel.add(btnExcluir);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        @SuppressWarnings("unchecked")
        T model = (T) value;
        this.valorAtual = model; 
        
        if (isSelected) {
            painel.setBackground(table.getSelectionBackground());
        } else {
            painel.setBackground(table.getBackground());
        }
        return painel;
    }

    @Override
    public Object getCellEditorValue() {
        return valorAtual;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true; 
    }
}