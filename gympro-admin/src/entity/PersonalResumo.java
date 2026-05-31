package entity;

public class PersonalResumo {

    private final String nome;
    private final String email;
    private final String cref;
    private final String especialidade;
    private final String status;

    public PersonalResumo(String nome, String email, String cref, String especialidade, String status) {
        this.nome = nome;
        this.email = email;
        this.cref = cref;
        this.especialidade = especialidade;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCref() {
        return cref;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getStatus() {
        return status;
    }
}
