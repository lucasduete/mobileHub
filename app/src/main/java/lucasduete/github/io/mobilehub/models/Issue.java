package lucasduete.github.io.mobilehub.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "issuesPinneds")
public class Issue {

    @DatabaseField(id = true)
    private int numero;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField
    private String Descricao;

    @DatabaseField(canBeNull = false)
    private String nomeAutor;

    @DatabaseField
    private String fotoAutor;

    public Issue() {

    }

    public Issue(int numero, String nome, String descricao, String nomeAutor, String fotoAutor) {
        this.numero = numero;
        this.nome = nome;
        Descricao = descricao;
        this.nomeAutor = nomeAutor;
        this.fotoAutor = fotoAutor;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getFotoAutor() {
        return fotoAutor;
    }

    public void setFotoAutor(String fotoAutor) {
        this.fotoAutor = fotoAutor;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue issue = (Issue) o;

        if (numero != issue.numero) return false;
        if (nome != null ? !nome.equals(issue.nome) : issue.nome != null) return false;
        if (Descricao != null ? !Descricao.equals(issue.Descricao) : issue.Descricao != null)
            return false;
        if (nomeAutor != null ? !nomeAutor.equals(issue.nomeAutor) : issue.nomeAutor != null)
            return false;
        return fotoAutor != null ? fotoAutor.equals(issue.fotoAutor) : issue.fotoAutor == null;
    }

    @Override
    public int hashCode() {

        int result = numero;
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (Descricao != null ? Descricao.hashCode() : 0);
        result = 31 * result + (nomeAutor != null ? nomeAutor.hashCode() : 0);
        result = 31 * result + (fotoAutor != null ? fotoAutor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer("Issue{");
        sb.append("numero=").append(numero);
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", Descricao='").append(Descricao).append('\'');
        sb.append(", nomeAutor='").append(nomeAutor).append('\'');
        sb.append(", fotoAutor='").append(fotoAutor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
