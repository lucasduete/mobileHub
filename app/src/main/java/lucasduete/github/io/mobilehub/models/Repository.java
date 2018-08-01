package lucasduete.github.io.mobilehub.models;

public class Repository {

    private int stars;
    private int forks;
    private String foto;
    private String nome;
    private String nomeAutor;
    private String descricao;

    public Repository() {

    }

    public Repository(int stars, int forks, String foto, String nome, String nomeAutor, String descricao) {
        this.stars = stars;
        this.forks = forks;
        this.foto = foto;
        this.nome = nome;
        this.nomeAutor = nomeAutor;
        this.descricao = descricao;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
