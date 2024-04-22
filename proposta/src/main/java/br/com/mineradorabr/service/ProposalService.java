package br.com.mineradorabr.service;

import br.com.mineradorabr.dto.ProposalDetailsDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public interface ProposalService {

    ProposalDetailsDTO findFullProposal(Long id);

    List<ProposalDetailsDTO> findAllProposals();

    void createNewProposal(ProposalDetailsDTO proposalDetailsDTO);

    void removeProposal(long id);
}
