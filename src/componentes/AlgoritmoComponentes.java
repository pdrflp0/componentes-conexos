package componentes;

/** Interface comum às estratégias de cálculo de componentes conexos. */
public interface AlgoritmoComponentes {

    /** Nome curto do algoritmo, para exibição. */
    String nome();

    /** Calcula os componentes conexos de um grafo não direcionado. */
    Componentes calcular(Grafo g);
}
