package componentes;

import java.util.Arrays;

/**
 * Componentes conexos por busca em largura (BFS).
 *
 * <p>Mesma ideia da DFS, mas a fronteira é uma fila: os vértices são descobertos em
 * "camadas" de distância crescente a partir da origem. Custo: O(V + E).</p>
 */
public final class BuscaEmLargura implements AlgoritmoComponentes {

    @Override
    public String nome() { return "BFS"; }

    @Override
    public Componentes calcular(Grafo g) {
        int n = g.numVertices();
        int[] rotulo = new int[n];
        Arrays.fill(rotulo, -1);
        int[] fila = new int[n];                 // fila circular desnecessária: cada vértice entra 1 vez
        int k = 0;
        for (int s = 0; s < n; s++) {
            if (rotulo[s] != -1) continue;
            int inicio = 0, fim = 0;
            rotulo[s] = k;
            fila[fim++] = s;
            while (inicio < fim) {
                int u = fila[inicio++];          // retira da frente da fila
                for (int w : g.vizinhos(u)) {
                    if (rotulo[w] == -1) {
                        rotulo[w] = k;
                        fila[fim++] = w;         // insere no fim da fila
                    }
                }
            }
            k++;
        }
        return new Componentes(rotulo, k);
    }
}
