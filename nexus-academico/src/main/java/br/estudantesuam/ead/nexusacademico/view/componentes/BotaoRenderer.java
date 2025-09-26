package br.estudantesuam.ead.nexusacademico.view.componentes;

import br.estudantesuam.ead.nexusacademico.view.DesignTelasUI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderizador para exibir botões em uma célula da JTable.
 */
public class BotaoRenderer extends JPanel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    public BotaoRenderer() {
        // Layout para garantir botões lado a lado
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setOpaque(true);
        // Força a altura mínima para acomodar os botões lado a lado
        setPreferredSize(new Dimension(150, 30)); 

        btnEditar = new JButton("Editar");
        btnEditar.setBackground(DesignTelasUI.COR_PRIMARIA);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditar.setFocusPainted(false);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBackground(new Color(220, 20, 60)); 
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setFocusPainted(false);

        add(btnEditar);
        add(btnExcluir);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }
}