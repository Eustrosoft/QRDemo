package org.eustrosoft.security;

import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.Participant;
import org.eustrosoft.services.ParticipantService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class SecurityComponent {
    private final ParticipantService participantService;

    public void checkUserRight(Supplier<Participant> participantSupplier) throws IllegalAccessException {
        if (participantSupplier == null) {
            throw new IllegalArgumentException("Supplier not provided");
        }
        Participant participant = participantSupplier.get();
        if (participant == null) {
            throw new IllegalAccessException("Participant not found for this qr");
        }
        Participant current = participantService.getCurrentOrThrow();
        if (!Objects.equals(current.getId(), participant.getId())) {
            throw new IllegalAccessException("Participant is not the same as this qr");
        }
    }

}
