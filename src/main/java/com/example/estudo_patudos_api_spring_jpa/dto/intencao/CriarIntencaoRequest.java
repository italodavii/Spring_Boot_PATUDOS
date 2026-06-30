package com.example.estudo_patudos_api_spring_jpa.dto.intencao;

// Payload enviado pelo adotante (USER) na tela /quero-adotar/:id.
// O front bloqueia o envio até termo aceito + obrigatórias; o backend revalida (defensivo).
public record CriarIntencaoRequest(
        Long petId,
        boolean termoAceito,
        String mensagem,

        // Endereço para a visita domiciliar (salvo também de volta no perfil)
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep,

        // Entrevista de triagem
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
) {}
