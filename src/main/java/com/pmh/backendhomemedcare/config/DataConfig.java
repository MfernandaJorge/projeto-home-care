package com.pmh.backendhomemedcare.config;

import com.pmh.backendhomemedcare.model.entity.*;
import com.pmh.backendhomemedcare.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Objects;

@Component
public class DataConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataConfig.class);

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
        // checar por descrições existentes antes de inserir
        List<String> existentes = tipoRepo.findAll().stream().map(TipoAtendimento::getDescricao).toList();

        List<TipoAtendimento> toCreate = new ArrayList<>();
        if (!existentes.contains("Baixa complexidade")) {
            TipoAtendimento baixa = new TipoAtendimento();
            baixa.setDescricao("Baixa complexidade");
            baixa.setDuracaoMinutos(30);
            toCreate.add(baixa);
        }
        if (!existentes.contains("Média complexidade")) {
            TipoAtendimento media = new TipoAtendimento();
            media.setDescricao("Média complexidade");
            media.setDuracaoMinutos(50);
            toCreate.add(media);
        }
        if (!existentes.contains("Alta complexidade")) {
            TipoAtendimento alta = new TipoAtendimento();
            alta.setDescricao("Alta complexidade");
            alta.setDuracaoMinutos(90);
            toCreate.add(alta);
        }

        if (!toCreate.isEmpty()) {
            tipoRepo.saveAll(toCreate);
            logger.info("✅ Tipos de atendimento cadastrados: {}", toCreate.stream().map(TipoAtendimento::getDescricao).collect(Collectors.joining(", ")));
        } else {
            logger.info("ℹ️ Tipos de atendimento já existentes, nada a fazer.");
        }
    }

    // ============================================================
    // 🔹 Profissionais e jornadas
    // ============================================================
    private void inicializarProfissionaisComJornada() {
        // carregue profissionais existentes por email para evitar duplicatas
        Set<String> existentesEmails = profissionalRepo.findAll().stream()
                .map(Profissional::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // ====== ENDEREÇOS ======
        final Endereco e1Tmp = criarEndereco("Rua das Flores", 100, "Centro", 5.0, 15);
        final Endereco e2Tmp = criarEndereco("Av. dos Médicos", 200, "Taquaral", 8.0, 20);
        final Endereco e3Tmp = criarEndereco("Rua das Palmeiras", 50, "Cambuí", 6.5, 18);
        final Endereco e4Tmp = criarEndereco("Rua das Rosas", 700, "Jardim São Gabriel", 10.0, 25);
        final Endereco e5Tmp = criarEndereco("Av. Brasil", 1200, "Nova Campinas", 9.5, 22);

        // Reusar endereços existentes quando possível (usa os temporários dentro da lambda)
        Endereco e1 = enderecoRepo.findByEndereco(e1Tmp).orElseGet(() -> enderecoRepo.save(e1Tmp));
        Endereco e2 = enderecoRepo.findByEndereco(e2Tmp).orElseGet(() -> enderecoRepo.save(e2Tmp));
        Endereco e3 = enderecoRepo.findByEndereco(e3Tmp).orElseGet(() -> enderecoRepo.save(e3Tmp));
        Endereco e4 = enderecoRepo.findByEndereco(e4Tmp).orElseGet(() -> enderecoRepo.save(e4Tmp));
        Endereco e5 = enderecoRepo.findByEndereco(e5Tmp).orElseGet(() -> enderecoRepo.save(e5Tmp));

        // ====== PROFISSIONAIS (criar somente os ausentes por email) ======
        List<Profissional> desejados = new ArrayList<>();
        Profissional p1 = criarProfissional("Dra. Ana Souza", "12345678900", e1, 1, "ana.souza@homemedcare.com", "199999999");
        Profissional p2 = criarProfissional("Dr. Bruno Lima", "98765432100", e2, 2, "bruno.lima@homemedcare.com", "199888888");
        Profissional p3 = criarProfissional("Enf. Carla Mendes", "55566677788", e3, 3, "carla.mendes@homemedcare.com", "199777777");
        Profissional p4 = criarProfissional("Tec. Daniel Rocha", "44455566677", e4, 4, "daniel.rocha@homemedcare.com", "199666666");
        Profissional p5 = criarProfissional("Dr. Eduardo Faria", "33344455566", e5, 5, "eduardo.faria@homemedcare.com", "199555555");
        desejados.add(p1); desejados.add(p2); desejados.add(p3); desejados.add(p4); desejados.add(p5);

        List<Profissional> toSave = new ArrayList<>();
        for (Profissional p : desejados) {
            if (p.getEmail() != null && existentesEmails.contains(p.getEmail())) {
                logger.info("ℹ️ Profissional já existe (por email), pulando: {}", p.getEmail());
            } else {
                toSave.add(p);
            }
        }

        // ====== JORNADA TEMPLATES ======
        // Se não houver jornadas, criamos os templates; caso contrário, reusamos as existentes (ordem heurística)
        JornadaProfissional jornadaManha = null;
        JornadaProfissional jornadaNoite = null;

        if (jornadaRepo.count() == 0) {
            jornadaManha = new JornadaProfissional();
            List<JornadaDia> diasManha = new ArrayList<>();
            for (DayOfWeek d : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d);
                jd.setInicio(LocalTime.of(7, 0));
                jd.setFim(LocalTime.of(17, 0));
                jd.setInicioAlmoco(LocalTime.of(12, 0));
                jd.setFimAlmoco(LocalTime.of(13, 0));
                jd.setTrabalha(true);
                jd.setJornada(jornadaManha);
                diasManha.add(jd);
            }
            // add saturday and sunday off entries (trabalha=false)
            for (DayOfWeek d : List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d);
                jd.setTrabalha(false);
                jd.setJornada(jornadaManha);
                diasManha.add(jd);
            }
            jornadaManha.setDiasSemana(diasManha);
            jornadaRepo.save(jornadaManha);

            jornadaNoite = new JornadaProfissional();
            List<JornadaDia> diasNoite = new ArrayList<>();
            for (DayOfWeek d : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d);
                jd.setInicio(LocalTime.of(17, 0));
                jd.setFim(LocalTime.of(3, 0));
                jd.setInicioAlmoco(LocalTime.of(22, 0)); // jantar start
                jd.setFimAlmoco(LocalTime.of(23, 0)); // jantar end
                jd.setTrabalha(true);
                jd.setJornada(jornadaNoite);
                diasNoite.add(jd);
            }
            // saturday and sunday defaultize to trabalhar false but provide weekend schedule separately
            for (DayOfWeek d : List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d);
                jd.setTrabalha(false);
                jd.setJornada(jornadaNoite);
                diasNoite.add(jd);
            }
            jornadaNoite.setDiasSemana(diasNoite);
            jornadaRepo.save(jornadaNoite);

            // cria template Fim de Semana localmente (não usado para assign padrão, mas útil para consulta)
            JornadaProfissional jornadaFimSemana = new JornadaProfissional();
            List<JornadaDia> diasFim = new ArrayList<>();
            for (DayOfWeek d : DayOfWeek.values()) {
                JornadaDia jd = new JornadaDia();
                jd.setDiaSemana(d);
                if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) {
                    jd.setInicio(LocalTime.of(8, 0));
                    jd.setFim(LocalTime.of(15, 0));
                    jd.setTrabalha(true);
                } else {
                    jd.setTrabalha(false);
                }
                jd.setJornada(jornadaFimSemana);
                diasFim.add(jd);
            }
            jornadaFimSemana.setDiasSemana(diasFim);
            jornadaRepo.save(jornadaFimSemana);
        } else {
            // Reusar jornadas existentes: pega até 3 existentes e mapeia heurísticamente
            List<JornadaProfissional> existentes = jornadaRepo.findAll();
            if (!existentes.isEmpty()) jornadaManha = existentes.get(0);
            if (existentes.size() > 1) jornadaNoite = existentes.get(1);
        }

        // ASSIGN TEMPLATE JORNADAS TO PROFESSIONALS (apenas para os que serão salvos agora)
        for (Profissional p : toSave) {
            // escolhe jornada baseada em ocupacao como heurística
            if (p.getOcupacao() == 1) p.setJornada(jornadaManha);
            else if (p.getOcupacao() == 2) p.setJornada(jornadaNoite != null ? jornadaNoite : jornadaManha);
            else if (p.getOcupacao() == 3) p.setJornada(jornadaNoite != null ? jornadaNoite : jornadaManha);
            else if (p.getOcupacao() == 4) p.setJornada(criarJornadaPlantonista());
            else p.setJornada(jornadaManha);
        }

        if (!toSave.isEmpty()) {
            profissionalRepo.saveAll(toSave);
            logger.info("✅ Profissionais criados: {}", toSave.stream().map(Profissional::getEmail).collect(Collectors.joining(", ")));
        } else {
            logger.info("ℹ️ Nenhum profissional novo para criar.");
        }
    }

    // ============================================================
    // 🔹 Paciente Maria Bonita
    // ============================================================
    private void inicializarPacienteMariaBonita() {
        // checar por documento (mais estável que count)
        boolean existe = pacienteRepo.findAll().stream().anyMatch(p -> "2312".equals(p.getDocumento()));
        if (existe) {
            logger.info("ℹ️ Paciente Maria Bonita já existe (documento 2312), pulando criação.");
            return;
        }

        final Endereco enderecoTmp = new Endereco();
        enderecoTmp.setLogradouro("Rua Mario Galante Junior");
        enderecoTmp.setNumero(365);
        enderecoTmp.setBairro("Centro");
        enderecoTmp.setCidade("Campinas");
        enderecoTmp.setEstado("SP");
        enderecoTmp.setCep("01001-000");
        enderecoTmp.setDistanceKm(8.0404);
        enderecoTmp.setDurationMin(11.0);
        enderecoTmp.setLatitude(-22.8629941);
        enderecoTmp.setLongitude(-47.0610529);

        Endereco endereco = enderecoRepo.findByEndereco(enderecoTmp).orElseGet(() -> enderecoRepo.save(enderecoTmp));

        Paciente paciente = new Paciente();
        paciente.setNome("Maria Bonita");
        paciente.setDocumento("2312");
        paciente.setEndereco(endereco);
        paciente.setDataNascimento(LocalDate.of(1990, 5, 15));
        paciente.setTelefone("1198765431");
        paciente.setEmail("joao.silva@example.com");

        pacienteRepo.save(paciente);

        logger.info("✅ Paciente Maria Bonita criada com sucesso!");
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

    private JornadaProfissional criarJornadaPlantonista() {
        JornadaProfissional jornada = new JornadaProfissional();

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
        return jornada;
    }
}
