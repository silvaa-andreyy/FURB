import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MochilaGUI {
    private static final int BAG_CAPACITY = 5;
    private static String[] itens = new String[BAG_CAPACITY];
    private static int[] pesos = new int[BAG_CAPACITY];
    private static int pesoMaximo = 30;
    private static int pesoAtual = 0;

    private static JTextArea areaMochila;
    private static JProgressBar barraPeso;
    private static JTextField campoPesoMaximo;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MochilaGUI::criarInterface);
    }

    private static void criarInterface() {
        JFrame frame = new JFrame("Mochila Virtual");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel painelTopo = new JPanel();
        painelTopo.setLayout(new FlowLayout());
        painelTopo.add(new JLabel("Peso Máximo:"));

        campoPesoMaximo = new JTextField(String.valueOf(pesoMaximo), 5);
        painelTopo.add(campoPesoMaximo);

        JButton btnDefinirPeso = new JButton("Definir");
        btnDefinirPeso.addActionListener(e -> definirPesoMaximo());
        painelTopo.add(btnDefinirPeso);

        areaMochila = new JTextArea(10, 40);
        areaMochila.setEditable(false);

        // Criar barraPeso ANTES de chamar atualizarAreaMochila
        barraPeso = new JProgressBar(0, pesoMaximo);
        barraPeso.setStringPainted(true);
        barraPeso.setValue(pesoAtual);

        atualizarAreaMochila();

        JPanel painelBotoes = new JPanel(new GridLayout(5, 2, 5, 5));

        adicionarBotao(painelBotoes, "Adicionar item", e -> adicionarItem());
        adicionarBotao(painelBotoes, "Ver inventário", e -> atualizarAreaMochila());
        adicionarBotao(painelBotoes, "Item mais pesado", e -> mostrarItemMaisPesado());
        adicionarBotao(painelBotoes, "Ordenar mochila", e -> ordenarMochila());
        adicionarBotao(painelBotoes, "Descartar item", e -> descartarItem());
        adicionarBotao(painelBotoes, "Peso atual", e -> mostrarPesoAtual());
        adicionarBotao(painelBotoes, "Esvaziar mochila", e -> esvaziarMochila());
        adicionarBotao(painelBotoes, "Salvar mochila", e -> salvarMochila());
        adicionarBotao(painelBotoes, "Carregar mochila", e -> carregarMochila());
        adicionarBotao(painelBotoes, "Sair", e -> System.exit(0));

        frame.setLayout(new BorderLayout());
        frame.add(painelTopo, BorderLayout.NORTH);
        frame.add(new JScrollPane(areaMochila), BorderLayout.CENTER);
        frame.add(barraPeso, BorderLayout.SOUTH);
        frame.add(painelBotoes, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private static void adicionarBotao(JPanel painel, String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(acao);
        painel.add(botao);
    }

    private static void definirPesoMaximo() {
        try {
            pesoMaximo = Integer.parseInt(campoPesoMaximo.getText());
            barraPeso.setMaximum(pesoMaximo);
            atualizarAreaMochila();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Peso inválido.");
        }
    }

    private static void atualizarAreaMochila() {
        StringBuilder sb = new StringBuilder("📦 Itens na mochila:\n");
        pesoAtual = 0;
        for (int i = 0; i < BAG_CAPACITY; i++) {
            if (itens[i] != null) {
                sb.append("- ").append(itens[i]).append(" (").append(pesos[i]).append("kg)\n");
                pesoAtual += pesos[i];
            }
        }
        areaMochila.setText(sb.toString());
        barraPeso.setValue(pesoAtual);
        barraPeso.setString(pesoAtual + " / " + pesoMaximo + " kg");
    }

    private static void adicionarItem() {
        if (pesoAtual >= pesoMaximo) {
            JOptionPane.showMessageDialog(null, "A mochila já atingiu o peso máximo.");
            return;
        }

        String nome = JOptionPane.showInputDialog("Nome do item:");
        if (nome == null || nome.isEmpty()) return;

        try {
            int peso = Integer.parseInt(JOptionPane.showInputDialog("Peso do item:"));
            if (pesoAtual + peso > pesoMaximo) {
                JOptionPane.showMessageDialog(null, "Item ultrapassa o peso máximo da mochila.");
                return;
            }
            for (int i = 0; i < BAG_CAPACITY; i++) {
                if (itens[i] == null) {
                    itens[i] = nome;
                    pesos[i] = peso;
                    atualizarAreaMochila();
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Mochila cheia!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Peso inválido.");
        }
    }

    private static void mostrarItemMaisPesado() {
        int maxPeso = -1;
        String maisPesado = "Nenhum";
        for (int i = 0; i < BAG_CAPACITY; i++) {
            if (itens[i] != null && pesos[i] > maxPeso) {
                maxPeso = pesos[i];
                maisPesado = itens[i];
            }
        }
        JOptionPane.showMessageDialog(null, "Item mais pesado: " + maisPesado + " (" + maxPeso + "kg)");
    }

    private static void ordenarMochila() {
        for (int i = 0; i < BAG_CAPACITY - 1; i++) {
            for (int j = i + 1; j < BAG_CAPACITY; j++) {
                if (itens[i] != null && itens[j] != null && pesos[i] > pesos[j]) {
                    String tempItem = itens[i];
                    itens[i] = itens[j];
                    itens[j] = tempItem;

                    int tempPeso = pesos[i];
                    pesos[i] = pesos[j];
                    pesos[j] = tempPeso;
                }
            }
        }
        atualizarAreaMochila();
    }

    private static void descartarItem() {
        String item = JOptionPane.showInputDialog("Nome do item para remover:");
        for (int i = 0; i < BAG_CAPACITY; i++) {
            if (itens[i] != null && itens[i].equalsIgnoreCase(item)) {
                pesoAtual -= pesos[i];
                itens[i] = null;
                pesos[i] = 0;
                atualizarAreaMochila();
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Item não encontrado.");
    }

    private static void mostrarPesoAtual() {
        JOptionPane.showMessageDialog(null, "Peso atual da mochila: " + pesoAtual + "kg");
    }

    private static void esvaziarMochila() {
        for (int i = 0; i < BAG_CAPACITY; i++) {
            itens[i] = null;
            pesos[i] = 0;
        }
        atualizarAreaMochila();
    }

    private static void salvarMochila() {
        JOptionPane.showMessageDialog(null, "Funcionalidade de salvar ainda será implementada.");
    }

    private static void carregarMochila() {
        JOptionPane.showMessageDialog(null, "Funcionalidade de carregar ainda será implementada.");
    }
}
