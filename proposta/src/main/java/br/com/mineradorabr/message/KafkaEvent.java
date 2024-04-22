package br.com.mineradorabr.message;

import br.com.mineradorabr.dto.ProposalDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvent {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvent.class);

    @Channel("quotation-channel")
    Emitter<ProposalDTO> proposalRequestEmitter;

    public void sendNewKafkaEvent(ProposalDTO proposal) {
        LOG.info("-- Enviando Proposta para Tópico Kafka --");
        proposalRequestEmitter.send(proposal).toCompletableFuture().join();
    }
}
