import java.util.ArrayList;
import java.util.List;

public final class Grafo {
    private final List<String> nomes;
    private final List<List<Integer>> vizinhos;

    public Grafo(String... nomes) {
        this.nomes = List.of(nomes);
        vizinhos = new ArrayList<>(nomes.length);
        for (int i = 0; i < nomes.length; i++) vizinhos.add(new ArrayList<>());
    }

    public void adicionarAresta(String a, String b) {
        int x = indice(a), y = indice(b);
        if (!vizinhos.get(x).contains(y)) vizinhos.get(x).add(y);
        if (!vizinhos.get(y).contains(x)) vizinhos.get(y).add(x);
    }

    public void removerAresta(String a, String b) {
        int x = indice(a), y = indice(b);
        vizinhos.get(x).remove(Integer.valueOf(y));
        vizinhos.get(y).remove(Integer.valueOf(x));
    }

    public int tamanho() { return vizinhos.size(); }
    public String nome(int v) { return nomes.get(v); }
    public List<Integer> vizinhosDe(int v) { return vizinhos.get(v); }

    private int indice(String nome) {
        int i = nomes.indexOf(nome);
        if (i < 0) throw new IllegalArgumentException("município inexistente: " + nome);
        return i;
    }
}
