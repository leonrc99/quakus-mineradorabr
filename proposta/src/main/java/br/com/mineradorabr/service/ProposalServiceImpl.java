package br.com.mineradorabr.service;

import br.com.mineradorabr.dto.ProposalDTO;
import br.com.mineradorabr.dto.ProposalDetailsDTO;
import br.com.mineradorabr.entity.ProposalEntity;
import br.com.mineradorabr.message.KafkaEvent;
import br.com.mineradorabr.repository.ProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ProposalServiceImpl implements ProposalService {

    @Inject
    ProposalRepository proposalRepository;

    @Inject
    KafkaEvent kafkaMessages;

    @Override
    public ProposalDetailsDTO findFullProposal(Long id) {
        ProposalEntity proposal = proposalRepository.findById(id);

        return ProposalDetailsDTO.builder()
                .proposalId(proposal.getId())
                .proposalValidityDays(proposal.getProposalValidityDays())
                .country(proposal.getCountry())
                .priceTonne(proposal.getPriceTonne())
                .customer(proposal.getCustomer())
                .tonnes(proposal.getTonnes())
                .created(proposal.getCreated())
                .build();
    }

    @Override
    public List<ProposalDetailsDTO> findAllProposals() {
        List<ProposalEntity> proposals = proposalRepository.findAll().list();

        List<ProposalDetailsDTO> proposalDetailsDTOList = new ArrayList<>();
        for (ProposalEntity proposal : proposals) {
            ProposalDetailsDTO proposalDetailsDTO = ProposalDetailsDTO.builder()
                    .proposalId(proposal.getId())
                    .proposalValidityDays(proposal.getProposalValidityDays())
                    .country(proposal.getCountry())
                    .priceTonne(proposal.getPriceTonne())
                    .customer(proposal.getCustomer())
                    .tonnes(proposal.getTonnes())
                    .created(proposal.getCreated())
                    .build();
            proposalDetailsDTOList.add(proposalDetailsDTO);
        }

        return proposalDetailsDTOList;
    }

    @Override
    @Transactional
    public void createNewProposal(ProposalDetailsDTO proposalDetailsDTO) {
        ProposalDTO proposal = buildAndSaveNewProposal(proposalDetailsDTO);
        kafkaMessages.sendNewKafkaEvent(proposal);
    }

    @Override
    @Transactional
    public void removeProposal(long id) {
        proposalRepository.deleteById(id);
    }

    @Transactional
    public ProposalDTO buildAndSaveNewProposal(ProposalDetailsDTO proposalDetailsDTO) {
        try {
            ProposalEntity proposal = new ProposalEntity();

            proposal.setCreated(new Date());
            proposal.setProposalValidityDays(proposalDetailsDTO.getProposalValidityDays());
            proposal.setCountry(proposalDetailsDTO.getCountry());
            proposal.setCustomer(proposalDetailsDTO.getCustomer());
            proposal.setPriceTonne(proposalDetailsDTO.getPriceTonne());
            proposal.setTonnes(proposalDetailsDTO.getTonnes());

            proposalRepository.persist(proposal);

            return ProposalDTO.builder()
                    .proposalId(proposal.getId())
                    .priceTonne(proposal.getPriceTonne())
                    .customer(proposal.getCustomer())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
