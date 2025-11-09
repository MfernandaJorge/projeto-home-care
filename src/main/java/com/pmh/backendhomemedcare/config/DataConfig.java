package com.pmh.backendhomemedcare.config;

import com.pmh.backendhomemedcare.model.entity.*;
import com.pmh.backendhomemedcare.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataConfig {

    private final TipoAtendimentoRepo tipoRepo;
    private final EnderecoRepo enderecoRepo;
    private final ProfissionalRepo profissionalRepo;
    private final JornadaProfissionalRepo jornadaRepo;
    private final PacienteRepository pacienteRepo;

    public DataConfig(TipoAtendimentoRepo tipoRepo,
                      EnderecoRepo enderecoRepo,
                      ProfissionalRepo profissionalRepo,
                      JornadaProfissionalRepo jornadaRepo,
                      PacienteRepository pacienteRepo) {
        this.tipoRepo = tipoRepo;
        this.enderecoRepo = enderecoRepo;
        this.profissionalRepo = profissionalRepo;
        this.jornadaRepo = jornadaRepo;
        this.pacienteRepo = pacienteRepo;
    }

    @PostConstruct
    public void init() {
        inicializarTiposAtendimento();
        inicializarProfissionaisComJornada();
        inicializarPacienteMariaBonita();
    }

    // ============================================================
    // 🔹 Tipos de atendimento
    // ============================================================
    private void inicializarTiposAtendimento() {
        if (tipoRepo.count() == 0) {
            TipoAtendimento baixa = new TipoAtendimento();
            baixa.setDescricao("Baixa complexidade");
            baixa.setDuracaoMinutos(30);

            TipoAtendimento media = new TipoAtendimento();
            media.setDescricao("Média complexidade");
            media.setDuracaoMinutos(50);

            TipoAtendimento alta = new TipoAtendimento();
            alta.setDescricao("Alta complexidade");
            alta.setDuracaoMinutos(90);

            tipoRepo.saveAll(List.of(baixa, media, alta));
            System.out.println("✅ Tipos de atendimento cadastrados!");
        }
    }

    // ============================================================
    // 🔹 Profissionais e jornadas
    // ============================================================
    private void inicializarProfissionaisComJornada() {
        if (profissionalRepo.count() > 0) return;

        // ====== ENDEREÇOS ======
        Endereco e1 = criarEndereco("Rua das Flores", 100, "Centro", 5.0, 15);
        Endereco e2 = criarEndereco("Av. dos Médicos", 200, "Taquaral", 8.0, 20);
        Endereco e3 = criarEndereco("Rua das Palmeiras", 50, "Cambuí", 6.5, 18);
        Endereco e4 = criarEndereco("Rua das Rosas", 700, "Jardim São Gabriel", 10.0, 25);
        Endereco e5 = criarEndereco("Av. Brasil", 1200, "Nova Campinas", 9.5, 22);

        enderecoRepo.saveAll(List.of(e1, e2, e3, e4, e5));

        // ====== PROFISSIONAIS ======
        Profissional p1 = criarProfissional("Dra. Ana Souza", "12345678900", e1, 1, "ana.souza@homemedcare.com", "199999999");
        Profissional p2 = criarProfissional("Dr. Bruno Lima", "98765432100", e2, 2, "bruno.lima@homemedcare.com", "199888888");
        Profissional p3 = criarProfissional("Enf. Carla Mendes", "55566677788", e3, 3, "carla.mendes@homemedcare.com", "199777777");
        Profissional p4 = criarProfissional("Tec. Daniel Rocha", "44455566677", e4, 4, "daniel.rocha@homemedcare.com", "199666666");
        Profissional p5 = criarProfissional("Dr. Eduardo Faria", "33344455566", e5, 5, "eduardo.faria@homemedcare.com", "199555555");

        profissionalRepo.saveAll(List.of(p1, p2, p3, p4, p5));

        // ====== JORNADAS ======
        criarJornadaPadrao(p1, LocalTime.of(8, 0), LocalTime.of(17, 0),
                LocalTime.of(12, 0), LocalTime.of(13, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));

        criarJornadaPadrao(p2, LocalTime.of(10, 0), LocalTime.of(19, 0),
                LocalTime.of(13, 0), LocalTime.of(14, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));

        criarJornadaPadrao(p3, LocalTime.of(18, 0), LocalTime.of(23, 59),
                null, null,
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));

        criarJornadaPlantonista(p4);

        criarJornadaPadrao(p5, LocalTime.of(8, 0), LocalTime.of(17, 0),
                LocalTime.of(12, 0), LocalTime.of(13, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));

        System.out.println("✅ Profissionais e jornadas criados!");
    }

    // ============================================================
    // 🔹 Paciente Maria Bonita
    // ============================================================
    private void inicializarPacienteMariaBonita() {
        if (pacienteRepo.count() > 0) return;

        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Mario Galante Junior");
        endereco.setNumero(365);
        endereco.setBairro("Centro");
        endereco.setCidade("Campinas");
        endereco.setEstado("SP");
        endereco.setCep("01001-000");
        endereco.setDistanceKm(8.0404);
        endereco.setDurationMin(11.0);
        endereco.setLatitude(-22.8629941);
        endereco.setLongitude(-47.0610529);

        enderecoRepo.save(endereco);

        Paciente paciente = new Paciente();
        paciente.setNome("Maria Bonita");
        paciente.setDocumento("2312");
        paciente.setEndereco(endereco);
        paciente.setDataNascimento(LocalDate.of(1990, 5, 15));
        paciente.setTelefone("1198765431");
        paciente.setEmail("joao.silva@example.com");

        pacienteRepo.save(paciente);

        System.out.println("✅ Paciente Maria Bonita criada com sucesso!");
    }

    // ============================================================
    // 🔹 Métodos utilitários
    // ============================================================
    private Endereco criarEndereco(String logradouro, int numero, String bairro, double distKm, double durMin) {
        Endereco e = new Endereco();
        e.setLogradouro(logradouro);
        e.setNumero(numero);
        e.setBairro(bairro);
        e.setCidade("Campinas");
        e.setEstado("SP");
        e.setCep("13000-000");
        e.setDistanceKm(distKm);
        e.setDurationMin(durMin);
        return e;
    }

    private Profissional criarProfissional(String nome, String doc, Endereco e, int ocupacao, String email, String telefone) {
        Profissional p = new Profissional();
        p.setNome(nome);
        p.setDocumento(doc);
        p.setEndereco(e);
        p.setOcupacao(ocupacao);
        p.setEmail(email);
        p.setTelefone(telefone);
        return p;
    }

    private void criarJornadaPadrao(Profissional profissional, LocalTime inicio, LocalTime fim,
                                    LocalTime almocoInicio, LocalTime almocoFim, List<DayOfWeek> dias) {

        JornadaProfissional jornada = new JornadaProfissional();
        jornada.setProfissional(profissional);

        List<JornadaDia> diasSemana = new ArrayList<>();
        for (DayOfWeek d : dias) {
            JornadaDia jd = new JornadaDia();
            jd.setDiaSemana(d);
            jd.setInicio(inicio);
            jd.setFim(fim);
            jd.setInicioAlmoco(almocoInicio);
            jd.setFimAlmoco(almocoFim);
            jd.setTrabalha(true);
            jd.setJornada(jornada);
            diasSemana.add(jd);
        }

        jornada.setDiasSemana(diasSemana);
        jornadaRepo.save(jornada);
    }

    private void criarJornadaPlantonista(Profissional profissional) {
        JornadaProfissional jornada = new JornadaProfissional();
        jornada.setProfissional(profissional);

        List<JornadaDia> dias = new ArrayList<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            JornadaDia jd = new JornadaDia();
            jd.setDiaSemana(d);
            jd.setInicio(LocalTime.of(7, 0));
            jd.setFim(LocalTime.of(23, 59));
            jd.setTrabalha(d.getValue() % 2 == 0); // dia sim, dia não
            jd.setJornada(jornada);
            dias.add(jd);
        }

        jornada.setDiasSemana(dias);
        jornadaRepo.save(jornada);
    }
}
