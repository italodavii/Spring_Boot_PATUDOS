package com.example.estudo_patudos_api_spring_jpa.dto.intencao;

import com.example.estudo_patudos_api_spring_jpa.model.IntencaoAdocao;

import java.time.LocalDateTime;

// Ficha do Adotante para o admin. Montada 100% a partir dos snapshots da IntencaoAdocao
// (sem tocar nas associações usuario/pet). Os alertas de risco são derivados no front.
public record IntencaoDTO(
        Long id,
        String status,
        LocalDateTime criadoEm,
        boolean termoAceito,
        String mensagem,

        // Adotante (snapshot)
        String adotanteNome,
        String adotanteEmail,
        String adotanteTelefone,

        // Pet (snapshot)
        Long petId,
        String petNome,
        String petUrlFoto,

        // Endereço para a visita
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,

        // Entrevista
        String tipoResidencia,
        String imovelSeguro,
        String tipoPosse,
        String alugadoAutorizaPets,
        String tempoSozinho,
        String outrosAnimais,
        String animaisVacinadosCastrados,
        String familiaDeAcordo,
        String planoViagemMudanca,
        String planoComportamento
) {
    public static IntencaoDTO de(IntencaoAdocao i) {
        return new IntencaoDTO(
                i.getId(),
                i.getStatus().name(),
                i.getCriadoEm(),
                i.isTermoAceito(),
                i.getMensagem(),
                i.getAdotanteNome(),
                i.getAdotanteEmail(),
                i.getAdotanteTelefone(),
                i.getPet().getId(),
                i.getPetNome(),
                i.getPetUrlFoto(),
                i.getLogradouro(),
                i.getNumero(),
                i.getComplemento(),
                i.getBairro(),
                i.getCidade(),
                i.getEstado(),
                i.getCep(),
                i.getTipoResidencia(),
                i.getImovelSeguro(),
                i.getTipoPosse(),
                i.getAlugadoAutorizaPets(),
                i.getTempoSozinho(),
                i.getOutrosAnimais(),
                i.getAnimaisVacinadosCastrados(),
                i.getFamiliaDeAcordo(),
                i.getPlanoViagemMudanca(),
                i.getPlanoComportamento()
        );
    }
}
