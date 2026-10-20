package componentes;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado do cálculo de componentes conexos: um rótulo por vértice.
 *
 * <p>Os componentes são numerados 0..k-1 na ordem do menor índice de vértice que cada
 * um contém. Com essa normalização, os três algoritmos (DFS, BFS e União-Busca)
 * produzem exatamente o mesmo vetor de rótulos, o que facilita compará-los nos testes.</p>
 */
public final class Componentes {

    private final int[] rotulo;      // rotulo[v] = componente do vértice v
    private final int quantidade;
    private final int[] tamanho;

    Componentes(int[] rotulo, int quantidade) {
        this.rotulo = rotulo;
        this.quantidade = quantidade;
        this.tamanho = new int[quantidade];
        for (int c : rotulo) tamanho[c]++;
    }

    /** Número de componentes conexos (k). */
    public int quantidade() { return quantidade; }

    /** Componente ao qual o vértice v pertence. */
    public int componenteDe(int v) { return rotulo[v]; }

    /** Consulta em O(1): existe caminho entre u e v? */
    public boolean conectados(int u, int v) { return rotulo[u] == rotulo[v]; }

    public int tamanho(int componente) { return tamanho[componente]; }

    /** Vértices de cada componente, em ordem crescente de índice. */
    public List<List<Integer>> listar() {
        List<List<Integer>> grupos = new ArrayList<>(quantidade);
        for (int c = 0; c < quantidade; c++) grupos.add(new ArrayList<>(tamanho[c]));
        for (int v = 0; v < rotulo.length; v++) grupos.get(rotulo[v]).add(v);
        return grupos;
    }

    /** Cópia do vetor de rótulos. */
    public int[] rotulos() { return rotulo.clone(); }

    /**
     * Renumera rótulos arbitrários (ex.: representantes da União-Busca) para 0..k-1,
     * na ordem em que aparecem ao percorrer os vértices 0, 1, 2, ...
     */
    static Componentes normalizar(int[] representante) {
        int n = representante.length;
        int[] novo = new int[n];
        int[] mapa = new int[n];
        java.util.Arrays.fill(mapa, -1);
        int k = 0;
        for (int v = 0; v < n; v++) {
            int r = representante[v];
            if (mapa[r] == -1) mapa[r] = k++;
            novo[v] = mapa[r];
        }
        return new Componentes(novo, k);
    }
}
