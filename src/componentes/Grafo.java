package componentes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo não direcionado representado por listas de adjacência.
 *
 * <p>Os vértices são identificados internamente por inteiros 0..n-1 (o que permite usar
 * vetores simples nos algoritmos) e externamente por nomes (por exemplo, o nome de uma
 * cidade). Arestas paralelas são ignoradas, porque não alteram a conectividade.</p>
 */
public final class Grafo {

    private final List<String> nomes = new ArrayList<>();
    private final Map<String, Integer> indicePorNome = new HashMap<>();
    private final List<List<Integer>> adjacencia = new ArrayList<>();
    private final Set<Long> chavesArestas = new HashSet<>();
    private int numArestas;

    /** Adiciona um vértice (se ainda não existir) e devolve o seu índice. */
    public int adicionarVertice(String nome) {
        Integer existente = indicePorNome.get(nome);
        if (existente != null) return existente;
        int v = nomes.size();
        nomes.add(nome);
        indicePorNome.put(nome, v);
        adjacencia.add(new ArrayList<>());
        return v;
    }

    /** Cria {@code n} vértices chamados "0", "1", ..., "n-1" (útil em testes). */
    public static Grafo comVertices(int n) {
        Grafo g = new Grafo();
        for (int i = 0; i < n; i++) g.adicionarVertice(Integer.toString(i));
        return g;
    }

    /** Adiciona a aresta {u, v}. Devolve false se ela já existia. */
    public boolean adicionarAresta(int u, int v) {
        validar(u);
        validar(v);
        if (!chavesArestas.add(chave(u, v))) return false;
        adjacencia.get(u).add(v);
        if (u != v) adjacencia.get(v).add(u);   // laço: guardado uma única vez
        numArestas++;
        return true;
    }

    /** Adiciona a aresta entre dois vértices dados pelo nome (criando-os se preciso). */
    public boolean adicionarAresta(String a, String b) {
        return adicionarAresta(adicionarVertice(a), adicionarVertice(b));
    }

    /** Remove a aresta {u, v}. Devolve false se ela não existia. */
    public boolean removerAresta(int u, int v) {
        validar(u);
        validar(v);
        if (!chavesArestas.remove(chave(u, v))) return false;
        adjacencia.get(u).remove(Integer.valueOf(v));
        if (u != v) adjacencia.get(v).remove(Integer.valueOf(u));
        numArestas--;
        return true;
    }

    public boolean removerAresta(String a, String b) {
        return removerAresta(indiceDe(a), indiceDe(b));
    }

    public boolean temAresta(int u, int v) {
        return chavesArestas.contains(chave(u, v));
    }

    public int numVertices() { return nomes.size(); }

    public int numArestas() { return numArestas; }

    public List<Integer> vizinhos(int v) {
        validar(v);
        return Collections.unmodifiableList(adjacencia.get(v));
    }

    public String nome(int v) {
        validar(v);
        return nomes.get(v);
    }

    /** Índice do vértice com esse nome; lança exceção se ele não existir. */
    public int indiceDe(String nome) {
        Integer v = indicePorNome.get(nome);
        if (v == null) throw new IllegalArgumentException("vértice inexistente: " + nome);
        return v;
    }

    public boolean contem(String nome) { return indicePorNome.containsKey(nome); }

    /** Lista de arestas {u, v} com u <= v, na ordem de inserção dos vértices. */
    public List<int[]> arestas() {
        List<int[]> lista = new ArrayList<>(numArestas);
        for (int u = 0; u < adjacencia.size(); u++)
            for (int v : adjacencia.get(u))
                if (u <= v) lista.add(new int[] {u, v});
        return lista;
    }

    /** Cópia independente (para simular remoções sem alterar o original). */
    public Grafo copia() {
        Grafo g = new Grafo();
        for (String n : nomes) g.adicionarVertice(n);
        for (int[] e : arestas()) g.adicionarAresta(e[0], e[1]);
        return g;
    }

    private void validar(int v) {
        if (v < 0 || v >= nomes.size())
            throw new IndexOutOfBoundsException("vértice " + v + " fora do intervalo 0.." + (nomes.size() - 1));
    }

    private static long chave(int u, int v) {
        int a = Math.min(u, v), b = Math.max(u, v);
        return ((long) a << 32) | (b & 0xffffffffL);
    }
}
