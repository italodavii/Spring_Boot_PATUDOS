package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.dto.intencao.CriarIntencaoRequest;
import com.example.estudo_patudos_api_spring_jpa.dto.intencao.IntencaoDTO;
import com.example.estudo_patudos_api_spring_jpa.model.IntencaoAdocao;
import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.model.StatusIntencao;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.IntencaoAdocaoRepository;
import com.example.estudo_patudos_api_spring_jpa.repository.PetRepository;
import com.example.estudo_patudos_api_spring_jpa.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntencaoAdocaoService {

    private final IntencaoAdocaoRepository intencaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;

    public IntencaoAdocaoService(IntencaoAdocaoRepository intencaoRepository,
                                 UsuarioRepository usuarioRepository,
                                 PetRepository petRepository) {
        this.intencaoRepository = intencaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public IntencaoDTO criar(String email, CriarIntencaoRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));

        // Ação sensível: exige e-mail verificado (o front também bloqueia e redireciona ao perfil).
        if (!usuario.isEmailVerificado()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Verifique seu e-mail antes de enviar uma intenção de adoção.");
        }

        // findById só retorna pets ativos (@SQLRestriction) — pet excluído/inexistente -> 404.
        Pet pet = petRepository.findById(req.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado ou indisponível."));

        // Pet já adotado (ativo, mas status != disponivel) não aceita novas intenções.
        if (!"disponivel".equalsIgnoreCase(pet.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pet não está mais disponível para adoção.");
        }

        // Bloqueia intenção duplicada do mesmo usuário p/ o mesmo pet (pendente ou já aprovada).
        // Uma intenção recusada não impede nova tentativa.
        if (intencaoRepository.existsByUsuarioAndPetAndStatusIn(
                usuario, pet, List.of(StatusIntencao.PENDENTE, StatusIntencao.APROVADA))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Você já tem uma intenção de adoção em andamento para este pet.");
        }

        validar(req);

        IntencaoAdocao i = new IntencaoAdocao();
        i.setUsuario(usuario);
        i.setPet(pet);

        // Snapshots
        i.setAdotanteNome(usuario.getNomeCompleto());
        i.setAdotanteEmail(usuario.getEmail());
        i.setAdotanteTelefone(usuario.getTelefone());
        i.setPetNome(pet.getNome());
        i.setPetUrlFoto(pet.getUrlFoto());

        // Endereço (snapshot + salvo de volta no perfil)
        i.setLogradouro(req.logradouro().trim());
        i.setNumero(req.numero().trim());
        i.setComplemento(req.complemento() == null ? null : req.complemento().trim());
        i.setBairro(req.bairro().trim());
        i.setCidade(req.cidade().trim());
        i.setEstado(req.estado().trim());
        i.setCep(req.cep().trim());

        i.setTermoAceito(true);
        i.setMensagem(req.mensagem() == null || req.mensagem().isBlank() ? null : req.mensagem().trim());

        i.setTipoResidencia(req.tipoResidencia());
        i.setImovelSeguro(req.imovelSeguro());
        i.setTipoPosse(req.tipoPosse());
        i.setAlugadoAutorizaPets("ALUGADO".equals(req.tipoPosse()) ? req.alugadoAutorizaPets() : null);
        i.setTempoSozinho(req.tempoSozinho());
        i.setOutrosAnimais(req.outrosAnimais());
        i.setAnimaisVacinadosCastrados(!"NENHUM".equals(req.outrosAnimais()) ? req.animaisVacinadosCastrados() : null);
        i.setFamiliaDeAcordo(req.familiaDeAcordo());
        i.setPlanoViagemMudanca(req.planoViagemMudanca().trim());
        i.setPlanoComportamento(req.planoComportamento().trim());

        i.setStatus(StatusIntencao.PENDENTE);

        // Persiste o endereço no perfil para não redigitar numa próxima adoção.
        usuario.setLogradouro(i.getLogradouro());
        usuario.setNumero(i.getNumero());
        usuario.setComplemento(i.getComplemento());
        usuario.setBairro(i.getBairro());
        usuario.setCidade(i.getCidade());
        usuario.setEstado(i.getEstado());
        usuario.setCep(i.getCep());
        usuarioRepository.save(usuario);

        return IntencaoDTO.de(intencaoRepository.save(i));
    }

    @Transactional(readOnly = true)
    public List<IntencaoDTO> listar() {
        return intencaoRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(IntencaoDTO::de)
                .toList();
    }

    // Intenções do próprio usuário logado (seção "Minhas Intenções" no perfil, somente leitura).
    @Transactional(readOnly = true)
    public List<IntencaoDTO> listarMinhas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));
        return intencaoRepository.findByUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(IntencaoDTO::de)
                .toList();
    }

    public long contarPendentes() {
        return intencaoRepository.countByStatus(StatusIntencao.PENDENTE);
    }

    @Transactional
    public IntencaoDTO definirStatus(Long id, StatusIntencao novo) {
        IntencaoAdocao i = intencaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intenção não encontrada."));
        i.setStatus(novo);
        return IntencaoDTO.de(intencaoRepository.save(i));
    }

    // Revalida o que o front já bloqueia (defensivo). Conditionais só obrigatórias quando exibidas.
    private void validar(CriarIntencaoRequest req) {
        if (!req.termoAceito()) {
            throw badRequest("É necessário aceitar o termo de responsabilidade.");
        }
        List<String> faltando = new ArrayList<>();
        exigir(faltando, "endereço (logradouro)", req.logradouro());
        exigir(faltando, "endereço (número)", req.numero());
        exigir(faltando, "endereço (bairro)", req.bairro());
        exigir(faltando, "endereço (cidade)", req.cidade());
        exigir(faltando, "endereço (estado)", req.estado());
        exigir(faltando, "endereço (CEP)", req.cep());
        exigir(faltando, "tipo de residência", req.tipoResidencia());
        exigir(faltando, "segurança do imóvel", req.imovelSeguro());
        exigir(faltando, "posse do imóvel", req.tipoPosse());
        exigir(faltando, "tempo sozinho", req.tempoSozinho());
        exigir(faltando, "outros animais", req.outrosAnimais());
        exigir(faltando, "concordância da família", req.familiaDeAcordo());
        exigir(faltando, "plano em viagens/mudança", req.planoViagemMudanca());
        exigir(faltando, "plano de comportamento", req.planoComportamento());

        if ("ALUGADO".equals(req.tipoPosse())) {
            exigir(faltando, "autorização do proprietário", req.alugadoAutorizaPets());
        }
        if (req.outrosAnimais() != null && !"NENHUM".equals(req.outrosAnimais())) {
            exigir(faltando, "vacinação/castração dos animais", req.animaisVacinadosCastrados());
        }

        if (!faltando.isEmpty()) {
            throw badRequest("Preencha todos os campos obrigatórios: " + String.join(", ", faltando) + ".");
        }
    }

    private void exigir(List<String> faltando, String rotulo, String valor) {
        if (valor == null || valor.isBlank()) faltando.add(rotulo);
    }

    private ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
