import java.util.Arrays;

public final class BuscaEmProfundidade {
    public Componentes calcular(Grafo grafo) {
        int[] rotulo = new int[grafo.tamanho()];
        Arrays.fill(rotulo, -1);
        int[] pilha = new int[grafo.tamanho()];
        int quantidade = 0;

        for (int inicio = 0; inicio < grafo.tamanho(); inicio++) {
            if (rotulo[inicio] != -1) continue;
            int topo = 0;
            rotulo[inicio] = quantidade;
            pilha[topo++] = inicio;
            while (topo > 0) {
                int atual = pilha[--topo];
                for (int vizinho : grafo.vizinhosDe(atual)) {
                    if (rotulo[vizinho] == -1) {
                        rotulo[vizinho] = quantidade;
                        pilha[topo++] = vizinho;
                    }
                }
            }
            quantidade++;
        }
        return new Componentes(rotulo, quantidade);
    }
}
