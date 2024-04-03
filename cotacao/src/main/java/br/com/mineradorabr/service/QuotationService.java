package br.com.mineradorabr.service;

import br.com.mineradorabr.client.CurrencyPriceClient;
import br.com.mineradorabr.dto.CurrencyPriceDTO;
import br.com.mineradorabr.dto.QuotationDTO;
import br.com.mineradorabr.entity.QuotationEntity;
import br.com.mineradorabr.message.KafkaEvents;
import br.com.mineradorabr.repository.QuotationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice() {
        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");

        if(updateCurrentInfoPrice(currencyPriceInfo)) {
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                    .date(new Date())
                    .build()
            );
        }
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyInfo) {
        BigDecimal currentPrice = new BigDecimal(currencyInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if(quotationList.isEmpty()) {
            saveQuotation(currencyInfo);
            updatePrice = true;
        } else {
            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size() -1);

            if (currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()) {
                updatePrice = true;
                saveQuotation(currencyInfo);
            }
        }

        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo) {
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());

        quotation.setCurrencyPrice(new BigDecimal(currencyInfo
                .getUSDBRL()
                .getBid()));

        quotation.setPctChange(currencyInfo
                .getUSDBRL()
                .getPctChange());

        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }

}
