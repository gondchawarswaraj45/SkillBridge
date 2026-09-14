package com.freelancing.service.freelancer;

import com.freelancing.dao.freelancer.SkillExchangeDAO;
import com.freelancing.model.freelancer.SkillExchange;
import com.freelancing.service.common.NotificationService;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service orchestrating peer-to-peer Skill Barter / Exchange offers and agreements.
 */
public class SkillExchangeService {
    private static final Logger LOGGER = Logger.getLogger(SkillExchangeService.class.getName());

    private final SkillExchangeDAO exchangeDAO;
    private final NotificationService notifService;

    public SkillExchangeService() {
        this.exchangeDAO = new SkillExchangeDAO();
        this.notifService = new NotificationService();
    }

    public SkillExchangeService(SkillExchangeDAO exchangeDAO, NotificationService notifService) {
        this.exchangeDAO = exchangeDAO;
        this.notifService = notifService;
    }

    public SkillExchange createOffer(String offererId, String offeredSkill, String requestedSkill, String description) {
        if (offererId == null || offererId.trim().isEmpty()) {
            throw new IllegalArgumentException("Offerer ID is required");
        }
        if (offeredSkill == null || offeredSkill.trim().isEmpty()) {
            throw new IllegalArgumentException("Offered skill is required");
        }
        if (requestedSkill == null || requestedSkill.trim().isEmpty()) {
            throw new IllegalArgumentException("Requested skill is required");
        }

        SkillExchange exchange = new SkillExchange();
        exchange.setOffererId(offererId.trim());
        exchange.setOfferedSkill(offeredSkill.trim());
        exchange.setRequestedSkill(requestedSkill.trim());
        exchange.setDescription(description != null ? description.trim() : "");
        exchange.setStatus(SkillExchange.Status.OPEN);

        SkillExchange created = exchangeDAO.create(exchange);
        LOGGER.info("Skill exchange offer created: " + created.getId() + " by offerer: " + offererId);
        return created;
    }

    public SkillExchange createOffer(String offererId, String offeredSkill, String requestedSkill, String description, int hoursPerWeek) {
        SkillExchange created = createOffer(offererId, offeredSkill, requestedSkill, description);
        if (created != null) {
            created.setHoursPerWeek(hoursPerWeek > 0 ? hoursPerWeek : 3);
        }
        return created;
    }

    public List<SkillExchange> getOffers(String statusFilter, String skillKeyword) {
        return exchangeDAO.findAll(statusFilter, skillKeyword);
    }

    public List<SkillExchange> getAllOffers(String statusFilter, String skillKeyword) {
        return getOffers(statusFilter, skillKeyword);
    }

    public SkillExchange getOfferById(String id) {
        return exchangeDAO.findById(id);
    }

    public List<SkillExchange> getUserOffers(String userId) {
        return exchangeDAO.findByOffererId(userId);
    }

    public List<SkillExchange> getUserParticipations(String userId) {
        return exchangeDAO.findByParticipantId(userId);
    }

    public boolean requestExchange(String offerId, String requesterId, String note) {
        if (offerId == null || requesterId == null) {
            return false;
        }

        SkillExchange offer = exchangeDAO.findById(offerId);
        if (offer == null) {
            throw new IllegalArgumentException("Skill exchange offer not found: " + offerId);
        }
        if (offer.getOffererId().equals(requesterId)) {
            throw new IllegalArgumentException("You cannot request an exchange on your own barter offer.");
        }
        if (offer.getStatus() != SkillExchange.Status.OPEN) {
            throw new IllegalStateException("Offer is not open for new requests (current status: " + offer.getStatus() + ").");
        }

        boolean ok = exchangeDAO.requestExchange(offerId, requesterId, note);
        if (ok) {
            notifService.sendNotification(
                    offer.getOffererId(),
                    "🤝 New Skill Barter Request",
                    "A developer requested a barter session for '" + offer.getOfferedSkill() + "'! Check your exchanges.",
                    "SKILL_EXCHANGE",
                    offerId
            );
            LOGGER.info("Skill exchange requested for offer: " + offerId + " by user: " + requesterId);
        }
        return ok;
    }

    public boolean acceptExchange(String offerId, String offererId) {
        SkillExchange offer = exchangeDAO.findById(offerId);
        if (offer == null) return false;

        boolean ok = exchangeDAO.acceptExchange(offerId, offererId);
        if (ok && offer.getRequesterId() != null) {
            notifService.sendNotification(
                    offer.getRequesterId(),
                    "🎉 Skill Barter Accepted!",
                    (offer.getOffererName() != null ? offer.getOffererName() : "Your partner")
                            + " accepted your barter request for '" + offer.getOfferedSkill() + "'!",
                    "SKILL_EXCHANGE",
                    offerId
            );
            LOGGER.info("Skill exchange accepted for offer: " + offerId);
        }
        return ok;
    }

    public boolean rejectExchange(String offerId, String offererId) {
        SkillExchange offer = exchangeDAO.findById(offerId);
        if (offer == null) return false;

        String prevRequester = offer.getRequesterId();
        boolean ok = exchangeDAO.rejectExchange(offerId, offererId);
        if (ok && prevRequester != null) {
            notifService.sendNotification(
                    prevRequester,
                    "Skill Barter Request Update",
                    "Your request for '" + offer.getOfferedSkill() + "' was not accepted at this time.",
                    "SKILL_EXCHANGE",
                    offerId
            );
            LOGGER.info("Skill exchange rejected for offer: " + offerId);
        }
        return ok;
    }

    public boolean completeExchange(String offerId, String userId) {
        SkillExchange offer = exchangeDAO.findById(offerId);
        if (offer == null) return false;

        boolean ok = exchangeDAO.completeExchange(offerId, userId);
        if (ok) {
            String partnerId = userId.equals(offer.getOffererId()) ? offer.getRequesterId() : offer.getOffererId();
            if (partnerId != null) {
                notifService.sendNotification(
                        partnerId,
                        "🏆 Skill Barter Completed!",
                        "The skill barter session for '" + offer.getOfferedSkill() + "' was marked completed.",
                        "SKILL_EXCHANGE",
                        offerId
                );
            }
            notifService.sendNotification(
                    userId,
                    "🏆 Skill Barter Completed!",
                    "Skill barter session marked as successfully completed!",
                    "SKILL_EXCHANGE",
                    offerId
            );
            LOGGER.info("Skill exchange completed for offer: " + offerId);
        }
        return ok;
    }

    public void deleteOffer(String offerId) {
        exchangeDAO.delete(offerId);
    }
}
