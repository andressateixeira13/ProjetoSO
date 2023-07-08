package model;

import java.util.Collection;

public class Atributo {
    private String nome;
    private String valor;

    public Atributo(String nome, String valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public static Atributo atributoPorNome(Collection<Atributo> atributoCollection, String nome) {
        return atributoCollection.stream().filter(atr -> nome.equals(atr.getNome())).findFirst().orElse(null);
    }

    public String getNome() {
        return nome;
    }

    public String getValor() {
        return valor;
    }

}
