package componentes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lê um grafo de um arquivo texto (UTF-8), uma declaração por linha:
 *
 * <pre>
 * # comentário
 * vertice: Fortaleza                 (vértice, mesmo que isolado)
 * aresta:  Fortaleza -- Aquiraz      (aresta não direcionada; cria os vértices se preciso)
 * chave:   valor                     (qualquer outra chave é guardada em "extras")
 * </pre>
 *
 * Os nomes podem conter espaços ("Limoeiro do Norte"); o separador de aresta é " -- ".
 */
public final class LeitorGrafo {

    /** Grafo lido + declarações extras (ex.: "base", "alagada"), na ordem do arquivo. */
    public record Dados(Grafo grafo, Map<String, List<String>> extras) {
        public List<String> extra(String chave) { return extras.getOrDefault(chave, List.of()); }
    }

    private LeitorGrafo() {}

    public static Dados ler(Path arquivo) throws IOException {
        Grafo g = new Grafo();
        Map<String, List<String>> extras = new LinkedHashMap<>();
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            int hash = linha.indexOf('#');
            if (hash >= 0) linha = linha.substring(0, hash);
            linha = linha.strip();
            if (linha.isEmpty()) continue;
            int dois = linha.indexOf(':');
            if (dois < 0) throw erro(arquivo, i, "esperado 'chave: valor'");
            String chave = linha.substring(0, dois).strip().toLowerCase();
            String valor = linha.substring(dois + 1).strip();
            if (valor.isEmpty()) throw erro(arquivo, i, "valor vazio");
            switch (chave) {
                case "vertice" -> g.adicionarVertice(valor);
                case "aresta" -> {
                    String[] p = separarAresta(valor);
                    if (p == null) throw erro(arquivo, i, "aresta deve ter o formato 'A -- B'");
                    g.adicionarAresta(p[0], p[1]);
                }
                default -> extras.computeIfAbsent(chave, c -> new ArrayList<>()).add(valor);
            }
        }
        return new Dados(g, extras);
    }

    /** Divide "A -- B" em {"A", "B"}; devolve null se o formato for inválido. */
    public static String[] separarAresta(String texto) {
        String[] p = texto.split("\\s+--\\s+");
        if (p.length != 2 || p[0].isBlank() || p[1].isBlank()) return null;
        return new String[] {p[0].strip(), p[1].strip()};
    }

    private static IOException erro(Path arquivo, int linha, String msg) {
        return new IOException(arquivo + ", linha " + (linha + 1) + ": " + msg);
    }
}
